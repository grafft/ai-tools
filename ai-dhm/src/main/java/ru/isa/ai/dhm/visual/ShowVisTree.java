package ru.isa.ai.dhm.visual;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

public class ShowVisTree extends JPanel
        implements ActionListener {


    private int newNodeSuffix = 1;
    private static String ADD_COMMAND = "add";
    private static String REMOVE_COMMAND = "remove";
    private static String CLEAR_COMMAND = "clear";

    private VisTree treePanel;

    public ShowVisTree() {
        super(new BorderLayout());

        treePanel = new VisTree();
        populateTree(treePanel);

        JButton addButton = new JButton("Add");
        addButton.setActionCommand(ADD_COMMAND);
        addButton.addActionListener(this);

        JButton removeButton = new JButton("Remove");
        removeButton.setActionCommand(REMOVE_COMMAND);
        removeButton.addActionListener(this);

        JButton clearButton = new JButton("Clear");
        clearButton.setActionCommand(CLEAR_COMMAND);
        clearButton.addActionListener(this);

        //Lay everything out.
        treePanel.setPreferredSize(new Dimension(300, 150));
        add(treePanel, BorderLayout.WEST);

        JPanel panel = new JPanel(new GridLayout(0,3));
        panel.add(addButton);
        panel.add(removeButton);
        panel.add(clearButton);
        add(panel, BorderLayout.SOUTH);
    }

    public void populateTree(VisTree treePanel) {
        String name = new String("Region");

        DefaultMutableTreeNode p1, p2;

        p1 = treePanel.addObject(null, name);
        p2 = treePanel.addObject(null, name);

        treePanel.addObject(p1, name);
        treePanel.addObject(p1, name);

        treePanel.addObject(p2, name);
        treePanel.addObject(p2, name);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (ADD_COMMAND.equals(command)) {
            treePanel.addObject("Region");// + newNodeSuffix++);
        } else if (REMOVE_COMMAND.equals(command)) {
            treePanel.removeCurrentNode();
        } else if (CLEAR_COMMAND.equals(command)) {
            treePanel.clear();
        }
    }
}