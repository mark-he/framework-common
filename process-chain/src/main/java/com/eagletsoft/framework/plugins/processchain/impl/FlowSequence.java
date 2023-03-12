package com.eagletsoft.framework.plugins.processchain.impl;

public class FlowSequence {
    private String exp;
    private String next;

    public FlowSequence(String exp, String next) {
        this.exp = exp;
        this.next = next;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}
