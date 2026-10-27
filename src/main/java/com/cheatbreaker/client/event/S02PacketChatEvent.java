package com.cheatbreaker.client.event;

import net.minecraft.network.play.server.S02PacketChat;

public class S02PacketChatEvent extends Event {
   public S02PacketChat s02PacketChat;

   public S02PacketChat getS02PacketChat() {
      return this.s02PacketChat;
   }

   public S02PacketChatEvent(S02PacketChat s02PacketChat) {
      this.s02PacketChat = s02PacketChat;
   }
}
