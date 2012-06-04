package ru.isa.ai.tests;

/**
 * Author: Aleksandr Panov
 * Date: 25.05.12
 * Time: 14:31
 */
public abstract class AutomatonState {
    protected int i;
    protected int j;

    protected AutomatonState() {
    }

    protected AutomatonState(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }
}
