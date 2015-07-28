package ru.isa.ai.ourhtm.app;

import casmi.matrix.Vector2D;
import cern.colt.matrix.tbit.BitMatrix;
import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.ourhtm.algorithms.SpatialPooler;
import ru.isa.ai.ourhtm.algorithms.VerySimpleMapper;
import ru.isa.ai.ourhtm.structure.Column;
import ru.isa.ai.ourhtm.structure.HTMSettings;
import ru.isa.ai.ourhtm.structure.Region;
import ru.isa.ai.ourhtm.structure.Synapse;
import ru.isa.ai.ourhtm.util.MathUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by APetrov on 22.07.2015.
 */
public class HtmRunner {

    public HtmRunner() {
        settings = HTMSettings.getDefaultSettings();
        HTMSettings.debug = true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.desiredLocalActivity = 3;
        settings.connectedPct = 1;
        settings.connectedPerm = 0.01;
        settings.xInput = W;
        settings.yInput = H;
        settings.potentialRadius = 4;
        settings.xDimension = 10;
        settings.yDimension = 10;
        settings.initialInhibitionRadius = 1;
        //settings.permanenceInc=0.1;
        //settings.permanenceDec=0.1;
    }

    public static void main(String[] args) throws IOException {



       /* if(args[0]=="out") {
            HtmRunner runner = new HtmRunner();
            runner.testLadder();
        }
        if(args[0]=="in") */
        {
            HtmRunner runner = new HtmRunner();
            runner.generateOut();
        }

    }

    private Column findByColXY(ArrayList<Column> cols, int x, int y) {
        for (Column c : cols) {
            Vector2D v = c.getCoord();
            if (v.getX() == x && v.getY() == y) return c;
        }
        return null;
    }

    enum Dir {UP, DOWN}

    ;
    HTMSettings settings;
    final int W = 95, H = 95;
    final int begX = 0, begY = 0;
    final int stepSize = 20;

    int[][] map = new int[W][H];
    int[] in = new int[W * H];
    int STEPS = 50;
    int TOTAL_STEPS = STEPS * 1000;

    public void testLadder() throws IOException {

        FileOutputStream fos = new FileOutputStream("out.txt");
        PrintWriter pw = new PrintWriter(fos);

        pw.print(settings.xDimension + " ");
        pw.print(settings.yDimension + " ");
        pw.print(TOTAL_STEPS);
        pw.println();

        Region r = new Region(settings, new VerySimpleMapper());

        SpatialPooler sp = new SpatialPooler(settings);


        int x = begX, y = begY;
        for (int i = x; i < x + stepSize; i++)
            for (int j = y; j < y + stepSize; j++)
                map[i][j] = 1;

        for (int step = 0; step < TOTAL_STEPS; step++) {

            //System.out.println("DATA:");
            // pw.println("Data: "+step);
            int index = 0;
            for (int k = 0; k < W; k++) {
                for (int m = 0; m < H; m++) {
                    in[index] = map[k][m];
//                    System.out.print(in[index]);
                    //       pw.print(in[index]);
                    index++;
                }
//                System.out.println();
                //   pw.println();
            }

            for (int i = x; i < x + stepSize; i++)
                for (int j = y; j < y + stepSize; j++)
                    if ((i < map.length) && (j < map[0].length))
                        map[i][j] = 0;

            x++;
            y++;
            if ((step + 1) % STEPS == 0) {
                x = 0;
                y = 0;
            }

            for (int i = x; i < x + stepSize; i++)
                for (int j = y; j < y + stepSize; j++)
                    if ((i < map.length) && (j < map[0].length))
                        map[i][j] = 1;

            BitVector input = new BitVector(in.length);
            MathUtils.assign(input, in);
            for (Column c : r.getColumns())
                c.setIsActive(false);
            int[] ov = sp.updateOverlaps(r.getColumns(), input);
            sp.inhibitionPhase(r.getColumns(), ov);
            sp.learningPhase(r.getColumns(), input, ov);

//            System.out.println("COLS:");
            ArrayList<Column> cols = r.getColumns();
            for (int i = 0; i < settings.xDimension; i++) {
                for (int j = 0; j < settings.yDimension; j++) {

                    int state = findByColXY(cols, i, j).isActive() ? 1 : 0;
//                    System.out.print(state);
                    pw.print(state);
                    pw.print(" ");

                }
                pw.println();
//                System.out.println();
            }
            pw.println();

            /*System.out.println("BOOST:");
            cols=r.getColumns();
            for(int i=0;i<settings.xDimension;i++)
            {
                for(int j=0;j<settings.yDimension;j++) {
                    System.out.print(cols.get(i*settings.yDimension+j).getProximalDendrite().getBoostFactor()+" ");
                }
                System.out.println();
            }*/

//            System.out.println();
        }
        pw.close();
    }

    public void generateOut() throws IOException {
        FileOutputStream fos = new FileOutputStream("out_predict.txt");
        PrintWriter pw = new PrintWriter(fos);

        FileInputStream fis = new FileInputStream("out_for_java.txt");
        Scanner sc = new Scanner(fis);

        Region r = new Region(settings, new VerySimpleMapper());

        int w = sc.nextInt();
        int h = sc.nextInt();
        int steps = sc.nextInt();

        pw.print(w + " ");
        pw.print(h + " ");
        pw.print(steps);
        pw.println();

        for (int s = 0; s < steps; s++) {
            BitMatrix bm = new BitMatrix(r.getInputH(), r.getInputW());
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int colstate = sc.nextInt();
                    if (colstate == 1) {
                        ArrayList<Synapse> syns = findByColXY(r.getColumns(), i, j).getProximalDendrite().getConnectedSynapses();
                        for (Synapse syn : syns) {
                            int x = syn.getIndexConnectTo() / r.getInputH();
                            int y = syn.getIndexConnectTo() % r.getInputW();
                            bm.put(x, y, true);
                        }
                    }


                }
            }


           // System.out.println();
            for (int i = 0; i < r.getInputH(); i++) {
                for (int j = 0; j < r.getInputW(); j++) {
                    //System.out.print(bm.get(i, j) ? 1 : 0);
                    pw.print(bm.get(i, j) ? 1 : 0);
                }
                pw.println();

               // System.out.println();
            }
            pw.println();
        }
    }
}
