package ru.isa.ai.tests;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

/**
 * Author: Aleksandr Panov
 * Date: 31.05.12
 * Time: 11:44
 */
public class WRModelContVisualizer {
    public static final int X = 100;
    public static final int Y = 100;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                WienerRosenbluethAutomatonModel model = new WienerRosenbluethAutomatonModel(X, Y);

                Color[] colors = new Color[model.getStateCount()];
                for (int i = 0; i < model.getStateCount(); i++) {
                    int greyInt = 255 * (model.getStateCount() - i) / model.getStateCount();
                    colors[i] = new Color(greyInt, greyInt, greyInt);
                }

                new AutomataWorldFrame(colors, model);
            }
        });
    }

    private static class AutomataWorldDrawPanel extends JPanel {
        final static BasicStroke stroke = new BasicStroke(2.0f);
        private Color[] colors;
        private int selectedColorIndex = 0;

        private WienerRosenbluethAutomatonModel model;

        private AutomataWorldDrawPanel(Color[] colors, final WienerRosenbluethAutomatonModel model) {
            this.model = model;
            this.colors = colors;

            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    AutomataWorldDrawPanel.this.setColorToElement(e.getX(), e.getY());
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    //Nothing
                }
            });

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    AutomataWorldDrawPanel.this.setColorToElement(e.getX(), e.getY());
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
                    if (model.getElement(i, j).getPhi() != 0) {
                        g2.setPaint(colors[model.getElement(i, j).getPhi()]);
                        g2.fill(new Rectangle2D.Double(i * elementWidth, j * elementHeight, elementWidth, elementHeight));
                    }
                }
            }
        }

        private void setColorToElement(int x, int y){
            Dimension d = getSize();
            int i = (int) (x / (d.getWidth() / X));
            int j = (int) (y / (d.getHeight() / Y));

            model.setElement(selectedColorIndex, 0, i, j);
            repaint();
        }

        public void setSelectedColorIndex(int buttonIndex) {
            selectedColorIndex = buttonIndex;
        }
    }

    private static class AutomataWorldFrame extends JFrame {
        private LineBorder redBorder = new LineBorder(Color.red, 2);

        private AutomataWorldFrame(Color[] colors, final WienerRosenbluethAutomatonModel model) {
            super("Wiener Rosenblueth Automata");
            final AutomataWorldDrawPanel drawPanel = new AutomataWorldDrawPanel(colors, model);

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
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

            JButton startButton = new JButton("Start");
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
            buttonPanel.add(startButton);


            JPanel colorPanel = new JPanel();
            Dimension size = new Dimension(150, 150);
            colorPanel.setMaximumSize(size);
            colorPanel.setPreferredSize(size);
            colorPanel.setMinimumSize(size);

            colorPanel.setLayout(new GridLayout(5, 1, 3, 3));
            TitledBorder border = new TitledBorder(new LineBorder(Color.black), "Colors", TitledBorder.CENTER, TitledBorder.BELOW_TOP);
            border.setTitleColor(Color.black);
            colorPanel.setBorder(border);

            final JButton[] colorButtons = new JButton[model.getStateCount()];
            for (int i = 0; i < model.getStateCount(); i++) {
                JButton colorButton = new JButton();
                colorButtons[i] = colorButton;
                final int buttonIndex = i;

                colorButton.setBackground(colors[i]);
                colorButton.setSize(50, 50);
                if (i == 0) {
                    colorButton.setBorder(redBorder);
                } else {
                    colorButton.setBorder(LineBorder.createBlackLineBorder());
                }
                colorButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (int i = 0; i < model.getStateCount(); i++) {
                            colorButtons[i].setBorder(LineBorder.createBlackLineBorder());
                        }
                        ((JButton) e.getSource()).setBorder(redBorder);
                        drawPanel.setSelectedColorIndex(buttonIndex);
                    }
                });
                colorPanel.add(colorButton);
            }
            buttonPanel.add(colorPanel);

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
            setSize(new Dimension(900, 600));
            setVisible(true);
        }
    }
}
