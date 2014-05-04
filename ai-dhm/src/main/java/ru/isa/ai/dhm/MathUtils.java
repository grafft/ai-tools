package ru.isa.ai.dhm;

import org.la4j.vector.Vector;

/**
 * Created by GraffT on 04.05.2014.
 */
public final class MathUtils {
    private MathUtils() {
    }

    public static double roundWithPrecision(double toRound, int signs) {
        double multiplier = Math.pow(10, signs);
        return Math.round(multiplier * toRound) / multiplier;
    }

    public static int max(int[] columnDimensions) {
        int max = Integer.MIN_VALUE;
        for (int columnDimension : columnDimensions) {
            if (max < columnDimension)
                max = columnDimension;
        }
        return max;
    }
}
