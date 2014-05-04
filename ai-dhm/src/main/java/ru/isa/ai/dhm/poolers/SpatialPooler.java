package ru.isa.ai.dhm.poolers;

import cern.colt.function.tint.IntProcedure;
import cern.colt.matrix.tbit.BitMatrix;
import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.tint.IntMatrix1D;
import cern.colt.matrix.tint.impl.DenseIntMatrix1D;
import cern.colt.matrix.tint.impl.SparseIntMatrix2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isa.ai.dhm.MathUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * The Spatial Pooler is responsible for creating a sparse distributed
 * representation of the input. Given an input it computes a set of sparse
 * active columns and simultaneously updates its permanences, duty cycles,
 * etc.
 * <p>
 * The primary public interfaces to this function are the "initialize"
 * and "compute" methods.
 * <p>
 * Example usage:
 * <p>
 * SpatialPooler sp; <p/>
 * sp.initialize(inputDimensions, columnDimensions, <parameters>);<p/>
 * while (true) {<p/>
 * <get input vector><p/>
 * sp.compute(inputVector, learn, activeColumns)<p/>
 * <do something with output><p/>
 * }
 */
public class SpatialPooler implements ISpatialPooler {

    private final Logger logger = LogManager.getLogger(SpatialPooler.class.getSimpleName());
    private final String SP_PROP_FILENAME = "dhm_sp.properties";
    private final int PRECISION = 5;

    /**
     * This parameter deteremines the extent of the
     * input that each column can potentially be connected to. This
     * can be thought of as the input bits that are visible to each
     * column, or a 'receptive field' of the field of vision. A large
     * enough value will result in global coverage, meaning
     * that each column can potentially be connected to every input
     * bit. This parameter defines a square (or hyper square) area: a
     * column will have a max square potential pool with sides of
     * length (2 * potentialRadius + 1).
     */
    private int potentialRadius = 16;
    /**
     * The percent of the inputs, within a column's
     * potential radius, that a column can be connected to. If set to
     * 1, the column will be connected to every input within its
     * potential radius. This parameter is used to give each column a
     * unique potential pool when a large potentialRadius causes
     * overlap between the columns. At initialization time we choose
     * ((2*potentialRadius + 1)^(# inputDimensions) * potentialPct)
     * input bits to comprise the column's potential pool.
     */
    private double potentialPct = 0.5;
    /**
     * If true, then during inhibition phase the
     * winning columns are selected as the most active columns from the
     * region as a whole. Otherwise, the winning columns are selected
     * with resepct to their local neighborhoods. Global inhibition
     * boosts performance significantly but there is no topology at the
     * output.
     */
    private boolean globalInhibition = true;
    /**
     * The desired density of active columns within
     * a local inhibition area (the size of which is set by the
     * internally calculated inhibitionRadius, which is in turn
     * determined from the average size of the connected potential
     * pools of all columns). The inhibition logic will insure that at
     * most N columns remain ON within a local inhibition area, where
     * N = localAreaDensity * (total number of columns in inhibition
     * area). If localAreaDensity is set to a negative value output
     * sparsity will be determined by the numActivePerInhArea.
     */
    private double localAreaDensity = -1.0;
    /**
     * An alternate way to control the sparsity of
     * active columns. If numActivePerInhArea is specified then
     * localAreaDensity must less than 0, and vice versa. When
     * numActivePerInhArea > 0, the inhibition logic will insure that
     * at most 'numActivePerInhArea' columns remain ON within a local
     * inhibition area (the size of which is set by the internally
     * calculated inhibitionRadius). When using this method, as columns
     * learn and grow their effective receptive fields, the
     * inhibitionRadius will grow, and hence the net density of the
     * active columns will *decrease*. This is in contrast to the
     * localAreaDensity method, which keeps the density of active
     * columns the same regardless of the size of their receptive
     * fields.
     */
    private long numActiveColumnsPerInhArea = 10;
    /**
     * This is a number specifying the minimum
     * number of synapses that must be active in order for a column to
     * turn ON. The purpose of this is to prevent noisy input from
     * activating columns.
     */
    private long stimulusThreshold = 0;
    /**
     * The amount by which the permanence of an
     * inactive synapse is decremented in each learning step.
     */
    private double synPermInactiveDec = 0.01;
    /**
     * The amount by which the permanence of an
     * active synapse is incremented in each round.
     */
    private double synPermActiveInc = 0.1;
    /**
     * The default connected threshold. Any synapse
     * whose permanence value is above the connected threshold is
     * a "connected synapse", meaning it can contribute to
     * the cell's firing.
     */
    private double synPermConnected = 0.1;
    /**
     * A number between 0 and 1.0, used to set
     * a floor on how often a column should have at least
     * stimulusThreshold active inputs. Periodically, each column looks
     * at the overlap duty cycle of all other column within its
     * inhibition radius and sets its own internal minimal acceptable
     * duty cycle to: minPctDutyCycleBeforeInh * max(other columns'
     * duty cycles). On each iteration, any column whose overlap duty
     * cycle falls below this computed value will get all of its
     * permanence values boosted up by synPermActiveInc. Raising all
     * permanences in response to a sub-par duty cycle before
     * inhibition allows a cell to search for new inputs when either
     * its previously learned inputs are no longer ever active, or when
     * the vast majority of them have been "hijacked" by other columns.
     */
    private double minPctOverlapDutyCycles = 0.001;
    /**
     * A number between 0 and 1.0, used to set
     * a floor on how often a column should be activate. Periodically,
     * each column looks at the activity duty cycle of all other
     * columns within its inhibition radius and sets its own internal
     * minimal acceptable duty cycle to:
     * <p>
     * minPctDutyCycleAfterInh * max(other columns' duty cycles).
     * <p>
     * On each iteration, any column whose duty cycle after inhibition
     * falls below this computed value will get its internal boost
     * factor increased.
     */
    private double minPctActiveDutyCycles = 0.001;
    /**
     * The period used to calculate duty cycles.
     * Higher values make it take longer to respond to changes in
     * boost. Shorter values make it potentially more unstable and
     * likely to oscillate.
     */
    private long dutyCyclePeriod = 1000;
    /**
     * The maximum overlap boost factor. Each column's
     * overlap gets multiplied by a boost factor before it gets
     * considered for inhibition. The actual boost factor for a column
     * is a number between 1.0 and maxBoost. A boost factor of 1.0 is
     * used if the duty cycle is >= minOverlapDutyCycle, maxBoost is
     * used if the duty cycle is 0, and any duty cycle in between is
     * linearly extrapolated from these 2 endpoints.
     */
    private double maxBoost = 10.0;

    private int[] inputDimensions;
    private int numInputs = 1;
    private int[] columnDimensions;
    private int numColumns = 1;

    private int inhibitionRadius = 0;
    private double synPermBelowStimulusInc;
    private double synPermTrimThreshold;
    private double synPermMin = 0.0;
    private double synPermMax = 1.0;

    private long updatePeriod = 50;
    private double initConnectedPct = 0.5;
    private int iterationNum = 0;
    private int iterationLearnNum = 0;

    private SparseDoubleMatrix2D permanences;
    private BitMatrix potentialPools;
    private BitMatrix connectedSynapses;
    private List<Integer> connectedCounts;

    private List<Integer> overlaps;
    private List<Double> overlapsPct;
    private List<Double> boostedOverlaps;
    private List<Integer> activeColumns;
    private List<Double> tieBreaker;

    private List<Double> boostFactors;
    private List<Double> overlapDutyCycles;
    private List<Double> activeDutyCycles;
    private List<Double> minOverlapDutyCycles;
    private List<Double> minActiveDutyCycles;

    private Random random = new Random();

    @Override
    public void initialize(int[] inputDimensions, int[] columnDimensions) throws SpatialPoolerInitializationException {
        this.inputDimensions = new int[inputDimensions.length];
        for (int i = 0; i < inputDimensions.length; i++) {
            numInputs *= inputDimensions[i];
            this.inputDimensions[i] = inputDimensions[i];
        }
        this.columnDimensions = new int[columnDimensions.length];
        for (int i = 0; i < columnDimensions.length; i++) {
            numColumns *= columnDimensions[i];
            this.columnDimensions[i] = columnDimensions[i];
        }

        loadProperties();
        checkProperties();

        tieBreaker = new ArrayList<>(numColumns);
        for (int i = 0; i < numColumns; i++)
            tieBreaker.set(i, 0.01 * random.nextDouble());
        potentialPools = new BitMatrix(numColumns, numInputs);
        permanences = new SparseDoubleMatrix2D(numColumns, numInputs);
        connectedSynapses = new BitMatrix(numColumns, numInputs);
        connectedCounts = new ArrayList<>(numColumns);

        overlapDutyCycles = new ArrayList<>(numColumns);
        Collections.fill(overlapDutyCycles, 0.0);
        activeDutyCycles = new ArrayList<>(numColumns);
        Collections.fill(activeDutyCycles, 0.0);
        minOverlapDutyCycles = new ArrayList<>(numColumns);
        Collections.fill(minOverlapDutyCycles, 0.0);
        minActiveDutyCycles = new ArrayList<>(numColumns);
        Collections.fill(minActiveDutyCycles, 0.0);
        boostFactors = new ArrayList<>(numColumns);
        Collections.fill(boostFactors, 1.0);
        overlaps = new ArrayList<>(numColumns);
        overlapsPct = new ArrayList<>(numColumns);
        boostedOverlaps = new ArrayList<>(numColumns);

        for (int i = 0; i < numColumns; i++) {
            BitVector potential = mapPotential1D(1, true);
            MathUtils.setRow(potentialPools, potential, i);
            DoubleMatrix1D perm = initPermanence(potential, initConnectedPct);
            updatePermanencesForColumn(perm, i, true);
        }

        updateInhibitionRadius();
    }

    private BitVector mapPotential1D(int column, boolean wrapAround) {
        double ratio = column / Math.max(numColumns - 1, 1.0);
        column = (int) ((numInputs - 1) * ratio);

        BitVector potential = new BitVector(numInputs);
        potential.clear();
        List<Integer> indices = new ArrayList<>();
        for (int i = -potentialRadius + column; i <= potentialRadius + column; i++) {
            if (wrapAround) {
                indices.add((i + numInputs) % numInputs);
            } else if (i >= 0 && i < numInputs) {
                indices.add(i);
            }
        }

        Set<Integer> unique = new TreeSet<>(indices);
        indices = Arrays.asList((Integer[]) unique.toArray());
        Collections.shuffle(indices, random);

        long numPotential = Math.round(indices.size() * potentialPct);
        for (int i = 0; i < numPotential; i++) {
            potential.set(indices.get(i));
        }

        return potential;
    }

    private DoubleMatrix1D initPermanence(BitVector potential, double connectedPct) {
        DoubleMatrix1D perm = new DenseDoubleMatrix1D(numInputs);
        perm.assign(0);
        for (int i = 0; i < numInputs; i++) {
            if (!potential.get(i))
                continue;

            if (random.nextDouble() <= connectedPct) {
                perm.set(i, MathUtils.roundWithPrecision(synPermConnected + random.nextDouble() * synPermActiveInc / 4.0, PRECISION));
            } else {
                perm.set(i, MathUtils.roundWithPrecision(synPermConnected * random.nextDouble(), PRECISION));
            }
            perm.set(i, perm.get(i) < synPermTrimThreshold ? 0 : perm.get(i));
        }

        return perm;
    }

    private void updatePermanencesForColumn(DoubleMatrix1D perm, int column, boolean raisePerm) {
        BitVector connectedSparse = new BitVector(numColumns);
        int numConnected;
        if (raisePerm) {
            BitVector potential = MathUtils.getRow(potentialPools, column);
            raisePermanencesToThreshold(perm, potential);
        }

        numConnected = 0;
        for (int i = 0; i < perm.size(); ++i) {
            if (perm.getQuick(i) >= synPermConnected) {
                connectedSparse.set(i);
                numConnected++;
            }
        }

        clip(perm, true);
        MathUtils.setRow(connectedSynapses, connectedSparse, column);
        //MathUtils.setRow(permanences, perm, column);
        connectedCounts.set(column, numConnected);
    }

    private void clip(DoubleMatrix1D perm, boolean trim) {
        double minVal = trim ? synPermTrimThreshold : synPermMin;
        for (int i = 0; i < perm.size(); i++) {
            double value = perm.getQuick(i);
            perm.set(i, value > synPermMax ? synPermMax : value);
            value = perm.getQuick(i);
            perm.set(i, value < minVal ? synPermMin : value);
        }
    }

    private int countConnected(DoubleMatrix1D perm) {
        int numConnected = 0;
        for (int i = 0; i < perm.size(); i++) {
            if (perm.getQuick(i) > synPermConnected) {
                numConnected++;
            }
        }
        return numConnected;
    }

    private int raisePermanencesToThreshold(DoubleMatrix1D perm, BitVector potential) {
        clip(perm, false);
        int numConnected;
        while (true) {
            numConnected = countConnected(perm);
            if (numConnected >= stimulusThreshold)
                break;

            for (int i = 0; i < potential.size(); i++) {
                if (potential.getQuick(i))
                    perm.set(i, perm.getQuick(i) + synPermBelowStimulusInc);
            }
        }
        return numConnected;
    }

    private void updateInhibitionRadius() {
        if (globalInhibition) {
            inhibitionRadius = MathUtils.max(columnDimensions);
            return;
        }

        double connectedSpan = 0;
        for (int i = 0; i < numColumns; i++) {
            connectedSpan += avgConnectedSpanForColumnND(i);
        }
        connectedSpan /= numColumns;
        double columnsPerInput = avgColumnsPerInput();
        double diameter = connectedSpan * columnsPerInput;
        double radius = (diameter - 1) / 2.0;
        radius = Math.max(1.0, radius);
        inhibitionRadius = (int) Math.round(radius);
    }

    private double avgConnectedSpanForColumnND(int column) {
        BitVector connectedSparse = MathUtils.getRow(connectedSynapses, column);
        IntMatrix1D maxCoord = new DenseIntMatrix1D(inputDimensions.length);
        maxCoord.assign(0);
        IntMatrix1D minCoord = new DenseIntMatrix1D(inputDimensions.length);
        minCoord.assign(MathUtils.max(inputDimensions));

        CoordinateConverterND conv = new CoordinateConverterND(inputDimensions);

        if (connectedSparse.size() == 0)
            return 0;

        List<Integer> columnCoord = new ArrayList<>();
        for (int i = 0; i < connectedSparse.size(); i++) {
            if(connectedSparse.get(i))
                conv.toCoord(i, columnCoord);
            for (int j = 0; j < columnCoord.size(); j++) {
                maxCoord.set(j, Math.max(maxCoord.get(j), columnCoord.get(j)));
                minCoord.set(j, Math.min(minCoord.get(j), columnCoord.get(j)));
            }
        }

        double totalSpan = 0;
        for (int j = 0; j < inputDimensions.length; j++) {
            totalSpan += maxCoord.get(j) - minCoord.get(j) + 1;
        }

        return totalSpan / inputDimensions.length;

    }

    private double avgColumnsPerInput() {
        int numDim = Math.max(columnDimensions.length, inputDimensions.length);
        double columnsPerInput = 0;
        for (int i = 0; i < numDim; i++) {
            double col = (i < columnDimensions.length) ? columnDimensions[i] : 1;
            double input = (i < inputDimensions.length) ? inputDimensions[i] : 1;
            columnsPerInput += col / input;
        }
        return columnsPerInput / numDim;
    }

    private void checkProperties() throws SpatialPoolerInitializationException {
        if (numColumns <= 0)
            throw new SpatialPoolerInitializationException("Column dimensions must be non zero positive values");
        if (numInputs <= 0)
            throw new SpatialPoolerInitializationException("Input dimensions must be non zero positive values");
        if (numActiveColumnsPerInhArea <= 0 && (localAreaDensity <= 0 || localAreaDensity > 0.5))
            throw new SpatialPoolerInitializationException("Or numActiveColumnsPerInhArea > 0 or localAreaDensity > 0 " +
                    "and localAreaDensity <= 0.5");
        if (potentialPct <= 0 || potentialPct > 1)
            throw new SpatialPoolerInitializationException("potentialPct must be > 0 and <= 1");
    }

    private void loadProperties() throws SpatialPoolerInitializationException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(SP_PROP_FILENAME));
            for (String name : properties.stringPropertyNames()) {
                switch (name) {
                    case "potentialRadius":
                        potentialRadius = Integer.parseInt(properties.getProperty(name));
                        potentialRadius = potentialRadius > numInputs ? numInputs : potentialRadius;
                        break;
                    case "potentialPct":
                        potentialPct = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "globalInhibition":
                        globalInhibition = Boolean.parseBoolean(properties.getProperty(name));
                        break;
                    case "localAreaDensity":
                        localAreaDensity = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "numActiveColumnsPerInhArea":
                        numActiveColumnsPerInhArea = Long.parseLong(properties.getProperty(name));
                        break;
                    case "stimulusThreshold":
                        stimulusThreshold = Long.parseLong(properties.getProperty(name));
                        break;
                    case "synPermInactiveDec":
                        synPermInactiveDec = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "synPermActiveInc":
                        synPermActiveInc = Double.parseDouble(properties.getProperty(name));
                        synPermTrimThreshold = synPermActiveInc / 2.0;
                        break;
                    case "synPermConnected":
                        synPermConnected = Double.parseDouble(properties.getProperty(name));
                        synPermBelowStimulusInc = synPermConnected / 10.0;
                        break;
                    case "minPctOverlapDutyCycles":
                        minPctOverlapDutyCycles = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "minPctActiveDutyCycles":
                        minPctActiveDutyCycles = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "dutyCyclePeriod":
                        dutyCyclePeriod = Long.parseLong(properties.getProperty(name));
                        break;
                    case "maxBoost":
                        maxBoost = Double.parseDouble(properties.getProperty(name));
                        break;
                    default:
                        logger.error("Illegal property name: " + name);
                        break;
                }
            }
        } catch (IOException e) {
            throw new SpatialPoolerInitializationException("Cannot load properties file " + SP_PROP_FILENAME, e);
        } catch (NumberFormatException nfe) {
            throw new SpatialPoolerInitializationException("Wrong property value in property file " + SP_PROP_FILENAME, nfe);
        }
    }

    @Override
    public void compute(BitVector inputVector, boolean learn, BitVector activeVector) {
        iterationNum++;
        if (learn) {
            iterationLearnNum++;
        }
//        calculateOverlap(inputVector, overlaps);
//        calculateOverlapPct(overlaps, overlapsPct);
//
//        if (learn) {
//            boostOverlaps(overlaps, boostedOverlaps);
//        } else {
//            for (int i = 0; i < overlaps.size(); i++)
//                boostedOverlaps.set(i, overlaps.get(i) ? 1.0 : 0.0);
//        }
//
//        inhibitColumns(boostedOverlaps, activeColumns);
//        toDense(activeColumns, activeVector, numColumns);
//
//        if (learn) {
//            adaptSynapses(inputVector, activeColumns);
//            updateDutyCycles(overlaps, activeVector);
//            bumpUpWeakColumns();
//            updateBoostFactors();
//            if (isUpdateRound()) {
//                updateInhibitionRadius();
//                updateMinDutyCycles();
//            }
//        } else {
//            stripNeverLearned(activeVector);
//        }
    }

    private void calculateOverlap(BitSet inputVector, List<Integer> overlaps) {
        overlaps.clear();
//        overlaps = connectedSynapses.rightVecSumAtNZ(inputVector, numInputs);
//        if (stimulusThreshold > 0) {
//            for (int i = 0; i < numColumns; i++) {
//                if (overlaps.get(i) < stimulusThreshold) {
//                    overlaps.set(i, 0);
//                }
//            }
//        }
    }
}
