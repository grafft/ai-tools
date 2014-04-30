package ru.isa.ai.causal.classifiers;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 30.04.2014
 * Time: 10:01
 */
public class ClassDescriber {
    public static Map<String, Set<CRProperty>> describeClasses(Map<String, List<AQRule>> rules, Instances data) {
        Map<String, Set<CRProperty>> factBase = new HashMap<>();
        int equalsPropsCount = 0;
        int collinearPropsCount = 0;
        int conflictedPropsCount = 0;
        for (Map.Entry<String, List<AQRule>> entry : rules.entrySet()) {
            Set<CRProperty> classProperties = new HashSet<>();
            for (AQRule rule : entry.getValue()) {
                // добавялем каждое свойство из правила с проверкой
                for (Map.Entry<CRFeature, List<Integer>> ruleEntry : rule.getTokens().entrySet()) {
                    CRProperty prop = new CRProperty(ruleEntry.getKey(), ruleEntry.getValue());
                    boolean toAdd = true;
                    CRProperty oldToRemove = null;
                    // проверки: уникальность, коллинераность, конфликтность
                    for (CRProperty existedProp : classProperties) {
                        if (existedProp.equals(prop)) {
                            equalsPropsCount++;
                            toAdd = false;
                            break;
                        } else if (existedProp.getFeature().equals(prop.getFeature())) {
                            int collinearity = existedProp.collinearity(prop);
                            // Улыб=1V2 > Улыб=1 - оставляем Улыб=1V2
                            if (collinearity == 1) {
                                collinearPropsCount++;
                                oldToRemove = existedProp;
                                break;
                            } else if (collinearity == -1) {
                                collinearPropsCount++;
                                toAdd = false;
                                break;
                            } else {
                                // оставляем более популярное
                                conflictedPropsCount++;
                                if (popularity(prop, data, entry.getKey()) > popularity(existedProp, data, entry.getKey())) {
                                    oldToRemove = existedProp;
                                    break;
                                } else {
                                    toAdd = false;
                                    break;
                                }
                            }
                        }
                    }

                    if (toAdd)
                        classProperties.add(prop);
                    if (oldToRemove != null)
                        classProperties.remove(oldToRemove);
                }
            }
            factBase.put(entry.getKey(), classProperties);
        }
        return factBase;
    }

    private static int popularity(CRProperty prop, Instances data, String plusClassName) {
        int popularity = 0;
        for (Instance event : data) {
            Attribute attr = event.attribute(prop.getFeature().getId());
            String className = data.classAttribute().value((int) event.classValue());
            if (className.equals(plusClassName) && attr.isNumeric() && attr.numValues() == 1 &&
                    prop.cover(event.value(attr.index())))
                popularity++;
        }
        return popularity;
    }
}
