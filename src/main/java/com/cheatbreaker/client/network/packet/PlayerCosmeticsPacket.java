package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;
import java.util.ArrayList;
import java.util.List;

public class PlayerCosmeticsPacket extends AbstractPacket {
    public List<CosmeticData> cosmetics = new ArrayList<>();
    public List<Integer> integerList = new ArrayList<>();
    public List<Integer> integerList2 = new ArrayList<>();
    public String playerId;
    public String playerName;
    public String text;

    public PlayerCosmeticsPacket() {
    }

    public List<CosmeticData> getCosmetics() {
        return this.cosmetics;
    }

    public List<Integer> getIntegerList() {
        return this.integerList;
    }

    public String getText() {
        return this.text;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.playerId = packetBuffer.readString();
        int readInt = packetBuffer.getByteBuf().readInt();
        this.cosmetics = new ArrayList<>();

        for (int i = 0; i < readInt; i++) {
            long readLong = packetBuffer.getByteBuf().readLong();
            float readFloat = packetBuffer.getByteBuf().readFloat();
            boolean readBoolean = packetBuffer.getByteBuf().readBoolean();
            String readString = packetBuffer.readString();
            String readString2 = packetBuffer.readString();
            String readString3 = packetBuffer.readString();
            int readInt2 = packetBuffer.getByteBuf().readInt();
            ArrayList list = new ArrayList(readInt2);

            for (int j = 0; j < readInt2; j++) {
                list.add(packetBuffer.readString());
            }

            this.cosmetics.add(new CosmeticData(readLong, readFloat, readBoolean, readString, readString2, readString3, list));
        }

        this.playerName = packetBuffer.readString();
        this.text = packetBuffer.readString();
        this.integerList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            this.integerList.add(packetBuffer.getByteBuf().readInt());
        }

        this.integerList2 = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            this.integerList2.add(packetBuffer.getByteBuf().readInt());
        }
    }

    public PlayerCosmeticsPacket(String str, List<CosmeticData> list, String str2, String str3, List<Integer> list2, List<Integer> list3) {
        this.playerId = str;
        this.cosmetics = list;
        this.playerName = str2;
        this.text = str3;
        this.integerList = list2;
        this.integerList2 = list3;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(this.playerId);
        packetBuffer.getByteBuf().writeInt(this.cosmetics.size());

        for (CosmeticData cosmeticData : this.cosmetics) {
            packetBuffer.getByteBuf().writeLong(cosmeticData.getId());
            packetBuffer.getByteBuf().writeFloat(cosmeticData.getScale());
            packetBuffer.getByteBuf().writeBoolean(cosmeticData.isEquipped());
            packetBuffer.writeString(cosmeticData.getTexturePath());
            packetBuffer.writeString(cosmeticData.getName());
            packetBuffer.writeString(cosmeticData.getType());
            packetBuffer.getByteBuf().writeInt(cosmeticData.getExtras().size());

            for (String str : cosmeticData.getExtras()) {
                packetBuffer.writeString(str);
            }
        }

        packetBuffer.writeString(this.playerName);
        packetBuffer.writeString(this.text);

        for (int n : this.integerList) {
            packetBuffer.getByteBuf().writeInt(n);
        }

        for (int n : this.integerList2) {
            packetBuffer.getByteBuf().writeInt(n);
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((WebsocketPacketHandler)packetHandler).handlePlayerCosmetics(this);
    }

    public List<Integer> getIntegerList2() {
        return this.integerList2;
    }
}
