package com.cheatbreaker.client.event;

public class FloatEvent8 extends CancellableEvent {
    public float floatValue;

    public float getFloatValue() {
        return this.floatValue;
    }

    public void setFloatValue(float f) {
        this.floatValue = f;
    }

    public FloatEvent8(float f) {
        this.floatValue = f;
    }
}
