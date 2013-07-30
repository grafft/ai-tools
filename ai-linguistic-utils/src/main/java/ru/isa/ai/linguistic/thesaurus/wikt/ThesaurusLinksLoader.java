package ru.isa.ai.linguistic.thesaurus.wikt;

import ru.isa.ai.linguistic.analyzers.wordnet.LinguisticLink;
import ru.isa.ai.linguistic.analyzers.wordnet.LinguisticNode;
import ru.isa.ai.linguistic.analyzers.wordnet.SemanticRelationType;
import wikokit.base.wikipedia.language.LanguageType;
import wikokit.base.wikipedia.sql.Connect;
import wikokit.base.wikt.constant.Relation;
import wikokit.base.wikt.sql.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 19.06.13
 * Time: 14:16
 */
public class ThesaurusLinksLoader {
    private Connect wiktConnect;

    public void init() {
        wiktConnect = new Connect();
        wiktConnect.Open(Connect.RUWIKT_HOST, Connect.RUWIKT_PARSED_DB, Connect.RUWIKT_USER, Connect.RUWIKT_PASS, LanguageType.ru);
        TLang.createFastMaps(wiktConnect);
        TPOS.createFastMaps(wiktConnect);
        TRelationType.createFastMaps(wiktConnect);
    }

    public void close() {
        wiktConnect.Close();
    }

    public List<LinguisticLink> getThesaurusLinks(LinguisticNode node) {
        List<LinguisticLink> list = new LinkedList<>();
        TPage page = TPage.get(wiktConnect, node.getNode());
        if (page != null) {
            TLangPOS[] langPOSes = TLangPOS.get(wiktConnect, page);
            TLangPOS langPOS = null;
            for (TLangPOS lpos : langPOSes) {
                if (lpos.getLang().getLanguage() == LanguageType.ru) {
                    langPOS = lpos;
                }
            }

            if (langPOS != null) {
                TMeaning[] meanings = TMeaning.get(wiktConnect, langPOS);
                if (meanings.length > 0) {
                    TMeaning meaning = meanings[0];
                    TRelation[] relations = TRelation.get(wiktConnect, meaning);
                    for (TRelation relation : relations) {
                        if (relation.getRelationType() == Relation.hypernymy) {
                            LinguisticNode hypernym = new LinguisticNode(relation.getWikiText().getText(), node.getType());
                            LinguisticLink link = new LinguisticLink(hypernym, node, "hyponymy", SemanticRelationType.hyponymy);
                            list.add(link);
                        } else if (relation.getRelationType() == Relation.hyponymy) {
                            LinguisticNode hyponym = new LinguisticNode(relation.getWikiText().getText(), node.getType());
                            LinguisticLink link = new LinguisticLink(node, hyponym, "hyponymy", SemanticRelationType.hyponymy);
                            list.add(link);
                        }
                    }
                }
            }
        }

        return list;
    }
}
