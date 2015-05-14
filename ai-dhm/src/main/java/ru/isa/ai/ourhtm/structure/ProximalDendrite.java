package ru.isa.ai.ourhtm.structure;

import java.util.ArrayList;

/**
 * Created by APetrov on 14.05.2015.
 */
public class ProximalDendrite {
    ArrayList<Synapse> synapses;
    private int overlap;
    private double boostFactor;

    public ArrayList<Synapse> getConnectedSynapses()
    {
        ArrayList<Synapse> conn_syn=new ArrayList<>(10);
        for(Synapse s : synapses)
            if(s.isConnected())
                conn_syn.add(s);
        return  conn_syn;
    }


    public int getOverlap() {
        return overlap;
    }

    public void setOverlap(int overlap) {
        this.overlap = overlap;
    }

    public double getBoostFactor() {
        return boostFactor;
    }

    public void setBoostFactor(double boostFactor) {
        this.boostFactor = boostFactor;
    }
}
