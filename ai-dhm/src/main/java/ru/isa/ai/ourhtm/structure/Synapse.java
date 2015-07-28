package ru.isa.ai.ourhtm.structure;

import java.util.Random;

/**
 * Created by APetrov on 14.05.2015.
 */
public class Synapse {
    private HTMSettings settings;
    private int indexConnectTo;
    private double permanence;

    private Random random = new Random();


    public Synapse(HTMSettings settings, int indexConnectTo) {
        this.settings = settings;
        this.indexConnectTo = indexConnectTo;
    }

    public Synapse(HTMSettings settings, int connectToIndex, double initPermanence) {
        this(settings, connectToIndex);
        this.permanence = initPermanence;
    }

    /**
     * Случайные значения преманентности должны быть из малого диапазона около connectedPerm
     */
    public void initPermanence(double k) {
        if(settings.debug==true) {
            permanence = settings.connectedPerm;
           // if(connectToIndex %2==0)
           //     permanence = settings.connectedPerm + 0.5*settings.permanenceInc / 4.0;
           // else
           //     permanence = settings.connectedPerm - 0.5*settings.permanenceInc / 4.0;
            permanence=permanence*(1/(k==0?0.5:k));

        }
        else {
            if (random.nextDouble() <= settings.initConnectedPct)
                permanence = settings.connectedPerm + random.nextDouble() * settings.permanenceInc / 4.0;
            else
                permanence = settings.connectedPerm - random.nextDouble() * settings.permanenceInc / 4.0;
            permanence = permanence *(1/(k==0?0.5:k));
        }
    }



    public void increasePermanence() {
        permanence += settings.permanenceInc;
        permanence = permanence > 1 ? 1 : permanence;
    }

    public void decreasePermanence() {
        permanence -= settings.permanenceDec;
        permanence = permanence < 0 ? 0 : permanence;
    }

    public boolean isConnected() {
        return true;//permanence > settings.connectedPerm;
    }

    /* connectToIndex - это либо номер бита из сигнала снизу, либо номер клетки (аксон) при латеральной связи */
    public int getIndexConnectTo() {
        return indexConnectTo;
    }

    /* Получить степени связанности между аксоном и дендритом. */
    public double getPermanence() {
        return permanence;
    }

    /* Установить степени связанности между аксоном и дендритом. */
    public void setPermanence(double permanence) {
        this.permanence = permanence;
    }
}
