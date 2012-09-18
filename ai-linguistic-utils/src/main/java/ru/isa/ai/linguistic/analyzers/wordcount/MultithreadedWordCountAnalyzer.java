package ru.isa.ai.linguistic.analyzers.wordcount;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.apache.log4j.Logger;
import ru.isa.ai.linguistic.analyzers.AbstractMultithreadedAnalyzer;
import ru.isa.ai.linguistic.analyzers.LinguisticsAnalyzingException;
import ru.isa.ai.linguistic.utils.LinguisticUtils;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 18.09.12
 * Time: 17:20
 */
public class MultithreadedWordCountAnalyzer extends AbstractMultithreadedAnalyzer<Map<String, Integer>, Map<String, Integer>> {

    private static final Logger logger = Logger.getLogger(MultithreadedWordCountAnalyzer.class);

    private Set<String> keywords = Collections.synchronizedSet(new HashSet<String>());

    public MultithreadedWordCountAnalyzer(Set<String> keywords) {
        super();
        this.keywords = keywords;
    }

    @Override
    public Map<String, Integer> analyzePart(String textName) throws LinguisticsAnalyzingException {
        Map<String, Integer> frequencies = new HashMap<>();
        try {
            Variant iterator = Dispatch.get(syntax, "Begin");

            if (iterator != null) {
                do {
                    Variant sentence = Dispatch.get(iterator.toDispatch(), "Value");
                    if (sentence != null) {
                        Variant tmp1 = Dispatch.get(sentence.toDispatch(), "Begin");
                        if (tmp1 != null) {
                            Dispatch tmp2 = Dispatch.get(tmp1.toDispatch(), "Value").toDispatch();
                            Dispatch wordIterator = Dispatch.get(tmp2, "Begin").toDispatch();

                            do {
                                Dispatch word = Dispatch.get(wordIterator, "Value").toDispatch();
                                Dispatch lexeme = Dispatch.get(word, "Lexeme").toDispatch();
                                String dictForm = (String) Dispatch.get(lexeme, "DictForm").toJavaObject();
                                int type = (Integer) Dispatch.get(lexeme, "Type").toJavaObject();
                                if (type != LinguisticUtils.LEXEME_PREPOSITION) {
                                    if (keywords.contains(dictForm)) {
                                        if (!frequencies.containsKey(dictForm)) {
                                            frequencies.put(dictForm, 0);
                                        }
                                        frequencies.put(dictForm, frequencies.get(dictForm) + 1);
                                    }
                                }
                            } while ((Boolean) Dispatch.call(wordIterator, "Next").toJavaObject());
                        }
                    }
                } while ((Boolean) Dispatch.call(iterator.toDispatch(), "Next").toJavaObject());
            }
        } catch (Exception e) {
            logger.error("Error during sentences extracting", e);
        }
        return frequencies;
    }

    @Override
    public Map<String, Integer> collectMainResult(Map<String, Integer> partResult, Map<String, Integer> currentResult) {
        return collect(partResult, currentResult);
    }

    @Override
    public Map<String, Integer> collectPartResult(Map<String, Integer> partResult, Map<String, Integer> currentResult) {
        return collect(partResult, currentResult);
    }

    private Map<String, Integer> collect(Map<String, Integer> partResult, Map<String, Integer> currentResult) {
        if (currentResult == null) {
            currentResult = new HashMap<>();
        }
        for (Map.Entry<String, Integer> entry : partResult.entrySet()) {
            if (currentResult.containsKey(entry.getKey())) {
                currentResult.put(entry.getKey(), currentResult.get(entry.getKey()) + entry.getValue());
            } else {
                currentResult.put(entry.getKey(), entry.getValue());
            }
        }
        return currentResult;
    }
}
