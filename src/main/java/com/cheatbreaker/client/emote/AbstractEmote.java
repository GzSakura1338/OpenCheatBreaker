package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.Animation;
import com.cheatbreaker.client.network.PlayerDataManager;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractEmote {
   public Animation animation;
   public List<Animation> animationList;
   public String text;
   public ResourceLocation clientEmotes;

   public abstract void noop(AbstractClientPlayer abstractClientPlayer, float f);

   public List<Animation> getAnimationList() {
      return this.animationList;
   }

   public AbstractEmote(String str, Animation animation) {
      this(str, str, animation);
   }

   public void handleAbstractClientPlayer(AbstractClientPlayer abstractClientPlayer) {
      if (abstractClientPlayer != null) {
         PlayerDataManager playerDataManager = CheatBreaker.getInstance().getPlayerDataManager();
         if (abstractClientPlayer.getUniqueID().equals(UUID.fromString(playerDataManager.formatText(Minecraft.getMinecraft().getSession().getPlayerID())))
            && CheatBreaker.getInstance().getEmoteManager().isChangedPerspective()) {
            Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
            CheatBreaker.getInstance().getEmoteManager().setChangedPerspective(false);
            CheatBreaker.getInstance().getEmoteManager().setKeepPerspective(false);
         }
      }
   }

   public AbstractEmote(String str, String str2, Animation animation) {
      this.animation = animation;
      animation.start();
      this.text = str;
      this.clientEmotes = new ResourceLocation("client/emotes/" + str2.toLowerCase().replace("-", "").replace(" ", "") + ".png");
   }

   public void process(long l) {
      if (this.animationList == null) {
         this.animationList = this.createArrayList();
      }

      for (Animation animation : this.animationList) {
         animation.delayStart(l);
      }
   }

   public Animation getAnimation() {
      return this.animation;
   }

   public List<Animation> createArrayList() {
      ArrayList list = new ArrayList();

      for (Class clazz = this.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
         for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) && Animation.class.isAssignableFrom(field.getType())) {
               try {
                  field.setAccessible(true);
                  Animation animation = (Animation)field.get(this);
                  if (animation != null) {
                     list.add(animation);
                  }
               } catch (IllegalAccessException e) {
               }
            }
         }
      }

      return list;
   }

   public ResourceLocation getClientEmotes() {
      return this.clientEmotes;
   }

   public String getText() {
      return this.text;
   }

   public abstract void handleAbstractClientPlayer(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f);

   public boolean check() {
      return this.animation.isFinished();
   }
}
