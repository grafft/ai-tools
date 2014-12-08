package ru.isa.ai.tests.tfi;

/**
 * Created by GraffT on 21.10.2014.
 * for ai-main
 */
public class FibbonachiNumbers {
    private static long[] mem;

    public static long fibNumber(int count) {
        return count > 2 ? fibNumber(count - 2) + fibNumber(count - 1) : 1;
    }

    public static void main(String[] args) {
        mem = new long[50];
        for (int i = 1; i <= 50; i++) {
            //System.out.println(i + ": " + fibNumber(i));
            System.out.println(i + ": " + fibNumberMem(i));
        }

    }

    public static long fibNumberMem(int count) {
        if (mem[count - 1] == 0)
            mem[count - 1] = count > 2 ? fibNumberMem(count - 2) + fibNumberMem(count - 1) : 1;
        return mem[count - 1];
    }
}
