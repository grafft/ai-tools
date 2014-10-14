package ru.isa.ai.dhm.core;

import cern.colt.matrix.tbit.BitVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isa.ai.dhm.DHMSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:23
 */
public class Neocortex {
    private static final Logger logger = LogManager.getLogger(Neocortex.class);
    private List<Region> regions = new ArrayList<>();

    public void initialization() {
        logger.debug("Initialization");
        for (Region region : regions) {
            region.initialization();
        }
    }

    public void iterate(BitVector input) {
        logger.debug("Start neocortex iteration");
        BitVector newInput = input;
        for (Region region : regions) {
            newInput = region.forwardInputProcessing(newInput);
            region.updateActiveCells();
            // TODO P: Добавить вызов UpdateHistory?
            region.updatePredictiveCells();
            region.updateRelations();
        }
        logger.debug("End neocortex iteration");
    }

    public Region addRegion(DHMSettings settings, List<Region> children) {
        Region region = new Region(settings);
        if (children != null) {
            for (Region child : children)
                region.addChild(child);
        }
        regions.add(region);
        return region;
    }

    public List<Region> getRegions() {
        return regions;
    }
}
