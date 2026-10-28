package com.cheatbreaker.client.event;

import com.cheatbreaker.nethandler.CBPacket;

public class CBPacketBooleanEvent extends CancellableEvent {
    public boolean flag;
    public CBPacket cbpacket;

    public boolean isFlag() {
        return this.flag;
    }

    public CBPacketBooleanEvent(boolean flag, CBPacket cbpacket) {
        this.flag = flag;
        this.cbpacket = cbpacket;
    }

    public CBPacket getCbpacket() {
        return this.cbpacket;
    }
}
