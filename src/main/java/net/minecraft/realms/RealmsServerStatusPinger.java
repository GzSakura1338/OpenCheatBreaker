package net.minecraft.realms;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerStatusPinger {
    public static Logger LOGGER = LogManager.getLogger();
    public List connections = Collections.synchronizedList(new ArrayList());

    public void pingServer(final String str, final ServerPing serverPing) throws IOException {
        if (str != null && !str.startsWith("0.0.0.0") && !str.isEmpty()) {
            RealmsServerAddress parseString = RealmsServerAddress.parseString(str);
            final NetworkManager provideLanClient = NetworkManager.provideLanClient(InetAddress.getByName(parseString.getHost()), parseString.getPort());
            this.connections.add(provideLanClient);
            provideLanClient.setNetHandler(new INetHandlerStatusClient() {
                public boolean flag = false;

                @Override
                public void handleServerInfo(S00PacketServerInfo packetIn) {
                    ServerStatusResponse serverMetadata = packetIn.getServerMetadata();
                    if (serverMetadata.getPlayerCountData() != null) {
                        serverPing.nrOfPlayers = String.valueOf(serverMetadata.getPlayerCountData().getOnlinePlayerCount());
                    }

                    provideLanClient.scheduleOutboundPacket(new C01PacketPing(Realms.currentTimeMillis()));
                    this.flag = true;
                }

                @Override
                public void handlePong(S01PacketPong packetIn) {
                    provideLanClient.closeChannel(new ChatComponentText("Finished"));
                }

                @Override
                public void onDisconnect(IChatComponent reason) {
                    if (!this.flag) {
                        RealmsServerStatusPinger.LOGGER.error("Can't ping " + str + ": " + reason.getUnformattedText());
                    }
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
            });

            try {
                provideLanClient.scheduleOutboundPacket(
                    new C00Handshake(RealmsSharedConstants.NETWORK_PROTOCOL_VERSION, parseString.getHost(), parseString.getPort(), EnumConnectionState.STATUS)
                );
                provideLanClient.scheduleOutboundPacket(new C00PacketServerQuery());
            } catch (Throwable e) {
                LOGGER.error(e);
            }
        }
    }

    public void tick() throws IOException {
        List connections = this.connections;
        synchronized (this.connections) {
            Iterator iterator = this.connections.iterator();

            while (iterator.hasNext()) {
                NetworkManager networkManager = (NetworkManager)iterator.next();
                if (networkManager.isChannelOpen()) {
                    networkManager.processReceivedPackets();
                } else {
                    iterator.remove();
                    if (networkManager.getExitMessage() != null) {
                        networkManager.getNetHandler().onDisconnect(networkManager.getExitMessage());
                    }
                }
            }
        }
    }

    public void removeAll() {
        List connections = this.connections;
        synchronized (this.connections) {
            Iterator iterator = this.connections.iterator();

            while (iterator.hasNext()) {
                NetworkManager networkManager = (NetworkManager)iterator.next();
                if (networkManager.isChannelOpen()) {
                    iterator.remove();
                    networkManager.closeChannel(new ChatComponentText("Cancelled"));
                }
            }
        }
    }
}
