package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityNoteFX extends EntityFX {
   public float noteParticleScale;

   public EntityNoteFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3) {
      this(worldIn, xCoordIn, yCoordIn, zCoordIn, d, d2, d3, 2.0F);
   }

   public EntityNoteFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3, float f) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.motionX *= 0.01F;
      this.motionY *= 0.01F;
      this.motionZ *= 0.01F;
      this.motionY += 0.2;
      this.particleRed = MathHelper.sin(((float)d + 0.0F) * (float) Math.PI * 2.0F) * 0.65F + 0.35F;
      this.particleGreen = MathHelper.sin(((float)d + 0.33333334F) * (float) Math.PI * 2.0F) * 0.65F + 0.35F;
      this.particleBlue = MathHelper.sin(((float)d + 0.6666667F) * (float) Math.PI * 2.0F) * 0.65F + 0.35F;
      this.particleScale *= 0.75F;
      this.particleScale *= f;
      this.noteParticleScale = this.particleScale;
      this.particleMaxAge = 6;
      this.noClip = false;
      this.setParticleTextureIndex(64);
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
      this.particleScale = this.noteParticleScale * f;
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

      this.motionX *= 0.66F;
      this.motionY *= 0.66F;
      this.motionZ *= 0.66F;
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
         return new EntityNoteFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
