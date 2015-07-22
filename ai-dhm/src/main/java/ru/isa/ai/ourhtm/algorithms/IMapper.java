package ru.isa.ai.ourhtm.algorithms;

import casmi.matrix.Vector2D;

import java.util.ArrayList;

/**
 * Created by APetrov on 20.07.2015.
 */
public interface IMapper {
    public ArrayList<Vector2D> mapOne(int[] inputWH, int[] colCoord, int radius);
    public ArrayList<ArrayList<Vector2D>> mapAll(int[] inputWH, int[] colsWH, int radius) throws Exception;
}
