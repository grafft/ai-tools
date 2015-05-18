package ru.isa.ai.ourhtm.structure;



import casmi.matrix.Vector2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by APetrov on 13.05.2015.
 */
public class Column {

    public Column(int[] coords,int[] receptorFieldCenter, int potentialRadius, double connectedPct, int cellNum, Region region)
    {
        this.receptorFieldCenter=receptorFieldCenter;
        this.coords=coords;
        r=region;
        cells=new Cell[cellNum];
        proximalDendrite.initSynapses();
    }

    public ProximalDendrite getProximalDendrite() { return proximalDendrite; }


    private Region r;
    private Cell[] cells; // клетки данной колонки
    private ProximalDendrite proximalDendrite=new ProximalDendrite();;


    private int[] receptorFieldCenter;
    private int[] coords;
    private int potentialRadius;
    private double connectedPct;


    public class ProximalDendrite {

        ArrayList<Synapse> potentialSynapses =new ArrayList<>(1);;
        private int overlap;
        private double boostFactor;
        private Random random = new Random();
        HTMSettings settings;

        public ArrayList<Synapse> getConnectedSynapses()
        {
            ArrayList<Synapse> conn_syn=new ArrayList<>(10);
            for(Synapse s : potentialSynapses)
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
            this.boostFactor = boostFactor;
        }

        private void initSynapses()
        {
            List<Integer> indices = new ArrayList<>();
            for (int i = receptorFieldCenter[0] - Column.this.potentialRadius; i <= receptorFieldCenter[0] + Column.this.potentialRadius; i++) {
                if (i >= 0 && i < r.getInputW()) {
                    for (int j = receptorFieldCenter[1] - Column.this.potentialRadius; j <= receptorFieldCenter[1] + Column.this.potentialRadius; j++) {
                        if (j >= 0 && j < r.getInputW())
                            indices.add(i * r.getInputH()+ j);
                    }
                }
            }

            if(HTMSettings.debug==false)
                Collections.shuffle(indices, random);

            // выберем только часть синапсов для данной колонки (если settings.connectedPct<1)
            // предполагается, что settings.connectedPct<1, в том случае, если рецептивные поля различных колонок пересекаются
            int numPotential = (int) Math.round(indices.size() * Column.this.connectedPct);
            for (int i = 0; i < numPotential; i++) {
                int index = indices.get(i);
                Synapse synapse = new Synapse(settings, index);
                synapse.initPermanence(Vector2D.getDistance(new Vector2D(1, 1), new Vector2D(2, 2)));
                potentialSynapses.add(index, synapse);
            }
        }

        public Vector2D delinear(int index,int w)
        {
            return new Vector2D(Math.ceil(index/w)+1,index-w*Math.ceil(index/w)+1);
        }
    }
}
