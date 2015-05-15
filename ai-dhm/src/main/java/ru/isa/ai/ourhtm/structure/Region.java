package ru.isa.ai.ourhtm.structure;

import java.util.ArrayList;

/**
 * Created by APetrov on 15.05.2015.
 */
public class Region {
    ArrayList<Column> columns;

    public Region(int w, int h, int cellPerCol)
    {
        columns=new ArrayList<>(w*h);
        for(int i=0;i<w*h;i++)
        {
            columns.add(new Column(cellPerCol));
        }
    }

    public ArrayList<Column> getColumns()
    {
        return columns;
    }
}
