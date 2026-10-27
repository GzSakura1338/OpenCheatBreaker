package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityPortalFX extends EntityFX {
   public double portalPosX;
   public double portalPosY;
   public double portalPosZ;
   public float portalParticleScale;

   public EntityPortalFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.motionX = xSpeedIn;
      this.motionY = ySpeedIn;
      this.motionZ = zSpeedIn;
      this.portalPosX = this.posX = xCoordIn;
      this.portalPosY = this.posY = yCoordIn;
      this.portalPosZ = this.posZ = zCoordIn;
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
      this.particleGreen *= 0.3F;
      this.particleRed *= 0.9F;
      this.particleMaxAge = (int)(Math.random() * 10.0) + 40;
      this.noClip = true;
      this.setParticleTextureIndex((int)(Math.random() * 8.0));
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
      float f = (this.particleAge + partialTicks) / this.particleMaxAge;
      f = 1.0F - f;
      f *= f;
      f = 1.0F - f;
      this.particleScale = this.portalParticleScale * f;
      super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      int brightnessForRender = super.getBrightnessForRender(partialTicks);
      float f = (float)this.particleAge / this.particleMaxAge;
      f *= f;
      f *= f;
      int n = brightnessForRender & 0xFF;
      int n2 = brightnessForRender >> 16 & 0xFF;
      n2 += (int)(f * 15.0F * 16.0F);
      if (n2 > 240) {
         n2 = 240;
      }

      return n | n2 << 16;
   }

   @Override
   public float getBrightness(float partialTicks) {
      float brightness = super.getBrightness(partialTicks);
      float f = (float)this.particleAge / this.particleMaxAge;
      f = f * f * f * f;
      return brightness * (1.0F - f) + f;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      float f = (float)this.particleAge / this.particleMaxAge;
      f = -f + f * f * 2.0F;
      f = 1.0F - f;
      this.posX = this.portalPosX + this.motionX * f;
      this.posY = this.portalPosY + this.motionY * f + (1.0F - f);
      this.posZ = this.portalPosZ + this.motionZ * f;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityPortalFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
