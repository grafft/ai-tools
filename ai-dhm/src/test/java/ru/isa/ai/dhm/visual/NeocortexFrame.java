package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.RegionSettings;
import ru.isa.ai.dhm.core2.Column;
import ru.isa.ai.dhm.core2.Neocortex;
import ru.isa.ai.dhm.core2.Region;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

/**
 * Author: Aleksandr Panov
 * Date: 03.09.2014
 * Time: 14:00
 */
public class NeocortexFrame extends JFrame {

    private static final int INPUT_SIZE = 100;
    private Neocortex neocortex;
    private RegionSettings settings;
    private BasicStroke regionStroke = new BasicStroke(2.0f);
    private BasicStroke columnStroke = new BasicStroke(1.0f);
    private Color activeColor = Color.RED;
    private Color passiveColor = new Color(230, 230, 230);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NeocortexFrame();
            }
        });
    }

    public NeocortexFrame() throws HeadlessException {
        super("DHM test");
        initCortex();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        getContentPane().setLayout(new GridBagLayout());
        JPanel infoPanel = new JPanel();
        Dimension size = new Dimension(100, 300);
        infoPanel.setMaximumSize(size);
        infoPanel.setPreferredSize(size);
        infoPanel.setMinimumSize(size);
        infoPanel.setLayout(new GridLayout(10, 1, 7, 7));
        infoPanel.setBorder(LineBorder.createBlackLineBorder());
        JButton stepButton = new JButton("Step");
        JButton stButton = new JButton("Start");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BitVector input = new BitVector(settings.xInput * settings.yInput);
                for (int i = 0; i < settings.xInput * settings.yInput; i++) {
                    if (Math.random() > 0.3)
                        input.set(i);
                }
                neocortex.iterate(input);
                getContentPane().repaint();
            }
        });
        final Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BitVector input = new BitVector(settings.xInput * settings.yInput);
                for (int i = 0; i < settings.xInput * settings.yInput; i++) {
                    if (Math.random() > 0.3)
                        input.set(i);
                }
                neocortex.iterate(input);
                getContentPane().repaint();
            }
        });
        stButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!timer.isRunning()){
                    timer.start();
                    ((JButton)e.getSource()).setText("Stop");
                }else{
                    timer.stop();
                    ((JButton)e.getSource()).setText("Start");
                }
            }
        });
        infoPanel.add(stepButton);
        infoPanel.add(stButton);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHEAST;
        c.insets = new Insets(3, 3, 3, 3);
        getContentPane().add(infoPanel, c);

        final NecortexPanel cortexPanel = new NecortexPanel();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.gridy = 0;
        getContentPane().add(cortexPanel, c);

        pack();
        setSize(new Dimension(900, 800));
        setVisible(true);
    }

    private void initCortex() {
        settings = RegionSettings.getDefaultSettings();
        neocortex = new Neocortex();
        Region region1 = new Region(settings);
        Region region2 = new Region(settings);
        region2.addParent(region1);
        region1.addChild(region2);
        neocortex.addRegion(region2);
        neocortex.addRegion(region1);
        neocortex.initialization();
    }

    private class NecortexPanel extends JPanel {
        private NecortexPanel() {
            setFocusable(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Dimension size = getSize();

            int heightInset = 30;
            int widthInset = 40;
            double regionHeight = (size.getHeight() - heightInset * (neocortex.getRegions().size() + 1)) / neocortex.getRegions().size();
            double regionWidth = size.getWidth() - widthInset * 2;

            int count = 0;
            for (Region region : neocortex.getRegions()) {
                double lowY = heightInset * (count + 1) + count * regionHeight;
                double lowX = widthInset;
                double columnHeight = regionHeight / settings.xDimension;
                double columnWidth = regionWidth / settings.yDimension;

                g2.setPaint(Color.BLACK);
                g2.setStroke(regionStroke);
                g2.draw(new Rectangle2D.Double(lowX, lowY, regionWidth, regionHeight));
                for (Column column : region.getColumns().values()) {
                    g2.setPaint(region.getActiveColumns().getQuick(column.getIndex()) ? activeColor : passiveColor);
                    g2.fill(new Rectangle2D.Double(lowX + columnWidth * column.getCoords()[1], lowY + columnHeight * column.getCoords()[0], columnWidth, columnHeight));
                    g2.setPaint(Color.BLACK);
                    g2.setStroke(columnStroke);
                    g2.draw(new Rectangle2D.Double(lowX + columnWidth * column.getCoords()[1], lowY + columnHeight * column.getCoords()[0],
                            columnWidth, columnHeight));
                }
                count++;
            }
        }
    }
}
