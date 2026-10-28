package com.cheatbreaker.client.render;

import org.lwjgl.opengl.GL11;

public class GlCapability {
    public boolean enabled = false;
    public int capability;

    public void disable() {
        this.setEnabled(false);
    }

    public void enable() {
        this.setEnabled(true);
    }

    public GlCapability(int n) {
        this.capability = n;
    }

    public void setEnabled(boolean flag) {
        if (flag != this.enabled) {
            this.enabled = flag;
            if (flag) {
                GL11.glEnable(this.capability);
            } else {
                GL11.glDisable(this.capability);
            }
        }
    }
}
