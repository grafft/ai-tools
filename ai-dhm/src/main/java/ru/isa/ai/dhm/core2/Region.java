package ru.isa.ai.dhm.core2;

import cern.colt.function.tint.IntProcedure;
import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tint.IntMatrix1D;
import cern.colt.matrix.tint.impl.DenseIntMatrix1D;
import com.google.common.primitives.Ints;
import ru.isa.ai.dhm.RegionSettings;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Region {
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

    private int desiredLocalActivity;
    private List<Region> childRegions = new ArrayList<>();
    private List<Region> parentRegions = new ArrayList<>();
    private Map<Integer, Column> columns = new HashMap<>();
    private Map<Integer, Cell> allCells = new HashMap<>();
    private BitVector activeColumns;
    private IntMatrix1D overlaps;

    private int numColumns;
    private int xDimension;
    private int yDimension;
    private int numInputs;
    private int xInput;
    private int yInput;
    private int iterationNum = 0;

    public Region(RegionSettings settings) {
        this.xDimension = settings.xDimension;
        this.yDimension = settings.yDimension;
        this.numColumns = settings.xDimension * settings.yDimension;
        this.xInput = settings.xInput;
        this.yInput = settings.yInput;
        this.numInputs = settings.xInput * settings.yInput;
        this.desiredLocalActivity = settings.desiredLocalActivity;
        for (int i = 0; i < settings.xDimension; i++) {
            for (int j = 0; j < settings.yDimension; j++) {
                Column column = new Column(i * yDimension + j, new int[]{i, j}, settings);
                columns.put(i, column);
                for (Cell cell : column.getCells()) {
                    allCells.put(cell.getIndex(), cell);
                }
            }
        }
        for (Column column : columns.values()) {
            column.setOtherCells(allCells);
        }
        activeColumns = new BitVector(numColumns);
        overlaps = new DenseIntMatrix1D(numColumns);
    }

    public void initialization() {
        for (Column column : columns.values()) {
            double pctOfInput = column.getIndex() / Math.max(columns.size() - 1, 1.0);
            column.initialization(pctOfInput);
        }
    }

    public BitVector spatialPooling(BitVector input) {
        iterationNum++;
        sOverlap(input);
        sInhibition();
        sLearning();

        return activeColumns;
    }

    private void sLearning() {
        activeColumns.forEachIndexFromToInState(0, activeColumns.size() - 1, true, new IntProcedure() {
            @Override
            public boolean apply(int element) {
                columns.get(element).learning();
                return true;
            }
        });
        long period = dutyCyclePeriod > iterationNum ? iterationNum : dutyCyclePeriod;
        for (Column column : columns.values()) {
            double newActive = (column.getActiveDutyCycles() * (period - 1) + (activeColumns.getQuick(column.getIndex()) ? 0 : 1)) / period;
            column.setActiveDutyCycles(newActive);
            double newOverlap = (column.getOverlapDutyCycles() * (period - 1) + (overlaps.getQuick(column.getIndex()) > 0 ? 0 : 1)) / period;
            column.setOverlapDutyCycles(newOverlap);

            double maxActiveDuty = 0;
            double maxOverlapDuty = 0;
            for (int index : getNeighbors(column)) {
                maxActiveDuty = Math.max(maxActiveDuty, columns.get(index).getActiveDutyCycles());
                maxOverlapDuty = Math.max(maxOverlapDuty, columns.get(index).getOverlapDutyCycles());
            }

            double minDutyCycle = 0.01 * maxActiveDuty;
            if (newOverlap < minDutyCycle)
                column.stimulate();

            column.setBoost(boostFunction(newActive, minDutyCycle));
            column.setInhibitionRadius(averageReceptiveFieldSize());
        }
    }

    private void sInhibition() {
        for (Column column : columns.values()) {
            double minLocalActivity = MathUtils.kthScore(overlaps.viewSelection(getNeighbors(column)), desiredLocalActivity);
            if (minLocalActivity > 0 && column.getOverlap() >= minLocalActivity)
                activeColumns.set(column.getIndex());
        }
    }

    private void sOverlap(BitVector input) {
        for (Column column : columns.values()) {
            overlaps.setQuick(column.getIndex(), column.overlapCalculating(input));
        }
    }

    private int[] getNeighbors(Column column) {
        List<Integer> neighbors = new ArrayList<>();
        for (int i = column.getCoords()[0] - column.getInhibitionRadius(); i < column.getCoords()[0] + column.getInhibitionRadius(); i++) {
            if (i >= 0 && i < xDimension) {
                for (int j = column.getCoords()[1] - column.getInhibitionRadius(); j < column.getCoords()[1] + column.getInhibitionRadius(); j++) {
                    if (j >= 0 && j < yDimension) {
                        neighbors.add(i * yDimension + j);
                    }
                }
            }
        }
        return Ints.toArray(neighbors);
    }

    private int averageReceptiveFieldSize() {
        final List<Integer> listX = new ArrayList<>();
        final List<Integer> listY = new ArrayList<>();
        for (final Column column : columns.values()) {
            final BitVector connected = column.getProximalSegment().getConnectedSynapses();
            connected.forEachIndexFromToInState(0, connected.size() - 1, true, new IntProcedure() {
                @Override
                public boolean apply(int element) {
                    int inputIndex = column.getProximalSegment().getPotentialSynapses().get(element).getInputSource();
                    listX.add(inputIndex / yInput);
                    listY.add(inputIndex - (inputIndex / yInput) * yInput);
                    return true;
                }
            });
        }

        int sumX = 0;
        for (Integer value : listX)
            sumX += value;
        int sumY = 0;
        for (Integer value : listY)
            sumY += value;
        return sumX / listX.size() > sumY / listY.size() ? sumX / listX.size() : sumY / listY.size();
    }

    private double boostFunction(double activeValue, double minDutyCycle) {
        double value = 1;
        if (activeValue < minDutyCycle)
            value = ((1 - maxBoost) / minDutyCycle * activeValue) + maxBoost;
        return value;
    }

    public void activeCalculation() {
        activeColumns.forEachIndexFromToInState(0, activeColumns.size() - 1, true, new IntProcedure() {
            @Override
            public boolean apply(int element) {
                columns.get(element).updateActiveCells();
                return true;
            }
        });
    }

    public void predictiveCalculation() {
        for (Column column : columns.values()) {
            column.updatePredictiveCells();
        }
    }

    public void learning() {
        for (Column column : columns.values()) {
            column.predictiveLearning();
        }
    }

    public void addParent(Region parent) {
        parentRegions.add(parent);
    }

    public void addChild(Region child) {
        childRegions.add(child);
    }

}
