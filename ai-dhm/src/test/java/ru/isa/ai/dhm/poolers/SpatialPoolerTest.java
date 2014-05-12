package ru.isa.ai.dhm.poolers;

import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.tint.IntMatrix1D;
import cern.colt.matrix.tint.impl.DenseIntMatrix1D;
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

    public void testUpdateMinDutyCyclesGlobal() throws SpatialPoolerInitializationException, ReflectiveOperationException {
        Method method = SpatialPooler.class.getDeclaredMethod("updateMinDutyCycles");
        method.setAccessible(true);

        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_def.properties").getPath());
        sp.initialize(new int[]{5}, new int[]{5});
        double minPctOverlap = 0.01;
        double minPctActive = 0.02;
        sp.setMinPctOverlapDutyCycles(minPctOverlap);
        sp.setMinPctActiveDutyCycles(minPctActive);

        DoubleMatrix1D overlapArr1 = new DenseDoubleMatrix1D(new double[]{0.06, 1, 3, 6, 0.5});
        DoubleMatrix1D activeArr1 = new DenseDoubleMatrix1D(new double[]{0.6, 0.07, 0.5, 0.4, 0.3});

        sp.setOverlapDutyCycles(overlapArr1);
        sp.setActiveDutyCycles(activeArr1);

        double trueMinOverlap1 = 0.01 * 6;
        double trueMinActive1 = 0.02 * 0.6;

        sp.setGlobalInhibition(true);
        method.invoke(sp);
        DoubleMatrix1D resultOverlap1 = sp.getMinOverlapDutyCycles();
        DoubleMatrix1D resultActive1 = sp.getMinActiveDutyCycles();
        for (int i = 0; i < 5; i++) {
            assertEquals(resultOverlap1.toArray()[i], trueMinOverlap1);
            assertEquals(resultActive1.toArray()[i], trueMinActive1);
        }

        minPctOverlap = 0.015;
        minPctActive = 0.03;
        sp.setMinPctOverlapDutyCycles(minPctOverlap);
        sp.setMinPctActiveDutyCycles(minPctActive);

        DoubleMatrix1D overlapArr2 = new DenseDoubleMatrix1D(new double[]{0.86, 2.4, 0.03, 1.6, 1.5});
        DoubleMatrix1D activeArr2 = new DenseDoubleMatrix1D(new double[]{0.16, 0.007, 0.15, 0.54, 0.13});
        sp.setOverlapDutyCycles(overlapArr2);
        sp.setActiveDutyCycles(activeArr2);

        double trueMinOverlap2 = 0.015 * 2.4;
        double trueMinActive2 = 0.03 * 0.54;

        method.invoke(sp);
        DoubleMatrix1D resultOverlap2 = sp.getMinOverlapDutyCycles();
        DoubleMatrix1D resultActive2 = sp.getMinActiveDutyCycles();
        for (int i = 0; i < 5; i++) {
            assertTrue(MathUtils.almostEquals(resultOverlap2.toArray()[i], trueMinOverlap2));
            assertTrue(MathUtils.almostEquals(resultActive2.toArray()[i], trueMinActive2));
        }


        minPctOverlap = 0.015;
        minPctActive = 0.03;
        sp.setMinPctOverlapDutyCycles(minPctOverlap);
        sp.setMinPctActiveDutyCycles(minPctActive);

        DoubleMatrix1D overlapArr3 = new DenseDoubleMatrix1D(new double[]{0, 0, 0, 0, 0});
        DoubleMatrix1D activeArr3 = new DenseDoubleMatrix1D(new double[]{0, 0, 0, 0, 0});
        sp.setOverlapDutyCycles(overlapArr3);
        sp.setActiveDutyCycles(activeArr3);

        double trueMinOverlap3 = 0;
        double trueMinActive3 = 0;

        method.invoke(sp);
        DoubleMatrix1D resultOverlap3 = sp.getMinOverlapDutyCycles();
        DoubleMatrix1D resultActive3 = sp.getMinActiveDutyCycles();
        for (int i = 0; i < 5; i++) {
            assertTrue(MathUtils.almostEquals(resultOverlap3.toArray()[i], trueMinOverlap3));
            assertTrue(MathUtils.almostEquals(resultActive3.toArray()[i], trueMinActive3));
        }
    }

    public void testUpdateMinDutyCyclesLocal() throws SpatialPoolerInitializationException, ReflectiveOperationException {
        Method method = SpatialPooler.class.getDeclaredMethod("updateMinDutyCycles");
        method.setAccessible(true);

        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_def.properties").getPath());
        sp.initialize(new int[]{5}, new int[]{8});
        sp.setMinPctActiveDutyCycles(0.1);
        sp.setMinPctOverlapDutyCycles(0.2);
        DoubleMatrix1D overlapArr1 = new DenseDoubleMatrix1D(new double[]{0.7, 0.1, 0.5, 0.01, 0.78, 0.55, 0.1, 0.001});
        DoubleMatrix1D activeArr1 = new DenseDoubleMatrix1D(new double[]{0.9, 0.3, 0.5, 0.7, 0.1, 0.01, 0.08, 0.12});
        sp.setOverlapDutyCycles(overlapArr1);
        sp.setActiveDutyCycles(activeArr1);
        sp.setInhibitionRadius(1);

        sp.setGlobalInhibition(false);
        method.invoke(sp);

        DoubleMatrix1D trueOverlapArr = new DenseDoubleMatrix1D(new double[]{0.14, 0.14, 0.1, 0.156, 0.156, 0.156, 0.11, 0.02});
        DoubleMatrix1D trueActiveArr = new DenseDoubleMatrix1D(new double[]{0.09, 0.09, 0.07, 0.07, 0.07, 0.01, 0.012, 0.012});

        DoubleMatrix1D resultOverlap1 = sp.getMinOverlapDutyCycles();
        DoubleMatrix1D resultActive1 = sp.getMinActiveDutyCycles();

        assertTrue(MathUtils.almostEquals(resultOverlap1.toArray(), trueOverlapArr.toArray()));
        assertTrue(MathUtils.almostEquals(resultActive1.toArray(), trueActiveArr.toArray()));
    }

    public void testUpdateDutyCycles() throws SpatialPoolerInitializationException, ReflectiveOperationException {
        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_def.properties").getPath());
        sp.initialize(new int[]{5}, new int[]{5});

        Method method = SpatialPooler.class.getDeclaredMethod("updateDutyCycles", IntMatrix1D.class, BitVector.class);
        method.setAccessible(true);

        DoubleMatrix1D initOverlapArr1 = new DenseDoubleMatrix1D(new double[]{1, 1, 1, 1, 1});
        sp.setOverlapDutyCycles(initOverlapArr1.copy());
        IntMatrix1D overlaps = new DenseIntMatrix1D(new int[]{1, 5, 7, 0, 0});
        BitVector active = new BitVector(5);

        sp.setIterationNum(2);
        method.invoke(sp, overlaps, active);

        DoubleMatrix1D resultOverlapArr1 = sp.getOverlapDutyCycles();
        double[] trueOverlapArr1 = {1, 1, 1, 0.5, 0.5};
        assertTrue(MathUtils.almostEquals(resultOverlapArr1.toArray(), trueOverlapArr1));

        sp.setOverlapDutyCycles(initOverlapArr1.copy());
        sp.setIterationNum(2000);
        sp.setUpdatePeriod(1000);
        method.invoke(sp, overlaps, active);

        DoubleMatrix1D resultOverlapArr2 = sp.getOverlapDutyCycles();
        double trueOverlapArr2[] = {1, 1, 1, 0.999, 0.999};

        assertTrue(MathUtils.almostEquals(resultOverlapArr2.toArray(), trueOverlapArr2));
    }

    public void testAvgColumnsPerInput() throws SpatialPoolerInitializationException, ReflectiveOperationException {
        Method method = SpatialPooler.class.getDeclaredMethod("avgColumnsPerInput");
        method.setAccessible(true);

        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_def.properties").getPath());
        sp.initialize(new int[]{4, 4, 4, 4}, new int[]{2, 2, 2, 2});

        double result = (Double) method.invoke(sp);
        assertEquals(0.5, result);

        sp.initialize(new int[]{7, 5, 1, 3}, new int[]{2, 2, 2, 2});
        result = (Double) method.invoke(sp);
        assertEquals((2.0 / 7 + 2.0 / 5 + 2.0 / 1 + 2 / 3.0) / 4, result);

        sp.initialize(new int[]{3, 3}, new int[]{3, 3});
        result = (Double) method.invoke(sp);
        assertEquals(1.0, result);

        sp.initialize(new int[]{5}, new int[]{25});
        result = (Double) method.invoke(sp);
        assertEquals(5.0, result);

        sp.initialize(new int[]{3, 5, 6}, new int[]{3, 5, 6});
        result = (Double) method.invoke(sp);
        assertEquals(1.0, result);

        sp.initialize(new int[]{2, 2, 2, 2}, new int[]{2, 4, 6, 8});
        result = (Double) method.invoke(sp);
        assertEquals(2.5, result);

    }

    public void testAvgConnectedSpanForColumnND() throws SpatialPoolerInitializationException, ReflectiveOperationException {
        Method method = SpatialPooler.class.getDeclaredMethod("avgConnectedSpanForColumnND", int.class);
        method.setAccessible(true);

        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_def.properties").getPath());
        sp.initialize(new int[]{4, 4, 2, 5}, new int[]{5});

        int numInputs = 160;
        int numColumns = 5;

        DoubleMatrix1D permArr0 = new DenseDoubleMatrix1D(numInputs);
        permArr0.setQuick(40 + 5, 1); // permArr0[1][0][1][0] = 1;
        permArr0.setQuick(40 + 5 + 1, 1); // permArr0[1][0][1][1] = 1;
        permArr0.setQuick(3 * 40 + 2 * 10 + 5, 1); // permArr0[3][2][1][0] = 1;
        permArr0.setQuick(3 * 40 + 5, 1); // permArr0[3][0][1][0] = 1;
        permArr0.setQuick(40 + 5 + 3, 1); // permArr0[1][0][1][3] = 1;
        permArr0.setQuick(40 + 2 * 10 + 5, 1); // permArr0[2][2][1][0] = 1;


        DoubleMatrix1D permArr1 = new DenseDoubleMatrix1D(numInputs);
        permArr1.setQuick(2 * 40 + 5, 1); // permArr1[2][0][1][0] = 1;
        permArr1.setQuick(2 * 40, 1); // permArr1[2][0][0][0] = 1;
        permArr1.setQuick(3 * 40, 1); // permArr1[3][0][0][0] = 1;
        permArr1.setQuick(3 * 40 + 5, 1); // permArr1[3][0][1][0] = 1;

        DoubleMatrix1D permArr2 = new DenseDoubleMatrix1D(numInputs);
        permArr2.setQuick(5 + 4, 1); // permArr2[0][0][1][4] = 1;
        permArr2.setQuick(3, 1); // permArr2[0][0][0][3] = 1;
        permArr2.setQuick(1, 1); // permArr2[0][0][0][1] = 1;
        permArr2.setQuick(40 + 2, 1); // permArr2[1][0][0][2] = 1;
        permArr2.setQuick(5 + 1, 1); // permArr2[0][0][1][1] = 1;
        permArr2.setQuick(3 * 40 + 3 * 10 + 5 + 1, 1); // permArr2[3][3][1][1] = 1;

        DoubleMatrix1D permArr3 = new DenseDoubleMatrix1D(numInputs);
        permArr3.setQuick(3 * 40 + 3 * 10 + 5 + 4, 1); // permArr3[3][3][1][4] = 1;
        permArr3.setQuick(0, 1); // permArr3[0][0][0][0] = 1;

        sp.setPermanence(0, permArr0);
        sp.setPermanence(1, permArr1);
        sp.setPermanence(2, permArr2);
        sp.setPermanence(3, permArr3);
        sp.setPermanence(4, new DenseDoubleMatrix1D(numInputs));

        double[] trueAvgConnectedSpan = new double[]{11.0 / 4, 6.0 / 4, 14.0 / 4, 15.0 / 4, 0};

        for (int i = 0; i < numColumns; i++) {
            method.invoke(sp, i);
            double result = (double) method.invoke(sp, i);
            assertEquals(trueAvgConnectedSpan[i], result);
        }

    }

//    public void test() throws SpatialPoolerInitializationException, ReflectiveOperationException {
//        Method method = SpatialPooler.class.getDeclaredMethod("avgColumnsPerInput");
//        method.setAccessible(true);
//
//        SpatialPooler sp = new SpatialPooler(getClass().getClassLoader().getResource("dhm_sp_def.properties").getPath());
//        sp.initialize(new int[]{1}, new int[]{57, 31, 2});
//
//        method.invoke(sp);
//    }
}
