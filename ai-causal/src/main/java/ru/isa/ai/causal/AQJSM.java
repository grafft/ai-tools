package ru.isa.ai.causal;

import org.apache.commons.cli.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isa.ai.causal.classifiers.*;
import ru.isa.ai.causal.jsm.JSMAnalyzer;
import ru.isa.ai.causal.jsm.JSMHypothesis;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Reorder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 30.04.2014
 * Time: 17:52
 */
public class AQJSM {
    private enum EvaluateMode {
        aq_simple, aq_best, aq_accum, aq_simple_jsm, aq_best_jsm, aq_accum_jsm, jsm
    }

    private static final Logger logger = LogManager.getLogger(AQJSM.class.getSimpleName());
    private static final String CD_FILE_NAME = "class_descriptions.xml";

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "produce help message");
        options.addOption(OptionBuilder.withDescription("set file of data").hasArg().create("f"));
        options.addOption("d", true, "set discretization type: 0 - without discretization, 1 - uniform discretization, 2 - chi-merge discretization");
        options.addOption("u", true, "set maximum size of universe of characters for JSM analyze");
        options.addOption("l", true, "set maximum length of causes");
        options.addOption("i", true, "set number of iterates (aq_accum* mode)");
        options.addOption("t", true, "set threshold for class properties (aq_accum* mode)");
        options.addOption("m", true, "mode: aq_simple - only simple AQ covering,\n" +
                "aq_best - only AQ recursive covering up to max size of property base (-u argument),\n" +
                "aq_accum - cumulative AQ covering,\n" +
                "aq_simple_jsm - simple AQ covering + JSM analyzing,\n" +
                "aq_best_jsm - best AQ covering + JSM analyzing, aq_accum_jsm - cumulative AQ covering + JSM analyzing,\n" +
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
                int maxHypothesisLength = Integer.parseInt(line.getOptionValue("l", "3"));
                int iterationCount = Integer.parseInt(line.getOptionValue("i", "100"));
                double threshold = Double.parseDouble(line.getOptionValue("t", "0.25"));
                List<String> classes = new ArrayList<>();
                if (line.hasOption("c"))
                    Collections.addAll(classes, line.getOptionValues("c"));

                ConverterUtils.DataSource trainSource = new ConverterUtils.DataSource(dataFile);
                Instances train = trainSource.getStructure();
                int actualClassIndex = train.numAttributes() - 1;
                Instances data = trainSource.getDataSet(actualClassIndex);

                JAXBContext context = JAXBContext.newInstance(new Class<?>[]{ClassDescriptionList.class,
                        AQClassDescription.class, CRProperty.class, CRFeature.class});
                Collection<AQClassDescription> classDescriptions = new ArrayList<>();
                AQ21ExternalClassifier classifier = new AQ21ExternalClassifier();
                switch (mode) {
                    case aq_simple_jsm:
                    case aq_simple:
                        logger.info("Start build class descriptions by simple aq covering");
                        classifier.buildClassifier(data);
                        classDescriptions = classifier.getDescriptions();
                        break;
                    case aq_best:
                    case aq_best_jsm:
                        logger.info("Start build class descriptions by best aq covering");
                        classifier.setTryToMinimize(true);
                        classifier.setMaximumDescriptionSize(maxUniverseSize);
                        classifier.buildClassifier(data);
                        classDescriptions = classifier.getDescriptions();
                        break;
                    case aq_accum:
                    case aq_accum_jsm:
                        logger.info("Start build class descriptions by cumulative aq covering");
                        classifier.setCumulative(true);
                        classifier.setMaximumDescriptionSize(maxUniverseSize);
                        classifier.setCumulativeThreshold(threshold);
                        classifier.setNumIterations(iterationCount);
                        classifier.buildClassifier(data);
                        classDescriptions = classifier.getDescriptions();
                        break;
                    case jsm:
                        logger.info("Start load class descriptions from " + CD_FILE_NAME);
                        String path = new File(AQJSM.class.getProtectionDomain().getCodeSource().getLocation().getPath()
                                + CD_FILE_NAME).getPath();
                        Unmarshaller unmarshaller = context.createUnmarshaller();
                        InputStream stream = new FileInputStream(path);
                        ClassDescriptionList list = (ClassDescriptionList) unmarshaller.unmarshal(stream);
                        stream.close();
                        classDescriptions.addAll(list.getClassDescriptions());
                        break;
                    default:
                        formatter.printHelp("aqjsm", options);
                        System.exit(-1);
                }

                if (mode != EvaluateMode.jsm) {
                    logger.info("Save class descriptions to class_descriptions.xml");
                    String path = new File(AQJSM.class.getProtectionDomain().getCodeSource().getLocation().getPath()
                            + "class_descriptions.xml").getPath();
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path));
                    Marshaller marshaller = context.createMarshaller();
                    marshaller.marshal(new ClassDescriptionList(classDescriptions), writer);
                    writer.flush();
                    writer.close();
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
                                logger.info("Start evaluating of causal relations for class " + description.getClassName());
                                logger.info(description.toString());
                                List<JSMHypothesis> hypothesises = analyzer.evaluateCauses();
                                int classIndex = data.classAttribute().indexOfValue(description.getClassName());
                                int objectCount = data.attributeStats(data.classIndex()).nominalCounts[classIndex];
                                StringBuilder builder = new StringBuilder();
                                builder.append("Causes for class ").append(description.getClassName()).append(" [desc_size=")
                                        .append(description.getDescription().size()).append(", object_num=").
                                        append(objectCount).append("]:\n");
                                for (int i = 0; i < hypothesises.size(); i++) {
                                    builder.append("\t").append(hypothesises.get(i).toString());
                                    if (i < hypothesises.size() - 1)
                                        builder.append("\n");
                                }
                                logger.info(builder.toString());
                            }
                        }
                        break;
                }
            }
        } catch (ParseException e) {
            logger.error("Error during parse command line", e);
        } catch (JAXBException e) {
            logger.error("Cannot parse or write description of classes", e);
        } catch (FileNotFoundException e) {
            logger.error("Cannot find file with classes' description: " + CD_FILE_NAME, e);
        } catch (IOException e) {
            logger.error("Cannot read from or write to file with classes' description: " + CD_FILE_NAME, e);
        } catch (Exception e) {
            logger.error("Weka exception: " + e.getMessage(), e);
        }

    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class ClassDescriptionList {
        @XmlElementWrapper
        @XmlElement
        private List<AQClassDescription> classDescriptions = new ArrayList<>();

        ClassDescriptionList() {
        }

        ClassDescriptionList(List<AQClassDescription> classDescriptions) {
            this.classDescriptions = classDescriptions;
        }

        ClassDescriptionList(Collection<AQClassDescription> classDescriptions) {
            this.classDescriptions.addAll(classDescriptions);
        }

        List<AQClassDescription> getClassDescriptions() {
            return classDescriptions;
        }

        void setClassDescriptions(List<AQClassDescription> classDescriptions) {
            this.classDescriptions = classDescriptions;
        }
    }
}
