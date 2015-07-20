package ru.isa.ai.dhm.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 03.09.2014
 * Time: 12:16
 */
/*Дистальный дендритный сегмент (между разными удаленными клетками одного региона)*/
public class LateralSegment {
    private int historyDeep = 2; // глубина истории обучения
    // Integer - индекс клетки в том же слое, с которой потенциально может быть связан данный дистальный дендрит с помошью синапса Synapse
    private Map<Integer, Synapse> synapses = new HashMap<>(); // синапсы дендрита
    // Integer - определенный момент в прошлом // List<Synapse> - список подключенных синапсов в сегменте
    private Map<Integer, List<Synapse>> activeHistory = new HashMap<>();
    private Map<Integer, List<Synapse>> learnHistory = new HashMap<>(); // массив клеток, использующихся во время обучения
    private Map<Integer, List<Synapse>> connectedHistory = new HashMap<>(); // история присоединенных синапсов

    /**
     * Порог активации для сегмента. Если число активных подключенных синапсов в сегменте больше чем
     * activationThreshold, данный сегмент считается активным.
     */
    private int activationThreshold = 20;
    // предсказывает ли данный сегмент активацию своей клетки от прямого входа в следующий момент времени.
    private boolean isSequenceSegment = false;

    public LateralSegment() {
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

    // обновление истории состояний синапсов данного сегмента
    public void updateHistory(Map<Integer, Cell> cells) {
        // сдвиг истории на 1
        for (int i = historyDeep - 1; i > 0; i--) {
            activeHistory.get(i).clear();
            learnHistory.get(i).clear();
            connectedHistory.get(i).clear();
            activeHistory.get(i).addAll(activeHistory.get(i - 1));
            learnHistory.get(i).addAll(learnHistory.get(i - 1));
            connectedHistory.get(i).addAll(connectedHistory.get(i - 1));
        }
        activeHistory.get(0).clear();
        learnHistory.get(0).clear();

        // обновление истории состояния синапсов
        for (Map.Entry<Integer, Synapse> entry : synapses.entrySet()) {
            if (entry.getValue().isConnected()) {
                if (cells.get(entry.getKey()).getStateHistory()[0] == Cell.State.active) {
                    activeHistory.get(0).add(entry.getValue());
                }
                if (cells.get(entry.getKey()).getLearnHistory()[0]) {
                    learnHistory.get(0).add(entry.getValue());
                }
                connectedHistory.get(0).add(entry.getValue());
            }
        }
    }

    public void updateSynapses(List<Integer> indexes, boolean reinforcement) {
        if (reinforcement) {
            for (Synapse synapse : synapses.values()) {
                if (indexes.contains(synapse.getInputSource())) {
                    synapse.increasePermanence();
                } else {
                    synapse.decreasePermanence();
                }
            }
        } else {
            for (Synapse synapse : synapses.values()) {
                if (indexes.contains(synapse.getInputSource())) {
                    synapse.decreasePermanence();
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

    // в режиме обучения возвращается количество синапсов у дендрита в определенный момент времени обучения
    // в режиме распознавания возвращается количество синапсов у дендрита в определенный момент времени распознавания
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
