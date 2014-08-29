package ru.isa.ai.dhm.core2;

import cern.colt.function.tdouble.DoubleFunction;
import cern.colt.function.tint.IntProcedure;
import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.jet.math.tdouble.DoublePlusMultSecond;
import ru.isa.ai.dhm.RegionSettings;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Column {
    private int index;
    private int numInputs;
    private DendriticSegment proximalSegment;

    private Cell[] cells;

    private Random random = new Random();

    public Column(int index, RegionSettings settings) {
        this.index = index;
        this.numInputs = settings.numInputs;
        cells = new Cell[settings.cellsPerColumn];
        proximalSegment = new DendriticSegment(settings);
    }

    public void initialization(double ration){
        proximalSegment.initialMapInput(ration);
        proximalSegment.initPermanences();
    }
    public int getIndex() {
        return index;
    }
}
