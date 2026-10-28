package com.cheatbreaker.client.event;

public class FloatEvent9 extends Event {
    public float floatValue;

    public void setFloatValue(float f) {
        this.floatValue = f;
    }

    public FloatEvent9(float f) {
        this.floatValue = f;
    }

    public float getFloatValue() {
        return this.floatValue;
    }
}
