package com.cheatbreaker.client.event;

public class TessellatorColorOpaqueEvent extends Event {
    public int red;
    public int green;
    public int blue;
    public int alpha;

    public void setBlue(int n) {
        this.blue = n;
    }

    public TessellatorColorOpaqueEvent(int n, int n2, int n3, int n4) {
        this.red = n;
        this.green = n2;
        this.blue = n3;
        this.alpha = n4;
    }

    public int getBlue() {
        return this.blue;
    }

    public void setAlpha(int n) {
        this.alpha = n;
    }

    public int getRed() {
        return this.red;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public void setGreen(int n) {
        this.green = n;
    }

    public int getGreen() {
        return this.green;
    }

    public void setRed(int n) {
        this.red = n;
    }
}
