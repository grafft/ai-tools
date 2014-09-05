package ru.isa.ai.dhm.oldcore;

import cern.colt.matrix.tbit.BitVector;

public class Cell {
    int index;

    /*
    Для каждой клетки мы вводим три различных состояния.
    Массивы activeState и predictiveState хранят в себе записи об активных состояниях и
    состояниях предсказания (предчувствия) для каждой из клеток в каждый из моментов времени.
    Массив learnState определяет какие клетки используются во время обучения.
     */
    public BitVector learnState;
    //All bits are initially false.

    public BitVector predictiveState;

    /*
    activeState(c, i, t) это вклад от клетки
    колонки c с номером i во время t. Если это 1, то клетка
    активна при данном прямом входе и временном контексте.
     */
    public BitVector activeState;

    Segment[] dendriteSegments;
    public int dendriteSegmentsNum;

    /*
   Список структур segmentUpdate. segmentUpdateList(c,i) это
   список изменений для клетки i в колонке c.
    */
    SegmentUpdate[] segmentUpdateList;
    int segmentUpdateListNum;

    final private int numSegments = 1000;
    final private int numStateCells = 3;

    public Cell(int i)
    {
       // column = c;
        index = i;
        dendriteSegments = new Segment[numSegments];
        dendriteSegmentsNum = 0;

        segmentUpdateList = new SegmentUpdate[numSegments];
        segmentUpdateListNum = 0;

        learnState = new BitVector(numStateCells);
        predictiveState = new BitVector(numStateCells);
        activeState = new BitVector(numStateCells);
    }

    public void clearSegmentUpdateList(){
        for (int i = 0 ; i < segmentUpdateListNum; i++)
            segmentUpdateList[i] = null;

        segmentUpdateListNum = 0;
    }
}