package ru.isa.ai.olddhm;

import cern.colt.matrix.tbit.BitMatrix;
import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;

import java.util.ArrayList;
import java.util.List;

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
        matrix.toBitVector().replaceFromToWith(rowIndex * matrix.columns(), (rowIndex + 1) * matrix.columns() - 1, row, 0);
    }

    public static void setRow(DoubleMatrix2D matrix, DoubleMatrix1D row, int rowIndex) {
        matrix.viewRow(rowIndex).assign(row);
    }

    public static BitVector getRow(BitMatrix matrix, int rowIndex) {
        return matrix.part(0, rowIndex, matrix.columns(), 1).toBitVector();
    }

    public static DoubleMatrix1D getRow(DoubleMatrix2D matrix, int rowIndex) {
        return matrix.viewRow(rowIndex);
    }

    public static List<List<Integer>> cartesianProduct(List<List<Integer>> vecs) {
        List<List<Integer>> product = new ArrayList<>();
        if (vecs.size() == 1) {
            for (int val : vecs.get(0)) {
                List<Integer> v = new ArrayList<>();
                v.add(val);
                product.add(v);
            }
            return product;
        }

        List<List<Integer>> result = new ArrayList<>(vecs);
        result.remove(0);

        List<List<Integer>> prod = cartesianProduct(result);
        for (int val : vecs.get(0)) {
            for (List<Integer> coord : prod) {
                coord.add(val);
                product.add(coord);
            }
        }
        return product;
    }

    public static double max(double[] doubles) {
        double max = Double.MIN_VALUE;
        for (double d : doubles) {
            if (max < d)
                max = d;
        }
        return max;
    }

    public static boolean almostEquals(double[] doubles, double[] doubles1) {
        if (doubles.length != doubles1.length)
            return false;

        for (int i = 0; i < doubles.length; i++)
            if (!almostEquals(doubles[i], doubles1[i]))
                return false;

        return true;
    }

    public static boolean almostEquals(double aDouble, double v) {
        double diff = Math.abs(aDouble - v);
        return diff < 1e-5;
    }

    public static boolean equals(int[] expectedMask1, BitVector mask) {
        if (expectedMask1.length != mask.size())
            return false;
        for (int i = 0; i < expectedMask1.length; i++)
            if ((mask.getQuick(i) && expectedMask1[i] == 0) ||
                    (!mask.getQuick(i) && expectedMask1[i] != 0))
                return false;

        return true;
    }

    public static void assign(BitVector potential, int[] arr) {
        if (potential.size() != arr.length)
            throw new IllegalArgumentException("Length must be equals");

        for (int i = 0; i < arr.length; i++)
            if (arr[i] > 0)
                potential.set(i);
    }
}
