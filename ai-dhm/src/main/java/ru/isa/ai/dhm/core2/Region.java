package ru.isa.ai.dhm.core2;

import cern.colt.function.tint.IntProcedure;
import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
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
    private int desiredLocalActivity;
    private List<Region> childRegions = new ArrayList<>();
    private List<Region> parentRegions = new ArrayList<>();
    private Map<Integer, Column> columns = new HashMap<>();
    private BitVector activeColumns;
    private RegionSettings settings;
    private IntMatrix1D overlaps;
    private int iterationNum = 0;

    private DoubleMatrix1D activeDutyCycles;
    private DoubleMatrix1D overlapDutyCycles;
    private DoubleMatrix1D minActiveDutyCycle;
    private DoubleMatrix1D minOverlapDutyCycle;

    public Region(RegionSettings settings) {
        this.settings = settings;
        this.desiredLocalActivity = settings.desiredLocalActivity;
        for (int i = 0; i < settings.numColumns; i++) {
            columns.put(i, new Column(i, settings));
        }
        activeColumns = new BitVector(settings.numColumns);
        overlaps = new DenseIntMatrix1D(settings.numColumns);
    }

    public void initialization() {
        for (Column column : columns.values()) {
            double pctOfInput = column.getIndex() / Math.max(columns.size() - 1, 1.0);
            column.initialization(pctOfInput);
        }
    }

    public BitVector compute(BitVector input) {
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
            activeDutyCycles.setQuick(column.getIndex(),
                    (activeDutyCycles.getQuick(column.getIndex()) * (period - 1) + (activeColumns.getQuick(column.getIndex()) ? 0 : 1)) / period);
            double newOverlap = (overlapDutyCycles.getQuick(column.getIndex()) * (period - 1) + (overlaps.getQuick(column.getIndex()) > 0 ? 0 : 1)) / period;
            overlapDutyCycles.setQuick(column.getIndex(), newOverlap);

            double maxActiveDuty = 0;
            double maxOverlapDuty = 0;
            for (int index : getNeighbors(column)) {
                maxActiveDuty = Math.max(maxActiveDuty, activeDutyCycles.getQuick(index));
                maxOverlapDuty = Math.max(maxOverlapDuty, overlapDutyCycles.getQuick(index));
            }

            double minDutyCycle = 0.01 * maxActiveDuty;
            if (newOverlap < minDutyCycle)
                column.stimulate();

            //column.setBoost();
            //column.setInhibitionRadius(averageReceptieveFieldSize());
        }
    }

    private void sInhibition() {
        for (Column column : columns.values()) {
            double minLocalActivity = MathUtils.kthScore(overlaps.viewSelection(getNeighbors(column)), desiredLocalActivity);
            if (minLocalActivity > 0 && column.getOverlap() >= minLocalActivity)
                activeColumns.set(column.getIndex());
        }
    }

    private int[] getNeighbors(Column column) {
        List<Integer> neighbors = new ArrayList<>();
        for (int i = column.getIndex() - column.getInhibitionRadius(); i < column.getIndex() + column.getInhibitionRadius(); i++) {
            if (i >= 0 && i < columns.size())
                neighbors.add(i);
        }
        return Ints.toArray(neighbors);
    }

    private void sOverlap(BitVector input) {
        for (Column column : columns.values()) {
            overlaps.setQuick(column.getIndex(), column.overlapCalculating(input));
        }
    }

    public void addParent(Region parent) {
        parentRegions.add(parent);
    }

    public void addChild(Region child) {
        childRegions.add(child);
    }
}
