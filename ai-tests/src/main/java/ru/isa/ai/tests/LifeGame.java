package ru.isa.ai.tests;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

/**
 * Author: Aleksandr Panov
 * Date: 15.01.14
 * Time: 17:25
 */
public class LifeGame {
    public static final int X = 500;
    public static final int Y = 500;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LifeGameFrame(new LifeGameModel(X, Y));
            }
        });
    }

    private static class LifeGameModel {
        private final int sizeX;
        private final int sizeY;
        private int[][] elements;
        private int t;
        private double populationDynamic;
        private double population;
        private double lifeCoefficient;
        private int terminator;
        private boolean increase = true;

        public LifeGameModel(int x, int y) {
            this.sizeX = x;
            this.sizeY = y;
            this.elements = new int[sizeX][sizeY];
            this.terminator = 3 * sizeX / 4;
        }

        public void tick() {
            double oldPopDyn = populationDynamic;
            int[][] future = new int[sizeX][sizeY];
            population = 0;
            populationDynamic = 0;
            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    int counter = 0;
                    if (isLive(i - 1, j - 1)) counter++;
                    if (isLive(i - 1, j)) counter++;
                    if (isLive(i - 1, j + 1)) counter++;
                    if (isLive(i, j - 1)) counter++;
                    if (isLive(i, j + 1)) counter++;
                    if (isLive(i + 1, j - 1)) counter++;
                    if (isLive(i + 1, j)) counter++;
                    if (isLive(i + 1, j + 1)) counter++;

                    if (i > sizeX / 4 && i < terminator)
                        future[i][j] = ((elements[i][j] == 0 && counter == 3) || (elements[i][j] == 1 && counter > 1 && counter < 4)) ? 1 : 0;
                    else if (i >= 0 && i <= sizeX / 4)
                        future[i][j] = ((elements[i][j] == 0 && counter == 3) || (elements[i][j] == 1 && counter > 2 && counter < 4)) ? 1 : 0;
                    else
                        future[i][j] = ((elements[i][j] == 0 && counter == 3) || (elements[i][j] == 1 && counter > 1 && counter < 5)) ? 1 : 0;

                    if (future[i][j] > 0) population++;
                    if (future[i][j] != elements[i][j]) populationDynamic++;
                }
            }
            elements = future;
            populationDynamic = populationDynamic / population;
            population = population / (sizeX * sizeY);
            lifeCoefficient = populationDynamic - oldPopDyn;

            terminator = increase ? terminator + 1 : terminator - 1;
            if(terminator > 13 * sizeX / 15)
                increase = false;
            if(terminator < 7 * sizeX / 15)
                increase = true;
            t++;
        }

        public void changeLive(int i, int j) {
            if (i >= 0 && i < sizeX && j >= 0 && j < sizeY)
                elements[i][j] = elements[i][j] == 1 ? 0 : 1;
        }

        public boolean isLive(int i, int j) {
            if (i < 0) i = sizeX - 1;
            if (j < 0) j = sizeY - 1;
            if (i >= sizeX) i = 0;
            if (j >= sizeY) j = 0;
            return elements[i][j] == 1;
        }

        public int getT() {
            return t;
        }

        public void clear() {
            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    elements[i][j] = 0;
                }
            }
        }

        public void generate(double v) {
            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    elements[i][j] = Math.random() < v ? 1 : 0;
                }
            }
        }

        public double getPopulationDynamic() {
            return populationDynamic;
        }

        public double getPopulation() {
            return population;
        }

        public double getLifeCoefficient() {
            return lifeCoefficient;
        }
    }

    private static class LifeGamePanel extends JPanel {
        final static BasicStroke stroke = new BasicStroke(2.0f);
        final static Color color = new Color(225, 225, 225);
        private LifeGameModel model;

        public LifeGamePanel(LifeGameModel model) {
            this.model = model;
            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    LifeGamePanel.this.setColorToElement(e.getX(), e.getY());
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    //Nothing
                }
            });

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    LifeGamePanel.this.setColorToElement(e.getX(), e.getY());
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    //Nothing
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    //Nothing
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    //Nothing
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    //Nothing
                }
            });

            setBackground(Color.WHITE);
            setBorder(LineBorder.createBlackLineBorder());
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension d = getSize();
            double elementWidth = d.getWidth() / X;
            double elementHeight = d.getHeight() / Y;

            g2.setStroke(stroke);
            for (int j = 0; j < Y; j++) {
                for (int i = 0; i < X; i++) {
                    if (model.isLive(i, j)) {
                        g2.setPaint(color);
                        g2.fill(new Rectangle2D.Double(i * elementWidth, j * elementHeight, elementWidth, elementHeight));
                    }
                }
            }
        }

        private void setColorToElement(int x, int y) {
            Dimension d = getSize();
            int i = (int) (x / (d.getWidth() / X));
            int j = (int) (y / (d.getHeight() / Y));

            model.changeLive(i, j);
            repaint();
        }
    }

    private static class LifeGameFrame extends JFrame {
        public LifeGameFrame(final LifeGameModel model) {
            super("Life Game");
            final LifeGamePanel drawPanel = new LifeGamePanel(model);
            final JLabel popLabel = new JLabel("0.0");
            final JLabel popDinLabel = new JLabel("0.0");
            final JLabel lifeCofLabel = new JLabel("0.0");

            this.getContentPane().setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

            final Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    model.tick();
                    getContentPane().repaint();
                    System.out.println("Tick " + model.getT());
                    popDinLabel.setText(String.format("%.4f", model.getPopulationDynamic()));
                    popLabel.setText(String.format("%.4f", model.getPopulation()));
                    lifeCofLabel.setText(String.format("%.4f", model.getLifeCoefficient()));
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

            JButton startButton = new JButton("Start");
            JButton clearButton = new JButton("Clear");
            JButton generateButton = new JButton("Generate");
            final JTextField population = new JTextField("0.0");
            //startButton.setSize(30, 10);
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton component = (JButton) e.getSource();
                    if (component.getText().equals("Start")) {
                        component.setText("Stop");
                        timer.start();
                    } else {
                        component.setText("Start");
                        timer.stop();
                    }
                }
            });
            clearButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    model.clear();
                    getContentPane().repaint();
                }
            });
            generateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        model.generate(Double.parseDouble(population.getText()));
                    } catch (NumberFormatException ex) {
                        population.setText("Error!");
                    }
                    getContentPane().repaint();
                }
            });

            buttonPanel.add(startButton);
            buttonPanel.add(clearButton);
            buttonPanel.add(generateButton);
            buttonPanel.add(population);
            buttonPanel.add(popDinLabel);
            buttonPanel.add(popLabel);
            buttonPanel.add(lifeCofLabel);

            c.anchor = GridBagConstraints.LINE_START;
            c.insets = new Insets(3, 3, 3, 3);
            this.getContentPane().add(buttonPanel, c);

            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1.0;
            c.weightx = 1.0;
            c.gridy = 0;
            getContentPane().add(drawPanel, c);

            pack();
            setSize(new Dimension(600, 600));
            setVisible(true);
        }
    }
}
