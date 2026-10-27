package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityLavaFX extends EntityFX {
   public float lavaParticleScale;

   public EntityLavaFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.motionX *= 0.8F;
      this.motionY *= 0.8F;
      this.motionZ *= 0.8F;
      this.motionY = this.rand.nextFloat() * 0.4F + 0.05F;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleScale = this.particleScale * (this.rand.nextFloat() * 2.0F + 0.2F);
      this.lavaParticleScale = this.particleScale;
      this.particleMaxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
      this.noClip = false;
      this.setParticleTextureIndex(49);
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      float f = (this.particleAge + partialTicks) / this.particleMaxAge;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      int brightnessForRender = super.getBrightnessForRender(partialTicks);
      short s = 240;
      int n = brightnessForRender >> 16 & 0xFF;
      return s | n << 16;
   }

   @Override
   public float getBrightness(float partialTicks) {
      return 1.0F;
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
      this.particleScale = this.lavaParticleScale * (1.0F - f * f);
      super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      float f = (float)this.particleAge / this.particleMaxAge;
      if (this.rand.nextFloat() > f) {
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
      }

      this.motionY -= 0.03;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.999F;
      this.motionY *= 0.999F;
      this.motionZ *= 0.999F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityLavaFX(worldIn, xCoordIn, yCoordIn, zCoordIn);
      }
   }
}
