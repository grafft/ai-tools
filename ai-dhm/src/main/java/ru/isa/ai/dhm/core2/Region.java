package ru.isa.ai.dhm.core2;

import cern.colt.function.tint.IntProcedure;
import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tint.IntMatrix1D;
import cern.colt.matrix.tint.impl.DenseIntMatrix1D;
import com.google.common.primitives.Ints;
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
public class Region {
    /**
     * The period used to calculate duty cycles.
     * Higher values make it take longer to respond to changes in
     * boost. Shorter values make it potentially more unstable and
     * likely to oscillate.
     */
    private int dutyCyclePeriod = 1000;

    private int desiredLocalActivity;
    private List<Region> childRegions = new ArrayList<>();
    private List<Region> parentRegions = new ArrayList<>();
    private Map<Integer, Column> columns = new HashMap<>();
    private Map<Integer, Cell> allCells = new HashMap<>();
    private BitVector activeColumns;
    private IntMatrix1D overlaps;

    private int numColumns;
    private int xInput;
    private int yInput;
    private int xDimension;
    private int yDimension;

    private int iterationNum = 0;

    public Region(RegionSettings settings) {
        this.xDimension = settings.xDimension;
        this.yDimension = settings.yDimension;
        this.xInput = settings.xInput;
        this.yInput = settings.yInput;
        this.desiredLocalActivity = settings.desiredLocalActivity;

        for (int i = 0; i < settings.xDimension; i++) {
            for (int j = 0; j < settings.yDimension; j++) {
                Column column = new Column(i * yDimension + j, new int[]{i, j}, settings);
                List<Integer> neighbors = new ArrayList<>();
                for (int k = column.getCoords()[0] - column.getInhibitionRadius(); k < column.getCoords()[0] + column.getInhibitionRadius(); k++) {
                    if (k >= 0 && k < xDimension) {
                        for (int m = column.getCoords()[1] - column.getInhibitionRadius(); m < column.getCoords()[1] + column.getInhibitionRadius(); m++) {
                            if (m >= 0 && m < yDimension) {
                                neighbors.add(k * yDimension + m);
                            }
                        }
                    }
                }
                column.setNeighbors(Ints.toArray(neighbors));
                columns.put(column.getIndex(), column);
                for (Cell cell : column.getCells()) {
                    allCells.put(cell.getIndex(), cell);
                }
            }
        }
        for (Column column : columns.values()) {
            column.setOtherCells(allCells);
        }
        activeColumns = new BitVector(numColumns);
        overlaps = new DenseIntMatrix1D(numColumns);
    }

    /**
     * Иниуиализация региона, для каждой колнки создается начальный список потенцильаных синапсов
     */
    public void initialization() {
        for (Column column : columns.values()) {
            int inputCenterX = (column.getCoords()[0] + 1) * (xInput / (xDimension + 1));
            int inputCenterY = (column.getCoords()[1] + 1) * (yInput / (yDimension + 1));
            column.initialization(inputCenterX, inputCenterY);
        }
    }

    /**
     * Обработка входного сигнала
     *
     * @param input
     * @return
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
     * @param input
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
            IntMatrix1D neighborOverlaps = overlaps.viewSelection(column.getNeighbors());
            double minLocalOverlap = MathUtils.kthScore(neighborOverlaps, desiredLocalActivity);
            if (column.getOverlap() > 0 && column.getOverlap() >= minLocalOverlap) {
                column.setActive(true);
                activeColumns.set(column.getIndex());
            } else {
                column.setActive(false);
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
        activeColumns.forEachIndexFromToInState(0, activeColumns.size() - 1, true, new IntProcedure() {
            @Override
            public boolean apply(int element) {
                columns.get(element).learning(input);
                return true;
            }
        });
        int period = dutyCyclePeriod > iterationNum ? iterationNum : dutyCyclePeriod;
        for (Column column : columns.values()) {
            double maxActiveDuty = 0;
            for (int index : column.getNeighbors()) {
                double activity = columns.get(index).getActiveDutyCycle();
                maxActiveDuty = maxActiveDuty > activity ? maxActiveDuty : activity;
            }
            double minDutyCycle = 0.01 * maxActiveDuty;

            column.updateActiveDutyCycle(period);
            column.updateBoostFactor(minDutyCycle);

            column.updateOverlapDutyCycle(period);
            if (column.getOverlapDutyCycle() < minDutyCycle)
                column.stimulate();

            column.setInhibitionRadius(averageReceptiveFieldSize());
        }
    }

    /**
     * Средний радиус подключенных рецептивных полей всех колонок
     * @return
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
    }

    public Map<Integer, Column> getColumns() {
        return columns;
    }

    public BitVector getActiveColumns() {
        return activeColumns;
    }
}
