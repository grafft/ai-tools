package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitVector;
import info.monitorenter.gui.chart.Chart2D;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.RegionSettingsException;
import ru.isa.ai.dhm.core.Neocortex;
import ru.isa.ai.dhm.core.Region;
import ru.isa.ai.dhm.core.Column;
import ru.isa.ai.dhm.core.Cell;
import ru.isa.ai.dhm.visual.HTMConfiguration;
import ru.isa.ai.dhm.visual.ImageClass;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVWriteProc;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by gmdidro on 03.11.2014.
 */
public class NeocortexAction implements ActionListener {
    public Neocortex neocortex;
    private DHMSettings[] settings;

    public ImageClass img;
    public ChartHandler chartHandler;
    private int numRegions;

    private Boolean runs = false;
    private Boolean pause = false;
    private Boolean makeStep = false;
    private Random rnd = new Random();

    public NeocortexAction(DHMSettings[] settings) {
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

    public void actionPerformed(ActionEvent e) {
                //TODO P: тут все для одного слоя (settings[0])
                final BitVector input = new BitVector(settings[0].xInput * settings[0].yInput);
                for (int i = 0; i < settings[0].xInput * settings[0].yInput; i++) {
                    //if (Math.random() > 0.3)
                    input.set(i);
                }

                neocortex.iterate(input);
               // TODO P: make changes
               // chartHandler.collectData(0);
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

    public void createCSVExportFiles(String outPutFileCells, String outPutFileColumns){
        CSV csv = CSV
                .separator(';')
                .quote('\'')
                .skipLines(1)
                .charset("UTF-8")
                .create();
        // CSVWriter will be closed after end of processing
        csv.write(outPutFileCells, new CSVWriteProc() {
            public void process(CSVWriter out) {
                out.writeNext("Cell Index", "State");
                for (Region reg: neocortex.getRegions()){
                    for (Column col : reg.getColumns().values()){
                        for (Cell cell : col.getCells()){
                            out.writeNext(String.valueOf(col.getIndex()) +"_"+String.valueOf(cell.getIndex()), String.valueOf(cell.getStateHistory()[1]));
                        }
                    }

                }
            }
        });

        csv.write(outPutFileColumns, new CSVWriteProc() {
            public void process(CSVWriter out) {
                out.writeNext("Column Index", "Activity","Overlap","Boost");
                for (Region reg: neocortex.getRegions()){
                    for (Column col : reg.getColumns().values()){
                        out.writeNext(String.valueOf(col.getIndex()), String.valueOf(col.isActive()), String.valueOf(col.getOverlap()), String.valueOf(col.getProximalSegment().getBoostFactor()));
                    }
                }
            }
        });
    }

    public void makeStep()
    {
        //TODO P: тут все для одного слоя (settings[0])
        final BitVector input = new BitVector(settings[0].xInput * settings[0].yInput);
        for (int i = 0; i < settings[0].xInput * settings[0].yInput; i++) {
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

