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
        testOverlapPhase(new int[]{1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1}, 4,4,2,2, new int[]{3,2,3,2});
        testOverlapPhase(new int[]{0,0,0,0, 1,1,1,1, 1,1,1,1, 1,1,1,1}, 4,4,2,2, new int[]{1,1,3,2});
        testInhibitionPhase(new int[]{1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1}, 4,4,2,2, new boolean[]{true,false,true,false});
        testInhibitionPhase(new int[]{0,0,0,0, 1,1,1,1, 1,1,1,1, 1,1,1,1}, 4,4,2,2, new boolean[]{false,false,true,false});

        testLearningPhase(new int[]{1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1}, 4,4,2,2, new double[]{1,1,1,1},
                new boolean[][]{{true,true,true,true,true},{false,true,false,false,true},{true,true,true,true,true},{false,true,false,false,true}}
        );
        testUpdateActiveCells(new int[]{1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1}, 4,4,2,2,new boolean[]{true,false,true,false},
                new String[]{"active", "passive","active", "passive",   "active", "passive","active", "passive",
                            "active", "passive","active", "passive",    "active", "passive","active", "passive",});
        testUpdateActiveCells(new int[]{0,0,0,0, 1,1,1,1, 1,1,1,1, 1,1,1,1}, 4,4,2,2, new boolean[]{false,false,true,false},
                new String[]{"passive", "passive","passive", "passive", "passive", "passive","passive", "passive",
                             "active", "passive","active", "passive",   "active", "passive","active", "passive",});
    }

    public static void main(String[] args) {


    }

    public void testOverlapPhase(int[] input, int inputW, int inputH, int colW, int colsH, int[] trueOverlaps) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {

            SpatialPoolerTest test=new SpatialPoolerTest();
            test.initCortex(inputW, inputH, colW, colsH);
            Region r=test.neocortex.getRegions().get(0);

            int[] arr = input;

            BitVector inputvec = new BitVector(arr.length);
            MathUtils.assign(inputvec, arr);

            Method method = Region.class.getDeclaredMethod("overlapPhase", BitVector.class);
            method.setAccessible(true);
            method.invoke(r, inputvec);

            Field field=Region.class.getDeclaredField("overlaps");
            field.setAccessible(true);
            IntMatrix1D overlaps1D=(IntMatrix1D)field.get(r);
            int[] overlaps=overlaps1D.toArray();

            int[] groundtruth=trueOverlaps;
            for (int i = 0; i < groundtruth.length; i++)
                assertTrue(overlaps[i]==groundtruth[i]);
    }

    public void testInhibitionPhase(int[] input, int inputW, int inputH, int colsW, int colsH, boolean[] trueColStates) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {

        SpatialPoolerTest test=new SpatialPoolerTest();
        test.initCortex(inputW, inputH, colsW, colsH);
        Region r=test.neocortex.getRegions().get(0);



        BitVector inputvec = new BitVector(input.length);
        MathUtils.assign(inputvec, input);

        Method method1 = Region.class.getDeclaredMethod("overlapPhase", BitVector.class);
        method1.setAccessible(true);
        method1.invoke(r, inputvec);

        Method method2 = Region.class.getDeclaredMethod("inhibitionPhase");
        method2.setAccessible(true);
        method2.invoke(r);

        Field field=Region.class.getDeclaredField("activeColumns");
        field.setAccessible(true);
        BitVector activeColumns=(BitVector)field.get(r);

        boolean[] groundtruth=trueColStates;
        for (int i = 0; i < groundtruth.length; i++)
            assertTrue(activeColumns.get(i)==groundtruth[i]);


    }

    public void testLearningPhase(int[] input, int inputW, int inputH, int colsW, int colsH, double[] trueBoosts, boolean[][] trueConnected) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {

        SpatialPoolerTest test=new SpatialPoolerTest();
        test.initCortex(inputW, inputH, colsW, colsH);
        Region r=test.neocortex.getRegions().get(0);

        BitVector inputvec = new BitVector(input.length);
        MathUtils.assign(inputvec, input);

        Field field1=Region.class.getDeclaredField("iterationNum");
        field1.setAccessible(true);
        field1.set(r,1);

        Method method1 = Region.class.getDeclaredMethod("overlapPhase", BitVector.class);
        method1.setAccessible(true);
        method1.invoke(r,inputvec);

        Method method2 = Region.class.getDeclaredMethod("inhibitionPhase");
        method2.setAccessible(true);
        method2.invoke(r);

        Method method3 = Region.class.getDeclaredMethod("learningPhase", BitVector.class);
        method3.setAccessible(true);
        method3.invoke(r,inputvec);

        Field field3=Column.class.getDeclaredField("proximalSegment");
        field3.setAccessible(true);

        Field field4=ProximalSegment.class.getDeclaredField("potentialSynapses");
        field4.setAccessible(true);

        // проверим как изменился boost-фактор и состояние синапсов проксимального сегмента каждой колонки
        int i=0;
        for (Column c : r.getColumns().values()) {
            ProximalSegment ps=(ProximalSegment) field3.get(c);
            assertTrue(ps.getBoostFactor() ==  trueBoosts[i]);

            Map<Integer, Synapse> psyn=(Map<Integer, Synapse>) field4.get(ps);
            int j=0;
            for (Synapse syn : psyn.values()) {
                assertTrue(syn.isConnected() ==  trueConnected[i][j]);
                j++;
            }
            i++;
        }
    }


    public void testUpdateActiveCells(int[] input, int inputW, int inputH, int colsW, int colsH, boolean[] trueColStates, String[] trueCellStates) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        LogUtils.Open("cells.csv", "cols.csv");

        SpatialPoolerTest test=new SpatialPoolerTest();
        test.initCortex(inputW,inputH,colsW,colsH);
        Region r=test.neocortex.getRegions().get(0);

        BitVector inputvec = new BitVector(input.length);
        MathUtils.assign(inputvec, input);

        LogUtils.printToCVS(r,"after initialization");

        r.forwardInputProcessing(inputvec);
        LogUtils.printToCVS(r,"after 1st forwardInputProcessing");

        //проверим, что победила та колонка, которую мы ожидаем
        Field field=Region.class.getDeclaredField("activeColumns");
        field.setAccessible(true);
        BitVector activeColumns=(BitVector)field.get(r);

        boolean[] groundtruth=trueColStates;
        for (int i = 0; i < groundtruth.length; i++)
            assertTrue(activeColumns.get(i)==groundtruth[i]);


        Method method = Region.class.getDeclaredMethod("updateActiveCells");
        method.setAccessible(true);
        method.invoke(r);
        LogUtils.printToCVS(r,"after 1st updateActiveCells");

        int i=0;
        //проверка активности клеток
        for (int colLine = 0; colLine < colsH; colLine++) {
            // перебор всех слоев клеток

            for (int layer = 0; layer < test.settings.cellsPerColumn; layer++) {
                for (int col = colLine * colsW; col < (colLine + 1) * colsW; col++) {
                    String state=r.getColumns().get(col).getCells()[layer].getStateHistory()[0].toString();
                    assertTrue(trueCellStates[i].equalsIgnoreCase(state));
                    i++;
                }
            }
        }
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
        Region region1 = neocortex.addRegion(0,settings, null);
       /* java.util.List<Region> children = new ArrayList<>();
        children.add(region1);
        neocortex.addRegion(settings, children);*/
        neocortex.initialization();
    }
}
