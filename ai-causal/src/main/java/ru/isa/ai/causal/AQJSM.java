package ru.isa.ai.causal;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import org.apache.commons.cli.*;
import org.apache.commons.lang.ArrayUtils;
import ru.isa.ai.causal.classifiers.AQ21ExternalClassifier;
import ru.isa.ai.causal.classifiers.AQClassDescription;
import ru.isa.ai.causal.classifiers.AQRule;
import ru.isa.ai.causal.classifiers.CRProperty;
import ru.isa.ai.causal.jsm.JSMAnalyzer;
import ru.isa.ai.causal.jsm.JSMHypothesis;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 30.04.2014
 * Time: 17:52
 */
public class AQJSM {
    private enum EvaluateMode {
        aq_simple, aq_best, aq_simple_jsm, aq_best_jsm, aq_accum_jsm, jsm
    }

    private static Map<String, List<AQRule>> ruleBase;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "produce help message");
        options.addOption(OptionBuilder.withDescription("set file of data").hasArg().create("f"));
        options.addOption("d", true, "set discretization type: 0 - without discretization, 1 - uniform discretization, 2 - chi-merge discretization");
        options.addOption("u", true, "set maximum size of universe of characters for JSM analyze");
        options.addOption("r", false, "set flag to reduce base of fact for JSM within conflicted properties");
        options.addOption("l", true, "set maximum length of causes");
        options.addOption("i", true, "set number of iterates in cumulative AQ covering mode(--full_accum)");
        options.addOption("m", true, "mode: aq_simple - only simple AQ covering, aq_best - only AQ recursive covering up to max size of property base (-u argument), " +
                "aq_simple_jsm - simple AQ covering + JSM analyzing, aq_best_jsm - best AQ covering + JSM analyzing, aq_accum_jsm - cumulative AQ covering + JSM analyzing, " +
                "jsm - only JSM analyzing");
        options.addOption(OptionBuilder.withValueSeparator(',').hasArgs().withDescription("set id of classes for JSM analyze").create("c"));

        CommandLineParser parser = new BasicParser();
        try {
            HelpFormatter formatter = new HelpFormatter();

            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h")) {
                formatter.printHelp("aqjsm", options);
            }

            if (!line.hasOption("f") || !line.hasOption("m")) {
                formatter.printHelp("aqjsm", options);
            } else {
                String dataFile = line.getOptionValue("f");
                EvaluateMode mode = EvaluateMode.valueOf(line.getOptionValue("m"));

                int discretization = Integer.parseInt(line.getOptionValue("d", "2"));
                int maxUniverseSize = Integer.parseInt(line.getOptionValue("u", "100"));
                boolean toReduce = line.hasOption("r");
                int maxHypothesisLength = Integer.parseInt(line.getOptionValue("l", "3"));
                int iterationCount = Integer.parseInt(line.getOptionValue("i", "100"));
                List<String> classes = new ArrayList<>();
                if (line.hasOption("c"))
                    Collections.addAll(classes, line.getOptionValues("c"));

                ConverterUtils.DataSource trainSource = new ConverterUtils.DataSource(dataFile);
                Instances train = trainSource.getStructure();
                int actualClassIndex = train.numAttributes() - 1;
                Instances data = trainSource.getDataSet(actualClassIndex);

                List<AQClassDescription> classDescriptions = new ArrayList<>();
                switch (mode) {
                    case aq_simple_jsm:
                    case aq_simple:
                        AQ21ExternalClassifier classifier = new AQ21ExternalClassifier();
                        classifier.buildClassifier(data);
                        ruleBase = classifier.getRules();
                        for (Map.Entry<String, List<AQRule>> classRules : ruleBase.entrySet())
                            classDescriptions.add(AQClassDescription.createFromRules(classRules.getValue(), classRules.getKey()));
                        break;
                    case aq_best:
                    case aq_best_jsm:
                        int maxSize = Integer.MAX_VALUE;
                        while (maxSize > maxUniverseSize) {
                            ruleBase = null;
                            classDescriptions = reorderedAQ(data);

                            maxSize = 0;
                            for (AQClassDescription description : classDescriptions)
                                if (maxSize < description.getDescription().size())
                                    maxSize = description.getDescription().size();
                        }
                        break;
                    case aq_accum_jsm:
                        Map<String, List<AQRule>> bestRules = null;
                        Map<String, Map<CRProperty, Integer>> stats = new HashMap<>();
                        int bestComplexity = Integer.MAX_VALUE;
                        for (int i = 0; i < iterationCount; i++) {
                            classDescriptions = reorderedAQ(data);
                            for (AQClassDescription description : classDescriptions) {
                                if (stats.get(description.getClassName()) == null)
                                    stats.put(description.getClassName(), new HashMap<CRProperty, Integer>());
                                for (CRProperty property : description.getDescription()) {
                                    Integer value = stats.get(description.getClassName()).get(property);
                                    if (value == null)
                                        stats.get(description.getClassName()).put(property, 1);
                                    else
                                        stats.get(description.getClassName()).put(property, value + 1);
                                }
                            }

                            int complexity = countComplexity(ruleBase);
                            if (complexity < bestComplexity) {
                                bestRules = ruleBase;
                                bestComplexity = complexity;
                            }
                        }
                        classDescriptions.clear();
                        for (Map.Entry<String, Map<CRProperty, Integer>> classEntry : stats.entrySet()) {
                            Multimap<Integer, CRProperty> sortedMap = TreeMultimap.create(new Comparator<Integer>() {
                                @Override
                                public int compare(Integer o1, Integer o2) {
                                    return -o1.compareTo(o2);
                                }
                            }, Ordering.<CRProperty>natural());
                            List<Integer> frequents = new ArrayList<>();
                            for (Map.Entry<CRProperty, Integer> entry : classEntry.getValue().entrySet()) {
                                sortedMap.put(entry.getValue(), entry.getKey());
                                frequents.add(entry.getValue());
                            }
                            Collections.sort(frequents, new Comparator<Integer>() {
                                @Override
                                public int compare(Integer o1, Integer o2) {
                                    return Integer.compare(o2, o1);
                                }
                            });
                            int minFrequency = frequents.get(0) / 2;
                            int countUniver = 0;
                            for (int freq : frequents)
                                if (freq > minFrequency)
                                    countUniver++;
                            if (countUniver > maxUniverseSize)
                                minFrequency = frequents.get(maxUniverseSize);
                            List<CRProperty> properties = new ArrayList<>();
                            for (Map.Entry<Integer, CRProperty> entry : sortedMap.entries()) {
                                if (entry.getKey() > minFrequency) {
                                    CRProperty prop = entry.getValue();
                                    prop.setPopularity(entry.getKey());
                                    properties.add(prop);
                                }
                            }
                            AQClassDescription classDescription = AQClassDescription.createFromProperties(properties, classEntry.getKey());
                            classDescriptions.add(classDescription);
                        }
                        ruleBase = bestRules;
                        break;
                    default:
                        formatter.printHelp("aqjsm", options);
                        System.exit(-1);
                }

                switch (mode) {
                    case aq_simple_jsm:
                    case aq_best_jsm:
                    case aq_accum_jsm:
                    case jsm:
                        for (AQClassDescription description : classDescriptions) {
                            JSMAnalyzer analyzer = new JSMAnalyzer(description, data);
                            analyzer.setMaxHypothesisLength(maxHypothesisLength);

                            if (classes.isEmpty() || classes.contains(description.getClassName())) {
                                System.out.println("Causes for class " + description.getClassName() + ": ");
                                List<JSMHypothesis> hypothesises = analyzer.evaluateCauses();
                                for (JSMHypothesis hypothesis : hypothesises) {
                                    System.out.println("\t" + hypothesis.toString());
                                }
                            }
                        }
                        break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static int countComplexity(Map<String, List<AQRule>> rules) {
        int totalComplexity = 0;
        for (List<AQRule> classRules : rules.values())
            for (AQRule rule : classRules)
                totalComplexity += rule.getComplexity();
        return totalComplexity;
    }

    private static List<AQClassDescription> reorderedAQ(Instances data) throws Exception {
        Reorder reorderFilter = new Reorder();
        List<Integer> listToShuffle = new ArrayList<>();
        List<Integer> listOfNominal = new ArrayList<>();
        for (int i = 0; i < data.numAttributes() - 1; i++) {
            if (data.attribute(i).isNominal())
                listOfNominal.add(i);
            else
                listToShuffle.add(i);
        }
        Collections.shuffle(listToShuffle);
        int[] nominalIndexes = ArrayUtils.toPrimitive(listOfNominal.toArray(new Integer[listOfNominal.size()]));
        int[] reorderedIndexes = ArrayUtils.toPrimitive(listToShuffle.toArray(new Integer[listToShuffle.size()]));
        int[] indexes = ArrayUtils.addAll(nominalIndexes, reorderedIndexes);
        reorderFilter.setAttributeIndicesArray(ArrayUtils.add(indexes, data.numAttributes() - 1));

        Instances toReorder = new Instances(data);
        reorderFilter.setInputFormat(toReorder);
        toReorder = Filter.useFilter(toReorder, reorderFilter);

        AQ21ExternalClassifier classifier = new AQ21ExternalClassifier();
        classifier.buildClassifier(toReorder);
        ruleBase = classifier.getRules();
        List<AQClassDescription> classDescriptions = new ArrayList<>();
        for (Map.Entry<String, List<AQRule>> classRules : ruleBase.entrySet())
            classDescriptions.add(AQClassDescription.createFromRules(classRules.getValue(), classRules.getKey()));

        return classDescriptions;
    }
}
