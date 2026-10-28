package net.minecraft.server.network;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.status.INetHandlerStatusServer;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

public class NetHandlerStatusServer implements INetHandlerStatusServer {
    public MinecraftServer server;
    public NetworkManager networkManager;

    public NetHandlerStatusServer(MinecraftServer serverIn, NetworkManager netManager) {
        this.server = serverIn;
        this.networkManager = netManager;
    }

    @Override
    public void onDisconnect(IChatComponent reason) {
    }

    @Override
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState) {
        if (newState != EnumConnectionState.STATUS) {
            throw new UnsupportedOperationException("Unexpected change in protocol to " + newState);
        }
    }

    @Override
    public void onNetworkTick() {
    }

    @Override
    public void processServerQuery(C00PacketServerQuery packetIn) {
        this.networkManager.scheduleOutboundPacket(new S00PacketServerInfo(this.server.getServerStatusResponse()));
    }

    @Override
    public void processPing(C01PacketPing packetIn) {
        this.networkManager.scheduleOutboundPacket(new S01PacketPong(packetIn.getClientTime()));
    }
}
