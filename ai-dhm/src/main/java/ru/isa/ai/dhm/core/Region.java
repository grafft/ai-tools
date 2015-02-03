package ru.isa.ai.dhm.core;

import cern.colt.function.tint.IntProcedure;
import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tint.IntMatrix1D;
import cern.colt.matrix.tint.impl.DenseIntMatrix1D;
import com.google.common.primitives.Ints;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.util.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Region {

    private DHMSettings settings;
    private Region parent = null;
    private List<Region> childRegions = new ArrayList<>();
    private Map<Integer, Column> columns = new HashMap<>(); // можно и в виде массива
    private BitVector activeColumns;  // для оптимизации
    private IntMatrix1D overlaps;
    private int id = 0;

    private int iterationNum = 0;

    public Region(int ID, DHMSettings settings) {
        this.settings = settings;
        this.id = ID;

        Map<Integer, Cell> allCells = new HashMap<>();
        for (int i = 0; i < settings.xDimension; i++) {
            for (int j = 0; j < settings.yDimension; j++) {
                Column column = new Column(i * settings.yDimension + j, new int[]{i, j}, settings);
                columns.put(column.getIndex(), column);
                for (Cell cell : column.getCells()) {
                    allCells.put(cell.getIndex(), cell);
                }
            }
        }
        for (Column column : columns.values()) {
            column.setOtherCells(allCells);
        }
        activeColumns = new BitVector(settings.xDimension * settings.yDimension);
        overlaps = new DenseIntMatrix1D(settings.xDimension * settings.yDimension);
    }

    /**
     * @return Возвращает 3 числа: W,H колонок, N клеток у каждой колонки
     */
    public int[] getDimensions()
    {
        return new int[]{settings.xDimension, settings.yDimension,settings.cellsPerColumn};
    }

    /**
     * Инициализация региона, для каждой колнки создается начальный список потенцильаных синапсов
     */
    public void initialization() {
        int inputW = settings.xInput-2;// ширина входного массива данных за исключением границ
        inputW = inputW <= 0 ? 1:inputW;
        int inputH = settings.yInput-2;
        inputH = inputH <= 0 ? 1:inputH;

        for (Column column : columns.values()) {
           // int inputCenterX = inputW ==1? 1 :(int) Math.ceil(((double)(column.getCoords()[0] ))/(settings.xDimension/ inputW))+1;
           // int inputCenterY = inputH ==1? 1 :(int) Math.ceil(((double)(column.getCoords()[1] ))/(settings.yDimension/ inputH))+1;

            //  floor(i*N/2) + 2^(i-1)
            int inputCenterX = inputW ==1? 1 :(int) Math.ceil(((double)(column.getCoords()[0] ))/(settings.xDimension/ inputW))+1;
            int inputCenterY = inputH ==1? 1 :(int) Math.ceil(((double)(column.getCoords()[1] ))/(settings.yDimension/ inputH))+1;

            column.initialization(inputCenterX, inputCenterY);
        }
    }

    /**
     * Обработка входного сигнала
     *
     * @param input - выходной сигнал
     * @return индексы активных колонки
     */
    public BitVector forwardInputProcessing(BitVector input) {
        iterationNum++;
        overlapPhase(input);

        inhibitionPhase();
        learningPhase(input);

        return activeColumns;
    }

    /**
     * Вычисление значения перекрытия каждой колонки с заданным входным вектором.
     *
     * @param input - входной сигнал
     */
    private void overlapPhase(BitVector input) {
        for (Column column : columns.values()) {
            int overlap = column.overlapCalculating(input);
            overlaps.setQuick(column.getIndex(), overlap);
        }
    }

    /**
     * Вычисление колонок, остающихся победителями после применения взаимного подавления.
     */
    private void inhibitionPhase() {
        for (Column column : columns.values()) {
            if (column.getNeighbors().size() > 0) {
                // выборка перекрытий колонок, соседних с данной
                IntMatrix1D neighborOverlaps = overlaps.viewSelection(Ints.toArray(column.getNeighbors()));
                // определить порог перекрытия
                double minLocalOverlap = MathUtils.kthScore(neighborOverlaps, settings.desiredLocalActivity);
                // если колонка имеет перекрытие большее, чем у соседей, то она становиться активной
                if (column.getOverlap() > 0 && column.getOverlap() >= minLocalOverlap) {
                    column.setActive(true);
                    activeColumns.set(column.getIndex());
                } else {
                    column.setActive(false);
                    activeColumns.clear(column.getIndex());
                }
            }
        }
    }

    /**
     * Обновление значений перманентности, фактора ускорения и радиуса подавления колонок.
     * Механизм ускорения работает в том случае, если колонка не побеждает достаточно долго (activeDutyCycle).
     * Если колонка плохо перекрывается с входным сигналом достоачно долго (overlapDutyCycle), то увеличиваются
     * перманентности.
     */
    private void learningPhase(final BitVector input) {

        // 1. изменить значения перманентности всех синапсов проксимальных сегментов колонок
        activeColumns.forEachIndexFromToInState(0, activeColumns.size() - 1, true, new IntProcedure() {
            @Override
            public boolean apply(int element) {
                columns.get(element).learning(input);
                return true;
            }
        });

        // определить перешли ли мы черех период
        int period = settings.dutyCyclePeriod > iterationNum ? iterationNum : settings.dutyCyclePeriod;


        for (Column column : columns.values()) {
            // определить колонку с максимальным числом срабатываний и само это число
            double maxActiveDuty = 0;
            for (int index : column.getNeighbors()) {
                    double activity = columns.get(index).getActiveDutyCycle();
                    maxActiveDuty = maxActiveDuty > activity ? maxActiveDuty : activity;
            }
            // определить минимальное число срабатываний
            double minDutyCycle = 0.01 * maxActiveDuty;

            column.updateBoostFactor(minDutyCycle);

            column.updateOverlapDutyCycle(period);
            // если колонка редко срабатывает стимулировать её
            if (column.getOverlapDutyCycle() < minDutyCycle)
                column.stimulate();

            // TODO P: почему есть зависимость -  inhibitionRadius от  averageReceptiveFieldSize ??
            // обновить соседей изсходя из нового рецептивного поля колонки
            column.updateNeighbors(averageReceptiveFieldSize());
        }

        // теперь обновим activeDutyCycle всех колонок.
        for (Column column: columns.values())
            column.updateActiveDutyCycle(period);
    }

    /**
     * Средний радиус подключенных рецептивных полей всех колонок
     *
     * @return средний радиус по всем клонкам
     */
    private int averageReceptiveFieldSize() {
        int sum = 0;
        for (final Column column : columns.values()) {
            sum += column.getReceptiveFieldSize();
        }

        return sum / columns.size();
    }

    /**
     * Вычисляются активные состояния для каждой клетки из победивших колонок.
     */
    public void updateActiveCells() {
        activeColumns.forEachIndexFromToInState(0, activeColumns.size() - 1, true, new IntProcedure() {
            @Override
            public boolean apply(int element) {
                columns.get(element).updateActiveCells();
                return true;
            }
        });
    }

    /**
     * Вычисляются состояния предсказания для каждой клетки.
     */
    public void updatePredictiveCells() {
        for (Column column : columns.values()) {
            column.updatePredictiveCells();
        }
    }

    public void updateRelations() {
        for (Column column : columns.values()) {
            column.predictiveLearning();
        }
    }

    public void addChild(Region child) {
        childRegions.add(child);
        child.parent = this;
    }

    public Region getParent(){
        return this.parent;
    }

    public List<Region> getChildRegions() {
        return childRegions;
    }

    public Map<Integer, Column> getColumns() {
        return columns;
    }

    public BitVector getActiveColumns() {
        return activeColumns;
    }

    public int getID() {return this.id; }

}
