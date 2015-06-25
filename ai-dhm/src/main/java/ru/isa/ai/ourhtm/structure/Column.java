package ru.isa.ai.ourhtm.structure;



import casmi.matrix.Vector2D;
import ru.isa.ai.dhm.util.MathUtils;
import ru.isa.ai.ourhtm.algorithms.SimpleMapper;

import java.util.*;

/**
 * Created by APetrov on 13.05.2015.
 */
public class Column {

    HTMSettings settings;
    public Column(int[] coords,int[] receptorFieldCenter, int potentialRadius, double connectedPct, int cellNum, Region region, HTMSettings settings)
    {
        this.settings=settings;
        this.receptorFieldCenter=receptorFieldCenter;
        this.potentialRadius=potentialRadius;
        this.connectedPct=connectedPct;
        r=region;
        cells=new Cell[cellNum];
        proximalDendrite.initSynapses();
    }

    public ProximalDendrite getProximalDendrite() { return proximalDendrite; }


    private Region r;
    private Cell[] cells; // клетки данной колонки
    private ProximalDendrite proximalDendrite=new ProximalDendrite();;


    private int[] receptorFieldCenter;
    private int potentialRadius;
    private double connectedPct;


    public class ProximalDendrite {

        // хэш синапсов: индекс элемента с которым соединение и сам синапс
        HashMap<Integer,Synapse> potentialSynapses =new HashMap<>();
        private int overlap;
        private double boostFactor = 1;
        private Random random = new Random();


        public ArrayList<Synapse> getConnectedSynapses()
        {
            ArrayList<Synapse> conn_syn=new ArrayList<>(10);
            for(Synapse s : potentialSynapses.values())
                if(s.isConnected())
                    conn_syn.add(s);
            return  conn_syn;
        }


        public int getOverlap() {
            return overlap;
        }

        public void setOverlap(int overlap) {
            this.overlap = overlap;
        }

        public double getBoostFactor() {
            return boostFactor;
        }

        public void setBoostFactor(double boostFactor) {
            this.boostFactor = 1;
        }

        private void initSynapses()
        {
            List<Integer[]> indices = SimpleMapper.map(new int[]{r.getInputH(),r.getInputW()}, receptorFieldCenter,Column.this.potentialRadius);

            if(HTMSettings.debug==false)
                Collections.shuffle(indices, random);

            // выберем только часть синапсов для данной колонки (если settings.connectedPct<1)
            // предполагается, что settings.connectedPct<1, в том случае, если рецептивные поля различных колонок пересекаются
            int numPotential = (int) Math.round(indices.size() * Column.this.connectedPct);
            for (int i = 0; i < numPotential; i++) {
                Integer[] coord = indices.get(i);
                int index=coord[0]*Column.this.settings.yDimension+coord[1];
                Synapse synapse = new Synapse(settings, index);
                //радиальное затухание перманентности от центра рецептивного поля колонки
                synapse.initPermanence(Vector2D.getDistance(MathUtils.delinear(index, Column.this.potentialRadius * 2), MathUtils.delinear((int) Math.pow((double) Column.this.potentialRadius, 2.0) / 2, Column.this.potentialRadius * 2)));
                potentialSynapses.put(index,synapse);
            }
        }
    }
}
