package ru.isa.ai.dhm.visual;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.tree.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tint.IntMatrix1D;
import ru.isa.ai.dhm.core.Region;

public class NewTreeCellRenderer extends JPanel implements TreeCellRenderer{
    protected Color background;
    protected Color foreground;
    protected Color b_focusColor = Color.yellow;//(Color) UIManager.get("Tree.selectionBackground");
    protected Color f_focusColor = (Color) UIManager.get("Tree.selectionForeground");
    protected Color b_Color_without_settings = Color.pink; //(Color) UIManager.get("Tree.Background");
    protected Color b_Color_with_settings = Color.green;
    protected Color f_Color = (Color) UIManager.get("Tree.Foreground");
    protected JTree tree;
    protected Component c;

    public NewTreeCellRenderer() {
        this.setLayout(new BorderLayout());
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, sel,
                expanded, leaf, row, hasFocus);
        this.tree = tree;
        tree.setRowHeight(25);

        if (sel) {
            background = b_focusColor;
            foreground = f_focusColor;
        } else {
            background = b_Color_without_settings;
            foreground = f_Color;
        }
        JPanel p = new JPanel();
        JLabel l = new JLabel(value.toString()/*имя узла*/);
        l.setHorizontalTextPosition(JLabel.CENTER);
        l.setBackground(background);
        l.setForeground(foreground);
        p.add(l);
        p.setBackground(background);
        return p;

    }
}