package ru.isa.ai.dhm.poolers;

import org.la4j.vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GraffT on 04.05.2014.
 */
public class CoordinateConverterND {
    private int[] inputDimensions;
    private int[] bounds;
    private int size;

    public CoordinateConverterND(int[] inputDimensions) {
        this.size = inputDimensions.length;
        this.inputDimensions = inputDimensions;
        this.bounds = new int[size];
        int b = 1;
        for (int i = size - 1; i >= 0; i--) {
            bounds[i] = b;
            b *= inputDimensions[i];
        }
    }

    public int[] toCoord(int index) {
        int[] coord = new int[size];
        for (int i = 0; i < bounds.length; i++) {
            coord[i] = ((index / bounds[i]) % inputDimensions[i]);
        }
        return coord;
    }

    public int toIndex(List<Integer> coords) {
        int index = 0;
        for (int i = 0; i < size; i++)
            index += coords.get(i) * bounds[i];
        return index;

    }
}
