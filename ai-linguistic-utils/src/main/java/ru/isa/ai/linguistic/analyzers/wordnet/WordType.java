package ru.isa.ai.linguistic.analyzers.wordnet;

import ru.isa.ai.linguistic.utils.LinguisticUtils;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 10:42
 */
public enum WordType {
    VERB(LinguisticUtils.LEXEME_VERB, LinguisticUtils.LEXEME_PREDICATE),
    NOUN(LinguisticUtils.LEXEME_NOUN, LinguisticUtils.LEXEME_PROPERNAME, LinguisticUtils.LEXEME_ABBREVIATION),
    ADJECTIVE(LinguisticUtils.LEXEME_ADJECTIVE, LinguisticUtils.LEXEME_ADVERB, LinguisticUtils.LEXEME_COMPARATIVE);

    private int[] indexes;

    WordType(int... indexes) {
        this.indexes = indexes;
    }

    public static WordType getByIndex(int index) {
        for (WordType type : values()) {
            for (int ind : type.indexes) {
                if (ind == index) return type;
            }
        }
        return null;
    }
}
