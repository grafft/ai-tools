package ru.isa.ai.ourhtm.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by APetrov on 23.06.2015.
 * Класс простого маппера. Он не знает сколько всего колонок и просто проецирует колонку на входную матрицу -
 * получает центр рецептивного поля с определенным радиусом
 */
public class SimpleMapper {

    // возвращает массив индексов входного поля inputWH, которые отнесены к колонке с координатами colCoord
    // inputWH - размеры входного поля
    // colCoord - координаты колонки в поле колонок (подразумевается, что размерность входного поля и поля колонок одинакова)
    // radius - половина стороны квадрата с центром в колонке
    static public ArrayList<Integer[]> map(int[] inputWH, int[] colCoord, int radius)
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
}
