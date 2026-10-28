package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;

public class RenderDroppedItemEvent extends CancellableEvent {
    public RenderItem renderItem;
    public Entity entity;
    public double x;
    public double y;
    public double z;
    public float entityYaw;
    public float partialTicks;

    public double getX() {
        return this.x;
    }

    public RenderDroppedItemEvent(RenderItem renderItem, Entity entity, double d, double d2, double d3, float f, float f2) {
        this.renderItem = renderItem;
        this.entity = entity;
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.entityYaw = f;
        this.partialTicks = f2;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public double getY() {
        return this.y;
    }

    public RenderItem getRenderItem() {
        return this.renderItem;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public double getZ() {
        return this.z;
    }

    public float getEntityYaw() {
        return this.entityYaw;
    }
}
