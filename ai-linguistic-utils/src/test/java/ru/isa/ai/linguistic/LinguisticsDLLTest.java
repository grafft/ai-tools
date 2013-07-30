package ru.isa.ai.linguistic;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import ru.isa.ai.linguistic.utils.LinguisticUtils;

/**
 * Author: Aleksandr Panov
 * Date: 09.06.12
 * Time: 17:09
 */
public class LinguisticsDLLTest {
    public static void main(String[] args) {
        ActiveXComponent lexics = new ActiveXComponent("PFU.Lexics");
        ActiveXComponent syntax = new ActiveXComponent("PFU.Syntax");
        ActiveXComponent semantics = new ActiveXComponent("PFU.Semantics");
        ActiveXComponent roleSemantics = new ActiveXComponent("PFU.RoleSemantics");
        ActiveXComponent semanticParser = new ActiveXComponent("PFU.SemanticParser");

        try {
            Dispatch.call(semanticParser, "Text2Sem", "Мама вымыла раму", lexics, syntax, semantics, roleSemantics);

            Variant iterator = Dispatch.get(syntax, "Begin");

            if (iterator != null) {
                do {
                    Variant sentence = Dispatch.get(iterator.toDispatch(), "Value");
                    if (sentence != null) {
                        Variant tmp1 = Dispatch.get(sentence.toDispatch(), "Begin");
                        if (tmp1 != null) {
                            Dispatch tmp2 = Dispatch.get(tmp1.toDispatch(), "Value").toDispatch();
                            Dispatch wordIterator = Dispatch.get(tmp2, "Begin").toDispatch();

                            do {
                                Dispatch word = Dispatch.get(wordIterator, "Value").toDispatch();
                                Dispatch lexeme = Dispatch.get(word, "Lexeme").toDispatch();
                                String dictForm = (String) Dispatch.get(lexeme, "DictForm").toJavaObject();
                                int type = (Integer) Dispatch.get(lexeme, "Type").toJavaObject();
                                if (type != LinguisticUtils.LEXEME_PREPOSITION) {
                                    System.out.println(dictForm);
                                }
                            } while ((Boolean) Dispatch.call(wordIterator, "Next").toJavaObject());
                        }
                    }
                } while ((Boolean) Dispatch.call(iterator.toDispatch(), "Next").toJavaObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
