package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitMatrix;
import ru.isa.ai.dhm.core.Region;
import cern.colt.matrix.tbit.BitVector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;

public class ActColsVisClass extends JPanel {
    private JTable tableUp;
    private JTable tableDown;
    private BitMatrix dataUp;
    private BitMatrix dataDown;

    private BitMatrix getDataForRegion(Region r){

        int[] dims = r.getDimensions();
        BitMatrix m = new BitMatrix(dims[0], dims[1]);

        BitVector v = r.getActiveColumns();
        for (int i = 0; i < dims[0]; i++){
            for (int j = 0 ; j < dims[1]; j++){
                m.put(i,j,v.get(i*dims[1]+j));
            }
        }
        return m;
    }

    private void initTable(JTable t, CellRenderer cr){
        //t.setIntercellSpacing(new Dimension(0, 0));
        t.setCellSelectionEnabled(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setDefaultRenderer(Object.class, cr);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        t.getTableHeader().setPreferredSize(new Dimension(0,0));
        for (int i=0; i < t.getColumnCount(); i++) {
            TableColumn tc = t.getColumnModel().getColumn(i);
            tc.setPreferredWidth(10);
        }
        for (int i=0; i < t.getRowCount(); i++) {
            t.setRowHeight(i, 10);
        }
    }


    public ActColsVisClass(Region up, Object down) {
        dataUp = getDataForRegion(up);

        if (down instanceof Region)
            dataDown= getDataForRegion((Region) down);
        else
            dataDown = (BitMatrix)down;

        DefaultTableModel dmUp = new DefaultTableModel(dataUp.rows(), dataUp.columns()) {
        };
        DefaultTableModel dmDown = new DefaultTableModel(dataDown.rows(), dataDown.columns()) {
        };
        CellRenderer cr = new CellRenderer(up);

        tableUp = new JTable(dmUp);
        tableUp.putClientProperty("owner","up");
        initTable(tableUp, cr);

        tableDown = new JTable(dmDown);
        tableDown.setCellSelectionEnabled(false);
        tableDown.putClientProperty("owner","down");
        initTable(tableDown,cr);
        tableDown.setEnabled(false);

        setLayout(new GridLayout());

        JPanel p = new JPanel(new GridLayout(2,1, 0,30));

        JScrollPane scrollUp = new JScrollPane(tableUp);
        p.add(scrollUp);

        JScrollPane scrollDown = new JScrollPane(tableDown);
        p.add(scrollDown);

        add(p, BorderLayout.CENTER);
    }



class CellRenderer extends JLabel implements TableCellRenderer {
    protected Border noFocusBorder;

    protected Border columnBorder;

    public CellRenderer(final Region up_) {
        noFocusBorder = new EmptyBorder(1, 2, 1, 2);
        setOpaque(true);
        up = up_;
    }

    private Vector<Point> sel_down_cells = new Vector<Point>();
    private Region up;

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Color f, b,v;
        if (isSelected) {
            f = table.getSelectionForeground();
            setForeground(f);
            b = table.getSelectionBackground();
            if (table.getClientProperty("owner") == "up") {
                v = (dataUp.get(column, row) == false) ? Color.white : Color.black;
                sel_down_cells = new Vector<Point>();
                java.util.List<Integer> indices = new ArrayList<>();
                indices = up.getColumns().get((row)*dataUp.columns()+column).getProximalSegment().connectedSynapses();
                for (int i = 0; i < indices.size(); i++) {
                    Point p = new Point();
                    int tmp = indices.get(i) % dataDown.columns();
                    p.x = (indices.get(i) - tmp) / dataDown.columns(); //ряд
                    p.y = tmp;
                    sel_down_cells.add(p);
                }
                tableDown.repaint();
            }
            else
                v = (dataDown.get(column, row) == false) ? Color.white : Color.black;
            Color new_b = new Color((b.getRed() + v.getRed()) / 2, (b.getGreen() + v.getGreen()) / 2, (b.getBlue() + v.getBlue()) / 2);
            setBackground(new_b);

        } else {
            f = table.getForeground();
            v = table.getSelectionBackground();
            setForeground(f);
            if (table.getClientProperty("owner") == "up")
                b = (dataUp.get(column, row) == false) ? Color.white : Color.black;
            else {
                b = (dataDown.get(column, row) == false) ? Color.white : Color.black;
                if (!sel_down_cells.isEmpty() && sel_down_cells.contains(new Point(row,column)))
                    b = new Color((b.getRed() + v.getRed()) / 2, (b.getGreen() + v.getGreen()) / 2, (b.getBlue() + v.getBlue()) / 2);
            }
            setBackground(b);
        }
        setFont(table.getFont());

        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (table.isCellEditable(row, column)) {
                setForeground(UIManager.getColor("Table.focusCellForeground"));
                setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        } else {
            if (value instanceof CellBorder) {
                Border border = ((CellBorder) value).getBorder();
                setBorder(border);
            } else {
                if (columnBorder != null) {
                    setBorder(columnBorder);
                } else {
                    setBorder(noFocusBorder);
                }
            }
        }
        setText((value == null) ? "" : value.toString());
        return this;
    }

}}


