package ru.isa.ai.htmviz;

//import ru.isa.ai.dhm.DHMSettings;
//import ru.isa.ai.dhm.core.Cell;
//import ru.isa.ai.dhm.core.Column;
//import ru.isa.ai.dhm.core.Region;
import ru.isa.ai.ourhtm.structure.HTMSettings;
import ru.isa.ai.ourhtm.structure.Synapse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gmdidro on 02.06.2015.
 * На данный момент эти классы не хранят историю состояний клеток\колонок
 */
public class HTMSerialization {

    public class Region{
        public Region(int id) {
            this.id = id;
        }
        public int id;
        public int width;
        public int height;
        public List<Column>cols=new ArrayList<>();
        public HTMSettings setting;
    }

    public class Column{
        public Column(int id){
            this.id=id;
        }
        public int id;
        public int cellNum;
        public List<Cell> cells=new ArrayList<>();
        public Boolean state;
        public int receptX;
        public int receptY;
        public int receptR;
        public int activeDutyCycle = 0;
        public int overlapDutyCycle = 0;

        public HashMap<Integer,Synapse> potentialSynapses =new HashMap<>();
        public int overlap;
        public double boostFactor = 1;
    }

    public class Cell {
        public Cell(int id) {
            this.id = id;
        }

        public int id;
        public ru.isa.ai.dhm.core.Cell.State state;
        public List<Synapse> synapces = new ArrayList<>();
        public double minThreshold;
    }



    public class Synapse
    {
        public Synapse(int sourceIndex,double permanence)
        {
           this.sourceIndex=sourceIndex;
            this.permanence=permanence;
        }
        public int sourceIndex;
        public double permanence;
    }

    List<Region> regions=new ArrayList<>();

    HashMap<Integer, List<Region>> regionConnection=new HashMap<>();
}
