package ru.isa.ai.tests.coursera.week1;

import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

/**
 * Author: Aleksandr Panov
 * Date: 09.09.13
 * Time: 13:07
 */
public class PercolationStats {
    private Percolation perc;
    private int T;
    private int N;
    private double[] thresholds;

    public PercolationStats(int N, int T) {
        if (N <= 0 && T <= 0) throw new IllegalArgumentException();
        perc = new Percolation(N);
        this.T = T;
        this.N = N;
        thresholds = new double[T];
    }    // perform T independent computational experiments on an N-by-N grid

    public double mean() {
        return StdStats.mean(thresholds);
    }                     // sample mean of percolation threshold

    public double stddev() {
        return StdStats.stddev(thresholds);
    }                   // sample standard deviation of percolation threshold

    public double confidenceLo() {
        return mean() - 1.96 * stddev() / Math.sqrt(T);
    }             // returns lower bound of the 95% confidence interval

    public double confidenceHi() {
        return mean() + 1.96 * stddev() / Math.sqrt(T);
    }             // returns upper bound of the 95% confidence interval

    public static void main(String[] args) {
        PercolationStats stats = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        Stopwatch stopwatch = new Stopwatch();
        for (int i = 0; i < stats.T; i++) {
            double opened = 0;
            while (!stats.perc.percolates()) {
                int x = StdRandom.uniform(stats.N) + 1;
                int y = StdRandom.uniform(stats.N) + 1;
                if (!stats.perc.isOpen(x, y)) {
                    stats.perc.open(x, y);
                    opened++;
                }
            }
            stats.thresholds[i] = opened / (stats.N * stats.N);
            stats.perc = new Percolation(stats.N);
        }
        StdOut.printf("time                    = %f\n", stopwatch.elapsedTime());
        StdOut.printf("mean                    = %f\n", stats.mean());
        StdOut.printf("stddev                  = %f\n", stats.stddev());
        StdOut.printf("95%% confidence interval = %f, %f\n", stats.confidenceLo(), stats.confidenceHi());
    }   // test client, described below
}
