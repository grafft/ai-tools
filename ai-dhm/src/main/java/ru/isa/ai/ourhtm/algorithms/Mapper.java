package ru.isa.ai.ourhtm.algorithms;

import casmi.matrix.Matrix2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by APetrov on 29.05.2015.
 */
public class Mapper {

    // возвращает список колонок, для каждой колонки координаты верхнего левого и правого нижнего углов прямоугольника,
    // который задает область входного вектора, связанную с данной колонкой
    static public ArrayList<int[]> map(int[] inputWH, int[] colsWH)
    {
        /*Пусть n - количество элементов массива, m - на сколько частей надо поделить.
        Тогда всего в m-n%m массивах будет по n/m  элементов, а в n%m массивах - по n/m+1 элементов. Ровно и без мороки.
        пример: n=105, m=10: в 105/10-105%10=5 по 105/10=10 элементов, в 105%10=5 по 105/10+1=11 элементов.*/

        int iW1parts = colsWH[0]- inputWH[0]%colsWH[0]; // число массивов по iW1size элементов
        int iW1size = inputWH[0]/colsWH[0];
        int iH1parts = colsWH[1]- inputWH[1]%colsWH[1]; // число массивов по iH1size элементов
        int iH1size = inputWH[1]/colsWH[1];

        int iW2parts = inputWH[0]%colsWH[0]; // число массивов по iW2size элементов
        int iW2size = inputWH[0]/colsWH[0];
        int iH2parts = inputWH[1]%colsWH[1]; // число массивов по iH2size элементов
        int iH2size = inputWH[1]/colsWH[1];



        ArrayList<int[]> cols_map_input= new ArrayList<int[]>();

        for(int i=0;i<iW1parts;i++) {
            for (int j = 0; j < iH1parts; j++) {
                int[] rect=new int[4];
                rect[0]=iW1size*i;
                rect[1]=iH1size*j;
                rect[2]=rect[0]+iW1size-1;
                rect[3]=rect[1]+iH1size-1;
                rect[0]= rect[0] >= inputWH[0] ? inputWH[0]-1 : rect[0];
                rect[1]= rect[1] >= inputWH[1] ? inputWH[1]-1 : rect[1];
                rect[2]= rect[2] >= inputWH[0] ? inputWH[0]-1 : rect[2];
                rect[3]= rect[3] >= inputWH[1] ? inputWH[1]-1 : rect[3];
                cols_map_input.add(rect);
            }
        }

        int deltaX=cols_map_input.get(cols_map_input.size()-1)[1]+1;
        int deltaY=cols_map_input.get(cols_map_input.size()-1)[3]+1;

        for(int i=0;i<iW2parts;i++) {
            for (int j = 0; j < iH2parts; j++) {
                int[] rect=new int[4];
                rect[0]=deltaX+iW2size*i;
                rect[1]=deltaY+iH2size*j;
                rect[2]=rect[0]+iW2size-1;
                rect[3]=rect[1]+iH2size-1;

                rect[0]= rect[0] >= inputWH[0] ? inputWH[0]-1 : rect[0];
                rect[1]= rect[1] >= inputWH[1] ? inputWH[1]-1 : rect[1];
                rect[2]= rect[2] >= inputWH[0] ? inputWH[0]-1 : rect[2];
                rect[3]= rect[3] >= inputWH[1] ? inputWH[1]-1 : rect[3];

                cols_map_input.add(rect);
            }
        }

        return cols_map_input;
    }
}
