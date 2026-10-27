package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerStatusPinger {
   public static Logger LOGGER = LogManager.getLogger();
   public List<NetworkManager> connections = Collections.synchronizedList(Lists.newArrayList());

   public void pingServer(final String str, final RealmsServerPing realmsServerPing) throws UnknownHostException {
      if (str != null && !str.startsWith("0.0.0.0") && !str.isEmpty()) {
         RealmsServerAddress parseString = RealmsServerAddress.parseString(str);
         final NetworkManager createNetworkManagerAndConnect = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(parseString.getHost()), parseString.getPort(), false);
         this.connections.add(createNetworkManagerAndConnect);
         createNetworkManagerAndConnect.setNetHandler(
            new INetHandlerStatusClient() {
               public boolean flag = false;

               @Override
               public void handleServerInfo(S00PacketServerInfo packetIn) {
                  ServerStatusResponse response = packetIn.getResponse();
                  if (response.getPlayerCountData() != null) {
                     realmsServerPing.nrOfPlayers = String.valueOf(response.getPlayerCountData().getOnlinePlayerCount());
                     if (ArrayUtils.isNotEmpty(response.getPlayerCountData().getPlayers())) {
                        StringBuilder sb = new StringBuilder();

                        for (GameProfile gameProfile : response.getPlayerCountData().getPlayers()) {
                           if (sb.length() > 0) {
                              sb.append("\n");
                           }

                           sb.append(gameProfile.getName());
                        }

                        if (response.getPlayerCountData().getPlayers().length < response.getPlayerCountData().getOnlinePlayerCount()) {
                           if (sb.length() > 0) {
                              sb.append("\n");
                           }

                           sb.append("... and ")
                              .append(response.getPlayerCountData().getOnlinePlayerCount() - response.getPlayerCountData().getPlayers().length)
                              .append(" more ...");
                        }

                        realmsServerPing.playerList = sb.toString();
                     }
                  } else {
                     realmsServerPing.playerList = "";
                  }

                  createNetworkManagerAndConnect.sendPacket(new C01PacketPing(Realms.currentTimeMillis()));
                  this.flag = true;
               }

               @Override
               public void handlePong(S01PacketPong packetIn) {
                  createNetworkManagerAndConnect.closeChannel(new ChatComponentText("Finished"));
               }

               @Override
               public void onDisconnect(IChatComponent reason) {
                  if (!this.flag) {
                     RealmsServerStatusPinger.LOGGER.error("Can't ping " + str + ": " + reason.getUnformattedText());
                  }
               }
            }
         );

         try {
            createNetworkManagerAndConnect.sendPacket(new C00Handshake(RealmsSharedConstants.NETWORK_PROTOCOL_VERSION, parseString.getHost(), parseString.getPort(), EnumConnectionState.STATUS));
            createNetworkManagerAndConnect.sendPacket(new C00PacketServerQuery());
         } catch (Throwable e) {
            LOGGER.error(e);
         }
      }
   }

   public void tick() {
      synchronized (this.connections) {
         Iterator iterator = this.connections.iterator();

         while (iterator.hasNext()) {
            NetworkManager networkManager = (NetworkManager)iterator.next();
            if (networkManager.isChannelOpen()) {
               networkManager.processReceivedPackets();
            } else {
               iterator.remove();
               networkManager.checkDisconnected();
            }
         }
      }
   }

   public void removeAll() {
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
