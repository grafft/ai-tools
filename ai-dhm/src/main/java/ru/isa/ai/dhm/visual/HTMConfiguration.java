package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitMatrix;
import info.monitorenter.gui.chart.Chart2D;
import ru.isa.ai.dhm.DHMSettings;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.Object;
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
    private JLabel numOfCell;

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
    private JButton showDefaultSetButton;
    private JButton setInputSourceButton;
    private JComboBox comboBox1;


    //HTM Comfiguration properties
    private int numOfRegions;
    private Map<Integer, DHMSettings> settings;
    private DHMSettings currentSettings;
    private Timer timer;
    public NeocortexAction neocortexAction;
    private ImageClass img;
    private int indexOfActiveReg;

    private String imagePath;
    private String PROPERTY_POSTFIX = ".properties";
    private String path;
    private JFileChooser fc;
    private Map<Integer, BitMatrix> picID_input = new HashMap<>();
    private boolean work_mode = false;

    IInputLoader inputLoader;

    public static void main(String[] args) {
        JFrame frame = new JFrame("HTMConfiguration");
        HTMConfiguration panel = new HTMConfiguration();
        frame.setContentPane(panel.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);

    }

    private int getID(String fullName) {
        String textID = fullName.substring(fullName.indexOf(" ") + 1);
        int ID = 0;
        try {
            ID = Integer.valueOf(textID);
        } catch (Exception ex) {
            System.out.print(ex);
        }
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
        showDefaultSetButton.addActionListener(new ShowDefaultSetButtonListener());

        currentSettings = DHMSettings.getDefaultSettings();
        settings = new HashMap<>();

        ShowVisTree contentPane = new ShowVisTree();
        contentPane.setOpaque(true);
        contentPane.treePanel.tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                        .getPath().getLastPathComponent();
                    int id = 0;
                    if (!node.toString().contains("HTM")) {
                        id = getID(node.toString());
                        numOfCell.setText(String.valueOf(id));
                    }
                    if (node.toString().contains("Region")) {
                        if (!work_mode) setSettingsButton.setEnabled(true);
                        //проверяем, если инициализировали уже
                        if (!settings.isEmpty() && settings.get(id) != null) {
                            //высвечиваем настройки
                            currentSettings = settings.get(id);
                            showCurrentSettings();
                        }

                    } else { //picture
                        if (!work_mode) setSettingsButton.setEnabled(false);
                    }
                    if (work_mode){
                        if (node.getParent() != null && !node.getParent().toString().contains("HTM")){
                            int parentID = getID(node.getParent().toString());
                            Object obj = null;
                            if (node.toString().contains("Picture")) {
                                obj = picID_input.get(id);

                            }
                            else {
                                obj = neocortexAction.getSelectedRegion(id);
                            }
                            ActColsVisClass actCols = new ActColsVisClass(neocortexAction.getSelectedRegion(parentID), obj);
                            actCols.setOpaque(true);
                            ActiveColsSelectedView.removeAll();
                            ActiveColsSelectedView.add(actCols, BorderLayout.CENTER);
                        }
                    }

            }
        });
        ActiveColsVisGenView.add(contentPane); //the results of tree preparation


        //text - editors

        Object[] objects = mainPanel.getComponents();
        int counter = 0;
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof JTextField) {
                JTextField tf = (JTextField) objects[i];
                //tf.getDocument().addDocumentListener(new DocumentListenerGeneral());
                tf.getDocument().putProperty("owner", tf);
                tf.getDocument().putProperty("property_id", counter);
                counter++;
            }
        }

        showCurrentSettings();

        img = new ImageClass();

        fc = new JFileChooser();
        setInputSourceButton.addActionListener(new SetInputSourceButtonListener());
    }

    private void showCurrentSettings() {

        Object[] objects = mainPanel.getComponents();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof JTextField) {
                JTextField tf = (JTextField) objects[i];
                int property_id = (Integer) tf.getDocument().getProperty("property_id");
                switch (property_id) {
                    case 0:
                        tf.setText(String.valueOf((currentSettings.debug == false) ? 0 : 1));
                        break;
                    case 1:
                        tf.setText(String.valueOf(currentSettings.xInput));
                        break;
                    case 2:
                        tf.setText(String.valueOf(currentSettings.yInput));
                        break;
                    case 3:
                        tf.setText(String.valueOf(currentSettings.xDimension));
                        break;
                    case 4:
                        tf.setText(String.valueOf(currentSettings.yDimension));
                        break;
                    case 5:
                        tf.setText(String.valueOf(currentSettings.initialInhibitionRadius));
                        break;
                    case 6:
                        tf.setText(String.valueOf(currentSettings.potentialRadius));
                        break;
                    case 7:
                        tf.setText(String.valueOf(currentSettings.cellsPerColumn));
                        break;
                    case 8:
                        tf.setText(String.valueOf(currentSettings.newSynapseCount));
                        break;
                    case 9:
                        tf.setText(String.valueOf(currentSettings.desiredLocalActivity));
                        break;
                    case 10:
                        tf.setText(String.valueOf(currentSettings.minOverlap));
                        break;
                    case 11:
                        tf.setText(String.valueOf(currentSettings.connectedPerm));
                        break;
                    case 12:
                        tf.setText(String.valueOf(currentSettings.permanenceInc));
                        break;
                    case 13:
                        tf.setText(String.valueOf(currentSettings.permanenceDec));
                        break;
                    case 14:
                        tf.setText(String.valueOf(currentSettings.activationThreshold));
                        break;
                    case 15:
                        tf.setText(String.valueOf(currentSettings.initialPerm));
                        break;
                    case 16:
                        tf.setText(String.valueOf(currentSettings.minThreshold));
                        break;
                }
            }
        }
    }

    ////////////////////////////////////Listeners//////////////////////////////////////////
    private class SetInputSourceButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            inputLoader = new BitVectorSeqLoader();
        }

    }

    private class LoadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int returnVal = fc.showOpenDialog(null);
            try {
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (file.getName().contains(PROPERTY_POSTFIX)) {
                        try {
                            currentSettings = DHMSettings.loadFromFile(file.getPath());
                            showCurrentSettings();
                        } catch (Exception exc) {
                            System.out.print(exc);
                        }
                    } else if (file.getName().contains(".png")) {
                        img.load(file.getPath());
                        BitMatrix matrix = new BitMatrix(img.getW(), img.getH());
                        matrix = img.getBitMatrix();
                        final TableSelectionClass frame = new TableSelectionClass(matrix);
                        frame.addWindowListener(new WindowAdapter() {
                            public void windowClosing(WindowEvent e) {
                                BitMatrix regsInput = frame.getReturnData();
                                picID_input.put(Integer.valueOf(numOfCell.getText()), regsInput);
                                updateCellsColors(false);
                                //System.exit(0);
                            }
                        });
                        frame.setSize(400, 240);
                        frame.setVisible(true);
                    } else System.out.print("File has non appropriate format \n");
                }
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(null, "File could not be loaded, try again. \n");
            }
        }

    }

    private class ShowDefaultSetButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            currentSettings = DHMSettings.getDefaultSettings();
            showCurrentSettings();
        }
    }

    private class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fc = new JFileChooser();
            fc.setSelectedFile(new File("Region " + numOfCell.getText() + PROPERTY_POSTFIX));
            int status = fc.showSaveDialog(null);

            try {
                if (status == JFileChooser.APPROVE_OPTION) {
                    File saveFile = fc.getSelectedFile();
                    currentSettings = getNewCurrentSettings();
                    currentSettings.saveIntoFile(saveFile.getPath());
                } else if (status == JFileChooser.CANCEL_OPTION) {
                    // User has pressed cancel button
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "File could not be written, try again. \n");
            }
        }
    }


    private void updateCellsColors(Boolean htmNet) {
        Object[] objects = ActiveColsVisGenView.getComponents();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof ShowVisTree) {
                ShowVisTree svt = (ShowVisTree) objects[i];
                Set<Integer> initedCells = new HashSet<Integer>();
                if (!settings.isEmpty())
                    initedCells.addAll(settings.keySet());
                if (!picID_input.isEmpty())
                    initedCells.addAll(picID_input.keySet());
                if (htmNet) initedCells.add(0);
                svt.treePanel.renderer.updateHashMap(initedCells);
            }
        }
    }

    private class SetSettingsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int regID = Integer.valueOf(numOfCell.getText());
            settings.put(regID, getNewCurrentSettings());
            updateCellsColors(false);

        }
    }

    private DHMSettings getNewCurrentSettings() {
        DHMSettings dhmSettings = new DHMSettings();
        Object[] objects = mainPanel.getComponents();
        int counter = 0;
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof JTextField) {
                JTextField tf = (JTextField) objects[i];
                double new_value = Double.parseDouble(tf.getText());
                int property_id = (Integer) tf.getDocument().getProperty("property_id");
                switch (property_id) {
                    case 0:
                        dhmSettings.debug = ( new_value == 1) ? true : false;
                        break;
                    case 1:
                        dhmSettings.xInput = (int) new_value;
                        break;
                    case 2:
                        dhmSettings.yInput = (int) new_value;
                        break;
                    case 3:
                        dhmSettings.xDimension = (int) new_value;
                        break;
                    case 4:
                        dhmSettings.yDimension = (int) new_value;
                        break;
                    case 5:
                        dhmSettings.initialInhibitionRadius = (int) new_value;
                        break;
                    case 6:
                        dhmSettings.potentialRadius = (int) new_value;
                        break;
                    case 7:
                        dhmSettings.cellsPerColumn = (int) new_value;
                        break;
                    case 8:
                        dhmSettings.newSynapseCount = (int) new_value;
                        break;
                    case 9:
                        dhmSettings.desiredLocalActivity = (int) new_value;
                        break;
                    case 10:
                        dhmSettings.minOverlap = (int) new_value;
                        break;
                    case 11:
                        dhmSettings.connectedPerm = new_value;
                        break;
                    case 12:
                        dhmSettings.permanenceInc = new_value;
                        break;
                    case 13:
                        dhmSettings.permanenceDec = new_value;
                        break;
                    case 14:
                        dhmSettings.activationThreshold = new_value;
                        break;
                    case 15:
                        dhmSettings.initialPerm = new_value;
                        break;
                    case 16:
                        dhmSettings.minThreshold = new_value;
                        break;
                }

            }
        }
        return dhmSettings;
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

    private TreePath find(DefaultMutableTreeNode root, String s) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            if (node.toString().equalsIgnoreCase(s)) {
                return new TreePath(node.getPath());
            }
        }
        return null;
    }

    private int checkInitialization(DefaultMutableTreeNode root, int f){
        int ID = (root.toString().contains("HTM")) ? 0 : getID(root.toString());
        int fl = (settings.containsKey(ID) || picID_input.containsKey(ID) || root.toString().contains("HTM")) == true ? 1: 0;
        for (int i = 0; i < root.getChildCount(); i++){
            f =f * fl * checkInitialization((DefaultMutableTreeNode)root.getChildAt(i),f);
        }
        return f;
     }

    private void optimizeStructures(DefaultMutableTreeNode root){
        Integer[] a = new Integer[settings.size()];
        settings.keySet().toArray(a);
        for (int i = 0; i < a.length; i++){
            if (find(root,"Region "+String.valueOf(a[i])) == null)
                settings.remove(a[i]);
        }
        a = new Integer[picID_input.size()];
        picID_input.keySet().toArray(a);
        for (int i = 0; i < a.length; i++){
            if (find(root,"Picture "+String.valueOf(a[i])) == null)
                picID_input.remove(a[i]);
        }
    }

    private class MakeStepButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // проверка на то, что все регионы инициализированы
            Object[] objects = ActiveColsVisGenView.getComponents();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof ShowVisTree ) {
                    ShowVisTree svt = (ShowVisTree) objects[i];
                    if (checkInitialization(svt.treePanel.rootNode,1) == 1) {
                        System.out.print("Initialazation completed successfully \n");
                        updateCellsColors(true);
                        optimizeStructures(svt.treePanel.rootNode);
                        setSettingsButton.setEnabled(false);
                        saveButton.setEnabled(false);
                        loadButton.setEnabled(false);
                        showDefaultSetButton.setEnabled(false);
                        work_mode = true;

                        initCortex();
                        neocortexAction.makeStep();


                    }
                    else
                        System.out.print("Initialization failed \n");
                }
            }

        }
    }

    private void initCortex() {
        VisTree vt = null;
        Object[] objects = ActiveColsVisGenView.getComponents();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof ShowVisTree) {
                vt = ((ShowVisTree)objects[i]).treePanel;
            }
        }
        neocortexAction = new NeocortexAction(settings, picID_input, vt);
        neocortexAction.init(chart2D1, chart2D2, this);

        timer = new Timer(1000, neocortexAction);
        //runCortexButton.setEnabled(true);
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
    /*private class DocumentListenerGeneral implements javax.swing.event.DocumentListener {

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

                    int num_of_reg = Integer.parseInt(numOfCell.getText());
                    double new_value = 0.0;

                    try {
                           new_value = Double.parseDouble(tf.getText());
                    } catch (NumberFormatException ex) {
                        if (num_of_reg != 0 ) System.out.print("Wrong properties for region " + num_of_reg + "\n");
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
    }*/
}