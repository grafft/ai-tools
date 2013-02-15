package ru.isa.ai.tests.fractals;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * Author: Aleksandr Panov
 * Date: 14.02.13
 * Time: 17:21
 */
public class JuliaSetViewer extends JFrame {

    private JLabel cLabel;
    private Timer timer;

    //    private double p = -0.1940;
//    private double q = -0.6557;
    private double p = -0.481762;
    private double q = -0.531657;

    private static final double DEFAULT_MIN = -1.5;
    private static final double DEFAULT_MAX = 1.5;

    private double xMin = -1.5;
    private double xMax = 1.5;
    private double yMin = -1.5;
    private double yMax = 1.5;

    private int M = 300;
    private int K = 1000;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JuliaSetViewer();
            }
        });
    }

    public JuliaSetViewer() {
        super("Julia Set");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        getContentPane().setLayout(new GridBagLayout());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        cLabel = new JLabel(String.format("z\u2192z\u00B2%+.3f%+.3fi", p, q), SwingConstants.LEFT);
        infoPanel.add(cLabel);

        JPanel previewPanel = new JPanel();
        Dimension size = new Dimension(100, 100);
        previewPanel.setMaximumSize(size);
        previewPanel.setPreferredSize(size);
        previewPanel.setMinimumSize(size);
        previewPanel.setLayout(new GridLayout(1, 1, 3, 3));
        previewPanel.setBorder(LineBorder.createBlackLineBorder());
        previewPanel.add(new PreviewPanel());
        infoPanel.add(previewPanel);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!timer.isRunning()) {
                    timer.start();
                    ((JButton) e.getSource()).setText("Stop");
                } else {
                    timer.stop();
                    ((JButton) e.getSource()).setText("Start");
                }
            }
        });

        infoPanel.add(startButton);

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
        setSize(new Dimension(1006, 900));
        setVisible(true);
    }

    private class PreviewPanel extends JPanel {

        private PreviewPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        xMin = yMin = DEFAULT_MIN;
                        xMax = yMax = DEFAULT_MAX;
                        getContentPane().repaint();
                    }
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    private class WorkPanel extends JPanel {

        private double currentX;
        private double currentY;
        private double deltaX;
        private double deltaY;

        private int iteration;
        private Timer iterateTimer;

        private Rectangle currentRect = null;
        private Rectangle rectToDraw = null;
        private Rectangle previousRectDrawn = new Rectangle();

        private boolean precisionChanged = false;
        private BufferedImage bufferedImage;
        private JuliaTimer redrawTimer;
        private JProgressBar progressBar;
        private Point[] points = new Point[K + 1];

        public WorkPanel() {
            setBorder(LineBorder.createBlackLineBorder());
            setLayout(new GridBagLayout());
            iterateTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (iteration < K) {
                        double tmp = currentX;
                        currentX = currentX * currentX - currentY * currentY + p;
                        currentY = 2 * tmp * currentY + q;
                        points[iteration + 1] = new Point((int) ((currentX - xMin) / deltaX), (int) ((currentY - yMin) / deltaY));
                        iteration++;
                    } else {
                        ((Timer) e.getSource()).stop();
                    }
                    getContentPane().repaint();
                }
            });

            redrawTimer = new JuliaTimer();
            redrawTimer.setRepeats(false);

            JuliaMouseListener listener = new JuliaMouseListener();
            addMouseMotionListener(listener);
            addMouseListener(listener);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    Dimension size = ((JPanel) e.getSource()).getSize();
                    bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
                    if (redrawTimer.isRunning()) {
                        redrawTimer.restart();
                    } else {
                        redrawTimer.start();
                    }
                }
            });

            progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
            progressBar.setStringPainted(true);
            progressBar.setMinimum(0);
            progressBar.setVisible(false);
            add(progressBar);

            timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    q = q + 0.01;
                    cLabel.setText(String.format("%.3f + %.3fi", p, q));
                    getContentPane().repaint();
                }
            });
            setFocusable(true);
        }

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);

            final Dimension size = getSize();
            if (bufferedImage == null || precisionChanged) {
                precisionChanged = false;

                bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

                if (redrawTimer.isRunning()) {
                    redrawTimer.restart();
                } else {
                    redrawTimer.start();
                }
            }

            if (!redrawTimer.isRunning()) {
                progressBar.setVisible(false);
                g.drawImage(bufferedImage, 0, 0, this);

                g.setColor(Color.red);
                for (int i = 0; i < points.length - 1; i++) {
                    if (points[i] != null && points[i + 1] != null) {
                        g.drawLine(points[i].x, points[i].y, points[i + 1].x, points[i + 1].y);
                    }
                }

                g.setColor(Color.green);
                if (currentRect != null) {
                    g.drawRect(rectToDraw.x, rectToDraw.y, rectToDraw.width - 1, rectToDraw.height - 1);
                }
            } else {
                progressBar.setVisible(true);
            }
        }

        private void updateDrawableRect(int compWidth, int compHeight) {
            int x = currentRect.x;
            int y = currentRect.y;
            int width = currentRect.width;
            int height = currentRect.height;

            //Make the width and height positive, if necessary.
            if (width < 0) {
                width = 0 - width;
                x = x - width + 1;
                if (x < 0) {
                    width += x;
                    x = 0;
                }
            }
            if (height < 0) {
                height = 0 - height;
                y = y - height + 1;
                if (y < 0) {
                    height += y;
                    y = 0;
                }
            }

            //The rectangle shouldn't extend past the drawing area.
            if ((x + width) > compWidth) {
                width = compWidth - x;
            }
            if ((y + height) > compHeight) {
                height = compHeight - y;
            }

            //Update rectToDraw after saving old value.
            if (rectToDraw != null) {
                previousRectDrawn.setBounds(rectToDraw.x, rectToDraw.y, rectToDraw.width, rectToDraw.height);
                rectToDraw.setBounds(x, y, width, height);
            } else {
                rectToDraw = new Rectangle(x, y, width, height);
            }
        }

        private class JuliaTimer extends Timer {

            public JuliaTimer() {
                super(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Graphics2D g2 = bufferedImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        Dimension size = WorkPanel.this.getSize();

                        double deltaX = (xMax - xMin) / (size.width - 1);
                        double deltaY = (yMax - yMin) / (size.height - 1);

                        progressBar.setMaximum((size.width - 1) * size.height);
                        for (int nx = 0; nx < size.width; nx++) {
                            for (int ny = 0; ny < size.height; ny++) {
                                double x = xMin + nx * deltaX;
                                double y = yMin + ny * deltaY;
                                int k = 0;
                                double r = x * x + y * y;

                                while (k < K && r <= M) {
                                    double tmp = x;
                                    x = x * x - y * y + p;
                                    y = 2 * tmp * y + q;
                                    r = x * x + y * y;
                                    k++;
                                }

                                if (k == K) {
                                    k = 0;
                                }
                                g2.setColor(k == 0 ? Color.black : new Color(0, 0, 255 - k % 200));
                                g2.drawLine(nx, ny, nx, ny);
                                progressBar.setValue(nx * size.height + ny);
                            }
                        }

                        WorkPanel.this.repaint();
                    }
                });

            }
        }

        private class JuliaMouseListener extends MouseInputAdapter {
            private long pressTime;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (iterateTimer.isRunning()) {
                    iterateTimer.stop();
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    for (int i = 0; i < points.length; i++) {
                        points[i] = null;
                    }
                    return;
                }
                Dimension size = WorkPanel.this.getSize();

                deltaX = (xMax - xMin) / (size.width - 1);
                deltaY = (yMax - yMin) / (size.height - 1);

                currentX = xMin + e.getX() * deltaX;
                currentY = yMin + e.getY() * deltaY;
                iteration = 0;
                for (int i = 0; i < points.length; i++) {
                    points[i] = null;
                }
                points[0] = e.getPoint();

                iterateTimer.start();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressTime = System.currentTimeMillis();
                int x = e.getX();
                int y = e.getY();
                currentRect = new Rectangle(x, y, 0, 0);
                updateDrawableRect(getWidth(), getHeight());
                WorkPanel.this.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentRect = null;
                if ((System.currentTimeMillis() - pressTime) > 100) {
                    precisionChanged = true;
                    double tmp = xMin;

                    Dimension size = WorkPanel.this.getSize();
                    double deltaX = (xMax - xMin) / (size.width - 1);
                    double deltaY = (yMax - yMin) / (size.height - 1);
                    xMin = tmp + rectToDraw.x * deltaX;
                    xMax = tmp + (rectToDraw.x + rectToDraw.width - 1) * deltaX;

                    tmp = yMin;
                    yMin = tmp + rectToDraw.y * deltaY;
                    yMax = tmp + (rectToDraw.y + rectToDraw.height - 1) * deltaY;
                    WorkPanel.this.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateSize(e);
            }

            void updateSize(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                currentRect.setSize(x - currentRect.x, y - currentRect.y);
                updateDrawableRect(getWidth(), getHeight());
                Rectangle totalRepaint = rectToDraw.union(previousRectDrawn);
                WorkPanel.this.repaint(totalRepaint.x, totalRepaint.y, totalRepaint.width, totalRepaint.height);
            }
        }
    }
}
