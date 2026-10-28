package com.cheatbreaker.client.event;

public class IntEvent6 extends CancellableEvent {
    public int intValue;

    public IntEvent6(int n) {
        this.intValue = n;
    }

    public int getIntValue() {
        return this.intValue;
    }
}
