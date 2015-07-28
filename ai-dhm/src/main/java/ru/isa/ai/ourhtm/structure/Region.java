package ru.isa.ai.ourhtm.structure;

import casmi.matrix.Vector2D;
import ru.isa.ai.ourhtm.algorithms.IMapper;
import ru.isa.ai.ourhtm.algorithms.VerySimpleMapper;

import java.util.ArrayList;

/**
 * Created by APetrov on 15.05.2015.
 */
public class Region {
    IMapper mapper;
    ArrayList<Column> columns;
    HTMSettings settings;
    public Region(HTMSettings set, IMapper mapper)
    {
        settings=set;
        this.mapper=mapper;
        ArrayList<ArrayList<Vector2D>> bottomIndices=null;
        try {
            bottomIndices = mapper.mapAll(new int[]{set.xInput, set.yInput}, new int[]{set.xDimension, set.yDimension}, set.potentialRadius);
        }
        catch (Exception e){
            System.out.print(e);
        }
        columns=new ArrayList<>(set.xDimension*set.yDimension);
        for (int i = 0; i < set.xDimension; i++) {
            for (int j = 0; j < set.yDimension; j++)
                columns.add(new Column(new int[]{i, j}, bottomIndices.get(i*settings.yDimension+j), this, settings));
        }
    }

    public ArrayList<Column> getColumns()
    {
        return columns;
    }

    public int getInputW(){ return settings.xInput;}
    public int getInputH(){return settings.yInput;}
}
