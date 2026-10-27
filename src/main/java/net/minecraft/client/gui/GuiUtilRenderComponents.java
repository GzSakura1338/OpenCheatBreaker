package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class GuiUtilRenderComponents {
   public static String getRenderChatMessage(String str, boolean flag) {
      return !flag && !Minecraft.getMinecraft().gameSettings.chatColours ? EnumChatFormatting.getTextWithoutFormattingCodes(str) : str;
   }

   public static List<IChatComponent> splitText(IChatComponent chatComponent, int n, FontRenderer fontRenderer, boolean flag, boolean flag2) {
      int n2 = 0;
      ChatComponentText chatComponentText = new ChatComponentText("");
      ArrayList newArrayList = Lists.newArrayList();
      ArrayList newArrayList2 = Lists.newArrayList(chatComponent);

      for (int i = 0; i < newArrayList2.size(); i++) {
         IChatComponent chatComponent2 = (IChatComponent)newArrayList2.get(i);
         String unformattedTextForChat = chatComponent2.getUnformattedTextForChat();
         boolean flag3 = false;
         if (unformattedTextForChat.contains("\n")) {
            int indexOf = unformattedTextForChat.indexOf(10);
            String substring = unformattedTextForChat.substring(indexOf + 1);
            unformattedTextForChat = unformattedTextForChat.substring(0, indexOf + 1);
            ChatComponentText chatComponentText2 = new ChatComponentText(substring);
            chatComponentText2.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
            newArrayList2.add(i + 1, chatComponentText2);
            flag3 = true;
         }

         String renderChatMessage = getRenderChatMessage(chatComponent2.getChatStyle().getFormattingCode() + unformattedTextForChat, flag2);
         String str = renderChatMessage.endsWith("\n") ? renderChatMessage.substring(0, renderChatMessage.length() - 1) : renderChatMessage;
         int stringWidth = fontRenderer.getStringWidth(str);
         ChatComponentText chatComponentText2 = new ChatComponentText(str);
         chatComponentText2.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
         if (n2 + stringWidth > n) {
            String trimStringToWidth = fontRenderer.trimStringToWidth(renderChatMessage, n - n2, false);
            String str2 = trimStringToWidth.length() < renderChatMessage.length() ? renderChatMessage.substring(trimStringToWidth.length()) : null;
            if (str2 != null && str2.length() > 0) {
               int lastIndexOf = trimStringToWidth.lastIndexOf(" ");
               if (lastIndexOf >= 0 && fontRenderer.getStringWidth(renderChatMessage.substring(0, lastIndexOf)) > 0) {
                  trimStringToWidth = renderChatMessage.substring(0, lastIndexOf);
                  if (flag) {
                     lastIndexOf++;
                  }

                  str2 = renderChatMessage.substring(lastIndexOf);
               } else if (n2 > 0 && !renderChatMessage.contains(" ")) {
                  trimStringToWidth = "";
                  str2 = renderChatMessage;
               }

               ChatComponentText chatComponentText3 = new ChatComponentText(str2);
               chatComponentText3.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
               newArrayList2.add(i + 1, chatComponentText3);
            }

            stringWidth = fontRenderer.getStringWidth(trimStringToWidth);
            chatComponentText2 = new ChatComponentText(trimStringToWidth);
            chatComponentText2.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
            flag3 = true;
         }

         if (n2 + stringWidth <= n) {
            n2 += stringWidth;
            chatComponentText.appendSibling(chatComponentText2);
         } else {
            flag3 = true;
         }

         if (flag3) {
            newArrayList.add(chatComponentText);
            n2 = 0;
            chatComponentText = new ChatComponentText("");
         }
      }

      newArrayList.add(chatComponentText);
      return newArrayList;
   }
}
