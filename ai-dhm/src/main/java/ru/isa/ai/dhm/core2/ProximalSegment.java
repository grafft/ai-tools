package ru.isa.ai.dhm.core2;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.RegionSettings;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 29.08.2014
 * Time: 10:30
 */
public class ProximalSegment {
    private Map<Integer, Synapse> potentialSynapses = new HashMap<>();

    private int xInput;
    private int yInput;
    private Random random = new Random();

    /**
     * This parameter deteremines the extent of the
     * input that each column can potentially be connected to. This
     * can be thought of as the input bits that are visible to each
     * column, or a 'receptive field' of the field of vision. A large
     * enough value will result in global coverage, meaning
     * that each column can potentially be connected to every input
     * bit. This parameter defines a square (or hyper square) area: a
     * column will have a max square potential pool with sides of
     * length (2 * potentialRadius + 1).
     */
    private int potentialRadius = 16;
    /**
     * The percent of the inputs, within a column's
     * potential radius, that a column can be connected to. If set to
     * 1, the column will be connected to every input within its
     * potential radius. This parameter is used to give each column a
     * unique potential pool when a large potentialRadius causes
     * overlap between the columns. At initialization time we choose
     * ((2*potentialRadius + 1)^(# inputDimensions) * connectedPct)
     * input bits to comprise the column's potential pool.
     */
    private double connectedPct = 0.5;
    /**
     * This is a number specifying the minimum
     * number of potentialSynapses that must be active in order for a column to
     * turn ON. The purpose of this is to prevent noisy input from
     * activating columns.
     */
    private long stimulusThreshold = 0;
    private int minOverlap;

    private int overlap;
    private double boostFactor;

    public ProximalSegment(RegionSettings settings) {
        this.xInput = settings.xInput;
        this.yInput = settings.yInput;
        minOverlap = settings.minOverlap;
    }

    // TODO AP: реализовать уменьшение перманентности при удалении от геометрического центра подключенных входов

    /**
     * Создание начального списка потенциальных синапсов, состоящего из случайного множества входных битов,
     * выбранных из пространства входных данных. Каждый входной бит предтсавлен синапсом с некоторым случайным
     * значением перманентности.
     *
     * @param xCenter
     * @param yCenter
     */
    public void initSynapses(int xCenter, int yCenter) {
        List<Integer> indices = new ArrayList<>();
        for (int i = xCenter - potentialRadius; i <= xCenter + potentialRadius; i++) {
            if (i >= 0 && i < xInput) {
                for (int j = yCenter - potentialRadius; j <= yCenter + potentialRadius; j++) {
                    if (j >= 0 && j < yInput)
                        indices.add(i * yInput + j);
                }
            }
        }

        Collections.shuffle(indices, random);
        int numPotential = (int) Math.round(indices.size() * connectedPct);
        for (int i = 0; i < numPotential; i++) {
            int index = indices.get(i);
            Synapse synapse = new Synapse(index);
            synapse.initPermanence();
            potentialSynapses.put(index, synapse);
        }
    }

    /**
     * Перекрытие - это просто число действующих синапсов подключенных к активным входным битам, умноженное на
     * фактор ускорения. Если полученное значение мньше minOverlap, то перекрытие устанавливается в 0.
     *
     * @param input
     * @return
     */
    public int overlapCalculating(BitVector input) {
        overlap = 0;
        for (int key : potentialSynapses.keySet())
            overlap += input.get(key) ? 1 : 0;

        overlap *= overlap < minOverlap ? 0 : boostFactor;
        return overlap;
    }

    /**
     * Есил синапс был активен (через него шел сигнал от входного вектора), его значение преманентности увеличивается,
     * а иначе - уменьшается.
     *
     * @param input
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
                int x = synapse.getInputSource() / yInput;
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

    public Map<Integer, Synapse> getPotentialSynapses() {
        return potentialSynapses;
    }
}
