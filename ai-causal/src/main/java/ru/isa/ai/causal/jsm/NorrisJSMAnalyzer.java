package ru.isa.ai.causal.jsm;

import ru.isa.ai.causal.classifiers.AQClassDescription;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 19.08.2014
 * Time: 16:47
 */
public class NorrisJSMAnalyzer extends AbstractJSMAnalyzer {

    public NorrisJSMAnalyzer(AQClassDescription classDescription, Instances data) {
        super(classDescription, data);
    }

    @Override
    public List<Intersection> reasons(FactBase factBase, int deep) {
        List<Intersection> plusInter = searchIntersection(factBase.plusExamples);
        List<Intersection> toRemove = new ArrayList<>();
        for (Intersection inter : plusInter) {
            if (inter.generators.size() < minGeneratrixSize)
                toRemove.add(inter);
        }
        plusInter.removeAll(toRemove);
        toRemove.clear();
        List<Intersection> minusInter = searchIntersection(factBase.minusExamples);
        for (Intersection inter : minusInter) {
            if (inter.generators.size() < minGeneratrixSize)
                toRemove.add(inter);
        }
        minusInter.removeAll(toRemove);
        toRemove.clear();
        for (Intersection interP : plusInter) {
            for (Intersection interM : minusInter) {
                if (interP.equals(interM) || BooleanArrayUtils.include(interM.value, interP.value)
                        || BooleanArrayUtils.include(interP.value, interM.value))
                    toRemove.add(interP);
            }
        }
        plusInter.removeAll(toRemove);
        return null;
    }

    public List<Intersection> searchIntersection(Map<Integer, BitSet> objectMap) {
        List<Intersection> intersections = new ArrayList<>();
        for (Map.Entry<Integer, BitSet> entry : objectMap.entrySet()) {
            if (intersections.size() == 0) {
                intersections.add(new Intersection(entry.getValue(), entry.getKey()));
            } else {
                boolean flag = false;
                for (Intersection inter : intersections) {
                    if (inter.value.equals(entry.getValue())) {
                        inter.generators.add(entry.getKey());
                        flag = true;
                    } else if (BooleanArrayUtils.include(entry.getValue(), inter.value)) {
                        inter.generators.add(entry.getKey());
                    } else if (BooleanArrayUtils.include(inter.value, entry.getValue())) {
                        inter.generators.add(entry.getKey());
                        flag = true;
                    } else {
                        BitSet result = BooleanArrayUtils.and(inter.value, entry.getValue());
                        boolean toAdd = true;
                        for (Integer parent : inter.generators) {
                            if (!BooleanArrayUtils.include(objectMap.get(parent), result)) {
                                toAdd = false;
                                break;
                            }
                        }
                        if (BooleanArrayUtils.cardinality(result) > 0 && toAdd) {
                            Intersection newInter = new Intersection(result, inter.generators);
                            newInter.generators.add(entry.getKey());
                        }
                    }
                }
                if (flag)
                    intersections.add(new Intersection(entry.getValue(), entry.getKey()));
            }
        }
        return intersections;
    }
}
