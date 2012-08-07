package ru.isa.ai.linguistic.utils;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 09.06.12
 * Time: 11:17
 */
public abstract class LinguisticUtils {
    public static Map<Integer, String> relations = new HashMap<>();

    static {
        relations.put(1, "ABL"); // аблативная связь, в которой один компонент обозначает исходную точку движения, направление второго компонента (Президент пошел с трибуны в зал.)
        relations.put(2, "ABS"); // абстинативная связь, в которой один компонент обозначает ситуацию или реже предмет, вызывающий определенное (чаще негативное) эмоциональное или модальное отношение лица, названного другим компонентом (Депутаты боятся лишения своих полномочий)
        relations.put(3, "ADR"); // адресатная связь, один компонент которой называет лицо или реже предмет, к которому обращено информативное, донативное или эмотивное действие лица, названного другим компонентом (Пушкин посвятил стихотворение «Я помню чудное мгновенье» А.П. Керн)
        relations.put(4, "CAUS"); // каузальная связь, один компонент которой обозначает причину проявления другого компонента спустя какое-то время (Казнокрадство приводит к обнищанию населения)
        relations.put(5, "CMP"); //
        relations.put(6, "COM"); // комитативная связь, один компонент которой обозначает сопровождающее другой компонент действие, сопутствующий предмет, сопровождающее лицо (Президент встретился с коллегой в своей загородной резиденции)
        relations.put(7, "COR"); // коррелятивная связь, один компонент которой выражает возможность наблюдения другого компонента или соответствия предмета другому предмету, назначению (Возместить потери в соответствии с законодательством)
        relations.put(8, "DES"); // дестинативная связь, один компонент которой обозначает назначение для другого компонента (обратить доходы на повышение производства)
        relations.put(9, "DIS"); // дистрибутивная связь, выражающая дистрибутивные отношения между компонентами (распределить все документы по папкам)
        relations.put(10, "DIR"); // директивная связь, в которой один компонент обозначает путь, направление второго компонента (В.В. Путин поехал в США)
        relations.put(11, "DLB"); // делиберативная связь, один компонент которой выражает содержание речемыслительного, социального действия или восприятия лица, названного другим компонентом (Мы договорились о встрече)
        relations.put(12, "DST"); // деструктивная связь, один компонент которой приводит к разрушению, нарушает целостность, прежнее состояние другого компонента (Американская авиация разбомбила Багдад)
        relations.put(13, "EQ"); // эквивалентная связь, выражающая отношение эквивалентности в некотором аспекте первого и второго компонентов (Путин является президентом России)
        relations.put(14, "FAB"); // фабрикативная связь, один компонент которой называет материал, из которого сделан, изготовлен другой компонент (зубы из золота)
        relations.put(15, "GEN"); // генеративная связь, один компонент которой обозначает лицо или предмет, принадлежащий некоторой совокупности, категории, обозначаемой вторым компонентом (Бельгия относится к промышленно развитым странам)
        relations.put(16, "INS"); // инструментальная связь, один компонент которой обозначает орудие действия, обозначаемого другим компонентом (диктовать письмо по телефону)
        relations.put(17, "LIM"); // лимитативная связь, один компонент которой обозначает сферу применения, назначения другого компонента (Президент действует в пределах своих полномочий)
        relations.put(18, "LOC"); // локативная связь, один компонент которой называет местонахождение другого компонента (В Париже с успехом прошли гастроли Большого театра)
        relations.put(19, "LIQ"); // ликвидативная связь, один компонент которой ликвидирует, запрещает, отменяет второй компонент (Парламент денонсировал подписанный 2 года назад договор)
        relations.put(20, "MED"); // медиативная связь, один компонент которой имеет значение способа, средства действия другого (Президенты двух стран регулярно общаются по телефону)
        relations.put(21, "OBJ"); // объектная связь, один компонент которой называет объект действия субъекта, названного другим компонентом (Он наконец-то построил дом в деревне)
        relations.put(22, "PAR"); // партитивная связь, один компонент которой обозначает часть (части) целого, названного другим компонентом (разбить вазу на кусочки; солдата ранило в руку)
        relations.put(23, "PTN"); //
        relations.put(24, "POS"); // посессивная связь, один компонент которой выражает отношение владения другим компонентом (Абрамовичу  принадлежит ф/клуб «Челси»)
        relations.put(25, "POT"); // потенсивная связь, в которой один компонент приводит к увеличению возможности появления другого спустя некоторое время (Начавшееся наводнение грозит затоплением  прибрежных районов)
        relations.put(26, "PRM"); //
        relations.put(27, "QLT"); // квалитативная связь, в которой один компонент выражает свойство, качество  второго компонента (Парламентарии с нетерпением  ожидают прихода премьер-министра)
        relations.put(28, "QNT"); // квантитативная связь, один компонент которой называет количественный показатель изменения или соотношения признаков другого компонента (поднять зарплату на 20 процентов)
        relations.put(29, "RSN");
        relations.put(30, "SIT"); // ситуативная связь, в которой один компонент обозначает ситуацию, определяющую ситуацию или область действия другого компонента (На съезде партийцы утвердили список кандидатов в Думу)
        relations.put(31, "SUR"); // сурсивная связь, один компонент которой указывает на источник информации, содержащейся во втором компоненте (Президент узнал о случившемся  от главы своей администрации)
        relations.put(32, "TAR"); //
        relations.put(33, "TRA"); // транзитивная связь, в которой один компонент обозначает маршрут, трассу движения другого (Старик идет по дороге)
        relations.put(34, "TRG"); // трансгрессивная связь, в которой один компонент обозначает результат превращения второго (Старинная грамота  рассыпалась в пыль от одного только прикосновения)
        relations.put(35, "TMP"); // темпоральная связь, в которой один компонент выражает временную локализацию признака, названного другим компонентом (Утром был туман)
    }

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
