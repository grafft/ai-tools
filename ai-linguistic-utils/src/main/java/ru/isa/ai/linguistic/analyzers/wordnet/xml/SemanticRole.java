package ru.isa.ai.linguistic.analyzers.wordnet.xml;

import javax.xml.bind.annotation.*;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 14:02
 */
@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.FIELD)
public class SemanticRole {
    @XmlAttribute(required = true)
    private Long id;
    @XmlValue
    private String name;

    public SemanticRole() {
    }

    public SemanticRole(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
