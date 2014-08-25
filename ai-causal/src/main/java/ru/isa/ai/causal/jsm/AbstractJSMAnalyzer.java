package ru.isa.ai.causal.jsm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isa.ai.causal.classifiers.AQClassDescription;
import ru.isa.ai.causal.classifiers.CRProperty;
import weka.core.Instances;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 19.08.2014
 * Time: 16:37
 */
public abstract class AbstractJSMAnalyzer {
    private static final Logger logger = LogManager.getLogger(AbstractJSMAnalyzer.class.getSimpleName());

    private AQClassDescription classDescription;
    private Instances data;
    protected int maxHypothesisLength = 3;
    protected int minGeneratrixSize = 2;

    public AbstractJSMAnalyzer(AQClassDescription classDescription, Instances data) {
        this.classDescription = classDescription;
        this.data = data;
    }

    public abstract List<JSMIntersection> reasons(JSMFactBase factBase, int deep);

    public List<JSMHypothesis> evaluateCauses() {
        int classIndex = data.classAttribute().indexOfValue(classDescription.getClassName());
        int objectCount = data.attributeStats(data.classIndex()).nominalCounts[classIndex];

        logger.info("Start evaluating of causal relations for class " + classDescription.getClassName() + " [desc_size=" +
                classDescription.getDescription().size() + ", object_num=" + objectCount + "]");
        logger.info(classDescription.toString());

        List<JSMHypothesis> causes = new ArrayList<>();
        for (CRProperty property : classDescription.getDescription()) {
            List<CRProperty> otherProps = new ArrayList<>(classDescription.getDescription());
            otherProps.remove(property);
            JSMFactBase factBase = JSMFactBase.buildFactBase(data, property, otherProps);
            factBase.reduceEquals();

            if (!factBase.isConflicted()) {
                logger.info("Start search causes for " + property.toString() + " [plus_ex=" + factBase.plusExamples.size() +
                        ", minus_ex=" + factBase.minusExamples.size() + ", univer=" + factBase.universe.size() + "]");
                JSMHypothesis cause = new JSMHypothesis(property);
                List<JSMIntersection> hypothesis = reasons(factBase, 0);
                Collections.sort(hypothesis, new Comparator<JSMIntersection>() {
                    @Override
                    public int compare(JSMIntersection o1, JSMIntersection o2) {
                        return Integer.compare(o1.generators.size(), o2.generators.size());
                    }
                });
                for (JSMIntersection intersection : hypothesis) {
                    Set<CRProperty> causeProps = new HashSet<>();
                    for (int i = 0; i < intersection.value.length(); i++)
                        if (intersection.value.get(i))
                            causeProps.add(otherProps.get(i));
                    if (causeProps.size() > 0)
                        cause.addValue(intersection.generators.size(), causeProps);
                }
                if (cause.getValue().size() > 0) {
                    causes.add(cause);
                    logger.info(cause.toString());
                }
            } else {
                logger.warn("Fact base is conflicted for property " + property.toString());
            }
        }
        return causes;
    }

    public void setMaxHypothesisLength(int maxHypothesisLength) {
        this.maxHypothesisLength = maxHypothesisLength;
    }

}
