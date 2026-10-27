package com.cheatbreaker.client.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatLogger {
   public static void handleString(String str) {
      Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(str));
   }
}
