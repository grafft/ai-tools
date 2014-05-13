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

        List<JSMAnalyzer.Intersection> hypothesises = analyzer.reasons(factBase);
        assertEquals(1, hypothesises.size());
        assertEquals(2, BooleanArrayUtils.cardinality(hypothesises.get(0).value));
    }
}
