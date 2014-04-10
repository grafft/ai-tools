package ru.isa.ai.tests;

import de.intarsys.pdf.cos.COSDocumentElement;
import de.intarsys.pdf.cos.COSInfoDict;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.tools.locator.FileLocator;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Aleksandr Panov
 * Date: 27.03.2014
 * Time: 17:42
 */
public class PDFTagger {
    public static void main(String[] args) {
        File sourceDir = new File("C:\\Users\\Alexandr\\Google Диск\\Science\\Аспирантура\\Литература");

        try {
            final List<PDDocument> documents = scanDir(sourceDir);
            final List<String> columnNames = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            //map.get()
            for (int i = 0; i < documents.size(); i++) {
                final COSInfoDict dict = documents.get(i).getInfoDict();
                if (i == 0) {
                    Iterator it = dict.cosGetDict().entryIterator();
                    while (it.hasNext())
                        columnNames.add(((COSName) ((Map.Entry) it.next()).getKey()).stringValue());

                }
//                System.out.println(String.format("%d) %s\ntitle:%s\nauthor: %s\ncreation date: %s\nWoS times cited: %s\nkeywords: %s\n",
//                        i + 1,
//                        documents.get(i).getName(),
//                        dict.getTitle(),
//                        dict.getAuthor(),
//                        dict.getCreationDate() != null ? dict.getCreationDate().toFormattedString() : "null",
//                        dict.getFieldString(COSName.constant("WoS cited"), null),
//                        dict.getKeywords()));
//                Matcher matcher = Pattern.compile("(\\d+)\\{(\\d+)\\}\\[(.+)\\](.+)").matcher(documents.get(i).getName());
//                if (matcher.matches()) {
//                    String authors = matcher.group(3);
//                    if (dict.getAuthor() == null || !dict.getAuthor().equals(authors)) {
//                        documents.get(i).setAuthor(authors);
//                        documents.get(i).save();
//                    }
//                    String title = matcher.group(4);
//                    if (dict.getTitle() == null || !dict.getTitle().equals(title)) {
//                        documents.get(i).setTitle(title);
//                        documents.get(i).save();
//                    }
//                    String cited = matcher.group(2);
//                    if (dict.getFieldString(COSName.constant("WoS cited"), null) == null) {
//                        documents.get(i).setDocumentInfo(COSName.constant("WoS cited"), cited);
//                        documents.get(i).save();
//                    }
//                }
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new JFrame("PDF Metadata") {
                        {
                            JTable table = new JTable(new TableModel() {
                                @Override
                                public int getRowCount() {
                                    return documents.size();
                                }

                                @Override
                                public int getColumnCount() {
                                    return columnNames.size();
                                }

                                @Override
                                public String getColumnName(int columnIndex) {
                                    return columnNames.get(columnIndex);
                                }

                                @Override
                                public Class<?> getColumnClass(int columnIndex) {
                                    return String.class;
                                }

                                @Override
                                public boolean isCellEditable(int rowIndex, int columnIndex) {
                                    return true;
                                }

                                @Override
                                public Object getValueAt(int rowIndex, int columnIndex) {
                                    return documents.get(rowIndex).getInfoDict().getFieldString(COSName.constant(columnNames.get(columnIndex)), "");
                                }

                                @Override
                                public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

                                }

                                @Override
                                public void addTableModelListener(TableModelListener l) {

                                }

                                @Override
                                public void removeTableModelListener(TableModelListener l) {

                                }
                            });
                            JScrollPane scrollPane = new JScrollPane(table);
                            table.setFillsViewportHeight(true);

                            getContentPane().setLayout(new BorderLayout());
                            getContentPane().add(table.getTableHeader(), BorderLayout.PAGE_START);
                            getContentPane().add(table, BorderLayout.CENTER);

                            addWindowListener(new WindowAdapter() {
                                public void windowClosing(WindowEvent e) {
                                    System.exit(0);
                                }
                            });

                            pack();
                            setVisible(true);
                        }
                    };
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<PDDocument> scanDir(File sourceDir) throws IOException {
        List<PDDocument> documents = new ArrayList<>();
        for (File file : sourceDir.listFiles()) {
            if (file.isDirectory())
                documents.addAll(scanDir(file));
            else {
                PDDocument doc = null;
                try {
                    doc = PDDocument.createFromLocator(new FileLocator(file));
                    documents.add(doc);
                } catch (COSLoadException e) {

                }
            }

        }
        return documents;
    }
}
