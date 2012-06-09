package ru.isa.ai.linguistic.analyzers;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import ru.isa.ai.linguistic.AbstractLinguisticAnalyzer;
import ru.isa.ai.linguistic.LinguisticsAnalyzingException;
import ru.isa.ai.linguistic.utils.LinguisticUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

/**
 * User: GraffT
 * Date: 19.10.11
 * Time: 16:04
 */
public class WordCountAnalyzer extends AbstractLinguisticAnalyzer<Map<String, Integer>> {

    private static final Logger logger = Logger.getLogger(WordCountAnalyzer.class);

    private Map<String, Integer> frequencies = new HashMap<>();

    @Override
    public void analyzeStub(String textName) throws LinguisticsAnalyzingException {
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
    }

    public void resetResult() {
        frequencies.clear();
    }

    @Override
    public Map<String, Integer> getResult() {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static List<String> loadKeywords(URL filename) throws IOException {
        ArrayList<String> array = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(filename.openStream(), Charset.forName("UTF-8")));
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter(Pattern.compile("\\n"));

        while (scanner.hasNext()) {
            String line = scanner.next().trim();
            if (line.contains("/")) {
                Pattern pattern = Pattern.compile("/");
                for (String part : pattern.split(line)) {
                    if (!StringUtils.isEmpty(part)) {
                        array.add(part.toLowerCase());
                    }
                }

            } else {
                if (!StringUtils.isEmpty(line)) {
                    array.add(line.toLowerCase());
                }
            }
        }
        scanner.close();
        return array;
    }

    public static List<String> loadKeywords(File file) throws IOException {
        return loadKeywords(file.toURI().toURL());
    }
}
