package ru.isa.ai.newdhm;

import cern.colt.matrix.tbit.BitMatrix;
import com.sun.scenario.Settings;
import info.monitorenter.gui.chart.Chart2D;
import ru.isa.ai.newdhm.applet.HTMConfiguration;
import ru.isa.ai.newdhm.applet.ImageClass;
import java.util.Random;

public class CortexThread extends Thread {
    public Cortex cr;// = new Cortex();
    public ImageClass img;
    public ChartHandler chartHandler;
    private int numRegions;

    private Boolean runs = false;
    private Boolean pause = false;
    private Boolean makeStep = false;
    private Random rnd = new Random();

    public int getNumOfRegions(){
        return numRegions;
    }


    public CortexThread(int numRegions_, HTMConfiguration.Settings[] settings) {
        numRegions = numRegions_;
        cr = new Cortex(numRegions, settings);
    }

    public void Init(Chart2D chart1, Chart2D chart2, HTMConfiguration configuration) {
        cr.sInitializationDefault();
        chartHandler = new ChartHandler(chart1, chart2, configuration);
        img = configuration.getImg();
    }

    public void drawOnChart(int regInd){
        chartHandler.CollectData(regInd);
    }

    public BitMatrix getInputMatrixAtT(){
        BitMatrix matrix = new BitMatrix(img.getW() ,img.getH());
        matrix = img.getBitMatrix();
        return matrix;
    }

    public BitMatrix getInputMatrixAtTDefault(int ind){
        BitMatrix matrix = new BitMatrix(cr.regions[ind].xDimension ,cr.regions[ind].yDimension);
        for(int i = 0 ; i < cr.regions[ind].xDimension; i++)
            for (int j = 0; j < cr.regions[ind].yDimension; j++){
                int value = cr.time % 2 > 0 ? rnd.nextInt(2) : Math.sin(i + j + cr.totalTime) > 0 ? 1 : 0;
                matrix.put(i, j, (value == 1) ? true : false);
            }
        return matrix;
    }

    public void run() {
        this.runs = true;
        //while (runs)
        {                                  //в момент t=1 подается следующая картинка на вход
            //if (!pause)
                {
                    cr.setInput2DMatrix(getInputMatrixAtT()); // 1 picture yet
                    cr.interactRegions();
                    chartHandler.CollectData(0);
                    cr.timestep();
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
