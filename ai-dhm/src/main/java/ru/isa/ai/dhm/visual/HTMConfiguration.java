package ru.isa.ai.dhm.visual;

import info.monitorenter.gui.chart.Chart2D;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.RegionSettingsException;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Object;
import java.util.*;
import java.util.List;

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
    private JButton setSettingsButton;
    private JButton putNumOfRegionsButton;
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
    private ShowVisTree contentPane;
    private Map<Integer, Boolean> initedRegs = new HashMap<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("HTMConfiguration");
        HTMConfiguration panel = new HTMConfiguration();
        frame.setContentPane(panel.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);

    }

    public HTMConfiguration() {
        path = HTMConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        imagePath = path + "image1.png";

        //buttons
        stopCortexButton.addActionListener(new StopCortexButtonListener());
        makeStepButton.addActionListener(new MakeStepButtonListener());
        runCortexButton.addActionListener(new RunCortexButtonListener());
        //showActiveColumnsButton.addActionListener(new ShowActiveColumnsListener());
        loadPropertiesFromFileButton.addActionListener(new LoadPropertiesButtonGUIListener());
        putNumOfRegionsButton.addActionListener(new PutNumOfRegionsButtonListener());
        setSettingsButton.addActionListener(new SetSettingsButtonListener());
        previousRegSettingsButton.addActionListener(new PreviousRegSettingsButtonListener());
        nextRegSettingsButton.addActionListener(new NextRegSettingsButtonListener());
        savePropertiesToFileButton.addActionListener(new SavePropertiesToFileButtonListener());

        contentPane = new ShowVisTree();
        contentPane.setOpaque(true);
        ActiveColsVisGenView.add(contentPane); //the results of tree preparation

        //text - editors
        Object[] objects = mainPanel.getComponents();
        int counter = 0;
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof JTextField) {
                JTextField tf = (JTextField) objects[i];
                tf.getDocument().addDocumentListener(new DocumentListenerGeneral());
                tf.getDocument().putProperty("owner", tf);
                tf.getDocument().putProperty("property_id", counter);
                counter++;
            }
        }
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

    private int getID(String fullName){
        String textID = fullName.substring(fullName.indexOf(" ")+1);
        int ID = 0;
        Boolean isInited = false;
        try{
            ID = Integer.valueOf(textID);
        }
        catch(Exception ex){System.out.print(ex);}
        return ID;
    }

    private Boolean regIsInited(String fullName){
        String textID = fullName.substring(fullName.indexOf(" ")+1);
        int ID = getID(fullName);
        Boolean isInited = false;
            if (initedRegs.containsKey(ID)){
                isInited = initedRegs.get(ID);
            }
        return isInited;
    }

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
            textField1.setEnabled(false);
            //show settings for 0-region
            showSettingsForRegion(0);

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
                    case 0: tf.setText(String.valueOf((settings[regInd].debug == false) ? 0 : 1)); break;
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
/*
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
    }*/

    private class SetSettingsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
//теперь задает настройки только для региона, который выбран в дереве

            //соотнесение настроек


            //выделение зеленым цветом, но надо только, если еще не зеленая
           /* Object[] objects = contentPane.getComponents();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof VisTree) {
                    VisTree vt = (VisTree) objects[i];
                    TreePath currentSelection = vt.tree.getSelectionPath();
                    if (currentSelection != null) {
                        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                                (currentSelection.getLastPathComponent());
                        JLabel l = new JLabel(currentNode.toString()/*имя узла*///);
                        /*l.setBackground(Color.green);
                        vt.add(l);
                    }
                }
            }*/


            /*
            initCortex();
            makeStepButton.setEnabled(true);
            setSettingsButton.setEnabled(false);
            loadPropertiesFromFileButton.setEnabled(false);

            //fields for settings are not available
            Object[] objects = mainPanel.getComponents();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof JTextField) {
                    JTextField tf = (JTextField) objects[i];
                    tf.setEditable(false);
                }
            }*/
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
                //showSettingsForRegion(0);
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
            //showActiveColumns();
        }
    }

    private void showActiveColumns(){
        ActiveColumnsVisualization cl = new ActiveColumnsVisualization(ActiveColsVisGenView);
        cl.setSettings(neocortexAction);
        cl.draw(0, -1);
    }
    /*
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
*/
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
                        System.out.print("Wrong properties for region " + num_of_reg);
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