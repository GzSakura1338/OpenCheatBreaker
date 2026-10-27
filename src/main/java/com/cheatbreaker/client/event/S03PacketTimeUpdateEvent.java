package com.cheatbreaker.client.event;

import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class S03PacketTimeUpdateEvent extends CancellableEvent {
   public S03PacketTimeUpdate s03PacketTimeUpdate;

   public S03PacketTimeUpdateEvent(S03PacketTimeUpdate s03PacketTimeUpdate) {
      this.s03PacketTimeUpdate = s03PacketTimeUpdate;
   }

   public S03PacketTimeUpdate getS03PacketTimeUpdate() {
      return this.s03PacketTimeUpdate;
   }
}
