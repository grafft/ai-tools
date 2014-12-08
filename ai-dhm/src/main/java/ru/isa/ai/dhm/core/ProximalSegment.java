package ru.isa.ai.dhm.core;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.DHMSettings;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 29.08.2014
 * Time: 10:30
 */
/*Проксимальный дендритный сегмент (один для колонки)*/
public class ProximalSegment {
    private DHMSettings settings;

    private int overlap; // вычисленное перекрытие данного дендрита (=колонки)
    private double boostFactor=1.0; // вычисленный фактор ускорения дендрита (=колонки)
    // Integer - индекс колонки на нижнем слое, с которой потенциально может быть связан данный проксимальный дендрит с помошью синапса Synapse
    private Map<Integer, Synapse> potentialSynapses = new HashMap<>();


    private Random random = new Random();

    public ProximalSegment(DHMSettings settings) {
        this.settings = settings;
    }

    // TODO AP: реализовать уменьшение перманентности при удалении от геометрического центра подключенных входов
    /**
     * Создание начального списка потенциальных синапсов, состоящего из случайного множества входных битов,
     * выбранных из пространства входных данных. Каждый входной бит предтсавлен синапсом с некоторым случайным
     * значением перманентности.
     *
     * @param xCenter - центр рецептивного поля
     * @param yCenter - центр рецептивного поля
     */
    public void initSynapses(int xCenter, int yCenter) {
        // индексы колонок из прямоугольника на нижнем слое вокруг проекции
        // данной колонки с верхнего слоя на нижний (которой принадлежит данный проксимальный сегмент)
        List<Integer> indices = new ArrayList<>();
        for (int i = xCenter - settings.potentialRadius; i <= xCenter + settings.potentialRadius; i++) {
            if (i >= 0 && i < settings.xInput) {
                for (int j = yCenter - settings.potentialRadius; j <= yCenter + settings.potentialRadius; j++) {
                    if (j >= 0 && j < settings.yInput)
                        indices.add(i * settings.yInput + j);
                }
            }
        }

        if(settings.debug==false)
            Collections.shuffle(indices, random);

        int numPotential = (int) Math.round(indices.size() * settings.connectedPct);
        for (int i = 0; i < numPotential; i++) {
            int index = indices.get(i);
            Synapse synapse = new Synapse(settings, index);
            synapse.initPermanence();
            // TODO P: почему только часть синапсов (а не все) попадают в potentialSynapses (а остальные получается баластом будут всю работу HTM лежать)
            potentialSynapses.put(index, synapse);
        }
    }

    /**
     * Перекрытие - это число действующих синапсов, подключенных к активным входным битам, умноженное на
     * фактор ускорения. Если полученное значение меньше minOverlap, то перекрытие устанавливается в 0.
     *
     * @param input - входной сигнал
     * @return значение перекрытия
     */
    public int overlapCalculating(BitVector input) {
        overlap = 0;
        List<Integer> cs=connectedSynapses();
        for (int key : cs)
            overlap += input.get(key) ? 1 : 0;

        overlap *= overlap < settings.minOverlap ? 0 : boostFactor;
        return overlap;
    }

    //Подмножество потенциальных синапсов potentialSynapses(c) у которых значение перманентности больше чем connectedPerm.
    public List<Integer> connectedSynapses()
    {
        List<Integer> indices = new ArrayList<>();
        for (Synapse s : potentialSynapses.values())
        {
            if(s.getPermanence()>settings.connectedPerm)
                indices.add(s.getInputSource());
        }

        return indices;
    }

    /**
     * Есил синапс был активен (через него шел сигнал от входного вектора), его значение преманентности увеличивается,
     * а иначе - уменьшается.
     *
     * @param input - входной сигнал
     */
    public void updateSynapses(BitVector input) {
        for (Synapse synapse : potentialSynapses.values()) {
            if (input.get(synapse.getInputSource()))
                synapse.increasePermanence();
            else
                synapse.decreasePermanence();
        }
    }

    public void stimulate() {
        for (Synapse synapse : potentialSynapses.values()) {
            synapse.stimulatePermanence();
        }
    }

    /**
     * Размер подключенного рейептивного поля определяется только по подключенным синаписам.
     */
    public int getReceptieveFieldSize() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = -1;
        int maxY = -1;
        for (Synapse synapse : potentialSynapses.values()) {
            if (synapse.isConnected()) {
                int x = synapse.getInputSource() / settings.yInput;
                int y = synapse.getInputSource() - x;
                if (x > maxX)
                    maxX = x;
                if (x < minX)
                    minX = x;
                if (y > maxY)
                    maxY = y;
                if (y < minY)
                    minY = y;

            }
        }
        int radiusX = (maxX - minX) / 2;
        int radiusY = (maxY - minY) / 2;
        return radiusX > radiusY ? radiusX : radiusY;
    }

    public int getOverlap() {
        return overlap;
    }

    public void setBoostFactor(double val) {
        boostFactor = val;
    }
    public double getBoostFactor() {return boostFactor; }
}
