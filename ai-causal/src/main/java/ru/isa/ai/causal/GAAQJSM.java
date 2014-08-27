package ru.isa.ai.causal;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isa.ai.causal.classifiers.AQClassDescription;
import ru.isa.ai.causal.classifiers.AQClassifierException;
import ru.isa.ai.causal.classifiers.ga.GAAQClassifier;
import ru.isa.ai.causal.jsm.AbstractJSMAnalyzer;
import ru.isa.ai.causal.jsm.JSMHypothesis;
import ru.isa.ai.causal.jsm.NorrisJSMAnalyzer;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 16.06.2014
 * Time: 10:52
 */
public class GAAQJSM {
    private static final Logger logger = LogManager.getLogger(GAAQJSM.class.getSimpleName());
    private static int classIndex;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "produce help message");
        options.addOption(OptionBuilder.withDescription("set file of data").hasArg().create("f"));
        options.addOption(OptionBuilder.withValueSeparator(',').hasArgs().withDescription("set id of classes for JSM analyze").create("c"));
        options.addOption("l", true, "set maximum length of causes");
        options.addOption("u", true, "set maximum size of universe of characters for JSM analyze");

        CommandLineParser parser = new BasicParser();

        try {
            HelpFormatter formatter = new HelpFormatter();

            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h") || !line.hasOption("f")) {
                formatter.printHelp("gaaqjsm", options);
            } else {
                final String dataFile = line.getOptionValue("f");
                int maxHypothesisLength = Integer.parseInt(line.getOptionValue("l", "3"));
                int maxUniverseSize = Integer.parseInt(line.getOptionValue("u", "10"));
                List<String> classes = new ArrayList<>();
                if (line.hasOption("c"))
                    Collections.addAll(classes, line.getOptionValues("c"));

                Instances data;
                if (ConverterUtils.DataSource.isArff(dataFile)) {
                    ConverterUtils.DataSource trainSource = new ConverterUtils.DataSource(dataFile);
                    Instances train = trainSource.getStructure();
                    int actualClassIndex = train.numAttributes() - 1;
                    data = trainSource.getDataSet(actualClassIndex);
                } else if (dataFile.toLowerCase().endsWith("gqj")) {
                    CSVLoader loader = new CSVLoader() {
                        @Override
                        public void setSource(File file){
                            setRetrieval(NONE);
                            m_structure = null;
                            m_sourceFile = null;
                            m_File = null;

                            try {
                                m_sourceReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "cp1251"));
                                classIndex = Integer.parseInt(m_sourceReader.readLine());
                                String nominalAttrs = m_sourceReader.readLine();
                                m_NominalAttributes.setRanges(nominalAttrs);
                                String nominalLabels = m_sourceReader.readLine();
                                setNominalLabelSpecs(nominalLabels.split(";"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void reset() throws IOException {
                            m_structure = null;
                            m_rowBuffer = null;
                            if (m_dataDumper != null) {
                                // close the uneeded temp files (if necessary)
                                m_dataDumper.close();
                                m_dataDumper = null;
                            }
                        }
                    };
                    loader.setSource(new File(dataFile));
                    loader.setFieldSeparator("\t");
                    ConverterUtils.DataSource trainSource = new ConverterUtils.DataSource(loader);
                    data = trainSource.getDataSet(classIndex);
                } else {
                    throw new AQClassifierException("Not supported file extension: " + dataFile);
                }

                GAAQClassifier classifier = new GAAQClassifier(classes);
                classifier.setMaximumDescriptionSize(maxUniverseSize);
                classifier.buildClassifier(data);
                Collection<AQClassDescription> classDescriptions = classifier.getDescriptions();
                for (AQClassDescription description : classDescriptions) {
                    if (classes.isEmpty() || classes.contains(description.getClassName())) {
                        AbstractJSMAnalyzer analyzer = new NorrisJSMAnalyzer(description, data);
                        analyzer.setMaxHypothesisLength(maxHypothesisLength);
                        List<JSMHypothesis> hypothesises = analyzer.evaluateCauses();
                    }
                }
            }
        } catch (ParseException e) {
            logger.error("Error during parse command line", e);
        } catch (Exception e) {
            logger.error("Weka exception: " + e.getMessage(), e);
        }
    }
}
