package com.cheatbreaker.client.event;

public class IntEvent3 extends CancellableEvent {
    public int intValue;

    public IntEvent3(int n) {
        this.intValue = n;
    }

    public void setIntValue(int n) {
        this.intValue = n;
    }

    public int getIntValue() {
        return this.intValue;
    }
}
