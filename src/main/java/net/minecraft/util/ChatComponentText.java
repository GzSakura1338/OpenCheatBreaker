package net.minecraft.util;

public class ChatComponentText extends ChatComponentStyle {
   public String text;
   public boolean hasIcon;
   public boolean highlighted;

   public ChatComponentText(String msg) {
      this.text = msg;
   }

   public String getChatComponentText_TextValue() {
      return this.text;
   }

   @Override
   public String getUnformattedTextForChat() {
      return this.text;
   }

   public ChatComponentText createCopy() {
      ChatComponentText chatComponentText = new ChatComponentText(this.text);
      chatComponentText.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent chatComponent : this.getSiblings()) {
         chatComponentText.appendSibling(chatComponent.createCopy());
      }

      return chatComponentText;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof ChatComponentText)) {
         return false;
      }

      ChatComponentText chatComponentText = (ChatComponentText)obj;
      return this.text.equals(chatComponentText.getChatComponentText_TextValue()) && super.equals(obj);
   }

   @Override
   public String toString() {
      return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
   }

   public void setHighlighted(boolean flag) {
      this.highlighted = flag;
   }

   public boolean hasIcon() {
      return this.hasIcon;
   }

   public boolean isHighlighted() {
      return this.highlighted;
   }

   public void setHasIcon(boolean flag) {
      this.hasIcon = flag;
   }
}
