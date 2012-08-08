package ru.isa.ai.linguistic.analyzers.wordnet.xml;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 14:29
 */
@XmlRootElement(namespace = "http://www.isa.ru/schema")
@XmlAccessorType(XmlAccessType.FIELD)
public class SemanticRelations {
    @XmlElementWrapper(name = "relations")
    @XmlElement(name = "relation")
    private List<SemanticRelation> relations = new LinkedList<>();

    public List<SemanticRelation> getRelations() {
        return relations;
    }

    public void setRelations(List<SemanticRelation> relations) {
        this.relations = relations;
    }

    public void addRelation(SemanticRelation relation) {
        relations.add(relation);

    }
}
