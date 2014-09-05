package ru.isa.ai.dhm.core;

import cern.colt.matrix.tbit.BitVector;
import ru.isa.ai.dhm.DHMSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:23
 */
public class Neocortex {
    private List<Region> regions = new ArrayList<>();

    public void initialization() {
        for (Region region : regions) {
            region.initialization();
        }
    }

    public void iterate(BitVector input) {
        BitVector newInput = input;
        for (Region region : regions) {
            newInput = region.forwardInputProcessing(newInput);
            region.updateActiveCells();
            region.updatePredictiveCells();
            region.updateRelations();
        }
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
