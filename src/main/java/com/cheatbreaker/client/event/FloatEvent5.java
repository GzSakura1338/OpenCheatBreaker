package com.cheatbreaker.client.event;

public class FloatEvent5 extends CancellableEvent {
    public float partialTicks;

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public FloatEvent5(float f) {
        this.partialTicks = f;
    }
}
