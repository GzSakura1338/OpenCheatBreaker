package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatComponentTranslation extends ChatComponentStyle {
   public Object syncLock = new Object();
   public long lastTranslationUpdateTimeInMilliseconds = -1L;
   public List<IChatComponent> children = Lists.newArrayList();
   public String key;
   public Object[] formatArgs;
   public static Pattern stringVariablePattern = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public ChatComponentTranslation(String translationKey, Object... args) {
      this.key = translationKey;
      this.formatArgs = args;

      for (Object obj : args) {
         if (obj instanceof IChatComponent) {
            ((IChatComponent)obj).getChatStyle().setParentStyle(this.getChatStyle());
         }
      }
   }

   public synchronized void ensureInitialized() {
      synchronized (this.syncLock) {
         long l = StatCollector.getLastTranslationUpdateTimeInMilliseconds();
         if (l == this.lastTranslationUpdateTimeInMilliseconds) {
            return;
         }

         this.lastTranslationUpdateTimeInMilliseconds = l;
         this.children.clear();
      }

      try {
         this.initializeFromFormat(StatCollector.translateToLocal(this.key));
      } catch (ChatComponentTranslationFormatException e) {
         this.children.clear();

         try {
            this.initializeFromFormat(StatCollector.translateToFallback(this.key));
         } catch (ChatComponentTranslationFormatException e2) {
            throw e;
         }
      }
   }

   public void initializeFromFormat(String format) {
      boolean flag = false;
      Matcher matcher = stringVariablePattern.matcher(format);
      int n = 0;
      int n2 = 0;

      try {
         while (matcher.find(n2)) {
            int start = matcher.start();
            int end = matcher.end();
            if (start > n2) {
               ChatComponentText chatComponentText = new ChatComponentText(String.format(format.substring(n2, start)));
               chatComponentText.getChatStyle().setParentStyle(this.getChatStyle());
               this.children.add(chatComponentText);
            }

            String group = matcher.group(2);
            String substring = format.substring(start, end);
            if ("%".equals(group) && "%%".equals(substring)) {
               ChatComponentText chatComponentText = new ChatComponentText("%");
               chatComponentText.getChatStyle().setParentStyle(this.getChatStyle());
               this.children.add(chatComponentText);
            } else {
               if (!"s".equals(group)) {
                  throw new ChatComponentTranslationFormatException(this, "Unsupported format: '" + substring + "'");
               }

               String group2 = matcher.group(1);
               int n3 = group2 != null ? Integer.parseInt(group2) - 1 : n++;
               if (n3 < this.formatArgs.length) {
                  this.children.add(this.getFormatArgumentAsComponent(n3));
               }
            }

            n2 = end;
         }

         if (n2 < format.length()) {
            ChatComponentText chatComponentText = new ChatComponentText(String.format(format.substring(n2)));
            chatComponentText.getChatStyle().setParentStyle(this.getChatStyle());
            this.children.add(chatComponentText);
         }
      } catch (IllegalFormatException e) {
         throw new ChatComponentTranslationFormatException(this, e);
      }
   }

   public IChatComponent getFormatArgumentAsComponent(int index) {
      if (index >= this.formatArgs.length) {
         throw new ChatComponentTranslationFormatException(this, index);
      }

      Object obj = this.formatArgs[index];
      IChatComponent chatComponent;
      if (obj instanceof IChatComponent) {
         chatComponent = (IChatComponent)obj;
      } else {
         chatComponent = new ChatComponentText(obj == null ? "null" : obj.toString());
         chatComponent.getChatStyle().setParentStyle(this.getChatStyle());
      }

      return chatComponent;
   }

   @Override
   public IChatComponent setChatStyle(ChatStyle style) {
      super.setChatStyle(style);

      for (Object obj : this.formatArgs) {
         if (obj instanceof IChatComponent) {
            ((IChatComponent)obj).getChatStyle().setParentStyle(this.getChatStyle());
         }
      }

      if (this.lastTranslationUpdateTimeInMilliseconds > -1L) {
         for (IChatComponent chatComponent : this.children) {
            chatComponent.getChatStyle().setParentStyle(style);
         }
      }

      return this;
   }

   @Override
   public Iterator<IChatComponent> iterator() {
      this.ensureInitialized();
      return Iterators.concat(createDeepCopyIterator(this.children), createDeepCopyIterator(this.siblings));
   }

   @Override
   public String getUnformattedTextForChat() {
      this.ensureInitialized();
      StringBuilder sb = new StringBuilder();

      for (IChatComponent chatComponent : this.children) {
         sb.append(chatComponent.getUnformattedTextForChat());
      }

      return sb.toString();
   }

   public ChatComponentTranslation createCopy() {
      Object[] objs = new Object[this.formatArgs.length];

      for (int i = 0; i < this.formatArgs.length; i++) {
         if (this.formatArgs[i] instanceof IChatComponent) {
            objs[i] = ((IChatComponent)this.formatArgs[i]).createCopy();
         } else {
            objs[i] = this.formatArgs[i];
         }
      }

      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(this.key, objs);
      chatComponentTranslation.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent chatComponent : this.getSiblings()) {
         chatComponentTranslation.appendSibling(chatComponent.createCopy());
      }

      return chatComponentTranslation;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof ChatComponentTranslation)) {
         return false;
      }

      ChatComponentTranslation chatComponentTranslation = (ChatComponentTranslation)obj;
      return Arrays.equals(this.formatArgs, chatComponentTranslation.formatArgs) && this.key.equals(chatComponentTranslation.key) && super.equals(obj);
   }

   @Override
   public int hashCode() {
      int n = super.hashCode();
      n = 31 * n + this.key.hashCode();
      return 31 * n + Arrays.hashCode(this.formatArgs);
   }

   @Override
   public String toString() {
      return "TranslatableComponent{key='"
         + this.key
         + '\''
         + ", args="
         + Arrays.toString(this.formatArgs)
         + ", siblings="
         + this.siblings
         + ", style="
         + this.getChatStyle()
         + '}';
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getFormatArgs() {
      return this.formatArgs;
   }
}
