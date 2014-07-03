package ru.isa.ai.newdhm.visual;

import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import junit.framework.TestCase;
import ru.isa.ai.dhm.MathUtils;
import ru.isa.ai.dhm.poolers.SpatialPooler;
import ru.isa.ai.dhm.poolers.SpatialPoolerInitializationException;
import ru.isa.ai.newdhm.Cortex;
import ru.isa.ai.newdhm.Region;

import java.lang.reflect.Method;

public class CortexTest extends TestCase {
/* what are the tests:
    testRaisePermanencesToThreshold();
    testMapPotential1D();
    testInitPermConnected();
    testInitPermNonConnected();
    testInitPermanence();
    testUpdatePermanencesForColumn();
    testUpdateInhibitionRadius();
    testUpdateMinDutyCycles();
    testUpdateMinDutyCyclesGlobal();
    testUpdateMinDutyCyclesLocal();
    testUpdateDutyCycles();
    testAvgColumnsPerInput();
    testAvgConnectedSpanForColumn1D();
    testAvgConnectedSpanForColumn2D();
    testAvgConnectedSpanForColumnND();
    testAdaptSynapses();
    testBumpUpWeakColumns();
    testUpdateDutyCyclesHelper();
    testUpdateBoostFactors();
    testUpdateBookeepingVars();
    testCalculateOverlap();
    testCalculateOverlapPct();
    testInhibitColumns();
    testIsWinner();
    testAddToWinners();
    testInhibitColumnsGlobal();
    testInhibitColumnsLocal();
    testGetNeighbors1D();
    testGetNeighbors2D();
    testCartesianProduct();
    testGetNeighborsND();
    testIsUpdateRound();
    testSerialize();
 */
public void testUpdateInhibitionRadius()
    {
      /*  Cortex crtx = new Cortex();
        crtx.sInitialization(new int[]{1,1,1}, new int[]{57,31,2});

        Method method = crtx.region.class.getDeclaredMethod("averageReceptiveFieldSize");
        method.setAccessible(true);

        assertEquals(crtx.region.getInhibitionRadius(), 57);

        // avgColumnsPerInput = 4
        // avgConnectedSpanForColumn = 3
        crtx.sInitialization(new int[]{3}, new int[]{12});
        crtx.region.setGlobalInhibition(false);

        for (int i = 0; i < 12; i++) {
            DoubleMatrix1D permArr = new DenseDoubleMatrix1D(3);
            permArr.assign(new double[]{1, 1, 1});
            sp.setPermanence(i, permArr);
        }
        int trueInhibitionRadius = 6;
        // ((3 * 4) - 1)/2 => round up
        method.invoke(crtx.region);
        assertEquals(trueInhibitionRadius, crtx.region.getInhibitionRadius());

        // avgColumnsPerInput = 1.2
        // avgConnectedSpanForColumn = 0.5
        crtx.sInitialization(new int[]{5}, new int[]{6});
        crtx.region.setGlobalInhibition(false);

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
        sp.sInitialization(new int[]{5}, new int[]{12});
        sp.setGlobalInhibition(false);

        for (int i = 0; i < 12; i++) {
            DoubleMatrix1D permArr = new DenseDoubleMatrix1D(5);
            permArr.assign(new double[]{1, 1, 0, 0, 0});
            sp.setPermanence(i, permArr);
        }
        trueInhibitionRadius = 2;
        // ((2.4 * 2) - 1)/2 => round up
        method.invoke(sp);
        assertEquals(trueInhibitionRadius, sp.getInhibitionRadius());*/
    }
}
