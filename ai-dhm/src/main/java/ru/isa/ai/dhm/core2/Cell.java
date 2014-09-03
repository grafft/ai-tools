package ru.isa.ai.dhm.core2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Cell {

    public enum State {
        active, predictive, passive
    }

    private int index;
    private State currentState = State.passive;
    private State previousState = State.passive;
    private boolean toLearn = false;

    private int minThreshold = 5;

    private List<DistalSegment> distalSegments = new ArrayList<>();

    public Cell(int index) {
        this.index = index;
    }

    public DistalSegment getActiveSegment(boolean inLearning) {
        DistalSegment segment = null;
        for (DistalSegment s : distalSegments) {
            if (segment == null) {
                segment = s;
            } else if (s.isSequenceSegment() && !segment.isSequenceSegment()) {
                segment = s;
            } else if (segment.countInState(inLearning) < s.countInState(inLearning)) {
                segment = s;
            }
        }
        return segment;
    }

    public DistalSegment getBestMatchingSegment() {
        DistalSegment bestSegment = null;
        for (DistalSegment s : distalSegments) {
            if ((bestSegment == null || bestSegment.countConnected() < s.countConnected()) && s.countConnected() > minThreshold)
                bestSegment = s;
        }
        return bestSegment;
    }

    public void updateSegments(Map<Integer, Cell> cells) {
        for (DistalSegment segment : distalSegments) {
            segment.updateSynapses(cells);
        }
    }

    public List<DistalSegment> getDistalSegments() {
        return distalSegments;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public State getPreviousState() {
        return previousState;
    }

    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

    public boolean isToLearn() {
        return toLearn;
    }

    public void setToLearn(boolean toLearn) {
        this.toLearn = toLearn;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
