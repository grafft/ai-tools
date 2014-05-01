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
    public static Map<String, List<CRProperty>> describeClasses(Map<String, List<AQRule>> rules, Instances data) {
        Map<String, List<CRProperty>> factBase = new HashMap<>();
        int equalsPropsCount = 0;
        int collinearPropsCount = 0;
        int conflictedPropsCount = 0;
        for (Map.Entry<String, List<AQRule>> entry : rules.entrySet()) {
            List<CRProperty> classProperties = new ArrayList<>();
            for (AQRule rule : entry.getValue()) {
                // добавялем каждое свойство из правила с проверкой
                for (Map.Entry<CRFeature, List<Integer>> ruleEntry : rule.getTokens().entrySet()) {
                    CRProperty prop = new CRProperty(ruleEntry.getKey(), ruleEntry.getValue());
                    prop.setPopularity(rule.getCoveredInstances().size());
                    if (!classProperties.contains(prop))
                        classProperties.add(prop);
                }
            }
            classProperties = clearDescription(classProperties);
            factBase.put(entry.getKey(), classProperties);
        }
        return factBase;
    }

    public static List<CRProperty> clearDescription(List<CRProperty> toClear) {
        int equalsPropsCount = 0;
        int collinearPropsCount = 0;
        int conflictedPropsCount = 0;
        List<CRProperty> cleared = new ArrayList<>();

        // проверки: уникальность, коллинераность, конфликтность
        for (CRProperty propToCheck : toClear) {
            boolean toAdd = true;
            CRProperty oldToRemove = null;
            for (CRProperty existedProp : cleared) {
                if (existedProp.equals(propToCheck)) {
                    toAdd = false;
                    break;
                } else if (existedProp.getFeature().equals(propToCheck.getFeature())) {
                    int collinearity = existedProp.collinearity(propToCheck);
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
                        if (propToCheck.getPopularity() > existedProp.getPopularity()) {
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
                cleared.add(propToCheck);
            if (oldToRemove != null)
                cleared.remove(oldToRemove);
        }
        return cleared;
    }

}
