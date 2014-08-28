package ru.isa.ai.dhm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 12:02
 */
public final class RegionSettings {
    Logger logger = LogManager.getLogger(RegionSettings.class);

    //group of default values for properties
    public static final double DESIRED_LOCAL_ACTIVITY_DEFAULT = 20.0;
    public static final double MINIMAL_OVERLAP_DEFAULT = 50.0;
    public static final double CONNECTED_PERMISSION_DEFAULT = 0.2;
    public static final double PERMANENCE_INC_DEFAULT = 0.1;
    public static final double PERMANENCE_DEC_DEFAULT = 0.1;
    public static final double CELLS_PER_COLUMN_DEFAULT = 4.0;
    public static final double ACTIVATION_THRESHOLD_DEFAULT = 10.0;
    public static final double INITIAL_PERMANENCE_DEFAULT = 0.1;
    public static final double MINIMAL_THRESHOLD_DEFAULT = 4.0;
    public static final double NEW_SYNAPSES_COUNT_DEFAULT = 30.0;
    public static final double REGION_X_DIMENSION_DEFAULT = 20.0;
    public static final double REGION_Y_DIMENSION_DEFAULT = 10.0;

    public double[] initialParameters;

    public RegionSettings() {
        initialParameters = new double[]{DESIRED_LOCAL_ACTIVITY_DEFAULT,
                MINIMAL_OVERLAP_DEFAULT,
                CONNECTED_PERMISSION_DEFAULT,
                PERMANENCE_INC_DEFAULT,
                PERMANENCE_DEC_DEFAULT,
                CELLS_PER_COLUMN_DEFAULT,
                ACTIVATION_THRESHOLD_DEFAULT,
                INITIAL_PERMANENCE_DEFAULT,
                MINIMAL_THRESHOLD_DEFAULT,
                NEW_SYNAPSES_COUNT_DEFAULT,
                REGION_X_DIMENSION_DEFAULT,
                REGION_Y_DIMENSION_DEFAULT};
    }
    
    public void saveIntoFile(String filePropName) throws RegionSettingsException {
        Properties properties = new Properties();
        try {
            properties.setProperty("desiredLocalActivity", String.valueOf(initialParameters[0]));
            properties.setProperty("minOverlap", String.valueOf(initialParameters[1]));
            properties.setProperty("connectedPerm", String.valueOf(initialParameters[2]));
            properties.setProperty("permanenceInc", String.valueOf(initialParameters[3]));
            properties.setProperty("permanenceDec", String.valueOf(initialParameters[4]));
            properties.setProperty("cellsPerColumn", String.valueOf(initialParameters[5]));
            properties.setProperty("activationThreshold", String.valueOf(initialParameters[6]));
            properties.setProperty("initialPerm", String.valueOf(initialParameters[7]));
            properties.setProperty("minThreshold", String.valueOf(initialParameters[8]));
            properties.setProperty("newSynapseCount", String.valueOf(initialParameters[9]));
            properties.setProperty("xDimension", String.valueOf(initialParameters[10]));
            properties.setProperty("yDimension", String.valueOf(initialParameters[11]));

            FileOutputStream output = new FileOutputStream(filePropName);
            properties.store(output, "Saved settings");
            output.close();

        } catch (IOException e) {
            throw new RegionSettingsException("Cannot save properties file " + filePropName, e);
        }
    }
    
    public void loadFromFile(String filePropName) throws RegionSettingsException {
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(filePropName);
            properties.load(input);
            for (String name : properties.stringPropertyNames()) {
                switch (name) {
                    case "desiredLocalActivity":
                        initialParameters[0] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "minOverlap":
                        initialParameters[1] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "connectedPerm":
                        initialParameters[2] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "permanenceInc":
                        initialParameters[3] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "permanenceDec":
                        initialParameters[4] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "cellsPerColumn":
                        initialParameters[5] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "activationThreshold":
                        initialParameters[6] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "initialPerm":
                        initialParameters[7] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "minThreshold":
                        initialParameters[8] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "newSynapseCount":
                        initialParameters[9] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "xDimension":
                        initialParameters[10] = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "yDimension":
                        initialParameters[11] = Double.parseDouble(properties.getProperty(name));
                        break;
                    default:
                        logger.error("Illegal property name: " + name);
                        break;
                }
            }
            input.close();
        } catch (IOException e) {
            throw new RegionSettingsException("Cannot load properties file " + filePropName, e);
        } catch (NumberFormatException nfe) {
            throw new RegionSettingsException("Wrong property value in property file " + filePropName, nfe);
        }
    }
}
