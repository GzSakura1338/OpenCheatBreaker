package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.util.mumble.MumbleIdentity;
import com.cheatbreaker.client.voice.VoiceChatPlayer;
import com.cheatbreaker.client.voice.VoicePushToTalkHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class VoiceChatUIMod extends AbstractHudMod {
   public static final float CONSTANT_18_0 = 18.0F;
   public static List<VoiceChatEntry> voiceChatEntryList = Arrays.asList(
      new VoiceChatEntry("Notch", false, true), new VoiceChatEntry("dinnerbone", false, true)
   );
   public static final float CONSTANT_110_0 = 110.0F;
   public static final float CONSTANT_20_0 = 20.0F;

   public static String createStringBuilder(String str) {
      return "u:" + str.replace("-", "").toLowerCase(Locale.ROOT);
   }

   public List<VoiceChatEntry> getList() {
      VoicePushToTalkHandler voicePushToTalkHandler = this.cheatBreaker.getModManager().voicePushToTalkHandler;
      boolean flag = this.cheatBreaker.getNetHandler().isVoiceEnabled() && this.cheatBreaker.getNetHandler().getVoiceChannels() != null;
      List talking = this.cheatBreaker.getModManager().mumbleLinkMod.getTalking();
      ArrayList list = new ArrayList();
      HashSet set = new HashSet();
      String username = this.mc.getSession().getUsername();
      String createStringBuilder3 = createStringBuilder(this.mc.thePlayer != null ? this.mc.thePlayer.getUniqueID().toString() : this.mc.getSession().getPlayerID());
      boolean flag2 = flag && voicePushToTalkHandler.isFlag();

      for (MumbleIdentity mumbleIdentity : (Iterable<MumbleIdentity>)talking) {
         flag2 |= checkMumbleIdentity(mumbleIdentity, createStringBuilder3, username);
      }

      if (flag2) {
         list.add(new VoiceChatEntry(username, true, true));
         set.add(createStringBuilder2(username));
         set.add(createStringBuilder3);
      }

      if (flag) {
         for (VoiceChatPlayer voiceChatPlayer : voicePushToTalkHandler.unmodifiableMap().keySet()) {
            String createStringBuilder22 = createStringBuilder2(voiceChatPlayer.getName());
            String createStringBuilder2 = createStringBuilder(voiceChatPlayer.getUuid().toString());
            if (!set.contains(createStringBuilder22) && !set.contains(createStringBuilder2)) {
               set.add(createStringBuilder22);
               set.add(createStringBuilder2);
               list.add(new VoiceChatEntry(voiceChatPlayer.getName(), false, true));
            }
         }
      }

      for (MumbleIdentity mumbleIdentity : (Iterable<MumbleIdentity>)talking) {
         String createStringBuilder22 = createStringBuilder2(mumbleIdentity.getName());
         String str = mumbleIdentity.getUuid() == null ? null : createStringBuilder(mumbleIdentity.getUuid());
         if (!checkMumbleIdentity(mumbleIdentity, createStringBuilder3, username) && !set.contains(createStringBuilder22) && (str == null || !set.contains(str))) {
            set.add(createStringBuilder22);
            if (str != null) {
               set.add(str);
            }

            list.add(new VoiceChatEntry(mumbleIdentity.getName(), false, mumbleIdentity.getUuid() != null));
         }
      }

      return list;
   }

   public static boolean checkMumbleIdentity(MumbleIdentity mumbleIdentity, String str, String str2) {
      return mumbleIdentity.getUuid() != null ? createStringBuilder(mumbleIdentity.getUuid()).equals(str) : mumbleIdentity.getName().equalsIgnoreCase(str2);
   }

   public VoiceChatUIMod() {
      super("Voice Chat UI");
      this.setAnchorPointAndDefault(AnchorPoint.RIGHT_TOP);
      this.setTranslationAndDefault(-8.0F, 8.0F);
      this.setEnabled(false);
      this.setApplyDefaultColorOnEnable(false);
      this.setUseDefaultTranslation(true);
      this.setDescription("Shows who is currently speaking in voice chat.");
      this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
      this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
      this.setEnabledByDefault(true);
   }

   public static String createStringBuilder2(String str) {
      return "n:" + str.toLowerCase(Locale.ROOT);
   }

   public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) {
      if (this.getList().isEmpty()) {
         this.setHudSize(110.0F, getHeight(voiceChatEntryList.size()));
         GlStateManager.pushMatrix();
         this.applyHudTransformForSize(scaledResolutionEvent7.getScaledResolution());
         float f = 0.0F;

         for (VoiceChatEntry voiceChatEntry : voiceChatEntryList) {
            this.drawEntry(voiceChatEntry.name, f, voiceChatEntry.speaking, voiceChatEntry.showHead);
            f += 20.0F;
         }

         GlStateManager.popMatrix();
      }
   }

   public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
      if (this.shouldRender()) {
         List list = this.getList();
         int n = list.isEmpty() ? voiceChatEntryList.size() : list.size();
         this.setHudSize(110.0F, getHeight(n));
         if (!list.isEmpty()) {
            GlStateManager.pushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
            float f = 0.0F;

            for (VoiceChatEntry voiceChatEntry : (Iterable<VoiceChatEntry>)list) {
               this.drawEntry(voiceChatEntry.name, f, voiceChatEntry.speaking, voiceChatEntry.showHead);
               f += 20.0F;
            }

            GlStateManager.popMatrix();
         }
      }
   }

   public void drawEntry(String str, float f, boolean flag, boolean flag2) {
      if (flag) {
         RenderUtils.drawBorderedGradientRect(0.0F, f, 110.0F, f + 18.0F, -11493284, -10176146, -11164318);
      } else {
         RenderUtils.drawBorderedGradientRect(0.0F, f, 110.0F, f + 18.0F, -1356454362, -1355664846, -1356191190);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (flag2) {
         ResourceLocation headTexture = this.cheatBreaker.getFontManager().getHeadTexture(str);
         RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, 2.0F, f + 2.0F);
      } else {
         RenderUtils.drawRoundedRectFilledPrecise(2.0, f + 2.0F, 16.0, f + 16.0F, 2.0, 1442840575);
         this.cheatBreaker.getFontManager().playRegular16.drawCenteredString("?", 9.0F, f + 4.0F, -1);
      }

      this.cheatBreaker.getFontManager().playRegular16.drawString(str, 22.0F, f + 4.0F, -1);
   }

   public static float getHeight(int n) {
      return Math.max(n, 1) * 20.0F - 2.0F;
   }
}
