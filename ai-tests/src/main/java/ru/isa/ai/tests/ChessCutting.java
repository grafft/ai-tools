package ru.isa.ai.tests;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 26.03.14
 * Time: 19:40
 */
public class ChessCutting {
    private static final int[] MAX_COUNTS = {1, 1, 2, 7, 18, 60, 196};

    public static void main(String[] args) {
        int n = new Scanner(System.in).nextInt();
        int max = 0, rest = n * n, currentSize = 0;
        while (rest > 0) {
            currentSize++;
            int places = currentSize * MAX_COUNTS[currentSize - 1];
            if (places <= rest) {
                rest -= places;
                max += MAX_COUNTS[currentSize - 1];
            } else {
                max += rest / currentSize;
                rest = -1;
            }
        }
        System.out.println("Max count = " + max + ", max size = " + currentSize);
//        int[][] desk = new int[n][n];
//        Set<Figure> used = new HashSet<>();
//
//        Point startPoint = new Point(0, 0);
//        Figure placed = null;
//        do {
//            placed = placeFigure(desk, used, startPoint);
//            used.add(placed);
//            startPoint = searchStart(desk);
//        } while (placed != null);
    }

    private static Point searchStart(int[][] desk) {
        return null;
    }

    private static Figure placeFigure(int[][] desk, Set<Figure> used, Point startPoint) {

        return null;
    }

    private static class Point {
        private int x;
        private int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class Figure {
        private int size;
        private int[] rotators;// 1 - up, 2 - right, 3 - down, 4 - left

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Figure figure = (Figure) o;

            if (size != figure.size) return false;
            if (!Arrays.equals(rotators, figure.rotators)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = size;
            result = 31 * result + (rotators != null ? Arrays.hashCode(rotators) : 0);
            return result;
        }
    }
}
