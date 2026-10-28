package com.cheatbreaker.client.animation;

public class EaseInOutAnimation extends LinearAnimation {
    @Override
    public float computeProgress() {
        float computeProgress = super.computeProgress();
        return computeProgress < 0.5 ? 2.0F * computeProgress * computeProgress : -1.0F + (4.0F - 2.0F * computeProgress) * computeProgress;
    }

    public EaseInOutAnimation(long l) {
        super(l);
    }
}
