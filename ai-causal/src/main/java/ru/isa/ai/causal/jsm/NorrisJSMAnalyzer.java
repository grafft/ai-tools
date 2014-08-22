package ru.isa.ai.causal.jsm;

import ru.isa.ai.causal.classifiers.AQClassDescription;
import weka.core.Instances;

import java.util.*;

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
    public List<JSMIntersection> reasons(JSMFactBase factBase, int deep) {
        List<JSMIntersection> plusInter = searchIntersection(factBase.plusExamples);
        List<JSMIntersection> toRemove = new ArrayList<>();
        for (JSMIntersection inter : plusInter) {
            if (inter.generators.size() < minGeneratrixSize)
                toRemove.add(inter);
        }
        plusInter.removeAll(toRemove);
        toRemove.clear();
        List<JSMIntersection> minusInter = searchIntersection(factBase.minusExamples);
        for (JSMIntersection inter : minusInter) {
            if (inter.generators.size() < minGeneratrixSize)
                toRemove.add(inter);
        }
        minusInter.removeAll(toRemove);
        toRemove.clear();
        for (JSMIntersection interP : plusInter) {
            for (JSMIntersection interM : minusInter) {
                if (interP.equals(interM) || BooleanArrayUtils.include(interM.value, interP.value)
                        || BooleanArrayUtils.include(interP.value, interM.value))
                    toRemove.add(interP);
            }
        }
        plusInter.removeAll(toRemove);
        return plusInter;
    }

    public List<JSMIntersection> searchIntersection(Map<Integer, BitSet> examples) {
        List<JSMIntersection> hypotheses = new ArrayList<>();
        Map<Integer, BitSet> viewedExamples = new HashMap<>();
        for (Map.Entry<Integer, BitSet> example : examples.entrySet()) {
            if (hypotheses.size() == 0) {
                hypotheses.add(new JSMIntersection(example.getValue(), example.getKey()));
            } else {
                List<JSMIntersection> toAdd = new ArrayList<>();
                for (JSMIntersection hyp : hypotheses) {
                    if (hyp.value.equals(example.getValue()) || BooleanArrayUtils.include(example.getValue(), hyp.value)) {
                        hyp.generators.add(example.getKey());
                    } else {
                        BitSet result = BooleanArrayUtils.and(hyp.value, example.getValue());
                        if (BooleanArrayUtils.cardinality(result) > 0) {
                            boolean addHyp = true;
                            for (Map.Entry<Integer, BitSet> ex : viewedExamples.entrySet()) {
                                if (BooleanArrayUtils.include(ex.getValue(), result) && !hyp.generators.contains(ex.getKey())) {
                                    addHyp = false;
                                    break;
                                }
                            }
                            if (addHyp) {
                                JSMIntersection newInter = new JSMIntersection(result, hyp.generators);
                                newInter.generators.add(example.getKey());
                                toAdd.add(newInter);
                            }
                        }
                    }
                }
                hypotheses.addAll(toAdd);
                boolean addExample = true;
                for (Map.Entry<Integer, BitSet> ex : viewedExamples.entrySet()) {
                    if(BooleanArrayUtils.include(ex.getValue(), example.getValue())){
                        addExample = false;
                        break;
                    }
                }
                if (addExample)
                    hypotheses.add(new JSMIntersection(example.getValue(), example.getKey()));
                viewedExamples.put(example.getKey(), example.getValue());
            }
        }
        return hypotheses;
    }
}
