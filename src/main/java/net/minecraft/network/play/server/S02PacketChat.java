package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;

public class S02PacketChat extends Packet {
    public boolean isChat = true;
    public IChatComponent chatComponent;

    public S02PacketChat() {
    }

    public S02PacketChat(IChatComponent component) {
        this(component, true);
    }

    public S02PacketChat(IChatComponent component, boolean chat) {
        this.chatComponent = component;
        this.isChat = chat;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.chatComponent = Serializer.jsonToComponent(data.readStringFromBuffer(32767));
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(Serializer.componentToJson(this.chatComponent));
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleChat(this);
    }

    @Override
    public String serialize() {
        return String.format("message='%s'", this.chatComponent);
    }

    public IChatComponent getMessage() {
        return this.chatComponent;
    }

    public boolean isChat() {
        return this.isChat;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
