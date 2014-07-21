package ru.isa.ai.newdhm.applet;

import info.monitorenter.gui.chart.Chart2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isa.ai.newdhm.CortexThread;
import ru.isa.ai.newdhm.RegionInitializationException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    public JCheckBox inputsGraphicsCheckBox;
    private JTabbedPane tabbedPane1;
    private Chart2D chart2D2;
    private JPanel casmiPanel;
    public JCheckBox drawDendritesTimlineCheckBox;
    private JButton loadPropertiesFromFileButton;
    private JButton showActiveColumnsButton;
    private JLabel numRegions;
    private JSpinner spinnerNumRegs;
    private JButton putNumOfRegionsButton;
    private JLabel numOfRegToInit;
    private JButton setSettingsButton;
    private JLabel setVisualizParameters;
    private JLabel ruleTheMainProcess;
    private JLabel regionNum;
    private JButton UPButton;
    private JButton DOWNButton;
    private JButton previousRegSettingsButton;
    private JButton nextRegSettingsButton;
    private JLabel numOfReg;
    private JLabel regToDraw;
    private JFrame f;
    private int numOfRegions;
    private Settings[] settings;
    private boolean[] regIsInited;
    //final private int REGIONS_AMOUNT = 1;
    public CortexThread crtx ;//= new CortexThread(REGIONS_AMOUNT);
    static HTMConfiguration panel;
    private ActiveColumnsVisualization cl;

    private final String SP_PROP_FILENAME = "htm.properties";
    private final String IMAGE_PATH =  "src/main/resources/image1.png"; //"D:\\work_folder\\image1.png";
    private String filePropName = SP_PROP_FILENAME;
    private String imagePath = IMAGE_PATH;

    //group of default values for properties
    private final double DESIRED_LOCAL_ACTIVITY_DEFAULT = 20.0;
    private final double MINIMAL_OVERLAP_DEFAULT = 50.0;
    private final double CONNECTED_PERMISSION_DEFAULT = 0.2;
    private final double PERMANENCE_INC_DEFAULT = 0.1;
    private final double PERMANENCE_DEC_DEFAULT =0.1;
    private final double CELLS_PER_COLUMN_DEFAULT = 4.0;
    private final double ACTIVATION_THRESHOLD_DEFAULT = 10.0;
    private final double INITIAL_PERMANENCE_DEFAULT = 0.1;
    private final double MINIMAL_THRESHOLD_DEFAULT = 4.0;
    private final double NEW_SYNAPSES_COUNT_DEFAULT = 30.0;
    private final double REGION_X_DIMENSION_DEFAULT = 20.0;
    private final double REGION_Y_DIMENSION_DEFAULT = 10.0;

    // загрузка свойств из файла
    ImageClass img;
    final private int NUM_OF_PARAMETERS_FOR_1_REG = 12;

    public HTMConfiguration () {
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
        //text - editors
        textField1.addPropertyChangeListener(new textField1PropertyChangeListener());
        textField2.addPropertyChangeListener(new textField2PropertyChangeListener());
        textField3.addPropertyChangeListener(new textField3PropertyChangeListener());
        textField4.addPropertyChangeListener(new textField4PropertyChangeListener());
        textField5.addPropertyChangeListener(new textField5PropertyChangeListener());
        textField6.addPropertyChangeListener(new textField6PropertyChangeListener());
        textField7.addPropertyChangeListener(new textField7PropertyChangeListener());
        textField8.addPropertyChangeListener(new textField8PropertyChangeListener());
        textField9.addPropertyChangeListener(new textField9PropertyChangeListener());
        textField10.addPropertyChangeListener(new textField10PropertyChangeListener());
        textField11.addPropertyChangeListener(new textField11PropertyChangeListener());
        textField12.addPropertyChangeListener(new textField12PropertyChangeListener());

        // from 1 to 10, in 1.0 steps start value 1.0
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 10, 1);
        spinnerNumRegs.setModel(model);

        loadImage();
    }

    private class textField12PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField12.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[11] (region y dimension) for region " + numOfReg );
                    new_value = REGION_Y_DIMENSION_DEFAULT;
                    textField12.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[11] = new_value;
            }
        }
    }

    private class textField11PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField11.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[10] (region x dimension) for region " + numOfReg );
                    new_value = REGION_X_DIMENSION_DEFAULT;
                    textField11.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[10] = new_value;
            }
        }
    }

    private class textField10PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField10.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[9] (new synapses count) for region " + numOfReg );
                    new_value = NEW_SYNAPSES_COUNT_DEFAULT;
                    textField10.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[9] = new_value;
            }
        }
    }

    private class textField9PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField9.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[8] (minimal threshold) for region " + numOfReg );
                    new_value = MINIMAL_THRESHOLD_DEFAULT;
                    textField9.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[8] = new_value;
            }
        }
    }

    private class textField8PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField8.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[7] (initial permanence) for region " + numOfReg );
                    new_value = INITIAL_PERMANENCE_DEFAULT;
                    textField8.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[7] = new_value;
            }
        }
    }

    private class textField7PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField7.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[6] (activation treshold) for region " + numOfReg );
                    new_value = ACTIVATION_THRESHOLD_DEFAULT;
                    textField7.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[6] = new_value;
            }
        }
    }

    private class textField6PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField6.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[5] (cells per column) for region " + numOfReg );
                    new_value = CELLS_PER_COLUMN_DEFAULT;
                    textField6.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[5] = new_value;
            }
        }
    }

    private class textField5PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField5.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[4] (permanence dec) for region " + numOfReg );
                    new_value = PERMANENCE_DEC_DEFAULT;
                    textField5.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[4] = new_value;
            }
        }
    }

    private class textField4PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField4.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[3] (permanence inc) for region " + numOfReg );
                    new_value = PERMANENCE_INC_DEFAULT;
                    textField4.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[3] = new_value;
            }
        }
    }

    private class textField3PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField3.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[2] (connected permission) for region " + numOfReg );
                    new_value = CONNECTED_PERMISSION_DEFAULT;
                    textField3.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[2] = new_value;
            }
        }
    }

    private class textField2PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField2.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[1] (minimal overlap) for region " + numOfReg );
                    new_value = MINIMAL_OVERLAP_DEFAULT;
                    textField2.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[1] = new_value;
            }
        }
    }

    private class textField1PropertyChangeListener implements PropertyChangeListener{
        public void propertyChange(PropertyChangeEvent e){
            String propertyName = e.getPropertyName();
            if ("text".equals(propertyName)){
                int num_of_reg =Integer.parseInt(numOfReg.getText());
                double new_value = 0.0;
                try{
                    new_value = Double.parseDouble(textField1.getText());
                }
                catch(NumberFormatException ex){
                    System.out.print("Wrong property[0] (desired local activity) for region " + numOfReg );
                    new_value = DESIRED_LOCAL_ACTIVITY_DEFAULT;
                    textField1.setText(String.valueOf(new_value));
                }
                settings[num_of_reg].initialParameters[0] = new_value;
            }
        }
    }

    private class PutNumOfRegionsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            numOfRegions = (Integer)spinnerNumRegs.getValue();
            if  (numOfRegions > 0) {
                //crtx = new CortexThread(numOfRegions, settings);
                //regIsInited = new boolean[numOfRegions];
                settings = new Settings[numOfRegions];
                for (int i = 0; i < numOfRegions; i++){
                    settings[i] = new Settings();
                }

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
                //////////////////////////////////////////////////////
                //buttons
                if (numOfRegions > 1) nextRegSettingsButton.setEnabled(true);
                setSettingsButton.setEnabled(true);
                putNumOfRegionsButton.setEnabled(false);
                loadPropertiesFromFileButton.setEnabled(true);
            }

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
            if (numOfNextReg == numOfRegions -1)
                nextRegSettingsButton.setEnabled(false);
        }
    }

    private void showSettingsForRegion(int regInd){
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
            if (numOfPrevReg  == numOfRegions - 2)
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
            if (numOfPrevReg  == numOfRegions - 2 )
                UPButton.setEnabled(true);
            regToDraw.setText(String.valueOf(numOfPrevReg));

            crtx.drawOnChart(numOfPrevReg);
            if (numOfPrevReg == 0)
                DOWNButton.setEnabled(false);
        }
    }

    private class SetSettingsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            InitCortex();
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

    public void loadImage(){
        img = new ImageClass();
        img.load(imagePath);
    }

    public ImageClass getImg(){
        return img;
    }

    public class Settings{
        public double[] initialParameters;

        public Settings(){
            initialParameters = new double[]{DESIRED_LOCAL_ACTIVITY_DEFAULT,
                                            MINIMAL_OVERLAP_DEFAULT,
                                            CONNECTED_PERMISSION_DEFAULT,
                                            PERMANENCE_INC_DEFAULT,
                                            PERMANENCE_DEC_DEFAULT,
                                            CELLS_PER_COLUMN_DEFAULT,
                                            ACTIVATION_THRESHOLD_DEFAULT,
                                            INITIAL_PERMANENCE_DEFAULT,
                                            MINIMAL_THRESHOLD_DEFAULT,
                                            NEW_SYNAPSES_COUNT_DEFAULT,
                                            REGION_X_DIMENSION_DEFAULT,
                                            REGION_Y_DIMENSION_DEFAULT};
        }
    }


    public void InitCortex() {
        crtx = new CortexThread(numOfRegions, settings);
        //crtx.cr.region.addColumns();
        crtx.Init(chart2D1, chart2D2, panel);
    }

    public void loadProperties() throws RegionInitializationException { //загрузка данных в массив settings[]
        Logger logger = LogManager.getLogger(Region.class.getSimpleName());
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(filePropName);
            properties.load(input);/*
            for (String name : properties.stringPropertyNames()) {
                switch (name) {
                    case "desiredLocalActivity":
                        crtx.cr.region.desiredLocalActivity = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "minOverlap":
                        crtx.cr.region.minOverlap = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "connectedPerm":
                        crtx.cr.region.connectedPerm = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "permanenceInc":
                        crtx.cr.region.permanenceInc = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "permanenceDec":
                        crtx.cr.region.permanenceDec = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "cellsPerColumn":
                        crtx.cr.region.cellsPerColumn = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "activationThreshold":
                        crtx.cr.region.activationThreshold = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "initialPerm":
                        crtx.cr.region.initialPerm = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "minThreshold":
                        crtx.cr.region.minThreshold = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "newSynapseCount":
                        crtx.cr.region.newSynapseCount = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "xDimension":
                        crtx.cr.region.xDimension = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "yDimension":
                        crtx.cr.region.yDimension = Integer.parseInt(properties.getProperty(name));
                        break;
                    default:
                        logger.error("Illegal property name: " + name);
                        break;
                }
            }*/
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
        Properties properties = new Properties();/*
        try {
            properties.setProperty("desiredLocalActivity",String.valueOf(crtx.cr.region.desiredLocalActivity));
            properties.setProperty("minOverlap",String.valueOf(crtx.cr.region.minOverlap));
            properties.setProperty("connectedPerm",String.valueOf(crtx.cr.region.connectedPerm));
            properties.setProperty("permanenceInc", String.valueOf(crtx.cr.region.permanenceInc));
            properties.setProperty("permanenceDec", String.valueOf(crtx.cr.region.permanenceDec));
            properties.setProperty("activationThreshold",String.valueOf(crtx.cr.region.activationThreshold));
            properties.setProperty("initialPerm",String.valueOf(crtx.cr.region.initialPerm));
            properties.setProperty("minThreshold",String.valueOf(crtx.cr.region.minThreshold));
            properties.setProperty("newSynapseCount",String.valueOf(crtx.cr.region.newSynapseCount));
            properties.setProperty("xDimension",String.valueOf(crtx.cr.region.xDimension));
            properties.setProperty("yDimension",String.valueOf(crtx.cr.region.yDimension));

            FileOutputStream output = new FileOutputStream(filePropName);
            properties.store(output,"Saved settings");
            output.close();

        } catch (IOException e) {
            throw new RegionInitializationException("Cannot save properties file " + filePropName, e);
        }*/
    }

    public class LoadPropertiesButtonGUIListener implements ActionListener  {
        //crtx = new CortexThread();
        ////////////////////////////////////////////////////////

        public void actionPerformed (ActionEvent event) {
            try{
                loadProperties();
                numOfReg.setText("0");
                showSettingsForRegion(0);
            }
            catch (RegionInitializationException e){
                    System.out.println("caught " + e);
            }
        }

    }

    public class RunCortexButtonListener implements ActionListener {
        public void actionPerformed (ActionEvent event) {
            if (!crtx.isRunning()) {
                //InitCortex();
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
            cl.draw(crtx, 0, -1);
            f.setVisible(true);
        }
    }
}
