package ru.isa.ai.causal.jsm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isa.ai.causal.classifiers.AQClassDescription;
import ru.isa.ai.causal.classifiers.CRProperty;
import weka.core.Attribute;
import weka.core.Instance;
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
    protected int minGeneratrixSize = 5;

    public AbstractJSMAnalyzer(AQClassDescription classDescription, Instances data) {
        this.classDescription = classDescription;
        this.data = data;
    }

    public abstract List<Intersection> reasons(FactBase factBase, int deep);

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
            FactBase factBase = FactBase.buildFactBase(data, property, otherProps);
            factBase.reduceEquals();

            if (!factBase.isConflicted()) {
                logger.info("Start search causes for " + property.toString() + " [plus_ex=" + factBase.plusExamples.size() +
                        ", minus_ex=" + factBase.minusExamples.size() + ", univer=" + factBase.universe.size() + "]");
                JSMHypothesis cause = new JSMHypothesis(property);
                List<Intersection> hypothesis = reasons(factBase, 0);
                for (Intersection intersection : hypothesis) {
                    Set<CRProperty> causeProps = new HashSet<>();
                    for (int i = 0; i < intersection.value.length(); i++)
                        if (intersection.value.get(i))
                            causeProps.add(otherProps.get(i));
                    if (causeProps.size() > 0)
                        cause.addValue(causeProps);
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

    public static class FactBase {
        public Map<Integer, BitSet> plusExamples = new HashMap<>();
        public Map<Integer, BitSet> minusExamples = new HashMap<>();
        public List<CRProperty> universe;

        public static  FactBase buildFactBase(Instances data, CRProperty keyProperty, List<CRProperty> properties) {
            FactBase factBase = new FactBase();
            factBase.universe = properties;

            Attribute keyAttr = data.attribute(keyProperty.getFeature().getName());
            for (Instance event : data) {
                BitSet objectVector = new BitSet(properties.size());
                for (int i = 0; i < properties.size(); i++) {
                    Attribute attr = data.attribute(properties.get(i).getFeature().getName());
                    switch (attr.type()) {
                        case Attribute.NOMINAL:
                            String value = attr.value((int) event.value(attr.index()));
                            objectVector.set(i, properties.get(i).coverNominal(value));
                            break;
                        case Attribute.NUMERIC:
                            objectVector.set(i, properties.get(i).cover(event.value(attr.index())));
                            break;
                    }
                }
                boolean isCover = false;
                switch (keyAttr.type()) {
                    case Attribute.NOMINAL:
                        String value = keyAttr.value((int) event.value(keyAttr.index()));
                        isCover = keyProperty.coverNominal(value);
                        break;
                    case Attribute.NUMERIC:
                        isCover = keyProperty.cover(event.value(keyAttr.index()));
                        break;
                }
                if (isCover) {
                    if (!factBase.plusExamples.containsValue(objectVector))
                        factBase.plusExamples.put(data.indexOf(event), objectVector);
                } else {
                    if (!factBase.minusExamples.containsValue(objectVector))
                        factBase.minusExamples.put(data.indexOf(event), objectVector);
                }
            }
            return factBase;
        }

        public void reduceEquals() {
            Set<Integer> toRemove = new HashSet<>();
            for (Map.Entry<Integer, BitSet> entry1 : plusExamples.entrySet()) {
                for (Map.Entry<Integer, BitSet> entry2 : plusExamples.entrySet()) {
                    if (!entry1.getKey().equals(entry2.getKey()) && !toRemove.contains(entry2.getKey()) &&
                            BooleanArrayUtils.equals(entry1.getValue(), entry2.getValue()))
                        toRemove.add(entry1.getKey());
                }
            }

            for (int index : toRemove)
                plusExamples.remove(index);

            toRemove.clear();
            for (Map.Entry<Integer, BitSet> entry1 : minusExamples.entrySet()) {
                for (Map.Entry<Integer, BitSet> entry2 : minusExamples.entrySet()) {
                    if (!entry1.getKey().equals(entry2.getKey()) && !toRemove.contains(entry2.getKey()) &&
                            BooleanArrayUtils.equals(entry1.getValue(), entry2.getValue()))
                        toRemove.add(entry1.getKey());
                }
            }
            for (int index : toRemove)
                minusExamples.remove(index);
        }

        public boolean isConflicted() {
            for (BitSet plusExample : plusExamples.values()) {
                for (BitSet minusExample : minusExamples.values()) {
                    if (BooleanArrayUtils.equals(plusExample, minusExample))
                        return true;
                }
            }
            return false;
        }
    }

    public static class Intersection implements Comparable<Intersection>, Cloneable {
        public BitSet value;
        public List<Integer> generators = new ArrayList<>();

        protected Intersection(BitSet value, int objectId) {
            this.value = value;
            generators.add(objectId);
        }

        protected Intersection(BitSet value, List<Integer> generators) {
            this.value = value;
            generators.addAll(generators);
        }

        public void intersect(Map<Integer, BitSet> objects) {
            for (Map.Entry<Integer, BitSet> entry : objects.entrySet()) {
                BitSet result = BooleanArrayUtils.and(value, entry.getValue());
                if (BooleanArrayUtils.cardinality(result) > 0) {
                    value = result;
                    generators.add(entry.getKey());
                }
            }
        }

        public void add(Intersection toAdd) {
            generators.clear();
            generators.addAll(toAdd.generators);
            value = BooleanArrayUtils.or(value, toAdd.value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Intersection that = (Intersection) o;

            return BooleanArrayUtils.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public int compareTo(Intersection o) {
            if (o == null)
                return 1;
            return Integer.compare(this.generators.size(), o.generators.size());
        }

        @Override
        public Intersection clone() {
            return new Intersection((BitSet) this.value.clone(), this.generators);
        }
    }

}
