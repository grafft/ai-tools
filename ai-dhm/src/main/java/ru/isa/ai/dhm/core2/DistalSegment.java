package ru.isa.ai.dhm.core2;

import cern.colt.matrix.tbit.BitVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 03.09.2014
 * Time: 12:16
 */
public class DistalSegment {
    private Map<Integer, Synapse> synapses = new HashMap<>();
    private List<Synapse> activeSynapses = new ArrayList<>();
    private List<Synapse> learnSynapses = new ArrayList<>();
    private List<Synapse> connectedSynapses = new ArrayList<>();

    private int activationThreshold = 20;
    private boolean isSequenceSegment = false;

    public boolean segmentActive(boolean inLearning) {
        if (!inLearning) {
            return activeSynapses.size() > activationThreshold;
        } else {
            return learnSynapses.size() > activationThreshold;
        }
    }

    public void updateSynapses(Map<Integer, Cell> cells) {
        connectedSynapses.clear();
        activeSynapses.clear();
        learnSynapses.clear();
        for (Map.Entry<Integer, Synapse> entry : synapses.entrySet()) {
            if (entry.getValue().isConnected()) {
                connectedSynapses.add(entry.getValue());
                if (cells.get(entry.getKey()).getCurrentState() == Cell.State.active) {
                    activeSynapses.add(entry.getValue());
                } else if (cells.get(entry.getKey()).isToLearn()) {
                    learnSynapses.add(entry.getValue());
                }
            }
        }
    }

    public boolean isSequenceSegment() {
        return isSequenceSegment;
    }

    public void setSequenceSegment(boolean isSequenceSegment) {
        this.isSequenceSegment = isSequenceSegment;
    }

    public int countInState(boolean inLearning) {
        if (inLearning)
            return learnSynapses.size();
        else
            return activeSynapses.size();
    }

    public int countConnected() {
        return connectedSynapses.size();
    }

    public List<Synapse> getActiveSynapses() {
        return activeSynapses;
    }

    public void addSynapse(Synapse synapse) {
        synapses.put(synapse.getInputSource(), synapse);
    }
}
