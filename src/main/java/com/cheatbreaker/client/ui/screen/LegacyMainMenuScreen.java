package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
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
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class LegacyMainMenuScreen extends GuiScreen implements GuiYesNoCallback {
   public static AtomicInteger THREAD_COUNTER = new AtomicInteger(0);
   public static Logger logger = LogManager.getLogger();
   public int warning1Width;
   public static Random RANDOM = new Random();
   public boolean unusedFlag2 = true;
   public Object threadLock = new Object();
   public int warningY2;
   public List unusedList = new ArrayList();
   public boolean unusedFlag1 = false;
   public int warningY1;
   public String openGLWarning2;
   public static ResourceLocation splashesTxt = new ResourceLocation("texts/splashes.txt");
   public DynamicTexture dynamicTexture;
   public static ResourceLocation minecraftPng = new ResourceLocation("textures/gui/title/minecraft.png");
   public static ResourceLocation[] TITLE_PANORAMA_PATHS = new ResourceLocation[]{
      new ResourceLocation("textures/gui/title/background/panorama_0.png"),
      new ResourceLocation("textures/gui/title/background/panorama_1.png"),
      new ResourceLocation("textures/gui/title/background/panorama_2.png"),
      new ResourceLocation("textures/gui/title/background/panorama_3.png"),
      new ResourceLocation("textures/gui/title/background/panorama_4.png"),
      new ResourceLocation("textures/gui/title/background/panorama_5.png")
   };
   public int panoramaTimer;
   public String splashText;
   public int warning2Width;
   public float updateCounter;
   public int warningX2;
   public ResourceLocation backgroundTexture;
   public String openGLWarning1;
   public String openGLWarningLink;
   public int warningX1;
   public static String OPENGL_WARNING_TEXT = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";

   @Override
   public void onGuiClosed() {
   }

   @Override
   public void keyTyped(char c, int n) {
   }

   public void switchToRealms() {
      RealmsBridge realmsBridge = new RealmsBridge();
      realmsBridge.switchToRealms(this);
   }

   public void drawButtons(int n, int n2) {
      for (int i = 0; i < this.buttonList.size(); i++) {
         this.buttonList.get(i).drawButton(this.mc, n, n2);
      }

      for (int i = 0; i < this.labelList.size(); i++) {
         this.labelList.get(i).drawLabel(this.mc, n, n2);
      }
   }

   public void drawPanorama(int n, int n2, float f) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.disableCull();
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      byte b = 8;
      int n3 = 64;
      CustomPanoramaProperties customPanoramaProperties = CustomPanorama.getCustomPanoramaProperties();
      if (customPanoramaProperties != null) {
         n3 = customPanoramaProperties.getBlur1();
      }

      for (int i = 0; i < n3; i++) {
         GlStateManager.pushMatrix();
         float f2 = ((float)(i % b) / b - 0.5F) / 64.0F;
         float f3 = ((float)(i / b) / b - 0.5F) / 64.0F;
         float f4 = 0.0F;
         GlStateManager.translate(f2, f3, f4);
         GlStateManager.rotate(MathHelper.sin((this.panoramaTimer + f) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-(this.panoramaTimer + f) * 0.1F, 0.0F, 1.0F, 0.0F);

         for (int j = 0; j < 6; j++) {
            GlStateManager.pushMatrix();
            if (j == 1) {
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (j == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (j == 3) {
               GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (j == 4) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (j == 5) {
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            ResourceLocation[] resourceLocations = TITLE_PANORAMA_PATHS;
            if (customPanoramaProperties != null) {
               resourceLocations = customPanoramaProperties.getPanoramaLocations();
            }

            this.mc.getTextureManager().bindTexture(resourceLocations[j]);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int n4 = 255 / (i + 1);
            float f5 = 0.0F;
            worldRenderer.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, n4).endVertex();
            worldRenderer.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, n4).endVertex();
            worldRenderer.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, n4).endVertex();
            worldRenderer.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, n4).endVertex();
            instance.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.popMatrix();
         GlStateManager.colorMask(true, true, true, false);
      }

      worldRenderer.setTranslation(0.0, 0.0, 0.0);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.enableDepth();
   }

   public void unusedHook(int n, int n2) {
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

      if (guiButton.id == 4) {
         this.mc.shutdown();
      }

      if (guiButton.id == 6 && Reflector.GuiModList_Constructor.exists()) {
         this.mc.displayGuiScreen((GuiScreen)Reflector.newInstance(Reflector.GuiModList_Constructor, this));
      }

      if (guiButton.id == 11) {
         this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
      }

      if (guiButton.id == 12) {
         ISaveFormat saveLoader = this.mc.getSaveLoader();
         WorldInfo worldInfo = saveLoader.getWorldInfo("Demo_World");
         if (worldInfo != null) {
            GuiYesNo makeDeleteWorldYesNo = GuiSelectWorld.makeDeleteWorldYesNo(this, worldInfo.getWorldName(), 12);
            this.mc.displayGuiScreen(makeDeleteWorldYesNo);
         }
      }
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      GlStateManager.disableAlpha();
      this.renderSkybox(n, n2, f);
      GlStateManager.enableAlpha();
      short s = 274;
      int n3 = this.width / 2 - s / 2;
      byte b = 30;
      this.drawGradientRect(0.0F, 0.0F, this.width, this.height, 1610612735, 805306367);
      drawRect(0.0F, 0.0F, this.width, 25.0F, -819846622);
      String str = "CheatBreaker (" + CheatBreaker.getInstance().getCommitId() + "/" + CheatBreaker.getInstance().getBranch() + ")";
      this.drawString(this.fontRendererObj, str, 2, this.height - 10, -1);
      String str2 = "Copyright Mojang AB. Do not distribute!";
      this.drawString(this.fontRendererObj, str2, this.width - this.fontRendererObj.getStringWidth(str2) - 2, this.height - 10, -1);
      if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0) {
         drawRect(this.warningX1 - 2, this.warningY1 - 2, this.warningX2 + 2, this.warningY2 - 1, 1428160512);
         this.drawString(this.fontRendererObj, this.openGLWarning1, this.warningX1, this.warningY1, -1);
         this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.warning2Width) / 2, this.buttonList.get(0).yPosition - 12, -1);
      }

      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f2 = 30.0F;
      float f3 = 15.0F;
      float f4 = 0.0F;
      float f5 = 0.0F;
      float f6 = 10.0F;
      float f7 = 5.0F;
      GlStateManager.enableBlend();
      this.mc.renderEngine.bindTexture(new ResourceLocation("client/icons/cb.png"));
      GL11.glBegin(7);
      GL11.glTexCoord2d(f4 / 5.0F, f5 / 5.0F);
      GL11.glVertex2d(f6, f7);
      GL11.glTexCoord2d(f4 / 5.0F, (f5 + 5.0F) / 5.0F);
      GL11.glVertex2d(f6, f7 + f3);
      GL11.glTexCoord2d((f4 + 5.0F) / 5.0F, (f5 + 5.0F) / 5.0F);
      GL11.glVertex2d(f6 + f2, f7 + f3);
      GL11.glTexCoord2d((f4 + 5.0F) / 5.0F, f5 / 5.0F);
      GL11.glVertex2d(f6 + f2, f7);
      GL11.glEnd();
      GlStateManager.disableBlend();
      GL11.glPopMatrix();
   }

   public LegacyMainMenuScreen() {
      this.openGLWarning2 = OPENGL_WARNING_TEXT;
      this.splashText = "missingno";
      BufferedReader bufferedReader = null;

      try {
         ArrayList newArrayList = Lists.newArrayList();
         bufferedReader = new BufferedReader(
            new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashesTxt).getInputStream(), Charsets.UTF_8)
         );

         String str;
         while ((str = bufferedReader.readLine()) != null) {
            str = str.trim();
            if (!str.isEmpty()) {
               newArrayList.add(str);
            }
         }

         if (!newArrayList.isEmpty()) {
            do {
               this.splashText = (String)newArrayList.get(RANDOM.nextInt(newArrayList.size()));
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

   public void renderSkybox(int n, int n2, float f) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GlStateManager.viewport(0, 0, 256, 256);
      this.drawPanorama(n, n2, f);
      this.rotateAndBlurSkybox(f);
      int n3 = 3;
      CustomPanoramaProperties customPanoramaProperties = CustomPanorama.getCustomPanoramaProperties();
      if (customPanoramaProperties != null) {
         n3 = customPanoramaProperties.getBlur3();
      }

      for (int i = 0; i < n3; i++) {
         this.rotateAndBlurSkybox(f);
         this.rotateAndBlurSkybox(f);
      }

      this.mc.getFramebuffer().bindFramebuffer(true);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      float f2 = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
      float f3 = this.height * f2 / 256.0F;
      float f4 = this.width * f2 / 256.0F;
      int width = this.width;
      int height = this.height;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(0.0, height, zLevel).tex(0.5F - f3, 0.5F + f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(width, height, zLevel).tex(0.5F - f3, 0.5F - f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(width, 0.0, zLevel).tex(0.5F + f3, 0.5F - f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(0.0, 0.0, zLevel).tex(0.5F + f3, 0.5F + f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      instance.draw();
   }

   public void rotateAndBlurSkybox(float f) {
      this.mc.getTextureManager().bindTexture(this.backgroundTexture);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.colorMask(true, true, true, false);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      GlStateManager.disableAlpha();
      byte b = 3;
      int n = 3;
      CustomPanoramaProperties customPanoramaProperties = CustomPanorama.getCustomPanoramaProperties();
      if (customPanoramaProperties != null) {
         n = customPanoramaProperties.getBlur2();
      }

      for (int i = 0; i < n; i++) {
         float f2 = 1.0F / (i + 1);
         int width = this.width;
         int height = this.height;
         float f3 = (i - b / 2) / 256.0F;
         worldRenderer.pos(width, height, zLevel).tex(0.0F + f3, 1.0).color(1.0F, 1.0F, 1.0F, f2).endVertex();
         worldRenderer.pos(width, 0.0, zLevel).tex(1.0F + f3, 1.0).color(1.0F, 1.0F, 1.0F, f2).endVertex();
         worldRenderer.pos(0.0, 0.0, zLevel).tex(1.0F + f3, 0.0).color(1.0F, 1.0F, 1.0F, f2).endVertex();
         worldRenderer.pos(0.0, height, zLevel).tex(0.0F + f3, 0.0).color(1.0F, 1.0F, 1.0F, f2).endVertex();
      }

      instance.draw();
      GlStateManager.enableAlpha();
      GlStateManager.colorMask(true, true, true, true);
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) throws IOException {
      super.mouseClicked(n, n2, n3);
      synchronized (this.threadLock) {
         if (this.openGLWarning1.length() > 0 && n >= this.warningX1 && n <= this.warningX2 && n2 >= this.warningY1 && n2 <= this.warningY2) {
            GuiConfirmOpenLink guiConfirmOpenLink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
            guiConfirmOpenLink.disableSecurityWarning();
            this.mc.displayGuiScreen(guiConfirmOpenLink);
         }
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   public void updateScreen() {
      this.panoramaTimer++;
   }

   @Override
   public void initGui() {
      this.dynamicTexture = new DynamicTexture(256, 256);
      this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.dynamicTexture);
      Calendar instance = Calendar.getInstance();
      instance.setTime(new Date());
      Object threadLock = this.threadLock;
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
}
