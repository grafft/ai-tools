package ru.isa.ai.causal.classifiers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ru.isa.ai.causal.jsm.BooleanArrayUtils;
import ru.isa.ai.causal.jsm.JSMAnalyzer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by GraffT on 03.05.2014.
 */
public class TestJSM extends TestCase{
    public TestJSM(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestJSM.class);
    }

    public void testJSM() {
        List<BitSet> arrays = new ArrayList<>();
        JSMAnalyzer analyzer = new JSMAnalyzer(null, null);
        JSMAnalyzer.FactBase factBase = analyzer.new FactBase();
        BitSet firstUnique = new BitSet(4);
        firstUnique.set(0);
        firstUnique.set(3);
        BitSet secondUnique = new BitSet(4);
        secondUnique.set(1);
        secondUnique.set(2);
        for (int i = 0; i < 1000; i++) {
            boolean unique = true;
            BitSet array;
            do {
                array = BooleanArrayUtils.getRandomBitSet(100);
                for (BitSet value : arrays) {
                    if (BooleanArrayUtils.equals(array, value)) {
                        unique = false;
                        break;
                    }
                }
            } while (!unique);
            arrays.add(array);
            if (i < 500)
                factBase.plusExamples.put(i, BooleanArrayUtils.join(firstUnique, array));
            else
                factBase.minusExamples.put(i, BooleanArrayUtils.join(secondUnique, array));
        }

        List<JSMAnalyzer.Intersection> hypothesises = analyzer.reasons(factBase, 0);
        assertEquals(1, hypothesises.size());
        assertEquals(2, BooleanArrayUtils.cardinality(hypothesises.get(0).value));
    }

    public void testIntersection(){
        JSMAnalyzer analyzer = new JSMAnalyzer(null, null);
        JSMAnalyzer.FactBase factBase = analyzer.new FactBase();

        factBase.plusExamples.put(0, BitSet.valueOf(new byte[]{0b0000001}));
        factBase.plusExamples.put(1, BitSet.valueOf(new byte[]{0b0000011}));
        factBase.plusExamples.put(2, BitSet.valueOf(new byte[]{0b0000111}));
        factBase.plusExamples.put(3, BitSet.valueOf(new byte[]{0b1101000}));
        factBase.plusExamples.put(4, BitSet.valueOf(new byte[]{0b1100000}));

        List<JSMAnalyzer.Intersection> intersections = analyzer.searchIntersection(factBase.plusExamples, true);
        assertEquals(2, intersections.size());
        assertEquals(BitSet.valueOf(new byte[]{0b0000001}), intersections.get(0).value);
        assertEquals(3, intersections.get(0).generators.size());
        assertEquals(BitSet.valueOf(new byte[]{0b1100000}), intersections.get(1).value);
        assertEquals(2, intersections.get(1).generators.size());
    }

    public void testReasons(){
        JSMAnalyzer analyzer = new JSMAnalyzer(null, null);
        JSMAnalyzer.FactBase factBase = analyzer.new FactBase();

        factBase.plusExamples.put(0, BitSet.valueOf(new byte[]{0b0001001}));
        factBase.plusExamples.put(1, BitSet.valueOf(new byte[]{0b0001011}));
        factBase.plusExamples.put(2, BitSet.valueOf(new byte[]{0b0000111}));

        factBase.minusExamples.put(3, BitSet.valueOf(new byte[]{0b1110001}));
        factBase.minusExamples.put(4, BitSet.valueOf(new byte[]{0b1100000}));

        List<JSMAnalyzer.Intersection> intersections = analyzer.reasons(factBase, 0);
        assertEquals(2, intersections.size());
        assertEquals(BitSet.valueOf(new byte[]{0b0001001}), intersections.get(0).value);
        assertEquals(BitSet.valueOf(new byte[]{0b0000011}), intersections.get(1).value);
    }
}
