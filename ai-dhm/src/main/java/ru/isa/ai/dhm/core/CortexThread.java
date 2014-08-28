package ru.isa.ai.dhm.core;

import cern.colt.matrix.tbit.BitMatrix;
//import com.sun.scenario.Settings;
import info.monitorenter.gui.chart.Chart2D;
import ru.isa.ai.dhm.RegionSettings;
import ru.isa.ai.dhm.visual.ChartHandler;
import ru.isa.ai.dhm.visual.HTMConfiguration;
import ru.isa.ai.dhm.visual.ImageClass;

import java.util.Random;

public class CortexThread extends Thread {
    public Cortex cr;
    public ImageClass img;
    public ChartHandler chartHandler;
    private int numRegions;

    private Boolean runs = false;
    private Boolean pause = false;
    private Boolean makeStep = false;
    private Random rnd = new Random();

    public CortexThread(int numRegions, RegionSettings[] settings) {
        this.numRegions = numRegions;
        cr = new Cortex(this.numRegions, settings);
    }

    public void run() {
        this.runs = true;
        //while (runs)
        {                                  //в момент t=1 подается следующая картинка на вход
            //if (!pause)
            {
                cr.setInput2DMatrix(getInputMatrixAtT()); // 1 picture yet
                cr.interactRegions();
                chartHandler.collectData(0);
                cr.timestep();
                if (makeStep) {
                    pause = true;
                    makeStep = false;
                }
            }
        }
    }

    public void init(Chart2D chart1, Chart2D chart2, HTMConfiguration configuration) {
        img = configuration.getImg();
        cr.sInitializationDefault(img.getW(), img.getH());
        chartHandler = new ChartHandler(chart1, chart2, configuration);

    }

    public int getNumOfRegions() {
        return numRegions;
    }




    public void drawOnChart(int regInd) {
        chartHandler.collectData(regInd);
    }

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
                matrix.put(i, j, (value == 1) ? true : false);
            }
        return matrix;
    }

    public Boolean isRunning() {
        return runs;
    }

    public Boolean isPaused() {
        return pause;
    }

    public void MakeStep() {
        if (!runs)
            this.start();
        this.pause = false;
        this.makeStep = true;
    }

    public void Continue() {
        this.pause = false;
    }

    public void Quit() {
        this.runs = false;
    }
}
