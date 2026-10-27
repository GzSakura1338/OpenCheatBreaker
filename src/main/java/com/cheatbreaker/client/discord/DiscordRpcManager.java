package com.cheatbreaker.client.discord;

import com.cheatbreaker.client.manager.AbstractManager;
import com.cheatbreaker.client.util.ClientVersion;
import com.cheatbreaker.client.util.Logging;
import com.cheatbreaker.client.util.ParallelExecutor;
import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.RichPresence.Builder;
import java.time.OffsetDateTime;
import net.minecraft.client.Minecraft;

public class DiscordRpcManager extends AbstractManager {
   public IPCClient ipcclient;
   public String text;

   public void run2() {
      ParallelExecutor.execute(
         () -> {
            try {
               IPCClient ipcclient = new IPCClient(1089597303041564682L);
               ipcclient.setListener(
                  new IPCListener() {
                     public void onPacketReceived(IPCClient ipcclient2, Packet packet) {
                     }

                     public void onReady(IPCClient ipcclient2) {
                        DiscordRpcManager.this.cheatBreaker.getDiscordRpcManager().setIpcclient(ipcclient2);
                        DiscordRpcManager.this.handleString(
                           Minecraft.getMinecraft().getSession().getUsername(),
                           DiscordPresenceUtils.getPresence(DiscordRpcManager.this.cheatBreaker.getDiscordRpcManager().text)
                        );
                     }

                     public void onDisconnect(IPCClient ipcclient2, Throwable throwable) {
                     }

                     public void onActivityJoin(IPCClient ipcclient2, String str) {
                     }

                     public void onActivityJoinRequest(IPCClient ipcclient2, String str, User user) {
                     }

                     public void onClose(IPCClient ipcclient2, JsonObject jsonObject) {
                     }

                     public void onActivitySpectate(IPCClient ipcclient2, String str) {
                     }

                     public void onPacketSent(IPCClient ipcclient2, Packet packet) {
                     }
                  }
               );
               ipcclient.connect(new DiscordBuild[0]);
               Logging.debugTagged("Discord", "Connected to Discord IPC");
            } catch (Exception e) {
               Logging.debugTagged("Discord", "Failed to connect to Discord IPC");
            }
         }
      );
   }

   public void handleString(String str) {
      if (this.cheatBreaker.getKeybindSettings().showDiscordRichPresence.getBooleanValue()) {
         if (this.cheatBreaker.getKeybindSettings().showActiveServer.getBooleanValue()) {
            this.handleString(
               DiscordPresenceUtils.getPresence(this.text)[5] != null ? DiscordPresenceUtils.getPresence(this.text)[5] : str,
               DiscordPresenceUtils.getPresence(this.text)
            );
         } else {
            this.handleString(str, new String[]{"Minecraft " + ClientVersion.text, "cb", "Minecraft " + ClientVersion.text});
         }
      }
   }

   @Override
   public void run() {
      this.run2();
   }

   public void setIpcclient(IPCClient ipcclient) {
      this.ipcclient = ipcclient;
   }

   public IPCClient getIpcclient() {
      return this.ipcclient;
   }

   public void handleString(String str, String[] strs) {
      try {
         if (this.getIpcclient() != null) {
            Builder builder = new Builder();
            builder.setState(str)
               .setDetails(strs[4])
               .setStartTimestamp(OffsetDateTime.now().toInstant().toEpochMilli())
               .setLargeImage(strs[1], strs[0], "https://cheatbreaker.net")
               .setSmallImage(strs[3], strs[2], "https://cheatbreaker.net");
            this.getIpcclient().sendRichPresence(builder.build());
         }
      } catch (Exception e) {
         this.cheatBreaker.getLogger().error(this.cheatBreaker.getChatPrefix() + "RPC was unable to be found");
      }
   }
}
