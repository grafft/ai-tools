package ru.isa.ai.causal.classifiers;

import ru.isa.ai.causal.jsm.JSMAnalyzer;
import ru.isa.ai.causal.jsm.JSMHypothesis;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: Aleksandr Panov
 * Date: 29.04.2014
 * Time: 16:28
 */
public class TestExternalAQ {
    public static void main(String[] args) throws Exception {
        AQ21ExternalClassifier cl = new AQ21ExternalClassifier();
        cl.setDebug(true);

        ConverterUtils.DataSource trainSource = new ConverterUtils.DataSource(AQ21ExternalClassifier.class.getClassLoader().getResource("ru/isa/ai/causal/classifiers/data1.arff").getPath());

        Instances train = trainSource.getStructure();
        int actualClassIndex = train.numAttributes() - 1;
        Instances tmpInst = trainSource.getDataSet(actualClassIndex);
        cl.buildClassifier(tmpInst);

        Map<String, List<AQRule>> rules = cl.getRules();
        Map<String, List<CRProperty>> classDescription = ClassDescriber.describeClasses(rules, tmpInst);

        JSMAnalyzer analyzer = new JSMAnalyzer(classDescription, tmpInst);
        Attribute classAttr = tmpInst.classAttribute();
        Enumeration<String> enumerator = classAttr.enumerateValues();
        while (enumerator.hasMoreElements()) {
            String className = enumerator.nextElement();
            System.out.println("Causes for class " + className + ": ");
            List<JSMHypothesis> hypothesises = analyzer.evaluateCauses(className);
            for (JSMHypothesis hypothesis : hypothesises) {
                System.out.println("\t" + hypothesis.toString());
            }
        }
    }
}
