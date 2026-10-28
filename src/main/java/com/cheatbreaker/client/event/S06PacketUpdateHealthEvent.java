package com.cheatbreaker.client.event;

import net.minecraft.network.play.server.S06PacketUpdateHealth;

public class S06PacketUpdateHealthEvent extends CancellableEvent {
    public S06PacketUpdateHealth s06PacketUpdateHealth;

    public S06PacketUpdateHealthEvent(S06PacketUpdateHealth s06PacketUpdateHealth) {
        this.s06PacketUpdateHealth = s06PacketUpdateHealth;
    }

    public S06PacketUpdateHealth getS06PacketUpdateHealth() {
        return this.s06PacketUpdateHealth;
    }
}
