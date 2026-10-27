package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCloudFX extends EntityFX {
   public float prevScale;

   public EntityCloudFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      float f = 2.5F;
      this.motionX *= 0.1F;
      this.motionY *= 0.1F;
      this.motionZ *= 0.1F;
      this.motionX += d;
      this.motionY += d2;
      this.motionZ += d3;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F - (float)(Math.random() * 0.3F);
      this.particleScale *= 0.75F;
      this.particleScale *= f;
      this.prevScale = this.particleScale;
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.3));
      this.particleMaxAge = (int)(this.particleMaxAge * f);
      this.noClip = false;
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
      this.particleScale = this.prevScale * f;
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

      this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.96F;
      this.motionY *= 0.96F;
      this.motionZ *= 0.96F;
      EntityPlayer closestPlayerToEntity = this.worldObj.getClosestPlayerToEntity(this, 2.0);
      if (closestPlayerToEntity != null && this.posY > closestPlayerToEntity.getEntityBoundingBox().minY) {
         this.posY = this.posY + (closestPlayerToEntity.getEntityBoundingBox().minY - this.posY) * 0.2;
         this.motionY = this.motionY + (closestPlayerToEntity.motionY - this.motionY) * 0.2;
         this.setPosition(this.posX, this.posY, this.posZ);
      }

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
         return new EntityCloudFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
