package ru.isa.ai.newdhm;

public class SegmentUpdate {

    public int[] segmentIndex;
    public Synapse[] activeSynapses;
    public Boolean sequenceSegment = false;

    public SegmentUpdate(int[] segmentIndex, Synapse[] activeSynapses) {
        this.segmentIndex = segmentIndex;
        this.activeSynapses = activeSynapses;
    }
}
