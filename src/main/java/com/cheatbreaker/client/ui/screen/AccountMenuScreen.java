package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public abstract class AccountMenuScreen extends UIScreen {
   public static int intValue = 4100;
   public String text = "";
   public List<LauncherAccount> launcherAccountList = new ArrayList<>();
   public static String text2 = !CheatBreaker.getInstance().getClientBuildType().isEmpty()
         && !CheatBreaker.getInstance().getClientBuildType().equalsIgnoreCase("Production")
      ? CheatBreaker.getInstance().getClientBuildType() + " "
      : "";
   public static String text3 = CheatBreaker.getInstance().getCommitId() + "/" + CheatBreaker.getInstance().getBranch();
   public File file = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "cheatbreaker_accounts.json");
   public static final String TEXT_COPYRIGHT_MOJANG_AB_DO_N = "Copyright Mojang AB. Do not distribute!";
   public DynamicTexture dynamicTexture;
   public static String text4 = createStringBuilder();
   public ResourceLocation resourceLocation;
   public ResourceLocation[] resourceLocationArray;
   public static String text5 = "CheatBreaker " + text2 + "(" + text4 + ")";

   public void run12() {
      this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
   }

   @Override
   public void mouseClickedScaled(float f, float f2, int n) {
      if (f >= 8.0F && f <= 26.0F && f2 >= 6.0F && f2 <= 26.0F) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         MenuScreen.cycleMenuStyle();
      }
   }

   @Override
   public void initGui() {
      super.initGui();
      int n = CheatBreaker.getInstance().getKeybindSettings().getMainMenuStyle().ordinal();
      MainMenuStyle mainMenuStyle = MainMenuStyle.values()[n];
      if (mainMenuStyle == MainMenuStyle.MAIN) {
         this.resourceLocationArray = PanoramaTextures.resourceLocationArray;
      } else {
         this.resourceLocationArray = PanoramaTextures.resourceLocationArray2;
      }

      this.dynamicTexture = new DynamicTexture(256, 256);
      this.resourceLocation = this.mc.getTextureManager().getDynamicTextureLocation("background", this.dynamicTexture);
   }

   public void render(int n, int n2, float f) {
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
         GL11.glRotatef(MathHelper.sin((intValue + f) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(-(intValue + f) * 0.39240506F * 0.2548387F, 0.0F, 1.0F, 0.0F);

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

            this.mc.getTextureManager().bindTexture(this.resourceLocationArray[j]);
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

   public void render2(int n, int n2, float f) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GL11.glViewport(0, 0, 256, 256);
      this.render(n, n2, f);
      this.render();
      this.render();
      this.render();
      this.render();
      this.render();
      this.render();
      this.render();
      this.mc.getFramebuffer().bindFramebuffer(true);
      GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      float f2 = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
      float f3 = this.height * f2 / 256.0F;
      float f4 = this.width * f2 / 256.0F;
      float width = this.width;
      float height = this.height;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(0.0, height, zLevel).tex(0.5F - f3, 0.5F + f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(width, height, zLevel).tex(0.5F - f3, 0.5F - f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(width, 0.0, zLevel).tex(0.5F + f3, 0.5F - f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(0.0, 0.0, zLevel).tex(0.5F + f3, 0.5F + f4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      instance.draw();
   }

   public void render() {
      this.mc.getTextureManager().bindTexture(this.resourceLocation);
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
         float width = this.width;
         float height = this.height;
         float f = (i - b / 2) / 256.0F;
         worldRenderer.pos(width, height, zLevel).tex(0.0F + f, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F / (i + 1)).endVertex();
         worldRenderer.pos(width, 0.0, zLevel).tex(1.0F + f, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F / (i + 1)).endVertex();
         worldRenderer.pos(0.0, 0.0, zLevel).tex(1.0F + f, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F / (i + 1)).endVertex();
         worldRenderer.pos(0.0, height, zLevel).tex(0.0F + f, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F / (i + 1)).endVertex();
      }

      instance.draw();
      GL11.glEnable(3008);
      GL11.glColorMask(true, true, true, true);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      intValue++;
   }

   public static String createStringBuilder() {
      String str = !CheatBreaker.isAprilFoolsEnabled() ? CheatBreaker.getInstance().getClientVersion() : "2069.4.20";
      if (CheatBreaker.getInstance().getClientBuildType().equalsIgnoreCase("Beta")) {
         return str + "-" + text3;
      } else {
         return !CheatBreaker.getInstance().getClientBuildType().equalsIgnoreCase("Production")
               && !CheatBreaker.getInstance().getClientBuildType().equalsIgnoreCase("Preview")
            ? text3
            : str;
      }
   }

   @Override
   public void keyTyped(char c, int n) throws java.io.IOException {
      if (n != 1 || !(this.mc.currentScreen instanceof MainMenuScreen)) {
         super.keyTyped(c, n);
      }
   }
}
