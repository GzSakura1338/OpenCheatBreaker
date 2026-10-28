package com.cheatbreaker.client.event;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S19PacketEntityStatus;

public class EntityS19PacketEntityStatusEvent extends CancellableEvent {
    public S19PacketEntityStatus s19PacketEntityStatus;
    public Entity entity;

    public S19PacketEntityStatus getS19PacketEntityStatus() {
        return this.s19PacketEntityStatus;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public EntityS19PacketEntityStatusEvent(S19PacketEntityStatus s19PacketEntityStatus, Entity entity) {
        this.s19PacketEntityStatus = s19PacketEntityStatus;
        this.entity = entity;
    }
}
