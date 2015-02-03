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
    public BitVectorSeqLoader()
    {
        start();
    }

    List<Set<Integer>> sequence;
    int currPattIndx=0;
    BitVector current;
    public void start()
    {
        currPattIndx=0;
        SequenceMachine sequenceMachine = new SequenceMachine(new ConsecutivePatternMachine(32, 3));
        List<Integer> input = Arrays.asList(0, 1, 2, 3, 4, 5, 4, 3, 2, 1, 0, -1);
        sequence = sequenceMachine.generateFromNumbers(input);
    }



    @Override
    public BitVector getNext() {
        if(currPattIndx<sequence.size())
            current= SequenceMachine.toBitVector(sequence.get(currPattIndx++),32);
        else
            current= null;
        return current;
    }

    @Override
    public BitVector getCurrent() {
        return current;
    }

    @Override
    public int[] getDim() {
        return new int[]{current.size(),1};
    }
}
