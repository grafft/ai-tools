package ru.isa.ai.dhm.core2;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:23
 */
public class Neocortex {
    private List<Region> regions = new ArrayList<>();

    public Neocortex() {
        for(Region region : regions){
            region.initialization();
        }
    }

    public void addRegion(Region region){
        regions.add(region);
    }
}
