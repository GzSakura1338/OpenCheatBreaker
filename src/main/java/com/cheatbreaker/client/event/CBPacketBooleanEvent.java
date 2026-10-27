package com.cheatbreaker.client.event;

import com.cheatbreaker.nethandler.CBPacket;

public class CBPacketBooleanEvent extends CancellableEvent {
   public boolean flag2;
   public CBPacket cbpacket;

   public CBPacket getCbpacket() {
      return this.cbpacket;
   }

   public CBPacketBooleanEvent(boolean flag, CBPacket cbpacket) {
      this.flag2 = flag;
      this.cbpacket = cbpacket;
   }

   public boolean isFlag5() {
      return this.flag2;
   }
}
