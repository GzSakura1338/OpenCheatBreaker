package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityMinecart.EnumMinecartType;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic {
   public int spawnDelay = 20;
   public String mobID = "Pig";
   public List<MobSpawnerBaseLogic.WeightedRandomMinecart> minecartToSpawn = Lists.newArrayList();
   public MobSpawnerBaseLogic.WeightedRandomMinecart randomEntity;
   public double mobRotation;
   public double prevMobRotation;
   public int minSpawnDelay = 200;
   public int maxSpawnDelay = 800;
   public int spawnCount = 4;
   public Entity cachedEntity;
   public int maxNearbyEntities = 6;
   public int activatingRangeFromPlayer = 16;
   public int spawnRange = 4;

   public String getEntityNameToSpawn() {
      if (this.getRandomEntity() == null) {
         if (this.mobID != null && this.mobID.equals("Minecart")) {
            this.mobID = "MinecartRideable";
         }

         return this.mobID;
      } else {
         return this.getRandomEntity().entityType;
      }
   }

   public void setEntityName(String name) {
      this.mobID = name;
   }

   public boolean isActivated() {
      BlockPos spawnerPosition = this.getSpawnerPosition();
      return this.getSpawnerWorld().isAnyPlayerWithinRangeAt(spawnerPosition.getX() + 0.5, spawnerPosition.getY() + 0.5, spawnerPosition.getZ() + 0.5, this.activatingRangeFromPlayer);
   }

   public void updateSpawner() {
      if (this.isActivated()) {
         BlockPos spawnerPosition = this.getSpawnerPosition();
         if (this.getSpawnerWorld().isRemote) {
            double d = spawnerPosition.getX() + this.getSpawnerWorld().rand.nextFloat();
            double d2 = spawnerPosition.getY() + this.getSpawnerWorld().rand.nextFloat();
            double d3 = spawnerPosition.getZ() + this.getSpawnerWorld().rand.nextFloat();
            this.getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d2, d3, 0.0, 0.0, 0.0);
            this.getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, d, d2, d3, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
               this.spawnDelay--;
            }

            this.prevMobRotation = this.mobRotation;
            this.mobRotation = (this.mobRotation + 1000.0F / (this.spawnDelay + 200.0F)) % 360.0;
         } else {
            if (this.spawnDelay == -1) {
               this.resetTimer();
            }

            if (this.spawnDelay > 0) {
               this.spawnDelay--;
               return;
            }

            boolean flag = false;

            for (int i = 0; i < this.spawnCount; i++) {
               Entity createEntityByName = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());
               if (createEntityByName == null) {
                  return;
               }

               int size = this.getSpawnerWorld()
                  .getEntitiesWithinAABB(
                     createEntityByName.getClass(),
                     new AxisAlignedBB(spawnerPosition.getX(), spawnerPosition.getY(), spawnerPosition.getZ(), spawnerPosition.getX() + 1, spawnerPosition.getY() + 1, spawnerPosition.getZ() + 1)
                        .expand(this.spawnRange, this.spawnRange, this.spawnRange)
                  )
                  .size();
               if (size >= this.maxNearbyEntities) {
                  this.resetTimer();
                  return;
               }

               double d = spawnerPosition.getX() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * this.spawnRange + 0.5;
               double d2 = spawnerPosition.getY() + this.getSpawnerWorld().rand.nextInt(3) - 1;
               double d3 = spawnerPosition.getZ() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * this.spawnRange + 0.5;
               EntityLiving entityLiving = createEntityByName instanceof EntityLiving ? (EntityLiving)createEntityByName : null;
               createEntityByName.setLocationAndAngles(d, d2, d3, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);
               if (entityLiving == null || entityLiving.getCanSpawnHere() && entityLiving.isNotColliding()) {
                  this.spawnNewEntity(createEntityByName, true);
                  this.getSpawnerWorld().playAuxSFX(2004, spawnerPosition, 0);
                  if (entityLiving != null) {
                     entityLiving.spawnExplosionParticle();
                  }

                  flag = true;
               }
            }

            if (flag) {
               this.resetTimer();
            }
         }
      }
   }

   public Entity spawnNewEntity(Entity entityIn, boolean spawn) {
      if (this.getRandomEntity() != null) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         entityIn.writeToNBTOptional(nbttagCompound);

         for (String str : this.getRandomEntity().nbtData.getKeySet()) {
            NBTBase tag = this.getRandomEntity().nbtData.getTag(str);
            nbttagCompound.setTag(str, tag.copy());
         }

         entityIn.readFromNBT(nbttagCompound);
         if (entityIn.worldObj != null && spawn) {
            entityIn.worldObj.spawnEntityInWorld(entityIn);
         }

         Entity entityIn2 = entityIn;

         while (nbttagCompound.hasKey("Riding", 10)) {
            NBTTagCompound compoundTag = nbttagCompound.getCompoundTag("Riding");
            Entity createEntityByName = EntityList.createEntityByName(compoundTag.getString("id"), entityIn.worldObj);
            if (createEntityByName != null) {
               NBTTagCompound nbttagCompound2 = new NBTTagCompound();
               createEntityByName.writeToNBTOptional(nbttagCompound2);

               for (String str : compoundTag.getKeySet()) {
                  NBTBase tag = compoundTag.getTag(str);
                  nbttagCompound2.setTag(str, tag.copy());
               }

               createEntityByName.readFromNBT(nbttagCompound2);
               createEntityByName.setLocationAndAngles(entityIn2.posX, entityIn2.posY, entityIn2.posZ, entityIn2.rotationYaw, entityIn2.rotationPitch);
               if (entityIn.worldObj != null && spawn) {
                  entityIn.worldObj.spawnEntityInWorld(createEntityByName);
               }

               entityIn2.mountEntity(createEntityByName);
            }

            entityIn2 = createEntityByName;
            nbttagCompound = compoundTag;
         }
      } else if (entityIn instanceof EntityLivingBase && entityIn.worldObj != null && spawn) {
         if (entityIn instanceof EntityLiving) {
            ((EntityLiving)entityIn).onInitialSpawn(entityIn.worldObj.getDifficultyForLocation(new BlockPos(entityIn)), (IEntityLivingData)null);
         }

         entityIn.worldObj.spawnEntityInWorld(entityIn);
      }

      return entityIn;
   }

   public void resetTimer() {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         int n = this.maxSpawnDelay - this.minSpawnDelay;
         this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(n);
      }

      if (this.minecartToSpawn.size() > 0) {
         this.setRandomEntity(WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.minecartToSpawn));
      }

      this.addBlockEvent(1);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.mobID = nbt.getString("EntityId");
      this.spawnDelay = nbt.getShort("Delay");
      this.minecartToSpawn.clear();
      if (nbt.hasKey("SpawnPotentials", 9)) {
         NBTTagList tagList = nbt.getTagList("SpawnPotentials", 10);

         for (int i = 0; i < tagList.tagCount(); i++) {
            this.minecartToSpawn.add(new MobSpawnerBaseLogic.WeightedRandomMinecart(tagList.getCompoundTagAt(i)));
         }
      }

      if (nbt.hasKey("SpawnData", 10)) {
         this.setRandomEntity(new MobSpawnerBaseLogic.WeightedRandomMinecart(nbt.getCompoundTag("SpawnData"), this.mobID));
      } else {
         this.setRandomEntity((MobSpawnerBaseLogic.WeightedRandomMinecart)null);
      }

      if (nbt.hasKey("MinSpawnDelay", 99)) {
         this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
         this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
         this.spawnCount = nbt.getShort("SpawnCount");
      }

      if (nbt.hasKey("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
         this.activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
      }

      if (nbt.hasKey("SpawnRange", 99)) {
         this.spawnRange = nbt.getShort("SpawnRange");
      }

      if (this.getSpawnerWorld() != null) {
         this.cachedEntity = null;
      }
   }

   public void writeToNBT(NBTTagCompound nbt) {
      String entityNameToSpawn = this.getEntityNameToSpawn();
      if (!StringUtils.isNullOrEmpty(entityNameToSpawn)) {
         nbt.setString("EntityId", entityNameToSpawn);
         nbt.setShort("Delay", (short)this.spawnDelay);
         nbt.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
         nbt.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         nbt.setShort("SpawnCount", (short)this.spawnCount);
         nbt.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         nbt.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
         nbt.setShort("SpawnRange", (short)this.spawnRange);
         if (this.getRandomEntity() != null) {
            nbt.setTag("SpawnData", this.getRandomEntity().nbtData.copy());
         }

         if (this.getRandomEntity() != null || this.minecartToSpawn.size() > 0) {
            NBTTagList nbttagList = new NBTTagList();
            if (this.minecartToSpawn.size() > 0) {
               for (MobSpawnerBaseLogic.WeightedRandomMinecart weightedRandomMinecart : this.minecartToSpawn) {
                  nbttagList.appendTag(weightedRandomMinecart.toNBT());
               }
            } else {
               nbttagList.appendTag(this.getRandomEntity().toNBT());
            }

            nbt.setTag("SpawnPotentials", nbttagList);
         }
      }
   }

   public Entity getRenderedEntity(World worldIn) {
      if (this.cachedEntity == null) {
         Entity createEntityByName = EntityList.createEntityByName(this.getEntityNameToSpawn(), worldIn);
         if (createEntityByName != null) {
            createEntityByName = this.spawnNewEntity(createEntityByName, false);
            this.cachedEntity = createEntityByName;
         }
      }

      return this.cachedEntity;
   }

   public boolean setDelayToMin(int delay) {
      if (delay == 1 && this.getSpawnerWorld().isRemote) {
         this.spawnDelay = this.minSpawnDelay;
         return true;
      } else {
         return false;
      }
   }

   public MobSpawnerBaseLogic.WeightedRandomMinecart getRandomEntity() {
      return this.randomEntity;
   }

   public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart weightedRandomMinecart) {
      this.randomEntity = weightedRandomMinecart;
   }

   public abstract void addBlockEvent(int n);

   public abstract World getSpawnerWorld();

   public abstract BlockPos getSpawnerPosition();

   public double getMobRotation() {
      return this.mobRotation;
   }

   public double getPrevMobRotation() {
      return this.prevMobRotation;
   }

   public class WeightedRandomMinecart extends Item {
      public NBTTagCompound nbtData;
      public String entityType;

      public WeightedRandomMinecart(NBTTagCompound tagCompound) {
         this(tagCompound.getCompoundTag("Properties"), tagCompound.getString("Type"), tagCompound.getInteger("Weight"));
      }

      public WeightedRandomMinecart(NBTTagCompound tagCompound, String type) {
         this(tagCompound, type, 1);
      }

      public WeightedRandomMinecart(NBTTagCompound tagCompound, String type, int weight) {
         super(weight);
         if (type.equals("Minecart")) {
            if (tagCompound != null) {
               type = EnumMinecartType.byNetworkID(tagCompound.getInteger("Type")).getName();
            } else {
               type = "MinecartRideable";
            }
         }

         this.nbtData = tagCompound;
         this.entityType = type;
      }

      public NBTTagCompound toNBT() {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         nbttagCompound.setTag("Properties", this.nbtData);
         nbttagCompound.setString("Type", this.entityType);
         nbttagCompound.setInteger("Weight", this.itemWeight);
         return nbttagCompound;
      }
   }
}
