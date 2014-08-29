package ru.isa.ai.dhm.core2;

import cern.colt.function.tint.IntProcedure;
import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.RegionSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Author: Aleksandr Panov
 * Date: 29.08.2014
 * Time: 10:30
 */
public class DendriticSegment {
    private Synapse[] synapses;
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
     * number of synapses that must be active in order for a column to
     * turn ON. The purpose of this is to prevent noisy input from
     * activating columns.
     */
    private long stimulusThreshold = 0;

    private int overlap;
    private double boost;

    public DendriticSegment(RegionSettings settings) {
        numInputs = settings.numInputs;
        synapses = new Synapse[numInputs];
        connectedSynapses = new BitVector(numInputs);
        connectedInputs = new BitVector(numInputs);
    }

    public void initialMapInput(double ratio) {
        int centerIndex = (int) ((numInputs - 1) * ratio);
        List<Integer> indices = new ArrayList<>();
        for (int i = centerIndex - potentialRadius; i <= centerIndex + potentialRadius; i++) {
            if (i >= 0 && i < numInputs)
                indices.add(i);
        }

        Collections.shuffle(indices, random);

        long numConnected = Math.round(indices.size() * connectedPct);
        for (int i = 0; i < numConnected; i++) {
            connectedInputs.set(indices.get(i));
        }
    }

    // TODO AP: реализовать уменьшение перманентности при удалении от геометрического центра подключенных входов
    public void initPermanences() {
        connectedInputs.forEachIndexFromToInState(0, connectedInputs.size() - 1, true, new IntProcedure() {
            @Override
            public boolean apply(int index) {
                synapses[index].initPermanence();
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
                        synapses[index].stimulatePermanence();
                        return true;
                    }
                });
            }
        }

        for (Synapse synapse : synapses) {
            if (synapse.isConnected())
                connectedSynapses.set(synapse.getIndex());
        }
    }

    public void overlapCalculating(BitVector input){
        overlap = 0;
        connectedSynapses.cardinality();
    }

    private int countConnected() {
        int connected = 0;
        for (Synapse synapse : synapses) {
            connected += synapse.isConnected() ? 1 : 0;
        }
        return connected;
    }

    private void clip(boolean trim) {
        for (Synapse synapse : synapses) {
            synapse.clip(trim);
        }
    }
}
