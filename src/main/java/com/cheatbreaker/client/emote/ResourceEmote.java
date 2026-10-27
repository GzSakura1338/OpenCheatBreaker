package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.LinearAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.util.ResourceLocation;

public class ResourceEmote extends AbstractEmote {
   public String text2;
   public boolean flag;

   @Override
   public void handleAbstractClientPlayer(AbstractClientPlayer abstractClientPlayer) {
      super.handleAbstractClientPlayer(abstractClientPlayer);
      EmotePlayHelper.playEmoteByName(null, abstractClientPlayer);
   }

   public ResourceEmote(String str) {
      super(createStringBuilder(str), new LinearAnimation(computeLong(str)));
      this.text2 = str;
      EmoteDefinition resolve = EmoteRegistry.resolve(str);
      this.flag = resolve != null && resolve.looping;
   }

   @Override
   public ResourceLocation getClientEmotes() {
      return new ResourceLocation("client/emotes/" + this.text2 + ".png");
   }

   @Override
   public void noop(AbstractClientPlayer abstractClientPlayer, float f) {
   }

   @Override
   public void handleAbstractClientPlayer(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
   }

   public static String createStringBuilder(String str) {
      String[] split = str.split("_");
      StringBuilder sb = new StringBuilder();

      for (String str2 : split) {
         if (!str2.isEmpty()) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append(Character.toUpperCase(str2.charAt(0))).append(str2.substring(1));
         }
      }

      return sb.toString();
   }

   @Override
   public boolean check() {
      return !this.flag && super.check();
   }

   public static long computeLong(String str) {
      EmoteDefinition resolve = EmoteRegistry.resolve(str);
      int n = resolve != null ? resolve.durationTicks : 100;
      return Math.max(n, 1) * 50L;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public String getText7() {
      return this.text2;
   }
}
