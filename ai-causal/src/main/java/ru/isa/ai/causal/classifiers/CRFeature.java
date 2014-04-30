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
    private int id;
    private List<Double> cutPoints = new ArrayList<>();
    private double upLimit;
    private double downLimit;

    public CRFeature() {
    }

    public CRFeature(String name, int id) {
        this.name = name;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CRFeature attribute = (CRFeature) o;

        if (id != attribute.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
