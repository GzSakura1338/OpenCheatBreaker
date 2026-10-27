package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCrit2FX extends EntityFX {
   public float floatValue;

   public EntityCrit2FX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3) {
      this(worldIn, xCoordIn, yCoordIn, zCoordIn, d, d2, d3, 1.0F);
   }

   public EntityCrit2FX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3, float f) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.motionX *= 0.1F;
      this.motionY *= 0.1F;
      this.motionZ *= 0.1F;
      this.motionX += d * 0.4;
      this.motionY += d2 * 0.4;
      this.motionZ += d3 * 0.4;
      this.particleRed = this.particleGreen = this.particleBlue = (float)(Math.random() * 0.3F + 0.6F);
      this.particleScale *= 0.75F;
      this.particleScale *= f;
      this.floatValue = this.particleScale;
      this.particleMaxAge = (int)(6.0 / (Math.random() * 0.8 + 0.6));
      this.particleMaxAge = (int)(this.particleMaxAge * f);
      this.noClip = false;
      this.setParticleTextureIndex(65);
      this.onUpdate();
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
      this.particleScale = this.floatValue * f;
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
      this.particleGreen = (float)(this.particleGreen * 0.96);
      this.particleBlue = (float)(this.particleBlue * 0.9);
      this.motionX *= 0.7F;
      this.motionY *= 0.7F;
      this.motionZ *= 0.7F;
      this.motionY -= 0.02F;
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
         return new EntityCrit2FX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }

   public static class MagicFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         EntityCrit2FX entityCrit2FX = new EntityCrit2FX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
         entityCrit2FX.setRBGColorF(entityCrit2FX.getRedColorF() * 0.3F, entityCrit2FX.getGreenColorF() * 0.8F, entityCrit2FX.getBlueColorF());
         entityCrit2FX.nextTextureIndexX();
         return entityCrit2FX;
      }
   }
}
