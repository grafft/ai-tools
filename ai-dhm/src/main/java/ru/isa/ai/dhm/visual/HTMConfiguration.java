package ru.isa.ai.dhm.visual;

import info.monitorenter.gui.chart.Chart2D;
import org.omg.CORBA.*;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.RegionSettingsException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Object;

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
    private JTextField textField13;
    private JTextField textField14;
    private JTextField textField15;
    private JTextField textField16;
    private JTextField textField17;

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


    private final static int NUM_OF_PARAMETERS_FOR_1_REG = 12;
    private final static int MAX_NUM_OF_REGIONS = 10;

    //HTM Comfiguration properties
    private int numOfRegions;
    private DHMSettings[] settings;
    private Timer timer;
    public NeocortexAction neocortexAction;
    public ImageClass img;

    private String imagePath;
    private String PROPERTY_POSTFIX = ".properties";
    private String path;

    public static void main(String[] args) {
        JFrame frame = new JFrame("HTMConfiguration");
        HTMConfiguration panel = new HTMConfiguration();
        frame.setContentPane(panel.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

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
        Object[] objects = mainPanel.getComponents();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof JTextField) {
                JTextField tf = (JTextField) objects[i];
                tf.getDocument().addDocumentListener(new DocumentListenerGeneral());
                tf.getDocument().putProperty("owner", tf);
                tf.getDocument().putProperty("property_id", i);
            }
        }
        /*
        textField1.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField1.getDocument().putProperty("owner", textField1);
        textField2.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField2.getDocument().putProperty("owner", textField2);
        textField3.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField3.getDocument().putProperty("owner", textField3);
        textField4.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField4.getDocument().putProperty("owner", textField4);
        textField5.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField5.getDocument().putProperty("owner", textField5);
        textField6.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField6.getDocument().putProperty("owner", textField6);
        textField7.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField7.getDocument().putProperty("owner", textField7);
        textField8.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField8.getDocument().putProperty("owner", textField8);
        textField9.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField9.getDocument().putProperty("owner", textField9);
        textField10.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField10.getDocument().putProperty("owner", textField10);
        textField11.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField11.getDocument().putProperty("owner", textField11);
        textField12.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField12.getDocument().putProperty("owner", textField12);
        textField13.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField13.getDocument().putProperty("owner", textField13);
        textField14.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField14.getDocument().putProperty("owner", textField14);
        textField15.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField15.getDocument().putProperty("owner", textField15);
        textField16.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField16.getDocument().putProperty("owner", textField16);
        textField17.getDocument().addDocumentListener(new DocumentListenerGeneral());
        textField17.getDocument().putProperty("owner", textField17);
*/

        // from 1 to 10, in 1.0 steps start value 1.0
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, MAX_NUM_OF_REGIONS, 1);
        spinnerNumRegs.setModel(model);

        loadImage();
    }

    public void loadImage() {
        img = new ImageClass();
        img.load(imagePath);
    }

    public void initCortex() {
        neocortexAction = new NeocortexAction(settings);
        neocortexAction.init(chart2D1, chart2D2, this);

        timer = new Timer(1000, neocortexAction);
        runCortexButton.setEnabled(true);
    }

    private void loadProperties() throws RegionSettingsException { //загрузка данных в массив settings[]
        // TODO P: как-то тут нужно переписать :) На мой взгляд лучше уж в одном файле настройки для всех регионов хранить
       /* File listFile = new File(path);
        File exportFiles[] = listFile.listFiles();
        String[] names = new String[exportFiles.length];
        int numOfFilesWithSettings = 0;
        for (int i = 0; i < names.length; i++) {
            if (exportFiles[i].getName().contains(PROPERTY_POSTFIX)) {
                names[i] = exportFiles[i].getName();
                numOfFilesWithSettings++;
            }
        }


        prepareInterfaceAndValues(numOfFilesWithSettings, true);
        spinnerNumRegs.setValue(numOfFilesWithSettings);

        if (numOfFilesWithSettings != 0) {
            for (int i = 0; i < numOfFilesWithSettings; i++) {
                settings[i].loadFromFile(path + names[i + 1]);
            }
        }*/

        settings = new DHMSettings[1];
        String path = HTMConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        settings[0] = DHMSettings.loadFromFile(path + "\\" + "test16xOnes.properties");

        setSettingsButton.doClick();
        setSettingsButton.setEnabled(false);
        savePropertiesToFileButton.setEnabled(true);
    }

    // TODO P: полезная функция, нужно доработать
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

    private void saveProperties() throws RegionSettingsException {
        int numOfFiles = (Integer) spinnerNumRegs.getValue();
        for (int i = 0; i < numOfFiles; i++) {
            settings[i].saveIntoFile(path + "htm" + String.valueOf(i) + PROPERTY_POSTFIX);
        }
    }

    private void prepareInterfaceAndValues(int numOfRegions, boolean textFieldsAvailable) {
        this.numOfRegions = numOfRegions;
        if (this.numOfRegions > 0) {

            settings = new DHMSettings[this.numOfRegions];
            for (int i = 0; i < this.numOfRegions; i++) {
                settings[i] = new DHMSettings();
            }

            if (textFieldsAvailable) {
                /////////////////////////////////////////////////////
                //edits for settings  should be enabled
                Object[] objects = mainPanel.getComponents();
                for (int i = 0; i < objects.length; i++) {
                    if (objects[i] instanceof JTextField) {
                        JTextField tf = (JTextField) objects[i];
                        tf.setEnabled(true);
                    }
                }
            }

            //buttons
            if (this.numOfRegions > 1) nextRegSettingsButton.setEnabled(true);
            setSettingsButton.setEnabled(true);
            putNumOfRegionsButton.setEnabled(false);
            spinnerNumRegs.setEnabled(false);
        }
    }

    private void showSettingsForRegion(int regInd) {

        Object[] objects = mainPanel.getComponents();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof JTextField) {
                JTextField tf = (JTextField) objects[i];
                int property_id = (Integer)tf.getDocument().getProperty("property_id");
                switch(property_id){
                    case 0: tf.setText(String.valueOf(settings[regInd].debug)); break;
                    case 1: tf.setText(String.valueOf(settings[regInd].xInput)); break;
                    case 2: tf.setText(String.valueOf(settings[regInd].yInput)); break;
                    case 3: tf.setText(String.valueOf(settings[regInd].xDimension)); break;
                    case 4: tf.setText(String.valueOf(settings[regInd].yDimension)); break;
                    case 5: tf.setText(String.valueOf(settings[regInd].initialInhibitionRadius)); break;
                    case 6: tf.setText(String.valueOf(settings[regInd].potentialRadius)); break;
                    case 7: tf.setText(String.valueOf(settings[regInd].cellsPerColumn)); break;
                    case 8: tf.setText(String.valueOf(settings[regInd].newSynapseCount)); break;
                    case 9: tf.setText(String.valueOf(settings[regInd].desiredLocalActivity)); break;
                    case 10: tf.setText(String.valueOf(settings[regInd].minOverlap)); break;
                    case 11: tf.setText(String.valueOf(settings[regInd].connectedPerm)); break;
                    case 12: tf.setText(String.valueOf(settings[regInd].permanenceInc)); break;
                    case 13: tf.setText(String.valueOf(settings[regInd].permanenceDec)); break;
                    case 14: tf.setText(String.valueOf(settings[regInd].activationThreshold)); break;
                    case 15: tf.setText(String.valueOf(settings[regInd].initialPerm)); break;
                    case 16: tf.setText(String.valueOf(settings[regInd].minThreshold)); break;
                }
            }
        }
    }

    public ImageClass getImg() {
        return img;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    ////////////////////////////////////Listeners//////////////////////////////////////////
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

            neocortexAction.drawOnChart(numOfNextReg);
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

            neocortexAction.drawOnChart(numOfPrevReg);
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

    private class SavePropertiesToFileButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                saveProperties();
            } catch (RegionSettingsException ex) {
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
            } catch (RegionSettingsException e) {
                System.out.println("caught " + e);
            }
        }
    }

    public class RunCortexButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (!timer.isRunning()) {
                timer.start();
                //((JButton) e.getSource()).setText("Stop");
                //stepButton.setEnabled(false);
            } else {
                timer.stop();
                //((JButton) e.getSource()).setText("Start");
            }
        }
    }

    private class StopCortexButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            timer.stop();
        }
    }

    private class MakeStepButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            neocortexAction.makeStep();
           /* showActiveColumnsButton.setEnabled(true);
            neocortexAction.thdMakeStep();
            if (numOfRegions > 1)
                UPButton.setEnabled(true);
            makeStepButton.setEnabled(false);
            */

        }
    }

    private class ShowActiveColumnsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame f = new JFrame("Active Columns Visualization");
            ActiveColumnsVisualization cl = new ActiveColumnsVisualization();
            f.setContentPane(cl.activeColumnsPanel_main);
            f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            f.pack();
            cl.setSettings(neocortexAction);
            cl.draw(0, -1);
            f.setVisible(true);
        }
    }

    /////////////////////////////////Property listeners/////////////////////////////////////////////
    private class DocumentListenerGeneral implements javax.swing.event.DocumentListener {

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

        private void updateLabel(final DocumentEvent e) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    Object owner = e.getDocument().getProperty("owner");
                    JTextField tf = (JTextField) owner;
                    int property_id = (Integer)tf.getDocument().getProperty("property_id");

                    int num_of_reg = Integer.parseInt(numOfReg.getText());
                    double new_value = 0.0;

                    try {
                        new_value = Double.parseDouble(tf.getText());
                    } catch (NumberFormatException ex) {
                        System.out.print("Wrong properties for region " + numOfReg);
                        DHMSettings default_settings = DHMSettings.getDefaultSettings();
                        double tmp = 0.0;
                        switch(property_id){
                            case 0: tmp = (default_settings.debug == true) ? 1 : 0 ; break;
                            case 1: tmp = default_settings.xInput; break;
                            case 2: tmp = default_settings.yInput; break;
                            case 3: tmp = default_settings.xDimension; break;
                            case 4: tmp = default_settings.yDimension; break;
                            case 5: tmp = default_settings.initialInhibitionRadius; break;
                            case 6: tmp = default_settings.potentialRadius; break;
                            case 7: tmp = default_settings.cellsPerColumn; break;
                            case 8: tmp = default_settings.newSynapseCount; break;
                            case 9: tmp = default_settings.desiredLocalActivity; break;
                            case 10: tmp = default_settings.minOverlap; break;
                            case 11: tmp = default_settings.connectedPerm; break;
                            case 12: tmp = default_settings.permanenceInc; break;
                            case 13: tmp = default_settings.permanenceDec; break;
                            case 14: tmp = default_settings.activationThreshold; break;
                            case 15: tmp = default_settings.initialPerm; break;
                            case 16: tmp =  default_settings.minThreshold; break;
                        }
                        tf.setText(String.valueOf(tmp));
                        new_value = tmp;
                    }
                    switch(property_id){
                        case 0: settings[num_of_reg].debug = (new_value == 1) ? true : false ; break;
                        case 1: settings[num_of_reg].xInput = (int)new_value; break;
                        case 2: settings[num_of_reg].yInput = (int )new_value; break;
                        case 3: settings[num_of_reg].xDimension = (int)new_value ; break;
                        case 4: settings[num_of_reg].yDimension = (int)new_value; break;
                        case 5: settings[num_of_reg].initialInhibitionRadius = (int)new_value; break;
                        case 6: settings[num_of_reg].potentialRadius = (int)new_value; break;
                        case 7: settings[num_of_reg].cellsPerColumn = (int)new_value; break;
                        case 8: settings[num_of_reg].newSynapseCount= (int)new_value; break;
                        case 9: settings[num_of_reg].desiredLocalActivity = (int)new_value ; break;
                        case 10: settings[num_of_reg].minOverlap = (int)new_value; break;
                        case 11: settings[num_of_reg].connectedPerm = new_value; break;
                        case 12: settings[num_of_reg].permanenceInc = new_value; break;
                        case 13: settings[num_of_reg].permanenceDec = new_value; break;
                        case 14: settings[num_of_reg].activationThreshold = new_value; break;
                        case 15: settings[num_of_reg].initialPerm = new_value; break;
                        case 16: settings[num_of_reg].minThreshold = new_value; break;
                    }
                }
            });
        }
    }
}