package com.cheatbreaker.client.event;

import net.minecraft.entity.player.EntityPlayer;

public class EntityPlayerEvent extends CancellableEvent {
    public EntityPlayer entityPlayer;

    public EntityPlayerEvent(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }

    public EntityPlayer getEntityPlayer() {
        return this.entityPlayer;
    }
}
