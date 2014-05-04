package ru.isa.ai.dhm.poolers;

import org.la4j.vector.Vector;

/**
 * Created by GraffT on 04.05.2014.
 */
public class CoordinateConverterND {
    private int[] inputDimensions;
    private int[] bounds;

    public CoordinateConverterND(int[] inputDimensions) {
        this.inputDimensions = inputDimensions;
        this.bounds = new int[inputDimensions.length];
        int b = 1;
        for (int i = inputDimensions.length - 1; i >= 0; i--) {
            bounds[inputDimensions.length - i - 1] = b;
            b *= inputDimensions[i];
        }
    }

    public void toCoord(int index, Vector coord) {
        coord.resize(0);
        for (int i = 0; i < bounds.length; i++) {
            coord.add((index / bounds[i]) % inputDimensions[i]);
        }
    }
}
