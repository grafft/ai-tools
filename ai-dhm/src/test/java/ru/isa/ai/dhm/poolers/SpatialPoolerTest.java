package ru.isa.ai.dhm.poolers;

import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ru.isa.ai.dhm.MathUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Author: Aleksandr Panov
 * Date: 08.05.2014
 * Time: 13:06
 */
public class SpatialPoolerTest extends TestCase {

    public SpatialPoolerTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(SpatialPoolerTest.class);
    }

    public void testRaisePermanencesToThreshold() throws SpatialPoolerInitializationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_rptt.properties").getPath());
        int numInputs = 5;
        int numColumns = 7;
        sp.initialize(new int[]{numInputs}, new int[]{numColumns});


        int[][] potentialArr =
                {
                        {1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1},
                        {1, 1, 0, 0, 1},
                        {0, 1, 1, 1, 0}
                };


        double[][] permArr =
                {
                        {0.0, 0.11, 0.095, 0.092, 0.01},
                        {0.12, 0.15, 0.02, 0.12, 0.09},
                        {0.51, 0.081, 0.025, 0.089, 0.31},
                        {0.18, 0.0601, 0.11, 0.011, 0.03},
                        {0.011, 0.011, 0.011, 0.011, 0.011},
                        {0.12, 0.056, 0, 0, 0.078},
                        {0, 0.061, 0.07, 0.14, 0}
                };

        double[][] truePerm =
                {
                        {0.01, 0.12, 0.105, 0.102, 0.02},  // incremented once
                        {0.12, 0.15, 0.02, 0.12, 0.09},  // no change
                        {0.53, 0.101, 0.045, 0.109, 0.33},  // increment twice
                        {0.22, 0.1001, 0.15, 0.051, 0.07},  // increment four times
                        {0.101, 0.101, 0.101, 0.101, 0.101},  // increment 9 times
                        {0.17, 0.106, 0, 0, 0.128},  // increment 5 times
                        {0, 0.101, 0.11, 0.18, 0} // increment 4 times
                };


        int[] trueConnectedCount = {3, 3, 4, 3, 5, 3, 3};

        for (int i = 0; i < numColumns; i++) {
            DoubleMatrix1D perm = new DenseDoubleMatrix1D(numInputs);
            perm.assign(permArr[i]);
            BitVector potential = new BitVector(numInputs);
            for (int j = 0; j < numInputs; j++) {
                if (potentialArr[i][j] > 0) {
                    potential.set(j);
                }
            }

            Method method = SpatialPooler.class.getDeclaredMethod("raisePermanencesToThreshold", DoubleMatrix1D.class, BitVector.class);
            method.setAccessible(true);
            int connected = (Integer) method.invoke(sp, perm, potential);
            assertTrue("Equality permanence for column " + i, MathUtils.almostEquals(truePerm[i], perm.toArray()));
            assertEquals("Equality connected for column " + i, connected, trueConnectedCount[i]);

        }

    }

    public void testMapPotential1D() throws SpatialPoolerInitializationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_mp1d.properties").getPath());
        sp.initialize(new int[]{10}, new int[]{4});

        Method method = SpatialPooler.class.getDeclaredMethod("mapPotential1D", int.class, boolean.class);
        method.setAccessible(true);

        // Test without wrapAround and potentialPct = 1
        int[] expectedMask1 = {1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
        BitVector mask = (BitVector) method.invoke(sp, 0, false);
        assertTrue(MathUtils.equals(expectedMask1, mask));

        int[] expectedMask2 = {0, 0, 0, 0, 1, 1, 1, 1, 1, 0};
        mask = (BitVector) method.invoke(sp, 2, false);
        assertTrue(MathUtils.equals(expectedMask2, mask));

        // Test with wrapAround and potentialPct = 1
        sp.setPotentialPct(1.0);

        int[] expectedMask3 = {1, 1, 1, 0, 0, 0, 0, 0, 1, 1};
        mask = (BitVector) method.invoke(sp, 0, true);
        assertTrue(MathUtils.equals(expectedMask3, mask));

        int[] expectedMask4 = {1, 1, 0, 0, 0, 0, 0, 1, 1, 1};
        mask = (BitVector) method.invoke(sp, 3, true);
        assertTrue(MathUtils.equals(expectedMask4, mask));

        // Test with potentialPct < 1
        sp.setPotentialPct(0.5);
        int[] supersetMask1 = {1, 1, 1, 0, 0, 0, 0, 0, 1, 1};
        mask = (BitVector) method.invoke(sp, 0, true);
        assertEquals(mask.cardinality(), 3);

        int[] unionMask1 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 10; i++) {
            unionMask1[i] = supersetMask1[i] | (mask.get(i) ? 1 : 0);
        }

        assertTrue(Arrays.equals(unionMask1, supersetMask1));
    }

    public void testInitPermanence() throws SpatialPoolerInitializationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_ip.properties").getPath());
        sp.initialize(new int[]{8}, new int[]{2});

        Method method = SpatialPooler.class.getDeclaredMethod("initPermanence", BitVector.class, double.class);
        method.setAccessible(true);

        int[] arr = {0, 1, 1, 0, 0, 1, 0, 1};

        BitVector potential = new BitVector(8);
        MathUtils.assign(potential, arr);
        DoubleMatrix1D perm = (DoubleMatrix1D) method.invoke(sp, potential, 1.0);
        for (int i = 0; i < 8; i++)
            if (potential.get(i))
                assertTrue(perm.getQuick(i) >= sp.getSynPermConnected());
            else
                assertTrue(perm.getQuick(i) < 1e-5);

        perm = (DoubleMatrix1D) method.invoke(sp, potential, 0);
        for (int i = 0; i < 8; i++)
            if (potential.get(i))
                assertTrue(perm.getQuick(i) <= sp.getSynPermConnected());
            else
                assertTrue(perm.getQuick(i) < 1e-5);

        sp.initialize(new int[]{100}, new int[]{2});
        potential.replaceFromToWith(0, potential.size() - 1, true);

        perm = (DoubleMatrix1D) method.invoke(sp, potential, 0.5);
        int count = 0;
        for (int i = 0; i < 100; i++) {
            double value = perm.getQuick(i);
            assertTrue(value < 1e-5 || value >= sp.getSynPermTrimThreshold());
            if (value >= sp.getSynPermConnected())
                count++;
        }
        assertTrue(count > 5 && count < 95);
    }

}
