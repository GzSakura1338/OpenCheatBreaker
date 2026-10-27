package net.minecraft.village;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Village {
   public List<VillageDoorInfo> villageDoorInfoList = Lists.newArrayList();
   public BlockPos centerHelper = BlockPos.ORIGIN;
   public BlockPos center = BlockPos.ORIGIN;
   public TreeMap<String, Integer> playerReputation = new TreeMap<>();
   public int villageRadius;
   public int lastAddDoorTimestamp;
   public int tickCounter;
   public int numVillagers;
   public int noBreedTicks;
   public List<Village.VillageAggressor> villageAgressors = Lists.newArrayList();
   public World worldObj;
   public int numIronGolems;

   public Village() {
   }

   public Village(World worldIn) {
      this.worldObj = worldIn;
   }

   public void setWorld(World worldIn) {
      this.worldObj = worldIn;
   }

   public void tick(int n) {
      this.tickCounter = n;
      this.removeDeadAndOutOfRangeDoors();
      this.removeDeadAndOldAgressors();
      if (n % 20 == 0) {
         this.updateNumVillagers();
      }

      if (n % 30 == 0) {
         this.updateNumIronGolems();
      }

      int n2 = this.numVillagers / 10;
      if (this.numIronGolems < n2 && this.villageDoorInfoList.size() > 20 && this.worldObj.rand.nextInt(7000) == 0) {
         Vec3 findVec3 = this.findVec3(this.center, 2, 4, 2);
         if (findVec3 != null) {
            EntityIronGolem entityIronGolem = new EntityIronGolem(this.worldObj);
            entityIronGolem.setPosition(findVec3.xCoord, findVec3.yCoord, findVec3.zCoord);
            this.worldObj.spawnEntityInWorld(entityIronGolem);
            this.numIronGolems++;
         }
      }
   }

   public Vec3 findVec3(BlockPos blockPos, int n, int n2, int n3) {
      for (int i = 0; i < 10; i++) {
         BlockPos blockPos2 = blockPos.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);
         if (this.checkBlockPos(blockPos2) && this.checkBlockPos(new BlockPos(n, n2, n3), blockPos2)) {
            return new Vec3(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
         }
      }

      return null;
   }

   public boolean checkBlockPos(BlockPos blockPos, BlockPos blockPos2) {
      if (!World.doesBlockHaveSolidTopSurface(this.worldObj, blockPos2.down())) {
         return false;
      }

      int n = blockPos2.getX() - blockPos.getX() / 2;
      int n2 = blockPos2.getZ() - blockPos.getZ() / 2;

      for (int i = n; i < n + blockPos.getX(); i++) {
         for (int j = blockPos2.getY(); j < blockPos2.getY() + blockPos.getY(); j++) {
            for (int k = n2; k < n2 + blockPos.getZ(); k++) {
               if (this.worldObj.getBlockState(new BlockPos(i, j, k)).getBlock().isNormalCube()) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public void updateNumIronGolems() {
      List entitiesWithinAABB = this.worldObj
         .getEntitiesWithinAABB(
            EntityIronGolem.class,
            new AxisAlignedBB(
               this.center.getX() - this.villageRadius,
               this.center.getY() - 4,
               this.center.getZ() - this.villageRadius,
               this.center.getX() + this.villageRadius,
               this.center.getY() + 4,
               this.center.getZ() + this.villageRadius
            )
         );
      this.numIronGolems = entitiesWithinAABB.size();
   }

   public void updateNumVillagers() {
      List entitiesWithinAABB = this.worldObj
         .getEntitiesWithinAABB(
            EntityVillager.class,
            new AxisAlignedBB(
               this.center.getX() - this.villageRadius,
               this.center.getY() - 4,
               this.center.getZ() - this.villageRadius,
               this.center.getX() + this.villageRadius,
               this.center.getY() + 4,
               this.center.getZ() + this.villageRadius
            )
         );
      this.numVillagers = entitiesWithinAABB.size();
      if (this.numVillagers == 0) {
         this.playerReputation.clear();
      }
   }

   public BlockPos getCenter() {
      return this.center;
   }

   public int getVillageRadius() {
      return this.villageRadius;
   }

   public int getNumVillageDoors() {
      return this.villageDoorInfoList.size();
   }

   public int getTicksSinceLastDoorAdding() {
      return this.tickCounter - this.lastAddDoorTimestamp;
   }

   public int getNumVillagers() {
      return this.numVillagers;
   }

   public boolean checkBlockPos(BlockPos pos) {
      return this.center.distanceSq(pos) < this.villageRadius * this.villageRadius;
   }

   public List<VillageDoorInfo> getVillageDoorInfoList() {
      return this.villageDoorInfoList;
   }

   public VillageDoorInfo getNearestDoor(BlockPos pos) {
      VillageDoorInfo villageDoorInfo = null;
      int n = Integer.MAX_VALUE;

      for (VillageDoorInfo villageDoorInfo2 : this.villageDoorInfoList) {
         int distanceToDoorBlockSq = villageDoorInfo2.getDistanceToDoorBlockSq(pos);
         if (distanceToDoorBlockSq < n) {
            villageDoorInfo = villageDoorInfo2;
            n = distanceToDoorBlockSq;
         }
      }

      return villageDoorInfo;
   }

   public VillageDoorInfo getDoorInfo(BlockPos pos) {
      VillageDoorInfo villageDoorInfo = null;
      int n = Integer.MAX_VALUE;

      for (VillageDoorInfo villageDoorInfo2 : this.villageDoorInfoList) {
         int distanceToDoorBlockSq = villageDoorInfo2.getDistanceToDoorBlockSq(pos);
         if (distanceToDoorBlockSq > 256) {
            distanceToDoorBlockSq *= 1000;
         } else {
            distanceToDoorBlockSq = villageDoorInfo2.getDoorOpeningRestrictionCounter();
         }

         if (distanceToDoorBlockSq < n) {
            villageDoorInfo = villageDoorInfo2;
            n = distanceToDoorBlockSq;
         }
      }

      return villageDoorInfo;
   }

   public VillageDoorInfo getExistedDoor(BlockPos doorBlock) {
      if (this.center.distanceSq(doorBlock) > this.villageRadius * this.villageRadius) {
         return null;
      }

      for (VillageDoorInfo villageDoorInfo : this.villageDoorInfoList) {
         if (villageDoorInfo.getDoorBlockPos().getX() == doorBlock.getX()
            && villageDoorInfo.getDoorBlockPos().getZ() == doorBlock.getZ()
            && Math.abs(villageDoorInfo.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
            return villageDoorInfo;
         }
      }

      return null;
   }

   public void addVillageDoorInfo(VillageDoorInfo doorInfo) {
      this.villageDoorInfoList.add(doorInfo);
      this.centerHelper = this.centerHelper.add(doorInfo.getDoorBlockPos());
      this.updateVillageRadiusAndCenter();
      this.lastAddDoorTimestamp = doorInfo.getInsidePosY();
   }

   public boolean isAnnihilated() {
      return this.villageDoorInfoList.isEmpty();
   }

   public void addOrRenewAgressor(EntityLivingBase entitylivingbaseIn) {
      for (Village.VillageAggressor villageAggressor : this.villageAgressors) {
         if (villageAggressor.agressor == entitylivingbaseIn) {
            villageAggressor.agressionTime = this.tickCounter;
            return;
         }
      }

      this.villageAgressors.add(new Village.VillageAggressor(entitylivingbaseIn, this.tickCounter));
   }

   public EntityLivingBase findNearestVillageAggressor(EntityLivingBase entitylivingbaseIn) {
      double d = Double.MAX_VALUE;
      Village.VillageAggressor villageAggressor = null;

      for (int i = 0; i < this.villageAgressors.size(); i++) {
         Village.VillageAggressor villageAggressor2 = this.villageAgressors.get(i);
         double distanceSqToEntity = villageAggressor2.agressor.getDistanceSqToEntity(entitylivingbaseIn);
         if (distanceSqToEntity <= d) {
            villageAggressor = villageAggressor2;
            d = distanceSqToEntity;
         }
      }

      return villageAggressor != null ? villageAggressor.agressor : null;
   }

   public EntityPlayer getNearestTargetPlayer(EntityLivingBase villageDefender) {
      double d = Double.MAX_VALUE;
      EntityPlayer entityPlayer = null;

      for (String str : this.playerReputation.keySet()) {
         if (this.isPlayerReputationTooLow(str)) {
            EntityPlayer playerEntityByName = this.worldObj.getPlayerEntityByName(str);
            if (playerEntityByName != null) {
               double distanceSqToEntity = playerEntityByName.getDistanceSqToEntity(villageDefender);
               if (distanceSqToEntity <= d) {
                  entityPlayer = playerEntityByName;
                  d = distanceSqToEntity;
               }
            }
         }
      }

      return entityPlayer;
   }

   public void removeDeadAndOldAgressors() {
      Iterator iterator = this.villageAgressors.iterator();

      while (iterator.hasNext()) {
         Village.VillageAggressor villageAggressor = (Village.VillageAggressor)iterator.next();
         if (!villageAggressor.agressor.isEntityAlive() || Math.abs(this.tickCounter - villageAggressor.agressionTime) > 300) {
            iterator.remove();
         }
      }
   }

   public void removeDeadAndOutOfRangeDoors() {
      boolean flag = false;
      boolean flag2 = this.worldObj.rand.nextInt(50) == 0;
      Iterator iterator = this.villageDoorInfoList.iterator();

      while (iterator.hasNext()) {
         VillageDoorInfo villageDoorInfo = (VillageDoorInfo)iterator.next();
         if (flag2) {
            villageDoorInfo.resetDoorOpeningRestrictionCounter();
         }

         if (!this.isWoodDoor(villageDoorInfo.getDoorBlockPos()) || Math.abs(this.tickCounter - villageDoorInfo.getInsidePosY()) > 1200) {
            this.centerHelper = this.centerHelper.subtract(villageDoorInfo.getDoorBlockPos());
            flag = true;
            villageDoorInfo.setIsDetachedFromVillageFlag(true);
            iterator.remove();
         }
      }

      if (flag) {
         this.updateVillageRadiusAndCenter();
      }
   }

   public boolean isWoodDoor(BlockPos pos) {
      Block block = this.worldObj.getBlockState(pos).getBlock();
      return block instanceof BlockDoor ? block.getMaterial() == Material.wood : false;
   }

   public void updateVillageRadiusAndCenter() {
      int size = this.villageDoorInfoList.size();
      if (size == 0) {
         this.center = new BlockPos(0, 0, 0);
         this.villageRadius = 0;
      } else {
         this.center = new BlockPos(this.centerHelper.getX() / size, this.centerHelper.getY() / size, this.centerHelper.getZ() / size);
         int n = 0;

         for (VillageDoorInfo villageDoorInfo : this.villageDoorInfoList) {
            n = Math.max(villageDoorInfo.getDistanceToDoorBlockSq(this.center), n);
         }

         this.villageRadius = Math.max(32, (int)Math.sqrt(n) + 1);
      }
   }

   public int getReputationForPlayer(String str) {
      Integer integer = this.playerReputation.get(str);
      return integer != null ? integer : 0;
   }

   public int setReputationForPlayer(String str, int n) {
      int reputationForPlayer = this.getReputationForPlayer(str);
      int n2 = MathHelper.clamp_int(reputationForPlayer + n, -30, 10);
      this.playerReputation.put(str, n2);
      return n2;
   }

   public boolean isPlayerReputationTooLow(String str) {
      return this.getReputationForPlayer(str) <= -15;
   }

   public void readVillageDataFromNBT(NBTTagCompound compound) {
      this.numVillagers = compound.getInteger("PopSize");
      this.villageRadius = compound.getInteger("Radius");
      this.numIronGolems = compound.getInteger("Golems");
      this.lastAddDoorTimestamp = compound.getInteger("Stable");
      this.tickCounter = compound.getInteger("Tick");
      this.noBreedTicks = compound.getInteger("MTick");
      this.center = new BlockPos(compound.getInteger("CX"), compound.getInteger("CY"), compound.getInteger("CZ"));
      this.centerHelper = new BlockPos(compound.getInteger("ACX"), compound.getInteger("ACY"), compound.getInteger("ACZ"));
      NBTTagList tagList = compound.getTagList("Doors", 10);

      for (int i = 0; i < tagList.tagCount(); i++) {
         NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
         VillageDoorInfo villageDoorInfo = new VillageDoorInfo(
            new BlockPos(compoundTagAt.getInteger("X"), compoundTagAt.getInteger("Y"), compoundTagAt.getInteger("Z")),
            compoundTagAt.getInteger("IDX"),
            compoundTagAt.getInteger("IDZ"),
            compoundTagAt.getInteger("TS")
         );
         this.villageDoorInfoList.add(villageDoorInfo);
      }

      NBTTagList tagList2 = compound.getTagList("Players", 10);

      for (int i = 0; i < tagList2.tagCount(); i++) {
         NBTTagCompound compoundTagAt = tagList2.getCompoundTagAt(i);
         if (compoundTagAt.hasKey("UUID")) {
            PlayerProfileCache playerProfileCache = MinecraftServer.getServer().getPlayerProfileCache();
            GameProfile profileByUUID = playerProfileCache.getProfileByUUID(UUID.fromString(compoundTagAt.getString("UUID")));
            if (profileByUUID != null) {
               this.playerReputation.put(profileByUUID.getName(), compoundTagAt.getInteger("S"));
            }
         } else {
            this.playerReputation.put(compoundTagAt.getString("Name"), compoundTagAt.getInteger("S"));
         }
      }
   }

   public void writeVillageDataToNBT(NBTTagCompound compound) {
      compound.setInteger("PopSize", this.numVillagers);
      compound.setInteger("Radius", this.villageRadius);
      compound.setInteger("Golems", this.numIronGolems);
      compound.setInteger("Stable", this.lastAddDoorTimestamp);
      compound.setInteger("Tick", this.tickCounter);
      compound.setInteger("MTick", this.noBreedTicks);
      compound.setInteger("CX", this.center.getX());
      compound.setInteger("CY", this.center.getY());
      compound.setInteger("CZ", this.center.getZ());
      compound.setInteger("ACX", this.centerHelper.getX());
      compound.setInteger("ACY", this.centerHelper.getY());
      compound.setInteger("ACZ", this.centerHelper.getZ());
      NBTTagList nbttagList = new NBTTagList();

      for (VillageDoorInfo villageDoorInfo : this.villageDoorInfoList) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         nbttagCompound.setInteger("X", villageDoorInfo.getDoorBlockPos().getX());
         nbttagCompound.setInteger("Y", villageDoorInfo.getDoorBlockPos().getY());
         nbttagCompound.setInteger("Z", villageDoorInfo.getDoorBlockPos().getZ());
         nbttagCompound.setInteger("IDX", villageDoorInfo.getInsideOffsetX());
         nbttagCompound.setInteger("IDZ", villageDoorInfo.getInsideOffsetZ());
         nbttagCompound.setInteger("TS", villageDoorInfo.getInsidePosY());
         nbttagList.appendTag(nbttagCompound);
      }

      compound.setTag("Doors", nbttagList);
      NBTTagList nbttagList2 = new NBTTagList();

      for (String str : this.playerReputation.keySet()) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         PlayerProfileCache playerProfileCache = MinecraftServer.getServer().getPlayerProfileCache();
         GameProfile gameProfileForUsername = playerProfileCache.getGameProfileForUsername(str);
         if (gameProfileForUsername != null) {
            nbttagCompound.setString("UUID", gameProfileForUsername.getId().toString());
            nbttagCompound.setInteger("S", this.playerReputation.get(str));
            nbttagList2.appendTag(nbttagCompound);
         }
      }

      compound.setTag("Players", nbttagList2);
   }

   public void endMatingSeason() {
      this.noBreedTicks = this.tickCounter;
   }

   public boolean isMatingSeason() {
      return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
   }

   public void setDefaultPlayerReputation(int n) {
      for (String str : this.playerReputation.keySet()) {
         this.setReputationForPlayer(str, n);
      }
   }

   class VillageAggressor {
      public EntityLivingBase agressor;
      public int agressionTime;

      public VillageAggressor(EntityLivingBase entityLivingBase, int n) {
         this.agressor = entityLivingBase;
         this.agressionTime = n;
      }
   }
}
