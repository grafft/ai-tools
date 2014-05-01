package ru.isa.ai.causal.jsm;

import ru.isa.ai.causal.classifiers.CRProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Author: Aleksandr Panov
 * Date: 30.04.2014
 * Time: 15:00
 */
public class JSMHypothesis {
    private CRProperty keyProperty;
    private Map<Integer, Set<CRProperty>> value = new HashMap<>();

    public JSMHypothesis(CRProperty keyProperty) {
        this.keyProperty = keyProperty;
    }

    public void addValue(Set<CRProperty> val) {
        value.put(value.size(), val);
    }

    public CRProperty getKeyProperty() {
        return keyProperty;
    }

    public Map<Integer, Set<CRProperty>> getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Causes for ").append(keyProperty).append(":\n");

        int totalCounter = 0;
        for (Map.Entry<Integer, Set<CRProperty>> entry : value.entrySet()) {
            builder.append("\t").append(entry.getKey()).append(": ");
            int counter = 0;
            for (CRProperty prop : entry.getValue()) {
                builder.append("(").append(prop).append(")");
                if (counter < entry.getValue().size() - 1)
                    builder.append(" & ");
                counter++;
            }
            if (totalCounter < value.size() - 1)
                builder.append("\n");
            totalCounter++;
        }

        return builder.toString();
    }
}
