package ru.isa.ai.ourhtm.spatialpooler.tests;

import casmi.matrix.Vector2D;
import cern.colt.matrix.tbit.BitVector;
import junit.framework.TestCase;
import ru.isa.ai.dhm.util.MathUtils;
import ru.isa.ai.ourhtm.algorithms.SpatialPooler;
import ru.isa.ai.ourhtm.structure.HTMSettings;
import ru.isa.ai.ourhtm.structure.Region;

/**
 * Created by APetrov on 15.05.2015.
 */
public class SpatialPoolerTest extends TestCase {

    public void testRun()
    {
        int[] in=new int[]{1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1};
        BitVector input=new BitVector(in.length);
        MathUtils.assign(input, in);

        HTMSettings settings=HTMSettings.getDefaultSettings();
        HTMSettings.debug=true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.historyDeep = 2;
        settings.dutyCyclePeriod = 2;
        settings.desiredLocalActivity = 1;
        settings.cellsPerColumn = 1;
        settings.connectedPct=1;
        settings.xInput=input.size();
        settings.yInput=1;
        settings.potentialRadius=2;
        settings.xDimension=4;
        settings.yDimension=1;

        Region r=new Region(settings);
        SpatialPooler sp=new SpatialPooler(settings);
        int[] overlaps=sp.updateOverlaps(input, r.getColumns());

        int[] groundtruth=new int[]{1,1,1,1, 1,1,1,1, 1,1,1,1, 1,1,1,1};
        for (int i = 0; i < groundtruth.length; i++)
            assertTrue(overlaps[i]==groundtruth[i]);

    }

    public void testHTMConstructuion()
    {
        HTMSettings settings=HTMSettings.getDefaultSettings();
        HTMSettings.debug=true;

        settings.activationThreshold = 1;
        settings.minOverlap = 1;
        settings.historyDeep = 2;
        settings.dutyCyclePeriod = 2;
        settings.desiredLocalActivity = 1;
        settings.cellsPerColumn = 1;
        settings.connectedPct=1;
        settings.xInput=1;
        settings.yInput=1;
        settings.potentialRadius=2;
        settings.xDimension=4;
        settings.yDimension=1;

        Region r=new Region(settings);

        assertTrue(r.getColumns().size()==settings.xDimension*settings.yDimension);
        assertTrue(r.getInputH()==settings.yInput);
        assertTrue(r.getInputW()==settings.xInput);
    }
}
