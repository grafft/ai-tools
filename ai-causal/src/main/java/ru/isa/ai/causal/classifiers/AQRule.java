package ru.isa.ai.causal.classifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 30.07.13
 * Time: 15:38
 */
public class AQRule {
    private Map<Integer, List<Integer>> tokens = new HashMap<>();
    private List<Integer> coveredExamples = new ArrayList<>();
    private int coverage;
    int complexity;

    public AQRule() {
    }

    public Map<Integer, List<Integer>> getTokens() {
        return tokens;
    }

    public void setTokens(Map<Integer, List<Integer>> tokens) {
        this.tokens = tokens;
    }

    public int getCoverage() {
        return coverage;
    }

    public void setCoverage(int coverage) {
        this.coverage = coverage;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public List<Integer> getCoveredExamples() {
        return coveredExamples;
    }

    public void setCoveredExamples(List<Integer> coveredExamples) {
        this.coveredExamples = coveredExamples;
    }
}
