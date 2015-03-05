package ru.isa.ai.dhm.core;

import cern.colt.matrix.tbit.BitVector;
import cern.colt.matrix.tint.IntMatrix1D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isa.ai.dhm.DHMSettings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.lang.reflect.Method;

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
            region.updateHistory();
            // TODO P: Добавить вызов UpdateHistory?

            region.updatePredictiveCells();
            region.updateRelations();


        }
        logger.debug("End neocortex iteration");


    }

    /*
    public Region addRegion(DHMSettings settings, List<Region> children) {
        Region region = new Region(settings);
        if (children != null) {
            for (Region child : children)
                region.addChild(child);
        }
        regions.add(region);
        return region;
    }
*/

    public Region addRegion(int ID, DHMSettings settings, Region itsParent) {
        Region region = new Region(ID, settings);
        if (itsParent != null) {
           itsParent.addChild(region);
        }
        regions.add(region);
        return region;
    }

    public Region getRoot(){
        int i=0, k =0;
        boolean fl = false;
        while (i < regions.size() && !fl){
            if (regions.get(i).getParent() == null)
            { fl = true; k = i; }
            i++;
        }
        return regions.get(k).getParent();
    }

    public int getMaxHeight(Region root){
        // додумать поиск макс высоты и ширины дерева

        /*List<Integer> heights = new LinkedList<>();
        инициализ
        if (root != null){
            for(int i = 0; i < root.getChildRegions().size(); i++){
                heights.a =
            }
        }*/
        return 1;
    }

    public List<Region> getRegions() {
        return regions;
    }
}
