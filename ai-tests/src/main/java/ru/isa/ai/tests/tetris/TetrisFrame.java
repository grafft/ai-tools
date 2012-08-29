package ru.isa.ai.tests.tetris;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 23.08.12
 * Time: 15:33
 */
public class TetrisFrame extends JFrame {
    private final BasicStroke stroke = new BasicStroke(2.0f);

    private Situation situation;
    private JLabel scoreLabel;

    public TetrisFrame(Map<Integer, Figure> figures) {
        super("Tetris v1.0");
        situation = new Situation(figures);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        getContentPane().setLayout(new GridBagLayout());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        scoreLabel = new JLabel("0");
        infoPanel.add(scoreLabel);

        JPanel previewPanel = new JPanel();
        Dimension size = new Dimension(100, 100);
        previewPanel.setMaximumSize(size);
        previewPanel.setPreferredSize(size);
        previewPanel.setMinimumSize(size);
        previewPanel.setLayout(new GridLayout(1, 1, 3, 3));
        previewPanel.setBorder(LineBorder.createBlackLineBorder());
        previewPanel.add(new PreviewPanel());
        infoPanel.add(previewPanel);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHEAST;
        c.insets = new Insets(3, 3, 3, 3);
        getContentPane().add(infoPanel, c);

        final WorkPanel workPanel = new WorkPanel();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.gridy = 0;
        getContentPane().add(workPanel, c);

        pack();
        setSize(new Dimension(500, 500));
        setVisible(true);
    }

    private class PreviewPanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension size = getSize();
            drawFigure(situation.getNextFigure(), g2,
                    new Point2D.Double(size.getWidth() / Situation.PREVIEW_SIZE, size.getHeight() / Situation.PREVIEW_SIZE));
        }
    }

    private class WorkPanel extends JPanel {

        public WorkPanel() {
            setBorder(LineBorder.createBlackLineBorder());
            addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 37) {
                        situation.moveFigure(-1, 0);
                    } else if (e.getKeyCode() == 40) {
                        situation.moveFigure(0, 1);
                    } else if (e.getKeyCode() == 39) {
                        situation.moveFigure(1, 0);
                    } else if (e.getKeyCode() == 38) {
                        situation.rotateFigure();
                    }
                    getContentPane().repaint();
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });

            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    situation.tick();
                    getContentPane().repaint();
                }
            });
            timer.start();

            setFocusable(true);
        }


        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension size = getSize();
            Point.Double d = new Point.Double(size.getWidth() / Situation.X, size.getHeight() / Situation.Y);
            drawFigure(situation.getCurrentFigure(), g2, d);
            for (Block block : situation.getStaticBlocks()) {
                drawBlock(block, new Point(0, 0), g2, d);
            }
            scoreLabel.setText("" + situation.getScore());
        }

    }

    private void drawFigure(Figure figure, Graphics2D g2, Point.Double d) {
        if (figure != null) {
            for (Block block : figure.getBlocks()) {
                drawBlock(block, new Point(figure.getxCoord(), figure.getyCoord()), g2, d);
            }
        }
    }

    private void drawBlock(Block block, Point startPoint, Graphics2D g2, Point.Double d) {
        g2.setStroke(stroke);
        g2.setPaint(Situation.COLORS[block.getColorIndex()]);
        double startX = (startPoint.x + block.getxCoord()) * d.getX();
        double startY = (startPoint.y + block.getyCoord()) * d.getY();
        g2.fill(new Rectangle2D.Double(startX, startY, d.getX(), d.getY()));
    }
}
