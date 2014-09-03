package ru.isa.ai.dhm.core2;

import cern.colt.function.tint.IntProcedure;
import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.RegionSettings;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 29.08.2014
 * Time: 10:30
 */
public class DendriticSegment {
    private Map<Integer, Synapse> potentialSynapses = new HashMap<>();
    private BitVector connectedSynapses;
    private BitVector connectedInputs;

    private int numInputs;
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

    public DendriticSegment(RegionSettings settings) {
        this.numInputs = settings.xInput * settings.yInput;
        minOverlap = settings.minOverlap;
        connectedSynapses = new BitVector(numInputs);
        connectedInputs = new BitVector(numInputs);
    }

    public void initPotentialSynapses(double ratio) {
        int centerIndex = (int) ((numInputs - 1) * ratio);
        List<Integer> indices = new ArrayList<>();
        for (int i = centerIndex - potentialRadius; i <= centerIndex + potentialRadius; i++) {
            if (i >= 0 && i < numInputs)
                indices.add(i);
        }

        Collections.shuffle(indices, random);

        int numConnected = (int) Math.round(indices.size() * connectedPct);
        for (int i = 0; i < numConnected; i++) {
            int index = indices.get(i);
            connectedInputs.set(index);
            potentialSynapses.put(index, new Synapse(index));
        }
    }

    // TODO AP: реализовать уменьшение перманентности при удалении от геометрического центра подключенных входов
    public void initPermanences() {
        connectedInputs.forEachIndexFromToInState(0, connectedInputs.size() - 1, true, new IntProcedure() {
            @Override
            public boolean apply(int index) {
                potentialSynapses.get(index).initPermanence();
                return true;
            }
        });
        updatePermanences(true);
    }

    public void updatePermanences(boolean raisePerm) {
        if (raisePerm) {
            clip(false);
            while (true) {
                if (countConnected() >= stimulusThreshold)
                    break;

                connectedInputs.forEachIndexFromToInState(0, connectedInputs.size() - 1, true, new IntProcedure() {
                    @Override
                    public boolean apply(int index) {
                        potentialSynapses.get(index).stimulatePermanence();
                        return true;
                    }
                });
            }
        }

        for (Map.Entry<Integer, Synapse> entry : potentialSynapses.entrySet()) {
            if (entry.getValue().isConnected())
                connectedSynapses.set(entry.getKey());
            else
                connectedSynapses.clear(entry.getKey());
        }
    }

    public int overlapCalculating(BitVector input) {
        BitVector intersection = (BitVector) connectedSynapses.clone();
        intersection.and(input);
        overlap = intersection.cardinality();
        if (overlap < minOverlap)
            overlap = 0;
        else
            overlap *= boostFactor;
        return overlap;
    }

    public void learning() {
        connectedSynapses.forEachIndexFromToInState(0, connectedSynapses.size() - 1, true, new IntProcedure() {
            @Override
            public boolean apply(int element) {
                potentialSynapses.get(element).increasePermanence();
                return true;
            }
        });
        connectedSynapses.forEachIndexFromToInState(0, connectedSynapses.size() - 1, false, new IntProcedure() {
            @Override
            public boolean apply(int element) {
                potentialSynapses.get(element).decreasePermanence();
                return true;
            }
        });
    }

    public void stimulate() {
        for (Synapse synapse : potentialSynapses.values()) {
            synapse.stimulatePermanence();
        }
    }

    private int countConnected() {
        int connected = 0;
        for (Synapse synapse : potentialSynapses.values()) {
            connected += synapse.isConnected() ? 1 : 0;
        }
        return connected;
    }

    private void clip(boolean trim) {
        for (Synapse synapse : potentialSynapses.values()) {
            synapse.clip(trim);
        }
    }

    public int getOverlap() {
        return overlap;
    }

    public void setBoostFactor(double val) {
        boostFactor = val;
    }

    public BitVector getConnectedSynapses() {
        return connectedSynapses;
    }

    public Map<Integer, Synapse> getPotentialSynapses() {
        return potentialSynapses;
    }

}
