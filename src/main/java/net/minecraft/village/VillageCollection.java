package net.minecraft.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockDoor;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData {
    public List villagerPositionsList = new ArrayList();
    public List newDoors = new ArrayList();
    public List villageList = new ArrayList();
    public World worldObj;
    public int tickCounter;

    public VillageCollection(String str) {
        super(str);
    }

    public VillageCollection(World world) {
        super("villages");
        this.worldObj = world;
        this.markDirty();
    }

    public void setWorld(World world) {
        this.worldObj = world;

        for (Village village : (Iterable<Village>)this.villageList) {
            village.setWorld(world);
        }
    }

    public void addVillagerPosition(int n, int n2, int n3) {
        if (this.villagerPositionsList.size() <= 64 && !this.isVillagerPositionPresent(n, n2, n3)) {
            this.villagerPositionsList.add(new ChunkCoordinates(n, n2, n3));
        }
    }

    public void tick() {
        this.tickCounter++;

        for (Village village : (Iterable<Village>)this.villageList) {
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

    public List getVillageList() {
        return this.villageList;
    }

    public Village findNearestVillage(int n, int n2, int n3, int n4) {
        Village village = null;
        float f = Float.MAX_VALUE;

        for (Village village2 : (Iterable<Village>)this.villageList) {
            float distanceSquared = village2.getCenter().getDistanceSquared(n, n2, n3);
            if (distanceSquared < f) {
                float f2 = n4 + village2.getVillageRadius();
                if (distanceSquared <= f2 * f2) {
                    village = village2;
                    f = distanceSquared;
                }
            }
        }

        return village;
    }

    public void dropOldestVillagerPosition() {
        if (!this.villagerPositionsList.isEmpty()) {
            this.addUnassignedWoodenDoorsAroundToNewDoorsList((ChunkCoordinates)this.villagerPositionsList.remove(0));
        }
    }

    public void addNewDoorsToVillageOrCreateVillage() {
        for (int i = 0; i < this.newDoors.size(); i++) {
            VillageDoorInfo villageDoorInfo = (VillageDoorInfo)this.newDoors.get(i);
            boolean flag = false;

            for (Village village : (Iterable<Village>)this.villageList) {
                int distanceSquared = (int)village.getCenter().getDistanceSquared(villageDoorInfo.posX, villageDoorInfo.posY, villageDoorInfo.posZ);
                int n = 32 + village.getVillageRadius();
                if (distanceSquared <= n * n) {
                    village.addVillageDoorInfo(villageDoorInfo);
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                Village village = new Village(this.worldObj);
                village.addVillageDoorInfo(villageDoorInfo);
                this.villageList.add(village);
                this.markDirty();
            }
        }

        this.newDoors.clear();
    }

    public void addUnassignedWoodenDoorsAroundToNewDoorsList(ChunkCoordinates chunkCoordinates) {
        byte b = 16;
        byte b2 = 4;
        byte b3 = 16;

        for (int i = chunkCoordinates.posX - b; i < chunkCoordinates.posX + b; i++) {
            for (int j = chunkCoordinates.posY - b2; j < chunkCoordinates.posY + b2; j++) {
                for (int k = chunkCoordinates.posZ - b3; k < chunkCoordinates.posZ + b3; k++) {
                    if (this.isWoodenDoorAt(i, j, k)) {
                        VillageDoorInfo villageDoorAt = this.getVillageDoorAt(i, j, k);
                        if (villageDoorAt == null) {
                            this.addDoorToNewListIfAppropriate(i, j, k);
                        } else {
                            villageDoorAt.lastActivityTimestamp = this.tickCounter;
                        }
                    }
                }
            }
        }
    }

    public VillageDoorInfo getVillageDoorAt(int n, int n2, int n3) {
        for (VillageDoorInfo villageDoorInfo : (Iterable<VillageDoorInfo>)this.newDoors) {
            if (villageDoorInfo.posX == n && villageDoorInfo.posZ == n3 && Math.abs(villageDoorInfo.posY - n2) <= 1) {
                return villageDoorInfo;
            }
        }

        for (Village village : (Iterable<Village>)this.villageList) {
            VillageDoorInfo villageDoorAt = village.getVillageDoorAt(n, n2, n3);
            if (villageDoorAt != null) {
                return villageDoorAt;
            }
        }

        return null;
    }

    public void addDoorToNewListIfAppropriate(int n, int n2, int n3) {
        int fullMetadata2 = ((BlockDoor)Blocks.wooden_door).getFullMetadata2(this.worldObj, n, n2, n3);
        if (fullMetadata2 != 0 && fullMetadata2 != 2) {
            int n4 = 0;

            for (int i = -5; i < 0; i++) {
                if (this.worldObj.canBlockSeeTheSky(n, n2, n3 + i)) {
                    n4--;
                }
            }

            for (int i = 1; i <= 5; i++) {
                if (this.worldObj.canBlockSeeTheSky(n, n2, n3 + i)) {
                    n4++;
                }
            }

            if (n4 != 0) {
                this.newDoors.add(new VillageDoorInfo(n, n2, n3, 0, n4 > 0 ? -2 : 2, this.tickCounter));
            }
        } else {
            int n4 = 0;

            for (int i = -5; i < 0; i++) {
                if (this.worldObj.canBlockSeeTheSky(n + i, n2, n3)) {
                    n4--;
                }
            }

            for (int i = 1; i <= 5; i++) {
                if (this.worldObj.canBlockSeeTheSky(n + i, n2, n3)) {
                    n4++;
                }
            }

            if (n4 != 0) {
                this.newDoors.add(new VillageDoorInfo(n, n2, n3, n4 > 0 ? -2 : 2, 0, this.tickCounter));
            }
        }
    }

    public boolean isVillagerPositionPresent(int n, int n2, int n3) {
        for (ChunkCoordinates chunkCoordinates : (Iterable<ChunkCoordinates>)this.villagerPositionsList) {
            if (chunkCoordinates.posX == n && chunkCoordinates.posY == n2 && chunkCoordinates.posZ == n3) {
                return true;
            }
        }

        return false;
    }

    public boolean isWoodenDoorAt(int n, int n2, int n3) {
        return this.worldObj.getBlock(n, n2, n3) == Blocks.wooden_door;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagCompound) {
        this.tickCounter = nbttagCompound.getInteger("Tick");
        NBTTagList tagList = nbttagCompound.getTagList("Villages", 10);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            Village village = new Village();
            village.readVillageDataFromNBT(compoundTagAt);
            this.villageList.add(village);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagCompound) {
        nbttagCompound.setInteger("Tick", this.tickCounter);
        NBTTagList nbttagList = new NBTTagList();

        for (Village village : (Iterable<Village>)this.villageList) {
            NBTTagCompound nbttagCompound2 = new NBTTagCompound();
            village.writeVillageDataToNBT(nbttagCompound2);
            nbttagList.appendTag(nbttagCompound2);
        }

        nbttagCompound.setTag("Villages", nbttagList);
    }
}
