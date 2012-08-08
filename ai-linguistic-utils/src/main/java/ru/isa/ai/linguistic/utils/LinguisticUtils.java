package ru.isa.ai.linguistic.utils;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 09.06.12
 * Time: 11:17
 */
public abstract class LinguisticUtils {
    public static final int LANGUAGE_UNKNOWN = -1;
    public static final int LANGUAGE_RUSSIAN = 0;
    public static final int LANGUAGE_ENGLISH = 1;
    public static final int LANGUAGE_GERMAN = 2;

    public static final int LEXEME_UNDEF = 0;
    public static final int LEXEME_FIRST = 1;
    public static final int LEXEME_VERB = 1;
    public static final int LEXEME_NOUN = 2;
    public static final int LEXEME_ADJECTIVE = 3;
    public static final int LEXEME_PRONOUN = 4;
    public static final int LEXEME_NUMERAL = 5;
    public static final int LEXEME_PROPERNAME = 6;
    public static final int LEXEME_PARENTHESIS = 7;
    public static final int LEXEME_INTERJECTION = 8;
    public static final int LEXEME_PREDICATE = 9;
    public static final int LEXEME_PREPOSITION = 10;
    public static final int LEXEME_CONJUNCTION = 11;
    public static final int LEXEME_PARTICLE = 12;
    public static final int LEXEME_ADVERB = 13;
    public static final int LEXEME_COMPARATIVE = 14;
    public static final int LEXEME_ABBREVIATION = 15;
    public static final int LEXEME_NUMBER = 16;
    public static final int LEXEME_LAST = 16;
    public static final List<Integer> LEXEME_NONSIGNED = Arrays.asList(LEXEME_UNDEF, LEXEME_PRONOUN, LEXEME_NUMERAL,
            LEXEME_PARENTHESIS, LEXEME_INTERJECTION, LEXEME_PREPOSITION, LEXEME_CONJUNCTION, LEXEME_PARTICLE, LEXEME_NUMBER);

    public static final int LEXEME_GEN_NOUN = 32;
    public static final int LEXEME_GEN_ADJECTIVE = 64;
    public static final int LEXEME_GEN_PREDICATE = 128;
    public static final int LEXEME_GEN_OTHER = 0;

    public static final int TENSE_UNDEF = 0;
    public static final int TENSE_INFINITIVE = 8192;
    public static final int TENSE_IMPERATIVE = 16384;
    public static final int TENSE_FUTURE = 24576;
    public static final int TENSE_PRESENT = 32768;
    public static final int TENSE_PAST = 40960;

    public static final int PERSON_UNDEF = 0;
    public static final int PERSON_FIRST = 65536;
    public static final int PERSON_SECOND = 131072;
    public static final int PERSON_THIRD = 196608;

    public static final int FORM_PERSONAL = 0;
    public static final int FORM_ACTIVE_PARTICIPLE = 262144;
    public static final int FORM_PASSIVE_PARTICIPLE = 524288;
    public static final int FORM_VERBAL_ADVERB = 786432;

    public static final int SYNTAXEME_UNDEF = 0;
    public static final int SYNTAXEME_SYNTAXEME = 1;
    public static final int SYNTAXEME_PREDICATOR = 2;

    public static final int SEM_NOUN_CLASS_NDF = 1;
    public static final int SEM_NOUN_CLASS_PRS = 2;
    public static final int SEM_NOUN_CLASS_OBJ = 4;
    public static final int SEM_NOUN_CLASS_ATR = 8;
    public static final int SEM_NOUN_CLASS_TMP = 16;
    public static final int SEM_NOUN_CLASS_LOC = 32;
    public static final int SEM_NOUN_CLASS_QNT = 64;
    public static final int SEM_NOUN_CLASS_EIZ = 128;
    public static final int SEM_NOUN_CLASS_PIZ = 256;
    public static final int SEM_VERB_CLASS_NDEF = 0;
    public static final int SEM_VERB_CLASS_ACTN = 1;
    public static final int SEM_VERB_CLASS_STAT = 2;
    public static final int SEM_VERB_CLASS_FUNC = 3;
    public static final int SEM_VERB_CLASS_LOCL = 4;
    public static final int SEM_VERB_CLASS_POSS = 5;
    public static final int SEM_VERB_CLASS_PART = 6;
    public static final int SEM_VERB_CLASS_CMPR = 7;
    public static final int SEM_VERB_CLASS_AVTR = 8;
    public static final int SEM_VERB_CLASS_CAUS = 9;
    public static final int SEM_VERB_CLASS_MODL = 10;
    public static final int SEM_VERB_CLASS_PHAS = 11;
    public static final int SEM_VERB_CLASS_CMPS = 12;
    public static final int SEM_VERB_CLASS_NFMN = 13;
    public static final int SEM_VERB_CLASS_RLDM = 14;
    public static final int SEM_VERB_CLASS_CMPL = 15;

    public static final long MIL_IN_DAY = 24 * 60 * 60 * 1000;
    public static final long MIL_IN_HOUR = 60 * 60 * 1000;
    public static final long MIL_IN_MIN = 60 * 1000;
    public static final long MIL_IN_SEC = 1000;

    public static String getFormattedIntervalFromCurrent(long fromMillis) {
        return getFormattedInterval(System.currentTimeMillis() - fromMillis);
    }

    public static String getFormattedInterval(long interval) {
        StringBuilder builder = new StringBuilder();
        long day = interval / MIL_IN_DAY;
        long hour = (interval - day * MIL_IN_DAY) / MIL_IN_HOUR;
        long min = (interval - day * MIL_IN_DAY - hour * MIL_IN_HOUR) / MIL_IN_MIN;
        long sec = (interval - day * MIL_IN_DAY - hour * MIL_IN_HOUR - min * MIL_IN_MIN) / MIL_IN_SEC;
        long mil = (interval - day * MIL_IN_DAY - hour * MIL_IN_HOUR - min * MIL_IN_MIN - sec * MIL_IN_SEC);
        builder.append(day).append(" day ").append(hour).append(" hour ")
                .append(min).append(" min ").append(sec).append(".")
                .append(mil).append(" sec");
        return builder.toString();
    }
}
