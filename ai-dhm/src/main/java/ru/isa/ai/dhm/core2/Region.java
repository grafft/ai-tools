package ru.isa.ai.dhm.core2;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.RegionSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Region {
    private List<Region> childRegions = new ArrayList<>();
    private List<Region> parentRegions = new ArrayList<>();
    private Column[] columns;
    private BitVector activeColumns;
    private RegionSettings settings;

    public Region(RegionSettings settings) {
        this.settings = settings;
        columns = new Column[settings.numColumns];
        for (int i = 0; i < settings.numColumns; i++) {
            columns[i] = new Column(i, settings);
        }
        activeColumns = new BitVector(settings.numColumns);
    }

    public void initialization() {
        for (Column column : columns) {
            double pctOfInput = column.getIndex() / Math.max(columns.length - 1, 1.0);
            column.initialization(pctOfInput);
        }
    }

    public BitVector compute(BitVector input){
        sOverlap();
        sInhibition();
        sLearning();

        return activeColumns;
    }

    private void sLearning() {
        for(Column column : columns){

        }
    }

    private void sInhibition() {

    }

    private void sOverlap() {

    }

    public void addParent(Region parent) {
        parentRegions.add(parent);
    }

    public void addChild(Region child) {
        childRegions.add(child);
    }
}
