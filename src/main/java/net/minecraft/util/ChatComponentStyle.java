package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent {
   public List<IChatComponent> siblings = Lists.newArrayList();
   public ChatStyle style;

   @Override
   public IChatComponent appendSibling(IChatComponent component) {
      component.getChatStyle().setParentStyle(this.getChatStyle());
      this.siblings.add(component);
      return this;
   }

   @Override
   public List<IChatComponent> getSiblings() {
      return this.siblings;
   }

   @Override
   public IChatComponent appendText(String text) {
      return this.appendSibling(new ChatComponentText(text));
   }

   @Override
   public IChatComponent setChatStyle(ChatStyle style) {
      this.style = style;

      for (IChatComponent chatComponent : this.siblings) {
         chatComponent.getChatStyle().setParentStyle(this.getChatStyle());
      }

      return this;
   }

   @Override
   public ChatStyle getChatStyle() {
      if (this.style == null) {
         this.style = new ChatStyle();

         for (IChatComponent chatComponent : this.siblings) {
            chatComponent.getChatStyle().setParentStyle(this.style);
         }
      }

      return this.style;
   }

   @Override
   public Iterator<IChatComponent> iterator() {
      return Iterators.concat(Iterators.forArray(new ChatComponentStyle[]{this}), createDeepCopyIterator(this.siblings));
   }

   @Override
   public String getUnformattedText() {
      StringBuilder sb = new StringBuilder();

      for (IChatComponent chatComponent : this) {
         sb.append(chatComponent.getUnformattedTextForChat());
      }

      return sb.toString();
   }

   @Override
   public String getFormattedText() {
      StringBuilder sb = new StringBuilder();

      for (IChatComponent chatComponent : this) {
         sb.append(chatComponent.getChatStyle().getFormattingCode());
         sb.append(chatComponent.getUnformattedTextForChat());
         sb.append(EnumChatFormatting.RESET);
      }

      return sb.toString();
   }

   public static Iterator<IChatComponent> createDeepCopyIterator(Iterable<IChatComponent> components) {
      Iterator concat = Iterators.concat(Iterators.transform(components.iterator(), new Function<IChatComponent, Iterator<IChatComponent>>() {
         public Iterator<IChatComponent> apply(IChatComponent chatComponent) {
            return chatComponent.iterator();
         }
      }));
      return Iterators.transform(concat, new Function<IChatComponent, IChatComponent>() {
         public IChatComponent apply(IChatComponent chatComponent) {
            IChatComponent createCopy = chatComponent.createCopy();
            createCopy.setChatStyle(createCopy.getChatStyle().createDeepCopy());
            return createCopy;
         }
      });
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof ChatComponentStyle)) {
         return false;
      }

      ChatComponentStyle chatComponentStyle = (ChatComponentStyle)obj;
      return this.siblings.equals(chatComponentStyle.siblings) && this.getChatStyle().equals(chatComponentStyle.getChatStyle());
   }

   @Override
   public int hashCode() {
      return 31 * this.style.hashCode() + this.siblings.hashCode();
   }

   @Override
   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
   }
}
