package com.cheatbreaker.client.event;

public class FloatEvent6 extends Event {
    public float partialTicks;

    public FloatEvent6(float f) {
        this.partialTicks = f;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
