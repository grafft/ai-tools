package ru.isa.ai.tests;

import java.util.concurrent.Executors;

/**
 * Author: Aleksandr Panov
 * Date: 24.12.13
 * Time: 15:42
 */
public class ThreadSyncTest {
    private static final Object obj = new Object();

    public static void main(String[] args) {
        for(int i=0; i < 3; i++)
            test(i);
    }

    public static void test(int num) {
        System.out.println("Start test");
        synchronized (obj) {
            System.out.println("Start new thread");
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10 * 1000);
                        System.out.println("End thread");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        System.out.println("End test");
    }
}
