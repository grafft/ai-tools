package ru.isa.ai.dhm.consoletest;

import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tint.IntMatrix1D;
import junit.framework.TestCase;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.RegionSettingsException;
import ru.isa.ai.dhm.core.*;
import ru.isa.ai.dhm.util.ConsecutivePatternMachine;
import ru.isa.ai.dhm.util.LogUtils;
import ru.isa.ai.dhm.util.SequenceMachine;
import ru.isa.ai.dhm.visual.HTMConfiguration;
import ru.isa.ai.olddhm.MathUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by gmdidro on 01.10.2014.
 * Implementation based on this thesis - http://pdxscholar.library.pdx.edu/open_access_etds/202/
 */
public class TemporalPoolerFOTest extends TestCase {

    private Neocortex neocortex;
    private DHMSettings settings;


    private int[] toIntArray(Set<Integer> pattern) {
        int[] retVal = new int[pattern.size()];
        int idx = 0;
        for(int i : pattern) {
            retVal[idx++] = i;
        }
        return retVal;
    }

    private List<Set<Integer>> getSeqs(int M, int N) {
        SequenceMachine sequenceMachine = new SequenceMachine(new ConsecutivePatternMachine(33, 3));
        List<Integer> input = Arrays.asList(new Integer[]{0, 1, 2, 3, 7, 10, -1});
        List<Set<Integer>> sequence = sequenceMachine.generateFromNumbers(input);
        sequence = sequenceMachine.generateFromNumbers(input);

        for (Set<Integer> s : sequence) {
            for (int i : s)
                System.out.print(" : " + i);
            System.out.println();
        }
        String sequenceText = sequenceMachine.prettyPrintSequence(sequence, 4);
        System.out.println("Feeding sequence " + sequenceText);
        return sequence;
    }

    public void testRun() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {


    }

    public static void main(String[] args) {


    }
    /*Test that a rst order sequence can be learned with M=1, N=100, P=1.*/
    public void testF1(int M, int N) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {

        TemporalPoolerFOTest test = new TemporalPoolerFOTest();
        test.initCortex(10, 10, 10, 10, 1);
        Region r = test.neocortex.getRegions().get(0);

        List<Set<Integer>> sequence = getSeqs(M,N);


        /*    for (Set<Integer> patt : sequence) {
                int arr[] = toIntArray(patt);
                BitVector inputvec = new BitVector(arr.length);
                MathUtils.assign(inputvec, arr);

                Method method = Region.class.getDeclaredMethod("overlapPhase", BitVector.class);
                method.setAccessible(true);
                method.invoke(r, inputvec);

                Field field = Region.class.getDeclaredField("overlaps");
                field.setAccessible(true);
                IntMatrix1D overlaps1D = (IntMatrix1D) field.get(r);
                int[] overlaps = overlaps1D.toArray();

                int[] groundtruth = trueOverlaps;
                for (int i = 0; i < groundtruth.length; i++)
                    assertTrue(overlaps[i] == groundtruth[i]);
            }
*/
    }


    /*Same as Test F1, except P=2. The same sequence is presented twice and
we check that synapse permanences are incremented and that no additional
synapses or segments are learned. The test fails if additional synapses or
segments are learned during the second pass.*/
    public void testF1(int M, int N, int P) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {

        TemporalPoolerFOTest test = new TemporalPoolerFOTest();
        test.initCortex(10, 10, 10, 10, 1);
        Region r = test.neocortex.getRegions().get(0);

        List<Set<Integer>> sequence = getSeqs(M,N);

      /*  for(int i=0;i<P) {
            for (Set<Integer> patt : sequence) {
                int arr[] = toIntArray(patt);
                BitVector inputvec = new BitVector(arr.length);
                MathUtils.assign(inputvec, arr);

                Method method = Region.class.getDeclaredMethod("overlapPhase", BitVector.class);
                method.setAccessible(true);
                method.invoke(r, inputvec);

                Field field = Region.class.getDeclaredField("overlaps");
                field.setAccessible(true);
                IntMatrix1D overlaps1D = (IntMatrix1D) field.get(r);
                int[] overlaps = overlaps1D.toArray();

                int[] groundtruth = trueOverlaps;
                for (int i = 0; i < groundtruth.length; i++)
                    assertTrue(overlaps[i] == groundtruth[i]);
            }
        }*/
    }






    private void initCortex(int xInput,int yInput,int xDimension,int yDimension, int cellPerClmn) {
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
        settings.cellsPerColumn=cellPerClmn;

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
