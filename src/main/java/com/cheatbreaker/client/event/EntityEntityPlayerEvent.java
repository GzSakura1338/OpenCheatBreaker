package com.cheatbreaker.client.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class EntityEntityPlayerEvent extends CancellableEvent {
    public EntityPlayer entityPlayer;
    public Entity entity;

    public Entity getEntity() {
        return this.entity;
    }

    public EntityPlayer getEntityPlayer() {
        return this.entityPlayer;
    }

    public EntityEntityPlayerEvent(EntityPlayer entityPlayer, Entity entity) {
        this.entityPlayer = entityPlayer;
        this.entity = entity;
    }
}
