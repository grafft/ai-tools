package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.util.ConsecutivePatternMachine;
import ru.isa.ai.dhm.util.SequenceMachine;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by gmdidro on 27.01.2015.
 */
public class BitVectorSeqLoader implements IInputLoader {
    List<Set<Integer>> sequence;
    int currPattIndx=0;
    public void start()
    {
        currPattIndx=0;
        SequenceMachine sequenceMachine = new SequenceMachine(new ConsecutivePatternMachine(32, 3));
        List<Integer> input = Arrays.asList(new Integer[]{0, 1, 2, 3, 4, 5, 4, 3, 2, 1, 0, -1});
        sequence = sequenceMachine.generateFromNumbers(input);
    }

    @Override
    public BitVector getNextInput() {
        if(currPattIndx<sequence.size())
            return SequenceMachine.toBitVector(sequence.get(currPattIndx++),32);
        else
            return null;
    }
}
