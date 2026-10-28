package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.world.EnumDifficulty;

public class C15PacketClientSettings extends Packet {
    public String lang;
    public int view;
    public EnumChatVisibility chatVisibility;
    public boolean enableColors;
    public EnumDifficulty difficulty;
    public boolean showCape;

    public C15PacketClientSettings() {
    }

    public C15PacketClientSettings(String str, int n, EnumChatVisibility enumChatVisibility, boolean flag, EnumDifficulty enumDifficulty, boolean flag2) {
        this.lang = str;
        this.view = n;
        this.chatVisibility = enumChatVisibility;
        this.enableColors = flag;
        this.difficulty = enumDifficulty;
        this.showCape = flag2;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.lang = data.readStringFromBuffer(7);
        this.view = data.readByte();
        this.chatVisibility = EnumChatVisibility.getEnumChatVisibility(data.readByte());
        this.enableColors = data.readBoolean();
        this.difficulty = EnumDifficulty.getDifficultyEnum(data.readByte());
        this.showCape = data.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.lang);
        data.writeByte(this.view);
        data.writeByte(this.chatVisibility.getChatVisibility());
        data.writeBoolean(this.enableColors);
        data.writeByte(this.difficulty.getDifficultyId());
        data.writeBoolean(this.showCape);
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processClientSettings(this);
    }

    public String getLang() {
        return this.lang;
    }

    public int getView() {
        return this.view;
    }

    public EnumChatVisibility getChatVisibility() {
        return this.chatVisibility;
    }

    public boolean isColorsEnabled() {
        return this.enableColors;
    }

    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }

    public boolean isShowCape() {
        return this.showCape;
    }

    @Override
    public String serialize() {
        return String.format(
            "lang='%s', view=%d, chat=%s, col=%b, difficulty=%s, cape=%b",
            this.lang,
            this.view,
            this.chatVisibility,
            this.enableColors,
            this.difficulty,
            this.showCape
        );
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
