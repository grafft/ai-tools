package ru.isa.ai.tests.fractals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Author: Aleksandr Panov
 * Date: 09.01.13
 * Time: 16:55
 */
public class IteratorTester {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            }
        });

        new IteratorFrame();
    }

    private static class IteratorFrame extends JFrame {
        private IteratorFrame() {
            super("Iterator Tester");

            this.getContentPane().setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            final IteratorDrawPanel drawPanel = new IteratorDrawPanel();

            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1.0;
            c.weightx = 1.0;
            c.gridy = 0;
            getContentPane().add(drawPanel, c);

            final Timer timer = new Timer(10, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    drawPanel.drawNewPoint();
                    getContentPane().repaint();
                }
            });
            timer.start();

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    timer.stop();
                    System.exit(0);
                }
            });

            pack();
            setSize(new Dimension(900, 600));
            setVisible(true);
        }
    }

    private static class IteratorDrawPanel extends JPanel {
        private ArrayList<Point2D> points = new ArrayList<>();
        private double p = -2;
        private double q = 0;
        private double scale = 500;

        private IteratorDrawPanel() {
            points.add(new Point2D.Double(0, 0));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension d = getSize();

            g2.setPaint(new Color(0, 0, 0));
            g2.drawLine(0, d.height / 2, d.width, d.height / 2);
            g2.drawLine(d.width / 2, 0, d.width / 2, d.height);

            for (Point2D point : points) {
                g2.drawOval((int) (scale * point.getX() - 1 + d.width / 2), (int) (scale * point.getY() - 1 + d.height / 2), 2, 2);
            }
        }

        public void drawNewPoint() {
            Point2D last = points.get(points.size() - 1);
            Point2D newPoint = new Point2D.Double(last.getX() * last.getX() - last.getY() * last.getY() + p, 2 * last.getX() * last.getY() + q);
            points.add(newPoint);
            System.out.println(String.format("Point: (%f, %f)", newPoint.getX(), newPoint.getY()));
        }
    }
}