package ru.isa.ai.ass;

import com.google.common.util.concurrent.FutureCallback;
import ru.isa.ai.linguistic.analyzers.wordnet.LinguisticWordNet;
import ru.isa.ai.linguistic.analyzers.wordnet.WordNetAnalyzer;
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
        TxtFileDataLoader loader = new TxtFileDataLoader(WordNetAnalyzerTest.class.getClassLoader().getResource("text_big.txt"), "UTF-8");
        loader.setDelimiter("\\s*\\n\\s*");
        SNCPrimer primer = loader.loadData();

        WordNetAnalyzer netAnalyzer = new WordNetAnalyzer();
        netAnalyzer.analyze(primer, new FutureCallback<LinguisticWordNet>() {
            @Override
            public void onSuccess(LinguisticWordNet result) {
                System.out.println(result.toString());
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
