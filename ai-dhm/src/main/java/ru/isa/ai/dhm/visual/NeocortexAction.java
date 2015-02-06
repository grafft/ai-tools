package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitMatrix;
import cern.colt.matrix.tbit.BitVector;
import info.monitorenter.gui.chart.Chart2D;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.core.Neocortex;
import ru.isa.ai.dhm.core.Region;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by gmdidro on 03.11.2014.
 */
public class NeocortexAction implements ActionListener {
    public Neocortex neocortex;
    private Map<Integer,DHMSettings> settings;
    private IInputLoader input;
    private VisTree tree = new VisTree();

    int stepNum=0;

    //public ImageClass img;
    public ChartHandler chartHandler;
    private int numRegions;

    private Boolean runs = false;
    private Boolean pause = false;
    private Boolean makeStep = false;
    private Random rnd = new Random();

    public NeocortexAction(final Map<Integer,DHMSettings> settings_, final IInputLoader input ,final VisTree tree_) {
        this.numRegions = settings_.size();
        this.settings = settings_;
        this.input = input;
        tree = tree_;
    }

    private void makeRegionHierarchy(DefaultMutableTreeNode root, Region parent){
        String nodeName = root.toString();
        int ID = 0;
        Region r = null;
        if (!nodeName.contains("Picture")){
            if (!nodeName.contains("HTM")) {
                ID = Integer.valueOf(nodeName.substring(nodeName.indexOf(" ") + 1));
                r = neocortex.addRegion(ID, settings.get(ID), parent);
            }
            for (int i = 0; i < root.getChildCount(); i++){
                makeRegionHierarchy((DefaultMutableTreeNode)root.getChildAt(i),r);
            }
        }
    }

    public Region getSelectedRegion(int ID){
        boolean fl = false;
        int i = 0;
        while (!fl && i < neocortex.getRegions().size()){
            if (neocortex.getRegions().get(i).getID() == ID){
                fl = true;
                return  neocortex.getRegions().get(i);
            }
            i++;
        }
        return null;
    }


    private void initCortex() {
        neocortex = new Neocortex();
        makeRegionHierarchy(tree.rootNode, null);
        neocortex.initialization();
    }

    public void actionPerformed(ActionEvent e) {
                //TODO P: тут все для одного слоя (settings[0])


                /*
                final BitVector input = new BitVector(DHMSettings.getDefaultSettings().xInput * DHMSettings.getDefaultSettings().yInput);
                for (int i = 0; i < DHMSettings.getDefaultSettings().xInput * DHMSettings.getDefaultSettings().yInput; i++) {
                    //if (Math.random() > 0.3)
                    input.set(i);
                }
*/
                BitVector vec= input.getNext();
                if (vec!=null) {
                    neocortex.iterate(vec);
                    stepNum++;
                }
               // TODO P: make changes
               // chartHandler.collectData(0);
    }

    public void init(Chart2D chart1, Chart2D chart2, HTMConfiguration configuration) {
        initCortex();
        //img = configuration.getImg();
        //chartHandler = new ChartHandler(chart1, chart2, configuration);
    }

    public int getNumOfRegions() {
        return numRegions;
    }

    public void drawOnChart(int regInd) {
       // TODO P: Make changes
       // chartHandler.collectData(regInd);
    }

   /*
    public BitMatrix getInputMatrixAtT() {
        BitMatrix matrix = new BitMatrix(img.getW(), img.getH());
        matrix = img.getBitMatrix();
        return matrix;
    }

    public BitMatrix getInputMatrixAtTDefault(int ind) {
        BitMatrix matrix = new BitMatrix(cr.regions[ind].getXDim(), cr.regions[ind].getYDim());
        for (int i = 0; i < cr.regions[ind].getXDim(); i++)
            for (int j = 0; j < cr.regions[ind].getYDim(); j++) {
                int value = cr.time % 2 > 0 ? rnd.nextInt(2) : Math.sin(i + j + cr.totalTime) > 0 ? 1 : 0;
                matrix.put(i, j, value == 1);
            }
        return matrix;
    }
    */



    public void makeStep()
    {

        new Runnable() {
            @Override
            public void run() {
                //try {
                BitVector vec= input.getNext();
                if (vec!=null) {
                    neocortex.iterate(vec);
                    stepNum++;
                }

                        /*} catch (Exception e) {
                            e.printStackTrace();
                        } finally {*/
                //}
            }
        }.run();
    }
}

