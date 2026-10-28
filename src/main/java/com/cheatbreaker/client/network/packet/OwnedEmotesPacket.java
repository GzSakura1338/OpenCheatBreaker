package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;
import java.util.concurrent.CopyOnWriteArrayList;

public class OwnedEmotesPacket extends AbstractPacket {
    public CopyOnWriteArrayList<Integer> ownedEmotes = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<Integer> enabledEmotes = new CopyOnWriteArrayList<>();

    @Override
    public void read(PacketBuffer packetBuffer) {
        int readInt = packetBuffer.getByteBuf().readInt();
        this.ownedEmotes = new CopyOnWriteArrayList<>();

        for (int i = 0; i < readInt; i++) {
            this.ownedEmotes.add(packetBuffer.getByteBuf().readInt());
        }

        int readInt2 = packetBuffer.getByteBuf().readInt();
        this.enabledEmotes = new CopyOnWriteArrayList<>();

        for (int i = 0; i < readInt2; i++) {
            this.enabledEmotes.add(packetBuffer.getByteBuf().readInt());
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((WebsocketPacketHandler)packetHandler).handleOwnedEmotes(this);
    }

    public OwnedEmotesPacket(CopyOnWriteArrayList<Integer> copyOnWriteArrayList, CopyOnWriteArrayList<Integer> copyOnWriteArrayList2) {
        this.ownedEmotes = copyOnWriteArrayList;
        this.enabledEmotes = copyOnWriteArrayList2;
    }

    public CopyOnWriteArrayList<Integer> getOwnedEmotes() {
        return this.ownedEmotes;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.getByteBuf().writeInt(this.ownedEmotes.size());

        for (int n : this.ownedEmotes) {
            packetBuffer.getByteBuf().writeInt(n);
        }

        packetBuffer.getByteBuf().writeInt(this.enabledEmotes.size());

        for (int n : this.enabledEmotes) {
            packetBuffer.getByteBuf().writeInt(n);
        }
    }

    public OwnedEmotesPacket() {
    }

    public CopyOnWriteArrayList<Integer> getEnabledEmotes() {
        return this.enabledEmotes;
    }
}
