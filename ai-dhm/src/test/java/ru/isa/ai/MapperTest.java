package ru.isa.ai;

import junit.framework.TestCase;
import ru.isa.ai.ourhtm.algorithms.SimpleMapper;
import ru.isa.ai.ourhtm.algorithms.UniformMapper;

import java.util.ArrayList;

/**
 * Created by APetrov on 08.06.2015.
 */
public class MapperTest  extends TestCase {
    public void testRun() {

        SimpleMapperTest();

       /* ArrayList<int[]> colsMap = UniformMapper.map(new int[]{1, 1}, new int[]{1, 1});
        assertTrue(colsMap.get(0)[0]==0 && colsMap.get(0)[1]==0 && colsMap.get(0)[2]==0 && colsMap.get(0)[3]==0);
        colsMap= UniformMapper.map(new int[]{20, 20}, new int[]{1, 1});
        assertTrue(colsMap.get(0)[0]==0 && colsMap.get(0)[1]==0 && colsMap.get(0)[2]==19 && colsMap.get(0)[3]==19);
        colsMap= UniformMapper.map(new int[]{4, 4}, new int[]{2, 2});
        assertTrue(colsMap.get(0)[0]==0 && colsMap.get(0)[1]==0 && colsMap.get(0)[2]==1 && colsMap.get(0)[3]==1);
        assertTrue(colsMap.get(1)[0]==0 && colsMap.get(1)[1]==2 && colsMap.get(1)[2]==1 && colsMap.get(1)[3]==3);
        assertTrue(colsMap.get(2)[0]==2 && colsMap.get(2)[1]==0 && colsMap.get(2)[2]==3 && colsMap.get(2)[3]==1);
        assertTrue(colsMap.get(3)[0]==2 && colsMap.get(3)[1]==2 && colsMap.get(3)[2]==3 && colsMap.get(3)[3]==3);*/
    }

    private void SimpleMapperTest() {

        mapOneTest();


       ArrayList<ArrayList<Integer[]>> colsMap;
        colsMap = SimpleMapper.mapAll(new int[]{3, 1}, new int[]{1, 1}, 1);
        assertTrue(colsMap.get(0).get(0)[0]==0 && colsMap.get(0).get(0)[1]==0 &&
                colsMap.get(0).get(1)[0]==1 && colsMap.get(0).get(1)[1]==0 &&
                colsMap.get(0).get(2)[0]==2 && colsMap.get(0).get(2)[1]==0);

       colsMap = SimpleMapper.mapAll(new int[]{5, 3}, new int[]{2, 2}, 1);
        assertTrue(colsMap.size()==4);
        assertTrue(colsMap.get(0).size()==6);
        assertTrue(colsMap.get(1).size()==9);
        assertTrue(colsMap.get(2).size()==6);
        assertTrue(colsMap.get(3).size()==9);
        assertTrue(colsMap.get(0).get(0)[0]==0 && colsMap.get(0).get(0)[1]==0 &&
                colsMap.get(0).get(5)[0]==2 && colsMap.get(0).get(5)[1]==1);

        assertTrue(colsMap.get(1).get(0)[0]==0 && colsMap.get(1).get(0)[1]==0 &&
                colsMap.get(1).get(8)[0]==2 && colsMap.get(1).get(8)[1]==2);

        assertTrue(colsMap.get(2).get(0)[0]==1 && colsMap.get(2).get(0)[1]==0 &&
                colsMap.get(2).get(5)[0]==3 && colsMap.get(2).get(5)[1]==1);

        assertTrue(colsMap.get(3).get(0)[0]==1 && colsMap.get(3).get(0)[1]==0 &&
                colsMap.get(3).get(8)[0]==3 && colsMap.get(3).get(8)[1]==2);


    }

    private void mapOneTest() {
        ArrayList<Integer[]> colMap;

        colMap = SimpleMapper.mapOne(new int[]{1, 1}, new int[]{1, 1}, 1);
        assertTrue(colMap.get(0)[0]==0 && colMap.get(0)[1]==0);

        colMap = SimpleMapper.mapOne(new int[]{1, 1}, new int[]{1, 1}, 10);
        assertTrue(colMap.get(0)[0]==0 && colMap.get(0)[1]==0);

        colMap = SimpleMapper.mapOne(new int[]{3, 1}, new int[]{1, 1},1);
        assertTrue(colMap.get(0)[0]==0 && colMap.get(0)[1]==0 &&
                   colMap.get(1)[0]==1 && colMap.get(1)[1]==0 &&
                   colMap.get(2)[0]==2 && colMap.get(2)[1]==0);

        colMap = SimpleMapper.mapOne(new int[]{3, 1}, new int[]{2, 1}, 1);
        assertTrue(colMap.get(0)[0]==1 && colMap.get(0)[1]==0 &&
                   colMap.get(1)[0]==2 && colMap.get(1)[1]==0);

        colMap = SimpleMapper.mapOne(new int[]{5, 3}, new int[]{2, 2},2);
        assertTrue(colMap.size()==15);
        assertTrue(colMap.get(0)[0]==0 && colMap.get(0)[1]==0 &&
                   colMap.get(1)[0]==0 && colMap.get(1)[1]==1);
    }
}
