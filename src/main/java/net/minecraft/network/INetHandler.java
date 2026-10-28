package net.minecraft.network;

import net.minecraft.util.IChatComponent;

public interface INetHandler {
    void onDisconnect(IChatComponent chatComponent) throws java.io.IOException;

    void onConnectionStateTransition(EnumConnectionState enumConnectionState, EnumConnectionState enumConnectionState2);

    void onNetworkTick();
}
