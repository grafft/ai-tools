package ru.isa.ai.dhm.inputgens;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class TestSetup41 {

    public static void main(String[] argv) throws IOException {
        new TestSetup41().run();
    }


    public void run() throws IOException {
        // TODO AP: этот файл не будет найден
        Scanner sc = new Scanner(new File("in.txt"));
        int n = sc.nextInt();
        int m = sc.nextInt();
        int k = sc.nextInt();

        int a[][];
        a = new int[m][n];
        for (int i = 0; i < k; ++i)
            a[0][i] = 1;
        randomShuffle(a[0], n);

        // TODO AP: этот файл не будет найден
        PrintWriter pw = new PrintWriter(new File("out.txt"));

        for (int i = 1; i < m; ++i) {
            int q[] = new int[n - k];
            int cnt = 0;
            for (int j = 0; j < n; ++j)
                if (a[i - 1][j] == 0) {
                    q[cnt] = j;
                    cnt++;
                }

            randomShuffle(q, n - k);
            for (int j = 0; j < k; j++)
                a[i][q[j]] = 1;
        }


        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; ++j) {
                pw.print(a[i][j]);
                pw.print(" ");
            }
            pw.println();
        }
        pw.close();
    }

    private void randomShuffle(int a[], int n) {
        for (int i = 0; i < n; ++i) {
            Random ran = new Random();
            int r = ran.nextInt(n);

            int temp = a[r];
            a[r] = a[i];
            a[i] = temp;
        }
    }

}
