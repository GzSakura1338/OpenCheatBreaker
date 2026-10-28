package com.cheatbreaker.client.event;

public class IntEvent5 extends SimpleEvent39 {
    public int MIN_VALUE;

    public void setMIN_VALUE(int n) {
        this.MIN_VALUE = n;
    }

    public int getMIN_VALUE() {
        return this.MIN_VALUE;
    }

    public IntEvent5(int n) {
        this.MIN_VALUE = n;
    }
}
