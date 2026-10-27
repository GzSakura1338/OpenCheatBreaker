package com.cheatbreaker.client.event;

import net.minecraft.client.multiplayer.WorldClient;

public class WorldClientEvent extends Event {
   public WorldClient worldClient;

   public WorldClient getWorldClient() {
      return this.worldClient;
   }

   public WorldClientEvent(WorldClient worldClient) {
      this.worldClient = worldClient;
   }
}
