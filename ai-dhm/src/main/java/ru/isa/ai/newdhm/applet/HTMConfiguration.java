package ru.isa.ai.newdhm.applet;

import info.monitorenter.gui.chart.Chart2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isa.ai.newdhm.CortexThread;
import ru.isa.ai.newdhm.RegionInitializationException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import ru.isa.ai.newdhm.Region;


public class HTMConfiguration {
    private JTextField textField1;
    private JTextField textField2;
    private JPanel mainPanel;
    private JButton runCortexButton;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField8;
    private JTextField textField9;
    private JTextField textField10;
    private JTextField textField11;
    private JTextField textField12;
    public JTextPane textPane1;
    private Chart2D chart2D1;
    private JButton stopCortexButton;
    public JCheckBox showDendritesGraphCheckBox;
    public JCheckBox showSynapsesPermanenceCheckBox;
    public JCheckBox showActiveCellsCheckBox;
    public JCheckBox showPredictiveCellsCheckBox;
    public JCheckBox showLearningCellsCheckBox;
    public JCheckBox showOverlapsCheckBox;
    public JCheckBox showActiveDutyCycleCheckBox;
    public JCheckBox showMinDutyCycleCheckBox;
    public JCheckBox showBoostCheckBox;
    public JCheckBox showOverlapsDutyCycleCheckBox;
    private JButton makeStepButton;
    private JButton showExtendedGUIButton;
    public JCheckBox inputsGraphicsCheckBox;
    private JTabbedPane tabbedPane1;
    private Chart2D chart2D2;
    private JPanel casmiPanel;
    public JCheckBox drawDendritesTimlineCheckBox;
    private JButton LoadPropertiesFromFileButton;
    private JButton showActiveColumnsButton;

    public CortexThread crtx = new CortexThread();
    static HTMConfiguration panel;

    private final String SP_PROP_FILENAME = "htm.properties";
    private String filePropName = SP_PROP_FILENAME;
    // загрузка свойств из файла



    public HTMConfiguration () {
        runCortexButton.addActionListener(new RunCortexButtonListener());
        stopCortexButton.addActionListener(new StopCortexButtonListener());
        makeStepButton.addActionListener(new MakeStepButtonListener());
        showExtendedGUIButton.addActionListener(new ShowExtendedGUIListener());
        showActiveColumnsButton.addActionListener(new ShowActiveColumnsListener());
        LoadPropertiesFromFileButton.addActionListener(new LoadPropertiesButtonGUIListener());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("HTMConfiguration");
        panel = new HTMConfiguration();
        frame.setContentPane(panel.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void InitCortex() {
        crtx = new CortexThread();
        try {
            crtx.r.region.desiredLocalActivity = new Integer(textField1.getText());
            crtx.r.region.minOverlap = new Integer(textField2.getText());
            crtx.r.region.connectedPerm = new Double(textField3.getText());
            crtx.r.region.permanenceInc = new Double(textField4.getText());
            crtx.r.region.permanenceDec = new Double(textField5.getText());
            crtx.r.region.cellsPerColumn = new Integer(textField6.getText());
            crtx.r.region.activationThreshold = new Integer(textField7.getText());
            crtx.r.region.initialPerm = new Double(textField8.getText());
            crtx.r.region.minThreshold = new Integer(textField9.getText());
            crtx.r.region.newSynapseCount = new Integer(textField10.getText());
            crtx.r.region.xDimension = new Integer(textField11.getText());
            crtx.r.region.yDimension = new Integer(textField12.getText());
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        crtx.r.region.addColumns();
        crtx.Init(chart2D1, chart2D2, panel);
    }

    public void loadProperties() throws RegionInitializationException {
        Logger logger = LogManager.getLogger(Region.class.getSimpleName());
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(filePropName);
            properties.load(input);
            for (String name : properties.stringPropertyNames()) {
                switch (name) {
                    case "desiredLocalActivity":
                        crtx.r.region.desiredLocalActivity = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "minOverlap":
                        crtx.r.region.minOverlap = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "connectedPerm":
                        crtx.r.region.connectedPerm = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "permanenceInc":
                        crtx.r.region.permanenceInc = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "permanenceDec":
                        crtx.r.region.permanenceDec = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "cellsPerColumn":
                        crtx.r.region.cellsPerColumn = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "activationThreshold":
                        crtx.r.region.activationThreshold = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "initialPerm":
                        crtx.r.region.initialPerm = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "minThreshold":
                        crtx.r.region.minThreshold = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "newSynapseCount":
                        crtx.r.region.newSynapseCount = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "xDimension":
                        crtx.r.region.xDimension = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "yDimension":
                        crtx.r.region.yDimension = Integer.parseInt(properties.getProperty(name));
                        break;
                    default:
                        logger.error("Illegal property name: " + name);
                        break;
                }
            }
            input.close();
        } catch (IOException e) {
            throw new RegionInitializationException("Cannot load properties file " + filePropName, e);
        } catch (NumberFormatException nfe) {
            throw new RegionInitializationException("Wrong property value in property file " + filePropName, nfe);
        }
    }


    /*
    private void checkProperties() throws RegionInitializationException {
        if (numColumns <= 0)
            throw new RegionInitializationException("Column dimensions must be non zero positive values");
        if (numInputs <= 0)
            throw new RegionInitializationException("Input dimensions must be non zero positive values");
        if (numActiveColumnsPerInhArea <= 0 && (localAreaDensity <= 0 || localAreaDensity > 0.5))
            throw new RegionInitializationException("Or numActiveColumnsPerInhArea > 0 or localAreaDensity > 0 " +
                    "and localAreaDensity <= 0.5");
        if (potentialPct <= 0 || potentialPct > 1)
            throw new RegionInitializationException("potentialPct must be > 0 and <= 1");
        potentialRadius = potentialRadius > numInputs ? numInputs : potentialRadius;
    }
    */

    public void saveProperties() throws RegionInitializationException {
        Properties properties = new Properties();
        try {
            properties.setProperty("desiredLocalActivity",String.valueOf(crtx.r.region.desiredLocalActivity));
            properties.setProperty("minOverlap",String.valueOf(crtx.r.region.minOverlap));
            properties.setProperty("connectedPerm",String.valueOf(crtx.r.region.connectedPerm));
            properties.setProperty("permanenceInc", String.valueOf(crtx.r.region.permanenceInc));
            properties.setProperty("permanenceDec", String.valueOf(crtx.r.region.permanenceDec));
            properties.setProperty("activationThreshold",String.valueOf(crtx.r.region.activationThreshold));
            properties.setProperty("initialPerm",String.valueOf(crtx.r.region.initialPerm));
            properties.setProperty("minThreshold",String.valueOf(crtx.r.region.minThreshold));
            properties.setProperty("newSynapseCount",String.valueOf(crtx.r.region.newSynapseCount));
            properties.setProperty("xDimension",String.valueOf(crtx.r.region.xDimension));
            properties.setProperty("yDimension",String.valueOf(crtx.r.region.yDimension));

            FileOutputStream output = new FileOutputStream(filePropName);
            properties.store(output,"Saved settings");
            output.close();

        } catch (IOException e) {
            throw new RegionInitializationException("Cannot save properties file " + filePropName, e);
        }
    }


    public class LoadPropertiesButtonGUIListener implements ActionListener  {
        //crtx = new CortexThread();
        ////////////////////////////////////////////////////////

        public void actionPerformed (ActionEvent event) {
            try{
                loadProperties();
                textField1.setText(String.valueOf(crtx.r.region.desiredLocalActivity));
                textField2.setText(String.valueOf(crtx.r.region.minOverlap));
                textField3.setText(String.valueOf(crtx.r.region.connectedPerm));
                textField4.setText(String.valueOf(crtx.r.region.permanenceInc));
                textField5.setText(String.valueOf(crtx.r.region.permanenceDec));
                textField6.setText(String.valueOf(crtx.r.region.cellsPerColumn));
                textField7.setText(String.valueOf(crtx.r.region.activationThreshold));
                textField8.setText(String.valueOf(crtx.r.region.initialPerm));
                textField9.setText(String.valueOf(crtx.r.region.minThreshold));
                textField10.setText(String.valueOf(crtx.r.region.newSynapseCount));
                textField11.setText(String.valueOf(crtx.r.region.xDimension));
                textField12.setText(String.valueOf(crtx.r.region.yDimension));
            }
            catch (RegionInitializationException e){
                    System.out.println("caught " + e);
            }
        }

    }

    public class RunCortexButtonListener implements ActionListener {
        public void actionPerformed (ActionEvent event) {
            if (!crtx.isRunning()) {
                InitCortex();
                crtx.start();
            }
            else
                crtx.Continue();
        }
    }

    private class StopCortexButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            crtx.Quit();
        }
    }

    private class MakeStepButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (crtx.isRunning())
                crtx.MakeStep();
            else {
                InitCortex();
                crtx.MakeStep();
            }
        }
    }

    private class ShowExtendedGUIListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Extended GUI");
            ExtensionGUI panel = new ExtensionGUI();
            frame.setContentPane(panel.extensionGUI);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

//              CasmiApplet.launch(crtx.region);
        }
    }

    private class ShowActiveColumnsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Active Columns Visualization");
            ActiveColumnsVisualization cl = new ActiveColumnsVisualization();
            frame.setContentPane(cl.activeColumnsPanel_main);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.pack();
            //frame.setResizable(false);
            cl.draw(20, 10, 20,20);
            frame.setVisible(true);
        }
    }
}
