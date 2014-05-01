package ru.isa.ai.causal.classifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Aleksandr Panov
 * Date: 30.07.13
 * Time: 15:17
 */
public class CRFeature {
    private String name;
    private List<Double> cutPoints = new ArrayList<>();
    private double upLimit;
    private double downLimit;

    public CRFeature() {
    }

    public CRFeature(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getCutPoints() {
        return cutPoints;
    }

    public void setCutPoints(List<Double> cutPoints) {
        this.cutPoints = cutPoints;
    }

    public double getUpLimit() {
        return upLimit;
    }

    public void setUpLimit(double upLimit) {
        this.upLimit = upLimit;
    }

    public double getDownLimit() {
        return downLimit;
    }

    public void setDownLimit(double downLimit) {
        this.downLimit = downLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CRFeature crFeature = (CRFeature) o;

        if (name != null ? !name.equals(crFeature.name) : crFeature.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
