package ru.isa.ai.dhm.poolers;

import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ru.isa.ai.dhm.MathUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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

    public void testRaisePermanencesToThreshold() throws SpatialPoolerInitializationException, ReflectiveOperationException {
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

    public void testMapPotential1D() throws SpatialPoolerInitializationException, ReflectiveOperationException {
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

    public void testInitPermanence() throws SpatialPoolerInitializationException, ReflectiveOperationException {
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
        potential = new BitVector(100);
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

    public void testUpdatePermanencesForColumn() throws SpatialPoolerInitializationException, ReflectiveOperationException {
        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_upfc.properties").getPath());
        sp.initialize(new int[]{5}, new int[]{5});

        double[][] permArr =
                {
                        {-0.10, 0.500, 0.400, 0.010, 0.020},
                        {0.300, 0.010, 0.020, 0.120, 0.090},
                        {0.070, 0.050, 1.030, 0.190, 0.060},
                        {0.180, 0.090, 0.110, 0.010, 0.030},
                        {0.200, 0.101, 0.050, -0.09, 1.100}
                };

        double[][] truePerm =
                {
                        {0.000, 0.500, 0.400, 0.000, 0.000},
                        // Clip     -     -      Trim   Trim
                        {0.300, 0.000, 0.000, 0.120, 0.090},
                        // -    Trim   Trim   -     -
                        {0.070, 0.050, 1.000, 0.190, 0.060},
                        // -     -   Clip   -     -
                        {0.180, 0.090, 0.110, 0.000, 0.000},
                        // -     -    -      Trim   Trim
                        {0.200, 0.101, 0.050, 0.000, 1.000}
                        // -      -     -      Clip   Clip
                };
        int[][] trueConnectedSynapses =
                {
                        {0, 1, 1, 0, 0},
                        {1, 0, 0, 1, 0},
                        {0, 0, 1, 1, 0},
                        {1, 0, 1, 0, 0},
                        {1, 1, 0, 0, 1}
                };

        int[] trueConnectedCount = {2, 2, 2, 2, 3};

        Method method = SpatialPooler.class.getDeclaredMethod("updatePermanencesForColumn", DoubleMatrix1D.class, int.class, boolean.class);
        method.setAccessible(true);

        for (int i = 0; i < 5; i++) {
            DoubleMatrix1D perm = new DenseDoubleMatrix1D(5);
            perm.assign(permArr[i]);
            method.invoke(sp, perm, i, false);

            DoubleMatrix1D permVect = MathUtils.getRow(sp.getPermanences(), i);
            BitVector connectedArr = MathUtils.getRow(sp.getConnectedSynapses(), i);
            List<Integer> connectedCountsArr = sp.getConnectedCounts();
            assertTrue(Arrays.equals(truePerm[i], permVect.toArray()));
            assertTrue(MathUtils.equals(trueConnectedSynapses[i], connectedArr));
            assertEquals(trueConnectedCount[i], (int) connectedCountsArr.get(i));
        }

    }

    public void testUpdateInhibitionRadius() throws SpatialPoolerInitializationException, ReflectiveOperationException {
        Method method = SpatialPooler.class.getDeclaredMethod("updateInhibitionRadius");
        method.setAccessible(true);

        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_uir.properties").getPath());
        sp.initialize(new int[]{1}, new int[]{57, 31, 2});

        assertEquals(sp.getInhibitionRadius(), 57);

        // avgColumnsPerInput = 4
        // avgConnectedSpanForColumn = 3
        sp.initialize(new int[]{3}, new int[]{12});
        sp.setGlobalInhibition(false);

        for (int i = 0; i < 12; i++) {
            DoubleMatrix1D permArr = new DenseDoubleMatrix1D(3);
            permArr.assign(new double[]{1, 1, 1});
            sp.setPermanence(i, permArr);
        }
        int trueInhibitionRadius = 6;
        // ((3 * 4) - 1)/2 => round up
        method.invoke(sp);
        assertEquals(trueInhibitionRadius, sp.getInhibitionRadius());

        // avgColumnsPerInput = 1.2
        // avgConnectedSpanForColumn = 0.5
        sp.initialize(new int[]{5}, new int[]{6});
        sp.setGlobalInhibition(false);

        for (int i = 0; i < 6; i++) {
            DoubleMatrix1D permArr = new DenseDoubleMatrix1D(5);
            permArr.assign(new double[]{i % 2 == 0 ? 1 : 0, 0, 0, 0, 0});
            sp.setPermanence(i, permArr);
        }
        trueInhibitionRadius = 1;
        method.invoke(sp);
        assertEquals(trueInhibitionRadius, sp.getInhibitionRadius());

        // avgColumnsPerInput = 2.4
        // avgConnectedSpanForColumn = 2
        sp.initialize(new int[]{5}, new int[]{12});
        sp.setGlobalInhibition(false);

        for (int i = 0; i < 12; i++) {
            DoubleMatrix1D permArr = new DenseDoubleMatrix1D(5);
            permArr.assign(new double[]{1, 1, 0, 0, 0});
            sp.setPermanence(i, permArr);
        }
        trueInhibitionRadius = 2;
        // ((2.4 * 2) - 1)/2 => round up
        method.invoke(sp);
        assertEquals(trueInhibitionRadius, sp.getInhibitionRadius());
    }

    public void testUpdateMinDutyCycles() throws SpatialPoolerInitializationException, ReflectiveOperationException {
        Method method = SpatialPooler.class.getDeclaredMethod("updateMinDutyCycles", DoubleMatrix1D.class, int.class, boolean.class);
        method.setAccessible(true);

        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_def.properties").getPath());
        sp.initialize(new int[]{5}, new int[]{10});

        sp.setMinPctOverlapDutyCycles(0.01);
        sp.setMinPctActiveDutyCycles(0.02);

        double[] initOverlapDuty = {0.01, 0.001, 0.02, 0.3, 0.012, 0.0512,
                0.054, 0.221, 0.0873, 0.309};

        double[] initActiveDuty = {0.01, 0.045, 0.812, 0.091, 0.001, 0.0003,
                0.433, 0.136, 0.211, 0.129};

//        sp.setOverlapDutyCycles(initOverlapDuty);
//        sp.setActiveDutyCycles(initActiveDuty);
//        sp.setGlobalInhibition(true);
//        sp.setInhibitionRadius(2);
//        sp.updateMinDutyCycles_();
//        Real resultMinActive[ 10];
//        Real resultMinOverlap[ 10];
//        sp.getMinOverlapDutyCycles(resultMinOverlap);
//        sp.getMinActiveDutyCycles(resultMinActive);
//
//
//        sp.updateMinDutyCyclesGlobal_();
//        Real resultMinActiveGlobal[ 10];
//        Real resultMinOverlapGlobal[ 10];
//        sp.getMinOverlapDutyCycles(resultMinOverlapGlobal);
//        sp.getMinActiveDutyCycles(resultMinActiveGlobal);
//
//        sp.updateMinDutyCyclesLocal_();
//        Real resultMinActiveLocal[ 10];
//        Real resultMinOverlapLocal[ 10];
//        sp.getMinOverlapDutyCycles(resultMinOverlapLocal);
//        sp.getMinActiveDutyCycles(resultMinActiveLocal);
//
//
//        NTA_CHECK(check_vector_eq(resultMinActive, resultMinActiveGlobal,
//                numColumns));
//        NTA_CHECK(!check_vector_eq(resultMinActive, resultMinActiveLocal,
//                numColumns));
//        NTA_CHECK(check_vector_eq(resultMinOverlap, resultMinOverlapGlobal,
//                numColumns));
//        NTA_CHECK(!check_vector_eq(resultMinActive, resultMinActiveLocal,
//                numColumns));
//
//        sp.setGlobalInhibition(false);
//        sp.updateMinDutyCycles_();
//        sp.getMinOverlapDutyCycles(resultMinOverlap);
//        sp.getMinActiveDutyCycles(resultMinActive);
//
//        NTA_CHECK(!check_vector_eq(resultMinActive, resultMinActiveGlobal,
//                numColumns));
//        NTA_CHECK(check_vector_eq(resultMinActive, resultMinActiveLocal,
//                numColumns));
//        NTA_CHECK(!check_vector_eq(resultMinOverlap, resultMinOverlapGlobal,
//                numColumns));
//        NTA_CHECK(check_vector_eq(resultMinActive, resultMinActiveLocal,
//                numColumns));
    }

//    public void test() throws SpatialPoolerInitializationException, ReflectiveOperationException {
//        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_upfc.properties").getPath());
//        sp.initialize(new int[]{1}, new int[]{57, 31, 2});
//
//        Method method = SpatialPooler.class.getDeclaredMethod("updatePermanencesForColumn", DoubleMatrix1D.class, int.class, boolean.class);
//        method.setAccessible(true);
//
//        assertEquals(sp.getInhibitionRadius(), 57);
//    }
}
