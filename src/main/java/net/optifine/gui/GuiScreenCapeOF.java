package net.optifine.gui;

import com.mojang.authlib.exceptions.InvalidCredentialsException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Random;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.optifine.Config;
import net.optifine.Lang;

public class GuiScreenCapeOF extends GuiScreenOF {
   public FontRenderer fontRenderer = Config.getMinecraft().fontRendererObj;
   public String title;
   public String message;
   public long messageHideTimeMs;
   public String linkUrl;
   public GuiButtonOF buttonCopyLink;
   public GuiScreen parentScreen;

   public GuiScreenCapeOF(GuiScreen guiScreen) {
      this.parentScreen = guiScreen;
   }

   @Override
   public void initGui() {
      int n = 0;
      this.title = I18n.format("of.options.capeOF.title");
      n += 2;
      this.buttonList.add(new GuiButtonOF(210, this.width / 2 - 155, this.height / 6 + 24 * (n >> 1), 150, 20, I18n.format("of.options.capeOF.openEditor")));
      this.buttonList
         .add(new GuiButtonOF(220, this.width / 2 - 155 + 160, this.height / 6 + 24 * (n >> 1), 150, 20, I18n.format("of.options.capeOF.reloadCape")));
      n += 6;
      this.buttonCopyLink = new GuiButtonOF(
         230, this.width / 2 - 100, this.height / 6 + 24 * (n >> 1), 200, 20, I18n.format("of.options.capeOF.copyEditorLink")
      );
      this.buttonCopyLink.visible = this.linkUrl != null;
      this.buttonList.add(this.buttonCopyLink);
      n += 4;
      this.buttonList.add(new GuiButtonOF(200, this.width / 2 - 100, this.height / 6 + 24 * (n >> 1), I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.enabled) {
         if (guiButton.id == 200) {
            this.mc.displayGuiScreen(this.parentScreen);
         }

         if (guiButton.id == 210) {
            try {
               String name = this.mc.getSession().getProfile().getName();
               String replace = this.mc.getSession().getProfile().getId().toString().replace("-", "");
               String token = this.mc.getSession().getToken();
               Random random = new Random();
               Random random2 = new Random(System.identityHashCode(new Object()));
               BigInteger bigInteger = new BigInteger(128, random);
               BigInteger bigInteger2 = new BigInteger(128, random2);
               BigInteger xor = bigInteger.xor(bigInteger2);
               String str = xor.toString(16);
               this.mc.getSessionService().joinServer(this.mc.getSession().getProfile(), token, str);
               String str2 = "https://optifine.net/capeChange?u=" + replace + "&n=" + name + "&s=" + str;
               boolean openWebLink = Config.openWebLink(new URI(str2));
               if (openWebLink) {
                  this.showMessage(Lang.get("of.message.capeOF.openEditor"), 10000L);
               } else {
                  this.showMessage(Lang.get("of.message.capeOF.openEditorError"), 10000L);
                  this.setLinkUrl(str2);
               }
            } catch (InvalidCredentialsException e) {
               Config.showGuiMessage(I18n.format("of.message.capeOF.error1"), I18n.format("of.message.capeOF.error2", e.getMessage()));
               Config.warn("Mojang authentication failed");
               Config.warn(e.getClass().getName() + ": " + e.getMessage());
            } catch (Exception e) {
               Config.warn("Error opening OptiFine cape link");
               Config.warn(e.getClass().getName() + ": " + e.getMessage());
            }
         }

         if (guiButton.id == 220) {
            this.showMessage(Lang.get("of.message.capeOF.reloadCape"), 15000L);
            if (this.mc.thePlayer != null) {
               long l = 15000L;
               long l2 = System.currentTimeMillis() + l;
               this.mc.thePlayer.setReloadCapeTimeMs(l2);
            }
         }

         if (guiButton.id == 230 && this.linkUrl != null) {
            setClipboardString(this.linkUrl);
         }
      }
   }

   public void showMessage(String str, long l) {
      this.message = str;
      this.messageHideTimeMs = System.currentTimeMillis() + l;
      this.setLinkUrl((String)null);
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
      if (this.message != null) {
         this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, this.height / 6 + 60, 16777215);
         if (System.currentTimeMillis() > this.messageHideTimeMs) {
            this.message = null;
            this.setLinkUrl((String)null);
         }
      }

      super.drawScreen(n, n2, f);
   }

   public void setLinkUrl(String str) {
      this.linkUrl = str;
      this.buttonCopyLink.visible = str != null;
   }
}
