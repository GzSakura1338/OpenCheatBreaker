package com.cheatbreaker.client.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatPrinter {
   public static Minecraft mc = Minecraft.getMinecraft();

   public static void print(String str, boolean flag, boolean flag2) {
      if (mc != null && mc.ingameGUI != null && mc.ingameGUI.getChatGUI() != null) {
         ChatComponentText chatComponentText = new ChatComponentText(str);
         chatComponentText.setHasIcon(flag);
         chatComponentText.setHighlighted(flag2);
         mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
      }
   }
}
