package net.minecraft.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;
import net.optifine.CustomLoadingScreen;
import net.optifine.CustomLoadingScreens;
import net.optifine.reflect.Reflector;

public class LoadingScreenRenderer implements IProgressUpdate {
   public String message = "";
   public String currentlyDisplayedText = "";
   public long systemTime = Minecraft.getSystemTime();
   public Minecraft mc;
   public boolean loadingSuccess;
   public ScaledResolution scaledResolution;
   public Framebuffer framebuffer;

   public LoadingScreenRenderer(Minecraft mcIn) {
      this.mc = mcIn;
      this.scaledResolution = new ScaledResolution(mcIn);
      this.framebuffer = new Framebuffer(mcIn.displayWidth, mcIn.displayHeight, false);
      this.framebuffer.setFramebufferFilter(9728);
   }

   @Override
   public void resetProgressAndMessage(String message) {
      this.loadingSuccess = false;
      this.displayString(message);
   }

   @Override
   public void displaySavingString(String message) {
      this.loadingSuccess = true;
      this.displayString(message);
   }

   public void displayString(String message) {
      this.currentlyDisplayedText = message;
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         if (OpenGlHelper.isFramebufferEnabled()) {
            int scaleFactor = this.scaledResolution.getScaleFactor();
            GlStateManager.ortho(0.0, this.scaledResolution.getScaledWidth() * scaleFactor, this.scaledResolution.getScaledHeight() * scaleFactor, 0.0, 100.0, 300.0);
         } else {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            GlStateManager.ortho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, 100.0, 300.0);
         }

         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -200.0F);
      }
   }

   @Override
   public void displayLoadingString(String message) {
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         this.systemTime = 0L;
         this.message = message;
         this.setLoadingProgress(-1);
         this.systemTime = 0L;
      }
   }

   @Override
   public void setLoadingProgress(int progress) {
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         long systemTime = Minecraft.getSystemTime();
         if (systemTime - this.systemTime >= 100L) {
            this.systemTime = systemTime;
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            int scaleFactor = scaledResolution.getScaleFactor();
            int scaledWidth = scaledResolution.getScaledWidth();
            int scaledHeight = scaledResolution.getScaledHeight();
            if (OpenGlHelper.isFramebufferEnabled()) {
               this.framebuffer.framebufferClear();
            } else {
               GlStateManager.clear(256);
            }

            this.framebuffer.bindFramebuffer(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, 100.0, 300.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);
            if (!OpenGlHelper.isFramebufferEnabled()) {
               GlStateManager.clear(16640);
            }

            boolean flag = true;
            if (Reflector.FMLClientHandler_handleLoadingScreen.exists()) {
               Object obj = Reflector.call(Reflector.FMLClientHandler_instance);
               if (obj != null) {
                  flag = !Reflector.callBoolean(obj, Reflector.FMLClientHandler_handleLoadingScreen, scaledResolution);
               }
            }

            if (flag) {
               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               CustomLoadingScreen customLoadingScreen = CustomLoadingScreens.getCustomLoadingScreen();
               if (customLoadingScreen != null) {
                  customLoadingScreen.drawBackground(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
               } else {
                  this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
                  float f = 32.0F;
                  worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                  worldRenderer.pos(0.0, scaledHeight, 0.0).tex(0.0, scaledHeight / f).color(64, 64, 64, 255).endVertex();
                  worldRenderer.pos(scaledWidth, scaledHeight, 0.0).tex(scaledWidth / f, scaledHeight / f).color(64, 64, 64, 255).endVertex();
                  worldRenderer.pos(scaledWidth, 0.0, 0.0).tex(scaledWidth / f, 0.0).color(64, 64, 64, 255).endVertex();
                  worldRenderer.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(64, 64, 64, 255).endVertex();
                  instance.draw();
               }

               if (progress >= 0) {
                  byte b = 100;
                  byte b2 = 2;
                  int n = scaledWidth / 2 - b / 2;
                  int n2 = scaledHeight / 2 + 16;
                  GlStateManager.disableTexture2D();
                  worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                  worldRenderer.pos(n, n2, 0.0).color(128, 128, 128, 255).endVertex();
                  worldRenderer.pos(n, n2 + b2, 0.0).color(128, 128, 128, 255).endVertex();
                  worldRenderer.pos(n + b, n2 + b2, 0.0).color(128, 128, 128, 255).endVertex();
                  worldRenderer.pos(n + b, n2, 0.0).color(128, 128, 128, 255).endVertex();
                  worldRenderer.pos(n, n2, 0.0).color(128, 255, 128, 255).endVertex();
                  worldRenderer.pos(n, n2 + b2, 0.0).color(128, 255, 128, 255).endVertex();
                  worldRenderer.pos(n + progress, n2 + b2, 0.0).color(128, 255, 128, 255).endVertex();
                  worldRenderer.pos(n + progress, n2, 0.0).color(128, 255, 128, 255).endVertex();
                  instance.draw();
                  GlStateManager.enableTexture2D();
               }

               GlStateManager.enableBlend();
               GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               this.mc
                  .fontRendererObj
                  .drawStringWithShadow(
                     this.currentlyDisplayedText, (scaledWidth - this.mc.fontRendererObj.getStringWidth(this.currentlyDisplayedText)) / 2, scaledHeight / 2 - 4 - 16, 16777215
                  );
               this.mc
                  .fontRendererObj
                  .drawStringWithShadow(this.message, (scaledWidth - this.mc.fontRendererObj.getStringWidth(this.message)) / 2, scaledHeight / 2 - 4 + 8, 16777215);
            }

            this.framebuffer.unbindFramebuffer();
            if (OpenGlHelper.isFramebufferEnabled()) {
               this.framebuffer.framebufferRender(scaledWidth * scaleFactor, scaledHeight * scaleFactor);
            }

            this.mc.updateDisplay();

            try {
               Thread.yield();
            } catch (Exception e) {
            }
         }
      }
   }

   @Override
   public void setDoneWorking() {
   }
}
