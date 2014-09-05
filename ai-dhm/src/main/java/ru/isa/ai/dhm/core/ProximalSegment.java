package ru.isa.ai.dhm.core;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.DHMSettings;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 29.08.2014
 * Time: 10:30
 */
public class ProximalSegment {
    private DHMSettings settings;

    private int overlap;
    private double boostFactor;
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
        List<Integer> indices = new ArrayList<>();
        for (int i = xCenter - settings.potentialRadius; i <= xCenter + settings.potentialRadius; i++) {
            if (i >= 0 && i < settings.xInput) {
                for (int j = yCenter - settings.potentialRadius; j <= yCenter + settings.potentialRadius; j++) {
                    if (j >= 0 && j < settings.yInput)
                        indices.add(i * settings.yInput + j);
                }
            }
        }

        Collections.shuffle(indices, random);
        int numPotential = (int) Math.round(indices.size() * settings.connectedPct);
        for (int i = 0; i < numPotential; i++) {
            int index = indices.get(i);
            Synapse synapse = new Synapse(settings, index);
            synapse.initPermanence();
            potentialSynapses.put(index, synapse);
        }
    }

    /**
     * Перекрытие - это просто число действующих синапсов подключенных к активным входным битам, умноженное на
     * фактор ускорения. Если полученное значение мньше minOverlap, то перекрытие устанавливается в 0.
     *
     * @param input - входной сигнал
     * @return значение перекрытия
     */
    public int overlapCalculating(BitVector input) {
        overlap = 0;
        for (int key : potentialSynapses.keySet())
            overlap += input.get(key) ? 1 : 0;

        overlap *= overlap < settings.minOverlap ? 0 : boostFactor;
        return overlap;
    }

    /**
     * Есил синапс был активен (через него шел сигнал от входного вектора), его значение преманентности увеличивается,
     * а иначе - уменьшается.
     *
     * @param input - входнйо сигнал
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
}
