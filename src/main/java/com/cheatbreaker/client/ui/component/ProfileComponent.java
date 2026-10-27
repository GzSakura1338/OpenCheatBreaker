package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.config.Profile;
import com.cheatbreaker.client.event.CustomEvent;
import com.cheatbreaker.client.mod.impl.AutoTextMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.ui.screen.ProfileNameScreen;
import com.cheatbreaker.client.util.ClientVersion;
import java.io.File;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ProfileComponent extends SettingComponent {
   public ResourceLocation delete64Png = new ResourceLocation("client/icons/delete-64.png");
   public ResourceLocation checkmark64Png = new ResourceLocation("client/icons/checkmark-64.png");
   public ResourceLocation rightPng = new ResourceLocation("client/icons/right.png");
   public ResourceLocation pencil64Png = new ResourceLocation("client/icons/pencil-64.png");
   public int intValue = 0;
   public Panel panel;
   public int intValue2;
   public Profile profile;
   public Button delete64Png2;
   public Button pencil64Png2;

   @Override
   public void render(int n, int n2, float f) {
      boolean flag = n > (this.x + 12) * 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue()
         && n < (this.x + this.width - (this.profile.isFlag() ? 0 : 30)) * 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue()
         && this.isMouseOver(n, n2);
      byte b = 75;
      Gui.drawRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, this.theme.getPrimaryTextColorValue2());
      if (flag) {
         if (this.intValue < b) {
            float animationStep = ModsScreen.animationStep(790.0F);
            this.intValue = (int)(this.intValue + animationStep);
            if (this.intValue > b) {
               this.intValue = b;
            }
         }
      } else if (this.intValue > 0) {
         float animationStep = ModsScreen.animationStep(790.0F);
         this.intValue = this.intValue - animationStep < 0.0F ? 0 : (int)(this.intValue - animationStep);
      }

      if (this.intValue > 0) {
         float f2 = (float)this.intValue / b * 100.0F;
         Gui.drawRect(
            this.x + 12,
            (int)(this.y + (this.height - this.height * f2 / 100.0F)),
            this.x + this.width - (this.profile.isFlag() ? 0 : 30),
            this.y + this.height,
            this.intValue2
         );
      }

      boolean flag2 = n > this.x * this.scale
         && n < (this.x + 12) * this.scale
         && n2 >= (this.y + this.scrollOffsetY) * this.scale
         && n2 <= (this.y + this.height / 2 + this.scrollOffsetY) * this.scale;
      boolean flag3 = n > this.x * this.scale
         && n < (this.x + 12) * this.scale
         && n2 > (this.y + this.height / 2 + this.scrollOffsetY) * this.scale
         && n2 < (this.y + this.height + this.scrollOffsetY) * this.scale;
      float red = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
      float green = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
      float blue = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
      GlStateManager.color(red, green, blue, 0.35F);
      float f2 = 2.5F;
      if (!this.profile.isFlag()) {
         boolean flag4 = false;
         boolean flag5 = false;
         ProfilesPanel panel = (ProfilesPanel)this.panel;
         if (panel.profileComponentList.indexOf(this) != 0 && panel.profileComponentList.indexOf(this) > 1) {
            flag4 = true;
            GlStateManager.pushMatrix();
            if (flag2) {
               GlStateManager.color(red, green, blue, 0.65F);
            }

            GL11.glTranslatef(this.x + 6 - f2, this.y + 7.0F, 0.0F);
            GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, f2, -1.0F, 0.0F);
            GlStateManager.popMatrix();
            GlStateManager.color(red, green, blue, 0.35F);
         }

         if (panel.profileComponentList.indexOf(this) != panel.profileComponentList.size() - 1) {
            flag5 = true;
            GlStateManager.pushMatrix();
            if (flag3) {
               GlStateManager.color(red, green, blue, 0.65F);
            }

            GL11.glTranslatef(this.x + 6 + f2, this.y + 7.0F, 0.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, f2, 2.0F, 0.0F);
            GlStateManager.popMatrix();
         }

         if (!flag4 && !flag5) {
            RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 2.5F, this.x + 4, this.y + 6.0F);
         }
      } else {
         RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 2.5F, this.x + 4, this.y + 6.0F);
      }

      if (CheatBreaker.getInstance().getConfigManager().profile == this.profile) {
         CheatBreaker.getInstance()
            .getFontManager()
            .playBold18
            .drawString(this.profile.getText().toUpperCase(), this.x + 16.0F, this.y + 3, this.theme.getTextColor2Value2());
      } else {
         CheatBreaker.getInstance()
            .getFontManager()
            .playRegular16
            .drawString(this.profile.getText().toUpperCase(), this.x + 16.0F, this.y + 3.5F, this.theme.getTextColor2Value2());
      }

      if (CheatBreaker.getInstance().getConfigManager().profile == this.profile) {
         CheatBreaker.getInstance()
            .getFontManager()
            .playRegular14
            .drawString(
               " (Active)",
               this.x + 17.0F + CheatBreaker.getInstance().getFontManager().playBold18.getStringWidth(this.profile.getText().toUpperCase()),
               this.y + 4.0F,
               this.theme.getTextColor4Value2()
            );
      }

      if (!this.profile.isFlag()) {
         this.pencil64Png2.setBounds(this.x + this.width - 29, this.y + 1, 14, 14);
         this.pencil64Png2.render(n, n2, f);
         this.pencil64Png2.scrollOffsetY = this.panel.intValue;
         this.delete64Png2.setBounds(this.x + this.width - 13, this.y + 1, 14, 14);
         this.delete64Png2.render(n, n2, f);
         this.delete64Png2.scrollOffsetY = this.panel.intValue;
      }
   }

   public ProfileComponent(Panel panel, int n, Profile profile, float f) {
      super(f);
      this.panel = panel;
      this.intValue2 = n;
      this.profile = profile;
      this.delete64Png2 = new Button(
         null,
         null,
         "delete-64.png",
         this.x + 4,
         this.y + this.height - 20,
         this.x + this.width - 4,
         this.y + this.height - 6,
         this.theme.getNormalHighlightColorValue(),
         f,
         false,
         true
      );
      this.delete64Png2.setSmallIcon(true);
      this.pencil64Png2 = new Button(
         null,
         null,
         "pencil-64.png",
         this.x + 4,
         this.y + this.height - 20,
         this.x + this.width - 4,
         this.y + this.height - 6,
         this.theme.getNormalHighlightColorValue(),
         f,
         false,
         true
      );
      this.pencil64Png2.setSmallIcon(true);
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      boolean isMouseOver = this.delete64Png2.isMouseOver(n, n2);
      boolean isMouseOver2 = this.pencil64Png2.isMouseOver(n, n2);
      boolean flag = n > this.x * this.scale
         && n < (this.x + 12) * this.scale
         && n2 >= (this.y + this.scrollOffsetY) * this.scale
         && n2 <= (this.y + this.height / 2 + this.scrollOffsetY) * this.scale;
      boolean flag2 = n > this.x * this.scale
         && n < (this.x + 12) * this.scale
         && n2 > (this.y + this.height / 2 + this.scrollOffsetY) * this.scale
         && n2 < (this.y + this.height + this.scrollOffsetY) * this.scale;
      ProfilesPanel panel = (ProfilesPanel)this.panel;
      if (this.profile.isFlag() || !flag && !flag2) {
         if (!this.profile.isFlag() && isMouseOver) {
            File file = new File(
               Minecraft.getMinecraft().mcDataDir
                  + File.separator
                  + "config"
                  + File.separator
                  + "cheatbreaker-client-"
                  + ClientVersion.text
                  + "-"
                  + CheatBreaker.getInstance().getBranch()
                  + File.separator
                  + "profiles"
            );
            File file2 = new File(file + File.separator + this.profile.getText().toLowerCase() + ".json");
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            if (CheatBreaker.getInstance().getConfigManager().profile == this.profile) {
               CheatBreaker.getInstance().getConfigManager().profile = CheatBreaker.getInstance().profiles.get(0);
               CheatBreaker.getInstance().configFileManager.loadProfile(CheatBreaker.getInstance().getConfigManager().profile.getText());
            }

            if (!this.profile.isFlag() && file2.delete()) {
               CheatBreaker.getInstance().profiles.removeIf(profile -> profile == this.profile);
               panel.profileComponentList.removeIf(profileComponent -> profileComponent == this);
            }
         } else if (isMouseOver2) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            Minecraft.getMinecraft()
               .displayGuiScreen(new ProfileNameScreen(this.profile, ModsScreen.modsScreen, (ProfilesPanel)this.panel, this.intValue2, this.scale));
         } else if (CheatBreaker.getInstance().getConfigManager().profile != null && CheatBreaker.getInstance().getConfigManager().profile != this.profile) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            CheatBreaker.getInstance().configFileManager.saveProfile(CheatBreaker.getInstance().getConfigManager().profile.getText());
            AutoTextMod autoTextMod = CheatBreaker.getInstance().getModManager().autoTextMod;
            autoTextMod.getSettings().removeIf(setting -> setting.getName().startsWith("Hot key"));
            CheatBreaker.getInstance().getModManager().autoTextMod.hotkeySettings.clear();
            CheatBreaker.getInstance().getModManager().autoTextMod.nextHotkeyIndex = 1;
            CheatBreaker.getInstance().getConfigManager().profile = this.profile;
            CheatBreaker.getInstance().configFileManager.loadProfile(CheatBreaker.getInstance().getConfigManager().profile.getText());
            CheatBreaker.getInstance().getEventBus().handleEvent(new CustomEvent(this.profile));
            CheatBreaker.getInstance().getUiManager().run5();
         }
      } else {
         if (flag && ((ProfilesPanel)this.panel).profileComponentList.indexOf(this) != 0 && ((ProfilesPanel)this.panel).profileComponentList.indexOf(this) > 1) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.profile.intValue = panel.profileComponentList.indexOf(this) - 1;
            panel.profileComponentList.get(panel.profileComponentList.indexOf(this) - 1).profile.intValue = panel.profileComponentList.indexOf(this);
            Collections.swap(panel.profileComponentList, panel.profileComponentList.indexOf(this), panel.profileComponentList.indexOf(this) - 1);
         }

         if (flag2 && panel.profileComponentList.indexOf(this) != panel.profileComponentList.size() - 1) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.profile.intValue = panel.profileComponentList.indexOf(this) + 1;
            panel.profileComponentList.get(panel.profileComponentList.indexOf(this) + 1).profile.intValue = panel.profileComponentList.indexOf(this);
            Collections.swap(panel.profileComponentList, panel.profileComponentList.indexOf(this), panel.profileComponentList.indexOf(this) + 1);
         }
      }
   }
}
