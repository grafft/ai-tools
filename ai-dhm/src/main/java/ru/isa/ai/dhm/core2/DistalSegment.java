package ru.isa.ai.dhm.core2;

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
    private int historyDeep = 2;
    private Map<Integer, Synapse> synapses = new HashMap<>();
    private Map<Integer, List<Synapse>> activeHistory = new HashMap<>();
    private Map<Integer, List<Synapse>> learnHistory = new HashMap<>();
    private Map<Integer, List<Synapse>> connectedHistory = new HashMap<>();

    private int activationThreshold = 20;
    private boolean isSequenceSegment = false;

    public DistalSegment() {
        for (int i = 0; i < historyDeep; i++) {
            activeHistory.put(i, new ArrayList<Synapse>());
            learnHistory.put(i, new ArrayList<Synapse>());
            connectedHistory.put(i, new ArrayList<Synapse>());
        }
    }

    public boolean isActiveInState(boolean inLearning, int historyLevel) {
        return inLearning ? learnHistory.get(historyLevel).size() > activationThreshold :
                activeHistory.get(historyLevel).size() > activationThreshold;
    }

    public void updateSynapses(Map<Integer, Cell> cells) {
        for (int i = historyDeep - 1; i > 0; i--) {
            activeHistory.get(i).clear();
            learnHistory.get(i).clear();
            activeHistory.get(i).addAll(activeHistory.get(i - 1));
            learnHistory.get(i).addAll(learnHistory.get(i - 1));
        }
        activeHistory.get(0).clear();
        learnHistory.get(0).clear();
        for (Map.Entry<Integer, Synapse> entry : synapses.entrySet()) {
            if (entry.getValue().isConnected()) {
                if (cells.get(entry.getKey()).getStateHistory()[0] == Cell.State.active) {
                    activeHistory.get(0).add(entry.getValue());
                } else if (cells.get(entry.getKey()).getLearnHistory()[0]) {
                    learnHistory.get(0).add(entry.getValue());
                }
            }
        }
    }

    public int countConnected(int historyLevel) {
        return connectedHistory.get(historyLevel).size();
    }

    public boolean isSequenceSegment() {
        return isSequenceSegment;
    }

    public void setSequenceSegment(boolean isSequenceSegment) {
        this.isSequenceSegment = isSequenceSegment;
    }

    public int countInState(boolean inLearning, int historyLevel) {
        return inLearning ? learnHistory.get(historyLevel).size() : activeHistory.get(historyLevel).size();
    }

    public List<Synapse> getActiveSynapses(int historyLevel) {
        return activeHistory.get(historyLevel);
    }

    public void addSynapse(Synapse synapse) {
        synapses.put(synapse.getInputSource(), synapse);
    }

}
