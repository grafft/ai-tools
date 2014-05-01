package ru.isa.ai.tests.mipt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by GraffT on 15.04.2014.
 */
public class Test18Java {
    public static void main(String[] args) {
        int max = 1_000_000;
        List<String> values = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            values.add(uuid.toString());
        }

        long t0 = System.nanoTime();
        values.stream().sorted();
        System.out.println(String.format("Sequential sort: %d ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0)));

        long t1 = System.nanoTime();
        System.out.println(values.parallelStream().sorted().count());
        System.out.println(String.format("Parallel sort: %d ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t1)));

    }
}
