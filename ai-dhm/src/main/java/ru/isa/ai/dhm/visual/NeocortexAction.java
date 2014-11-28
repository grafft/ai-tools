package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitVector;
import info.monitorenter.gui.chart.Chart2D;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.core.Neocortex;
import ru.isa.ai.dhm.core.Region;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Random;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Created by gmdidro on 03.11.2014.
 */
public class NeocortexAction implements ActionListener {
    public Neocortex neocortex;
    private Map<Integer,DHMSettings> settings;
    private VisTree tree = new VisTree();

    public ImageClass img;
    public ChartHandler chartHandler;
    private int numRegions;

    private Boolean runs = false;
    private Boolean pause = false;
    private Boolean makeStep = false;
    private Random rnd = new Random();

    public NeocortexAction(Map<Integer,DHMSettings> settings, VisTree tree_) {
        this.numRegions = settings.size();
        this.settings=settings;
        tree = tree_;
    }

    private void makeRegionHierarchy(DefaultMutableTreeNode root, Region parent){
        String nodeName = root.toString();
        int ID = 0;
        Region r = null;
        if (!nodeName.contains("Picture")){
            if (!nodeName.contains("HTMNetwork")) {
                ID = Integer.valueOf(nodeName.substring(nodeName.indexOf(" ") + 1));
                r = neocortex.addRegion(settings.get(ID), parent);
            }
            for (int i = 0; i < root.getChildCount(); i++){
                makeRegionHierarchy((DefaultMutableTreeNode)root.getChildAt(i),r);
            }
        }
    }

    private void initCortex() {
        neocortex = new Neocortex();
        makeRegionHierarchy(tree.rootNode, null);
        neocortex.initialization();
    }

    public void actionPerformed(ActionEvent e) {
                //TODO P: тут все для одного слоя (settings[0])
                final BitVector input = new BitVector(DHMSettings.getDefaultSettings().xInput * DHMSettings.getDefaultSettings().yInput);
                for (int i = 0; i < DHMSettings.getDefaultSettings().xInput * DHMSettings.getDefaultSettings().yInput; i++) {
                    //if (Math.random() > 0.3)
                    input.set(i);
                }

                neocortex.iterate(input);
               // TODO P: make changes
               // chartHandler.collectData(0);
    }

    public void init(Chart2D chart1, Chart2D chart2, HTMConfiguration configuration) {
        initCortex();
        //img = configuration.getImg();
        chartHandler = new ChartHandler(chart1, chart2, configuration);
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
        //TODO P: тут все для одного слоя (settings[0])
        final BitVector input = new BitVector(DHMSettings.getDefaultSettings().xInput * DHMSettings.getDefaultSettings().yInput);
        for (int i = 0; i < DHMSettings.getDefaultSettings().xInput * DHMSettings.getDefaultSettings().yInput; i++) {
            //if (Math.random() > 0.3)
            input.set(i);
        }
        new Runnable() {
            @Override
            public void run() {
                //try {
                neocortex.iterate(input);

                        /*} catch (Exception e) {
                            e.printStackTrace();
                        } finally {*/
                //}
            }
        }.run();
    }
}

