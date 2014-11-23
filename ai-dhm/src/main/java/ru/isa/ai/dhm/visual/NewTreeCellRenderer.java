package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tint.IntMatrix1D;
import com.sun.java.util.jar.pack.*;
import ru.isa.ai.dhm.core.Region;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.tree.*;
import java.lang.reflect.Field;

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
    private Map<Integer, Boolean> initedRegs = new HashMap<>();

    public NewTreeCellRenderer() {
        this.setLayout(new BorderLayout());
    }

    public void updateHashMap(Map<Integer, Boolean> initedRegs_){
        initedRegs = initedRegs_;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, sel,
                expanded, leaf, row, hasFocus);
        this.tree = tree;
        tree.setRowHeight(25);

        String val = value.toString();
        if (sel) {
            background = b_focusColor;
            foreground = f_focusColor;
        } else {
            Boolean inited = false;
            if (!initedRegs.isEmpty() && val !="Picture" && val != "HTM Network") {
                String sub = val.substring(val.indexOf(" ") + 1);
                Object obj = initedRegs.get(Integer.valueOf(sub));
                if (obj != null)
                    inited = true;
            }
            background = (inited == true) ? b_Color_with_settings : b_Color_without_settings;
            foreground = f_Color;
        }
        JPanel p = new JPanel();
        JLabel l = new JLabel(val/*имя узла*/);
        l.setHorizontalTextPosition(JLabel.CENTER);
        l.setBackground(background);
        l.setForeground(foreground);
        p.add(l);
        p.setBackground(background);
        return p;

    }
}