package net.minecraft.client.network;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;

public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer {
    public MinecraftServer server;
    public NetworkManager connection;

    public NetHandlerHandshakeMemory(MinecraftServer minecraftServer, NetworkManager networkManager) {
        this.server = minecraftServer;
        this.connection = networkManager;
    }

    @Override
    public void processHandshake(C00Handshake packetIn) {
        this.connection.setConnectionState(packetIn.getRequestedState());
    }

    @Override
    public void onDisconnect(IChatComponent reason) {
    }

    @Override
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState) {
        Validate.validState(newState == EnumConnectionState.LOGIN || newState == EnumConnectionState.STATUS, "Unexpected protocol " + newState, new Object[0]);
        switch (newState) {
            case LOGIN:
                this.connection.setNetHandler(new NetHandlerLoginServer(this.server, this.connection));
            default:
                return;
            case STATUS:
                throw new UnsupportedOperationException("NYI");
        }
    }

    @Override
    public void onNetworkTick() {
    }
}
