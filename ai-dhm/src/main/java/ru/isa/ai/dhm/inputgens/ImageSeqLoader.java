package ru.isa.ai.dhm.inputgens;

import cern.colt.matrix.tbit.BitMatrix;
import cern.colt.matrix.tbit.BitVector;

/**
 * Created by gmdidro on 24.01.2015.
 */
public class ImageSeqLoader implements IInputLoader {
    BitMatrix bm;
    String[] files;
    ImageClass[] images;
    public void SetImageFileNames(String[] names)
    {
        files=names;
    }

    void PreLoad()
    {
        images=new ImageClass[files.length];
        for(int i=0;i<files.length;i++)
            images[i].load(files[i]);
    }


    @Override
    public BitVector getNext() {
        return null;
    }

    @Override
    public BitVector getCurrent() {
        return null;
    }

    @Override
    public int[] getDim() {
        return new int[0];
    }
}