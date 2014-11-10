package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitVector;
import info.monitorenter.gui.chart.Chart2D;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.RegionSettingsException;
import ru.isa.ai.dhm.core.Neocortex;
import ru.isa.ai.dhm.core.Region;
import ru.isa.ai.dhm.visual.HTMConfiguration;
import ru.isa.ai.dhm.visual.ImageClass;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by gmdidro on 03.11.2014.
 */
public class NeocortexThread extends Thread {
    public Neocortex neocortex;
    private DHMSettings[] settings;

    public ImageClass img;
    public ChartHandler chartHandler;
    private int numRegions;

    private Boolean runs = false;
    private Boolean pause = false;
    private Boolean makeStep = false;
    private Random rnd = new Random();

    public NeocortexThread(DHMSettings[] settings) {
        this.numRegions = settings.length;
        this.settings=settings;
    }

    private void initCortex() {
        neocortex = new Neocortex();

        Region leaf = neocortex.addRegion(settings[0], null);
        for(int i=1;i<settings.length;i++)
            leaf = neocortex.addRegion(settings[i], new ArrayList<>(Arrays.asList(leaf)));

        neocortex.initialization();
    }

    public void run() {
        this.runs = true;
        while (runs)
        {                                  //в момент t=1 подается следующая картинка на вход
            if (!pause)
            {
                //TODO P: тут все для одного слоя (settings[0])
                final BitVector input = new BitVector(settings[0].xInput * settings[0].yInput);
                for (int i = 0; i < settings[0].xInput * settings[0].yInput; i++) {
                    //if (Math.random() > 0.3)
                    input.set(i);
                }

                neocortex.iterate(input);
               // TODO P: make changes
               // chartHandler.collectData(0);

                if (makeStep) {
                    pause = true;
                    makeStep = false;
                }
            }
        }
    }

    public void init(Chart2D chart1, Chart2D chart2, HTMConfiguration configuration) {
        initCortex();
        img = configuration.getImg();
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
    public Boolean isRunning() {
        return runs;
    }

    public Boolean isPaused() {
        return pause;
    }

    public void thdMakeStep() {
        if (!runs)
            this.start();
        this.pause = false;
        this.makeStep = true;
    }

    public void thdContinue() {
        this.pause = false;
    }

    public void thdQuit() {
        this.runs = false;
    }
}

