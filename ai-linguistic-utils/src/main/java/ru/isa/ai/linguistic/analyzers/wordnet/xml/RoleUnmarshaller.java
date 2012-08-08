package ru.isa.ai.linguistic.analyzers.wordnet.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 14:11
 */
public class RoleUnmarshaller {
    private static final Class<?>[] serializable = new Class<?>[]{
            SemanticRoles.class, SemanticRole.class
    };

    private Unmarshaller unmarshaller;

    public RoleUnmarshaller() {
        try {
            JAXBContext context = JAXBContext.newInstance(serializable);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public SemanticRoles loadRoles() {
        try {
            InputStream stream = getClass().getResourceAsStream("/sem_roles.xml");
            return (SemanticRoles) unmarshaller.unmarshal(stream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }
}
