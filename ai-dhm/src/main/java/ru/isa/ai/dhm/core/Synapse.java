package ru.isa.ai.dhm.core;

import ru.isa.ai.dhm.DHMSettings;

import java.util.Random;

/**
 * Author: Aleksandr Panov
 * Date: 28.08.2014
 * Time: 14:24
 */
public class Synapse {
    private DHMSettings settings;
    private int inputSource;
    private double permanence;

    private Random random = new Random();


    public Synapse(DHMSettings settings, int sourceIndex) {
        this.settings = settings;
        this.inputSource = sourceIndex;
    }

    public Synapse(DHMSettings settings, int sourceIndex, double initPermanence) {
        this(settings, sourceIndex);
        this.permanence = initPermanence;
    }

    /**
     * Случайные значения преманентности должны быть из малого диапазона около connectedPerm
     */
    public void initPermanence() {
       // if(settings.debug==true) {
            if(inputSource%2==0)
                permanence = settings.connectedPerm + 0.5*settings.permanenceInc / 4.0;
            else
                permanence = settings.connectedPerm - 0.5*settings.permanenceInc / 4.0;
          //  return;
      //  }

        if (random.nextDouble() <= settings.initConnectedPct)
            permanence = settings.connectedPerm + random.nextDouble() * settings.permanenceInc / 4.0;
        else
            permanence = settings.connectedPerm - random.nextDouble() * settings.permanenceInc / 4.0;
    }

    public void stimulatePermanence() {
        permanence += settings.stimulusInc;
        permanence = permanence > 1 ? 1 : permanence;
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
        return permanence > settings.connectedPerm;
    }

    /* inputSource - это либо номер бита из сигнала снизу, либо номер клетки (аксон) при латеральной связи */
    public int getInputSource() {
        return inputSource;
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
