package ru.isa.ai.causal.jsm;

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
    private Map<String, Set<CRProperty>> classDescriptions;
    private Instances data;
    private int maxHypothesisLength = 3;

    public JSMAnalyzer(Map<String, Set<CRProperty>> classDescriptions, Instances data) {
        this.classDescriptions = classDescriptions;
        this.data = data;
    }

    public List<JSMHypothesis> evaluateCauses(String className) {
        List<JSMHypothesis> causes = new ArrayList<>();
        Set<CRProperty> properties = classDescriptions.get(className);
        for (CRProperty property : properties) {
            List<CRProperty> otherProps = new ArrayList<>(properties);
            otherProps.remove(property);
            FactBase factBase = buildFactBase(data, property, otherProps);
            factBase.reduceEquals();

            if (!factBase.isConflicted()) {
                JSMHypothesis cause = new JSMHypothesis(property);
                List<Intersection> hypothesis = reasons(factBase);
                for (Intersection intersection : hypothesis) {
                    Set<CRProperty> causeProps = new HashSet<>();
                    for (int i = 0; i < intersection.value.length; i++)
                        if (intersection.value[i] == 1)
                            causeProps.add(otherProps.get(i));
                    if (causeProps.size() > 0)
                        cause.addValue(causeProps);
                }
                if (cause.getValue().size() > 0)
                    causes.add(cause);
            }
        }
        return causes;
    }

    public void setMaxHypothesisLength(int maxHypothesisLength) {
        this.maxHypothesisLength = maxHypothesisLength;
    }

    private List<Intersection> reasons(FactBase factBase) {
        List<Intersection> hypothesis = new ArrayList<>();

        // 1. Находим минимальные пересечения над объектами, обладающими свойством
        List<Intersection> intersections = searchIntersection(factBase.plusExamples, true);
        // упорядочиваем их по убыванию мощности множеств образующих
        Collections.sort(intersections);
        // 2. Для любого минимального пересечения:
        for (Intersection intersection : intersections) {
            // 2.1. Ищем объект из объектов, не обладающих свойством, в который входит это пересечение (его индекс)
            int minusObject = -1;
            for (Map.Entry<Integer, byte[]> entry : factBase.minusExamples.entrySet()) {
                if (BooleanArrayUtils.include(entry.getValue(), intersection.value)) {
                    minusObject = entry.getKey();
                    break;
                }
            }
            // 2.2 Если такого объекта нет, то включить пересечение в гипотезы
            if (minusObject == -1) {
                hypothesis.add(intersection);
            } else if (intersection.value.length < maxHypothesisLength) {
                // положительные примеры - это множество образующих с вычтенным пересечением
                FactBase newFactBase = new FactBase();
                for (Integer objectId : intersection.generators) {
                    newFactBase.plusExamples.put(objectId, BooleanArrayUtils.subtraction(factBase.plusExamples.get(objectId), intersection.value));
                }
                // отрицательные примеры - множество исходныех отрицательных с вычетом найденного примера
                for (Map.Entry<Integer, byte[]> entry : factBase.minusExamples.entrySet()) {
                    newFactBase.minusExamples.put(entry.getKey(), BooleanArrayUtils.subtraction(entry.getValue(), intersection.value));
                }
                // с полученными новыми мнжествами примеров и усеченным универсумом - ищем причины
                List<Intersection> toAdd = reasons(newFactBase);
                for (Intersection inter : toAdd) {
                    intersection.add(inter);
                    hypothesis.add(intersection);
                }
            }
        }
        // 3. Включаем в гипотезы объекты, не входящие во множество образующих ни одного минимального пересечения
        for (Map.Entry<Integer, byte[]> entry : factBase.plusExamples.entrySet()) {
            boolean toAdd = true;
            for (Intersection inter : intersections) {
                if (inter.generators.contains(entry.getKey())) {
                    toAdd = false;
                    break;
                }
            }
            // если его размер не слишеом велик
            if (toAdd && BooleanArrayUtils.countNonZero(entry.getValue()) <= maxHypothesisLength)
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

    private List<Intersection> searchIntersection(Map<Integer, byte[]> objectMap, boolean check) {
        List<Intersection> intersections = new ArrayList<>();

        List<byte[]> objects = new ArrayList<>();
        int firstKey = -1;
        Intersection intersection = null;
        for (Map.Entry<Integer, byte[]> entry : objectMap.entrySet()) {
            if (firstKey == -1) {
                intersection = new Intersection(entry.getValue(), entry.getKey());
                firstKey = entry.getKey();
            } else {
                objects.add(entry.getValue());
            }
        }
        if (intersection != null) {
            // 1. последовательно пересекать первый объект со следующим, накапливая результат, пропуская пустые объекты
            intersection.intersect(objects);
            // 2. если образующих больше 1, то пересечение - минимальное
            if (intersection.generators.size() > 1)
                intersections.add(intersection);
            // 3. построить новый массив объектов вычитанием из имеющихся объектов полученного пересечения, считая непустые объекты
            Map<Integer, byte[]> newObjects = new HashMap<>();
            for (Map.Entry<Integer, byte[]> entry : objectMap.entrySet()) {
                byte[] result = BooleanArrayUtils.subtraction(entry.getValue(), intersection.value);
                if (BooleanArrayUtils.countNonZero(result) > 0) {
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
                    List<byte[]> generators = new ArrayList<>();
                    for (Map.Entry<Integer, byte[]> entry : objectMap.entrySet()) {
                        if (inter.generators.contains(entry.getKey()))
                            generators.add(entry.getValue());
                    }
                    byte[] result = BooleanArrayUtils.multiplyAll(generators);
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
            byte[] objectVector = new byte[properties.size()];
            for (int i = 0; i < properties.size(); i++) {
                Attribute attr = data.attribute(properties.get(i).getFeature().getName());
                objectVector[i] = (byte) (properties.get(i).cover(event.value(attr.index())) ? 1 : 0);
            }
            if (keyProperty.cover(event.value(keyAttr.index()))) {
                factBase.plusExamples.put(data.indexOf(event), objectVector);
            } else {
                factBase.minusExamples.put(data.indexOf(event), objectVector);
            }
        }
        return factBase;
    }

    private class FactBase {
        Map<Integer, byte[]> plusExamples = new HashMap<>();
        Map<Integer, byte[]> minusExamples = new HashMap<>();
        List<CRProperty> universe;

        void reduceEquals() {
            List<Integer> toRemove = new ArrayList<>();
            for (int i = 0; i < plusExamples.size(); i++) {
                for (int j = i + 1; j < plusExamples.size(); j++) {
                    if (BooleanArrayUtils.equals(plusExamples.get(i), plusExamples.get(j)))
                        toRemove.add(i);
                }
            }
            for (int index : toRemove)
                plusExamples.remove(index);
            toRemove.clear();
            for (int i = 0; i < minusExamples.size(); i++) {
                for (int j = i + 1; j < minusExamples.size(); j++) {
                    if (BooleanArrayUtils.equals(minusExamples.get(i), minusExamples.get(j)))
                        toRemove.add(i);
                }
            }
            for (int index : toRemove)
                minusExamples.remove(index);
        }

        boolean isConflicted() {
            for (byte[] plusExample : plusExamples.values()) {
                for (byte[] minusExample : minusExamples.values()) {
                    if (BooleanArrayUtils.equals(plusExample, minusExample))
                        return true;
                }
            }
            return false;
        }
    }

    private class Intersection implements Comparable<Intersection> {
        byte[] value;
        List<Integer> generators = new ArrayList<>();

        private Intersection(byte[] value, int objectId) {
            this.value = value;
            generators.add(objectId);
        }

        public void intersect(List<byte[]> objects) {
            for (int i = 0; i < objects.size(); i++) {
                byte[] result = BooleanArrayUtils.multiply(value, objects.get(i));
                if (BooleanArrayUtils.countNonZero(result) > 0) {
                    value = result;
                    generators.add(i);
                }
            }
        }

        public void add(Intersection toAdd) {
            generators.clear();
            generators.addAll(toAdd.generators);
            value = BooleanArrayUtils.addition(value, toAdd.value);
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
            return Arrays.hashCode(value);
        }

        @Override
        public int compareTo(Intersection o) {
            if (o == null)
                return 1;
            return Integer.compare(this.generators.size(), o.generators.size());
        }

    }
}
