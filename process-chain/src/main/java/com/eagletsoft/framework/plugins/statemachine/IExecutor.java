package com.eagletsoft.framework.plugins.statemachine;

public interface IExecutor {
    void execute(IStateMachine stateMachine, String event, Object data);
}
