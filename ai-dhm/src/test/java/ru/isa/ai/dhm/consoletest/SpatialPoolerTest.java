package ru.isa.ai.dhm.consoletest;

import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tint.IntMatrix1D;
import junit.framework.TestCase;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.LogUtils;
import ru.isa.ai.dhm.RegionSettingsException;
import ru.isa.ai.dhm.core.*;
import ru.isa.ai.dhm.visual.HTMConfiguration;
import ru.isa.ai.olddhm.MathUtils;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static junit.framework.Assert.assertTrue;


/**
 * Created by gmdidro on 01.10.2014.
 */
public class SpatialPoolerTest  extends TestCase {

    private Neocortex neocortex;
    private DHMSettings settings;


    public void testRun() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        testOverlapPhase();
        testInhibitionPhase();
        testLearningPhase();
    }
    public static void main(String[] args) {

            SpatialPoolerTest test=new SpatialPoolerTest();
            test.initCortex(4,4,2,2);

            final BitVector input = new BitVector(test.settings.xInput * test.settings.yInput);
            for (int i = 0; i < test.settings.xInput * test.settings.yInput; i++) {
                //if (Math.random() > 0.3)
                    input.set(i);
            }


            for(int q=0;q<10;q++) {
                System.out.println("------- q= "+q+" ----------");
                BitVector result = test.neocortex.getRegions().get(0).forwardInputProcessing(input);


                for (int i = 0; i < result.size(); i++) {

                    System.out.print(i + "= " + result.get(i) + " ");
                    if ((i + 1) % test.settings.xDimension == 0)
                        System.out.println();

                }
            }
            /*for(Column c : test.neocortex.getRegions().get(0).getColumns().values()) {
                System.out.print(c.isActive()+" ");
                if((c.getIndex()+1) % test.settings.xDimension==0)
                    System.out.println();
            } */

    }

    public void testOverlapPhase() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {

            SpatialPoolerTest test=new SpatialPoolerTest();
            test.initCortex(4,4,2,2);
            Region r=test.neocortex.getRegions().get(0);

            int[] arr = {1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1};

            BitVector input = new BitVector(arr.length);
            MathUtils.assign(input, arr);

            Method method = Region.class.getDeclaredMethod("overlapPhase", BitVector.class);
            method.setAccessible(true);
            method.invoke(r,input);

            Field field=Region.class.getDeclaredField("overlaps");
            field.setAccessible(true);
            IntMatrix1D overlaps1D=(IntMatrix1D)field.get(r);
            int[] overlaps=overlaps1D.toArray();

            int[] groundtruth={3,2,3,2};
            for (int i = 0; i < groundtruth.length; i++)
                assertTrue(overlaps[i]==groundtruth[i]);


    }

    public void testInhibitionPhase() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {

        SpatialPoolerTest test=new SpatialPoolerTest();
        test.initCortex(4,4,2,2);
        Region r=test.neocortex.getRegions().get(0);

        int[] arr = {1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1};

        BitVector input = new BitVector(arr.length);
        MathUtils.assign(input, arr);

        Method method1 = Region.class.getDeclaredMethod("overlapPhase", BitVector.class);
        method1.setAccessible(true);
        method1.invoke(r,input);

        Method method2 = Region.class.getDeclaredMethod("inhibitionPhase");
        method2.setAccessible(true);
        method2.invoke(r);


        Field field=Region.class.getDeclaredField("activeColumns");
        field.setAccessible(true);
        BitVector activeColumns=(BitVector)field.get(r);

        boolean[] groundtruth={true,false,true,false};
        for (int i = 0; i < groundtruth.length; i++)
            assertTrue(activeColumns.get(i)==groundtruth[i]);


    }

    public void testLearningPhase() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {

        SpatialPoolerTest test=new SpatialPoolerTest();
        test.initCortex(4,4,2,2);
        Region r=test.neocortex.getRegions().get(0);

        int[] arr = {1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1};

        BitVector input = new BitVector(arr.length);
        MathUtils.assign(input, arr);

        Method method1 = Region.class.getDeclaredMethod("overlapPhase", BitVector.class);
        method1.setAccessible(true);
        method1.invoke(r,input);

        Method method2 = Region.class.getDeclaredMethod("inhibitionPhase");
        method2.setAccessible(true);
        method2.invoke(r);

        Field field1=Region.class.getDeclaredField("iterationNum");
        field1.setAccessible(true);
        field1.set(r,1);

        Method method3 = Region.class.getDeclaredMethod("learningPhase", BitVector.class);
        method3.setAccessible(true);
        method3.invoke(r,input);

        Field field2=Region.class.getDeclaredField("columns");
        field2.setAccessible(true);
        Map<Integer, Column> columns=(Map<Integer, Column>)field2.get(r);

        Field field3=Column.class.getDeclaredField("proximalSegment");
        field3.setAccessible(true);


        /*for(Column c : columns.values()) {
            Integer[] neighbors = new Integer[3];
            c.getNeighbors().toArray(neighbors);
            int[] groundtruth = IntStream.rangeClosed(0, 3).filter(p-> p!=c.getIndex()).toArray();
            for (int i = 0; i < groundtruth.length; i++)
                assertTrue(neighbors[i]==groundtruth[i]);

            if(c.isActive()) {
                ProximalSegment proximalSegment = (ProximalSegment) field3.get(c);
                assertTrue(proximalSegment.getReceptieveFieldSize()==2);

            }
        } */
    }


    public void testUpdateActiveCells() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        LogUtils.Open("cells.csv", "cols.csv");

        SpatialPoolerTest test=new SpatialPoolerTest();
        test.initCortex(4,4,2,2);
        Region r=test.neocortex.getRegions().get(0);

        int[] arr = {1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1};

        BitVector input = new BitVector(arr.length);
        MathUtils.assign(input, arr);

        LogUtils.printToCVS(r,"before iterate");

        // первая итерация для данного теста всегда дает один и тот же результат
        test.neocortex.iterate(input);
        for(Cell c : r.getColumns().get(0).getCells())
            assertTrue(c.getStateHistory()[0]== Cell.State.active);

        LogUtils.printToCVS(r,"after iterate");

        BitVector output=r.forwardInputProcessing(input);
        LogUtils.printToCVS(r,"after forwardInputProcessing");

        Method method = Region.class.getDeclaredMethod("updateActiveCells");
        method.setAccessible(true);
        method.invoke(r);
        LogUtils.printToCVS(r,"after updateActiveCells");

        Field field=Region.class.getDeclaredField("overlaps");
        field.setAccessible(true);
        IntMatrix1D overlaps1D=(IntMatrix1D)field.get(r);
        int[] overlaps=overlaps1D.toArray();

        int[] groundtruth={3,2,3,2};
        for (int i = 0; i < groundtruth.length; i++)
            assertTrue(overlaps[i]==groundtruth[i]);
    }


    private void initCortex(int xInput,int yInput,int xDimension,int yDimension) {
        settings = DHMSettings.getDefaultSettings();
        settings.debug=true; // отключим недетерменированность в алгоритмах для отладки
        settings.xInput=xInput; // ширина входного слоя (в сигналах)
        settings.yInput=yInput; // высота входного слоя (в сигналах)
        settings.xDimension=xDimension; // ширина региона (в колонках)
        settings.yDimension=yDimension; // высота региона (в колонках)
        settings.initialInhibitionRadius=1;
        settings.potentialRadius=1;
        settings.desiredLocalActivity=1;
        settings.connectedPerm=0.1;

        String path = HTMConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try{
        settings.saveIntoFile(path+"\\"+"test16xOnes.properties");
        } catch (RegionSettingsException e) {
            e.printStackTrace();
        }
        neocortex = new Neocortex();
        Region region1 = neocortex.addRegion(settings, null);
       /* java.util.List<Region> children = new ArrayList<>();
        children.add(region1);
        neocortex.addRegion(settings, children);*/
        neocortex.initialization();
    }
}
