package ru.isa.ai.dhm.util;

/**
 * Created by gmdidro on 14.01.2015.
 */
import cern.colt.matrix.tbit.BitVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utilities for generating and manipulating sequences, for use in
 * experimentation and tests.
 *
 * @author David Ray
 */
public class SequenceMachine {
    private PatternMachine patternMachine;

    /**
     * Represents the end of a pattern or sequence when inserted in
     * a {@link Collection}, otherwise the primitive form of "None"
     * is -1
     */
    public static final Set<Integer> NONE = new HashSet<Integer>() {
        private static final long serialVersionUID = 1L;

        public String toString() { return "None"; }
    };

    /**
     * Constructs a new {@code SequenceMachine}
     * @param pMachine
     */
    public SequenceMachine(PatternMachine pMachine) {
        this.patternMachine = pMachine;
    }

    /**
     * Generate a sequence from a list of numbers.
     *
     * @param numbers
     * @return
     */
    public List<Set<Integer>> generateFromNumbers(List<Integer> numbers) {
        List<Set<Integer>> sequence = new ArrayList<Set<Integer>>();
        for(Integer i : numbers) {
            if(i == -1) {
                sequence.add(NONE);
            }else{
                Set<Integer> pattern = patternMachine.get(i);
                if(pattern==null) throw new NullPointerException();
                sequence.add(pattern);

            }
        }

        return sequence;
    }

    /**
     * Pretty print a sequence.
     *
     * @param sequence      the sequence of numbers to print
     * @param verbosity     the extent of output chatter
     * @return
     */
    public String prettyPrintSequence(List<Set<Integer>> sequence, int verbosity) {
        String text = "";

        for(int i = 0;i < sequence.size();i++) {
            Set<Integer> pattern = sequence.get(i);
            if(pattern == NONE) {
                text += "<reset>";
                if(i < sequence.size() - 1) {
                    text += "\n";
                }
            }else{
                text += patternMachine.prettyPrintPattern(pattern, verbosity);
            }
        }
        return text;
    }

    static public int[] toIntArray(Set<Integer> pattern) {
        int[] retVal = new int[pattern.size()];
        int idx = 0;
        for(int i : pattern) {
            retVal[idx++] = i;
        }
        return retVal;
    }

    static public BitVector toBitVector(Set<Integer> pattern, int outLen) {
        BitVector bv=new BitVector(outLen);
        for(int i:pattern)
            bv.set(i);
        return bv;
    }
}
