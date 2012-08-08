package ru.isa.ai.ass;

import ru.isa.ai.linguistic.analyzers.wordnet.xml.RelationsUnmarshaller;
import ru.isa.ai.linguistic.analyzers.wordnet.xml.RoleUnmarshaller;
import ru.isa.ai.linguistic.analyzers.wordnet.xml.SemanticRelations;
import ru.isa.ai.linguistic.analyzers.wordnet.xml.SemanticRoles;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 14:18
 */
public class UnmarshallerTest {
    public static void main(String[] args) {
        RoleUnmarshaller unmarshaller = new RoleUnmarshaller();
        SemanticRoles roles = unmarshaller.loadRoles();
        assert roles != null;

        RelationsUnmarshaller unmarshaller2 = new RelationsUnmarshaller();
        SemanticRelations relations = unmarshaller2.loadRelations();
        assert relations != null;
    }
}
