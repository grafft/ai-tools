package ru.isa.ai.ourhtm.algorithms;

import cern.colt.function.tint.IntProcedure;
import cern.colt.matrix.tint.IntMatrix1D;
import cern.colt.matrix.tint.impl.DenseIntMatrix1D;
import com.google.common.primitives.Ints;
import ru.isa.ai.ourhtm.util.MathUtils;
import ru.isa.ai.ourhtm.structure.Column;
import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.ourhtm.structure.HTMSettings;
import ru.isa.ai.ourhtm.structure.Synapse;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by APetrov on 13.05.2015.
 */
public class SpatialPooler {

    public long seed=10;
    HTMSettings settings;
    private Random random = new Random(seed);
    int[] activeDutyCycles;
    int[] overlapDutyCycles;

    public SpatialPooler(HTMSettings settings)
    {
        this.settings=settings;
        activeDutyCycles=new int[settings.xDimension*settings.yDimension];
        overlapDutyCycles=new int[settings.xDimension*settings.yDimension];

    }

    public int[] getActiveDutyCycles()
    {
        return activeDutyCycles;
    }


    /**
     * Вычисление значения перекрытия каждой колонки с заданным входным вектором.
     *
     * @param input - входной сигнал
     * @param cols - колонки
     * @return значения переключения для каждой колонки
     */
    public int[] updateOverlaps(ArrayList<Column> cols,BitVector input) {
        int[] overlaps=new int[cols.size()];
        int i=0;
        for (Column c : cols) {
            Column.ProximalDendrite pd = c.getProximalDendrite();
            for (Synapse s : pd.getConnectedSynapses()) {
                overlaps[i]=overlaps[i] + (input.get(s.getIndexConnectTo()) ? 1 : 0);
            }
            i++;
        }
        return overlaps;
    }

    /**
     * Вычисление колонок, остающихся победителями после применения взаимного подавления.
     */
    public  ArrayList<Column> inhibitionPhase(ArrayList<Column> cols, int[] overlaps) {
        ArrayList<Column> activeColumns=new ArrayList<>();

        List<Integer> indexies =MathUtils.makeSequence(0,cols.size()-1);
        Collections.shuffle(indexies, random);
        for (int indx : indexies) {
            Column column=cols.get(indx);
            if (column.getNeighbors().size() > 0) {
                IntMatrix1D ov=new DenseIntMatrix1D(overlaps);
                // выборка перекрытий колонок, соседних с данной

                IntMatrix1D neighborOverlaps = ov.viewSelection(Ints.toArray(column.getNeighbors()));
                // определить порог перекрытия
                double minLocalOverlap = MathUtils.kthScore(neighborOverlaps, settings.desiredLocalActivity);
                // если колонка имеет перекрытие большее, чем у соседей, то она становиться активной

                if (overlaps[column.getIndex()] > 0 && overlaps[column.getIndex()] >= minLocalOverlap) {
                    // для случая одинаковых оверлапов у выбраныных соседей
                    int n=0;
                    for(int i:column.getNeighbors())
                        n=n+(findByColIndex(cols,i).isActive()?1:0);
                    if(n<=(settings.desiredLocalActivity-1)) { //-1 - считая саму колонку
                        column.setIsActive(true);
                        activeColumns.add(column);
                    }
                } else {
                    column.setIsActive(false);
                }
            }
        }
        return activeColumns;
    }

    private Column findByColIndex(ArrayList<Column> cols, int index)
    {
        for(Column c:cols)
        {
            if(c.getIndex()==index) return c;
        }
        return null;
    }

    /**
     * Есил синапс был активен (через него шел сигнал от входного вектора), его значение преманентности увеличивается,
     * а иначе - уменьшается.
     *
     * @param input - входной сигнал
     */
    public void updateSynapses(ArrayList<Column> cols, BitVector input) {
        for (Column col : cols) {
            for (Synapse synapse : col.getProximalDendrite().getPotentialSynapses().values()) {
                if (input.get(synapse.getIndexConnectTo()))
                    synapse.increasePermanence();
                else
                    synapse.decreasePermanence();
            }
        }
    }


    private void updateActiveDutyCycle(ArrayList<Column> cols) {

        for(int i=0;i<cols.size();i++)
            activeDutyCycles[i] = activeDutyCycles[i] + (cols.get(i).isActive() ? 1 : 0);
    }

    private void updateOverlapDutyCycle(Column col,int[] overlaps) {
            overlapDutyCycles[col.getIndex()] = overlapDutyCycles[col.getIndex()] + (overlaps[col.getIndex()] > settings.minOverlap ? 1 : 0);
    }

    /**
     * Если activeDutyCycle больше minValue, то значение ускорения равно 1. Ускорение начинает линейно увеличиваться
     * как только activeDutyCycle колонки падает ниже minDutyCycle.
     *
     * @param minValue - минимальнео число активных циклов
     */
    private void updateBoostFactor(Column col, double minValue) {
            double value = 1;

            if (activeDutyCycles[col.getIndex()] < minValue)
                value = 1 + (minValue - activeDutyCycles[col.getIndex()]) * (settings.maxBoost - 1);
            col.getProximalDendrite().setBoostFactor(value);
    }



    /**
     * Обновление значений перманентности, фактора ускорения и радиуса подавления колонок.
     * Механизм ускорения работает в том случае, если колонка не побеждает достаточно долго (activeDutyCycle).
     * Если колонка плохо перекрывается с входным сигналом достоачно долго (overlapDutyCycle), то увеличиваются
     * перманентности.
     */
    public void learningPhase(ArrayList<Column> cols, final BitVector input,final int[] overlaps) {

        // 1. изменить значения перманентности всех синапсов проксимальных сегментов *активных* колонок
        updateSynapses(cols,input);

        for (Column column : cols) {
            // определить максимальное число срабатываний колонки среди соседей колонки и её самой колонку
            double maxActiveDuty = 0;
            for (int index : column.getNeighbors()) {
                maxActiveDuty = maxActiveDuty > activeDutyCycles[index] ? maxActiveDuty : activeDutyCycles[index];
            }
            // определить минимальное число срабатываний (% от maxActiveDuty)
            double minDutyCycle = settings.minDutyCycleFraction * maxActiveDuty;

            updateBoostFactor(column,minDutyCycle);

            updateOverlapDutyCycle(column,overlaps);
            // если колонка редко срабатывает стимулировать её
            if (overlapDutyCycles[column.getIndex()] < minDutyCycle)
                column.getProximalDendrite().stimulate();

            // TODO: в оригинальной реализиации радиус менялся и соседи тоже...
            //column.updateNeighbors(averageReceptiveFieldSize());

        }

        // теперь обновим activeDutyCycle всех колонок.
        updateActiveDutyCycle(cols);
    }


}
