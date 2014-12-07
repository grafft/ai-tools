package ru.isa.ai.dhm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 12:02
 */
// настройки региона
public final class DHMSettings {
    public boolean debug=false;
    public int xDimension = 30; // ширина региона (в колонках)
    public int yDimension = 30; // высота региона (в колонках)
    public int xInput = 20;//100; // ширина входного слоя (в сигналах)
    public int yInput = 10;//100; // высота входного слоя (в сигналах)

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
    public int desiredLocalActivity = 10;
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
     * The percent of the inputs, within a column's
     * potential radius, that a column can be connected to. If set to
     * 1, the column will be connected to every input within its
     * potential radius. This parameter is used to give each column a
     * unique potential pool when a large potentialRadius causes
     * overlap between the columns. At initialization time we choose
     * ((2*potentialRadius + 1)^(# inputDimensions) * connectedPct)
     * input bits to comprise the column's potential pool.
     */
    public double connectedPct = 0.5;

    /**
     * Длина периода подсчета рабочих циклов
     */
    public int dutyCyclePeriod = 1000;
    public double maxBoost = 10.0;
    public double initConnectedPct = 0.5;
    public double stimulusInc;                  //TODO: негде не загружается!
    public int initialInhibitionRadius = 10;

    public void saveIntoFile(String filePropName) throws RegionSettingsException {
        Properties properties = new Properties();
        try {
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
            properties.store(output, "Saved settings");
            output.close();

        } catch (IOException e) {
            throw new RegionSettingsException("Cannot save properties file " + filePropName, e);
        }
    }

    public static DHMSettings loadFromFile(String filePropName) throws RegionSettingsException {
        Properties properties = new Properties();
        DHMSettings settings = new DHMSettings();
        try {
            FileInputStream input = new FileInputStream(filePropName);
            properties.load(input);
            for (String name : properties.stringPropertyNames()) {
                switch (name) {
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
                        throw new RegionSettingsException("Cannot load properties file " + filePropName);
                }
            }
            input.close();
            return settings;
        } catch (IOException e) {
            throw new RegionSettingsException("Cannot load properties file " + filePropName, e);
        } catch (NumberFormatException nfe) {
            throw new RegionSettingsException("Wrong property value in property file " + filePropName, nfe);
        }
    }

    public static DHMSettings getDefaultSettings(){
        return new DHMSettings();
    }
}
