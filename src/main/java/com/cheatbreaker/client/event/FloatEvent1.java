package com.cheatbreaker.client.event;

public class FloatEvent1 extends CancellableEvent {
    public float floatValue1;
    public float attackedAtYaw;

    public void setFloatValue1(float f) {
        this.floatValue1 = f;
    }

    public float getFloatValue1() {
        return this.floatValue1;
    }

    public void setAttackedAtYaw(float f) {
        this.attackedAtYaw = f;
    }

    public FloatEvent1(float f, float f2) {
        this.floatValue1 = f;
        this.attackedAtYaw = f2;
    }

    public float getAttackedAtYaw() {
        return this.attackedAtYaw;
    }
}
