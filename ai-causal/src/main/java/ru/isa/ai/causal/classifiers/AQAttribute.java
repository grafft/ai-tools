package ru.isa.ai.causal.classifiers;

import weka.core.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 30.07.13
 * Time: 15:17
 */
public class AQAttribute {
    private String name;
    private int id;
    private List<Float> cutPoints = new ArrayList<>();
    private float upLimit;
    private float downLimit;

    public AQAttribute() {
    }

    public AQAttribute(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Float> getCutPoints() {
        return cutPoints;
    }

    public void setCutPoints(List<Float> cutPoints) {
        this.cutPoints = cutPoints;
    }

    public float getUpLimit() {
        return upLimit;
    }

    public void setUpLimit(float upLimit) {
        this.upLimit = upLimit;
    }

    public float getDownLimit() {
        return downLimit;
    }

    public void setDownLimit(float downLimit) {
        this.downLimit = downLimit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
