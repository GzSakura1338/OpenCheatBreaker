package com.cheatbreaker.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class WorldLabel {
   public static List<WorldLabel> labels = new ArrayList<>();
   public UUID playerId;
   public String[] lines;
   public double x;
   public double y;
   public double z;

   public double getX() {
      return this.x;
   }

   public double getZ() {
      return this.z;
   }

   public WorldLabel(UUID uuid, double d, double d2, double d3) {
      this.playerId = uuid;
      this.x = d;
      this.y = d2;
      this.z = d3;
   }

   public double getY() {
      return this.y;
   }

   public void setLines(String[] strs) {
      this.lines = strs;
   }

   public String[] getLines() {
      return this.lines;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public static void renderAll() {
      FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
      RenderManager renderManager = RenderManager.getRenderManager();

      for (WorldLabel worldLabel : labels) {
         if (worldLabel.getLines() != null && worldLabel.getLines().length > 0) {
            for (int i = worldLabel.getLines().length - 1; i >= 0; i--) {
               String str = worldLabel.getLines()[worldLabel.getLines().length - i - 1];
               float f = (float)(worldLabel.getX() - (float)renderManager.getRenderPosX());
               float f2 = (float)(worldLabel.getY() + 1.0 + i * 0.25F - (float)renderManager.getRenderPosY());
               float f3 = (float)(worldLabel.getZ() - (float)renderManager.getRenderPosZ());
               float f4 = 1.6F;
               float f5 = 0.016666668F * f4;
               GlStateManager.pushMatrix();
               GL11.glTranslatef(f, f2, f3);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
               GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
               GL11.glScalef(-f5, -f5, f5);
               GL11.glDisable(2896);
               GL11.glDepthMask(false);
               GL11.glDisable(2929);
               GlStateManager.enableBlend();
               OpenGlHelper.glBlendFunc(770, 771, 1, 0);
               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               byte b = 0;
               GlStateManager.disableTexture2D();
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
               int n = fontRendererObj.getStringWidth(str) / 2;
               worldRenderer.pos(-n - 1, -1 + b, 0.0).color(0.0F, 1.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(-n - 1, 8 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(n + 1, 8 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(n + 1, -1 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               instance.draw();
               GlStateManager.enableTexture2D();
               fontRendererObj.drawString(str, -fontRendererObj.getStringWidth(str) / 2, b, 553648127);
               GL11.glEnable(2929);
               GL11.glDepthMask(true);
               fontRendererObj.drawString(str, -fontRendererObj.getStringWidth(str) / 2, b, -1);
               GL11.glEnable(2896);
               GlStateManager.disableBlend();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.popMatrix();
            }
         }
      }
   }

   public static List<WorldLabel> getLabels() {
      return labels;
   }
}
