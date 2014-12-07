package ru.isa.ai.dhm.visual;

import cern.colt.matrix.tbit.BitMatrix;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.*;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class TableSelectionClass extends JFrame {

    private BitMatrix data;
    private BitMatrix returnData;

    public BitMatrix getReturnData(){
        return  returnData;
    }

    public TableSelectionClass(BitMatrix data_) {
        super("Select the input for the region");

        data = new BitMatrix(data_.columns(),data_.rows());
        data = data_;

        JTable table;
        final Color color = UIManager.getColor("Table.gridColor");

        DefaultTableModel dm = new DefaultTableModel(data.rows(), data.columns()) {
            public void setValueAt(Object obj, int row, int col) {
                if (obj instanceof MyData) {
                    super.setValueAt(obj, row, col);
                } else {
                    MyData myData = null;
                    Object oldObject = getValueAt(row, col);
                    if (oldObject == null) {
                        myData = new MyData(obj, new LinesBorder(color, 0));
                    } else if (oldObject instanceof MyData) {
                        myData = (MyData) oldObject;
                    } else {
                        System.out.println("error");
                        return;
                    }
                    myData.setObject(obj);
                    super.setValueAt(myData, row, col);
                }
            }


        };

        table = new JTable(dm);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.setDefaultRenderer(Object.class, new BorderCellRenderer(data));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setPreferredSize(new Dimension(0,0));
        for (int i=0; i < table.getColumnCount(); i++) {
            TableColumn tc = table.getColumnModel().getColumn(i);
            // tc.setCellRenderer(new MyImageRenderer());
            tc.setPreferredWidth(15);

        }
        for (int i=0; i < table.getRowCount(); i++) {
            table.setRowHeight(i, 15);
        }

        JScrollPane scroll = new JScrollPane(table);
        //ThicknessPanel thicknessPanel = new ThicknessPanel();
        Box box = new Box(BoxLayout.Y_AXIS);
        //box.add(thicknessPanel);
        box.add(new ButtonPanel(table));//, thicknessPanel));
        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(box, BorderLayout.EAST);
    }

    class ButtonPanel extends JPanel {
        JTable table;

        Color color = UIManager.getColor("Table.gridColor");

        ButtonPanel(final JTable table){
            this.table = table;
            setLayout(new GridLayout(3, 1));
            setBorder(new TitledBorder("Action:"));
            JButton b_and = new JButton("Check");

            add(b_and);

            b_and.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setCellBorder(true, true);
                }
            });

        }

        private void formReturnData(){
            int firstCol = table.getSelectedColumn();
            int firstRow = table.getSelectedRow();
            int amountCols = table.getSelectedColumnCount();
            int amountRows = table.getSelectedRowCount();
            returnData = new BitMatrix(amountCols,amountRows);
            int k=0,l=0;
            for (int i= firstRow; i < firstRow+amountRows; i++,k++){
                l=0;
                for (int j = firstCol; j < firstCol+amountCols; j++, l++){
                    returnData.put(l,k, data.get(j,i));
                }
            }
            System.out.print("");
        }

        private void setCellBorder(boolean isReplace, boolean isBlock) {
            boolean isTop, isLeft, isBottom, isRight;
            Insets insets = new Insets(3, 3,3, 3);
            int[] columns = table.getSelectedColumns();
            int[] rows = table.getSelectedRows();
            formReturnData();
            int rowMax = rows.length;
            int columnMax = columns.length;

            for (int i = 0; i < rowMax; i++) {
                int row = rows[i];
                isTop = (i == 0) ? true : false;
                isBottom = (i == rowMax - 1) ? true : false;

                for (int j = 0; j < columnMax; j++) {
                    int column = columns[j];
                    isLeft = (j == 0) ? true : false;
                    isRight = (j == columnMax - 1) ? true : false;

                    MyData myData = (MyData) table.getValueAt(row, column);
                    if (myData == null) {
                        myData = new MyData("", new LinesBorder(color, 0));
                    }
                    LinesBorder border = (LinesBorder) myData.getBorder();

                    if (true) {
                        Insets tmp = new Insets(0, 0, 0, 0);
                        if (isTop)
                            tmp.top = Math.max(tmp.top, insets.top);
                        if (isLeft)
                            tmp.left = Math.max(tmp.left, insets.left);
                        if (isBottom)
                            tmp.bottom = Math.max(tmp.bottom, insets.bottom);
                        if (isRight)
                            tmp.right = Math.max(tmp.right, insets.right);
                        border.append(tmp, isReplace);
                    } else {
                        border.append(insets, isReplace);
                    }

                    table.setValueAt(myData, row, column);
                }
            }
            table.clearSelection();
            table.revalidate();
            table.repaint();
        }
    }

    class MyData implements CellBorder {
        private Border border;

        private Object obj;

        public MyData(Object obj, Border border) {
            this.obj = obj;
            this.border = border;
        }

        public void setObject(Object obj) {
            this.obj = obj;
        }

        public String toString() {
            return obj.toString();
        }

        // CellBorder
        public void setBorder(Border border) {
            this.border = border;
        }

        public Border getBorder() {
            return border;
        }

        public void setBorder(Border border, int row, int col) {
        }

        public Border getBorder(int row, int col) {
            return null;
        }
    }
}

class BorderCellRenderer extends JLabel implements TableCellRenderer {
    protected Border noFocusBorder;

    protected Border columnBorder;

    public BorderCellRenderer(BitMatrix data_) {
        noFocusBorder = new EmptyBorder(1, 2, 1, 2);
        setOpaque(true);
        data = new BitMatrix(data_.columns(), data_.rows());
        data = data_;
    }
    public BitMatrix data;

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Color f,b;
        if (isSelected) {
            f = table.getSelectionForeground();
            setForeground(f);
            b = table.getSelectionBackground();
            Color v = (data.get(column,row) == false) ? Color.white : Color.black;
            Color new_b = new Color((b.getRed()+v.getRed())/2, (b.getGreen() + v.getGreen())/2, (b.getBlue() + v.getBlue())/2);
            setBackground(new_b);
        } else {
            f = table.getForeground();
            setForeground(f);
            b = (data.get(column,row) == false) ? Color.white : Color.black; //table.getBackground();
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

    public void setColumnBorder(Border border) {
        columnBorder = border;
    }

    public Border getColumnBorder() {
        return columnBorder;
    }

}

interface CellBorder {

    public Border getBorder();

    public Border getBorder(int row, int column);

    public void setBorder(Border border);

    public void setBorder(Border border, int row, int column);

}

class LinesBorder extends AbstractBorder implements SwingConstants {
    protected int northThickness;

    protected int southThickness;

    protected int eastThickness;

    protected int westThickness;

    protected Color northColor;

    protected Color southColor;

    protected Color eastColor;

    protected Color westColor;

    public LinesBorder(Color color) {
        this(color, 1);
    }

    public LinesBorder(Color color, int thickness) {
        setColor(Color.red);
        setThickness(thickness);
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width,
                            int height) {
        Color oldColor = g.getColor();

        g.setColor(northColor);
        for (int i = 0; i < northThickness; i++) {
            g.drawLine(x, y + i, x + width - 1, y + i);
        }
        g.setColor(southColor);
        for (int i = 0; i < southThickness; i++) {
            g
                    .drawLine(x, y + height - i - 1, x + width - 1, y + height
                            - i - 1);
        }
        g.setColor(eastColor);
        for (int i = 0; i < westThickness; i++) {
            g.drawLine(x + i, y, x + i, y + height - 1);
        }
        g.setColor(westColor);
        for (int i = 0; i < eastThickness; i++) {
            g.drawLine(x + width - i - 1, y, x + width - i - 1, y + height - 1);
        }

        g.setColor(oldColor);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(northThickness, westThickness, southThickness,
                eastThickness);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        return new Insets(northThickness, westThickness, southThickness,
                eastThickness);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void setColor(Color c) {
        northColor = c;
        southColor = c;
        eastColor = c;
        westColor = c;
    }

    public void setColor(Color c, int direction) {
        switch (direction) {
            case NORTH:
                northColor = c;
                break;
            case SOUTH:
                southColor = c;
                break;
            case EAST:
                eastColor = c;
                break;
            case WEST:
                westColor = c;
                break;
            default:
        }
    }

    public void setThickness(int n) {
        northThickness = n;
        southThickness = n;
        eastThickness = n;
        westThickness = n;
    }

    public void setThickness(Insets insets) {
        northThickness = insets.top;
        southThickness = insets.bottom;
        eastThickness = insets.right;
        westThickness = insets.left;
    }

    public void setThickness(int n, int direction) {
        switch (direction) {
            case NORTH:
                northThickness = n;
                break;
            case SOUTH:
                southThickness = n;
                break;
            case EAST:
                eastThickness = n;
                break;
            case WEST:
                westThickness = n;
                break;
            default:
        }
    }

    public void append(LinesBorder b, boolean isReplace) {
        if (isReplace) {
            northThickness = b.northThickness;
            southThickness = b.southThickness;
            eastThickness = b.eastThickness;
            westThickness = b.westThickness;
        } else {
            northThickness = Math.max(northThickness, b.northThickness);
            southThickness = Math.max(southThickness, b.southThickness);
            eastThickness = Math.max(eastThickness, b.eastThickness);
            westThickness = Math.max(westThickness, b.westThickness);
        }
    }

    public void append(Insets insets, boolean isReplace) {
        if (isReplace) {
            northThickness = insets.top;
            southThickness = insets.bottom;
            eastThickness = insets.right;
            westThickness = insets.left;
        } else {
            northThickness = Math.max(northThickness, insets.top);
            southThickness = Math.max(southThickness, insets.bottom);
            eastThickness = Math.max(eastThickness, insets.right);
            westThickness = Math.max(westThickness, insets.left);
        }
    }

}
