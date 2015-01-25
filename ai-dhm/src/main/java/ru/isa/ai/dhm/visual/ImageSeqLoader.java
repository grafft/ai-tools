package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitMatrix;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by gmdidro on 24.01.2015.
 */
public class ImageSeqLoader implements IInputLoader {
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

}
