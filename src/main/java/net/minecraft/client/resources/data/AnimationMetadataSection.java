package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnimationMetadataSection implements IMetadataSection {
    public List animationFrames;
    public int frameWidth;
    public int frameHeight;
    public int frameTime;

    public AnimationMetadataSection(List list, int n, int n2, int n3) {
        this.animationFrames = list;
        this.frameWidth = n;
        this.frameHeight = n2;
        this.frameTime = n3;
    }

    public int getFrameHeight() {
        return this.frameHeight;
    }

    public int getFrameWidth() {
        return this.frameWidth;
    }

    public int getFrameCount() {
        return this.animationFrames.size();
    }

    public int getFrameTime() {
        return this.frameTime;
    }

    public AnimationFrame getAnimationFrame(int n) {
        return (AnimationFrame)this.animationFrames.get(n);
    }

    public int getFrameTimeSingle(int n) {
        AnimationFrame animationFrame = this.getAnimationFrame(n);
        return animationFrame.hasNoTime() ? this.frameTime : animationFrame.getFrameTime();
    }

    public boolean frameHasTime(int n) {
        return !((AnimationFrame)this.animationFrames.get(n)).hasNoTime();
    }

    public int getFrameIndex(int n) {
        return ((AnimationFrame)this.animationFrames.get(n)).getFrameIndex();
    }

    public Set getFrameIndexSet() {
        HashSet newHashSet = Sets.newHashSet();

        for (AnimationFrame animationFrame : (Iterable<AnimationFrame>)this.animationFrames) {
            newHashSet.add(animationFrame.getFrameIndex());
        }

        return newHashSet;
    }
}
