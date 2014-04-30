package ru.isa.ai.causal.classifiers;

import org.apache.commons.lang.SystemUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.*;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Author: Aleksandr Panov
 * Date: 30.07.13
 * Time: 11:18
 */
public class AQ21ExternalClassifier extends AbstractClassifier {

    private Map<String, List<AQRule>> rules = new HashMap<>();
    private Map<String, Integer> classMap = new HashMap<>();

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();

        // attributes
        result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);

        // class
        result.enable(Capabilities.Capability.NOMINAL_CLASS);

        return result;
    }

    @Override
    public void buildClassifier(Instances testData) throws Exception {
        // can classifier handle the data?
        getCapabilities().testWithFail(testData);

        // remove instances with missing class
        testData = new Instances(testData);
        testData.deleteWithMissingClass();

        String dataPath = createDataFile(testData);
        String[] cmd = {getClass().getClassLoader().getResource("ru/isa/ai/causal/classifiers/" +
                (SystemUtils.IS_OS_WINDOWS ? "aq21.exe" : "aq21")).getPath(),
                dataPath};
        Process process = Runtime.getRuntime().exec(cmd);
        //process.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder resultBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            resultBuilder.append(line);
            resultBuilder.append("\n");
        }
        parseResult(resultBuilder.toString(), testData);
    }

    private String createDataFile(Instances testData) throws IOException, URISyntaxException {
        StringBuilder builder = new StringBuilder();
        // Описание задачи
        builder.append("Problem_description\n");
        builder.append("{\n");
        builder.append("	Building rules for classes\n");
        builder.append("}\n");

        // Список признаков и их шкал
        builder.append("Attributes\n");
        builder.append("{\n");
        // Первый признак - разделитель классов
        builder.append("	class nominal {");
        Enumeration classEnu = testData.classAttribute().enumerateValues();
        int i = 0;
        StringBuilder builderRuns = new StringBuilder();
        while (classEnu.hasMoreElements()) {
            Object val = classEnu.nextElement();
            classMap.put(val.toString(), i);
            builder.append(val.toString());
            if (i < testData.classAttribute().numValues() - 1) {
                builder.append(", ");
            }
            builderRuns.append("	rules_for_");
            builderRuns.append(val.toString());
            builderRuns.append("\n");
            builderRuns.append("	{\n");

            builderRuns.append("		Consequent = [class=");
            builderRuns.append(val.toString());
            builderRuns.append("]\n");
            builderRuns.append("		Mode = TF\n");
            builderRuns.append("		Ambiguity = IgnoreForLearning\n");
            builderRuns.append("		Display_selectors_coverage = false\n");
            builderRuns.append("		Display_events_covered = true\n");

            builderRuns.append("	}\n");
            i++;
        }

        builder.append("}\n");
        // Далее признаки из входных данных
        Enumeration attrEnu = testData.enumerateAttributes();
        while (attrEnu.hasMoreElements()) {
            Attribute attribute = (Attribute) attrEnu.nextElement();
            switch (attribute.type()) {
                case Attribute.NOMINAL:
                    builder.append("	");
                    builder.append(attribute.name());
                    builder.append(" nominal {");
                    Enumeration attrValEnu = attribute.enumerateValues();
                    int j = 0;
                    while (attrValEnu.hasMoreElements()) {
                        builder.append(attrValEnu.nextElement().toString());
                        if (j < attribute.numValues() - 1) {
                            builder.append(", ");
                        }
                        j++;
                    }
                    builder.append("}\n");
                    break;
                case Attribute.NUMERIC:
                    builder.append("	");
                    builder.append(attribute.name());
                    builder.append(" continuous ChiMerge 3\n");
                    break;
            }
        }
        builder.append("}\n");

        // Условия запуска aq21
        builder.append("Runs\n");
        builder.append("{\n");
        builder.append(builderRuns);
        builder.append("}\n");

        // Входные объекты для aq21
        builder.append("Events\n");
        builder.append("{\n");
        Enumeration instEnu = testData.enumerateInstances();
        while (instEnu.hasMoreElements()) {
            Instance instance = (Instance) instEnu.nextElement();
            builder.append("	");
            builder.append(testData.classAttribute().value((int) instance.classValue()));
            builder.append(",");
            for (int j = 0; j < instance.numValues(); j++) {
                if (j != instance.classIndex()) {
                    builder.append(instance.value(j));
                    if (j < instance.numValues() - 2) {
                        builder.append(",");
                    }
                }
            }
            builder.append("\n");
        }

        builder.append("}\n");

        String path = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "external_input.aq21").getPath();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path));
        writer.write(builder.toString());
        writer.flush();
        writer.close();

        return path;
    }

    private void parseResult(String result, Instances testData) {
        if (getDebug())
            System.out.println(result);
        Map<String, CRFeature> attributeMap = new HashMap<>();

        Enumeration attrEnu = testData.enumerateAttributes();
        while (attrEnu.hasMoreElements()) {
            Attribute attr = (Attribute) attrEnu.nextElement();
            CRFeature aqAttr = new CRFeature(attr.name(), attr.index());
            int discrPos = result.indexOf(attr.name() + "_Discretized");
            if (discrPos != -1) {
                String line = result.substring(discrPos, result.indexOf("\n", discrPos));

                Scanner scanner = getScanner(line.substring(line.indexOf("[") + 1, line.indexOf("]")), Pattern.compile("\\s|,\\s"));
                while (scanner.hasNextFloat()) {
                    aqAttr.getCutPoints().add(scanner.nextDouble());
                }
            }
            attributeMap.put(attr.name(), aqAttr);
        }

        String rule_start_indicator = "<--";
        String info_start_indicator = " : p=";
        String info_complex_start_indicator = ",cx=";
        String info_end_indicator = ",";
        String rule_end_indicator = "#";
        String part_start_indicator = "[";
        String part_end_indicator = "]";
        String rule_number_indicator = "Number of rules in the cover";
        String covered_info_start = "covered_positives";
        String examples_start_indicator = "{";
        String examples_end_endicator = "}";

        // Находим правила для каждого класса
        Enumeration classEnu = testData.classAttribute().enumerateValues();
        while (classEnu.hasMoreElements()) {
            List<AQRule> classRules = new ArrayList<>();
            String className = classEnu.nextElement().toString();
            int classPos = result.indexOf("Output_Hypotheses rules_for_" + className);
            if (classPos == -1) {
                continue;
            }
            // Находим количество правил в классе
            int startLine = result.indexOf(rule_number_indicator, classPos);
            int endLine = result.indexOf("\n", startLine);
            int count = getScanner(result.substring(result.indexOf("=", startLine) + 1, endLine), Pattern.compile("\\s")).nextInt();

            // Находим каждое правило, начинающееся с <-- и заканчивающееся # id\n
            int startRule = classPos;
            int endRule;
            for (int i = 0; i < count; i++) {
                AQRule rule = new AQRule();
                startRule = result.indexOf(rule_start_indicator, startRule + 1);
                endRule = result.indexOf(rule_end_indicator, startRule);

                // Находим каждую часть правила, начинающуюся с [ и заканчивающуюся ]
                int startPart = startRule;
                int endPart;
                boolean endOfRule = false;
                while (!endOfRule) {
                    startPart = result.indexOf(part_start_indicator, startPart + 1);
                    endPart = result.indexOf(part_end_indicator, startPart);
                    if (startPart != -1 && startPart < endRule && endPart != -1 && endPart < endRule) {
                        // Анализируем значение интервалов
                        String partString = result.substring(startPart + 1, endPart);
                        String attrName = "";
                        List<Integer> values = new ArrayList<>();
                        float top = Float.MAX_VALUE;
                        float bottom = Float.MAX_VALUE;
                        if (partString.contains(">=")) {
                            Scanner scanner = getScanner(partString, Pattern.compile("=|(\\.\\.)"));
                            attrName = scanner.next();
                            bottom = scanner.nextFloat();
                            top = Float.MIN_VALUE;
                        } else if (partString.contains("<=")) {
                            Scanner scanner = getScanner(partString, Pattern.compile("<="));
                            attrName = scanner.next();
                            top = scanner.nextFloat();
                            bottom = Float.MIN_VALUE;
                        } else if (partString.contains("=") && partString.contains("..")) {
                            Scanner scanner = getScanner(partString, Pattern.compile("=|\\.{2}"));
                            attrName = scanner.next();
                            bottom = scanner.nextFloat();
                            top = scanner.nextFloat();
                        } else if (partString.contains("=")) {
                            int startValue = partString.indexOf("=");
                            Scanner scanner = getScanner(partString.substring(0, startValue), Pattern.compile("=|,"));
                            attrName = scanner.next();
                            while (scanner.hasNextInt()) {
                                values.add(scanner.nextInt());
                            }
                        }

                        CRFeature attribute = attributeMap.get(attrName);
                        if (top != Float.MIN_VALUE && bottom != Float.MIN_VALUE) {
                            for (int j = 0; j < attribute.getCutPoints().size() - 1; j++) {
                                if (attribute.getCutPoints().get(j) == bottom) {
                                    for (int k = j + 1; k < attribute.getCutPoints().size(); k++) {
                                        if (top <= attribute.getCutPoints().get(k)) {
                                            for (int l = 0; l < k - j; l++) {
                                                values.add(j + l + 1);
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }

                        } else if (top != Float.MIN_VALUE) {
                            values.add(1);
                            values.add(2);
                        } else if (bottom != Float.MIN_VALUE) {
                            values.add(2);
                            values.add(3);
                        }
                        rule.getTokens().put(attribute, values);
                    } else {
                        endOfRule = true;
                    }
                }
                // считываем количество покрытых положительных примеров
                int startInfo = result.indexOf(info_start_indicator, startRule);
                int end_info = result.indexOf(info_end_indicator, startInfo);
                int coverage = -1;
                if (startInfo != -1 && end_info != -1) {
                    coverage = getScanner(result.substring(startInfo + info_start_indicator.length(), end_info), Pattern.compile("\\s")).nextInt();
                }

                // считываем значение сложности примера
                int startComplexInfo = result.indexOf(info_complex_start_indicator, startRule);
                int end_complex_info = result.indexOf(info_end_indicator, startComplexInfo + 1);
                if (startComplexInfo != -1 && end_complex_info != -1) {
                    int complexity = getScanner(result.substring(startComplexInfo + info_complex_start_indicator.length(), end_complex_info), Pattern.compile("\\s")).nextInt();
                    rule.setComplexity(complexity);
                } else {
                    rule.setComplexity(0);
                }

                // считываем id правила
                int startExamplePart = result.indexOf(covered_info_start, startRule);
                String idPart = result.substring(endRule + 1, startExamplePart);
                int id = getScanner(idPart, Pattern.compile("\\s")).nextInt();
                rule.setId(id);

                // считываем номера покрытых примеров
                int startExmaplePart = result.indexOf(examples_start_indicator, startRule);
                int end_example_number_part = result.indexOf(examples_end_endicator, startExmaplePart + 1);
                if (startExmaplePart != -1 && end_example_number_part != -1) {
                    int next_value = startExmaplePart + 2;
                    for (int j = 0; j < coverage; j++) {
                        int end_line = result.indexOf("\n", next_value);
                        int last_part = result.lastIndexOf(",", end_line);
                        int event_number = getScanner(result.substring(last_part + 1, end_line), Pattern.compile("\\s")).nextInt();
                        rule.addCoveredInstance(testData.get(event_number));
                        next_value = end_line + 1;
                    }
                }

                classRules.add(rule);
            }
            rules.put(className, classRules);
        }
    }

    private Scanner getScanner(String s, Pattern delimiter) {
        Scanner scanner = new Scanner(s);
        scanner.useDelimiter(delimiter);
        scanner.useLocale(Locale.US);

        return scanner;
    }

    public Map<String, List<AQRule>> getRules() {
        return rules;
    }

    @Override
    public double classifyInstance(Instance instance) throws Exception {
        for (Map.Entry<String, List<AQRule>> clazz : rules.entrySet()) {
            boolean contained = false;
            for (AQRule rule : clazz.getValue()) {
                if (rule.ifCover(instance)) {
                    contained = true;
                    break;
                }
            }
            if (contained) return classMap.get(clazz.getKey());
        }
        return Utils.missingValue();
    }

    public static void main(String[] argv) {
        runClassifier(new AQ21ExternalClassifier(),
                new String[]{"-t",
                        AQ21ExternalClassifier.class.getClassLoader().getResource("ru/isa/ai/causal/classifiers/diabetes.arff").getPath()}
        );
    }
}
