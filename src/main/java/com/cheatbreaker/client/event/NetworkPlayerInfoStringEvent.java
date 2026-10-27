package com.cheatbreaker.client.event;

import net.minecraft.client.network.NetworkPlayerInfo;

public class NetworkPlayerInfoStringEvent extends CancellableEvent {
   public String text;
   public NetworkPlayerInfo networkPlayerInfo;

   public String getText() {
      return this.text;
   }

   public NetworkPlayerInfo getNetworkPlayerInfo() {
      return this.networkPlayerInfo;
   }

   public NetworkPlayerInfoStringEvent(String str, NetworkPlayerInfo networkPlayerInfo) {
      this.text = str;
      this.networkPlayerInfo = networkPlayerInfo;
   }
}
