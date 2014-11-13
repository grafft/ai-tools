package ru.isa.ai.dhm;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVWriteProc;
import au.com.bytecode.opencsv.CSVWriter;
import ru.isa.ai.dhm.core.Column;
import ru.isa.ai.dhm.core.Neocortex;
import ru.isa.ai.dhm.core.Region;

import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Created by gmdidro on 12.11.2014.
 */
public class LogUtils {

    static FileWriter mCellsWriter = null;
    static FileWriter mCollsWriter = null;

    /**
     * Выводит в 2 cvs-файла и очищает их содержимое
     */
    public static void Open(String outPutFileCells, String outPutFileColumns){
        try {
            mCellsWriter = new FileWriter(outPutFileCells, false);
            mCellsWriter.close();
            mCellsWriter = new FileWriter(outPutFileCells, true);

            mCollsWriter = new FileWriter(outPutFileColumns, false);
            mCollsWriter.close();
            mCollsWriter = new FileWriter(outPutFileColumns, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Выводит в 2 файла текущие параметры клеток и колонок одного (первого) региона неокортекса
     * Для большего числа регионов нужно дописывать
     * @param neo
     */
    public static void printToCVS(Neocortex neo){
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
                //for (Region reg : neocortex.getRegions()) {
                {
                    Region reg=neocortex.getRegions().get(0);
                    int dim[] = reg.getDimensions();
                    int ColsW = dim[0];
                    int ColsH = dim[1];
                    int cellsPerCol = dim[2];

                    for (int colLine = 0; colLine < ColsH; colLine++) {
                        // перебор всех слоев клеток
                        String s[]=new String[ColsW*cellsPerCol+cellsPerCol*2];
                        int i=0;
                        for (int layer = 0; layer < cellsPerCol; layer++) {
                            for (int col = colLine * ColsW; col < (colLine + 1) * ColsW; col++) {
                                s[i]=String.valueOf(reg.getColumns().get(col).getCells()[layer].getStateHistory()[1]);
                                i++;
                            }
                            s[i++]="";
                            s[i++]="";
                        }
                        out.writeNext(s);
                    }
                }
            }
        });

        CSVWriter mCsvWriter2 = new CSVWriter(mCollsWriter,';',CSVWriter.NO_QUOTE_CHARACTER);

        csv.write(mCsvWriter2, new CSVWriteProc() {
            public void process(CSVWriter out) {
                out.writeNext("Activity", "Overlap", "Boost", "NeighborsSize");
                //for (Region reg : neocortex.getRegions()) {
                {
                    Region reg=neocortex.getRegions().get(0);
                    int dim[] = reg.getDimensions();
                    int ColsW = dim[0];
                    int ColsH = dim[1];
                    int cellsPerCol = dim[2];
                    final int params = 4;

                    for (int colLine = 0; colLine < ColsH; colLine++) {
                        String s[] = new String[ColsW * params + params * 2];
                        int i = 0;

                        for (int col = colLine * ColsW; col < (colLine + 1) * ColsW; col++) {
                            s[i] = String.valueOf(reg.getColumns().get(col).isActive());
                            i++;
                        }
                        s[i++] = "";
                        s[i++] = "";

                        for (int col = colLine * ColsW; col < (colLine + 1) * ColsW; col++) {
                            s[i] = String.valueOf(reg.getColumns().get(col).getOverlap());
                            i++;
                        }
                        s[i++] = "";
                        s[i++] = "";

                        for (int col = colLine * ColsW; col < (colLine + 1) * ColsW; col++) {
                            s[i] = String.valueOf(reg.getColumns().get(col).getProximalSegment().getBoostFactor());
                            i++;
                        }
                        s[i++] = "";
                        s[i++] = "";

                        for (int col = colLine * ColsW; col < (colLine + 1) * ColsW; col++) {
                            s[i] = String.valueOf(reg.getColumns().get(col).getNeighbors().size());
                            i++;
                        }
                        s[i++] = "";
                        s[i++] = "";

                        out.writeNext(s);
                    }

                }
            }
        });
    }
}
