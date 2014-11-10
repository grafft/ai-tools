package ru.isa.ai.dhm.core;

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
        active,     // активное состояние клетки - клетка активна от прямого (feed-forward) воздействия
        predictive, // состояние предсказания - клетка активна от латерального воздействия
        passive     // пассивное (неактивное) состояние
    }

    private int index;
    private int minThreshold = 5;
    private int historyDeep = 2;

    private List<DistalSegment> distalSegments = new ArrayList<>(); // TODO P: нигде не обновляется, всегда пуст
    private State[] stateHistory; // история состояний клетки
    private boolean[] learnHistory;

    public Cell(int index) {
        this.index = index;
        stateHistory = new State[historyDeep];
        learnHistory = new boolean[historyDeep];
        for (int i = 0; i < historyDeep; i++) {
            stateHistory[i] = State.passive;
            learnHistory[i] = false;
        }
    }

    /**
     * Возвращает самый активный по состоянию сегмент. Если активны несколько сегментов, то сегментам последовательностей
     * отдается предпочтение. В противном случае предпочтение отдается сегментам с наибольшей активностью.
     *
     * @param inLearning
     * @return
     */
    public DistalSegment getMostActiveSegment(boolean inLearning, int historyLevel) {
        DistalSegment segment = null;
        for (DistalSegment s : distalSegments) {
            if (segment == null) {
                segment = s;
            } else if (s.isSequenceSegment() && !segment.isSequenceSegment()) {
                segment = s;
            } else if (segment.countInState(inLearning, historyLevel) < s.countInState(inLearning, historyLevel)) {
                segment = s;
            }
        }
        return segment;
    }

    /**
     * Возвращается сегмент с самсым большим числом активных синапсов. При этом значения перманентности синапсов
     * могут быть ниже порога connectedPerm. Число активных синапсов допускается ниже порога activationThreshold,
     * но должно быть выше minThreshold.
     *
     * @param historyLevel
     * @return
     */
    public DistalSegment getBestMatchingSegment(int historyLevel) {
        DistalSegment bestSegment = null;
        for (DistalSegment s : distalSegments) {
            if (bestSegment != null) {
                int connected = s.getActiveSynapses(historyLevel).size();
                int bestConnected = bestSegment.getActiveSynapses(historyLevel).size();
                if (connected > minThreshold && bestConnected < connected)
                    bestSegment = s;
            } else {
                bestSegment = s;
            }
        }
        return bestSegment;
    }

    public void updateHistory(Map<Integer, Cell> cells) {
        for (DistalSegment segment : distalSegments) {
            segment.updateHistory(cells);
        }
        for (int i = historyDeep - 1; i > 0; i--) {
            stateHistory[i] = stateHistory[i - 1];
            learnHistory[i] = learnHistory[i - 1];
        }
    }

    public List<DistalSegment> getDistalSegments() {
        return distalSegments;
    }

    public State[] getStateHistory() {
        return stateHistory;
    }

    public boolean[] getLearnHistory() {
        return learnHistory;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
