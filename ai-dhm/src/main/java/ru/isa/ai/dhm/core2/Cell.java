package ru.isa.ai.dhm.core2;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Cell {
    public enum State{
        feedForwardActive, lateralActive, passive
    }

    private DendriticSegment[] distalSegments;
}
