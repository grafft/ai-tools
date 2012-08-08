package ru.isa.ai.linguistic.analyzers.wordnet;

import ru.isa.ai.linguistic.analyzers.wordnet.xml.*;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 10:24
 */
public abstract class LinguisticRelation {
    private static SemanticRoles roles;
    private static SemanticRelations relations;

    static {
        RoleUnmarshaller roleUnmarshaller = new RoleUnmarshaller();
        roles = roleUnmarshaller.loadRoles();

        RelationsUnmarshaller relationsUnmarshaller = new RelationsUnmarshaller();
        relations = relationsUnmarshaller.loadRelations();
    }

    public static String getRoleById(int id) {
        for (SemanticRole role : roles.getRoles()) {
            if (role.getId().intValue() == id) return role.getName();
        }
        return null;
    }

    public static String getRelationById(int id) {
        for (SemanticRelation relation : relations.getRelations()) {
            if (relation.getId().intValue() == id) return relation.getName();
        }
        return null;
    }
}
