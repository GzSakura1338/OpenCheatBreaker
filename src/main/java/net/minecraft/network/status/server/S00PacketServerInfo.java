package net.minecraft.network.status.server;

import com.cheatbreaker.client.network.ServerStatusResponseSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.ServerStatusResponse.MinecraftProtocolVersionIdentifier;
import net.minecraft.network.ServerStatusResponse.PlayerCountData;
import net.minecraft.network.ServerStatusResponse.MinecraftProtocolVersionIdentifier.Serializer;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.IChatComponent;

public class S00PacketServerInfo extends Packet {
    public static Gson GSON = new GsonBuilder()
        .registerTypeAdapter(MinecraftProtocolVersionIdentifier.class, new Serializer())
        .registerTypeAdapter(PlayerCountData.class, new net.minecraft.network.ServerStatusResponse.PlayerCountData.Serializer())
        .registerTypeAdapter(ServerStatusResponse.class, new ServerStatusResponseSerializer())
        .registerTypeAdapter(ServerStatusResponse.class, new net.minecraft.network.ServerStatusResponse.Serializer())
        .registerTypeHierarchyAdapter(IChatComponent.class, new net.minecraft.util.IChatComponent.Serializer())
        .registerTypeHierarchyAdapter(ChatStyle.class, new net.minecraft.util.ChatStyle.Serializer())
        .registerTypeAdapterFactory(new EnumTypeAdapterFactory())
        .create();
    public ServerStatusResponse response;

    public S00PacketServerInfo() {
    }

    public S00PacketServerInfo(ServerStatusResponse responseIn) {
        this.response = responseIn;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.response = (ServerStatusResponse)GSON.fromJson(data.readStringFromBuffer(32767), ServerStatusResponse.class);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(GSON.toJson(this.response));
    }

    public void processPacket(INetHandlerStatusClient handler) {
        handler.handleServerInfo(this);
    }

    public ServerStatusResponse getServerMetadata() {
        return this.response;
    }

    @Override
    public boolean hasPriority() {
        return true;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerStatusClient)netHandler);
    }
}
