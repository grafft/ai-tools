package ru.isa.ai.linguistic;

import ru.isa.ai.linguistic.analyzers.wordnet.LinguisticLink;
import ru.isa.ai.linguistic.analyzers.wordnet.LinguisticNode;
import ru.isa.ai.linguistic.analyzers.wordnet.WordType;
import ru.isa.ai.linguistic.thesaurus.wikt.ThesaurusLinksLoader;

import java.util.List;
import java.util.Scanner;

/**
 * Author: Aleksandr Panov
 * Date: 19.06.13
 * Time: 14:42
 */
public class ThesaurusLoaderTest {
    public static void main(String[] args) {
        ThesaurusLinksLoader loader = new ThesaurusLinksLoader();
        loader.init();

        Scanner scanner = new Scanner(System.in);
        String query = scanner.nextLine();
        while (!"".equals(query)) {
            LinguisticNode node = new LinguisticNode(query, WordType.VERB);
            List<LinguisticLink> links = loader.getThesaurusLinks(node);
            System.out.println(links.toString());
            query = scanner.nextLine();
        }

        loader.close();
    }
}
