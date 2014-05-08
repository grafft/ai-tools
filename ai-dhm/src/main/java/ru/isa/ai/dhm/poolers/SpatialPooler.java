package ru.isa.ai.dhm.poolers;

import cern.colt.function.tdouble.DoubleFunction;
import cern.colt.function.tint.IntIntProcedure;
import cern.colt.function.tint.IntProcedure;
import cern.colt.matrix.tbit.BitMatrix;
import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.tint.IntMatrix1D;
import cern.colt.matrix.tint.impl.DenseIntMatrix1D;
import cern.jet.math.tdouble.DoublePlusMultSecond;
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
 * <p/>
 * The primary public interfaces to this function are the "initialize"
 * and "compute" methods.
 * <p/>
 * Example usage:
 * <p/>
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

    private String filePropName = SP_PROP_FILENAME;

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
     * <p/>
     * minPctDutyCycleAfterInh * max(other columns' duty cycles).
     * <p/>
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

    private IntMatrix1D overlaps;
    private DoubleMatrix1D overlapsPct;
    private DoubleMatrix1D boostedOverlaps;
    private List<Integer> activeColumns;
    private DoubleMatrix1D tieBreaker;

    private DoubleMatrix1D boostFactors;
    private DoubleMatrix1D overlapDutyCycles;
    private DoubleMatrix1D activeDutyCycles;
    private DoubleMatrix1D minOverlapDutyCycles;
    private DoubleMatrix1D minActiveDutyCycles;

    private Random random = new Random();

    public SpatialPooler() {
    }

    public SpatialPooler(String filePropName) {
        this.filePropName = filePropName;
    }

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

        activeColumns = new ArrayList<>();
        tieBreaker = new DenseDoubleMatrix1D(numColumns);
        tieBreaker.assign(new DoubleFunction() {
            @Override
            public double apply(double argument) {
                return 0.01 * random.nextDouble();
            }
        });
        potentialPools = new BitMatrix(numInputs, numColumns);
        permanences = new SparseDoubleMatrix2D(numColumns, numInputs);
        connectedSynapses = new BitMatrix(numInputs, numColumns);
        connectedCounts = new ArrayList<>(numColumns);

        overlapDutyCycles = new DenseDoubleMatrix1D(numColumns);
        activeDutyCycles = new DenseDoubleMatrix1D(numColumns);
        minOverlapDutyCycles = new DenseDoubleMatrix1D(numColumns);
        minActiveDutyCycles = new DenseDoubleMatrix1D(numColumns);
        boostFactors = new DenseDoubleMatrix1D(numColumns);
        boostFactors.assign(1.0);
        overlaps = new DenseIntMatrix1D(numColumns);
        overlapsPct = new DenseDoubleMatrix1D(numColumns);
        boostedOverlaps = new DenseDoubleMatrix1D(numColumns);

        for (int i = 0; i < numColumns; i++) {
            BitVector potential = mapPotential1D(i, true);
            MathUtils.setRow(potentialPools, potential, i);
            DoubleMatrix1D perm = initPermanence(potential, initConnectedPct);
            updatePermanencesForColumn(perm, i, true);
        }

        updateInhibitionRadius();
    }

    /**
     * Возращает вектор индексов входных эелементов связанных с колонкой с индексом column
     *
     * @param columnIndex - index of column
     * @param wrapAround
     * @return
     */
    private BitVector mapPotential1D(int columnIndex, boolean wrapAround) {
        double ratio = columnIndex / Math.max(numColumns - 1, 1.0);
        int inputIndex = (int) ((numInputs - 1) * ratio);

        BitVector potential = new BitVector(numInputs);
        List<Integer> indices = new ArrayList<>();
        for (int i = inputIndex - potentialRadius; i <= inputIndex + potentialRadius; i++) {
            if (wrapAround)
                indices.add((numInputs + i) % numInputs);
            else if (i >= 0 && i < numInputs)
                indices.add(i);
        }

        Set<Integer> unique = new TreeSet<>(indices);
        indices = Arrays.asList(unique.toArray(new Integer[unique.size()]));
        Collections.shuffle(indices, random);

        long numPotential = Math.round(indices.size() * potentialPct);
        for (int i = 0; i < numPotential; i++) {
            potential.set(indices.get(i));
        }

        return potential;
    }

    /**
     * Инициализирует вектор состояния синапсов
     *
     * @param potential    - connected inputs, size - numInput
     * @param connectedPct
     * @return
     */
    private DoubleMatrix1D initPermanence(BitVector potential, final double connectedPct) {
        final DoubleMatrix1D perm = new DenseDoubleMatrix1D(numInputs);
        potential.forEachIndexFromToInState(0, potential.size() - 1, true, new IntProcedure() {

            @Override
            public boolean apply(int index) {
                double value;
                if (random.nextDouble() <= connectedPct)
                    value = MathUtils.roundWithPrecision(synPermConnected + random.nextDouble() * synPermActiveInc / 4.0, PRECISION);
                else
                    value = MathUtils.roundWithPrecision(synPermConnected * random.nextDouble(), PRECISION);

                value = value < synPermTrimThreshold ? 0 : value;
                perm.set(index, value);
                return true;
            }
        });
        return perm;
    }

    /**
     * Обновляет связи колонки
     *
     * @param perm    - размер numInput
     * @param columnIndex
     * @param raisePerm
     */
    private void updatePermanencesForColumn(DoubleMatrix1D perm, int columnIndex, boolean raisePerm) {
        final BitVector connectedSparse = new BitVector(numInputs);
        if (raisePerm) {
            BitVector potential = MathUtils.getRow(potentialPools, columnIndex);
            raisePermanencesToThreshold(perm, potential);
        }

        int numConnected = countConnected(perm);
        for (int i = 0; i < numInputs; ++i) {
            if (perm.getQuick(i) >= synPermConnected)
                connectedSparse.set(i);
        }

        clip(perm, true);
        MathUtils.setRow(connectedSynapses, connectedSparse, columnIndex);
        MathUtils.setRow(permanences, perm, columnIndex);
        connectedCounts.add(columnIndex, numConnected);
    }

    private void clip(DoubleMatrix1D perm, boolean trim) {
        final double minVal = trim ? synPermTrimThreshold : synPermMin;
        perm.assign(new DoubleFunction() {
            @Override
            public double apply(double argument) {
                double value = argument > synPermMax ? synPermMax : argument;
                return value < minVal ? synPermMin : value;
            }
        });
    }

    private int countConnected(DoubleMatrix1D perm) {
        return (int) perm.aggregate(DoublePlusMultSecond.plusMult(1), new DoubleFunction() {

            @Override
            public double apply(double argument) {
                return argument > synPermConnected ? 1 : 0;
            }
        });
    }

    private int raisePermanencesToThreshold(final DoubleMatrix1D perm, BitVector potential) {
        clip(perm, false);
        int numConnected;
        while (true) {
            numConnected = countConnected(perm);
            if (numConnected >= stimulusThreshold)
                break;

            potential.forEachIndexFromToInState(0, potential.size() - 1, true, new IntProcedure() {
                @Override
                public boolean apply(int index) {
                    perm.setQuick(index, perm.getQuick(index) + synPermBelowStimulusInc);
                    return true;
                }
            });
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
        final IntMatrix1D maxCoord = new DenseIntMatrix1D(inputDimensions.length);
        final IntMatrix1D minCoord = new DenseIntMatrix1D(inputDimensions.length);
        minCoord.assign(MathUtils.max(inputDimensions));

        final CoordinateConverterND conv = new CoordinateConverterND(inputDimensions);

        if (connectedSparse.size() == 0)
            return 0;

        final List<Integer> columnCoord = new ArrayList<>();
        connectedSparse.forEachIndexFromToInState(0, connectedSparse.size() - 1, true, new IntProcedure() {
            @Override
            public boolean apply(int index) {
                conv.toCoord(index, columnCoord);
                for (int j = 0; j < columnCoord.size(); j++) {
                    int coord = columnCoord.get(j);
                    maxCoord.set(j, Math.max(maxCoord.get(j), coord));
                    minCoord.set(j, Math.min(minCoord.get(j), coord));
                }
                return true;
            }
        });

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
        potentialRadius = potentialRadius > numInputs ? numInputs : potentialRadius;
    }

    private void loadProperties() throws SpatialPoolerInitializationException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filePropName));
            boolean synPermBelowStimulusIncInited = false;
            boolean synPermTrimThresholdInited = false;
            for (String name : properties.stringPropertyNames()) {
                switch (name) {
                    case "potentialRadius":
                        potentialRadius = Integer.parseInt(properties.getProperty(name));
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
                        break;
                    case "synPermTrimThreshold":
                        synPermTrimThreshold = Double.parseDouble(properties.getProperty(name));
                        synPermTrimThresholdInited = true;
                        break;
                    case "synPermConnected":
                        synPermConnected = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "synPermBelowStimulusInc":
                        synPermBelowStimulusInc = Double.parseDouble(properties.getProperty(name));
                        synPermBelowStimulusIncInited = true;
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
            if (!synPermBelowStimulusIncInited)
                synPermBelowStimulusInc = synPermConnected / 10.0;
            if (!synPermTrimThresholdInited)
                synPermTrimThreshold = synPermActiveInc / 2.0;
        } catch (IOException e) {
            throw new SpatialPoolerInitializationException("Cannot load properties file " + filePropName, e);
        } catch (NumberFormatException nfe) {
            throw new SpatialPoolerInitializationException("Wrong property value in property file " + filePropName, nfe);
        }
    }

    @Override
    public void compute(BitVector inputVector, boolean learn, BitVector activeVector) {
        iterationNum++;
        if (learn) {
            iterationLearnNum++;
        }
        calculateOverlap(inputVector, overlaps);
        calculateOverlapPct(overlaps, overlapsPct);

        if (learn) {
            for (int i = 0; i < numColumns; i++)
                boostedOverlaps.setQuick(i, overlaps.getQuick(i) * boostFactors.getQuick(i));
        } else {
            for (int i = 0; i < numColumns; i++)
                boostedOverlaps.set(i, overlaps.getQuick(i));
        }

        inhibitColumns(boostedOverlaps, activeColumns);
        activeVector.replaceFromToWith(0, numColumns, false);
        for (int index : activeColumns)
            activeVector.set(index);

        if (learn) {
            adaptSynapses(inputVector, activeColumns);
            updateDutyCycles(overlaps, activeVector);
            bumpUpWeakColumns();
            updateBoostFactors();
            if ((iterationNum % updatePeriod) == 0) {
                updateInhibitionRadius();
                updateMinDutyCycles();
            }
        } else {
            for (int i = 0; i < numColumns; i++) {
                if (activeDutyCycles.getQuick(i) == 0)
                    activeVector.clear(i);
            }
        }
    }

    private void updateMinDutyCycles() {
        if (globalInhibition || inhibitionRadius > MathUtils.max(columnDimensions)) {
            // updateMinDutyCyclesGlobal
            double maxActiveDutyCycles = MathUtils.max(activeDutyCycles.toArray());
            double maxOverlapDutyCycles = MathUtils.max(overlapDutyCycles.toArray());
            minActiveDutyCycles.assign(minPctActiveDutyCycles * maxActiveDutyCycles);
            minOverlapDutyCycles.assign(minPctOverlapDutyCycles * maxOverlapDutyCycles);
        } else {
            // updateMinDutyCyclesLocal
            for (int column = 0; column < numColumns; column++) {
                List<Integer> neighbors = getNeighborsND(column, columnDimensions, inhibitionRadius, false);
                neighbors.add(column);
                double maxActiveDuty = 0;
                double maxOverlapDuty = 0;
                for (int index : neighbors) {
                    maxActiveDuty = Math.max(maxActiveDuty, activeDutyCycles.getQuick(index));
                    maxOverlapDuty = Math.max(maxOverlapDuty, overlapDutyCycles.getQuick(index));
                }

                minActiveDutyCycles.setQuick(column, maxActiveDuty * minPctActiveDutyCycles);
                minOverlapDutyCycles.setQuick(column, maxOverlapDuty * minPctOverlapDutyCycles);
            }
        }
    }

    private void updateBoostFactors() {
        for (int i = 0; i < numColumns; i++) {
            if (minActiveDutyCycles.getQuick(i) > 0) {
                if (activeDutyCycles.getQuick(i) > minActiveDutyCycles.getQuick(i))
                    boostFactors.setQuick(i, 1.0);
                else
                    boostFactors.setQuick(i, ((1 - maxBoost) / minActiveDutyCycles.getQuick(i) *
                            activeDutyCycles.getQuick(i)) + maxBoost);
            }
        }
    }

    private void bumpUpWeakColumns() {
        for (int i = 0; i < numColumns; i++) {
            if (overlapDutyCycles.getQuick(i) >= minOverlapDutyCycles.getQuick(i)) {
                continue;
            }
            final DoubleMatrix1D perm = MathUtils.getRow(permanences, i);
            BitVector potential = MathUtils.getRow(potentialPools, i);
            potential.forEachIndexFromToInState(0, potential.size() - 1, true, new IntProcedure() {
                @Override
                public boolean apply(int index) {
                    perm.setQuick(index, perm.getQuick(index) + synPermBelowStimulusInc);
                    return true;
                }
            });
            updatePermanencesForColumn(perm, i, false);
        }
    }

    private void updateDutyCycles(IntMatrix1D overlaps, BitVector activeVector) {
        IntMatrix1D newOverlapVal = new DenseIntMatrix1D(numColumns);
        IntMatrix1D newActiveVal = new DenseIntMatrix1D(numColumns);

        for (int i = 0; i < numColumns; i++) {
            newOverlapVal.setQuick(i, overlaps.getQuick(i) > 0 ? 1 : 0);
            newActiveVal.setQuick(i, activeVector.get(i) ? 1 : 0);
        }

        long period = dutyCyclePeriod > iterationNum ? iterationNum : dutyCyclePeriod;

        for (int i = 0; i < overlapDutyCycles.size(); i++)
            overlapDutyCycles.setQuick(i, overlapDutyCycles.getQuick(i) * (period - 1) + newOverlapVal.getQuick(i) / period);

        for (int i = 0; i < activeDutyCycles.size(); i++)
            activeDutyCycles.setQuick(i, activeDutyCycles.getQuick(i) * (period - 1) + newActiveVal.getQuick(i) / period);
    }

    private void adaptSynapses(BitVector inputVector, List<Integer> activeColumns) {
        final DoubleMatrix1D permChanges = new DenseDoubleMatrix1D(numInputs);
        permChanges.assign(-1 * synPermInactiveDec);
        inputVector.forEachIndexFromToInState(0, inputVector.size() - 1, true, new IntProcedure() {
            @Override
            public boolean apply(int index) {
                permChanges.setQuick(index, synPermActiveInc);
                return true;
            }
        });

        for (int columnIndex : activeColumns) {
            BitVector potential = MathUtils.getRow(potentialPools, columnIndex);
            final DoubleMatrix1D perm = MathUtils.getRow(permanences, columnIndex);
            potential.forEachIndexFromToInState(0, potential.size() - 1, true, new IntProcedure() {
                @Override
                public boolean apply(int index) {
                    perm.setQuick(index, perm.getQuick(index) + permChanges.getQuick(index));
                    return true;
                }
            });


            updatePermanencesForColumn(perm, columnIndex, true);
        }
    }

    private void inhibitColumns(DoubleMatrix1D boostedOverlaps, List<Integer> activeColumns) {
        double density = localAreaDensity;
        if (numActiveColumnsPerInhArea > 0) {
            double inhibitionArea = Math.pow(2 * inhibitionRadius + 1.0, columnDimensions.length);
            inhibitionArea = Math.min(inhibitionArea, numColumns);
            density = numActiveColumnsPerInhArea / inhibitionArea;
            density = Math.min(density, 0.5);
        }

        DoubleMatrix1D overlapsWithNoise = new DenseDoubleMatrix1D(numColumns);
        for (int i = 0; i < numColumns; i++) {
            overlapsWithNoise.setQuick(i, boostedOverlaps.getQuick(i) + tieBreaker.getQuick(i));
        }

        activeColumns.clear();
        if (globalInhibition || inhibitionRadius > MathUtils.max(columnDimensions)) {
            // inhibitColumnsGlobal
            int numActive = (int) (density * numColumns);
            TreeMap<Integer, Double> winners = new TreeMap<>();
            for (int i = 0; i < numColumns; i++) {
                double score = overlapsWithNoise.getQuick(i);
                if (winners.size() < numActive || score > winners.get(winners.lastKey()))
                    winners.put(i, score);
            }

            for (int key : winners.keySet()) {
                activeColumns.add(key);
            }
        } else {
            // inhibitColumnsLocal
            double arbitration = MathUtils.max(overlapsWithNoise.toArray()) / 1000.0;
            for (int column = 0; column < numColumns; column++) {
                List<Integer> neighbors = getNeighborsND(column, columnDimensions, inhibitionRadius, false);
                int numActive = (int) (0.5 + (density * (neighbors.size() + 1)));
                int numBigger = 0;
                for (int index : neighbors) {
                    if (overlapsWithNoise.getQuick(index) > overlapsWithNoise.getQuick(column)) {
                        numBigger++;
                    }
                }

                if (numBigger < numActive) {
                    activeColumns.add(column);
                    overlapsWithNoise.setQuick(column, overlapsWithNoise.getQuick(column) + arbitration);
                }

            }
        }
    }

    private List<Integer> getNeighborsND(int column, int[] columnDimensions, int inhibitionRadius, boolean wrapAround) {
        List<Integer> neighbors = new ArrayList<>();
        CoordinateConverterND conv = new CoordinateConverterND(columnDimensions);

        List<Integer> columnCoord = new ArrayList<>();
        conv.toCoord(column, columnCoord);

        List<List<Integer>> rangeND = new ArrayList<>();

        for (int i = 0; i < columnDimensions.length; i++) {
            List<Integer> curRange = new ArrayList<>();
            for (int j = columnCoord.get(i) - inhibitionRadius; j <= columnCoord.get(i) + inhibitionRadius; j++) {
                if (wrapAround) {
                    curRange.add((j + columnDimensions[i]) % columnDimensions[i]);
                } else if (j >= 0 && j < columnDimensions[i]) {
                    curRange.add(j);
                }
            }
            rangeND.add(0, curRange);
        }

        List<List<Integer>> neighborCoords = MathUtils.cartesianProduct(rangeND);
        for (List<Integer> coords : neighborCoords) {
            int index = conv.toIndex(coords);
            if (index != column)
                neighbors.add(index);
        }

        return neighbors;
    }

    private void calculateOverlapPct(IntMatrix1D overlaps, DoubleMatrix1D overlapsPct) {
        overlapsPct.assign(0);
        for (int i = 0; i < numColumns; i++) {
            double connectedCount = connectedCounts.get(i);
            if (connectedCount != 0) {
                overlapsPct.setQuick(i, overlaps.getQuick(i) / connectedCount);
            } else {
                // The intent here is to see if a cell matches its input well.
                // Therefore if nothing is connected the overlapPct is set to 0.
                overlapsPct.setQuick(i, 0);
            }
        }
    }

    private void calculateOverlap(final BitVector inputVector, final IntMatrix1D overlaps) {
        overlaps.assign(0);
        connectedSynapses.forEachCoordinateInState(true, new IntIntProcedure() {
            @Override
            public boolean apply(int first, int second) {
                overlaps.setQuick(first, overlaps.getQuick(first) + (inputVector.get(second) ? 1 : 0));
                return true;
            }
        });
        if (stimulusThreshold > 0) {
            overlaps.assign(new IntProcedure() {
                @Override
                public boolean apply(int element) {
                    return element < stimulusThreshold;
                }
            }, 0);
        }
    }

    /**************Getters and Setters*************************/

    public String getFilePropName() {
        return filePropName;
    }

    public void setFilePropName(String filePropName) {
        this.filePropName = filePropName;
    }

    public int getPotentialRadius() {
        return potentialRadius;
    }

    public void setPotentialRadius(int potentialRadius) {
        this.potentialRadius = potentialRadius;
    }

    public double getPotentialPct() {
        return potentialPct;
    }

    public void setPotentialPct(double potentialPct) {
        this.potentialPct = potentialPct;
    }

    public boolean isGlobalInhibition() {
        return globalInhibition;
    }

    public void setGlobalInhibition(boolean globalInhibition) {
        this.globalInhibition = globalInhibition;
    }

    public double getLocalAreaDensity() {
        return localAreaDensity;
    }

    public void setLocalAreaDensity(double localAreaDensity) {
        this.localAreaDensity = localAreaDensity;
    }

    public long getNumActiveColumnsPerInhArea() {
        return numActiveColumnsPerInhArea;
    }

    public void setNumActiveColumnsPerInhArea(long numActiveColumnsPerInhArea) {
        this.numActiveColumnsPerInhArea = numActiveColumnsPerInhArea;
    }

    public long getStimulusThreshold() {
        return stimulusThreshold;
    }

    public void setStimulusThreshold(long stimulusThreshold) {
        this.stimulusThreshold = stimulusThreshold;
    }

    public double getSynPermInactiveDec() {
        return synPermInactiveDec;
    }

    public void setSynPermInactiveDec(double synPermInactiveDec) {
        this.synPermInactiveDec = synPermInactiveDec;
    }

    public double getSynPermActiveInc() {
        return synPermActiveInc;
    }

    public void setSynPermActiveInc(double synPermActiveInc) {
        this.synPermActiveInc = synPermActiveInc;
    }

    public double getSynPermConnected() {
        return synPermConnected;
    }

    public void setSynPermConnected(double synPermConnected) {
        this.synPermConnected = synPermConnected;
    }

    public double getMinPctOverlapDutyCycles() {
        return minPctOverlapDutyCycles;
    }

    public void setMinPctOverlapDutyCycles(double minPctOverlapDutyCycles) {
        this.minPctOverlapDutyCycles = minPctOverlapDutyCycles;
    }

    public double getMinPctActiveDutyCycles() {
        return minPctActiveDutyCycles;
    }

    public void setMinPctActiveDutyCycles(double minPctActiveDutyCycles) {
        this.minPctActiveDutyCycles = minPctActiveDutyCycles;
    }

    public long getDutyCyclePeriod() {
        return dutyCyclePeriod;
    }

    public void setDutyCyclePeriod(long dutyCyclePeriod) {
        this.dutyCyclePeriod = dutyCyclePeriod;
    }

    public double getMaxBoost() {
        return maxBoost;
    }

    public void setMaxBoost(double maxBoost) {
        this.maxBoost = maxBoost;
    }

    public int getInhibitionRadius() {
        return inhibitionRadius;
    }

    public void setInhibitionRadius(int inhibitionRadius) {
        this.inhibitionRadius = inhibitionRadius;
    }

    public double getSynPermBelowStimulusInc() {
        return synPermBelowStimulusInc;
    }

    public void setSynPermBelowStimulusInc(double synPermBelowStimulusInc) {
        this.synPermBelowStimulusInc = synPermBelowStimulusInc;
    }

    public double getSynPermTrimThreshold() {
        return synPermTrimThreshold;
    }

    public void setSynPermTrimThreshold(double synPermTrimThreshold) {
        this.synPermTrimThreshold = synPermTrimThreshold;
    }

    public double getSynPermMin() {
        return synPermMin;
    }

    public void setSynPermMin(double synPermMin) {
        this.synPermMin = synPermMin;
    }

    public double getSynPermMax() {
        return synPermMax;
    }

    public void setSynPermMax(double synPermMax) {
        this.synPermMax = synPermMax;
    }

    public long getUpdatePeriod() {
        return updatePeriod;
    }

    public void setUpdatePeriod(long updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    public double getInitConnectedPct() {
        return initConnectedPct;
    }

    public void setInitConnectedPct(double initConnectedPct) {
        this.initConnectedPct = initConnectedPct;
    }
}
