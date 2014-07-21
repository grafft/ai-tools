package ru.isa.ai.newdhm;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import ru.isa.ai.newdhm.applet.ActiveColumnsVisualization;
import ru.isa.ai.newdhm.applet.HTMConfiguration;

import java.awt.*;

public class ChartHandler {
    private Chart2D chart2D1;
    private Chart2D chart2D2;
    private HTMConfiguration cfg;


      /////////////////////////////////////////////

    Boolean showDistalSegmentsCount = false;
    Boolean drawTimeline = false;
    Boolean perm = false;
    Boolean act = false;
    Boolean predict = false;
    Boolean learn = false;
    Boolean over = false;
    Boolean adc = false;
    Boolean mdc = false;
    Boolean odc = false;
    Boolean bst = false;
    Boolean inp = false;

    ITrace2D traceA = new Trace2DSimple("Activity");
    ITrace2D traceL = new Trace2DSimple("Learn");
    ITrace2D traceP = new Trace2DSimple("Predictive");
    ITrace2D traceD = new Trace2DSimple("Dendrite Segments");
    ITrace2D traceS = new Trace2DSimple("Permanences");
    ITrace2D traceO = new Trace2DSimple("Overlaps");
    ITrace2D traceADC = new Trace2DSimple("Active Duty Cycle");
    ITrace2D traceMDC = new Trace2DSimple("Min Duty Cycle");
    ITrace2D traceODC = new Trace2DSimple("Overlap Duty Cycle");
    ITrace2D traceBST = new Trace2DSimple("Column Boost");
    ITrace2D traceINP = new Trace2DSimple("Inputs Graphic");
    ITrace2D traceTMLN = new Trace2DSimple("Progress in Time");

    public ChartHandler(Chart2D chart1, Chart2D chart2, HTMConfiguration configuration) {
        this.chart2D1 = chart1;
        this.chart2D2 = chart2;
        this.cfg = configuration;
        showDistalSegmentsCount = cfg.showDendritesGraphCheckBox.isSelected();
        perm = cfg.showSynapsesPermanenceCheckBox.isSelected();
        act = cfg.showActiveCellsCheckBox.isSelected();
        predict = cfg.showPredictiveCellsCheckBox.isSelected();
        learn = cfg.showLearningCellsCheckBox.isSelected();
        over = cfg.showOverlapsCheckBox.isSelected();
        adc = cfg.showActiveDutyCycleCheckBox.isSelected();
        mdc = cfg.showMinDutyCycleCheckBox.isSelected();
        odc = cfg.showOverlapsDutyCycleCheckBox.isSelected();
        bst = cfg.showBoostCheckBox.isSelected();
        inp = cfg.inputsGraphicsCheckBox.isSelected();
        drawTimeline = cfg.drawDendritesTimlineCheckBox.isSelected();

        this.chart2D1.removeAllTraces();

        if (act) {
            chart2D1.addTrace(traceA);
            traceA.setColor(Color.CYAN);
            traceA.setTracePainter(new TracePainterDisc(4));
        }
        if (learn) {
            chart2D1.addTrace(traceL);
            traceL.setColor(Color.MAGENTA);
            traceL.setTracePainter(new TracePainterDisc(4));
        }
        if (predict) {
            chart2D1.addTrace(traceP);
            traceP.setColor(Color.BLUE);
            traceP.setTracePainter(new TracePainterDisc(4));
        }
        if (showDistalSegmentsCount) {
            chart2D1.addTrace(traceD);
            traceD.setTracePainter(new TracePainterDisc(4));
        }
        if (drawTimeline)
            chart2D2.addTrace(traceTMLN);
        if (perm) {
            chart2D1.addTrace(traceS);
            traceS.setColor(Color.BLUE);
            traceS.setTracePainter(new TracePainterDisc(4));
        }
        if (over) {
            chart2D1.addTrace(traceO);
            traceO.setColor(Color.RED);
            traceO.setTracePainter(new TracePainterDisc(4));
        }
        if (adc) {
            chart2D1.addTrace(traceADC);
            traceADC.setColor(Color.GREEN);
            traceADC.setTracePainter(new TracePainterDisc(4));
        }
        if (mdc) {
            chart2D1.addTrace(traceMDC);
            traceMDC.setColor(Color.GRAY);
            traceMDC.setTracePainter(new TracePainterDisc(4));
        }
        if (odc) {
            chart2D1.addTrace(traceODC);
            traceODC.setColor(Color.ORANGE);
            traceODC.setTracePainter(new TracePainterDisc(4));
        }
        if (bst) {
            chart2D1.addTrace(traceBST);
            traceBST.setColor(Color.DARK_GRAY);
            traceBST.setTracePainter(new TracePainterDisc(4));
        }
        if (inp) {
            chart2D1.addTrace(traceINP);
            traceINP.setColor(Color.RED);
            traceINP.setTracePainter(new TracePainterDisc(4));
        }
    }

    public void CollectData(int regInd) {
        for (ITrace2D trace2D : chart2D1.getTraces()) {
            trace2D.removeAllPoints();
        }
        Integer time = cfg.crtx.cr.time - 1 > 0 ? cfg.crtx.cr.time - 1 : 0;
        if (inp) {
            /*
            for (int i = 0; i < cfg.crtx.cr.regions[regInd].xDimension; i++) {
                for (int j = 0; j < cfg.crtx.cr.regions[regInd].xDimension; j++) {           ///////////////////////?????????
                    traceINP.addPoint(i + j, (cfg.crtx.cr.input(i,j) == true) ? 1 : 0);
                }*/
                for (int j = 0; j < cfg.crtx.cr.regions[regInd].getYDim(); j++) {
                    for (int i = 0; i < cfg.crtx.cr.regions[regInd].getXDim(); i++) {           ///////////////////////?????????
                        traceINP.addPoint(i + j, (cfg.crtx.cr.input(i,j) == true) ? 1 : 0);
                }
            }
        }
        String buf = "";
        int overalDSCount = 0;
        buf += "Cells Activity: \r\n" + "Timestep: " + cfg.crtx.cr.totalTime + "\r\n";
        buf += "Inhibition Radius: " + cfg.crtx.cr.regions[regInd].inhibitionRadius + "\r\n";
        //if (cfg.crtx.r.activeColumns.size() > 0)
        int index = cfg.crtx.cr.time - 1 > 0 ? cfg.crtx.cr.time - 1 : 0;
        buf += "Active Columns: " + cfg.crtx.cr.regions[regInd].activeColumns.viewRow(index).getQuick(0) + "\r\n";
        for (int c = 0; c < cfg.crtx.cr.regions[regInd].getXDim() * cfg.crtx.cr.regions[regInd].getYDim(); c++) {

            if (over) {
                traceO.addPoint(c, cfg.crtx.cr.regions[regInd].columns[c].overlap);
            }
            if (adc) {
                traceADC.addPoint(c, cfg.crtx.cr.regions[regInd].columns[c].activeDutyCycle);
            }
            if (mdc) {
                traceMDC.addPoint(c, cfg.crtx.cr.regions[regInd].columns[c].minDutyCycle);
            }
            if (odc) {
                traceODC.addPoint(c, cfg.crtx.cr.regions[regInd].columns[c].overlapDutyCycle);
            }
            if (bst) {
                traceBST.addPoint(c, cfg.crtx.cr.regions[regInd].columns[c].boost);
            }
            for (int i = 0; i < cfg.crtx.cr.regions[regInd].cellsPerColumn; i++) {
                Boolean val;
                if (act) {
                    val = cfg.crtx.cr.regions[regInd].columns[c].cells[i].activeState.get(time);
                    traceA.addPoint(c, val ? i + 1 * 1.0 : 0.0);
                }
                if (learn) {
                    val = cfg.crtx.cr.regions[regInd].columns[c].cells[i].learnState.get(time);
                    traceL.addPoint(c, val ? i + 1 * 1.0 : 0.0);
                }
                if (predict) {
                    val = cfg.crtx.cr.regions[regInd].columns[c].cells[i].predictiveState.get(time);
                    traceP.addPoint(c, val ? i + 1 * 1.0 : 0.0);
                }

                Integer size = cfg.crtx.cr.regions[regInd].columns[c].cells[i].dendriteSegmentsNum;   //dendriteSegments.length;  //!!!
                overalDSCount += size;
                if (showDistalSegmentsCount) {
                    traceD.addPoint(c, i + 1 * size);
                    buf += "C: " + c + " I: " + i + " N: " + size + " L: " +
                            cfg.crtx.cr.regions[regInd].columns[c].cells[i].learnState + " # ";
                }
            }
            if (perm) {
                Integer activeSynapses = 0;
                for (int s = 0; s < cfg.crtx.cr.regions[regInd].numColumns; s++) {
                    activeSynapses += cfg.crtx.cr.regions[regInd].columns[c].potentialSynapses[s].permanence >
                            cfg.crtx.cr.regions[regInd].connectedPerm ? 1 : 0;
                }
                traceS.addPoint(c, activeSynapses);
            }
            buf += "\r\n";
        }
        if (drawTimeline)
            traceTMLN.addPoint(cfg.crtx.cr.totalTime, overalDSCount);
        buf += "Overall Dendrite Segments Count: " + overalDSCount + "\r\n";
        cfg.textPane1.setText(buf);
    }
}
