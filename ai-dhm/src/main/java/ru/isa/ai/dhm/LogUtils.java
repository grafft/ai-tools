package ru.isa.ai.dhm;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVWriteProc;
import au.com.bytecode.opencsv.CSVWriter;
import ru.isa.ai.dhm.core.Column;
import ru.isa.ai.dhm.core.Neocortex;
import ru.isa.ai.dhm.core.Region;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by gmdidro on 12.11.2014.
 */
public class LogUtils {

    static FileWriter mCellsWriter = null;
    static FileWriter mCollsWriter = null;

    public static void Open(String outPutFileCells, String outPutFileColumns){
        try {
            mCellsWriter = new FileWriter(outPutFileCells, true);
            mCollsWriter = new FileWriter(outPutFileColumns, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createCSVExportFiles(Neocortex neo){
        CSV csv = CSV
                .separator(';')
                .quote('\'')
                .skipLines(1)
                .charset("UTF-8")
                .create();
        // CSVWriter will be closed after end of processing
        final Neocortex neocortex=neo;

        CSVWriter mCsvWriter = new CSVWriter(mCellsWriter,';',CSVWriter.NO_QUOTE_CHARACTER);

        csv.write(mCsvWriter, new CSVWriteProc() {
            public void process(CSVWriter out) {
                out.writeNext("Cell Index", "State");
                for (Region reg: neocortex.getRegions()){
                    for (Column col : reg.getColumns().values()){
                        String[] s=new String[col.getCells().length];
                        for (int i=0;i<col.getCells().length;i++){
                            s[i] = String.valueOf(col.getCells()[i].getStateHistory()[1]);
                        }
                        out.writeNext(s);
                    }
                }
            }
        });

        CSVWriter mCsvWriter2 = new CSVWriter(mCollsWriter,';',CSVWriter.NO_QUOTE_CHARACTER);

        csv.write(mCsvWriter2, new CSVWriteProc() {
            public void process(CSVWriter out) {
                out.writeNext("Column Index", "Activity","Overlap","Boost");
                for (Region reg: neocortex.getRegions()){
                    for (Column col : reg.getColumns().values()){
                        out.writeNext(String.valueOf(col.getIndex()), String.valueOf(col.isActive()), String.valueOf(col.getOverlap()), String.valueOf(col.getProximalSegment().getBoostFactor()));
                    }
                }
            }
        });
    }
}
