package ru.isa.ai;

import casmi.matrix.Vector2D;
import junit.framework.TestCase;
import ru.isa.ai.ourhtm.algorithms.SimpleMapper;
import ru.isa.ai.ourhtm.algorithms.UniformMapper;

import java.util.ArrayList;

/**
 * Created by APetrov on 08.06.2015.
 */
public class MapperTest  extends TestCase {
    public void testRun() throws Exception {

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

    private void SimpleMapperTest() throws Exception{

        mapOneTest();


       ArrayList<ArrayList<Vector2D>> colsMap;
        colsMap = SimpleMapper.mapAll(new int[]{3, 1}, new int[]{1, 1}, 1);
        assertTrue(colsMap.get(0).get(0).getX()==0 && colsMap.get(0).get(0).getY()==0 &&
                colsMap.get(0).get(1).getX()==1 && colsMap.get(0).get(1).getY()==0 &&
                colsMap.get(0).get(2).getX()==2 && colsMap.get(0).get(2).getY()==0);

       colsMap = SimpleMapper.mapAll(new int[]{5, 3}, new int[]{2, 2}, 1);
        assertTrue(colsMap.size()==4);
        assertTrue(colsMap.get(0).size()==6);
        assertTrue(colsMap.get(1).size()==9);
        assertTrue(colsMap.get(2).size()==6);
        assertTrue(colsMap.get(3).size()==9);
        assertTrue(colsMap.get(0).get(0).getX()==0 && colsMap.get(0).get(0).getY()==0 &&
                colsMap.get(0).get(5).getX()==2 && colsMap.get(0).get(5).getY()==1);

        assertTrue(colsMap.get(1).get(0).getX()==0 && colsMap.get(1).get(0).getY()==0 &&
                colsMap.get(1).get(8).getX()==2 && colsMap.get(1).get(8).getY()==2);

        assertTrue(colsMap.get(2).get(0).getX()==1 && colsMap.get(2).get(0).getY()==0 &&
                colsMap.get(2).get(5).getX()==3 && colsMap.get(2).get(5).getY()==1);

        assertTrue(colsMap.get(3).get(0).getX()==1 && colsMap.get(3).get(0).getY()==0 &&
                colsMap.get(3).get(8).getX()==3 && colsMap.get(3).get(8).getY()==2);

        colsMap = SimpleMapper.mapAll(new int[]{2, 2}, new int[]{2, 2}, 1);
        assertTrue(colsMap.size()==4);
        assertTrue(colsMap.get(0).size()==4);
        assertTrue(colsMap.get(1).size()==4);
        assertTrue(colsMap.get(2).size()==4);
        assertTrue(colsMap.get(3).size()==4);

        try {
            colsMap = SimpleMapper.mapAll(new int[]{2, 2}, new int[]{4, 4}, 1);
            assertTrue(false);
        }
        catch (Exception e)
        {
            assertTrue(true);
        }

        colsMap = SimpleMapper.mapAll(new int[]{16, 1}, new int[]{4, 1}, 2);
        assertTrue(colsMap.size()==4);
        assertTrue(colsMap.get(0).size()==5);
        assertTrue(colsMap.get(1).size()==5);
        assertTrue(colsMap.get(2).size()==5);
        assertTrue(colsMap.get(3).size()==5);


        colsMap = SimpleMapper.mapAll(new int[]{2, 2}, new int[]{2, 2}, 1);
        assertTrue(colsMap.size()==4);
        assertTrue(colsMap.get(0).size()==4);
        assertTrue(colsMap.get(1).size()==4);
        assertTrue(colsMap.get(2).size()==4);
        assertTrue(colsMap.get(3).size()==4);

        try {
            colsMap = SimpleMapper.mapAll(new int[]{2, 2}, new int[]{4, 4}, 1);
            assertTrue(false);
        }
        catch (Exception e)
        {
            assertTrue(true);
        }



    }

    private void mapOneTest() {
        ArrayList<Vector2D> colMap;

        colMap = SimpleMapper.mapOne(new int[]{1, 1}, new int[]{1, 1}, 1);
        assertTrue(colMap.get(0).getX()==0 && colMap.get(0).getY()==0);

        colMap = SimpleMapper.mapOne(new int[]{1, 1}, new int[]{1, 1}, 10);
        assertTrue(colMap.get(0).getX()==0 && colMap.get(0).getY()==0);

        colMap = SimpleMapper.mapOne(new int[]{3, 1}, new int[]{1, 1},1);
        assertTrue(colMap.get(0).getX()==0 && colMap.get(0).getY()==0 &&
                   colMap.get(1).getX()==1 && colMap.get(1).getY()==0 &&
                   colMap.get(2).getX()==2 && colMap.get(2).getY()==0);

        colMap = SimpleMapper.mapOne(new int[]{3, 1}, new int[]{2, 1}, 1);
        assertTrue(colMap.get(0).getX()==1 && colMap.get(0).getY()==0 &&
                   colMap.get(1).getX()==2 && colMap.get(1).getY()==0);

        colMap = SimpleMapper.mapOne(new int[]{5, 3}, new int[]{2, 2},2);
        assertTrue(colMap.size()==15);
        assertTrue(colMap.get(0).getX()==0 && colMap.get(0).getY()==0 &&
                   colMap.get(1).getX()==0 && colMap.get(1).getY()==1);
    }
}
