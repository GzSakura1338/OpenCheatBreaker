package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityLargeExplodeFX extends EntityFX {
   public static ResourceLocation EXPLOSION_TEXTURE = new ResourceLocation("textures/entity/explosion.png");
   public static VertexFormat vertexFormat = new VertexFormat()
      .addElement(DefaultVertexFormats.POSITION_3F)
      .addElement(DefaultVertexFormats.TEX_2F)
      .addElement(DefaultVertexFormats.COLOR_4UB)
      .addElement(DefaultVertexFormats.TEX_2S)
      .addElement(DefaultVertexFormats.NORMAL_3B)
      .addElement(DefaultVertexFormats.PADDING_1B);
   public int intValue;
   public TextureManager theRenderEngine;
   public int intValue2;
   public float floatValue;

   public EntityLargeExplodeFX(
      TextureManager renderEngine, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3
   ) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.theRenderEngine = renderEngine;
      this.intValue2 = 6 + this.rand.nextInt(4);
      this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.6F + 0.4F;
      this.floatValue = 1.0F - (float)d * 0.5F;
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
      int n = (int)((this.intValue + partialTicks) * 15.0F / this.intValue2);
      if (n <= 15) {
         this.theRenderEngine.bindTexture(EXPLOSION_TEXTURE);
         float f = n % 4 / 4.0F;
         float f2 = f + 0.24975F;
         float f3 = n / 4 / 4.0F;
         float f4 = f3 + 0.24975F;
         float f5 = 2.0F * this.floatValue;
         float f6 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
         float f7 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
         float f8 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         RenderHelper.disableStandardItemLighting();
         worldRendererIn.begin(7, vertexFormat);
         worldRendererIn.pos(f6 - rotationX * f5 - rotationXY * f5, f7 - rotationZ * f5, f8 - rotationYZ * f5 - rotationXZ * f5)
            .tex(f2, f4)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         worldRendererIn.pos(f6 - rotationX * f5 + rotationXY * f5, f7 + rotationZ * f5, f8 - rotationYZ * f5 + rotationXZ * f5)
            .tex(f2, f3)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         worldRendererIn.pos(f6 + rotationX * f5 + rotationXY * f5, f7 + rotationZ * f5, f8 + rotationYZ * f5 + rotationXZ * f5)
            .tex(f, f3)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         worldRendererIn.pos(f6 + rotationX * f5 - rotationXY * f5, f7 - rotationZ * f5, f8 + rotationYZ * f5 - rotationXZ * f5)
            .tex(f, f4)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         Tessellator.getInstance().draw();
         GlStateManager.enableLighting();
      }
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 61680;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.intValue++;
      if (this.intValue == this.intValue2) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 3;
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityLargeExplodeFX(Minecraft.getMinecraft().getTextureManager(), worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
