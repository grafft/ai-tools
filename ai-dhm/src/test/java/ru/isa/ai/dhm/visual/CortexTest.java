package ru.isa.ai.dhm.visual;

import junit.framework.TestCase;
import ru.isa.ai.dhm.RegionSettings;
import ru.isa.ai.dhm.core.Column;
import ru.isa.ai.dhm.core.Cortex;

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
        System.out.print("Asdad");

        HTMConfiguration conf=new HTMConfiguration();
        RegionSettings[] set=new RegionSettings[1];
        /*looks like a strange thing for me... */
        set[0]= new RegionSettings();

        //c.sInitializationTest(new int[]{numInputs,1,1}, new int[]{57,31,2});
        // TODO AP: comment by refactoring!
//        set[0].initialParameters[2]=0.5; // connectedPerm
//        set[0].initialParameters[5]=2; // cells per column
//        set[0].initialParameters[10]=57; //xColumnDim
//        set[0].initialParameters[11]=31; //yColumnDim

        Cortex c = new Cortex(1,set);
        int numInputs = 1;

        //sp.setGlobalInhibition(true);
        for (Column col: c.regions[0].columns) {
            double permArr[] = {1};
            c.initSynapsesTest(0, col, numInputs, permArr);
        }
        c.updateInhibitionRadius(0);
        assertEquals(c.regions[0].getInhibitionRadius(), 57);

       /*
        // avgColumnsPerInput = 4
        // avgConnectedSpanForColumn = 3
        numInputs = 3;
        int numCols = 12;
        c.sInitializationTest(new int[]{numInputs,1,1}, new int[]{6,2,2});
        //sp.setGlobalInhibition(false);
        for (Column col: c.regions[0].columns) {
            double permArr[] = {1, 1, 1};
            c.initSynapsesTest(0,col, numInputs, permArr);
        }
        int trueInhibitionRadius = 6;
        // ((3 * 4) - 1)/2 => round up
        c.updateInhibitionRadius(0);
        assertEquals(c.regions[0].getInhibitionRadius(), trueInhibitionRadius);


        // avgColumnsPerInput = 1.2
        // avgConnectedSpanForColumn = 0.5
        numInputs = 5;
        numCols = 6;
        c.sInitializationTest(new int[]{numInputs,1,1}, new int[]{3,2,2});
        //sp.setGlobalInhibition(false);
        int i = 0;
        for (Column col: c.regions[0].columns) {
            double permArr[] = {1, 0, 0, 0, 0};
            if (i % 2 == 0) {
                permArr[0] = 0;
            }
            i++;
            c.initSynapsesTest(0,col, numInputs, permArr);
        }
        trueInhibitionRadius = 1;
        c.updateInhibitionRadius(0);
        assertEquals(c.regions[0].getInhibitionRadius(), trueInhibitionRadius);

        // avgColumnsPerInput = 2.4
        // avgConnectedSpanForColumn = 2
        numInputs = 5;
        numCols = 12;
        c.sInitializationTest(new int[]{numInputs,1,1}, new int[]{6,2,2});
        //sp.setGlobalInhibition(false);

        for (Column col: c.regions[0].columns) {
            double permArr[] = {1, 1, 0, 0, 0};
            c.initSynapsesTest(0,col, numInputs, permArr);
        }
        trueInhibitionRadius = 2;
        // ((2.4 * 2) - 1)/2 => round up
        c.updateInhibitionRadius(0);
        assertEquals(c.regions[0].getInhibitionRadius(), trueInhibitionRadius);
         */
    }

    public void testUpdateMinDutyCycles()
    {/*
        Cortex c = new Cortex();
        int numColumns = 10;
        int numInputs = 5;
        c.sInitializationTest(new int[]{numInputs,1,1}, new int[]{5,2,2});
        double initOverlapDuty[] = {0.01, 0.001, 0.02, 0.3, 0.012, 0.0512, 0.054, 0.221, 0.0873, 0.309};
        double initActiveDuty[] = {0.01, 0.045, 0.812, 0.091, 0.001, 0.0003, 0.433, 0.136, 0.211, 0.129};
        c.region.initParametersForColumns(0.01, 0.02, initOverlapDuty, initActiveDuty );
        //sp.setGlobalInhibition(true);
        c.region.setInhibitionRadius(2);


        sp.updateMinDutyCycles_();
        Real resultMinActive[10];
        Real resultMinOverlap[10];
        sp.getMinOverlapDutyCycles(resultMinOverlap);
        sp.getMinActiveDutyCycles(resultMinActive);


        sp.updateMinDutyCyclesGlobal_();
        Real resultMinActiveGlobal[10];
        Real resultMinOverlapGlobal[10];
        sp.getMinOverlapDutyCycles(resultMinOverlapGlobal);
        sp.getMinActiveDutyCycles(resultMinActiveGlobal);

        sp.updateMinDutyCyclesLocal_();
        Real resultMinActiveLocal[10];
        Real resultMinOverlapLocal[10];
        sp.getMinOverlapDutyCycles(resultMinOverlapLocal);
        sp.getMinActiveDutyCycles(resultMinActiveLocal);


        NTA_CHECK(check_vector_eq(resultMinActive, resultMinActiveGlobal,
                numColumns));
        NTA_CHECK(!check_vector_eq(resultMinActive, resultMinActiveLocal,
                numColumns));
        NTA_CHECK(check_vector_eq(resultMinOverlap, resultMinOverlapGlobal,
                numColumns));
        NTA_CHECK(!check_vector_eq(resultMinActive, resultMinActiveLocal,
                numColumns));

        sp.setGlobalInhibition(false);
        sp.updateMinDutyCycles_();
        sp.getMinOverlapDutyCycles(resultMinOverlap);
        sp.getMinActiveDutyCycles(resultMinActive);

        NTA_CHECK(!check_vector_eq(resultMinActive, resultMinActiveGlobal,
                numColumns));
        NTA_CHECK(check_vector_eq(resultMinActive, resultMinActiveLocal,
                numColumns));
        NTA_CHECK(!check_vector_eq(resultMinOverlap, resultMinOverlapGlobal,
                numColumns));
        NTA_CHECK(check_vector_eq(resultMinActive, resultMinActiveLocal,
                numColumns));
*/
    }

}
