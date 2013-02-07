package ru.isa.ai.tests.iterations;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Aleksandr Panov
 * Date: 07.02.13
 * Time: 18:07
 */
public class RFIterationViewer extends ApplicationFrame {
    public static final double R = 1.8;
    public static double currentR = R;

    public RFIterationViewer(String title) {
        super(title);
        JPanel chartPanel = createPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {
        RFIterationViewer visualizer = new RFIterationViewer("RF iterations");
        visualizer.pack();
        RefineryUtilities.centerFrameOnScreen(visualizer);
        visualizer.setVisible(true);
    }

    public static JPanel createPanel() {
        final XYSeriesCollection dataset = createDataset(currentR);
        new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentR += 0.001;
                XYSeries xySeries = dataset.getSeries(0);
                xySeries.setKey("Function for "+currentR);
                xySeries.clear();
                double x = 0.01D;
                for (int i = 0; i < 100; i++) {
                    xySeries.add(i, x);
                    x = (1 + currentR) * x - currentR * x * x;
                }
            }
        }).start();
        return new ChartPanel(createChart(dataset), false);
    }

    private static XYSeriesCollection createDataset(double r) {
        XYSeries series = new XYSeries("Function for "+currentR);
        double x = 0.01D;
        for (int i = 0; i < 100; i++) {
            series.add(i, x);
            x = (1 + r) * x - r * x * x;
        }
        return new XYSeriesCollection(series);
    }

    private static JFreeChart createChart(XYDataset dataset) {
        NumberAxis numberaxis = new NumberAxis("X");
        numberaxis.setAutoRangeIncludesZero(false);
        NumberAxis numberaxis1 = new NumberAxis("Y");
        numberaxis1.setAutoRangeIncludesZero(false);
        XYSplineRenderer xysplinerenderer = new XYSplineRenderer();
        XYPlot xyplot = new XYPlot(dataset, numberaxis, numberaxis1, xysplinerenderer);
        xyplot.setBackgroundPaint(Color.lightGray);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));

        JFreeChart chart = new JFreeChart("Iterations", JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);
        chart.setBackgroundPaint(Color.white);
        return chart;
    }
}
