package ru.isa.ai.causal.jsm;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 21.08.2014
 * Time: 17:09
 */
public class JSMIntersection implements Comparable<JSMIntersection>, Cloneable {
    public BitSet value;
    public List<Integer> generators = new ArrayList<>();

    protected JSMIntersection(BitSet value, int objectId) {
        this.value = value;
        generators.add(objectId);
    }

    protected JSMIntersection(BitSet value, List<Integer> generators) {
        this.value = value;
        generators.addAll(generators);
    }

    public void intersect(Map<Integer, BitSet> objects) {
        for (Map.Entry<Integer, BitSet> entry : objects.entrySet()) {
            BitSet result = BooleanArrayUtils.and(value, entry.getValue());
            if (BooleanArrayUtils.cardinality(result) > 0) {
                value = result;
                generators.add(entry.getKey());
            }
        }
    }

    public void add(JSMIntersection toAdd) {
        generators.clear();
        generators.addAll(toAdd.generators);
        value = BooleanArrayUtils.or(value, toAdd.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JSMIntersection that = (JSMIntersection) o;

        return BooleanArrayUtils.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public int compareTo(JSMIntersection o) {
        if (o == null)
            return 1;
        return Integer.compare(this.generators.size(), o.generators.size());
    }

    @Override
    public JSMIntersection clone() {
        return new JSMIntersection((BitSet) this.value.clone(), this.generators);
    }
}