package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityHeartFX extends EntityFX {
   public float particleScaleOverTime;

   public EntityHeartFX(World worldIn, double d, double d2, double d3, double d4, double d5, double d6) {
      this(worldIn, d, d2, d3, d4, d5, d6, 2.0F);
   }

   public EntityHeartFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3, float scale) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.motionX *= 0.01F;
      this.motionY *= 0.01F;
      this.motionZ *= 0.01F;
      this.motionY += 0.1;
      this.particleScale *= 0.75F;
      this.particleScale *= scale;
      this.particleScaleOverTime = this.particleScale;
      this.particleMaxAge = 16;
      this.noClip = false;
      this.setParticleTextureIndex(80);
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
      float f = (this.particleAge + partialTicks) / this.particleMaxAge * 32.0F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      this.particleScale = this.particleScaleOverTime * f;
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

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      if (this.posY == this.prevPosY) {
         this.motionX *= 1.1;
         this.motionZ *= 1.1;
      }

      this.motionX *= 0.86F;
      this.motionY *= 0.86F;
      this.motionZ *= 0.86F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   public static class AngryVillagerFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         EntityHeartFX entityHeartFX = new EntityHeartFX(worldIn, xCoordIn, yCoordIn + 0.5, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
         entityHeartFX.setParticleTextureIndex(81);
         entityHeartFX.setRBGColorF(1.0F, 1.0F, 1.0F);
         return entityHeartFX;
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityHeartFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
