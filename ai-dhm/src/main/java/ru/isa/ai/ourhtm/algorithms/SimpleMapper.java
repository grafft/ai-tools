package ru.isa.ai.ourhtm.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by APetrov on 23.06.2015.
 * Класс простого маппера. Он не знает сколько всего колонок и просто проецирует колонку на входную матрицу -
 * получает центр рецептивного поля с определенным радиусом
 */
public class SimpleMapper {

    // возвращает массив кортежей координат элементов входного поля inputWH, которые отнесены к колонке с координатами colCoord
    // inputWH - размеры входного поля
    // colCoord - координаты колонки в поле колонок (подразумевается, что размерность входного поля и поля колонок одинакова)
    // radius - половина стороны квадрата с центром в колонке
    static public ArrayList<Integer[]> mapOne(int[] inputWH, int[] colCoord, int radius)
    {
        ArrayList<Integer[]> indices = new ArrayList<>();
        for (int i = colCoord[0] - radius; i <= colCoord[0] + radius; i++) {
            if (i >= 0 && i < inputWH[0]) {
                for (int j = colCoord[1] - radius; j <= colCoord[1] + radius; j++) {
                    if (j >= 0 && j < inputWH[1])
                        indices.add(new Integer[]{i,j});
                }
            }
        }
        return indices;
    }

    // возвращает список, в котором для каждой колонки указаны индексы связанных с ней элементов нижлежащего слоя
    static public ArrayList<ArrayList<Integer[]>> mapAll(int[] inputWH, int[] colsWH, int radius) throws Exception
    {
        if((inputWH[0] < colsWH[0])||(inputWH[1] < colsWH[1]))
            throw new Exception("Колонок как минимум по одному из измерений больше, чем элементов нижлежащего слоя по соответствующему измерению.");

        ArrayList<ArrayList<Integer[]>> cols_map_input= new ArrayList<ArrayList<Integer[]>>();

        for (int i = 0; i < colsWH[0]; i++) {
            for (int j = 0; j < colsWH[1]; j++) {
                int inputCenterX = i + (int) Math.floor((inputWH[0] - colsWH[0]) / 2);
                int inputCenterY = j + (int) Math.floor((inputWH[1] - colsWH[1]) / 2);
                ArrayList<Integer[]> indices = SimpleMapper.mapOne(inputWH, new int[]{inputCenterX, inputCenterY}, radius);
                cols_map_input.add(indices);
            }
        }
        return cols_map_input;
    }
}
