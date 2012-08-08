package ru.isa.ai.linguistic.analyzers.wordnet;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.apache.log4j.Logger;
import ru.isa.ai.linguistic.analyzers.AbstractLinguisticAnalyzer;
import ru.isa.ai.linguistic.analyzers.LinguisticsAnalyzingException;
import ru.isa.ai.linguistic.utils.LinguisticUtils;

/**
 * Author: Aleksandr Panov
 * Date: 06.08.12
 * Time: 14:16
 */
public class WordNetAnalyzer extends AbstractLinguisticAnalyzer<LinguisticWordNet, LinguisticWordNet> {
    private static final Logger logger = Logger.getLogger(WordNetAnalyzer.class);

    @Override
    public LinguisticWordNet analyzePart(String textName) throws LinguisticsAnalyzingException {
        LinguisticWordNet network = new LinguisticWordNet();
        analyzePartVerbs(network);
        analyzePartNouns(network);
        return network;
    }

    private void analyzePartVerbs(LinguisticWordNet network) {
        try {
            Variant semRoleIterator = Dispatch.get(roleSemantics, "Begin");
            if (semRoleIterator != null) {
                Variant hasNext;
                do {
                    Variant semRole = Dispatch.get(semRoleIterator.toDispatch(), "Value");
                    if (semRole != null) {
                        int semRoleType = (Integer) Dispatch.get(semRole.toDispatch(), "Type").toJavaObject();
                        Dispatch predicator = Dispatch.get(semRole.toDispatch(), "Predicator").toDispatch();
                        Variant predicatorLexeme = Dispatch.get(predicator, "Lexeme");
                        if (predicatorLexeme != null) {
                            String predicatorDictForm = (String) Dispatch.get(predicatorLexeme.toDispatch(), "DictForm").toJavaObject();
                            int predicatorType = (Integer) Dispatch.get(predicatorLexeme.toDispatch(), "Type").toJavaObject();

                            Dispatch syntaxeme = Dispatch.get(semRole.toDispatch(), "Syntaxeme").toDispatch();
                            Dispatch synWordIterator = Dispatch.get(syntaxeme, "WordIterator").toDispatch();
                            Dispatch word = Dispatch.get(synWordIterator, "Value").toDispatch();
                            Dispatch wordLexeme = Dispatch.get(word, "Lexeme").toDispatch();
                            String wordDictForm = (String) Dispatch.get(wordLexeme, "DictForm").toJavaObject();
                            int wordType = (Integer) Dispatch.get(wordLexeme, "Type").toJavaObject();
                            if (wordType == LinguisticUtils.LEXEME_PREPOSITION) {
                                if ((Boolean) Dispatch.call(synWordIterator, "Next").toJavaObject()) {
                                    word = Dispatch.get(synWordIterator, "Value").toDispatch();
                                    wordLexeme = Dispatch.get(word, "Lexeme").toDispatch();
                                    wordDictForm = (String) Dispatch.get(wordLexeme, "DictForm").toJavaObject();
                                    wordType = (Integer) Dispatch.get(wordLexeme, "Type").toJavaObject();
                                }
                            }
                            if (LinguisticUtils.LEXEME_VERB == predicatorType && !LinguisticUtils.LEXEME_NONSIGNED.contains(wordType)) {
                                LinguisticNode firstNode = new LinguisticNode(predicatorDictForm, WordType.getByIndex(predicatorType));
                                LinguisticNode secondNode = new LinguisticNode(wordDictForm, WordType.getByIndex(wordType));
                                network.addLink(new LinguisticLink(firstNode, secondNode, LinguisticRelation.getRoleById(semRoleType), SemanticRelationType.role));
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

    @Override
    public LinguisticWordNet collectMainResult(LinguisticWordNet partResult, LinguisticWordNet currentResult) {
        if (currentResult == null) {
            currentResult = new LinguisticWordNet();
        }
        currentResult.injection(partResult);
        return currentResult;
    }

}
