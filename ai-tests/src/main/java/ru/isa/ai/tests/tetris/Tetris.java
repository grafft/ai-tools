package ru.isa.ai.tests.tetris;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: Aleksandr Panov
 * Date: 23.08.12
 * Time: 14:58
 */
public class Tetris {
    private static final Class<?>[] serializable = new Class<?>[]{
            TetrisProperties.class, TetrisFigure.class
    };
    private static Unmarshaller unmarshaller;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final Map<Long, Figure> figures = new HashMap<>();
                loadFigures(figures);

                new TetrisFrame(figures);
            }
        });

    }

    private static void loadFigures(Map<Long, Figure> figures) {
        try {
            JAXBContext context = JAXBContext.newInstance(serializable);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        try {
            InputStream stream = Tetris.class.getResourceAsStream("/tetris_properties.xml");
            TetrisProperties properties = (TetrisProperties) unmarshaller.unmarshal(stream);
            for (TetrisFigure figure : properties.figures) {
                String[] parts = figure.value.trim().split("\n");
                int ySize = parts.length;
                int xSize = parts[0].trim().length();
                byte[] form = new byte[ySize * xSize];
                for (int j = 0; j < ySize; j++) {
                    char[] chars = parts[j].trim().toCharArray();
                    for (int i = 0; i < xSize; i++) {
                        if (chars[i] == '+') {
                            form[xSize * j + i] = 1;
                        } else {
                            form[xSize * j + i] = 0;
                        }
                    }
                }
                figures.put(figure.name, new Figure(xSize, ySize, form));
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @XmlRootElement(namespace = "http://www.isa.ru/schema", name = "tetris")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class TetrisProperties {
        @XmlElementWrapper(name = "figures")
        @XmlElement(name = "figure")
        private List<TetrisFigure> figures = new LinkedList<>();

        public List<TetrisFigure> getFigures() {
            return figures;
        }

        public void setFigures(List<TetrisFigure> figures) {
            this.figures = figures;
        }
    }

    @XmlRootElement(name = "figure")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class TetrisFigure {
        @XmlAttribute(required = true)
        private Long name;
        @XmlValue
        private String value;

        public Long getName() {
            return name;
        }

        public void setName(Long name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
