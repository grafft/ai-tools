package ru.isa.ai.tests.mipt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Created by Aleksandr on 17.02.2015.
 */
public class FunctionTests {
    public static void main(String[] args) {
        try {
            shuffleTest();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void shuffleTest() throws FileNotFoundException, NumberFormatException {
        int[] array15 = IntStream.range(1, 16).toArray();
        int[] array20 = IntStream.range(1, 21).toArray();

        shuffle(array15);
        shuffle(array20);
        System.out.println(Arrays.toString(array15));
        System.out.println(Arrays.toString(array20));

//        Scanner scanner = new Scanner(System.in);
//        scanner.useDelimiter(" ");
//        scanner.nextInt();

//        FileInputStream stream = new FileInputStream("Fi");
    }

    private static void shuffle(int[] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            // Simple swap
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }
}
