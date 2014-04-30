package ru.isa.ai.causal.jsm;

import java.util.Arrays;
import java.util.Collection;

/**
 * Author: Aleksandr Panov
 * Date: 30.04.2014
 * Time: 12:06
 */
public class BooleanArrayUtils {
    public static int countNonZero(byte[] array) {
        int nonZero = 0;
        for (byte val : array)
            if (val > 0)
                nonZero++;
        return nonZero;
    }

    public static byte[] multiply(byte[] array1, byte[] array2) {
        if (array1.length != array2.length)
            throw new IllegalArgumentException("Arrays must have equals size");
        byte[] result = new byte[array1.length];
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] == 1 && array2[i] == 1)
                result[i] = 1;
            else
                result[i] = 0;
        }
        return result;
    }

    public static byte[] subtraction(byte[] array1, byte[] array2) {
        if (array1.length != array2.length)
            throw new IllegalArgumentException("Arrays must have equals size");
        byte[] result = new byte[array1.length];
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] == 1 && array2[i] == 1)
                result[i] = 0;
            else if (array1[i] == 1)
                result[i] = 1;
            else
                result[i] = 0;
        }
        return result;
    }

    public static byte[] multiplyAll(Collection<byte[]> arrays) {
        if (arrays.size() < 1)
            throw new IllegalArgumentException("Collection must contains elements");
        byte[] result = null;
        for (byte[] array : arrays) {
            if (result == null) {
                result = array;
            } else if (result.length != array.length) {
                throw new IllegalArgumentException("Arrays must have equals size");
            } else {
                result = multiply(result, array);
            }
        }
        return result;
    }

    public static boolean equals(byte[] array1, byte[] array2) {
        return Arrays.equals(array1, array2);
    }

    public static boolean include(byte[] bigArray, byte[] smallArray) {
        if (bigArray.length != smallArray.length)
            throw new IllegalArgumentException("Arrays must have equals size");
        for (int i = 0; i < bigArray.length; i++) {
            if (bigArray[i] == 0 && smallArray[i] == 1)
                return false;
        }
        return true;
    }

    public static byte[] addition(byte[] array1, byte[] array2) {
        if (array1.length != array2.length)
            throw new IllegalArgumentException("Arrays must have equals size");
        byte[] result = new byte[array1.length];
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] == 0 && array2[i] == 0)
                result[i] = 0;
            else
                result[i] = 1;
        }
        return result;
    }

}
