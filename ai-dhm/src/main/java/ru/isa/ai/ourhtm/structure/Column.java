package ru.isa.ai.ourhtm.structure;



import casmi.matrix.Vector2D;
import ru.isa.ai.ourhtm.util.MathUtils;

import java.util.*;

/**
 * Created by APetrov on 13.05.2015.
 */
public class Column {

    HTMSettings set;
    Vector2D col_coords;
    public Column(int[] coords, ArrayList<Vector2D> bottomIndices, Region region, HTMSettings set)
    {
        this.set = set;
        this.bottomIndices=bottomIndices;
        this.potentialRadius= set.potentialRadius;
        this.connectedPct= set.connectedPct;
        r=region;
        col_coords=new Vector2D(coords[0],coords[1]);
        cells=new Cell[set.cellsPerColumn];
        proximalDendrite.initSynapses();
        updateNeighbors(set.initialInhibitionRadius);
    }

    public int getIndex()
    {
        return (int)col_coords.getX()*Column.this.set.yDimension+(int)col_coords.getY();
    }

    public ProximalDendrite getProximalDendrite() { return proximalDendrite; }


    private Region r;
    private Cell[] cells; // клетки данной колонки
    private ProximalDendrite proximalDendrite=new ProximalDendrite();;
    private ArrayList<Vector2D> bottomIndices;
    private List<Integer> neighbors = new ArrayList<>();  // колонки, находящиеся в радиусе подавления данной
    private int potentialRadius;
    private double connectedPct;
    private boolean isActive;

    /**
     * Изменение списка соседних колонок, которые отстоят от данной в круге радиусом inhibitionRadius
     * @param inhibitionRadius радиус подавления (в начале назначется из настроек, потом берется как усредненный радиус рецептивного поля)
     */
    public void updateNeighbors(int inhibitionRadius) {
        neighbors.clear();
        for (int k = (int)col_coords.getX() - inhibitionRadius; k <= (col_coords.getX() + inhibitionRadius); k++) {
            if (k >= 0 && k < set.xDimension) {
                for (int m = (int)col_coords.getY() - inhibitionRadius; m <= (col_coords.getY() + inhibitionRadius); m++) {
                    if (m >= 0 && m < set.yDimension) {
                        if((k!=(int)col_coords.getX())||(m!=(int)col_coords.getY()))
                            neighbors.add(k * set.yDimension + m);
                    }
                }
            }
        }
    }
    public List<Integer> getNeighbors() {
        return neighbors;
    }

    public void setIsActive(boolean isActive)
    {
        this.isActive=isActive;
    }
    public boolean isActive()
    {
        return isActive;
    }

    public Vector2D getCoord()
    {
        return col_coords;
    }

    public class ProximalDendrite {

        // хэш синапсов: индекс элемента с которым соединение и сам синапс
        HashMap<Integer,Synapse> potentialSynapses =new HashMap<>();

        private double boostFactor = 1;
        private Random random = new Random();

        public HashMap<Integer,Synapse> getPotentialSynapses()
        {
            return potentialSynapses;
        }

        public ArrayList<Synapse> getConnectedSynapses()
        {
            ArrayList<Synapse> conn_syn=new ArrayList<>(10);
            for(Synapse s : potentialSynapses.values())
                if(s.isConnected())
                    conn_syn.add(s);
            return  conn_syn;
        }




        public double getBoostFactor() {
            return boostFactor;
        }

        public void setBoostFactor(double boostFactor) {
            this.boostFactor = 1;
        }

        public void stimulate() {
            for (Synapse synapse : potentialSynapses.values()) {
                synapse.increasePermanence();
            }
        }

        private void initSynapses()
        {

            Vector2D center =bottomIndices.get(bottomIndices.size()/2);

            if(HTMSettings.debug==false)
                Collections.shuffle(bottomIndices, random);

            // выберем только часть синапсов для данной колонки (если set.connectedPct<1)
            // предполагается, что set.connectedPct<1, в том случае, если рецептивные поля различных колонок пересекаются
            int numPotential = (int) Math.round(bottomIndices.size() * Column.this.connectedPct);
            for (int i = 0; i < numPotential; i++) {
                Vector2D coord = bottomIndices.get(i);
                int index=(int)coord.getX()*Column.this.set.yInput+(int)coord.getY();
                Synapse synapse = new Synapse(set, index);
                //радиальное затухание перманентности от центра рецептивного поля колонки
                //double k = MathUtils.distFromCenter(index, set.potentialRadius, set.xDimension, set.yDimension);
                double k = Vector2D.getDistance(coord,center );
                synapse.initPermanence(k);
                potentialSynapses.put(index,synapse);
            }
        }
    }
}