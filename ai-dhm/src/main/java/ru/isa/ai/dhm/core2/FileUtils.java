package ru.isa.ai.dhm.core2;

import ru.isa.ai.dhm.RegionSettings;
import ru.isa.ai.dhm.RegionSettingsException;

import java.io.File;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:30
 */
public class FileUtils {
    public static final String PROPERTY_POSTFIX = ".properties";

    public static Neocortex loadFromPropFiles(String path) throws RegionSettingsException {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            Neocortex cortex = new Neocortex();
            for (File file : new File(path).listFiles()) {
                if (file.getName().contains(PROPERTY_POSTFIX)) {
                    try {
                        cortex.addRegion(new Region(RegionSettings.loadFromFile(file.getPath())));
                    } catch (RegionSettingsException e) {
                        e.printStackTrace();
                    }
                }
            }
            cortex.initialization();
            return cortex;
        } else {
            throw new RegionSettingsException("Cannot find directory " + path);
        }
    }
}
