package ru.isa.ai.olddhm.poolers;

import java.util.Comparator;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 16.05.2014
 * Time: 13:18
 */
public class ValueComparator implements Comparator<Integer> {
    private Map<Integer, Double> base;

    public ValueComparator(Map<Integer, Double> base) {
        this.base = base;
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        return base.get(o1).compareTo(base.get(o2));
    }
}
