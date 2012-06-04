package ru.isa.ai.tests;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Aleksandr Panov
 * Date: 25.05.12
 * Time: 15:06
 */
public class WRModelButtonVisualizer {
    public static final int X = 100;
    public static final int Y = 100;

    public static final int BUTTON_SIZE_X = 25;
    public static final int BUTTON_SIZE_Y = 25;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                WienerRosenbluethAutomatonModel model = new WienerRosenbluethAutomatonModel(X, Y);
                new AutomataWorldFrame(model);
            }
        });

    }

    private static class AutomataWorldFrame extends JFrame {
        private JButton[][] buttons = new JButton[X][Y];

        private Color[] colors;

        private AutomataWorldFrame(final WienerRosenbluethAutomatonModel model) {
            super("Wiener Rosenblueth Automata");
            colors = new Color[model.getStateCount()];
            for (int i = 0; i < model.getStateCount(); i++) {
                int greyInt = 255 * (model.getStateCount() - i) / model.getStateCount();
                colors[i] = new Color(greyInt, greyInt, greyInt);
            }

            this.getContentPane().setLayout(new GridLayout(Y + 1, X));
            for (int j = 0; j < Y; j++) {
                for (int i = 0; i < X; i++) {
                    final int x = i;
                    final int y = j;
                    buttons[j][i] = new JButton();
                    buttons[j][i].setBackground(colors[model.getElement(i, j).getPhi()]);
                    buttons[j][i].setBorder(LineBorder.createBlackLineBorder());

                    buttons[j][i].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int oldPhi = model.getElement(x, y).getPhi();
                            if (oldPhi < model.getStateCount() - 1) {
                                model.setElement(oldPhi + 1, model.getElement(x, y).getU(), x, y);
                                ((JButton) e.getSource()).setBackground(colors[oldPhi + 1]);
                            } else {
                                model.setElement(0, model.getElement(x, y).getU(), x, y);
                                ((JButton) e.getSource()).setBackground(colors[0]);
                            }
                            System.out.println(x + ", " + y);
                        }
                    });
                    this.getContentPane().add(buttons[j][i]);
                }
            }
            this.setSize(BUTTON_SIZE_X * X, BUTTON_SIZE_Y * Y);
            // pack();
            this.setVisible(true);
            this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            final Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    model.tick();
                    for (int i = 0; i < X; i++) {
                        for (int j = 0; j < Y; j++) {
                            buttons[j][i].setBackground(colors[model.getElement(i, j).getPhi()]);
                        }
                    }
                    System.out.println("Tick " + model.getT());
                }
            });

            JButton startButton = new JButton("Start");
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
            this.getContentPane().add(startButton);
        }
    }
}
