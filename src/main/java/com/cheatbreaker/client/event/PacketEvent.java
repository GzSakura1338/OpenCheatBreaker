package com.cheatbreaker.client.event;

import net.minecraft.network.Packet;

public class PacketEvent extends CancellableEvent {
    public Packet packet;

    public Packet getPacket() {
        return this.packet;
    }

    public PacketEvent(Packet packet) {
        this.packet = packet;
    }
}
