package ru.isa.ai.htmviz;

import ru.isa.ai.dhm.core.Cell;
import ru.isa.ai.dhm.core.Column;
import ru.isa.ai.dhm.core.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gmdidro on 02.06.2015.
 */
public class HTMSerialization {

    public class Region{
        public int id;
        public int width;
        public int height;
        public List<Column>cols=new ArrayList<>();
    }

    public class Column{
        public int id;
        public int cellNum;
        public List<Cell> cells=new ArrayList<>();
        public Boolean state;
        public int receptX;
        public int receptY;
        public int receptR;
    }

    public class Cell
    {
        public int id;
        public ru.isa.ai.dhm.core.Cell.State state;
        public List<Dendrite> dendtires=new ArrayList<>();
        public double minThreshold;
    }

    public class Dendrite
    {
        public Cell connection;

    }

    List<Region> regions=new ArrayList<>();

    HashMap<Integer, List<Region>> regionConnection=new HashMap<>();



}
