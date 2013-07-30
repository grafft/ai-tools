package ru.isa.ai.linguistic.analyzers.wordnet;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.apache.log4j.Logger;
import ru.isa.ai.linguistic.analyzers.AbstractLinguisticAnalyzer;
import ru.isa.ai.linguistic.analyzers.LinguisticsAnalyzingException;
import ru.isa.ai.linguistic.thesaurus.wikt.ThesaurusLinksLoader;
import ru.isa.ai.linguistic.utils.LinguisticUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 06.08.12
 * Time: 14:16
 */
public class WordNetAnalyzer extends AbstractLinguisticAnalyzer<LinguisticWordNet, LinguisticWordNet> {
    private static final Logger logger = Logger.getLogger(WordNetAnalyzer.class);
    private static ThesaurusLinksLoader thesaurusLinksLoader = new ThesaurusLinksLoader();
    static{
        thesaurusLinksLoader.init();
    }

    @Override
    public LinguisticWordNet analyzePart(String textName) throws LinguisticsAnalyzingException {
        LinguisticWordNet network = new LinguisticWordNet();
        analyzePartVerbs(network);
        //analyzePartNouns(network);
        return network;
    }

    private void analyzePartVerbs(LinguisticWordNet network) {
        try {
            Variant semRoleIterator = Dispatch.get(roleSemantics, "Begin");
            if (semRoleIterator != null) {
                Variant hasNext;
                String predicatorDictForm = "";
                int predicatorType = 0;
                do {
                    Variant semRole = Dispatch.get(semRoleIterator.toDispatch(), "Value");
                    if (semRole != null) {
                        // выделяем глагол
                        int semRoleType = (Integer) Dispatch.get(semRole.toDispatch(), "Type").toJavaObject();
                        Dispatch predicator = Dispatch.get(semRole.toDispatch(), "Predicator").toDispatch();
                        Variant predicatorLexeme = Dispatch.get(predicator, "Lexeme");
                        if (predicatorLexeme != null) {
                            predicatorDictForm = (String) Dispatch.get(predicatorLexeme.toDispatch(), "DictForm").toJavaObject();
                            predicatorType = (Integer) Dispatch.get(predicatorLexeme.toDispatch(), "Type").toJavaObject();
                        }

                        // анализируем именную группу
                        Dispatch iIterator = Dispatch.get(semRole.toDispatch(), "Syntaxeme").toDispatch();
                        Dispatch synWordIterator = Dispatch.get(iIterator, "WordIterator").toDispatch();
                        Dispatch synWord = Dispatch.get(synWordIterator, "Value").toDispatch();
                        LinguisticWordNet nestedNet = analyzeSynWord(synWord);
                        network.injection(nestedNet);

                        if (LinguisticUtils.LEXEME_VERB == predicatorType && nestedNet.getMainNode() != null) {
                            LinguisticNode firstNode = new LinguisticNode(predicatorDictForm, WordType.getByIndex(predicatorType));
                            LinguisticNode secondNode = new LinguisticNode(nestedNet.getMainNode().getNode(), WordType.NOUN);
                            network.addLink(new LinguisticLink(firstNode, secondNode, LinguisticRelation.getRoleById(semRoleType), SemanticRelationType.role));

                            for(LinguisticLink link : thesaurusLinksLoader.getThesaurusLinks(firstNode)){
                                network.addLink(link);
                            }
                            for(LinguisticLink link : thesaurusLinksLoader.getThesaurusLinks(secondNode)){
                                network.addLink(link);
                            }
                        }
                    }
                    hasNext = Dispatch.call(semRoleIterator.toDispatch(), "Next");
                } while (hasNext != null && (Boolean) hasNext.toJavaObject());
            }
        } catch (Exception e) {
            logger.error("Error during sentences extracting", e);
        }
    }

    private void analyzePartNouns(LinguisticWordNet network) {
        try {
            Variant semanticIterator = Dispatch.get(semantics, "Begin");
            if (semanticIterator != null) {
                Variant hasNext;
                do {
                    Variant semanteme = Dispatch.get(semanticIterator.toDispatch(), "Value");
                    if (semanteme != null) {
                        int semantemeType = (Integer) Dispatch.get(semanteme.toDispatch(), "Type").toJavaObject();

                        Dispatch firstSyntaxeme = Dispatch.get(semanteme.toDispatch(), "FirstSyntaxeme").toDispatch();
                        Dispatch firstWordIterator = Dispatch.get(firstSyntaxeme, "WordIterator").toDispatch();
                        Dispatch firstWord = Dispatch.get(firstWordIterator, "Value").toDispatch();
                        Dispatch firstLexeme = Dispatch.get(firstWord, "Lexeme").toDispatch();
                        String firstDictForm = (String) Dispatch.get(firstLexeme, "DictForm").toJavaObject();
                        int firstType = (Integer) Dispatch.get(firstLexeme, "Type").toJavaObject();
                        int firstSemClass = (Integer) Dispatch.get(firstLexeme, "SemClass").toJavaObject();
                        if (firstType == LinguisticUtils.LEXEME_PREPOSITION) {
                            if ((Boolean) Dispatch.call(firstWordIterator, "Next").toJavaObject()) {
                                firstWord = Dispatch.get(firstWordIterator, "Value").toDispatch();
                                firstLexeme = Dispatch.get(firstWord, "Lexeme").toDispatch();
                                firstDictForm = (String) Dispatch.get(firstLexeme, "DictForm").toJavaObject();
                                firstType = (Integer) Dispatch.get(firstLexeme, "Type").toJavaObject();
                                firstSemClass = (Integer) Dispatch.get(firstLexeme, "SemClass").toJavaObject();
                            }
                        }

                        Dispatch secondSyntaxeme = Dispatch.get(semanteme.toDispatch(), "SecondSyntaxeme").toDispatch();
                        Dispatch secondWordIterator = Dispatch.get(secondSyntaxeme, "WordIterator").toDispatch();
                        Dispatch secondWord = Dispatch.get(secondWordIterator, "Value").toDispatch();
                        Dispatch secondLexeme = Dispatch.get(secondWord, "Lexeme").toDispatch();
                        String secondDictForm = (String) Dispatch.get(secondLexeme, "DictForm").toJavaObject();
                        int secondType = (Integer) Dispatch.get(secondLexeme, "Type").toJavaObject();
                        int secondSemClass = (Integer) Dispatch.get(secondLexeme, "SemClass").toJavaObject();
                        if (secondType == LinguisticUtils.LEXEME_PREPOSITION) {
                            if ((Boolean) Dispatch.call(secondWordIterator, "Next").toJavaObject()) {
                                secondWord = Dispatch.get(secondWordIterator, "Value").toDispatch();
                                secondLexeme = Dispatch.get(secondWord, "Lexeme").toDispatch();
                                secondDictForm = (String) Dispatch.get(secondLexeme, "DictForm").toJavaObject();
                                secondType = (Integer) Dispatch.get(secondLexeme, "Type").toJavaObject();
                                secondSemClass = (Integer) Dispatch.get(secondLexeme, "SemClass").toJavaObject();
                            }
                        }

                        if (!LinguisticUtils.LEXEME_NONSIGNED.contains(firstType) && !LinguisticUtils.LEXEME_NONSIGNED.contains(secondType)) {
                            LinguisticNode firstNode = new LinguisticNode(firstDictForm, WordType.getByIndex(firstType));
                            LinguisticNode secondNode = new LinguisticNode(secondDictForm, WordType.getByIndex(secondType));
                            network.addLink(new LinguisticLink(firstNode, secondNode, LinguisticRelation.getRelationById(semantemeType), SemanticRelationType.relation));
                        }
                    }
                    hasNext = Dispatch.call(semanticIterator.toDispatch(), "Next");
                }
                while (hasNext != null && (Boolean) hasNext.toJavaObject());

            }
        } catch (Exception e) {
            logger.error("Error during sentences extracting", e);
        }
    }

    private void analyzeStructure() {
        Dispatch sentenceIterator = Dispatch.get(syntax, "Begin").toDispatch();
        do {
            Dispatch sentence = Dispatch.get(sentenceIterator, "Value").toDispatch();
            int sentenceSize = (Integer) Dispatch.get(sentence, "Size").toJavaObject();
            Dispatch variantIterator = Dispatch.get(sentence, "Begin").toDispatch();
            do {
                Dispatch variant = Dispatch.get(variantIterator, "Value").toDispatch();
                int variantSize = (Integer) Dispatch.get(variant, "Size").toJavaObject();
                Dispatch syntaxemeIterator = Dispatch.get(variant, "SyntaxemeBegin").toDispatch();
                do {
                    Dispatch syntaxeme = Dispatch.get(syntaxemeIterator, "Value").toDispatch();
                    Dispatch synWord = Dispatch.get(syntaxeme, "Word").toDispatch();
                    int type = (Integer) Dispatch.get(syntaxeme, "Type").toJavaObject();
                    Dispatch wordLexeme = Dispatch.get(synWord, "Lexeme").toDispatch();
                    String wordDictForm = (String) Dispatch.get(wordLexeme, "DictForm").toJavaObject();
                    int wordType = (Integer) Dispatch.get(wordLexeme, "Type").toJavaObject();
                    Dispatch synWordIterator = Dispatch.get(synWord, "Begin").toDispatch();
                    Variant hasNext;
                    do {
                        Variant synWordVar = Dispatch.get(synWordIterator, "Value");
                        if (synWordVar != null) {
                            synWord = synWordVar.toDispatch();
                            wordLexeme = Dispatch.get(synWord, "Lexeme").toDispatch();
                            wordDictForm = (String) Dispatch.get(wordLexeme, "DictForm").toJavaObject();
                            wordType = (Integer) Dispatch.get(wordLexeme, "Type").toJavaObject();
                        }
                        hasNext = Dispatch.call(synWordIterator, "Next");
                    } while (hasNext != null && (Boolean) hasNext.toJavaObject());
                } while ((Boolean) Dispatch.call(syntaxemeIterator, "Next").toJavaObject());
            } while ((Boolean) Dispatch.call(variantIterator, "Next").toJavaObject());
        } while ((Boolean) Dispatch.call(sentenceIterator, "Next").toJavaObject());
    }

    private LinguisticWordNet analyzeSynWord(Dispatch synWord) {
        LinguisticWordNet network = new LinguisticWordNet();

        Dispatch wordLexeme = Dispatch.get(synWord, "Lexeme").toDispatch();
        String wordDictForm = (String) Dispatch.get(wordLexeme, "DictForm").toJavaObject();
        int wordType = (Integer) Dispatch.get(wordLexeme, "Type").toJavaObject();

        LinguisticNode firstNode = null;
        if (wordType == LinguisticUtils.LEXEME_NOUN) {
            firstNode = new LinguisticNode(wordDictForm, WordType.NOUN);
            network.setMainNode(firstNode);
        }

        Dispatch synWordIterator = Dispatch.get(synWord, "Begin").toDispatch();
        Variant hasNext;
        do {
            Variant synWordVar = Dispatch.get(synWordIterator, "Value");
            if (synWordVar != null) {
                Dispatch synWordNested = synWordVar.toDispatch();
                Dispatch wordLexemeNested = Dispatch.get(synWordNested, "Lexeme").toDispatch();
                String wordDictFormNested = (String) Dispatch.get(wordLexemeNested, "DictForm").toJavaObject();
                int wordTypeNested = (Integer) Dispatch.get(wordLexemeNested, "Type").toJavaObject();
                if (firstNode != null &&
                        wordTypeNested == LinguisticUtils.LEXEME_ADJECTIVE || wordTypeNested == LinguisticUtils.LEXEME_NUMERAL) {
                    LinguisticNode secondNode = new LinguisticNode(wordDictFormNested, WordType.ADJECTIVE);
                    network.addLink(new LinguisticLink(firstNode, secondNode, "свойство", SemanticRelationType.property));
                }
                if (firstNode != null &&
                        wordTypeNested == LinguisticUtils.LEXEME_NOUN) {
                    LinguisticNode secondNode = new LinguisticNode(wordDictFormNested, WordType.NOUN);
                    network.addLink(new LinguisticLink(firstNode, secondNode, "принадлежность", SemanticRelationType.belong));
                }
                LinguisticWordNet nestedNet = analyzeSynWord(synWordNested);
                network.injection(nestedNet);
            }
            hasNext = Dispatch.call(synWordIterator, "Next");
        } while (hasNext != null && (Boolean) hasNext.toJavaObject());

        return network;
    }

    @Override
    public LinguisticWordNet collectMainResult(LinguisticWordNet partResult, LinguisticWordNet currentResult) {
        if (currentResult == null) {
            currentResult = new LinguisticWordNet();
        }
        currentResult.injection(partResult);
        return currentResult;
    }

}
