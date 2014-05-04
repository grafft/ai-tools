package ru.isa.ai.dhm;

import cern.colt.matrix.tbit.BitMatrix;
import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tint.impl.SparseIntMatrix2D;
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

    public static void setRow(BitMatrix matrix, BitVector row, int rowIndex) {
        matrix.toBitVector().replaceFromToWith(matrix.columns() * rowIndex, (matrix.columns() + 1) * rowIndex, row, 0);
    }

    public static BitVector getRow(BitMatrix matrix, int rowIndex) {
        return matrix.part(0, rowIndex, matrix.columns(), 1).toBitVector();
    }
}
