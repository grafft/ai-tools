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
public final class RegionSettings {
    public int numInputs = 200;

    public int numColumns = 100;
    public int cellsPerColumn = 4;
    public int newSynapseCount = 10;

    public int desiredLocalActivity = 20;
    public int minOverlap = 50;
    public double connectedPerm = 0.2;
    public double permanenceInc = 0.1;
    public double permanenceDec = 0.1;
    public int activationThreshold = 10;
    public double initialPerm = 0.1;
    public int minThreshold = 4;

    public void saveIntoFile(String filePropName) throws RegionSettingsException {
        Properties properties = new Properties();
        try {
            properties.setProperty("xDimension", String.valueOf(numInputs));
            properties.setProperty("yDimension", String.valueOf(numColumns));
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

    public static RegionSettings loadFromFile(String filePropName) throws RegionSettingsException {
        Properties properties = new Properties();
        RegionSettings settings = new RegionSettings();
        try {
            FileInputStream input = new FileInputStream(filePropName);
            properties.load(input);
            for (String name : properties.stringPropertyNames()) {
                switch (name) {
                    case "numInputs":
                        settings.numInputs = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "numColumns":
                        settings.numColumns = Integer.parseInt(properties.getProperty(name));
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
                        settings.activationThreshold = Integer.parseInt(properties.getProperty(name));
                        break;
                    case "initialPerm":
                        settings.initialPerm = Double.parseDouble(properties.getProperty(name));
                        break;
                    case "minThreshold":
                        settings.minThreshold = Integer.parseInt(properties.getProperty(name));
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
}
