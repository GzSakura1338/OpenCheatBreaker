package com.cheatbreaker.client.animation;

public class LinearAnimation extends Animation {
    public LinearAnimation(long l) {
        super(l, 1.0F);
    }

    @Override
    public float computeProgress() {
        return (float)(this.duration - this.getRemainingTime()) / (float)this.duration;
    }

    public LinearAnimation(long l, float f) {
        super(l, f);
    }
}
