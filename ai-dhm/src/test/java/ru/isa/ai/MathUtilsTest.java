package ru.isa.ai;

import casmi.matrix.Vector2D;
import junit.framework.Assert;
import junit.framework.TestCase;
import ru.isa.ai.dhm.util.MathUtils;
import ru.isa.ai.ourhtm.algorithms.SimpleMapper;

import java.util.ArrayList;

/**
 * Created by APetrov on 08.06.2015.
 */
public class MathUtilsTest extends TestCase {
    public void testRun() throws Exception {
        Vector2D vec=MathUtils.delinear(4,5);
        Assert.assertTrue(vec.getX()==0);
        Assert.assertTrue(vec.getY()==4);

        double k = MathUtils.distFromCenter(2,2,5,5);
        Assert.assertTrue(k==2.0);

        k = MathUtils.distFromCenter(0,2,5,5);
        Assert.assertTrue(k==Math.sqrt(8));

        k = MathUtils.distFromCenter(0,2,5,1);
        Assert.assertTrue(k==2.0);

        k = MathUtils.distFromCenter(0,2,1,5);
        Assert.assertTrue(k==2.0);

    }


}
