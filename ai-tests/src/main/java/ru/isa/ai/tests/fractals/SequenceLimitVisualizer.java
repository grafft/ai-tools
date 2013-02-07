package ru.isa.ai.tests.fractals;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Aleksandr Panov
 * Date: 10.01.13
 * Time: 16:32
 */
public class SequenceLimitVisualizer extends ApplicationFrame {
    private static int maxX = 3;
    private static int maxY = 3;
    private static int scale = 10;

    public SequenceLimitVisualizer(String title) {
        super(title);
        JPanel chartPanel = createPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {
        SequenceLimitVisualizer visualizer = new SequenceLimitVisualizer("Limit visualizer");
        visualizer.pack();
        RefineryUtilities.centerFrameOnScreen(visualizer);
        visualizer.setVisible(true);
    }

    public static JPanel createPanel() {
        return new ChartPanel(createChart(createDataset()));
    }

    private static JFreeChart createChart(XYZDataset dataset) {
        NumberAxis xAxis = new NumberAxis("X");
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        NumberAxis yAxis = new NumberAxis("Y");
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);

        XYBlockRenderer renderer = new XYBlockRenderer();
        PaintScale scale = new GrayPaintScale(-1.0, 1.0);
        renderer.setPaintScale(scale);

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);
        JFreeChart chart = new JFreeChart("Sequence limits", plot);
        chart.removeLegend();
        chart.setBackgroundPaint(Color.white);
        return chart;
    }

    private static XYZDataset createDataset() {
        return new XYZDataset() {
            public int getSeriesCount() {
                return 1;
            }

            public int getItemCount(int series) {
                return 4 * maxX * maxY * scale * scale;
            }

            public Number getX(int series, int item) {
                return getXValue(series, item);
            }

            public double getXValue(int series, int item) {
                return ((item - (item / (2 * maxX * scale)) * 2 * maxX * scale) - maxX * scale) / (scale + 0.0);
            }

            public Number getY(int series, int item) {
                return getYValue(series, item);
            }

            public double getYValue(int series, int item) {
                return (item / (2 * maxX * scale) - maxY * scale) / (scale + 0.0);
            }

            public Number getZ(int series, int item) {
                return getZValue(series, item);
            }

            public double getZValue(int series, int item) {
                double x = getXValue(series, item);
                double y = getYValue(series, item);

                double currX = x, currY = y, arg = 0;
                for (int i = 0; i < 1000; i++) {
                    currX = currX * currX - currY * currY + x;
                    currY = 2 * currX * currY + y;
                    arg = currX * currX + currY * currY;
                }
                return arg < 4 ? -1 : 1;
            }

            public void addChangeListener(DatasetChangeListener listener) {
                // ignore - this dataset never changes
            }

            public void removeChangeListener(DatasetChangeListener listener) {
                // ignore
            }

            public DatasetGroup getGroup() {
                return null;
            }

            public void setGroup(DatasetGroup group) {
                // ignore
            }

            public Comparable getSeriesKey(int series) {
                return "sin(sqrt(x + y))";
            }

            public int indexOf(Comparable seriesKey) {
                return 0;
            }

            public DomainOrder getDomainOrder() {
                return DomainOrder.ASCENDING;
            }
        };
    }
}
