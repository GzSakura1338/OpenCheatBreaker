package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.util.FastTrig;
import com.cheatbreaker.client.util.SneakyThrow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class ClassicMainMenuScreen extends GuiScreen implements GuiYesNoCallback {
   public int warningY1;
   public static String OPENGL_WARNING_TEXT = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
   public static Logger logger = LogManager.getLogger();
   public DynamicTexture dynamicTexture;
   public Object threadLock = new Object();
   public int warningX2;
   public int warning1Width;
   public double swingAngle;
   public int panoramaTimer;
   public ResourceLocation logoOuterPng = new ResourceLocation("client/logo_outer.png");
   public ResourceLocation logoInnerPng = new ResourceLocation("client/logo_inner.png");
   public String splashText = "missingno";
   public float logoRotation = 0.0F;
   public ResourceLocation backgroundTexture;
   public String openGLWarning2 = OPENGL_WARNING_TEXT;
   public GuiButton menuResetdemo;
   public int warning2Width;
   public static Random RANDOM = new Random();
   public float updateCounter;
   public static ResourceLocation splashesTxt = new ResourceLocation("texts/splashes.txt");
   public int warningX1;
   public static ResourceLocation minecraftPng = new ResourceLocation("textures/gui/title/minecraft.png");
   public int warningY2;
   public String openGLWarning1;
   public String openGLWarningLink;
   public static ResourceLocation[] TITLE_PANORAMA_PATHS = new ResourceLocation[]{
      new ResourceLocation("textures/gui/title/background/panorama_0.png"),
      new ResourceLocation("textures/gui/title/background/panorama_1.png"),
      new ResourceLocation("textures/gui/title/background/panorama_2.png"),
      new ResourceLocation("textures/gui/title/background/panorama_3.png"),
      new ResourceLocation("textures/gui/title/background/panorama_4.png"),
      new ResourceLocation("textures/gui/title/background/panorama_5.png")
   };

   public void addSingleplayerMultiplayerButtons(int n, int n2) {
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, n, I18n.format("menu.singleplayer")));
      this.buttonList.add(new GuiButton(2, this.width / 2 - 100, n + n2, I18n.format("menu.multiplayer")));
   }

   @Override
   public void keyTyped(char c, int n) {
   }

   public void drawPanorama(int n, int n2, float f) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GL11.glMatrixMode(5889);
      GlStateManager.pushMatrix();
      GL11.glLoadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GL11.glMatrixMode(5888);
      GlStateManager.pushMatrix();
      GL11.glLoadIdentity();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.enableBlend();
      GL11.glDisable(3008);
      GL11.glDisable(2884);
      GL11.glDepthMask(false);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      byte b = 8;

      for (int i = 0; i < b * b; i++) {
         GlStateManager.pushMatrix();
         float f2 = ((float)(i % b) / b - 0.5F) / 64.0F;
         float f3 = ((float)(i / b) / b - 0.5F) / 64.0F;
         float f4 = 0.0F;
         GL11.glTranslatef(f2, f3, f4);
         GL11.glRotatef(MathHelper.sin((this.panoramaTimer + f) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(-(this.panoramaTimer + f) * 0.055882353F * 1.7894737F, 0.0F, 1.0F, 0.0F);

         for (int j = 0; j < 6; j++) {
            GlStateManager.pushMatrix();
            if (j == 1) {
               GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (j == 2) {
               GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (j == 3) {
               GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (j == 4) {
               GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (j == 5) {
               GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            this.mc.getTextureManager().bindTexture(TITLE_PANORAMA_PATHS[j]);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            float f5 = 0.0F;
            worldRenderer.pos(-1.0, -1.0, 1.0).tex(0.0F + f5, 0.0F + f5).color(255, 255, 255, 255 / (i + 1)).endVertex();
            worldRenderer.pos(1.0, -1.0, 1.0).tex(1.0F - f5, 0.0F + f5).color(255, 255, 255, 255 / (i + 1)).endVertex();
            worldRenderer.pos(1.0, 1.0, 1.0).tex(1.0F - f5, 1.0F - f5).color(255, 255, 255, 255 / (i + 1)).endVertex();
            worldRenderer.pos(-1.0, 1.0, 1.0).tex(0.0F + f5, 1.0F - f5).color(255, 255, 255, 255 / (i + 1)).endVertex();
            instance.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.popMatrix();
         GL11.glColorMask(true, true, true, false);
      }

      GL11.glColorMask(true, true, true, true);
      GL11.glMatrixMode(5889);
      GlStateManager.popMatrix();
      GL11.glMatrixMode(5888);
      GlStateManager.popMatrix();
      GL11.glDepthMask(true);
      GL11.glEnable(2884);
      GL11.glEnable(2929);
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      try {
         super.mouseClicked(n, n2, n3);
         if (n >= 7 && n <= 39 && n2 >= 5 && n2 <= 20) {
            MenuScreen.cycleMenuStyle();
         }

         synchronized (this.threadLock) {
            if (this.openGLWarning1.length() > 0 && n >= this.warningX1 && n <= this.warningX2 && n2 >= this.warningY1 && n2 <= this.warningY2) {
               GuiConfirmOpenLink guiConfirmOpenLink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
               guiConfirmOpenLink.disableSecurityWarning();
               this.mc.displayGuiScreen(guiConfirmOpenLink);
            }
         }
      } catch (Throwable e) {
         throw SneakyThrow.sneakyThrow(e);
      }
   }

   public void rotateAndBlurSkybox(float f) {
      this.mc.getTextureManager().bindTexture(this.backgroundTexture);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GlStateManager.enableBlend();
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GL11.glColorMask(true, true, true, false);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      GL11.glDisable(3008);
      byte b = 3;

      for (int i = 0; i < b; i++) {
         int width = this.width;
         int height = this.height;
         float f2 = (i - b / 2) / 256.0F;
         worldRenderer.pos(width, height, zLevel).tex(0.0F + f2, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F / (i + 1)).endVertex();
         worldRenderer.pos(width, 0.0, zLevel).tex(1.0F + f2, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F / (i + 1)).endVertex();
         worldRenderer.pos(0.0, 0.0, zLevel).tex(1.0F + f2, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F / (i + 1)).endVertex();
         worldRenderer.pos(0.0, height, zLevel).tex(0.0F + f2, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F / (i + 1)).endVertex();
      }

      instance.draw();
      GL11.glEnable(3008);
      GL11.glColorMask(true, true, true, true);
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   public void confirmClicked(boolean flag, int n) {
      if (flag && n == 12) {
         ISaveFormat saveLoader = this.mc.getSaveLoader();
         saveLoader.flushCache();
         saveLoader.deleteWorldDirectory("Demo_World");
         this.mc.displayGuiScreen(this);
      } else if (n == 13) {
         if (flag) {
            try {
               Class forName = Class.forName("java.awt.Desktop");
               Object invoke = forName.getMethod("getDesktop").invoke(null);
               forName.getMethod("browse", URI.class).invoke(invoke, new URI(this.openGLWarningLink));
            } catch (Throwable e) {
               logger.error("Couldn't open link", e);
            }
         }

         this.mc.displayGuiScreen(this);
      }
   }

   @Override
   public void initGui() {
      this.dynamicTexture = new DynamicTexture(256, 256);
      this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.dynamicTexture);
      Calendar instance = Calendar.getInstance();
      instance.setTime(new Date());
      if (instance.get(2) + 1 == 11 && instance.get(5) == 9) {
         this.splashText = "Happy birthday, ez!";
      } else if (instance.get(2) + 1 == 6 && instance.get(5) == 1) {
         this.splashText = "Happy birthday, Notch!";
      } else if (instance.get(2) + 1 == 12 && instance.get(5) == 24) {
         this.splashText = "Merry X-mas!";
      } else if (instance.get(2) + 1 == 1 && instance.get(5) == 1) {
         this.splashText = "Happy new year!";
      } else if (instance.get(2) + 1 == 10 && instance.get(5) == 31) {
         this.splashText = "OOoooOOOoooo! Spooky!";
      }

      boolean flag = true;
      int n = this.height / 4 + 48;
      if (this.mc.isDemo()) {
         this.addDemoButtons(n, 24);
      } else {
         this.addSingleplayerMultiplayerButtons(n, 24);
      }

      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, n + 48, 98, 20, I18n.format("menu.options")));
      this.buttonList.add(new GuiButton(4, this.width / 2 + 2, n + 48, 98, 20, I18n.format("menu.quit")));
      this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, n + 48));
      Object threadLock = this.threadLock;
      Object threadLock2 = this.threadLock;
      synchronized (this.threadLock) {
         this.warning1Width = this.fontRendererObj.getStringWidth(this.openGLWarning1);
         this.warning2Width = this.fontRendererObj.getStringWidth(this.openGLWarning2);
         int max = Math.max(this.warning1Width, this.warning2Width);
         this.warningX1 = (this.width - max) / 2;
         this.warningY1 = this.buttonList.get(0).yPosition - 24;
         this.warningX2 = this.warningX1 + max;
         this.warningY2 = this.warningY1 + 24;
      }
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.id == 0) {
         this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
      }

      if (guiButton.id == 5) {
         this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
      }

      if (guiButton.id == 1) {
         this.mc.displayGuiScreen(new GuiSelectWorld(this));
      }

      if (guiButton.id == 2) {
         this.mc.displayGuiScreen(new GuiMultiplayer(this));
      }

      if (guiButton.id == 14) {
         this.switchToRealms();
      }

      if (guiButton.id == 4) {
         this.mc.shutdown();
      }

      if (guiButton.id == 11) {
         this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
      }

      WorldInfo worldInfo;
      if (guiButton.id == 12 && (worldInfo = this.mc.getSaveLoader().getWorldInfo("Demo_World")) != null) {
         GuiYesNo makeDeleteWorldYesNo = GuiSelectWorld.makeDeleteWorldYesNo(this, worldInfo.getWorldName(), 12);
         this.mc.displayGuiScreen(makeDeleteWorldYesNo);
      }
   }

   public void renderSkybox(int n, int n2, float f) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GL11.glViewport(0, 0, 256, 256);
      this.drawPanorama(n, n2, f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.rotateAndBlurSkybox(f);
      this.mc.getFramebuffer().bindFramebuffer(true);
      GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      float f2 = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
      float f3 = this.height * f2 / 256.0F;
      float f4 = this.width * f2 / 256.0F;
      int width = this.width;
      int height = this.height;
      worldRenderer.pos(0.0, height, zLevel).tex(0.5F - f3, 0.5F + f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(width, height, zLevel).tex(0.5F - f3, 0.5F - f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(width, 0.0, zLevel).tex(0.5F + f3, 0.5F - f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(0.0, 0.0, zLevel).tex(0.5F + f3, 0.5F + f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      instance.draw();
   }

   public void switchToRealms() {
      RealmsBridge realmsBridge = new RealmsBridge();
      realmsBridge.switchToRealms(this);
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      GlStateManager.disableAlpha();
      this.renderSkybox(n, n2, f);
      GlStateManager.enableAlpha();
      short s = 274;
      int n3 = this.width / 2 - s / 2;
      boolean flag = true;
      this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
      this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
      GlStateManager.pushMatrix();
      GL11.glTranslatef(this.width / 2 - 40, this.height / 4 - 40, 0.0F);
      byte b = 40;
      GL11.glTranslatef(b, b, b);
      GL11.glRotatef(this.logoRotation, 0.0F, 0.0F, 1.0F);
      GL11.glTranslatef(-b, -b, -b);
      RenderUtils.drawSquareTextureViaRenderEngine(this.logoOuterPng, b, 0.0F, 0.0F);
      GlStateManager.popMatrix();
      RenderUtils.drawSquareTextureViaRenderEngine(this.logoInnerPng, 40.0F, this.width / 2 - 40, this.height / 4 - 38);
      GlStateManager.pushMatrix();
      GL11.glTranslatef(this.width / 2 + 90, 70.0F, 0.0F);
      GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      float f2 = 1.8F
         - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * 3.998391F * 0.78571427F * 2.0F) * 0.09589041F * 1.0428572F);
      f2 = f2 * 100.0F / (this.fontRendererObj.getStringWidth(this.splashText) + 32);
      GL11.glScalef(f2, f2, f2);
      GlStateManager.popMatrix();
      String str = "CheatBreaker (" + CheatBreaker.getInstance().getCommitId() + "/" + CheatBreaker.getInstance().getBranch() + ")";
      this.drawString(this.fontRendererObj, str, 2, this.height - 10, -1);
      String str2 = "Copyright Mojang AB. Do not distribute!";
      this.drawString(this.fontRendererObj, str2, this.width - this.fontRendererObj.getStringWidth(str2) - 2, this.height - 10, -1);
      if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0) {
         drawRect(this.warningX1 - 2, this.warningY1 - 2, this.warningX2 + 2, this.warningY2 - 1, 1428160512);
         this.drawString(this.fontRendererObj, this.openGLWarning1, this.warningX1, this.warningY1, -1);
         this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.warning2Width) / 2, this.buttonList.get(0).yPosition - 12, -1);
      }

      super.drawScreen(n, n2, f);
   }

   public ClassicMainMenuScreen() {
      BufferedReader bufferedReader = null;

      try {
         ArrayList list = new ArrayList();
         bufferedReader = new BufferedReader(
            new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashesTxt).getInputStream(), Charsets.UTF_8)
         );

         String str;
         while ((str = bufferedReader.readLine()) != null) {
            if (!(str = str.trim()).isEmpty()) {
               list.add(str);
            }
         }

         if (!list.isEmpty()) {
            do {
               this.splashText = (String)list.get(RANDOM.nextInt(list.size()));
            } while (this.splashText.hashCode() == 125780783);
         }
      } catch (IOException e) {
      } finally {
         if (bufferedReader != null) {
            try {
               bufferedReader.close();
            } catch (IOException e) {
            }
         }
      }

      this.updateCounter = RANDOM.nextFloat();
      this.openGLWarning1 = "";
      if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
         this.openGLWarning1 = I18n.format("title.oldgl1");
         this.openGLWarning2 = I18n.format("title.oldgl2");
         this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
      }
   }

   public void addDemoButtons(int n, int n2) {
      this.buttonList.add(new GuiButton(11, this.width / 2 - 100, n, I18n.format("menu.playdemo")));
      this.menuResetdemo = new GuiButton(12, this.width / 2 - 100, n + n2, I18n.format("menu.resetdemo"));
      this.buttonList.add(this.menuResetdemo);
      ISaveFormat saveLoader = this.mc.getSaveLoader();
      WorldInfo worldInfo = saveLoader.getWorldInfo("Demo_World");
      if (worldInfo == null) {
         this.menuResetdemo.enabled = false;
      }
   }

   @Override
   public void updateScreen() {
      this.panoramaTimer++;
      this.swingAngle += 0.06283185307179587;
      this.logoRotation = (float)((FastTrig.sin(this.swingAngle) / 2.0 + 0.5) * 180.0);
   }
}
