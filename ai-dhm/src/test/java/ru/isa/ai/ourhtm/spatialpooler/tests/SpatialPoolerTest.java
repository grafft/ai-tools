package ru.isa.ai.ourhtm.spatialpooler.tests;

import casmi.matrix.Vector2D;
import cern.colt.matrix.tbit.BitMatrix;
import cern.colt.matrix.tbit.BitVector;
import junit.framework.Assert;
import junit.framework.TestCase;
import ru.isa.ai.ourhtm.algorithms.SimpleMapper;
import ru.isa.ai.ourhtm.algorithms.VerySimpleMapper;
import ru.isa.ai.ourhtm.structure.Column;
import ru.isa.ai.ourhtm.structure.Synapse;
import ru.isa.ai.ourhtm.util.MathUtils;
import ru.isa.ai.ourhtm.algorithms.SpatialPooler;
import ru.isa.ai.ourhtm.structure.HTMSettings;
import ru.isa.ai.ourhtm.structure.Region;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by APetrov on 15.05.2015.
 */
public class SpatialPoolerTest extends TestCase {

    public void testRun() throws IOException {
     /*   testHTMConstructuion();

        //overlap test
        testOverlapOnOnes();
        testOverlapOnNotOnes();

        //inhibition test
        testInhibitionPhase();

        testUpdateSynapses();
        testUpdateActiveDutyCycle();

        //testLearning();
*/
        //testLadder();


    }


    //TODO: переписать, сейчас не верно беруться размеры
    public void testDiff() throws IOException
    {
        FileInputStream fis_truth=new FileInputStream("in.txt");
        Scanner sc_truth=new Scanner(fis_truth);
        FileInputStream fis_predict=new FileInputStream("out_predict.txt");
        Scanner sc_p=new Scanner(fis_predict);
        FileOutputStream fos_err=new FileOutputStream("errs.txt");
        PrintWriter pw_err=new PrintWriter(fos_err);



        int w=sc_truth.nextInt();
        int h=sc_truth.nextInt();
        int step=sc_truth.nextInt();

        sc_truth.nextLine();
        sc_p.nextLine();

        for(int s=0;s<step;s++) {
            int[] errs=new int[h];
            for (int i = 0; i < h; i++) {
                BitVector true_bv=MathUtils.bitvectorFromString(sc_truth.nextLine());
                //System.out.println(sc_truth.nextLine());
                BitVector predict_bv=MathUtils.bitvectorFromString(sc_p.nextLine());
                //System.out.println(sc_p.nextLine());

                predict_bv.xor(true_bv);
                errs[i]=(int)MathUtils.sumOfLongs(predict_bv.elements());
            }
            pw_err.println(MathUtils.sumOfInts(errs));
        }
        pw_err.close();
    }




    private Column findByColXY(ArrayList<Column> cols, int x, int y)
    {
        for(Column c:cols)
        {
            Vector2D v=c.getCoord();
            if(v.getX()==x && v.getY()==y) return c;
        }
        return null;
    }

    enum Dir{UP, DOWN};
    public void testLadder() throws IOException {

        FileOutputStream fos=new FileOutputStream("out.txt");
        PrintWriter pw=new PrintWriter(fos);

        FileOutputStream fos_in=new FileOutputStream("in.txt");
        PrintWriter pw_in=new PrintWriter(fos_in);

        final int W=95, H=95;
        final int begX=0,begY=0;
        final int stepSize=20;

        int[][] map = new int[W][H];
        int[] in=new int[W*H];
        int STEPS=25;
        int TOTAL_STEPS=1000;
        int STEP_SIZE=STEPS;

        HTMSettings settings=HTMSettings.getDefaultSettings();
        HTMSettings.debug=true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.desiredLocalActivity = 3;
        settings.connectedPct=1;
        settings.connectedPerm=0.01;
        settings.xInput=W;
        settings.yInput=H;
        settings.potentialRadius=4;
        settings.xDimension=10;
        settings.yDimension=10;
        settings.initialInhibitionRadius=1;
        //settings.permanenceInc=0.1;
        //settings.permanenceDec=0.1;

        pw.print(settings.xDimension + " ");
        pw.print(settings.yDimension + " ");
        pw.print(TOTAL_STEPS);
        pw.println();

        pw_in.print(settings.xDimension + " ");
        pw_in.print(settings.yDimension + " ");
        pw_in.print(TOTAL_STEPS);
        pw_in.println();

        Region r=new Region(settings,new VerySimpleMapper());

        SpatialPooler sp=new SpatialPooler(settings);


        int x=begX,y=begY;
        for (int i = x; i < x + stepSize; i++)
            for (int j = y; j < y + stepSize; j++)
                map[i][j] = 1;

        for(int step=0;step<TOTAL_STEPS;step++) {

            System.out.println("DATA:");
            int index = 0;
            for (int k = 0; k < W; k++)
            {
                for (int m = 0; m < H; m++) {
                    in[index] = map[k][m];
//                    System.out.print(in[index]);
                    pw_in.print(in[index]);
                    index++;
                }
//                System.out.println();
                pw_in.println();

            }
            pw_in.println();

            for (int i = x; i < x + stepSize; i++)
                for (int j = y; j < y + stepSize; j++)
                    if((i<map.length)&&(j<map[0].length))
                        map[i][j] = 0;

            x=x+STEP_SIZE;
            y=y+STEP_SIZE;
            if(x>W) {x=0;y=0;}

           for (int i = x; i < x + stepSize; i++)
                for (int j = y; j < y + stepSize; j++)
                    if((i<map.length)&&(j<map[0].length))
                        map[i][j] = 1;

            BitVector input=new BitVector(in.length);
            MathUtils.assign(input, in);
            for(Column c:r.getColumns())
             c.setIsActive(false);
            int[] ov=sp.updateOverlaps(r.getColumns(), input);
            sp.inhibitionPhase(r.getColumns(), ov);
            sp.learningPhase(r.getColumns(), input, ov);

//            System.out.println("COLS:");
            ArrayList<Column> cols=r.getColumns();
            for(int i=0;i<settings.xDimension;i++)
            {
                for(int j=0;j<settings.yDimension;j++) {

                    int state=findByColXY(cols,i,j).isActive() ? 1 : 0;
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

    public void testLearning()
    {
        int[] in=new int[]{1,1,1,1,1,0,1,1, 1,1,1,1, 1,1,1,1};
        BitVector input=new BitVector(in.length);
        MathUtils.assign(input, in);

        HTMSettings settings=HTMSettings.getDefaultSettings();
        HTMSettings.debug=true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.desiredLocalActivity = 1;
        settings.connectedPct=1;
        settings.xInput=input.size();
        settings.yInput=1;
        settings.potentialRadius=2;
        settings.xDimension=4;
        settings.yDimension=1;
        settings.initialInhibitionRadius=1;
        settings.permanenceInc=0.2;
        settings.permanenceDec=0.2;

        Region r=new Region(settings,new SimpleMapper());
        SpatialPooler sp=new SpatialPooler(settings);
        r.getColumns().get(0).getProximalDendrite().getPotentialSynapses().get(4).setPermanence(0.5);
        r.getColumns().get(0).getProximalDendrite().getPotentialSynapses().get(5).setPermanence(0.5);
        int[] ov=sp.updateOverlaps(r.getColumns(), input);
        sp.inhibitionPhase(r.getColumns(),ov);
        sp.learningPhase(r.getColumns(), input, ov);

        double v=r.getColumns().get(0).getProximalDendrite().getPotentialSynapses().get(4).getPermanence();
        Assert.assertTrue(v == 0.7);
        v=r.getColumns().get(0).getProximalDendrite().getPotentialSynapses().get(5).getPermanence();
        Assert.assertTrue(v == 0.3);

    }

    public void testUpdateActiveDutyCycle() {
        Method method = null;
        try {
            Method[] m = SpatialPooler.class.getMethods();
            method = SpatialPooler.class.getDeclaredMethod("updateActiveDutyCycle", ArrayList.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);


        int[] in=new int[]{1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1};
        BitVector input=new BitVector(in.length);
        MathUtils.assign(input, in);

        HTMSettings settings=HTMSettings.getDefaultSettings();
        HTMSettings.debug=true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.desiredLocalActivity = 1;
        settings.connectedPct=1;
        settings.xInput=input.size();
        settings.yInput=1;
        settings.potentialRadius=2;
        settings.xDimension=4;
        settings.yDimension=1;
        settings.initialInhibitionRadius=1;
        try {
            Region r=new Region(settings,new SimpleMapper());

            SpatialPooler sp=new SpatialPooler(settings);
            int[] overlaps=sp.updateOverlaps(r.getColumns(),input);
            sp.inhibitionPhase(r.getColumns(), overlaps);
            method.invoke(sp, r.getColumns());
            sp.inhibitionPhase(r.getColumns(), overlaps);
            method.invoke(sp, r.getColumns());
            sp.inhibitionPhase(r.getColumns(), overlaps);
            method.invoke(sp, r.getColumns());
            sp.inhibitionPhase(r.getColumns(), overlaps);
            method.invoke(sp, r.getColumns());

            Assert.assertTrue(sp.getActiveDutyCycles().length == r.getColumns().size());
            Assert.assertTrue(sp.getActiveDutyCycles()[0] == 4);


            in=new int[]{1,0,1,0, 1,0,1,0, 1,0,1,0, 1,0,1,0};
            input=new BitVector(in.length);
            MathUtils.assign(input, in);

            r=new Region(settings, new SimpleMapper());
            sp=new SpatialPooler(settings);
            overlaps=sp.updateOverlaps( r.getColumns(),input);
            sp.inhibitionPhase(r.getColumns(), overlaps);
            method.invoke(sp, r.getColumns());
            sp.inhibitionPhase(r.getColumns(), overlaps);
            method.invoke(sp, r.getColumns());
            sp.inhibitionPhase(r.getColumns(), overlaps);
            method.invoke(sp, r.getColumns());
            sp.inhibitionPhase(r.getColumns(), overlaps);
            method.invoke(sp, r.getColumns());

            Assert.assertTrue(sp.getActiveDutyCycles().length == r.getColumns().size());
            Assert.assertTrue(sp.getActiveDutyCycles()[0]==0);
            Assert.assertTrue(sp.getActiveDutyCycles()[1]==3);
            Assert.assertTrue(sp.getActiveDutyCycles()[2]==3);
            Assert.assertTrue(sp.getActiveDutyCycles()[3]==2);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void testUpdateSynapses() {
        int[] in=new int[]{1,1,1,1,1,0,1,1, 1,1,1,1, 1,1,1,1};
        BitVector input=new BitVector(in.length);
        MathUtils.assign(input, in);

        HTMSettings settings=HTMSettings.getDefaultSettings();
        HTMSettings.debug=true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.desiredLocalActivity = 1;
        settings.connectedPct=1;
        settings.xInput=input.size();
        settings.yInput=1;
        settings.potentialRadius=2;
        settings.xDimension=4;
        settings.yDimension=1;
        settings.initialInhibitionRadius=1;
        settings.permanenceInc=0.2;
        settings.permanenceDec=0.2;
        Region r=new Region(settings,new SimpleMapper());

        SpatialPooler sp=new SpatialPooler(settings);
        r.getColumns().get(0).getProximalDendrite().getPotentialSynapses().get(4).setPermanence(0.5);
        sp.updateSynapses(r.getColumns(),input);
        double v=r.getColumns().get(0).getProximalDendrite().getPotentialSynapses().get(4).getPermanence();
        Assert.assertTrue(v==0.7);


        r.getColumns().get(0).getProximalDendrite().getPotentialSynapses().get(5).setPermanence(0.5);
        sp.updateSynapses(r.getColumns(),input);
        v=r.getColumns().get(0).getProximalDendrite().getPotentialSynapses().get(5).getPermanence();
        Assert.assertTrue(v==0.3);
    }

    public void testInhibitionPhase() {
        int[] in=new int[]{1,0,1,0, 1,0,1,0, 1,0,1,0, 1,0,1,0};
        BitVector input=new BitVector(in.length);
        MathUtils.assign(input, in);

        HTMSettings settings=HTMSettings.getDefaultSettings();
        HTMSettings.debug=true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.desiredLocalActivity = 1;
        settings.connectedPct=1;
        settings.xInput=input.size();
        settings.yInput=1;
        settings.potentialRadius=2;
        settings.xDimension=4;
        settings.yDimension=1;
        settings.initialInhibitionRadius=1;

        Region r=new Region(settings,new SimpleMapper());

        SpatialPooler sp=new SpatialPooler(settings);
        sp.seed=10;
        int[] overlaps=sp.updateOverlaps( r.getColumns(),input);
        ArrayList<Column> cols=sp.inhibitionPhase(r.getColumns(), overlaps);

        Assert.assertTrue(cols.size()==2);

        r=new Region(settings,new SimpleMapper());
        sp=new SpatialPooler(settings);
        overlaps=sp.updateOverlaps( r.getColumns(),input);
        // ожидаем разные результаты теста из-за рандомного шафла
        cols=sp.inhibitionPhase(r.getColumns(), overlaps);
        Assert.assertTrue(cols.size()==2);
        cols=sp.inhibitionPhase(r.getColumns(), overlaps);
        Assert.assertTrue(cols.size()==2);
        cols=sp.inhibitionPhase(r.getColumns(), overlaps);
        Assert.assertTrue(cols.size()==2);
        cols=sp.inhibitionPhase(r.getColumns(), overlaps);
        Assert.assertTrue(cols.size()==1);
    }

    public void testOverlapOnOnes() {
        int[] in=new int[]{1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1};
        BitVector input=new BitVector(in.length);
        MathUtils.assign(input, in);

        HTMSettings settings=HTMSettings.getDefaultSettings();
        HTMSettings.debug=true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.desiredLocalActivity = 1;
        settings.connectedPct=1;
        settings.xInput=input.size();
        settings.yInput=1;
        settings.potentialRadius=2;
        settings.xDimension=4;
        settings.yDimension=1;

        Region r=new Region(settings,new SimpleMapper());

        SpatialPooler sp=new SpatialPooler(settings);
        int[] overlaps=sp.updateOverlaps( r.getColumns(),input);

        int[] groundtruth=new int[]{5,5,5,5};
        for (int i = 0; i < groundtruth.length; i++)
            assertTrue(overlaps[i]==groundtruth[i]);

        settings.potentialRadius=2;
        settings.xDimension=1;
        settings.yDimension=1;

        r=new Region(settings,new SimpleMapper());
        sp=new SpatialPooler(settings);
        overlaps=sp.updateOverlaps( r.getColumns(),input);

        groundtruth=new int[]{5};
        for (int i = 0; i < groundtruth.length; i++)
            assertTrue(overlaps[i]==groundtruth[i]);


        settings.potentialRadius=2;
        settings.xDimension=16;
        settings.yDimension=1;

        r=new Region(settings,new SimpleMapper());
        sp=new SpatialPooler(settings);
        overlaps=sp.updateOverlaps( r.getColumns(),input);

        groundtruth=new int[]{3,4,5,5, 5,5,5,5, 5,5,5,5, 5,5,4,3};
        for (int i = 0; i < groundtruth.length; i++)
            assertTrue(overlaps[i]==groundtruth[i]);
    }

    public void testOverlapOnNotOnes() {
        int[] in=new int[]{1,0,1,0, 1,0,1,0, 1,0,1,0, 1,0,1,0};
        BitVector input=new BitVector(in.length);
        MathUtils.assign(input, in);

        HTMSettings settings=HTMSettings.getDefaultSettings();
        HTMSettings.debug=true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.desiredLocalActivity = 1;
        settings.connectedPct=1;
        settings.xInput=input.size();
        settings.yInput=1;
        settings.potentialRadius=2;
        settings.xDimension=4;
        settings.yDimension=1;

        Region r=new Region(settings,new SimpleMapper());

        SpatialPooler sp=new SpatialPooler(settings);
        int[] overlaps=sp.updateOverlaps( r.getColumns(),input);

        int[] groundtruth=new int[]{3,2, 3,2};
        for (int i = 0; i < groundtruth.length; i++)
            assertTrue(overlaps[i]==groundtruth[i]);
    }

    public void testHTMConstructuion() {
        HTMSettings settings = HTMSettings.getDefaultSettings();
        HTMSettings.debug = true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.desiredLocalActivity = 1;
        settings.connectedPct = 1;
        settings.xInput = 5;
        settings.yInput = 1;
        settings.potentialRadius = 2;
        settings.xDimension = 4;
        settings.yDimension = 1;
        settings.initialInhibitionRadius=2;

        Region r = new Region(settings,new SimpleMapper());


        assertTrue(r.getColumns().size() == 4);
        assertTrue(r.getInputH() == 1);
        assertTrue(r.getInputW() == 5);
        assertTrue(r.getColumns().get(0).getNeighbors().size()==2);
        Vector2D v=r.getColumns().get(r.getColumns().get(0).getNeighbors().get(0)).getCoord();
        assertTrue(v.getX()==1.0f && v.getY()==0.0f);
    }
}
