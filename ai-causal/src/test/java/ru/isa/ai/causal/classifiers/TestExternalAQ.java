package ru.isa.ai.causal.classifiers;

import ru.isa.ai.causal.jsm.JSMAnalyzer;
import ru.isa.ai.causal.jsm.JSMHypothesis;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 29.04.2014
 * Time: 16:28
 */
public class TestExternalAQ {
    public static void main(String[] args) throws Exception {
        AQ21ExternalClassifier cl = new AQ21ExternalClassifier();

        ConverterUtils.DataSource trainSource = new ConverterUtils.DataSource(AQ21ExternalClassifier.class.getClassLoader().getResource("ru/isa/ai/causal/classifiers/data1.arff").getPath());

        Instances train = trainSource.getStructure();
        int actualClassIndex = train.numAttributes() - 1;
        Instances tmpInst = trainSource.getDataSet(actualClassIndex);
        cl.buildClassifier(tmpInst);

        Map<String, List<AQRule>> rules = cl.getClassRules();

        for (Map.Entry<String, List<AQRule>> entry : rules.entrySet()) {
            JSMAnalyzer analyzer = new JSMAnalyzer(AQClassDescription.createFromRules(entry.getValue(), 100, entry.getKey()), tmpInst);
            System.out.println("Causes for class " + entry.getKey() + ": ");
            List<JSMHypothesis> hypothesises = analyzer.evaluateCauses();
            for (JSMHypothesis hypothesis : hypothesises) {
                System.out.println("\t" + hypothesis.toString());
            }
        }
    }
}
