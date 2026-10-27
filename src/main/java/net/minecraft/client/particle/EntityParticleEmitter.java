package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityParticleEmitter extends EntityFX {
   public Entity attachedEntity;
   public int age;
   public int lifetime;
   public EnumParticleTypes particleTypes;
   public float floatValue;

   public EntityParticleEmitter(World worldIn, Entity entity, EnumParticleTypes particleTypesIn) {
      super(worldIn, entity.posX, entity.getEntityBoundingBox().minY + entity.height / 2.0F, entity.posZ, entity.motionX, entity.motionY, entity.motionZ);
      this.attachedEntity = entity;
      this.lifetime = 3;
      this.particleTypes = particleTypesIn;
      this.floatValue = 1.0F;
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
   }

   @Override
   public void onUpdate() {
      for (int i = 0; i < 16.0F * this.floatValue; i++) {
         double d = this.rand.nextFloat() * 2.0F - 1.0F;
         double d2 = this.rand.nextFloat() * 2.0F - 1.0F;
         double d3 = this.rand.nextFloat() * 2.0F - 1.0F;
         if (d * d + d2 * d2 + d3 * d3 <= 1.0) {
            double d4 = this.attachedEntity.posX + d * this.attachedEntity.width / 4.0;
            double d5 = this.attachedEntity.getEntityBoundingBox().minY + this.attachedEntity.height / 2.0F + d2 * this.attachedEntity.height / 4.0;
            double d6 = this.attachedEntity.posZ + d3 * this.attachedEntity.width / 4.0;
            this.worldObj.spawnParticle(this.particleTypes, false, d4, d5, d6, d, d2 + 0.2, d3);
         }
      }

      this.age++;
      if (this.age >= this.lifetime) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 3;
   }

   public EntityParticleEmitter(World world, Entity entity, EnumParticleTypes enumParticleTypes, float f) {
      super(world, entity.posX, entity.getEntityBoundingBox().minY + entity.height / 2.0F, entity.posZ, entity.motionX, entity.motionY, entity.motionZ);
      this.attachedEntity = entity;
      this.lifetime = 3;
      this.particleTypes = enumParticleTypes;
      this.floatValue = f;
      this.onUpdate();
   }
}
