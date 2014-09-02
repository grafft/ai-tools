package ru.isa.ai.dhm.core2;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.RegionSettings;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Column {
    private int index;
    private int[] coords;
    private int inhibitionRadius;

    private double activeDutyCycles = 0;
    private double overlapDutyCycles = 0;

    private DendriticSegment proximalSegment;

    private Cell[] cells;

    private Random random = new Random();

    public Column(int index, int[] coords, RegionSettings settings) {
        this.index = index;
        this.coords = coords;
        cells = new Cell[settings.cellsPerColumn];
        proximalSegment = new DendriticSegment(settings);
    }

    public void initialization(double ration) {
        proximalSegment.initPotentialSynapses(ration);
        proximalSegment.initPermanences();
    }

    public int overlapCalculating(BitVector input) {
        return proximalSegment.overlapCalculating(input);
    }

    public void learning() {
        proximalSegment.learning();
    }

    public void stimulate() {
        proximalSegment.stimulate();
    }

    public int getIndex() {
        return index;
    }

    public int getOverlap() {
        return proximalSegment.getOverlap();
    }

    public void setBoost(double val) {
        proximalSegment.setBoostFactor(val);
    }

    public int getInhibitionRadius() {
        return inhibitionRadius;
    }

    public void setInhibitionRadius(int inhibitionRadius) {
        this.inhibitionRadius = inhibitionRadius;
    }

    public double getActiveDutyCycles() {
        return activeDutyCycles;
    }

    public void setActiveDutyCycles(double activeDutyCycles) {
        this.activeDutyCycles = activeDutyCycles;
    }

    public double getOverlapDutyCycles() {
        return overlapDutyCycles;
    }

    public void setOverlapDutyCycles(double overlapDutyCycles) {
        this.overlapDutyCycles = overlapDutyCycles;
    }

    public int[] getCoords() {
        return coords;
    }

    public DendriticSegment getProximalSegment() {
        return proximalSegment;
    }
}
