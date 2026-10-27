package net.minecraft.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData {
   public List<BlockPos> villagerPositionsList = Lists.newArrayList();
   public List<VillageDoorInfo> newDoors = Lists.newArrayList();
   public List<Village> villageList = Lists.newArrayList();
   public World worldObj;
   public int tickCounter;

   public VillageCollection(String name) {
      super(name);
   }

   public VillageCollection(World worldIn) {
      super(fileNameForProvider(worldIn.provider));
      this.worldObj = worldIn;
      this.markDirty();
   }

   public void setWorldsForAll(World worldIn) {
      this.worldObj = worldIn;

      for (Village village : this.villageList) {
         village.setWorld(worldIn);
      }
   }

   public void addToVillagerPositionList(BlockPos pos) {
      if (this.villagerPositionsList.size() <= 64 && !this.positionInList(pos)) {
         this.villagerPositionsList.add(pos);
      }
   }

   public void tick() {
      this.tickCounter++;

      for (Village village : this.villageList) {
         village.tick(this.tickCounter);
      }

      this.removeAnnihilatedVillages();
      this.dropOldestVillagerPosition();
      this.addNewDoorsToVillageOrCreateVillage();
      if (this.tickCounter % 400 == 0) {
         this.markDirty();
      }
   }

   public void removeAnnihilatedVillages() {
      Iterator iterator = this.villageList.iterator();

      while (iterator.hasNext()) {
         Village village = (Village)iterator.next();
         if (village.isAnnihilated()) {
            iterator.remove();
            this.markDirty();
         }
      }
   }

   public List<Village> getVillageList() {
      return this.villageList;
   }

   public Village getNearestVillage(BlockPos doorBlock, int radius) {
      Village village = null;
      double d = Float.MAX_VALUE;

      for (Village village2 : this.villageList) {
         double distanceSq = village2.getCenter().distanceSq(doorBlock);
         if (distanceSq < d) {
            float f = radius + village2.getVillageRadius();
            if (distanceSq <= f * f) {
               village = village2;
               d = distanceSq;
            }
         }
      }

      return village;
   }

   public void dropOldestVillagerPosition() {
      if (!this.villagerPositionsList.isEmpty()) {
         this.addDoorsAround(this.villagerPositionsList.remove(0));
      }
   }

   public void addNewDoorsToVillageOrCreateVillage() {
      for (int i = 0; i < this.newDoors.size(); i++) {
         VillageDoorInfo villageDoorInfo = this.newDoors.get(i);
         Village nearestVillage = this.getNearestVillage(villageDoorInfo.getDoorBlockPos(), 32);
         if (nearestVillage == null) {
            nearestVillage = new Village(this.worldObj);
            this.villageList.add(nearestVillage);
            this.markDirty();
         }

         nearestVillage.addVillageDoorInfo(villageDoorInfo);
      }

      this.newDoors.clear();
   }

   public void addDoorsAround(BlockPos central) {
      byte b = 16;
      byte b2 = 4;
      byte b3 = 16;

      for (int i = -b; i < b; i++) {
         for (int j = -b2; j < b2; j++) {
            for (int k = -b3; k < b3; k++) {
               BlockPos blockPos = central.add(i, j, k);
               if (this.isWoodDoor(blockPos)) {
                  VillageDoorInfo checkDoorExistence = this.checkDoorExistence(blockPos);
                  if (checkDoorExistence == null) {
                     this.addToNewDoorsList(blockPos);
                  } else {
                     checkDoorExistence.setLastActivityTimestamp(this.tickCounter);
                  }
               }
            }
         }
      }
   }

   public VillageDoorInfo checkDoorExistence(BlockPos doorBlock) {
      for (VillageDoorInfo villageDoorInfo : this.newDoors) {
         if (villageDoorInfo.getDoorBlockPos().getX() == doorBlock.getX()
            && villageDoorInfo.getDoorBlockPos().getZ() == doorBlock.getZ()
            && Math.abs(villageDoorInfo.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
            return villageDoorInfo;
         }
      }

      for (Village village : this.villageList) {
         VillageDoorInfo existedDoor = village.getExistedDoor(doorBlock);
         if (existedDoor != null) {
            return existedDoor;
         }
      }

      return null;
   }

   public void addToNewDoorsList(BlockPos doorBlock) {
      EnumFacing facing = BlockDoor.getFacing(this.worldObj, doorBlock);
      EnumFacing opposite = facing.getOpposite();
      int countBlocksCanSeeSky = this.countBlocksCanSeeSky(doorBlock, facing, 5);
      int countBlocksCanSeeSky2 = this.countBlocksCanSeeSky(doorBlock, opposite, countBlocksCanSeeSky + 1);
      if (countBlocksCanSeeSky != countBlocksCanSeeSky2) {
         this.newDoors.add(new VillageDoorInfo(doorBlock, countBlocksCanSeeSky < countBlocksCanSeeSky2 ? facing : opposite, this.tickCounter));
      }
   }

   public int countBlocksCanSeeSky(BlockPos centerPos, EnumFacing direction, int limitation) {
      int n = 0;

      for (int i = 1; i <= 5; i++) {
         if (this.worldObj.canSeeSky(centerPos.offset(direction, i))) {
            if (++n >= limitation) {
               return n;
            }
         }
      }

      return n;
   }

   public boolean positionInList(BlockPos pos) {
      for (BlockPos blockPos : this.villagerPositionsList) {
         if (blockPos.equals(pos)) {
            return true;
         }
      }

      return false;
   }

   public boolean isWoodDoor(BlockPos doorPos) {
      Block block = this.worldObj.getBlockState(doorPos).getBlock();
      return block instanceof BlockDoor ? block.getMaterial() == Material.wood : false;
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      this.tickCounter = nbt.getInteger("Tick");
      NBTTagList tagList = nbt.getTagList("Villages", 10);

      for (int i = 0; i < tagList.tagCount(); i++) {
         NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
         Village village = new Village();
         village.readVillageDataFromNBT(compoundTagAt);
         this.villageList.add(village);
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("Tick", this.tickCounter);
      NBTTagList nbttagList = new NBTTagList();

      for (Village village : this.villageList) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         village.writeVillageDataToNBT(nbttagCompound);
         nbttagList.appendTag(nbttagCompound);
      }

      nbt.setTag("Villages", nbttagList);
   }

   public static String fileNameForProvider(WorldProvider provider) {
      return "villages" + provider.getInternalNameSuffix();
   }
}
