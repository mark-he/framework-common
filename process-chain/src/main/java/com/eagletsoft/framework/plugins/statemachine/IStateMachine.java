package com.eagletsoft.framework.plugins.statemachine;

public interface IStateMachine<T> {
    T getTarget();
    String currentState();
    void updateNextState(String state);
    String turn(String event, Object data);

    boolean test(String event, Object data);
}
