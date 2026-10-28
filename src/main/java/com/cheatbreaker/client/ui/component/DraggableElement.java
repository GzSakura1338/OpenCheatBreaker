package com.cheatbreaker.client.ui.component;

import com.sun.javafx.geom.Vec2d;
import java.util.concurrent.atomic.AtomicBoolean;
import org.lwjgl.input.Mouse;

public abstract class DraggableElement extends UIElement {
    public Vec2d vec2d = new Vec2d();
    public AtomicBoolean dragging = new AtomicBoolean();

    public void updateDrag(float f, float f2) {
        if (this.dragging.get()) {
            if (!Mouse.isButtonDown(0)) {
                this.dragging.set(false);
                return;
            }

            double d = f - this.vec2d.x;
            double d2 = f2 - this.vec2d.y;
            this.setBounds((float)d, (float)d2, this.width, this.height);
        }
    }

    public void stopDrag() {
        if (this.dragging.get()) {
            this.dragging.set(false);
        }
    }

    public void startDrag(float f, float f2) {
        this.vec2d.set(f - this.x, f2 - this.y);
        this.dragging.set(true);
    }
}
