package ru.isa.ai.dhm.visual;

import info.monitorenter.gui.chart.Chart2D;
import ru.isa.ai.dhm.DHMSettings;
import ru.isa.ai.dhm.RegionSettingsException;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.Object;
import java.lang.reflect.Field;
import java.util.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

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
    private JButton setSettingsButton;

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

    private JLabel numOfRegToInit;
    private JLabel setVisualizParameters;
    private JLabel ruleTheMainProcess;
    private JLabel numOfReg;

    //panels
    private JPanel mainPanel;
    public JTextPane textPane1;
    private JTabbedPane tabbedPane1;

    //charts
    private Chart2D chart2D1;
    private Chart2D chart2D2;

    private JPanel ActiveColsVisGenView;
    private JPanel ActiveColsSelectedView;

    private JButton saveButton;
    private JButton loadButton;


    //HTM Comfiguration properties
    private int numOfRegions;
    private Map<Integer,DHMSettings> settings;
    DHMSettings currentSettings;
    private Timer timer;
    public NeocortexAction neocortexAction;
    public ImageClass img;
    private int indexOfActiveReg;

    private String imagePath;
    private String PROPERTY_POSTFIX = ".properties";
    private String path;
    private JFileChooser fc;
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
    private int getID(String fullName){
        String textID = fullName.substring(fullName.indexOf(" ")+1);
        int ID = 0;
        try{
            ID = Integer.valueOf(textID);
        }
        catch(Exception ex){System.out.print(ex);}
        return ID;
    }




    public HTMConfiguration() {
        path = HTMConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        imagePath = path + "image1.png";

        //buttons
        stopCortexButton.addActionListener(new StopCortexButtonListener());
        makeStepButton.addActionListener(new MakeStepButtonListener());
        runCortexButton.addActionListener(new RunCortexButtonListener());
        setSettingsButton.addActionListener(new SetSettingsButtonListener());
        loadButton.addActionListener(new LoadButtonListener());
        saveButton.addActionListener(new SaveButtonListener());

        ShowVisTree contentPane = new ShowVisTree();
        contentPane.setOpaque(true);
        contentPane.treePanel.tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                        .getPath().getLastPathComponent();
                if (node.toString().contains("Region"))
                    numOfReg.setText(String.valueOf(getID(node.toString())));
            }
        });
        ActiveColsVisGenView.add(contentPane); //the results of tree preparation

        currentSettings = DHMSettings.getDefaultSettings();
        settings = new HashMap<>();
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

        showCurrentSettings();
        loadImage();
        fc = new JFileChooser();
    }

    public void loadImage() {
        img = new ImageClass();
        img.load(imagePath);
    }

    public void initCortex() {
        VisTree vt = null;
        Object[] objects = ActiveColsVisGenView.getComponents();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof VisTree) {
                vt = (VisTree) objects[i];
            }
        }
        neocortexAction = new NeocortexAction(settings, vt);
        neocortexAction.init(chart2D1, chart2D2, this);

        timer = new Timer(1000, neocortexAction);
        runCortexButton.setEnabled(true);
    }

    private void initReg(String fullName, Boolean fl){
        String textID = fullName.substring(fullName.indexOf(" ")+1);
        int ID = getID(fullName);
        Boolean isInited = false;
        initedRegs.put(ID, fl); //Добавляет ключ и значение к карте. Если такой ключ уже имеется, то новый объект заменяет предыдущий, связанный с этим ключом.
    }

    private void showCurrentSettings() {

        Object[] objects = mainPanel.getComponents();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof JTextField) {
                JTextField tf = (JTextField) objects[i];
                int property_id = (Integer)tf.getDocument().getProperty("property_id");
                switch(property_id){
                    case 0: tf.setText(String.valueOf((currentSettings.debug == false) ? 0 : 1)); break;
                    case 1: tf.setText(String.valueOf(currentSettings.xInput)); break;
                    case 2: tf.setText(String.valueOf(currentSettings.yInput)); break;
                    case 3: tf.setText(String.valueOf(currentSettings.xDimension)); break;
                    case 4: tf.setText(String.valueOf(currentSettings.yDimension)); break;
                    case 5: tf.setText(String.valueOf(currentSettings.initialInhibitionRadius)); break;
                    case 6: tf.setText(String.valueOf(currentSettings.potentialRadius)); break;
                    case 7: tf.setText(String.valueOf(currentSettings.cellsPerColumn)); break;
                    case 8: tf.setText(String.valueOf(currentSettings.newSynapseCount)); break;
                    case 9: tf.setText(String.valueOf(currentSettings.desiredLocalActivity)); break;
                    case 10: tf.setText(String.valueOf(currentSettings.minOverlap)); break;
                    case 11: tf.setText(String.valueOf(currentSettings.connectedPerm)); break;
                    case 12: tf.setText(String.valueOf(currentSettings.permanenceInc)); break;
                    case 13: tf.setText(String.valueOf(currentSettings.permanenceDec)); break;
                    case 14: tf.setText(String.valueOf(currentSettings.activationThreshold)); break;
                    case 15: tf.setText(String.valueOf(currentSettings.initialPerm)); break;
                    case 16: tf.setText(String.valueOf(currentSettings.minThreshold)); break;
                }
            }
        }
    }

    public ImageClass getImg() {
        return img;
    }


    ////////////////////////////////////Listeners//////////////////////////////////////////
    private class LoadButtonListener  implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int returnVal = fc.showOpenDialog(null);
            try{
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (file.getName().contains(PROPERTY_POSTFIX)){
                    try {
                        currentSettings = DHMSettings.loadFromFile(file.getPath());
                        showCurrentSettings();
                    }catch (Exception exc){
                        System.out.print(exc);
                    }
                }
                else System.out.print("File has non appropriate format");
            }
        }catch (Exception exc){
                JOptionPane.showMessageDialog(null, "File could not be loaded, try again.");
            }
        }

     }

    private class SaveButtonListener  implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fc = new JFileChooser();
            fc.setSelectedFile(new File("Region "+ numOfReg.getText() + PROPERTY_POSTFIX));
            int status = fc.showSaveDialog(null);

            try {
                if (status == JFileChooser.APPROVE_OPTION) {
                    File saveFile = fc.getSelectedFile();
                    currentSettings.saveIntoFile(saveFile.getPath());
                } else if (status == JFileChooser.CANCEL_OPTION) {
                    // User has pressed cancel button
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "File could not be written, try again.");
            }
        }
    }


    private class SetSettingsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
//теперь задает настройки только для региона, который выбран в дереве

            int regID = Integer.valueOf(numOfReg.getText());
            settings.put(regID,currentSettings);
            initedRegs.put(regID, true);

            //выделение зеленым цветом, но надо только, если еще не зеленая

            Object[] objects = ActiveColsVisGenView.getComponents();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof ShowVisTree ) {
                    ShowVisTree svt = (ShowVisTree) objects[i];
                    svt.treePanel.renderer.updateHashMap(initedRegs);
                }
            }
        }
    }


    public class RunCortexButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (!timer.isRunning()) {
                timer.start();
            } else {
                timer.stop();
            }
        }
    }

    private class StopCortexButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            timer.stop();
        }
    }

    private Boolean checkInitialization(DefaultMutableTreeNode root){
        String nodeName = root.toString();
        int ID = 0;
        Boolean fl = false;







        return fl;
     }

    private class MakeStepButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // проверка на то, что все регионы инициализированы
            Object[] objects = ActiveColsVisGenView.getComponents();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof ShowVisTree ) {
                    ShowVisTree svt = (ShowVisTree) objects[i];
                    if (checkInitialization(svt.treePanel.rootNode))
                        neocortexAction.makeStep();
                }
            }
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
                        case 0: currentSettings.debug = (new_value == 1) ? true : false ; break;
                        case 1: currentSettings.xInput = (int)new_value; break;
                        case 2: currentSettings.yInput = (int )new_value; break;
                        case 3: currentSettings.xDimension = (int)new_value ; break;
                        case 4: currentSettings.yDimension = (int)new_value; break;
                        case 5: currentSettings.initialInhibitionRadius = (int)new_value; break;
                        case 6: currentSettings.potentialRadius = (int)new_value; break;
                        case 7: currentSettings.cellsPerColumn = (int)new_value; break;
                        case 8: currentSettings.newSynapseCount= (int)new_value; break;
                        case 9: currentSettings.desiredLocalActivity = (int)new_value ; break;
                        case 10: currentSettings.minOverlap = (int)new_value; break;
                        case 11: currentSettings.connectedPerm = new_value; break;
                        case 12: currentSettings.permanenceInc = new_value; break;
                        case 13: currentSettings.permanenceDec = new_value; break;
                        case 14: currentSettings.activationThreshold = new_value; break;
                        case 15: currentSettings.initialPerm = new_value; break;
                        case 16: currentSettings.minThreshold = new_value; break;
                    }
                }
            });
        }
    }
}