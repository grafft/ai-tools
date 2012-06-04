package ru.isa.ai.tests;

/**
 * Author: Aleksandr Panov
 * Date: 25.05.12
 * Time: 14:29
 */
public interface AutomatonModel<S extends AutomatonState> {
    void tick();

    S calculateNextState(S currentState);

    int getStateCount();
}
