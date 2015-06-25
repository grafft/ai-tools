package ru.isa.ai.ourhtm.structure;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by APetrov on 14.05.2015.
 */
public class HTMSettings {
    static public boolean debug=true;
    public int historyDeep=2;
    public int xDimension = 30; // ширина региона (в колонках)
    public int yDimension = 30; // высота региона (в колонках)
    public int xInput = 20;//100; // ширина входного слоя (в сигналах)
    public int yInput = 10;//100; // высота входного слоя (в сигналах)
    /**
     * This parameter deteremines the extent of the
     * input that each column can potentially be connected to. This
     * can be thought of as the input bits that are visible to each
     * column, or a 'receptive field' of the field of vision. A large
     * enough value will result in global coverage, meaning
     * that each column can potentially be connected to every input
     * bit. This parameter defines a square (or hyper square) area: a
     * column will have a max square potential pool with sides of
     * length (2 * potentialRadius + 1).
     */
    public int potentialRadius = 16;      // TODO: сделать в зависимотси от размеров? InputWidth / ColonNumsOnXAxis
    /**
     * Число клеток в каждой из колонок.
     */
    public int cellsPerColumn = 4;
    /**
     * Максимальное значение синапсов, добавляемых сегменту при обучении.
     */
    public int newSynapseCount = 10;

    /**
     *  Параметр, контролирующий число колонок победителей.
     */
    public int desiredLocalActivity = 2; // TODO должен быть меньше чем число колонок
    /**
     * Минимальное число активных входов колонки для ее участия в шаге подавления.
     */
    public int minOverlap = 0;
    /**
     * Если значение перманентности синапса больше этого значения, то он ситается подключенным.
     */
    public double connectedPerm = 0.1;
    /**
     * Количество значений перманентности синапсов, которые были увелечины при обучении.
     */
    public double permanenceInc = 0.1;
    /**
     * Количество значений перманентности синапсов, которые были уменьшены при обучении.
     */
    public double permanenceDec = 0.01;
    /**
     * Порог активации для сегмента. Если число активных подключенных синапсов большем этого значения, данный
     * сегмент считается активным.
     */
    public double activationThreshold = 10.0;
    /**
     * Начальное значение перманентности для синапсов.
     */
    public double initialPerm = 0.1;
    /**
     * Минимальное число активных синапсов для сегмент при поиске лучшего
     */
    public double minThreshold = 4.0;

    /**
     * The percent of the inputs, within a column's
     * potential radius, that a column can be connected to. If set to
     * 1, the column will be connected to every input within its
     * potential radius. This parameter is used to give each column a
     * unique potential pool when a large potentialRadius causes
     * overlap between the columns. At initialization time we choose
     * ((2*potentialRadius + 1)^(# inputDimensions) * connectedPct)
     * input bits to comprise the column's potential pool.
     */
    public double connectedPct = 1;

    /**
     * Длина периода подсчета рабочих циклов
     */
    public int dutyCyclePeriod = 1000;
    public double maxBoost = 10.0;
    public double initConnectedPct = 0.5;
    public int initialInhibitionRadius = 10;

    public void saveIntoFile(String filePropName) throws Exception {
        Properties properties = new Properties();
        try {

            properties.setProperty("connectedPct", String.valueOf(connectedPct));
            properties.setProperty("historyDeep", String.valueOf(historyDeep));
            properties.setProperty("debug", String.valueOf(debug));
            properties.setProperty("xInput", String.valueOf(xInput));
            properties.setProperty("yInput", String.valueOf(yInput));
            properties.setProperty("xDimension", String.valueOf(xDimension));
            properties.setProperty("yDimension", String.valueOf(yDimension));
            properties.setProperty("initialInhibitionRadius", String.valueOf(initialInhibitionRadius));
            properties.setProperty("potentialRadius", String.valueOf(potentialRadius));

            properties.setProperty("cellsPerColumn", String.valueOf(cellsPerColumn));
            properties.setProperty("newSynapseCount", String.valueOf(newSynapseCount));

            properties.setProperty("desiredLocalActivity", String.valueOf(desiredLocalActivity));
            properties.setProperty("minOverlap", String.valueOf(minOverlap));
            properties.setProperty("connectedPerm", String.valueOf(connectedPerm));
            properties.setProperty("permanenceInc", String.valueOf(permanenceInc));
            properties.setProperty("permanenceDec", String.valueOf(permanenceDec));
            properties.setProperty("activationThreshold", String.valueOf(activationThreshold));
            properties.setProperty("initialPerm", String.valueOf(initialPerm));
            properties.setProperty("minThreshold", String.valueOf(minThreshold));

            FileOutputStream output = new FileOutputStream(filePropName);
            properties.store(output, "Saved set");
            output.close();

        } catch (IOException e) {
            throw new Exception("Cannot save properties file " + filePropName, e);
        }
    }

    public static HTMSettings loadFromFile(String filePropName) throws Exception {
        Properties properties = new Properties();
        HTMSettings settings = new HTMSettings();
        try {
            FileInputStream input = new FileInputStream(filePropName);
            properties.load(input);
            for (String name : properties.stringPropertyNames()) {
                switch (name) {

                    case "connectedPct":
                        settings.connectedPct = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "historyDeep":
                        settings.historyDeep = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "debug":
                        settings.debug = Boolean.parseBoolean(properties.getProperty(name));
                        break;
                    case "xInput":
                        settings.xInput = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "yInput":
                        settings.yInput = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "xDimension":
                        settings.xDimension = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "yDimension":
                        settings.yDimension = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "initialInhibitionRadius":
                        settings.initialInhibitionRadius = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "potentialRadius":
                        settings.potentialRadius = Integer.parseInt(properties.getProperty(name));
                        break;

                    case "cellsPerColumn":
                        settings.cellsPerColumn = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "newSynapseCount":
                        settings.newSynapseCount = Integer.parseInt(properties.getProperty(name));
                        break;

                    case "desiredLocalActivity":
                        settings.desiredLocalActivity = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "minOverlap":
                        settings.minOverlap = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "connectedPerm":
                        settings.connectedPerm = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "permanenceInc":
                        settings.permanenceInc = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "permanenceDec":
                        settings.permanenceDec = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "activationThreshold":
                        settings.activationThreshold = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "initialPerm":
                        settings.initialPerm = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "minThreshold":
                        settings.minThreshold = Double.parseDouble(properties.getProperty(name));
                        break;
                    default:
                        throw new Exception("Cannot load properties file " + filePropName);
                }
            }
            input.close();
            return settings;
        } catch (IOException e) {
            throw new Exception("Cannot load properties file " + filePropName, e);
        } catch (NumberFormatException nfe) {
            throw new Exception("Wrong property value in property file " + filePropName+". "+nfe.getLocalizedMessage(), nfe);
        }
    }

    public static HTMSettings getDefaultSettings(){
        HTMSettings.debug=false;
        return new HTMSettings();
    }
}
