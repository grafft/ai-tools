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

    private int newSynapsesCount = 10;

    private DendriticSegment proximalSegment;
    private Map<Integer, Cell> otherCells;

    private Cell[] cells;
    private Map<Integer, List<SegmentUpdate>> toUpdate = new HashMap<>();

    public Column(int index, int[] coords, RegionSettings settings) {
        this.index = index;
        this.coords = coords;
        cells = new Cell[settings.cellsPerColumn];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Cell(index * settings.cellsPerColumn + i);
        }
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

    public void updateActiveCells() {
        boolean wasPredicted = false;
        boolean toLearn = false;
        for (Cell cell : cells) {
            if (cell.getPreviousState() == Cell.State.predictive) {
                DistalSegment s = cell.getActiveSegment(false);
                if (s.isSequenceSegment()) {
                    wasPredicted = true;
                    cell.setCurrentState(Cell.State.active);
                    if (s.segmentActive(true)) {
                        toLearn = true;
                        cell.setToLearn(true);
                    }
                }
            }
        }
        if (!wasPredicted) {
            for (Cell cell : cells) {
                cell.setCurrentState(Cell.State.active);
            }
        }
        if (!toLearn) {
            Cell bestCell = getBestMatchingCell();
            bestCell.setToLearn(true);
            SegmentUpdate sUpdate = getSegmentActiveSynapses(otherCells, null, true);
            sUpdate.isSequenceSegment = true;
            addToUpdate(bestCell, sUpdate);
        }
    }

    public void updatePredictiveCells() {
        for (Cell cell : cells) {
            for (DistalSegment s : cell.getDistalSegments()) {
                if (s.segmentActive(false)) {
                    cell.setCurrentState(Cell.State.predictive);

                    SegmentUpdate sUpdate = getSegmentActiveSynapses(otherCells, s, false);
                    addToUpdate(cell, sUpdate);
                }
            }
            DistalSegment predS = cell.getBestMatchingSegment();
            SegmentUpdate predSUpdate = getSegmentActiveSynapses(otherCells, predS, true);
            addToUpdate(cell, predSUpdate);
        }
    }

    private void addToUpdate(Cell cell, SegmentUpdate predSUpdate) {
        List<SegmentUpdate> list = toUpdate.get(cell.getIndex());
        if (list == null) {
            list = new ArrayList<>();
            toUpdate.put(cell.getIndex(), list);
        }
        list.add(predSUpdate);
    }

    public void predictiveLearning() {
        for (Cell cell : cells) {
            if (cell.isToLearn()) {
                adaptSegments(toUpdate.get(cell.getIndex()), true);
                toUpdate.remove(cell.getIndex());
            } else if (cell.getCurrentState() != Cell.State.predictive && cell.getPreviousState() == Cell.State.predictive) {
                adaptSegments(toUpdate.get(cell.getIndex()), false);
                toUpdate.remove(cell.getIndex());
            }
        }
    }

    private void adaptSegments(List<SegmentUpdate> segmentUpdates, boolean reinforcement) {
        for (SegmentUpdate su : segmentUpdates) {
            if (reinforcement) {
                for (Synapse synapse : su.synapses) {
                    synapse.increasePermanence();
                }
            } else {
                for (Synapse synapse : su.synapses) {
                    synapse.decreasePermanence();
                }
            }
        }

        for (Cell cell : cells) {
            cell.updateSegments(otherCells);
        }
    }

    private SegmentUpdate getSegmentActiveSynapses(Map<Integer, Cell> cells, DistalSegment s, boolean addNewSynapses) {
        final SegmentUpdate su = new SegmentUpdate();
        if (s != null) {
            su.segment = s;
            su.synapses = s.getActiveSynapses();
        } else {
            su.segment = new DistalSegment();
        }
        if (addNewSynapses) {
            List<Cell> cellsToLearn = new ArrayList<>();
            for (Cell cell : cells.values()) {
                if (cell.isToLearn())
                    cellsToLearn.add(cell);
            }
            for (int i = 0; i < newSynapsesCount - su.synapses.size(); i++) {
                int index = (int) (cellsToLearn.size() * Math.random());
                Synapse synapse = new Synapse(cellsToLearn.get(index).getIndex());
                su.synapses.add(synapse);
                su.segment.addSynapse(synapse);
            }
        }
        return su;
    }

    private Cell getBestMatchingCell() {
        Cell bestMatching = null;
        DistalSegment bestSegment = null;
        for (Cell cell : cells) {
            DistalSegment s = cell.getBestMatchingSegment();
            if (s != null && (bestSegment == null || bestSegment.countConnected() < s.countConnected())) {
                bestSegment = s;
                bestMatching = cell;
            }
        }
        return bestMatching;
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

    public void setOtherCells(Map<Integer, Cell> otherCells) {
        this.otherCells = otherCells;
    }

    public Cell[] getCells() {
        return cells;
    }

    private class SegmentUpdate {
        DistalSegment segment = null;
        List<Synapse> synapses = new ArrayList<>();
        boolean isSequenceSegment = false;
    }
}
