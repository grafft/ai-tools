package ru.isa.ai.ass;

import com.google.common.util.concurrent.FutureCallback;
import ru.isa.ai.linguistic.analyzers.WordNetAnalyzer;
import ru.isa.ai.linguistic.data.SNCPrimer;
import ru.isa.ai.linguistic.data.TxtFileDataLoader;

import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 06.08.12
 * Time: 14:57
 */
public class WordNetAnalyzerTest {
    public static void main(String[] args) throws Exception {
        TxtFileDataLoader loader = new TxtFileDataLoader(WordNetAnalyzerTest.class.getClassLoader().getResource("text_to_analyze.txt"), "UTF-8");
        loader.setDelimiter("\\s*\\n\\s*");
        SNCPrimer primer = loader.loadData();

        WordNetAnalyzer netAnalyzer = new WordNetAnalyzer();
        netAnalyzer.analyze(primer, new FutureCallback<Map<String, List<WordNetAnalyzer.Pair<String>>>>() {
            @Override
            public void onSuccess(Map<String, List<WordNetAnalyzer.Pair<String>>> result) {
                for (Map.Entry<String, List<WordNetAnalyzer.Pair<String>>> entry : result.entrySet()) {
                    for (WordNetAnalyzer.Pair<String> pair : entry.getValue()) {
                        System.out.println(entry.getKey() + "<-" + pair);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }
}
