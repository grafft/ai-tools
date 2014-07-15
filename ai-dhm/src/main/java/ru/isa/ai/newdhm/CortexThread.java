package ru.isa.ai.newdhm;

import cern.colt.matrix.tbit.BitMatrix;
import info.monitorenter.gui.chart.Chart2D;
import ru.isa.ai.newdhm.applet.HTMConfiguration;
import ru.isa.ai.newdhm.applet.ImageClass;

import java.awt.image.*;
import java.util.Random;

public class CortexThread extends Thread {
    public Cortex r;// = new Cortex();
    public ImageClass img;
    public ChartHandler chartHandler;

    private Boolean runs = false;
    private Boolean pause = false;
    private Boolean makeStep = false;
    private Random rnd = new Random();

    public CortexThread() {
        r = new Cortex();
    }

    public void Init(Chart2D chart1, Chart2D chart2, HTMConfiguration configuration) {

        r.sInitializationDefault();
        chartHandler = new ChartHandler(chart1, chart2, configuration);
        img = configuration.getImg();
    }

    public BitMatrix getInputMatrixAtT(){
        BitMatrix matrix = new BitMatrix(img.getW() ,img.getH());
        matrix = img.getBitMatrix();
        return matrix;
    }

    public BitMatrix getInputMatrixAtTDefault(){
        BitMatrix matrix = new BitMatrix(r.region.xDimension ,r.region.yDimension);
        for(int i = 0 ; i < r.region.xDimension; i++)
            for (int j = 0; j < r.region.yDimension; j++){
                int value = r.time % 2 > 0 ? rnd.nextInt(2) : Math.sin(i + j + r.totalTime) > 0 ? 1 : 0;
                matrix.put(i, j, (value == 1) ? true : false);
            }
        return matrix;
    }

    public void run() {
        this.runs = true;
        while (runs) {
            if (!pause) {
                r.setInput2DMatrix(getInputMatrixAtT());
                r.sOverlap();
                r.sInhibition();
                r.sLearning();
                r.tCellStates();
                r.tPredictiveStates();
                r.tLearning();
                chartHandler.CollectData();
                r.timestep();
                if (makeStep) {
                    pause = true;
                    makeStep = false;
                }
            }
        }
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
