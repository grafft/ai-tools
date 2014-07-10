package ru.isa.ai.newdhm;

public class Synapse {
    public double permanence;

    public int c;
    public int i;

    public Synapse(int c, int i, Double permanence) {
        this.permanence = permanence;
        this.c = c;
        this.i = i;
    }
}