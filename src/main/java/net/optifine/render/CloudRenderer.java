package net.optifine.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class CloudRenderer {
   public boolean updated = false;
   public boolean renderFancy = false;
   public boolean updateRenderFancy = false;
   public int cloudTickCounter;
   public Vec3 cloudColor;
   public float partialTicks;
   public int updateCloudTickCounter = 0;
   public Vec3 updateCloudColor = new Vec3(-1.0, -1.0, -1.0);
   public double updatePlayerX = 0.0;
   public double updatePlayerY = 0.0;
   public double updatePlayerZ = 0.0;
   public int glListClouds = -1;
   public Minecraft mc;

   public CloudRenderer(Minecraft mc) {
      this.mc = mc;
      this.glListClouds = GLAllocation.generateDisplayLists(1);
   }

   public void prepareToRender(boolean flag, int n, float f, Vec3 vec3) {
      this.renderFancy = flag;
      this.cloudTickCounter = n;
      this.partialTicks = f;
      this.cloudColor = vec3;
   }

   public boolean shouldUpdateGlList() {
      if (!this.updated) {
         return true;
      }

      if (this.renderFancy != this.updateRenderFancy) {
         return true;
      }

      if (this.cloudTickCounter >= this.updateCloudTickCounter + 20) {
         return true;
      }

      if (Math.abs(this.cloudColor.xCoord - this.updateCloudColor.xCoord) > 0.003) {
         return true;
      }

      if (Math.abs(this.cloudColor.yCoord - this.updateCloudColor.yCoord) > 0.003) {
         return true;
      }

      if (Math.abs(this.cloudColor.zCoord - this.updateCloudColor.zCoord) > 0.003) {
         return true;
      }

      Entity renderViewEntity = this.mc.getRenderViewEntity();
      boolean flag = this.updatePlayerY + renderViewEntity.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
      boolean flag2 = renderViewEntity.prevPosY + renderViewEntity.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
      return flag2 != flag;
   }

   public void startUpdateGlList() {
      GL11.glNewList(this.glListClouds, 4864);
   }

   public void endUpdateGlList() {
      GL11.glEndList();
      this.updateRenderFancy = this.renderFancy;
      this.updateCloudTickCounter = this.cloudTickCounter;
      this.updateCloudColor = this.cloudColor;
      this.updatePlayerX = this.mc.getRenderViewEntity().prevPosX;
      this.updatePlayerY = this.mc.getRenderViewEntity().prevPosY;
      this.updatePlayerZ = this.mc.getRenderViewEntity().prevPosZ;
      this.updated = true;
      GlStateManager.resetColor();
   }

   public void renderGlList() {
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      double d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * this.partialTicks;
      double d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * this.partialTicks;
      double d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * this.partialTicks;
      double d4 = this.cloudTickCounter - this.updateCloudTickCounter + this.partialTicks;
      float f = (float)(d - this.updatePlayerX + d4 * 0.03);
      float f2 = (float)(d2 - this.updatePlayerY);
      float f3 = (float)(d3 - this.updatePlayerZ);
      GlStateManager.pushMatrix();
      if (this.renderFancy) {
         GlStateManager.translate(-f / 12.0F, -f2, -f3 / 12.0F);
      } else {
         GlStateManager.translate(-f, -f2, -f3);
      }

      GlStateManager.callList(this.glListClouds);
      GlStateManager.popMatrix();
      GlStateManager.resetColor();
   }

   public void reset() {
      this.updated = false;
   }
}
