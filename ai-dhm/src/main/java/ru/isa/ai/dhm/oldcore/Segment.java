package ru.isa.ai.dhm.oldcore;

public class Segment {
    public Synapse[] synapses;
    public int synapsesNum;

    public Boolean sequenceSegment = false;

    public Segment() {
        synapses = new Synapse[1000];
        synapsesNum = 0;
        sequenceSegment = false;
    }

    public boolean segmentContainsSynapse(Synapse syn){
        boolean flag = false;
        int ind = 0 ;
        while(!flag && ind < this.synapsesNum){
            if (this.synapses[ind] == syn){
                flag = true;
            }
            ind++;
        }
        return flag;
    }
}
