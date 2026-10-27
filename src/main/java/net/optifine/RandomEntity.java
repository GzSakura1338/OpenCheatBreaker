package net.optifine;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

public class RandomEntity implements IRandomEntity {
   public Entity entity;

   @Override
   public int getId() {
      UUID uniqueID = this.entity.getUniqueID();
      long leastSignificantBits = uniqueID.getLeastSignificantBits();
      return (int)(leastSignificantBits & 2147483647L);
   }

   @Override
   public BlockPos getSpawnPosition() {
      return this.entity.getDataWatcher().spawnPosition;
   }

   @Override
   public BiomeGenBase getSpawnBiome() {
      return this.entity.getDataWatcher().spawnBiome;
   }

   @Override
   public String getName() {
      return this.entity.hasCustomName() ? this.entity.getCustomNameTag() : null;
   }

   @Override
   public int getHealth() {
      if (!(this.entity instanceof EntityLiving)) {
         return 0;
      }

      EntityLiving entity = (EntityLiving)this.entity;
      return (int)entity.getHealth();
   }

   @Override
   public int getMaxHealth() {
      if (!(this.entity instanceof EntityLiving)) {
         return 0;
      }

      EntityLiving entity = (EntityLiving)this.entity;
      return (int)entity.getMaxHealth();
   }

   public Entity getEntity() {
      return this.entity;
   }

   public void setEntity(Entity entity) {
      this.entity = entity;
   }
}
