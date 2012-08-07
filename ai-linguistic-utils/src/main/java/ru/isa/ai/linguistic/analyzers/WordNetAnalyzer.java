package ru.isa.ai.linguistic.analyzers;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.apache.log4j.Logger;
import ru.isa.ai.linguistic.utils.LinguisticUtils;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 06.08.12
 * Time: 14:16
 */
public class WordNetAnalyzer extends AbstractLinguisticAnalyzer<Map<String, List<WordNetAnalyzer.Pair<String>>>, Map<String, List<WordNetAnalyzer.Pair<String>>>> {
    private static final Logger logger = Logger.getLogger(WordNetAnalyzer.class);

    public class Pair<T> {
        private T first;
        private T second;

        public Pair(T first, T second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public void setFirst(T first) {
            this.first = first;
        }

        public T getSecond() {
            return second;
        }

        public void setSecond(T second) {
            this.second = second;
        }

        public String toString() {
            return String.format("(%s,%s)", first.toString(), second.toString());
        }
    }

    @Override
    public Map<String, List<Pair<String>>> analyzePart(String textName) throws LinguisticsAnalyzingException {
        Map<String, List<Pair<String>>> network = new HashMap<>();
        try {
            Variant semanticIterator = Dispatch.get(semantics, "Begin");
            if (semanticIterator != null) {
                Variant hasNext;
                do {
                    Variant semanteme = Dispatch.get(semanticIterator.toDispatch(), "Value");
                    if (semanteme != null) {
                        int semantemeType = (Integer) Dispatch.get(semanteme.toDispatch(), "Type").toJavaObject();
                        String semantemeName = (String) Dispatch.get(semanteme.toDispatch(), "Name").toJavaObject();

                        Dispatch firstSyntaxeme = Dispatch.get(semanteme.toDispatch(), "FirstSyntaxeme").toDispatch();
                        Dispatch firstWordIterator = Dispatch.get(firstSyntaxeme, "WordIterator").toDispatch();
                        Dispatch firstWord = Dispatch.get(firstWordIterator, "Value").toDispatch();
                        Dispatch firstLexeme = Dispatch.get(firstWord, "Lexeme").toDispatch();
                        String firstDictForm = (String) Dispatch.get(firstLexeme, "DictForm").toJavaObject();
                        int firstType = (Integer) Dispatch.get(firstLexeme, "Type").toJavaObject();
                        int firstSemClass = (Integer) Dispatch.get(firstLexeme, "SemClass").toJavaObject();
                        if (firstType == LinguisticUtils.LEXEME_PREPOSITION) {
                            if ((Boolean) Dispatch.call(firstWordIterator, "Next").toJavaObject()) {
                                firstWord = Dispatch.get(firstWordIterator, "Value").toDispatch();
                                firstLexeme = Dispatch.get(firstWord, "Lexeme").toDispatch();
                                firstDictForm = (String) Dispatch.get(firstLexeme, "DictForm").toJavaObject();
                                firstType = (Integer) Dispatch.get(firstLexeme, "Type").toJavaObject();
                                firstSemClass = (Integer) Dispatch.get(firstLexeme, "SemClass").toJavaObject();
                            }
                        }

                        Dispatch secondSyntaxeme = Dispatch.get(semanteme.toDispatch(), "SecondSyntaxeme").toDispatch();
                        Dispatch secondWordIterator = Dispatch.get(secondSyntaxeme, "WordIterator").toDispatch();
                        Dispatch secondWord = Dispatch.get(secondWordIterator, "Value").toDispatch();
                        Dispatch secondLexeme = Dispatch.get(secondWord, "Lexeme").toDispatch();
                        String secondDictForm = (String) Dispatch.get(secondLexeme, "DictForm").toJavaObject();
                        int secondType = (Integer) Dispatch.get(secondLexeme, "Type").toJavaObject();
                        int secondSemClass = (Integer) Dispatch.get(secondLexeme, "SemClass").toJavaObject();
                        if (secondType == LinguisticUtils.LEXEME_PREPOSITION) {
                            if ((Boolean) Dispatch.call(secondWordIterator, "Next").toJavaObject()) {
                                secondWord = Dispatch.get(secondWordIterator, "Value").toDispatch();
                                secondLexeme = Dispatch.get(secondWord, "Lexeme").toDispatch();
                                secondDictForm = (String) Dispatch.get(secondLexeme, "DictForm").toJavaObject();
                                secondType = (Integer) Dispatch.get(secondLexeme, "Type").toJavaObject();
                                secondSemClass = (Integer) Dispatch.get(secondLexeme, "SemClass").toJavaObject();
                            }
                        }

                        if (!LinguisticUtils.LEXEME_NONSIGNED.contains(firstType) && !LinguisticUtils.LEXEME_NONSIGNED.contains(secondType)) {
                            List<Pair<String>> links;
                            if (network.containsKey(firstDictForm)) {
                                links = network.get(firstDictForm);
                            } else {
                                links = new ArrayList<>(1);
                                network.put(firstDictForm, links);
                            }
                            links.add(new Pair<>(secondDictForm, "" + semantemeName));
                        }
                    }
                    hasNext = Dispatch.call(semanticIterator.toDispatch(), "Next");
                }
                while (hasNext != null && (Boolean) hasNext.toJavaObject());

            }
        } catch (Exception e) {
            logger.error("Error during sentences extracting", e);
        }
        return network;
    }

    @Override
    public Map<String, List<Pair<String>>> collectMainResult(Map<String, List<Pair<String>>> partResult, Map<String, List<Pair<String>>> currentResult) {
        if (currentResult == null) {
            currentResult = new HashMap<>();
        }
        for (Map.Entry<String, List<Pair<String>>> entry : partResult.entrySet()) {
            if (currentResult.containsKey(entry.getKey())) {
                List<Pair<String>> toAdd = new ArrayList<>(5);
                List<Pair<String>> currentLinks = currentResult.get(entry.getKey());
                for (Pair<String> link : currentLinks) {
                    if (!currentResult.get(entry.getKey()).contains(link)) {
                        toAdd.add(link);
                    }
                }
                currentLinks.addAll(toAdd);
            } else {
                currentResult.put(entry.getKey(), entry.getValue());
            }
        }
        return currentResult;
    }

}
