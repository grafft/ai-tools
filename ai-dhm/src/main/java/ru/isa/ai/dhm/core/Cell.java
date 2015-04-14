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
    private double minThreshold = 5.0;
    private int historyDeep = 2;

    private List<LateralSegment> distalSegments = new ArrayList<>();
    private State[] stateHistory; // история состояний клетки
    private boolean[] learnHistory; // история состояния обучения клетки (когда клетка была в состоянии обучения)
    // если клетка в состоянии обучения, то к ней могут быть добавлены новые синапсы при создании нового латерального сегмента
    /*
    Note that even with all the cells in the column in the active state, only
    one will be in the learn state. It is an important trick. Throughout the process of choose an active distal dendrite segment,
    only synapses connected to cells in the learn state are considered.
    This avoids overrepresenting a fully active column in dendritic segments.
    */

    public Cell(int index, double minThreshold, int historyDeep) {
        this.index = index;
        this.minThreshold = minThreshold;
        this.historyDeep = historyDeep;

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
    public LateralSegment getMostActiveSegment(boolean inLearning, int historyLevel) {
        LateralSegment segment = null;
        for (LateralSegment s : distalSegments) {
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
    public LateralSegment getBestMatchingSegment(int historyLevel) {
        LateralSegment bestSegment = null;
        for (LateralSegment s : distalSegments) {
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

    /// Advances this cell to the next time step.
    ///
    /// The current state of this cell (active, learning, predicting) will be set as the
    /// previous state and the current state will be reset to no cell activity by
    /// default until it can be determined.
    public void updateHistory(Map<Integer, Cell> cells) {
        for (LateralSegment segment : distalSegments) {
            segment.updateHistory(cells);
        }
        for (int i = historyDeep - 1; i > 0; i--) {
            stateHistory[i] = stateHistory[i - 1];
            learnHistory[i] = learnHistory[i - 1];
        }

        stateHistory[0] = State.passive;
        learnHistory[0] = false;
    }

    public List<LateralSegment> getLateralSegments() {
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
