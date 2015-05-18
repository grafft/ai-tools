package ru.isa.ai.ourhtm.structure;

import java.util.ArrayList;

/**
 * Created by APetrov on 15.05.2015.
 */
public class Region {
    ArrayList<Column> columns;
    HTMSettings settings;
    public Region(HTMSettings set)
    {
        settings=set;
        columns=new ArrayList<>(set.xDimension*set.yDimension);
        for (int i = 0; i < set.xDimension; i++) {
            for (int j = 0; j < set.yDimension; j++) {
                int inputCenterX = (int) Math.floor((i + 1 - 0.5) * settings.xInput / settings.xDimension);
                int inputCenterY = (int) Math.floor((j + 1 - 0.5) * settings.yInput / settings.yDimension);
                columns.add(new Column(new int[]{i, j}, new int[]{inputCenterX, inputCenterY}, set.potentialRadius, set.connectedPct, set.cellsPerColumn, this));
            }
        }
    }

    public ArrayList<Column> getColumns()
    {
        return columns;
    }

    public int getInputW(){ return settings.xInput;}
    public int getInputH(){return settings.yInput;}
}
