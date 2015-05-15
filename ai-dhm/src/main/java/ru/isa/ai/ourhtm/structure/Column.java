package ru.isa.ai.ourhtm.structure;



import java.util.ArrayList;

/**
 * Created by APetrov on 13.05.2015.
 */
public class Column {

    public Column(int cellNum)
    {
        cells=new Cell[cellNum];
        proximalDendrite=new ProximalDendrite();
    }
    private Cell[] cells; // клетки данной колонки
    private ProximalDendrite proximalDendrite;
    public ProximalDendrite getProximalDendrite()
    {return proximalDendrite;}
}
