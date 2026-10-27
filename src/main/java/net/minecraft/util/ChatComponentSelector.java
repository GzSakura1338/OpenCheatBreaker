package net.minecraft.util;

public class ChatComponentSelector extends ChatComponentStyle {
   public String selector;

   public ChatComponentSelector(String selectorIn) {
      this.selector = selectorIn;
   }

   public String getSelector() {
      return this.selector;
   }

   @Override
   public String getUnformattedTextForChat() {
      return this.selector;
   }

   public ChatComponentSelector createCopy() {
      ChatComponentSelector chatComponentSelector = new ChatComponentSelector(this.selector);
      chatComponentSelector.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent chatComponent : this.getSiblings()) {
         chatComponentSelector.appendSibling(chatComponent.createCopy());
      }

      return chatComponentSelector;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof ChatComponentSelector)) {
         return false;
      }

      ChatComponentSelector chatComponentSelector = (ChatComponentSelector)obj;
      return this.selector.equals(chatComponentSelector.selector) && super.equals(obj);
   }

   @Override
   public String toString() {
      return "SelectorComponent{pattern='" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
   }
}
