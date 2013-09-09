package ru.isa.ai.tests.coursera.week1;

import edu.princeton.cs.algs4.QuickUnionUF;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * Author: Aleksandr Panov
 * Date: 09.09.13
 * Time: 13:07
 */
public class Percolation {
    private int[][] sites;
    private int N;
    private WeightedQuickUnionUF qu;

    public Percolation(int N) {
        this.N = N;

        sites = new int[N][N];
        qu = new WeightedQuickUnionUF(N * N + 2);
    }              // create N-by-N grid, with all sites blocked

    public void open(int i, int j) {
        if (i < 1 || i > N || j < 1 || j > N) throw new IndexOutOfBoundsException();
        sites[i - 1][j - 1] = 1;
        if (i > 1 && isOpen(i - 1, j)) qu.union(N * (j - 1) + i - 1, N * (j - 1) + i - 2);
        if (i < N && isOpen(i + 1, j)) qu.union(N * (j - 1) + i - 1, N * (j - 1) + i);
        if (j > 1 && isOpen(i, j - 1)) {
            qu.union(N * (j - 1) + i - 1, N * (j - 2) + i - 1);
        } else if (j == 1) {
            qu.union(N * (j - 1) + i - 1, N * N);
        }
        if (j < N && isOpen(i, j + 1)) {
            qu.union(N * (j - 1) + i - 1, N * j + i - 1);
        } else if (j == N) {
            qu.union(N * (j - 1) + i - 1, N * N + 1);
        }
    }         // open site (row i, column j) if it is not already

    public boolean isOpen(int i, int j) {
        return sites[i - 1][j - 1] == 1;
    }    // is site (row i, column j) open?

    public boolean isFull(int i, int j) {
        return isOpen(i, j) && qu.connected(N * N, N * (j - 1) + i - 1);
    }    // is site (row i, column j) full?

    public boolean percolates() {
        return qu.connected(N * N, N * N + 1);
    }            // does the system percolate?
}
