package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitVector;

/**
 * Created by gmdidro on 24.01.2015.
 */
public interface IInputLoader {
    BitVector getNextInput();
}
