package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.cosmetic.Cosmetic;
import com.cheatbreaker.client.cosmetic.CosmeticType;
import com.cheatbreaker.client.cosmetic.RecentCosmeticsManager;
import com.cheatbreaker.client.font.CustomFontRenderer;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.PlayerViewer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CosmeticItemComponent extends SettingComponent {
   public boolean flag;
   public ColorAnimation colorAnimation = new ColorAnimation(0, 788529152);
   public static ResourceLocation star21Png = new ResourceLocation("client/icons/star-21.png");
   public Cosmetic cosmetic;
   public static ResourceLocation starFilled21Png = new ResourceLocation("client/icons/star-filled-21.png");

   public String formatText(String str) {
      CustomFontRenderer playRegular16 = CheatBreaker.getInstance().getFontManager().playRegular16;
      float f = this.width - 58.0F;
      if (playRegular16.getStringWidth(str) <= f) {
         return str;
      }

      while (!str.isEmpty() && playRegular16.getStringWidth(str + "...") > f) {
         str = str.substring(0, str.length() - 1);
      }

      return str.trim() + "...";
   }

   public CosmeticItemComponent(Cosmetic cosmetic, float f) {
      super(f);
      this.height = 30;
      this.cosmetic = cosmetic;
   }

   public boolean isFlag() {
      return this.flag;
   }

   @Override
   public void render(int n, int n2, float f) {
      boolean flag = n > this.x && n < this.x + this.width && n2 > this.y && n2 < this.y + this.height;
      Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.colorAnimation.getColor(flag).getRGB());
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.cosmetic.getCosmeticType() == CosmeticType.CAPE) {
         Minecraft.getMinecraft().renderEngine.bindTexture(this.cosmetic.getTexture());
         GlStateManager.pushMatrix();
         GL11.glTranslatef(this.x + 20, this.y + 7, 0.0F);
         GL11.glScalef(0.25F, 0.13F, 0.25F);
         RenderUtils.drawTexturedRect256(0.0F, 0.0F, 2.0F, 7.0F, 44, 120);
         GlStateManager.popMatrix();
      } else {
         RenderUtils.drawSquareTextureViaRenderEngine(this.cosmetic.getThumbnail(), 8.0F, this.x + 20, this.y + 7);
      }

      CheatBreaker.getInstance()
         .getFontManager()
         .playRegular16
         .drawString(this.formatText(this.cosmetic.getName().replace("_", " ")), this.x + 42, this.y + this.height / 2 - 5, -1342177281);
      if (this.flag || flag) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, this.flag ? 1.0F : 0.35F);
         RenderUtils.drawSquareTextureViaRenderEngine(this.flag ? starFilled21Png : star21Png, 4.0F, this.x + this.width - 14, this.y + this.height / 2 - 4);
      }

      if (this.cosmetic.isEquipped()) {
         GlStateManager.color(0.0F, 0.8F, 0.0F, 0.45F);
      } else {
         GlStateManager.color(0.0F, 0.0F, 0.0F, 0.25F);
      }

      RenderUtils.drawFilledCircle(this.x + 8, this.y + this.height / 2, 3.0);
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      boolean flag = n > this.x && n < this.x + this.width && n2 > this.y && n2 < this.y + this.height;
      EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
      if (flag) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         if (this.cosmetic.isEquipped()) {
            if (this.cosmetic.getCosmeticType() == CosmeticType.CAPE) {
               PlayerViewer.guiButtonPress = null;
               PlayerViewer.previewPlayer.setCosmetic(null);
               if (Minecraft.getMinecraft().theWorld != null) {
                  thePlayer.setCosmetic(null);
               }
            } else if (this.cosmetic.getCosmeticType() == CosmeticType.HAT) {
               PlayerViewer.previewPlayer.setCosmetic3(null);
               if (Minecraft.getMinecraft().theWorld != null) {
                  thePlayer.setCosmetic3(null);
               }
            } else {
               PlayerViewer.previewPlayer.setCosmetic2(null);
               if (Minecraft.getMinecraft().theWorld != null) {
                  thePlayer.setCosmetic2(null);
               }
            }

            this.cosmetic.setEquipped(false);
         } else if (this.cosmetic.getCosmeticType() == CosmeticType.CAPE) {
            this.cosmetic.setEquipped(true);

            for (Cosmetic cosmetic : CheatBreaker.getInstance().getCosmeticsManager().getList()) {
               if (cosmetic != this.cosmetic && cosmetic.getCosmeticType().equals(CosmeticType.CAPE)) {
                  cosmetic.setEquipped(false);
               }
            }

            this.cosmetic.setEquipped(true);
            if (Minecraft.getMinecraft().theWorld != null) {
               thePlayer.setCosmetic(this.cosmetic);
            }

            PlayerViewer.guiButtonPress = this.cosmetic.getTexture();
         } else if (this.cosmetic.getCosmeticType() == CosmeticType.HAT) {
            for (Cosmetic cosmetic : CheatBreaker.getInstance().getCosmeticsManager().getList()) {
               if (cosmetic != this.cosmetic && cosmetic.getCosmeticType() == CosmeticType.HAT) {
                  cosmetic.setEquipped(false);
               }
            }

            this.cosmetic.setEquipped(true);
            if (Minecraft.getMinecraft().theWorld != null) {
               thePlayer.setCosmetic3(this.cosmetic);
            }

            PlayerViewer.previewPlayer.setCosmetic3(this.cosmetic);
         } else {
            this.cosmetic.setEquipped(true);

            for (Cosmetic cosmetic : CheatBreaker.getInstance().getCosmeticsManager().getList()) {
               if (cosmetic != this.cosmetic && cosmetic.getCosmeticType() == CosmeticType.WINGS) {
                  cosmetic.setEquipped(false);
               }
            }

            if (Minecraft.getMinecraft().theWorld != null) {
               thePlayer.setCosmetic2(this.cosmetic);
            }

            this.cosmetic.setEquipped(true);
            PlayerViewer.previewPlayer.setCosmetic2(this.cosmetic);
         }

         if (this.cosmetic.isEquipped()) {
            RecentCosmeticsManager.markRecent(this.cosmetic);
         }

         CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendCosmeticSettings();
      }
   }

   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   public Cosmetic getCosmetic() {
      return this.cosmetic;
   }
}
