package ru.isa.ai.linguistic.analyzers.wordnet.xml;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 13:59
 */
@XmlRootElement(namespace = "http://www.isa.ru/schema")
@XmlAccessorType(XmlAccessType.FIELD)
public class SemanticRoles {
    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<SemanticRole> roles = new LinkedList<>();

    public List<SemanticRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SemanticRole> roles) {
        this.roles = roles;
    }

    public void addRole(SemanticRole role) {
        roles.add(role);
    }
}
