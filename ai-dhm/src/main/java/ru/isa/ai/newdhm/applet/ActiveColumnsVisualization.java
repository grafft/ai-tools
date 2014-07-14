package ru.isa.ai.newdhm.applet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ActiveColumnsVisualization extends JFrame {

    public JPanel activeColumnsPanel_main;
    private JPanel activeColsPanel;
    private JSlider slider1;
    private JLabel spinnersLabel;
    HighlightableArea ha;
    int squaresNumWidth = 0;
    int squaresNumHeight = 0;
    int squaresNumWidth_input = 0;
    int squaresNumHeight_input = 0;

    public ActiveColumnsVisualization(){
        activeColsPanel.setPreferredSize(new Dimension(500, 500));

        slider1.addChangeListener(new BoundedChangeListener());
        slider1.setMinorTickSpacing(2);
        slider1.setMajorTickSpacing(10);
        slider1.setPaintTicks(true);
        slider1.setPaintLabels(true);

        //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel("0") );
        labelTable.put( new Integer( 50 ), new JLabel("0.5") );
        labelTable.put( new Integer( 100 ), new JLabel("1.0") );
        slider1.setLabelTable( labelTable );

        slider1.setPaintLabels(true);


    }

    private class BoundedChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent changeEvent) {
            if (!slider1.getValueIsAdjusting()) {
                activeColsPanel.remove(ha);
                AreaHighlightTest();
                activeColumnsPanel_main.setVisible(false);
                activeColumnsPanel_main.setVisible(true);
            }
        }
    }

    private void AreaHighlightTest() {
        activeColsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(Color.blue)));
        ha = new HighlightableArea(squaresNumWidth,squaresNumHeight,(float)slider1.getValue()/100.0, Color.gray);
        ha.setBackground(Color.white);
        activeColsPanel.add(ha, BorderLayout.CENTER);
        activeColsPanel.setVisible(true);
    }

    public void draw(int squaresNumWidth_, int squaresNumHeight_, int squaresNumWidth_input_, int squaresNumHeight_input_ ){
        squaresNumWidth = squaresNumWidth_;
        squaresNumHeight = squaresNumHeight_;
        squaresNumWidth_input = squaresNumWidth_input_;
        squaresNumHeight_input = squaresNumHeight_input_;
        AreaHighlightTest();
    }
}

class HighlightableArea extends JPanel {

    private int hx = -1;
    private int hy = -1;

    private int squaresNumPerW = 0;
    private double otstup = 0.0;

    private int squaresNumPerH = 0;
    private double stepNaklInPerCent = 0.0;

    private Color highlightColor;


    public HighlightableArea(int squaresNumPerW, int squaresNumPerH, double stepNaklInPerCent, Color highlightColor) {
        this.squaresNumPerW = squaresNumPerW;
        this.squaresNumPerH = squaresNumPerH;
        this.highlightColor = highlightColor;
        this.stepNaklInPerCent = stepNaklInPerCent;
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());
    }

    public void paintGrid(){
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.otstup = getHeight() * stepNaklInPerCent;
        double dx = (getWidth() - otstup) / squaresNumPerW;
        double dy = otstup / squaresNumPerH;

        g.setColor(Color.darkGray);

        for (int i = 0; i <= squaresNumPerW; i++) {
            g.drawLine((int)(otstup + dx * i), 0, (int) (dx * i), (int)otstup);
        }

        for (int i = 0; i <= squaresNumPerH; i++) {
            g.drawLine((int)(otstup - dy * i), (int) (dy * i), (int)(getWidth() - dy * i), (int) (dy * i));
        }

       // for (int i = 0; i <= squa; i++){
           // g.drawLine();
        //}

        if (hx >= 0 && hy >= 0) {

            //draw filled rectangle
            g.setColor(highlightColor);

            int up_left_x = (int)(otstup - hy*dy + dx*hx);
            int down_left_x = (int)(up_left_x - Math.sqrt((2 * Math.pow(otstup, 2.0) / Math.pow(squaresNumPerH, 2.0)) - Math.pow(dy, 2.0)));
            int down_right_x = (int)(down_left_x + dx);
            int up_right_x = (int)(up_left_x + dx);

            int up_left_y = (int)(dy*hy);
            int down_left_y = (int)(up_left_y + dy);
            int down_right_y = down_left_y + 1;
            int up_right_y = up_left_y + 1;

            g.fillPolygon(new int[]{up_left_x, down_left_x, down_right_x, up_right_x },
                    new int[]{up_left_y, down_left_y, down_right_y, up_right_y }
                    ,4);

            //draw column's links

        }
    }

    private class MouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            double dx = ((double) getWidth() - otstup) / squaresNumPerW;
            double dy = otstup / squaresNumPerH;
            int ny = (int)(e.getY() / dy);
            double dx1 = (otstup - ny*dy) ;
            int nx = (int)((e.getX() - dx1) / dx);

            if (nx >=0 && nx < squaresNumPerW && ny >= 0 && ny < squaresNumPerH ){
              if (nx != hx || ny != hy) {
                hx = nx;
                hy = ny;
                System.out.print("x = " + hx + "; y = "+ hy +"\n");
                  paintGrid();
              }
            }
        }
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            double dx = ((double) getWidth() - otstup) / squaresNumPerW;
            double dy = otstup / squaresNumPerH;
            int hy = (int)(e.getY() / dy);
            double dx1 = (otstup - hy*dy) ;
            int hx = (int)((e.getX() - dx1) / dx);

            System.out.print("x = " + hx + "; y = "+ hy +"\n");
            if (hx >=0 && hx < squaresNumPerW && hy >= 0 && hy < squaresNumPerH )
                paintGrid();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hx = -1;
            hy = -1;
            paintGrid();
        }
    }
}

