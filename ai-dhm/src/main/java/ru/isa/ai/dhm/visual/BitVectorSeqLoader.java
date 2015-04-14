package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.util.ConsecutivePatternMachine;
import ru.isa.ai.dhm.util.SequenceMachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by gmdidro on 27.01.2015.
 */
public class BitVectorSeqLoader implements IInputLoader {
    int len;
    public BitVectorSeqLoader(int[] inputDim)
    {
        len=inputDim[0];
        start(len);
    }

    List<Set<Integer>> sequence;
    int currPattIndx=0;
    BitVector current;
    public void start(int len)
    {
        currPattIndx=0;
        SequenceMachine sequenceMachine = new SequenceMachine(new ConsecutivePatternMachine(len, 4));
        List<Integer> input=new ArrayList<>();
        for(int i=0;i<40;i++)
        {
            input.addAll(Arrays.asList(0, 1,2,3,2, 1));
        }

        //List<Integer> input = Arrays.asList(0, 1,2,3,4,3,2, 1, 0,1,2,3,4,3,2, 1, 0,1,2,3,4,3,2, 1, 0,1,2,3,4,3,2, 1, 0,1,2,3,4,3,2, 1, 0, -1);
        //List<Integer> input = Arrays.asList(0, 1,2,3,4,5,4,3,2, 1, 0,1,2,3,4,5,4,3,2, 1, 0,1,2,3,4,5,4,3,2, 1, 0,1,2,3,4,5,4,3,2, 1, 0,1,2,3,4,5,4,3,2, 1, 0, -1);
        //input = Arrays.asList(0,1,2,3, 0,1,2,3, 0,1,2,3, 0,1,2,3, 0,1,2,3, 0,1,2,3, 0,1,2,3, 0,1,2,3, 0,1,2,3, 0,1,2,3, 0,1,2,3, -1);
        input = Arrays.asList(0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1, -1);
        sequence = sequenceMachine.generateFromNumbers(input);
    }



    @Override
    public BitVector getNext() {
        if(currPattIndx<sequence.size()) {
            current = SequenceMachine.toBitVector(sequence.get(currPattIndx), len);
            currPattIndx = currPattIndx + 1;
        }
        else
            current = null;
        return current;
    }

    @Override
    public BitVector getCurrent() {
        return current;
    }

    @Override
    public int[] getDim() {
        if(current!=null)
            return new int[]{current.size(),1};
        else
            return new int[]{0,0};
    }
}
