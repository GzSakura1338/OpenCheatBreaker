package net.minecraft.util;

public class FrameTimer {
    public int counter;
    public int index;
    public int lastIndex;
    public long[] frames = new long[240];

    public int getIndex() {
        return this.index;
    }

    public int getLagometerValue(long l, int n) {
        double d = l / 1.6666666E7;
        return (int)(d * n);
    }

    public int parseIndex(int n) {
        return n % 240;
    }

    public int getLastIndex() {
        return this.lastIndex;
    }

    public long[] getFrames() {
        return this.frames;
    }

    public void addFrame(long l) {
        this.frames[this.index] = l;
        this.index++;
        if (this.index == 240) {
            this.index = 0;
        }

        if (this.counter < 240) {
            this.lastIndex = 0;
            this.counter++;
        } else {
            this.lastIndex = this.parseIndex(this.index + 1);
        }
    }
}
