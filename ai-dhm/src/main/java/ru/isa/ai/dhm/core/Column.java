package ru.isa.ai.dhm.core;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.DHMSettings;

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
    private DHMSettings settings;

    private int index; // линеаризированные координаты колонки в слое
    private int[] coords; // координаты колотки в слое X,Y
    private boolean isActive; // TODO: в Region уже есть Bitvector activeColumns

    private ProximalSegment proximalSegment;
    private Map<Integer, Cell> otherCells; // все клетки слоя

    private Cell[] cells; // клетки данной колонки
    private Map<Integer, List<SegmentUpdate>> toUpdate = new HashMap<>();
    private List<Integer> neighbors = new ArrayList<>();  // колонки, находящиеся в радиусе подавления данной

    // DutyCycle - рабочий цикл = последовательность итераций в течение которых колонка находилась в возбужденном состоянии?
    private int activeDutyCycle = 0;
    private int overlapDutyCycle = 0;

    public Column(int index, int[] coords, DHMSettings settings) {
        this.settings = settings;
        this.index = index;
        this.coords = coords;
        cells = new Cell[settings.cellsPerColumn];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Cell(index * settings.cellsPerColumn + i, settings.minThreshold,settings.historyDeep);
        }
        proximalSegment = new ProximalSegment(settings);
    }

    /**
     * Инициализация колнки - создание начального списка потенциальных синапсов
     *
     * @param inputCenterX - центр рецептивного поля
     * @param inputCenterY - центр рецептивного поля
     */
    public void initialization(int inputCenterX, int inputCenterY) {
        proximalSegment.initSynapses(inputCenterX, inputCenterY);
        updateNeighbors(settings.initialInhibitionRadius);
    }

    /**
     * Изменение списка соседних колонок, которые отстоят от данной в круге радиусом inhibitionRadius
     * @param inhibitionRadius радиус подавления (в начале назначется из настроек, потом берется как усредненный радиус рецептивного поля)
     */
    public void updateNeighbors(int inhibitionRadius) {
        neighbors.clear();
        for (int k = getCoords()[0] - inhibitionRadius; k <= (getCoords()[0] + inhibitionRadius); k++) {
            if (k >= 0 && k < settings.xDimension) {
                for (int m = getCoords()[1] - inhibitionRadius; m <= (getCoords()[1] + inhibitionRadius); m++) {
                    if (m >= 0 && m < settings.yDimension) {
                       if((k * settings.yDimension + m)!=index)
                            neighbors.add(k * settings.yDimension + m);
                    }
                }
            }
        }
    }

    public int overlapCalculating(BitVector input) {
        return proximalSegment.overlapCalculating(input);
    }

    public void learning(BitVector input) {
        proximalSegment.updateSynapses(input);
    }

    //  обновление счетчиков рабочих циклов перекрытия и активного
    public void updateOverlapDutyCycle(int period) {
        overlapDutyCycle = (overlapDutyCycle * (period - 1) + (getOverlap()) > settings.minOverlap ? 1 : 0) / period;
    }

    public void updateActiveDutyCycle(int period) {
        activeDutyCycle = (activeDutyCycle * (period - 1) + (isActive ? 1 : 0)) / period;
    }

    /**
     * Если activeDutyCycle больше minValue, то значение ускорения равно 1. Ускорение начинает линейно увеличиваться
     * как только activeDutyCycle колонки падает ниже minDutyCycle.
     *
     * @param minValue - минимальнео число активных циклов
     */
    public void updateBoostFactor(double minValue) {
        double value = 1;
        if (activeDutyCycle < minValue)
            value = 1 + (minValue - activeDutyCycle) * (settings.maxBoost - 1) / minValue;
        proximalSegment.setBoostFactor(value);
    }

    public int getReceptiveFieldSize() {
        return proximalSegment.getReceptieveFieldSize();
    }

    public void stimulate() {
        proximalSegment.stimulate();
    }

    /**
     * 1) Если текущий прямой вход снизу был предсказан какой-либо из клеток (т.е. ее параметр predictiveState
     был равен 1 благодаря ее сегменту последовательностей (латеральному), на предыдущем временном шаге),
     тогда эти клетки становятся активными.
       2) Если этот сегмент стал активным из-за клеток выбранных для обучения, тогда такая клетка также выбирается
     * для обучения.
     * 3) Если же текущий вход не был предсказан (т.е. в ней не было клеток с predictiveState равным 1), тогда все клетки
     * становятся активными и кроме того, лучше всего подходящая под входные данные клетка вбирается для обучения
     * с добавлением нового латерального сегмента.
     */
    public void updateActiveCells() {
        boolean wasPredicted = false; // активность всей колонки была предсказана ранее
        boolean toLearn = false; // колонка в состоянии обучения
        for (Cell cell : cells) {
            if (cell.getStateHistory()[1] == Cell.State.predictive) {   // если клетка была в состоянии предсказания
                // выберем латеральный сегмент, у которого шаг назад было наибольшее количество синапсов к клеткам в активном состоянии
                LateralSegment s = cell.getMostActiveSegment(false, 1); // TODO: избыточно - возвращает лишние сегменты (активные сегменты, которые isSequenceSegment()==false)
                if (s.isSequenceSegment()) { // сегмент, который предсказал ситуацию ровно на шаг назад
                    wasPredicted = true;
                    // предсказание клетки подтвердилось! (шаг назад была predictive, а стала на этом шаге active)
                    cell.getStateHistory()[0] = Cell.State.active;
                    if (s.isActiveInState(true, 1)) { // было ли шаг назад количество синапсов к клеткам в состоянии обучения выше порога
                        toLearn = true; // обучение колонки нужно продолжить
                        cell.getLearnHistory()[0] = true; // обучение клетки нужно продолжить
                    }
                }
            }
        }
        if (!wasPredicted) {  // текущий вход не был предсказан
            for (Cell cell : cells) {
                cell.getStateHistory()[0] = Cell.State.active;
            }
        }
        if (!toLearn) {  // нужно выбрать клетку для обучения
            // выбираем клетку с латеральным сегментом таким, что шаг назад у него было больше всего синапсов
            Cell bestCell = getBestMatchingCell(1);
            bestCell.getLearnHistory()[0] = true;
            //SegmentUpdate sUpdate = createSegmentUpdate(bestCell,otherCells, bestCell.getBestMatchingSegment(1), 1, true);
            SegmentUpdate sUpdate = createSegmentUpdate(bestCell,otherCells, null, 1, true);
            sUpdate.isSequenceSegment = true;
            addToUpdate(sUpdate);
        }
    }

    /**
     * Клетка включает свое состояние предсказания, если любой из ее латеральных сегментов становится активным.
     * В этом случае проводятся следующие изменения: а) усиление активных сейчас латеральных сегментов и б)
     * усиление сегментов, которые могли бы предсказать данную активацию.
     */
    public void updatePredictiveCells() {
        for (Cell cell : cells) {
            boolean toUpdate = false;
            for (LateralSegment s : cell.getLateralSegments()) {
                if (s.isActiveInState(false, 0)) { // сегмент с количеством синапсов к клеткам в активном состоянии выше порога
                    cell.getStateHistory()[0] = Cell.State.predictive;
                    SegmentUpdate sUpdate = createSegmentUpdate(cell,otherCells, s, 0, false);
                    addToUpdate(sUpdate);
                    toUpdate = true;
                }
            }
            if (toUpdate) {
                LateralSegment predS = cell.getBestMatchingSegment(1);
                SegmentUpdate predSUpdate = createSegmentUpdate(cell, otherCells, predS, 1, true);
                addToUpdate(predSUpdate);
            }
        }
    }

    private void addToUpdate(SegmentUpdate predSUpdate) {
        List<SegmentUpdate> list = toUpdate.get(predSUpdate.cell.getIndex());
        if (list == null) {
            list = new ArrayList<>();
            toUpdate.put(predSUpdate.cell.getIndex(), list);
        }
        list.add(predSUpdate);
    }

    // применение обновлений дистальных сегментов
    public void predictiveLearning() {
       if(toUpdate.size()>0) {
           for (Cell cell : cells) {
               // клетка в состоянии обучения
               if (cell.getLearnHistory()[0]) {
                   adaptSegments(toUpdate.get(cell.getIndex()), true);
                   toUpdate.remove(cell.getIndex());
               } else
                   // клетка перестала предсказывать
                   if (cell.getStateHistory()[0] != Cell.State.predictive && cell.getStateHistory()[1] == Cell.State.predictive) {
                       adaptSegments(toUpdate.get(cell.getIndex()), false);
                       toUpdate.remove(cell.getIndex());
                   }
           }
       }
    }

    /**
     * Усиление каждого сегмента. Если reinforcement==true, то синапсы из списка на обновление увеличивают значения
     * своих перманентностей. Все остальные синапсы уменьшают свои перманентности. В противном случае синапсы на
     * обновление уменьшают свои перманентности. Для новых синапсов назначается перманентность, равная initialPerm.
     *
     * @param segmentUpdates - список изменений
     * @param reinforcement  - тип обучения
     */
    private void adaptSegments(List<SegmentUpdate> segmentUpdates, boolean reinforcement) {
        for (SegmentUpdate su : segmentUpdates) {
            su.segment.updateSynapses(su.synapses, reinforcement);
            su.segment.setSequenceSegment(su.isSequenceSegment);
        }
        /*for (Cell cell : cells) {
            cell.updateHistory(otherCells);
        }*/
    }

    public void updateHistory()
    {
        for (Cell cell : cells) {
            cell.updateHistory(otherCells);
        }
    }

    /**
     * Создание списка изменений для сегмента s. Если s != null, то на обноваление выставляются активные синапсы
     * у исходны клеток которых было активное состояние в прердыдущий (historyLevel = 1) или в текущий момент времени.
     * Если addNewSynapses==true, то добавляются новые синапсы до максимального числа, равного newSynapseCount.
     * Такие синапсы случайно выбираются из числа клеток, которые были назначены для обучения в текущий момент времени
     * или в предыдущий (historyLevel = 1).
     *
     * @param otherCells          - все окружающие клетки
     * @param s              - сегмент на обновление (null ели новый)
     * @param historyLevel   - время
     * @param addNewSynapses - добавлять ли новые синапсы
     * @return - список изменений
     */
    private SegmentUpdate createSegmentUpdate(Cell updatingCell, Map<Integer, Cell> otherCells, LateralSegment s, int historyLevel, boolean addNewSynapses) {
        final SegmentUpdate su = new SegmentUpdate();
        su.cell=updatingCell;
        if (s != null) {
            su.segment = s;
            for (Synapse synapse : s.getActiveSynapses(historyLevel)) {
                su.synapses.add(synapse.getInputSource());
            }
        } else {
            su.segment = new LateralSegment(settings.activationThreshold);
            su.cell.getLateralSegments().add(su.segment);
        }
        if (addNewSynapses) {
            // клетки которые находятся в состоянии обучения
            List<Cell> cellsToLearn = new ArrayList<>();
            for (Cell cell : otherCells.values()) {
                if (cell.getLearnHistory()[historyLevel])
                    cellsToLearn.add(cell);
            }

            // к некоторым клеткам, находящимся в состоянии обучения, добавляем новые синапсы
            if(cellsToLearn.size()>0) {
                for (int i = 0; i < settings.newSynapseCount - su.synapses.size(); i++) {
                    int index;
                    if(settings.debug) {
                        index = (int) (cellsToLearn.size() / 2 + i);
                        index=index<cellsToLearn.size() ? index:cellsToLearn.size()/2;
                    }
                    else index=(int) (cellsToLearn.size() * Math.random());

                    Synapse synapse = new Synapse(settings, cellsToLearn.get(index).getIndex(), settings.initialPerm);
                    su.synapses.add(cellsToLearn.get(index).getIndex());
                    su.segment.addSynapse(synapse);
                }
            }
        }
        return su;
    }

    /**
     * Возвращается клетка с самым соответсвующим ситуации сегментом. Если такой клетки нет, то возвращается клетка с
     * минимальным количеством сегментов.
     *
     * @return - лучше всего подходящая клетка
     */
    private Cell getBestMatchingCell(int historyLevel) {
        Cell bestMatching = null;
        Cell withMinSegments = null;
        LateralSegment bestSegment = null;
        for (Cell cell : cells) {
            LateralSegment s = cell.getBestMatchingSegment(historyLevel);
            if (s != null && (bestSegment == null || bestSegment.countConnected(historyLevel) < s.countConnected(historyLevel))) {
                bestSegment = s;
                bestMatching = cell;
            }
            if (withMinSegments == null || (withMinSegments.getLateralSegments().size() > cell.getLateralSegments().size())) {
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

    public int[] getCoords() {
        return coords;
    }

    public void setOtherCells(Map<Integer, Cell> otherCells) {
        this.otherCells = otherCells;
    }

    public Cell[] getCells() {
        return cells;
    }

    public List<Integer> getNeighbors() {
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

    public ProximalSegment getProximalSegment() {return proximalSegment; }

    private class SegmentUpdate {
        Cell cell; // cell with updating segment or cell to which new segment will be added
        LateralSegment segment = null;
        List<Integer> synapses = new ArrayList<>();
        /*
         A sequence segment is a segment that predicts bottom-up activation in the very next time step, while
         a non-sequence segment predicts bottom-up activity at a later time step.
         So if the cell was predictive due to a non-sequence segment in the previous time step, and is active
         in the current time step, then that prediction did not "come true" -- the prediction was for activity
         at a later time step instead. Only a prediction in the previous time step that is due to a sequence segment
         would be fulfilled by activation in the current time step, and so only in that case is the predicted cell
         activated rather than having the whole column burst
         ...
         If this is a “sequence segment”, i. e., this is a segment that is predicting the next
            step in the future (we will see that can exist segments pointing to more steps in the past),
            then set the cell as active (activeState(c, i, t) = 1).
         ...
         Deprecated in newer version of HTM
         */
        boolean isSequenceSegment = false;
    }
}
