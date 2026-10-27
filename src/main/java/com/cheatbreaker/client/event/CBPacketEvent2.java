package com.cheatbreaker.client.event;

import com.cheatbreaker.nethandler.CBPacket;

public class CBPacketEvent2 extends Event {
   public CBPacket cbpacket;

   public CBPacketEvent2(CBPacket cbpacket) {
      this.cbpacket = cbpacket;
   }

   public CBPacket getCbpacket() {
      return this.cbpacket;
   }
}
