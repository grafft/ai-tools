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
 * Date: 30.04.2014
 * Time: 11:21
 */
public class JSMAnalyzer {
    private static final Logger logger = LogManager.getLogger(JSMAnalyzer.class.getSimpleName());

    private AQClassDescription classDescription;
    private Instances data;
    private int maxHypothesisLength = 3;

    public JSMAnalyzer(AQClassDescription classDescription, Instances data) {
        this.classDescription = classDescription;
        this.data = data;
    }

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
            FactBase factBase = buildFactBase(data, property, otherProps);
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

    public List<Intersection> reasons(FactBase factBase, int deep) {
        List<Intersection> hypothesis = new ArrayList<>();

        // 1. Находим минимальные пересечения над объектами, обладающими свойством
        List<Intersection> intersections = searchIntersection(factBase.plusExamples, true);
        // упорядочиваем их по убыванию мощности множеств образующих
        Collections.sort(intersections);
        // 2. Для любого минимального пересечения:
        for (Intersection intersection : intersections) {
            // 2.1. Ищем объект из объектов, не обладающих свойством, в который входит это пересечение (его индекс)
            int minusObject = -1;
            for (Map.Entry<Integer, BitSet> entry : factBase.minusExamples.entrySet()) {
                if (BooleanArrayUtils.include(entry.getValue(), intersection.value)) {
                    minusObject = entry.getKey();
                    break;
                }
            }
            // 2.2 Если такого объекта нет, то включить пересечение в гипотезы
            if (minusObject == -1) {
                hypothesis.add(intersection);
            } else {
                // положительные примеры - это множество образующих с вычтенным пересечением
                FactBase newFactBase = new FactBase();
                for (Integer objectId : intersection.generators) {
                    newFactBase.plusExamples.put(objectId, BooleanArrayUtils.andNot(factBase.plusExamples.get(objectId), intersection.value));
                }
                // отрицательные примеры - множество исходныех отрицательных с вычетом найденного примера
                for (Map.Entry<Integer, BitSet> entry : factBase.minusExamples.entrySet()) {
                    newFactBase.minusExamples.put(entry.getKey(), BooleanArrayUtils.andNot(entry.getValue(), intersection.value));
                }
                // с полученными новыми мнжествами примеров и усеченным универсумом - ищем причины
                List<Intersection> toAdd = reasons(newFactBase, deep + 1);
                for (Intersection inter : toAdd) {
                    Intersection clone = intersection.clone();
                    clone.add(inter);
                    if (BooleanArrayUtils.cardinality(clone.value) <= maxHypothesisLength) {
                        hypothesis.add(clone);
                    }
                }
            }
        }
        // 3. Включаем в гипотезы объекты, не входящие во множество образующих ни одного минимального пересечения
        for (Map.Entry<Integer, BitSet> entry : factBase.plusExamples.entrySet()) {
            boolean toAdd = true;
            for (Intersection inter : intersections) {
                if (inter.generators.contains(entry.getKey())) {
                    toAdd = false;
                    break;
                }
            }
            // если его размер не слишеом велик
            if (toAdd && BooleanArrayUtils.cardinality(entry.getValue()) <= maxHypothesisLength)
                hypothesis.add(new Intersection(entry.getValue(), entry.getKey()));
        }
        // 4. Исключаем из гипотез те гипотезы, которые являются надмножествами других
        List<Intersection> toDel = new ArrayList<>();
        for (int i = 0; i < hypothesis.size(); i++) {
            for (int j = 0; j < hypothesis.size(); j++) {
                if (i != j && BooleanArrayUtils.include(hypothesis.get(i).value, hypothesis.get(j).value)) {
                    toDel.add(hypothesis.get(i));
                    break;
                }
            }
        }
        for (Intersection inter : toDel)
            hypothesis.remove(inter);
        return hypothesis;
    }

    public List<Intersection> searchIntersection(Map<Integer, BitSet> objectMap, boolean check) {
        List<Intersection> intersections = new ArrayList<>();

        Map<Integer, BitSet> objects = new HashMap<>();
        int firstKey = -1;
        Intersection intersection = null;
        for (Map.Entry<Integer, BitSet> entry : objectMap.entrySet()) {
            if (firstKey == -1) {
                intersection = new Intersection(entry.getValue(), entry.getKey());
                firstKey = entry.getKey();
            } else {
                objects.put(entry.getKey(), entry.getValue());
            }
        }
        if (intersection != null) {
            // 1. последовательно пересекать первый объект со следующим, накапливая результат, пропуская пустые объекты
            intersection.intersect(objects);
            // 2. если образующих больше 1, то пересечение - минимальное
            if (intersection.generators.size() > 1)
                intersections.add(intersection);
            // 3. построить новый массив объектов вычитанием из имеющихся объектов полученного пересечения, считая непустые объекты
            Map<Integer, BitSet> newObjects = new HashMap<>();
            for (Map.Entry<Integer, BitSet> entry : objectMap.entrySet()) {
                BitSet result = BooleanArrayUtils.andNot(entry.getValue(), intersection.value);
                if (BooleanArrayUtils.cardinality(result) > 0) {
                    newObjects.put(entry.getKey(), result);
                }
            }
            // 4. Если непустых объектов, по крайней мере два, повторить процедуру с шага 1, считая первый непустой элемент первым
            if (newObjects.size() > 1) {
                intersections.addAll(searchIntersection(newObjects, false));
            }

            // 5. Для каждого полученного минимального пересечения получить пересечение его образующих и
            // в случае несовпадения результата с имнимальным пересечением исключить последнее из
            // множества минимальных пересечений
            if (check) {
                List<Intersection> toDel = new ArrayList<>();
                for (Intersection inter : intersections) {
                    List<BitSet> generators = new ArrayList<>();
                    for (Map.Entry<Integer, BitSet> entry : objectMap.entrySet()) {
                        if (inter.generators.contains(entry.getKey()))
                            generators.add(entry.getValue());
                    }
                    BitSet result = BooleanArrayUtils.andAll(generators);
                    if (!BooleanArrayUtils.equals(inter.value, result))
                        toDel.add(inter);
                }
                for (Intersection inter : toDel)
                    intersections.remove(inter);
            }
        }
        return intersections;
    }

    private FactBase buildFactBase(Instances data, CRProperty keyProperty, List<CRProperty> properties) {
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

    public class FactBase {
        public Map<Integer, BitSet> plusExamples = new HashMap<>();
        public Map<Integer, BitSet> minusExamples = new HashMap<>();
        List<CRProperty> universe;

        void reduceEquals() {
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

        boolean isConflicted() {
            for (BitSet plusExample : plusExamples.values()) {
                for (BitSet minusExample : minusExamples.values()) {
                    if (BooleanArrayUtils.equals(plusExample, minusExample))
                        return true;
                }
            }
            return false;
        }
    }

    public class Intersection implements Comparable<Intersection>, Cloneable {
        public BitSet value;
        public List<Integer> generators = new ArrayList<>();

        private Intersection(BitSet value, int objectId) {
            this.value = value;
            generators.add(objectId);
        }

        private Intersection(BitSet value, List<Integer> generators) {
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
