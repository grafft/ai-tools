package ru.isa.ai.linguistic.analyzers.wordnet.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Author: Aleksandr Panov
 * Date: 08.08.12
 * Time: 14:40
 */
public class RelationsUnmarshaller {
    private static final Class<?>[] serializable = new Class<?>[]{
            SemanticRelations.class, SemanticRelation.class
    };

    private Unmarshaller unmarshaller;

    public RelationsUnmarshaller() {
        try {
            JAXBContext context = JAXBContext.newInstance(serializable);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public SemanticRelations loadRelations() {
        try {
            InputStream stream = getClass().getResourceAsStream("/sem_relations.xml");
            return (SemanticRelations) unmarshaller.unmarshal(stream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }
}
