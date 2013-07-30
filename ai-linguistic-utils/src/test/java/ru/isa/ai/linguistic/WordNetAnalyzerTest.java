package ru.isa.ai.linguistic;

import com.google.common.util.concurrent.FutureCallback;
import ru.isa.ai.linguistic.analyzers.SNCProgressor;
import ru.isa.ai.linguistic.analyzers.wordnet.LinguisticWordNet;
import ru.isa.ai.linguistic.analyzers.wordnet.WordNetAnalyzer;
import ru.isa.ai.linguistic.data.SNCPrimer;
import ru.isa.ai.linguistic.data.TxtFileDataLoader;

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
        netAnalyzer.setProgressor(new SNCProgressor() {
            @Override
            public void markProgress(double part) {
                System.out.println(String.format("%.0f%s", part*100, "%"));
            }
        });
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
