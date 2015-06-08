package ru.isa.ai;

import junit.framework.TestCase;
import ru.isa.ai.ourhtm.algorithms.Mapper;

import java.util.ArrayList;

/**
 * Created by APetrov on 08.06.2015.
 */
public class MapperTest  extends TestCase {
    public void testRun() {
        ArrayList<int[]> colsMap= Mapper.map(new int[]{5, 1}, new int[]{2, 1});

    }
}
