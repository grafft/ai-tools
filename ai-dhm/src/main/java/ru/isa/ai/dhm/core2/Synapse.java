package ru.isa.ai.dhm.core2;

import java.util.Random;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Synapse {
    private int index;
    private int columnIndex;

    private double permanence;
    private int sourceInput;

    private Random random = new Random();
    /**
     * The default connected threshold. Any synapse
     * whose permanence value is above the connected threshold is
     * a "connected synapse", meaning it can contribute to
     * the cell's firing.
     */
    private double permConnected = 0.1;

    /**
     * The amount by which the permanence of an
     * active synapse is incremented in each round.
     */
    private double permanenceInc = 0.1;
    /**
     * The amount by which the permanence of an
     * inactive synapse is decremented in each learning step.
     */
    private double permanenceDec = 0.01;

    private double initConnectedPct = 0.5;
    private double permTrimThreshold;
    private double permBelowStimulusInc;
    private double synPermMin = 0.0;
    private double synPermMax = 1.0;

    public Synapse(int index, int columnIndex) {
        this.index = index;
        this.columnIndex = columnIndex;
        this.permTrimThreshold = permanenceInc / 2.0;
        this.permBelowStimulusInc = permConnected / 10.0;
    }

    public void initPermanence() {
        double value;
        if (random.nextDouble() <= initConnectedPct)
            value = MathUtils.roundWithPrecision(permConnected + random.nextDouble() * permanenceInc / 4.0);
        else
            value = MathUtils.roundWithPrecision(permConnected * random.nextDouble());

        permanence = value < permTrimThreshold ? 0 : value;
    }

    public void stimulatePermanence() {
        permanence += permBelowStimulusInc;
    }

    public boolean isConnected() {
        return permanence > permConnected;
    }

    public void clip(boolean trim) {
        double minVal = trim ? permTrimThreshold : synPermMin;
        double value = permanence > synPermMax ? synPermMax : permanence;
        permanence =  value < minVal ? synPermMin : value;
    }

    public int getIndex() {
        return index;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public double getPermanence() {
        return permanence;
    }

    public void setPermanence(double permanence) {
        this.permanence = permanence;
    }

    public int getSourceInput() {
        return sourceInput;
    }

    public void setSourceInput(int sourceInput) {
        this.sourceInput = sourceInput;
    }
}
