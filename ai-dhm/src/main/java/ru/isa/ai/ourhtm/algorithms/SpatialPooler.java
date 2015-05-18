package ru.isa.ai.ourhtm.algorithms;

import ru.isa.ai.ourhtm.structure.Column;
import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.ourhtm.structure.HTMSettings;
import ru.isa.ai.ourhtm.structure.Synapse;

import java.util.ArrayList;

/**
 * Created by APetrov on 13.05.2015.
 */
public class SpatialPooler {
    HTMSettings settings;
    public int[] updateOverlaps(BitVector input, ArrayList<Column> cols) {
        int[] overlaps=new int[cols.size()];
        int i=0;
        for (Column c : cols) {
            Column.ProximalDendrite pd = c.getProximalDendrite();
            for (Synapse s : pd.getConnectedSynapses()) {
                pd.setOverlap(pd.getOverlap() + (input.get(s.getSourceIndex()) ? 1 : 0));
            }
            if (pd.getOverlap() < settings.minOverlap)
                pd.setOverlap(0);
            else
                pd.setOverlap(pd.getOverlap() * (int) pd.getBoostFactor());
            overlaps[i]=pd.getOverlap();
            i++;
        }
        return overlaps;
    }
}
