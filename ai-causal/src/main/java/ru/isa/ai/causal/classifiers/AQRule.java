package ru.isa.ai.causal.classifiers;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 30.07.13
 * Time: 15:38
 */
public class AQRule {
    private Map<AQAttribute, List<Integer>> tokens = new HashMap<>();
    private int id;
    private int complexity;
    private Set<Instance> coveredInstances = new HashSet<>();

    public AQRule() {
    }

    public AQRule(AQRule rule) {
        tokens.putAll(rule.getTokens());
        complexity = rule.complexity;
        coveredInstances = new HashSet<>(rule.coveredInstances);
    }

    public Map<AQAttribute, List<Integer>> getTokens() {
        return tokens;
    }

    public void setTokens(Map<AQAttribute, List<Integer>> tokens) {
        this.tokens = tokens;
    }

    public int coverage() {
        return coveredInstances.size();
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Instance> getCoveredInstances() {
        return coveredInstances;
    }

    public void setCoveredInstances(Set<Instance> coveredInstances) {
        this.coveredInstances = coveredInstances;
    }

    public int size() {
        return tokens.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AQRule aqRule = (AQRule) o;

        for (Map.Entry<AQAttribute, List<Integer>> entry : tokens.entrySet()) {
            if (!aqRule.getTokens().containsKey(entry.getKey())) return false;
            if (!entry.getValue().equals(aqRule.getTokens().get(entry.getKey()))) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return tokens != null ? tokens.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(String.format("rule %d[cov=%d,cx=%d]: ", id, coverage(), complexity));
        int tokenCounter = 0;
        for (Map.Entry<AQAttribute, List<Integer>> entry : tokens.entrySet()) {
            builder.append(String.format("(%s=", entry.getKey().toString()));
            int count = 0;
            for (int part : entry.getValue()) {
                builder.append(part);
                if (count < entry.getValue().size() - 1) builder.append("V");
                count++;
            }
            builder.append(")");
            if (tokenCounter < tokens.size() - 1) builder.append("&");
            tokenCounter++;
        }
        return builder.toString();
    }

    public boolean ifCover(Instance object) {
        for (Map.Entry<AQAttribute, List<Integer>> entry : tokens.entrySet()) {
            double value = object.value(entry.getKey().getId());
            if (object.attribute(entry.getKey().getId()).isNumeric()) {
                boolean result = false;
                for (int part : entry.getValue()) {
                    if (entry.getKey().getCutPoints().get(part - 1) < value
                            && entry.getKey().getCutPoints().get(part) > value) {
                        result = true;
                        break;
                    }
                }
                if (!result) return false;
            } else if (object.attribute(entry.getKey().getId()).isNominal()) {
                if (!entry.getValue().contains((int) value)) return false;
            }

        }
        return true;
    }

    public int inflate(AQAttribute attrToInflate, Instances plusInstances, Instances minusInstances) {
        List<Integer> parts = tokens.get(attrToInflate);
        int inflationRate = 0;
        if (parts != null) {
            Attribute attribute = plusInstances.attribute(attrToInflate.getName());
            Enumeration valEnu = attribute.enumerateValues();
            while (valEnu.hasMoreElements()) {
                Integer value = attribute.indexOfValue((String) valEnu.nextElement());
                if (!parts.contains(value)) {
                    parts.add(value);
                    if (testCoverage(minusInstances) > 0) {
                        parts.remove(value);
                    } else {
                        updateCoverage(plusInstances);
                        plusInstances.removeAll(coveredInstances);
                        inflationRate++;
                    }
                }
            }
            if (parts.size() == attribute.numValues()) {
                tokens.remove(attrToInflate);
            } else {
                Collections.sort(parts);
            }
        }

        return inflationRate;
    }

    private void updateCoverage(Instances newInstances) {
        for (Instance instance : newInstances) {
            if (ifCover(instance)) coveredInstances.add(instance);
        }
    }

    private int testCoverage(Instances instances) {
        int coverage = 0;
        for (Instance instance : instances) {
            if (ifCover(instance)) coverage++;
        }
        return coverage;
    }

}
