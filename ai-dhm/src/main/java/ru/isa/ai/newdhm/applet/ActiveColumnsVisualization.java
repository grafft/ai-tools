package ru.isa.ai.newdhm.applet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.*;

public class ActiveColumnsVisualization extends JFrame {

    public JPanel activeColumnsPanel;
    /*private JButton drawButton;

    public ActiveColumnsVisualization(){
        drawButton.addActionListener(new Listener());
    }

    private class Listener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            AreaHighlightTest();
        }
    }  */

    public ActiveColumnsVisualization(){
        activeColumnsPanel.setPreferredSize(new Dimension(500, 500));
    }

    void AreaHighlightTest() {
        //JPanel cp = new JPanel(new BorderLayout());
        activeColumnsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(Color.blue)));
        HighlightableArea ha = new HighlightableArea(10,10,0.5, Color.gray);
        ha.setBackground(Color.white);
        activeColumnsPanel.add(ha, BorderLayout.CENTER);
        //setSize(500,500);
        setLocationRelativeTo(null);
        activeColumnsPanel.setVisible(true);
    }

    public void draw(){
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.otstup = (double) (getHeight() * stepNaklInPerCent);
        double dx = (double) (getWidth() - otstup) / squaresNumPerW;
        double dy = (double) otstup / squaresNumPerH;

        g.setColor(Color.darkGray);

        for (int i = 0; i <= squaresNumPerW; i++) {
            g.drawLine((int)(otstup + dx * i), 0, (int) (dx * i), (int)otstup);
        }

        for (int i = 0; i <= squaresNumPerH; i++) {
            g.drawLine((int)(otstup - dy * i), (int) (dy * i), (int)(getWidth() - dy * i), (int) (dy * i));
        }

        if (hx >= 0 && hy >= 0) {
            g.setColor(highlightColor);

            hy++;
            int up_left_x = (int)(otstup - hy*dy + dx*hx);
            int down_left_x = (int)(up_left_x + 2*dy -dx);
            int down_right_x = (int)(down_left_x + dx);
            int up_right_x = (int)(up_left_x + dx);

            int up_left_y = (int)(dy*hy);
            int down_left_y = (int)(up_left_y - dy);
            int down_right_y = down_left_y;
            int up_right_y = up_left_y;

            g.fillPolygon(new int[]{up_left_x, down_left_x, down_right_x, up_right_x },
                          new int[]{up_left_y, down_left_y, down_right_y, up_right_y }
                         ,4);
            //g.fillPolygon(new int[]{(int)otstup,(int)(otstup -dy),(int)(otstup+dx-dy),(int)(otstup+dx) }, new int[]{0,(int)dy,(int)dy, 0},4);

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

            if (nx >=0 && nx <=9 && ny >= 0 && ny <= 9 ){
              if (nx != hx || ny != hy) {
                hx = nx;
                hy = ny;
                System.out.print("x = " + hx + "; y = "+ hy +"\n");
                repaint();
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
            if (hx >=0 && hx <=9 && hy >= 0 && hy <= 9 )
                repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            hx = -1;
            hy = -1;
            repaint();
        }
    }
}
