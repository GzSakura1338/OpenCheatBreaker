package com.cheatbreaker.client.emote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SaltParticle extends EntityFX {
   public static ModelRenderer modelRenderer;
   public static ResourceLocation minecraft = new ResourceLocation("minecraft", "emoticons/textures/particles.png");

   public SaltParticle(World world, double d, double d2, double d3, double d4) {
      super(world, d, d2, d3);
      this.particleGravity = 0.5F;
      this.particleScale = 0.5F;
      this.particleMaxAge = 20 + this.rand.nextInt(10);
      this.motionX = this.rand.nextFloat() * 0.05F;
      this.motionZ = this.rand.nextFloat() * 0.05F;
      this.motionY = d4;
      if (modelRenderer == null) {
         EmptyParticleModel emptyParticleModel = new EmptyParticleModel();
         emptyParticleModel.textureWidth = 64;
         emptyParticleModel.textureHeight = 64;
         modelRenderer = new ModelRenderer(emptyParticleModel, 0, 0);
         modelRenderer.addBox(-0.25F, -0.25F, 0.25F, 1, 1, 1);
      }
   }

   @Override
   public void renderParticle(WorldRenderer worldRenderer, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
      float f7 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - interpPosX);
      float f8 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - interpPosY);
      float f9 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - interpPosZ);
      int n = this.particleMaxAge - this.particleAge;
      float f10 = 0.5F * (n < 5 ? n / 5.0F : 1.0F);
      GlStateManager.enableTexture2D();
      Minecraft.getMinecraft().getTextureManager().bindTexture(minecraft);
      int brightnessForRender = entity.getBrightnessForRender(f);
      if (entity.isBurning()) {
         brightnessForRender = 15728880;
      }

      int n2 = brightnessForRender % 65536;
      int n3 = brightnessForRender / 65536;
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n2, n3);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate(f7, f8, f9);
      GlStateManager.scale(f10, f10, f10);
      RenderHelper.enableStandardItemLighting();
      modelRenderer.render(0.0625F);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   @Override
   public int getFXLayer() {
      return 3;
   }
}
