package ru.isa.ai.dhm.core2;

import cern.colt.matrix.tbit.BitVector;

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
        for (Region region : regions) {
            BitVector activeColumns = region.spatialPooling(input);
            region.activeCalculation();
            region.predictiveCalculation();
            region.learning();
        }
    }

    public void addRegion(Region region) {
        regions.add(region);
    }
}
