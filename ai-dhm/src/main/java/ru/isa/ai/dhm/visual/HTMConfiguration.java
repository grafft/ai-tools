package ru.isa.ai.dhm.visual;

import info.monitorenter.gui.chart.Chart2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isa.ai.dhm.HTMSettings;
import ru.isa.ai.dhm.core.CortexThread;
import ru.isa.ai.dhm.RegionInitializationException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import ru.isa.ai.dhm.core.Region;

import javax.swing.event.DocumentEvent;
import java.io.File;

public class HTMConfiguration {
    //text fields
    private JTextField textField1;
    private JTextField textField2;
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

    //buttons
    private JButton runCortexButton;
    private JButton stopCortexButton;
    private JButton makeStepButton;
    private JButton loadPropertiesFromFileButton;
    private JButton showActiveColumnsButton;
    private JButton setSettingsButton;
    private JButton putNumOfRegionsButton;
    private JButton UPButton;
    private JButton DOWNButton;
    private JButton previousRegSettingsButton;
    private JButton nextRegSettingsButton;
    private JButton savePropertiesToFileButton;

    //check boxes
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
    public JCheckBox inputsGraphicsCheckBox;
    public JCheckBox drawDendritesTimlineCheckBox;

    //labels
    private JLabel numRegions;
    private JLabel numOfRegToInit;
    private JLabel setVisualizParameters;
    private JLabel ruleTheMainProcess;
    private JLabel regionNum;
    private JLabel numOfReg;
    private JLabel regToDraw;

    //panels
    private JPanel mainPanel;
    public JTextPane textPane1;
    private JTabbedPane tabbedPane1;

    //charts
    private Chart2D chart2D1;
    private Chart2D chart2D2;

    //spinners
    private JSpinner spinnerNumRegs;
    private JPanel ActiveColsVisGenView;
    private JPanel ActiveColsSelectedView;

    //frames
    private JFrame f;

    //HTM Comfiguration properties
    private int numOfRegions;
    private HTMSettings[] settings;
    public CortexThread crtx;
    static HTMConfiguration panel;
    private ActiveColumnsVisualization cl;
    public ImageClass img;
    final private int NUM_OF_PARAMETERS_FOR_1_REG = 12;
    final private int MAX_NUM_OF_REGIONS = 10;

    private String filePropName;
    private String imagePath;
    private String PROPERTY_POSTFIX = ".properties";
    private String path;

    public HTMConfiguration() {
        path = HTMConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        imagePath = path + "image1.png";

        //buttons
        stopCortexButton.addActionListener(new StopCortexButtonListener());
        makeStepButton.addActionListener(new MakeStepButtonListener());
        runCortexButton.addActionListener(new RunCortexButtonListener());
        showActiveColumnsButton.addActionListener(new ShowActiveColumnsListener());
        loadPropertiesFromFileButton.addActionListener(new LoadPropertiesButtonGUIListener());
        putNumOfRegionsButton.addActionListener(new PutNumOfRegionsButtonListener());
        UPButton.addActionListener(new UPButtonListener());
        DOWNButton.addActionListener(new DOWNButtonListener());
        setSettingsButton.addActionListener(new SetSettingsButtonListener());
        previousRegSettingsButton.addActionListener(new PreviousRegSettingsButtonListener());
        nextRegSettingsButton.addActionListener(new NextRegSettingsButtonListener());
        savePropertiesToFileButton.addActionListener(new SavePropertiesToFileButtonListener());

        //text - editors
        textField1.getDocument().addDocumentListener(new DocumentListener1());
        textField2.getDocument().addDocumentListener(new DocumentListener2());
        textField3.getDocument().addDocumentListener(new DocumentListener3());
        textField4.getDocument().addDocumentListener(new DocumentListener4());
        textField5.getDocument().addDocumentListener(new DocumentListener5());
        textField6.getDocument().addDocumentListener(new DocumentListener6());
        textField7.getDocument().addDocumentListener(new DocumentListener7());
        textField8.getDocument().addDocumentListener(new DocumentListener8());
        textField9.getDocument().addDocumentListener(new DocumentListener9());
        textField10.getDocument().addDocumentListener(new DocumentListener10());
        textField11.getDocument().addDocumentListener(new DocumentListener11());
        textField12.getDocument().addDocumentListener(new DocumentListener12());

        // from 1 to 10, in 1.0 steps start value 1.0
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, MAX_NUM_OF_REGIONS, 1);
        spinnerNumRegs.setModel(model);

        loadImage();
    }

    private void prepareInterfaceAndValues(int numOfRegions_, boolean textFieldsAvailable) {
        numOfRegions = numOfRegions_;
        if (numOfRegions > 0) {

            settings = new HTMSettings[numOfRegions];
            for (int i = 0; i < numOfRegions; i++) {
                settings[i] = new HTMSettings();
            }

            if (textFieldsAvailable) {
                /////////////////////////////////////////////////////
                //edits for settings  should be enabled
                textField1.setEnabled(true);
                textField2.setEnabled(true);
                textField3.setEnabled(true);
                textField4.setEnabled(true);
                textField5.setEnabled(true);
                textField6.setEnabled(true);
                textField7.setEnabled(true);
                textField8.setEnabled(true);
                textField9.setEnabled(true);
                textField10.setEnabled(true);
                textField11.setEnabled(true);
                textField12.setEnabled(true);
            }

            //buttons
            if (numOfRegions > 1) nextRegSettingsButton.setEnabled(true);
            setSettingsButton.setEnabled(true);
            putNumOfRegionsButton.setEnabled(false);
            spinnerNumRegs.setEnabled(false);
        }
    }

    private class PutNumOfRegionsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            prepareInterfaceAndValues((Integer) spinnerNumRegs.getValue(), true);
            savePropertiesToFileButton.setEnabled(true);
        }
    }

    private class NextRegSettingsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //inc counter of current region
            int numOfNextReg = Integer.parseInt(numOfReg.getText()) + 1;
            if (numOfNextReg == 1)
                previousRegSettingsButton.setEnabled(true);
            numOfReg.setText(String.valueOf(numOfNextReg));
            //show setting for previous region
            showSettingsForRegion(numOfNextReg);
            if (numOfNextReg == numOfRegions - 1)
                nextRegSettingsButton.setEnabled(false);
        }
    }

    private void showSettingsForRegion(int regInd) {
        textField1.setText(String.valueOf(settings[regInd].initialParameters[0]));
        textField2.setText(String.valueOf(settings[regInd].initialParameters[1]));
        textField3.setText(String.valueOf(settings[regInd].initialParameters[2]));
        textField4.setText(String.valueOf(settings[regInd].initialParameters[3]));
        textField5.setText(String.valueOf(settings[regInd].initialParameters[4]));
        textField6.setText(String.valueOf(settings[regInd].initialParameters[5]));
        textField7.setText(String.valueOf(settings[regInd].initialParameters[6]));
        textField8.setText(String.valueOf(settings[regInd].initialParameters[7]));
        textField9.setText(String.valueOf(settings[regInd].initialParameters[8]));
        textField10.setText(String.valueOf(settings[regInd].initialParameters[9]));
        textField11.setText(String.valueOf(settings[regInd].initialParameters[10]));
        textField12.setText(String.valueOf(settings[regInd].initialParameters[11]));
    }


    private class PreviousRegSettingsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //dec counter of current region
            int numOfPrevReg = Integer.parseInt(numOfReg.getText()) - 1;
            if (numOfPrevReg == numOfRegions - 2)
                nextRegSettingsButton.setEnabled(true);
            numOfReg.setText(String.valueOf(numOfPrevReg));
            //show setting for previous region
            showSettingsForRegion(numOfPrevReg);
            if (numOfPrevReg == 0)
                previousRegSettingsButton.setEnabled(false);
        }
    }

    private class UPButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //inc counter of current region
            int numOfNextReg = Integer.parseInt(regToDraw.getText()) + 1;
            if (numOfNextReg == 1)
                DOWNButton.setEnabled(true);
            regToDraw.setText(String.valueOf(numOfNextReg));

            crtx.drawOnChart(numOfNextReg);
            if (numOfNextReg == numOfRegions - 1)
                UPButton.setEnabled(false);
        }
    }

    private class DOWNButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //dec counter of current region
            int numOfPrevReg = Integer.parseInt(regToDraw.getText()) - 1;
            if (numOfPrevReg == numOfRegions - 2)
                UPButton.setEnabled(true);
            regToDraw.setText(String.valueOf(numOfPrevReg));

            crtx.drawOnChart(numOfPrevReg);
            if (numOfPrevReg == 0)
                DOWNButton.setEnabled(false);
        }
    }

    private class SetSettingsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            initCortex();
            makeStepButton.setEnabled(true);
            setSettingsButton.setEnabled(false);
            loadPropertiesFromFileButton.setEnabled(false);

            //fields for settings are not available
            textField1.setEditable(false);
            textField2.setEditable(false);
            textField3.setEditable(false);
            textField4.setEditable(false);
            textField5.setEditable(false);
            textField6.setEditable(false);
            textField7.setEditable(false);
            textField8.setEditable(false);
            textField9.setEditable(false);
            textField10.setEditable(false);
            textField11.setEditable(false);
            textField12.setEditable(false);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("HTMConfiguration");
        panel = new HTMConfiguration();
        frame.setContentPane(panel.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public void loadImage() {
        img = new ImageClass();
        img.load(imagePath);
    }

    public ImageClass getImg() {
        return img;
    }

    public void initCortex() {
        crtx = new CortexThread(numOfRegions, settings);
        //crtx.cr.region.addColumns();
        crtx.init(chart2D1, chart2D2, panel);
    }

    public void loadProperties() throws RegionInitializationException { //загрузка данных в массив settings[]

        File listFile = new File(path);
        File exportFiles[] = listFile.listFiles();
        String[] names = new String[exportFiles.length];
        int numOfFilesWithSettings = 0;
        for (int i = 0; i < names.length; i++) {
            if (exportFiles[i].getName().contains(PROPERTY_POSTFIX)) {
                names[i] = exportFiles[i].getName();
                numOfFilesWithSettings++;
            }
        }
        /*
        for (int i = 1; i <= numOfFilesWithSettings; i++){
            System.out.print(names[i]+ "\n");
        }*/

        prepareInterfaceAndValues(numOfFilesWithSettings, true);
        spinnerNumRegs.setValue(numOfFilesWithSettings);

        Logger logger = LogManager.getLogger(Region.class.getSimpleName());

        if (numOfFilesWithSettings != 0) {
            for (int i = 0; i < numOfFilesWithSettings; i++) {
                Properties properties = new Properties();
                try {
                    filePropName = path + names[i + 1];
                    FileInputStream input = new FileInputStream(filePropName);
                    properties.load(input);
                    for (String name : properties.stringPropertyNames()) {
                        switch (name) {
                            case "desiredLocalActivity":
                                settings[i].initialParameters[0] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "minOverlap":
                                settings[i].initialParameters[1] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "connectedPerm":
                                settings[i].initialParameters[2] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "permanenceInc":
                                settings[i].initialParameters[3] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "permanenceDec":
                                settings[i].initialParameters[4] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "cellsPerColumn":
                                settings[i].initialParameters[5] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "activationThreshold":
                                settings[i].initialParameters[6] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "initialPerm":
                                settings[i].initialParameters[7] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "minThreshold":
                                settings[i].initialParameters[8] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "newSynapseCount":
                                settings[i].initialParameters[9] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "xDimension":
                                settings[i].initialParameters[10] = Double.parseDouble(properties.getProperty(name));
                                break;
                            case "yDimension":
                                settings[i].initialParameters[11] = Double.parseDouble(properties.getProperty(name));
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
        }

        setSettingsButton.doClick();
        setSettingsButton.setEnabled(false);
        savePropertiesToFileButton.setEnabled(true);
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
        int numOfFiles = (Integer) spinnerNumRegs.getValue();

        for (int i = 0; i < numOfFiles; i++) {
            Properties properties = new Properties();
            try {
                properties.setProperty("desiredLocalActivity", String.valueOf(settings[i].initialParameters[0]));
                properties.setProperty("minOverlap", String.valueOf(settings[i].initialParameters[1]));
                properties.setProperty("connectedPerm", String.valueOf(settings[i].initialParameters[2]));
                properties.setProperty("permanenceInc", String.valueOf(settings[i].initialParameters[3]));
                properties.setProperty("permanenceDec", String.valueOf(settings[i].initialParameters[4]));
                properties.setProperty("cellsPerColumn", String.valueOf(settings[i].initialParameters[5]));
                properties.setProperty("activationThreshold", String.valueOf(settings[i].initialParameters[6]));
                properties.setProperty("initialPerm", String.valueOf(settings[i].initialParameters[7]));
                properties.setProperty("minThreshold", String.valueOf(settings[i].initialParameters[8]));
                properties.setProperty("newSynapseCount", String.valueOf(settings[i].initialParameters[9]));
                properties.setProperty("xDimension", String.valueOf(settings[i].initialParameters[10]));
                properties.setProperty("yDimension", String.valueOf(settings[i].initialParameters[11]));

                filePropName = path + "htm" + String.valueOf(i) + PROPERTY_POSTFIX;
                FileOutputStream output = new FileOutputStream(filePropName);
                properties.store(output, "Saved settings");
                output.close();

            } catch (IOException e) {
                throw new RegionInitializationException("Cannot save properties file " + filePropName, e);
            }
        }
    }

    private class SavePropertiesToFileButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                saveProperties();
            } catch (RegionInitializationException ex) {
                System.out.println("caught " + ex);
            }
        }
    }

    public class LoadPropertiesButtonGUIListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                loadProperties();
                numOfReg.setText("0");
                showSettingsForRegion(0);
            } catch (RegionInitializationException e) {
                System.out.println("caught " + e);
            }
        }
    }

    public class RunCortexButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (!crtx.isRunning()) {
                //initCortex();
                crtx.start();
            } else
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
            showActiveColumnsButton.setEnabled(true);
            crtx.MakeStep();
            if (numOfRegions > 1)
                UPButton.setEnabled(true);
            makeStepButton.setEnabled(false);
        }
    }

    private class ShowActiveColumnsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            f = new JFrame("Active Columns Visualization");
            cl = new ActiveColumnsVisualization();
            f.setContentPane(cl.activeColumnsPanel_main);
            f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            f.pack();
            cl.setSettings(crtx);
            cl.draw(0, -1);
            f.setVisible(true);
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    private class DocumentListener1 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {

                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField1.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[0] (desired local activity) for region " + numOfReg);
                        new_value = HTMSettings.DESIRED_LOCAL_ACTIVITY_DEFAULT;
                        textField1.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[0] = new_value;
                }
            });
        }
    }

    private class DocumentListener12 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField12.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[11] (region y dimension) for region " + numOfReg);
                        new_value = HTMSettings.REGION_Y_DIMENSION_DEFAULT;
                        textField12.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[11] = new_value;
                }
            });
        }
    }

    private class DocumentListener11 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField11.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[10] (region x dimension) for region " + numOfReg);
                        new_value = HTMSettings.REGION_X_DIMENSION_DEFAULT;
                        textField11.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[10] = new_value;
                }
            });
        }
    }

    private class DocumentListener10 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField10.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[9] (new synapses count) for region " + numOfReg);
                        new_value = HTMSettings.NEW_SYNAPSES_COUNT_DEFAULT;
                        textField10.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[9] = new_value;
                }
            });
        }
    }

    private class DocumentListener9 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField9.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[8] (minimal threshold) for region " + numOfReg);
                        new_value = HTMSettings.MINIMAL_THRESHOLD_DEFAULT;
                        textField9.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[8] = new_value;
                }
            });
        }
    }

    private class DocumentListener8 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField8.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[7] (initial permanence) for region " + numOfReg);
                        new_value = HTMSettings.INITIAL_PERMANENCE_DEFAULT;
                        textField8.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[7] = new_value;
                }
            });
        }
    }

    private class DocumentListener7 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField7.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[6] (activation treshold) for region " + numOfReg);
                        new_value = HTMSettings.ACTIVATION_THRESHOLD_DEFAULT;
                        textField7.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[6] = new_value;
                }
            });
        }
    }

    private class DocumentListener6 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField6.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[5] (cells per column) for region " + numOfReg);
                        new_value = HTMSettings.CELLS_PER_COLUMN_DEFAULT;
                        textField6.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[5] = new_value;
                }
            });
        }
    }

    private class DocumentListener5 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField5.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[4] (permanence dec) for region " + numOfReg);
                        new_value = HTMSettings.PERMANENCE_DEC_DEFAULT;
                        textField5.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[4] = new_value;
                }
            });
        }
    }

    private class DocumentListener4 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField4.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[3] (permanence inc) for region " + numOfReg);
                        new_value = HTMSettings.PERMANENCE_INC_DEFAULT;
                        textField4.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[3] = new_value;
                }
            });
        }
    }

    private class DocumentListener3 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField3.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[2] (connected permission) for region " + numOfReg);
                        new_value = HTMSettings.CONNECTED_PERMISSION_DEFAULT;
                        textField3.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[2] = new_value;
                }
            });
        }
    }

    private class DocumentListener2 implements javax.swing.event.DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabel(e);
        }

        private void updateLabel(DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;
                    try {
                        new_value = Double.parseDouble(textField2.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong property[1] (minimal overlap) for region " + numOfReg);
                        new_value = HTMSettings.MINIMAL_OVERLAP_DEFAULT;
                        textField2.setText(String.valueOf(new_value));
                    }
                    settings[num_of_reg].initialParameters[1] = new_value;
                }
            });
        }
    }
}
