package net.minecraft.village;

import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

public class VillageSiege {
   public int intValue = -1;
   public boolean spawned;
   public World worldObj;
   public int intValue2;
   public int intValue3;
   public Village theVillage;
   public int intValue4;
   public int intValue5;
   public int intValue6;

   public VillageSiege(World worldIn) {
      this.worldObj = worldIn;
   }

   public void tick() {
      if (this.worldObj.isDaytime()) {
         this.intValue = 0;
      } else if (this.intValue != 2) {
         if (this.intValue == 0) {
            float celestialAngle = this.worldObj.getCelestialAngle(0.0F);
            if (celestialAngle < 0.5 || celestialAngle > 0.501) {
               return;
            }

            this.intValue = this.worldObj.rand.nextInt(10) == 0 ? 1 : 2;
            this.spawned = false;
            if (this.intValue == 2) {
               return;
            }
         }

         if (this.intValue != -1) {
            if (!this.spawned) {
               if (!this.check()) {
                  return;
               }

               this.spawned = true;
            }

            if (this.intValue3 > 0) {
               this.intValue3--;
            } else {
               this.intValue3 = 2;
               if (this.intValue2 > 0) {
                  this.spawnZombie();
                  this.intValue2--;
               } else {
                  this.intValue = 2;
               }
            }
         }
      }
   }

   public boolean check() {
      for (EntityPlayer entityPlayer : this.worldObj.playerEntities) {
         if (!entityPlayer.isSpectator()) {
            this.theVillage = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(entityPlayer), 1);
            if (this.theVillage != null
               && this.theVillage.getNumVillageDoors() >= 10
               && this.theVillage.getTicksSinceLastDoorAdding() >= 20
               && this.theVillage.getNumVillagers() >= 20) {
               BlockPos center = this.theVillage.getCenter();
               float villageRadius = this.theVillage.getVillageRadius();
               boolean flag = false;

               for (int i = 0; i < 10; i++) {
                  float f = this.worldObj.rand.nextFloat() * (float) Math.PI * 2.0F;
                  this.intValue4 = center.getX() + (int)(MathHelper.cos(f) * villageRadius * 0.9);
                  this.intValue5 = center.getY();
                  this.intValue6 = center.getZ() + (int)(MathHelper.sin(f) * villageRadius * 0.9);
                  flag = false;

                  for (Village village : this.worldObj.getVillageCollection().getVillageList()) {
                     if (village != this.theVillage && village.checkBlockPos(new BlockPos(this.intValue4, this.intValue5, this.intValue6))) {
                        flag = true;
                        break;
                     }
                  }

                  if (!flag) {
                     break;
                  }
               }

               if (flag) {
                  return false;
               }

               Vec3 createVec3 = this.createVec3(new BlockPos(this.intValue4, this.intValue5, this.intValue6));
               if (createVec3 != null) {
                  this.intValue3 = 0;
                  this.intValue2 = 20;
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean spawnZombie() {
      Vec3 createVec3 = this.createVec3(new BlockPos(this.intValue4, this.intValue5, this.intValue6));
      if (createVec3 == null) {
         return false;
      }

      EntityZombie entityZombie;
      try {
         entityZombie = new EntityZombie(this.worldObj);
         entityZombie.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityZombie)), (IEntityLivingData)null);
         entityZombie.setVillager(false);
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }

      entityZombie.setLocationAndAngles(createVec3.xCoord, createVec3.yCoord, createVec3.zCoord, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
      this.worldObj.spawnEntityInWorld(entityZombie);
      BlockPos center = this.theVillage.getCenter();
      entityZombie.setHomePosAndDistance(center, this.theVillage.getVillageRadius());
      return true;
   }

   public Vec3 createVec3(BlockPos blockPos) {
      for (int i = 0; i < 10; i++) {
         BlockPos blockPos2 = blockPos.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);
         if (this.theVillage.checkBlockPos(blockPos2)
            && SpawnerAnimals.canCreatureTypeSpawnAtLocation(SpawnPlacementType.ON_GROUND, this.worldObj, blockPos2)) {
            return new Vec3(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
         }
      }

      return null;
   }
}
