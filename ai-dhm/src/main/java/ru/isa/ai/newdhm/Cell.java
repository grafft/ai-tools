package ru.isa.ai.newdhm;

import cern.colt.bitvector.BitVector;
import cern.colt.matrix.tbit.BitMatrix;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    int index;

    /*
    Для каждой клетки мы вводим три различных состояния.
    Массивы activeState и predictiveState хранят в себе записи об активных состояниях и
    состояниях предсказания (предчувствия) для каждой из клеток в каждый из моментов времени.
    Массив learnState определяет какие клетки используются во время обучения.
     */
    BitVector learnState;
    //All bits are initially false.

    BitVector predictiveState;

    /*
    activeState(c, i, t) это вклад от клетки
    колонки c с номером i во время t. Если это 1, то клетка
    активна при данном прямом входе и временном контексте.
     */
    BitVector activeState;

    Segment[] dendriteSegments;
    int dendriteSegmentsNum;

    /*
   Список структур segmentUpdate. segmentUpdateList(c,i) это
   список изменений для клетки i в колонке c.
    */
    SegmentUpdate[] segmentUpdateList;
    int segmentUpdateListNum;

    public Cell(int i)
    {
       // column = c;
        index = i;
        dendriteSegments = new Segment[1000];
        dendriteSegmentsNum = 0;

        segmentUpdateList = new SegmentUpdate[1000];
        segmentUpdateListNum = 0;

        learnState = new BitVector(1000);
        predictiveState = new BitVector(1000);
        activeState = new BitVector(1000);
    }

    public void clearSegmentUpdateList(){
        for (int i = 0 ; i < segmentUpdateList.length; i++)
            segmentUpdateList[i] = null;

        segmentUpdateListNum = 0;
    }
}