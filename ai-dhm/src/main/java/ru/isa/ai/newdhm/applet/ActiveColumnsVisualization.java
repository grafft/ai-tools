package ru.isa.ai.newdhm.applet;

import cern.colt.matrix.tbit.BitMatrix;
import ru.isa.ai.newdhm.CortexThread;
import ru.isa.ai.newdhm.Synapse;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

public class ActiveColumnsVisualization extends JFrame {

    public JPanel activeColumnsPanel_main;
    private JPanel activeColsPanel;
    private JSlider slider1;
    private JLabel spinnersLabel;
    private JButton buttonUP;
    private JButton buttonDOWN;
    private JLabel regionLabel;
    private JLabel numOfRegToDraw;
    private HighlightableArea ha;
    private CortexThread crtx;
    private int curTime = 0;
    private final int PANEL_DIMENSION_X = 500;
    private final int PANEL_DIMENSION_Y = 500;
    private int indOfUpReg = 0;
    private int indOfDownReg = 0;

    public ActiveColumnsVisualization(){
        activeColsPanel.setPreferredSize(new Dimension(PANEL_DIMENSION_X, PANEL_DIMENSION_Y));

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

        buttonUP.addActionListener(new ButtonUPListener());
        buttonDOWN.addActionListener(new ButtonDOWNListener());
    }

    private class ButtonUPListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //inc counter of current region
            int numOfNextReg = Integer.parseInt(numOfRegToDraw.getText()) + 1;
            if (numOfNextReg == 1)
                buttonDOWN.setEnabled(true);
            numOfRegToDraw.setText(String.valueOf(numOfNextReg));

            indOfUpReg++;
            indOfDownReg++;
            draw(indOfUpReg, indOfDownReg);
            if (numOfNextReg == crtx.getNumOfRegions() - 1)
                buttonUP.setEnabled(false);
            }
    }

    private class ButtonDOWNListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            int numOfPrevReg = Integer.parseInt(numOfRegToDraw.getText()) - 1;
            if (numOfPrevReg  == crtx.getNumOfRegions() - 2)
                buttonUP.setEnabled(true);
            numOfRegToDraw.setText(String.valueOf(numOfPrevReg));

            indOfUpReg--;
            indOfDownReg--;
            draw(indOfUpReg, indOfDownReg);
            if (numOfPrevReg == 0)
                buttonDOWN.setEnabled(false);
        }
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
        ha = new HighlightableArea(crtx, indOfUpReg, indOfDownReg, curTime,(float)slider1.getValue()/100.0);
        ha.setBackground(Color.white);
        activeColsPanel.add(ha, BorderLayout.CENTER);
        activeColsPanel.setVisible(true);
    }

    public void setSettings(CortexThread crtx_){
        if (crtx_.cr != null){
            crtx = crtx_;
            if (crtx.getNumOfRegions() > 1)
                buttonUP.setEnabled(true);
        }
    }

    public void draw(int up_regInd, int down_regInd){
      if (crtx.cr != null){
            indOfUpReg = up_regInd;
            indOfDownReg = down_regInd;
            curTime = crtx.cr.time - 1 > 0 ? crtx.cr.time - 1 : 0;
            if (ha != null)
                activeColsPanel.remove(ha);
            AreaHighlightTest();
            activeColsPanel.setVisible(false);
            activeColsPanel.setVisible(true);
      }
    }
}

class HighlightableArea extends JPanel {

    private int hx = -1;
    private int hy = -1;
    private double otstup = 0.0;
    private double stepNaklInPerCent = 0.0;
    private int curTime;
    private CortexThread crtx;
    private final int dopuskForImage = 40 ;
    private int enlargeParameter = 1;
    private int imageCurrentWidth = 0;
    private int indOfUPreg = 0;
    private int indOfDOWNreg = 0;

    private int up_squaresNumPerW = 0;
    private int up_squaresNumPerH = 0;

    private int down_squaresNumPerW = 0;
    private int down_squaresNumPerH = 0;

    public HighlightableArea(CortexThread crtx_, int indOfUPreg_, int indOfDOWNreg_, int curTime_, double stepNaklInPerCent_) {

        this.indOfUPreg = indOfUPreg_;
        this.indOfDOWNreg = indOfDOWNreg_;
        this.crtx = crtx_;
        this.curTime = curTime_;
        this.stepNaklInPerCent = stepNaklInPerCent_;
        this.up_squaresNumPerW = crtx.cr.regions[indOfUPreg].getXDim();
        this.up_squaresNumPerH = crtx.cr.regions[indOfUPreg].getYDim();

        if (indOfDOWNreg != -1){
            this.down_squaresNumPerW = crtx.cr.regions[indOfDOWNreg].getXDim();
            this.down_squaresNumPerH = crtx.cr.regions[indOfDOWNreg].getYDim();
        }
        else {
            this.down_squaresNumPerW = crtx.cr.regions[0].getInputXDim();
            this.down_squaresNumPerH = crtx.cr.regions[0].getInputYDim();
        }

        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());
    }

    public void paintGrid(){
        this.repaint();
    }

    /*
    //DO NOT DELETE !!!

    public Point findPointForLinksVis(Point p1, Point p2, Point pp1, Point pp2){
        Point p = new Point();
        int ch_x = (p1.x*pp1.x*p2.y -
                p2.x*pp1.x*p1.y -
                p1.x*pp2.x*p2.y +
                p2.x*pp2.x*p1.y-
                p1.x*pp1.x*pp2.y+
                p1.x*pp2.x*pp1.y+
                p2.x*pp1.x*pp2.y-
                p2.x*pp2.x*pp1.y);
        int zn = (pp1.x*p1.y -
                 p1.x*pp1.y -
                 pp1.x*p2.y -
                 pp2.x*p1.y +
                 p1.x*pp2.y +
                 p2.x*pp1.y +
                 pp2.x*p2.y -
                 p2.x*pp2.y);
        int ch_y = (p1.x*p2.y*pp1.y -
                p2.x*pp1.y*p1.y -
                p1.x*pp2.y*p2.y +
                p2.x*pp2.y*p1.y-
                p1.y*pp1.x*pp2.y+
                p1.y*pp2.x*pp1.y+
                p2.y*pp1.x*pp2.y-
                p2.y*pp2.x*pp1.y);
        p.x = -(int)(ch_x / zn);
        p.y = -(int)(ch_y / zn);
        return p;
    }
    */

    private void drawFilledRectangle(Graphics2D g2, int hx, int hy, double dx, double dy, Color color, int colInd) {
        g2.setColor(color);
        //highlight the square - column
        int up_left_x = (int) (otstup - hy * dy + dx * hx);
        int down_left_x = (int) (up_left_x - Math.sqrt((2 * Math.pow(otstup, 2.0) / Math.pow(up_squaresNumPerH, 2.0)) - Math.pow(dy, 2.0)));
        int down_right_x = (int) (down_left_x + dx + 1);
        int up_right_x = (int) (up_left_x + dx + 1);

        int up_left_y = (int) (dy * hy + 0.5);
        int down_left_y = (int) (up_left_y + dy + 0.5);
        int down_right_y = down_left_y;
        int up_right_y = up_left_y;

        g2.fillPolygon(new int[]{up_left_x, down_left_x, down_right_x, up_right_x},
                new int[]{up_left_y, down_left_y, down_right_y, up_right_y}
                , 4);

        if (colInd >= 0) {
            //highlight the square - input
            int input_square_w = imageCurrentWidth / down_squaresNumPerW; ///////////////////////////
            for (Synapse s : crtx.cr.regions[indOfUPreg].columns[colInd].connectedSynapses) {
                if (s == null) break;

                int up_left_for_input_x = dopuskForImage + input_square_w * s.c;
                int up_left_for_input_y = (int) (otstup + dopuskForImage + input_square_w * s.i);

                int up_right_for_input_x = up_left_for_input_x + input_square_w;
                int up_right_for_input_y = up_left_for_input_y;

                int down_left_for_input_x = up_left_for_input_x;
                int down_left_for_input_y = up_left_for_input_y + input_square_w;

                int down_right_for_input_x = down_left_for_input_x + input_square_w;
                int down_right_for_input_y = down_left_for_input_y;

                g2.setColor(Color.red);
               /* g2.fillPolygon(new int[]{up_left_for_input_x, down_left_for_input_x, down_right_for_input_x, up_right_for_input_x},
                        new int[]{up_left_for_input_y, down_left_for_input_y, down_right_for_input_y, up_right_for_input_y}
                        , 4); */
                g2.fillOval((up_right_for_input_x - up_left_for_input_x)/2 + up_left_for_input_x , (down_left_for_input_y - up_left_for_input_y) /2 + up_left_for_input_y , 4,4);
                /*
                //draw lines and "sun", DO NOT DELETE !!!

                int x1 = down_left_x + (up_right_x - down_left_x) / 2;
                int y1 = up_left_y + (down_left_y - up_left_y) / 2;
                int x2 = up_left_for_input_x + (up_right_for_input_x - up_left_for_input_x) / 2;
                int y2 =  up_left_for_input_y + (down_left_for_input_y - up_left_for_input_y) / 2;
                //g2.drawLine(x1,y1,x2,y2);

                g2.setColor(Color.yellow);
                Point potentPoint1;
                Point potentPoint2;
                Point pp1_1;
                Point pp1_2;
                Point p1 = new Point(x1, y1);
                Point p2 = new Point(x2, y2);
                Point pp2 = new Point((int)(getWidth() - otstup),(int)otstup);
                pp1_1 = new Point(getWidth(), 0);
                potentPoint1 = findPointForLinksVis(p1,p2, pp1_1, pp2);
                pp1_2 = new Point(0, (int)otstup);
                potentPoint2 = findPointForLinksVis(p1,p2, pp1_2, pp2);
                int sq1 = (int)Math.sqrt(Math.pow(x1 - potentPoint1.x, 2.0) + Math.pow(y1 - potentPoint1.y, 2.0));
                int sq2 = (int)Math.sqrt(Math.pow(x1 - potentPoint2.x, 2.0) + Math.pow(y1 - potentPoint2.y, 2.0));
                   if (sq1 <= sq2 && y1 < potentPoint1.y)
                        g2.drawLine(potentPoint1.x +2, potentPoint1.y+1, x2, y2);
                    else
                        g2.drawLine(potentPoint2.x +2, potentPoint2.y+1, x2, y2);
                */
            }
        }
    }

    private void countEnlargeParameter(BufferedImage image){
        int enlargeParameterW = (int)(getWidth() - dopuskForImage * 2) / image.getWidth();
        int enlargeParameterH = (int)(getHeight() - dopuskForImage * 2 - otstup) / image.getHeight();
        enlargeParameter = Math.min(enlargeParameterH, enlargeParameterW);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        super.paintComponent(g2);

        this.otstup = getHeight() * stepNaklInPerCent;
        double dx = (getWidth() - otstup) / up_squaresNumPerW;
        double dy = otstup / up_squaresNumPerH;

        BitMatrix m = new BitMatrix(up_squaresNumPerW , up_squaresNumPerH);
        m = crtx.cr.getColumnsMapAtT(indOfUPreg, curTime);
        Color c;
        for (int j = 0; j <  crtx.cr.regions[indOfUPreg].getYDim(); j++)  //////////////////////////!!!!!!!!!!!!!!!!!!!
            for (int i = 0; i < crtx.cr.regions[indOfUPreg].getXDim(); i++)
            {
                c = (m.get(i, j) == false) ?  Color.gray : Color.lightGray;
                drawFilledRectangle(g2, i, j, dx, dy, c , -1);
            }

        g2.setColor(Color.darkGray);
        if (up_squaresNumPerW * up_squaresNumPerH != 0){

            for (int i = 0; i <= up_squaresNumPerW; i++) {
                g2.drawLine((int)(otstup + dx * i), 0, (int) (dx * i), (int)otstup);
            }

            for (int i = 0; i <= up_squaresNumPerH; i++) {
                g2.drawLine((int)(otstup - dy * i), (int) (dy * i), (int)(getWidth() - dy * i), (int) (dy * i));
            }

        if (indOfDOWNreg == -1) {
            /////////////////////////////////////////////////////////////////////
            //      image painting
            countEnlargeParameter(crtx.img.getBufferedImage());
            imageCurrentWidth = (enlargeParameter + 1) * crtx.img.getW() + 1;
            BufferedImage enlargedImage =
                    new BufferedImage((enlargeParameter + 1) * crtx.img.getW() + 1, (enlargeParameter + 1) * crtx.img.getH() + 1, crtx.img.getImageType());
            enlargedImage = crtx.img.enlarge(enlargeParameter);
            g2.drawImage(enlargedImage, dopuskForImage, (int) otstup + dopuskForImage, null);
            /////////////////////////////////////////////////////////////////////
        }
        else
        {
            BitMatrix matr = new BitMatrix(down_squaresNumPerW , down_squaresNumPerH);
            matr = crtx.cr.getColumnsMapAtT(indOfDOWNreg, curTime);
            ImageClass temp = new ImageClass();
            temp.setBufferedImage(temp.createBufferedImFromBitMatrix(matr, down_squaresNumPerW, down_squaresNumPerH));
            countEnlargeParameter(temp.getBufferedImage());
            imageCurrentWidth = (enlargeParameter + 1) * temp.getW() + 1;
            BufferedImage enlargedImage =
                    new BufferedImage((enlargeParameter + 1) * temp.getW() + 1, (enlargeParameter + 1) * temp.getH() + 1, temp.getImageType());
            enlargedImage = temp.enlarge(enlargeParameter);
            g2.drawImage(enlargedImage, dopuskForImage, (int) otstup + dopuskForImage, null);
        }

        if (hx >= 0 && hy >= 0) {
            //draw filled rectangle
            //draw column's links
            int columnInd = hy * up_squaresNumPerW + hx;
            drawFilledRectangle(g2, hx, hy, dx, dy,Color.yellow, columnInd);
        }
      }
    }

    private class MouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            double dx = ((double) getWidth() - otstup) / up_squaresNumPerW;
            double dy = otstup / up_squaresNumPerH;
            int ny = (int)(e.getY() / dy);
            double dx1 = (otstup - ny*dy) ;
            int nx = (int)((e.getX() - dx1) / dx);

            if (nx >=0 && nx < up_squaresNumPerW && ny >= 0 && ny < up_squaresNumPerH ){
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
            double dx = ((double) getWidth() - otstup) / up_squaresNumPerW;
            double dy = otstup / up_squaresNumPerH;
            int hy = (int)(e.getY() / dy);
            double dx1 = (otstup - hy*dy) ;
            int hx = (int)((e.getX() - dx1) / dx);

            System.out.print("x = " + hx + "; y = "+ hy +"\n");
            if (hx >=0 && hx < up_squaresNumPerW && hy >= 0 && hy < up_squaresNumPerH )
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

