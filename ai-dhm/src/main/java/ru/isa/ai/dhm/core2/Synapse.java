package ru.isa.ai.dhm.core2;

import java.util.Random;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Synapse {
    private int inputSource;
    private double permanence;

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
     * inactive synapse is decremented in each updateRelations step.
     */
    private double permanenceDec = 0.01;

    private double initConnectedPct = 0.5;
    private double permTrimThreshold;
    private double stimulusInc;

    public Synapse(int sourceIndex) {
        this.inputSource = sourceIndex;
        this.permTrimThreshold = permanenceInc / 2.0;
        this.stimulusInc = permConnected / 10.0;
    }

    public Synapse(int sourceIndex, double initPermanence) {
        this.inputSource = sourceIndex;
        this.permTrimThreshold = permanenceInc / 2.0;
        this.stimulusInc = permConnected / 10.0;
        this.permanence = initPermanence;
    }

    /**
     * Случайные значения преманентности должны быть из малого диапазона около connectedPerm
     */
    public void initPermanence() {
        if (random.nextDouble() <= initConnectedPct)
            permanence = permConnected + random.nextDouble() * permanenceInc / 4.0;
        else
            permanence = permConnected - random.nextDouble() * permanenceInc / 4.0;
    }

    public void stimulatePermanence() {
        permanence += stimulusInc;
        permanence = permanence > 1 ? 1 : permanence;
    }

    public void increasePermanence() {
        permanence += permanenceInc;
        permanence = permanence > 1 ? 1 : permanence;
    }

    public void decreasePermanence() {
        permanence -= permanenceDec;
        permanence = permanence < 0 ? 0 : permanence;
    }

    public boolean isConnected() {
        return permanence > permConnected;
    }

    public int getInputSource() {
        return inputSource;
    }

    public double getPermanence() {
        return permanence;
    }

    public void setPermanence(double permanence) {
        this.permanence = permanence;
    }

}
