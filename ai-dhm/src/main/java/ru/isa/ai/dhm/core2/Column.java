package ru.isa.ai.dhm.core2;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.RegionSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Column {
    private int index;
    private int[] coords;
    private boolean isActive;

    private int inhibitionRadius = 10;
    private int newSynapsesCount = 10;
    private int minOverlap;
    private double maxBoost = 10.0;
    private double initialPerm = 0.1;

    private ProximalSegment proximalSegment;
    private Map<Integer, Cell> otherCells;

    private Cell[] cells;
    private Map<Integer, List<SegmentUpdate>> toUpdate = new HashMap<>();
    private int[] neighbors;

    private double activeDutyCycle = 0;
    private double overlapDutyCycle = 0;

    public Column(int index, int[] coords, RegionSettings settings) {
        this.index = index;
        this.coords = coords;
        this.minOverlap = settings.minOverlap;
        cells = new Cell[settings.cellsPerColumn];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Cell(index * settings.cellsPerColumn + i);
        }
        proximalSegment = new ProximalSegment(settings);
    }

    /**
     * Инициализация колнки - создание начального списка потенциальных синапсов
     *
     * @param inputCenterX
     * @param inputCenterY
     */
    public void initialization(int inputCenterX, int inputCenterY) {
        proximalSegment.initSynapses(inputCenterX, inputCenterY);
    }

    public int overlapCalculating(BitVector input) {
        return proximalSegment.overlapCalculating(input);
    }

    public void learning(BitVector input) {
        proximalSegment.updateSynapses(input);
    }

    public void updateOverlapDutyCycle(int period) {
        overlapDutyCycle = (overlapDutyCycle * (period - 1) + (getOverlap()) > minOverlap ? 1 : 0) / period;
    }

    public void updateActiveDutyCycle(int period) {
        activeDutyCycle = (activeDutyCycle * (period - 1) + (isActive ? 1 : 0)) / period;
    }

    /**
     * Если activeDutyCycle больше minValue, то значение ускорения равно 1. Ускорение начинает линейно увеличиваться
     * как только activeDutyCycle колонки падает ниже minDutyCycle.
     *
     * @param minValue
     */
    public void updateBoostFactor(double minValue) {
        double value = 1;
        if (activeDutyCycle < minValue)
            value = 1 + (minValue - activeDutyCycle) * (maxBoost - 1) / minValue;
        proximalSegment.setBoostFactor(value);
    }

    public int getReceptiveFieldSize() {
        return proximalSegment.getReceptieveFieldSize();
    }

    public void stimulate() {
        proximalSegment.stimulate();
    }

    /**
     * Если ткущий прямой вход снизу был предсказан какой-либо из клекто, тогда эти клетки становятся активными.
     * Если этот сегмент стал активным из-за клеток выбранных для обучения, тогда такая клетка также выбирается
     * для обучения. Если же текущий вход не был предсказан, тогда все клетки становятся активными и кроме того,
     * лучше всего подходящая под входные данные клетка вбирается для обучения с добавлением нового латерального
     * сегмента.
     */
    public void updateActiveCells() {
        boolean wasPredicted = false;
        boolean toLearn = false;
        for (Cell cell : cells) {
            if (cell.getStateHistory()[1] == Cell.State.predictive) {
                DistalSegment s = cell.getMostActiveSegment(false, 1);
                if (s.isSequenceSegment()) {
                    wasPredicted = true;
                    cell.getStateHistory()[0] = Cell.State.active;
                    if (s.isActiveInState(true, 1)) {
                        toLearn = true;
                        cell.getLearnHistory()[0] = true;
                    }
                }
            }
        }
        if (!wasPredicted) {
            for (Cell cell : cells) {
                cell.getStateHistory()[0] = Cell.State.active;
            }
        }
        if (!toLearn) {
            Cell bestCell = getBestMatchingCell(1);
            bestCell.getLearnHistory()[0] = true;
            SegmentUpdate sUpdate = createSegmentUpdate(otherCells, null, 1, true);
            sUpdate.isSequenceSegment = true;
            addToUpdate(bestCell, sUpdate);
        }
    }

    /**
     * Клетка включает свое состояние предчувствия, если любой из ее латеральных сегментов становится активным.
     * В этом случае проводятся следующие изменения: а) усиление активных сейчас латеральных сегментов и б)
     * усиление сегментов, которые могли бы предсказать данную активацию.
     */
    public void updatePredictiveCells() {
        for (Cell cell : cells) {
            boolean toUpdate = false;
            for (DistalSegment s : cell.getDistalSegments()) {
                if (s.isActiveInState(false, 0)) {
                    cell.getStateHistory()[0] = Cell.State.predictive;
                    SegmentUpdate sUpdate = createSegmentUpdate(otherCells, s, 0, false);
                    addToUpdate(cell, sUpdate);
                    toUpdate = true;
                }
            }
            if (toUpdate) {
                DistalSegment predS = cell.getBestMatchingSegment(1);
                SegmentUpdate predSUpdate = createSegmentUpdate(otherCells, predS, 1, true);
                addToUpdate(cell, predSUpdate);
            }
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
            if (cell.getLearnHistory()[0]) {
                adaptSegments(toUpdate.get(cell.getIndex()), true);
                toUpdate.remove(cell.getIndex());
            } else if (cell.getStateHistory()[0] != Cell.State.predictive && cell.getStateHistory()[1] == Cell.State.predictive) {
                adaptSegments(toUpdate.get(cell.getIndex()), false);
                toUpdate.remove(cell.getIndex());
            }
        }
    }

    /**
     * Усиление каждого сегмента. Если reinforcement==true, то синапсы из списка на обновление увеличивают значения
     * своих перманентностей. Все остальные синапсы уменьшают свои перманентности. В противном случае синапсы на
     * обновление уменьшают свои перманентности. Для новых синапсов назначается перманентность, равная initialPerm.
     *
     * @param segmentUpdates
     * @param reinforcement
     */
    private void adaptSegments(List<SegmentUpdate> segmentUpdates, boolean reinforcement) {
        for (SegmentUpdate su : segmentUpdates) {
            su.segment.updateSynapses(su.synapses, reinforcement);
        }
        for (Cell cell : cells) {
            cell.updateHistory(otherCells);
        }
    }

    /**
     * Создание списка изменений для сегмента s. Если s != null, то на оьноваление выставляются активные синапсы
     * у исходны клеток коnторых было активное состояние в прердыдущий (historyLevel = 1) или в текущий момент времени.
     * Если addNewSynapses==true, то добавляются новые синапсы до максимального числа, равного newSynapseCount.
     * Такие синапсы случайно выбираются из числа клеток, которые были назначены для обучения в текущий момент времени
     * или в предыдущий (historyLevel = 1).
     *
     * @param cells
     * @param s
     * @param historyLevel
     * @param addNewSynapses
     * @return
     */
    private SegmentUpdate createSegmentUpdate(Map<Integer, Cell> cells, DistalSegment s, int historyLevel, boolean addNewSynapses) {
        final SegmentUpdate su = new SegmentUpdate();
        if (s != null) {
            su.segment = s;
            for (Synapse synapse : s.getActiveSynapses(historyLevel)) {
                su.synapses.add(synapse.getInputSource());
            }
        } else {
            su.segment = new DistalSegment();
        }
        if (addNewSynapses) {
            List<Cell> cellsToLearn = new ArrayList<>();
            for (Cell cell : cells.values()) {
                if (cell.getLearnHistory()[historyLevel])
                    cellsToLearn.add(cell);
            }
            for (int i = 0; i < newSynapsesCount - su.synapses.size(); i++) {
                int index = (int) (cellsToLearn.size() * Math.random());
                Synapse synapse = new Synapse(cellsToLearn.get(index).getIndex(), initialPerm);
                su.synapses.add(cellsToLearn.get(index).getIndex());
                su.segment.addSynapse(synapse);
            }
        }
        return su;
    }

    /**
     * Возвращается клетка с самым соответсвующим входу сегментом. Если такой клетки нет, то возвращается клетка с
     * минимальным количеством сегментов.
     *
     * @return
     */
    private Cell getBestMatchingCell(int historyLevel) {
        Cell bestMatching = null;
        Cell withMinSegments = null;
        DistalSegment bestSegment = null;
        for (Cell cell : cells) {
            DistalSegment s = cell.getBestMatchingSegment(historyLevel);
            if (s != null && (bestSegment == null || bestSegment.countConnected(historyLevel) < s.countConnected(historyLevel))) {
                bestSegment = s;
                bestMatching = cell;
            }
            if (withMinSegments == null || (withMinSegments.getDistalSegments().size() > cell.getDistalSegments().size())) {
                withMinSegments = cell;
            }
        }
        return bestMatching != null ? bestMatching : withMinSegments;
    }

    public int getIndex() {
        return index;
    }

    public int getOverlap() {
        return proximalSegment.getOverlap();
    }

    public int getInhibitionRadius() {
        return inhibitionRadius;
    }

    public void setInhibitionRadius(int inhibitionRadius) {
        this.inhibitionRadius = inhibitionRadius;
    }

    public int[] getCoords() {
        return coords;
    }

    public ProximalSegment getProximalSegment() {
        return proximalSegment;
    }

    public void setOtherCells(Map<Integer, Cell> otherCells) {
        this.otherCells = otherCells;
    }

    public Cell[] getCells() {
        return cells;
    }

    public void setNeighbors(int[] neighbors) {
        this.neighbors = neighbors;
    }

    public int[] getNeighbors() {
        return neighbors;
    }

    public double getActiveDutyCycle() {
        return activeDutyCycle;
    }

    public double getOverlapDutyCycle() {
        return overlapDutyCycle;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    private class SegmentUpdate {
        DistalSegment segment = null;
        List<Integer> synapses = new ArrayList<>();
        boolean isSequenceSegment = false;
    }
}
