package net.minecraft.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Village {
    public List villageDoorInfoList = new ArrayList();
    public ChunkCoordinates centerHelper = new ChunkCoordinates(0, 0, 0);
    public ChunkCoordinates center = new ChunkCoordinates(0, 0, 0);
    public TreeMap playerReputation = new TreeMap();
    public int villageRadius;
    public int lastAddDoorTimestamp;
    public int tickCounter;
    public int numVillagers;
    public int noBreedTicks;
    public List villageAgressors = new ArrayList();
    public World worldObj;
    public int numIronGolems;

    public Village() {
    }

    public Village(World world) {
        this.worldObj = world;
    }

    public void setWorld(World world) {
        this.worldObj = world;
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
            Vec3 tryGetIronGolemSpawningLocation = this.tryGetIronGolemSpawningLocation(
                MathHelper.floor_float(this.center.posX), MathHelper.floor_float(this.center.posY), MathHelper.floor_float(this.center.posZ), 2, 4, 2
            );
            if (tryGetIronGolemSpawningLocation != null) {
                EntityIronGolem entityIronGolem = new EntityIronGolem(this.worldObj);
                entityIronGolem.setPosition(tryGetIronGolemSpawningLocation.xCoord, tryGetIronGolemSpawningLocation.yCoord, tryGetIronGolemSpawningLocation.zCoord);
                this.worldObj.spawnEntityInWorld(entityIronGolem);
                this.numIronGolems++;
            }
        }
    }

    public Vec3 tryGetIronGolemSpawningLocation(int n, int n2, int n3, int n4, int n5, int n6) {
        for (int i = 0; i < 10; i++) {
            int n7 = n + this.worldObj.rand.nextInt(16) - 8;
            int n8 = n2 + this.worldObj.rand.nextInt(6) - 3;
            int n9 = n3 + this.worldObj.rand.nextInt(16) - 8;
            if (this.isInRange(n7, n8, n9) && this.isValidIronGolemSpawningLocation(n7, n8, n9, n4, n5, n6)) {
                return Vec3.createVectorHelper(n7, n8, n9);
            }
        }

        return null;
    }

    public boolean isValidIronGolemSpawningLocation(int n, int n2, int n3, int n4, int n5, int n6) {
        if (!World.doesBlockHaveSolidTopSurface(this.worldObj, n, n2 - 1, n3)) {
            return false;
        }

        int n7 = n - n4 / 2;
        int n8 = n3 - n6 / 2;

        for (int i = n7; i < n7 + n4; i++) {
            for (int j = n2; j < n2 + n5; j++) {
                for (int k = n8; k < n8 + n6; k++) {
                    if (this.worldObj.getBlock(i, j, k).isNormalCube()) {
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
                AxisAlignedBB.getBoundingBox(
                    this.center.posX - this.villageRadius,
                    this.center.posY - 4,
                    this.center.posZ - this.villageRadius,
                    this.center.posX + this.villageRadius,
                    this.center.posY + 4,
                    this.center.posZ + this.villageRadius
                )
            );
        this.numIronGolems = entitiesWithinAABB.size();
    }

    public void updateNumVillagers() {
        List entitiesWithinAABB = this.worldObj
            .getEntitiesWithinAABB(
                EntityVillager.class,
                AxisAlignedBB.getBoundingBox(
                    this.center.posX - this.villageRadius,
                    this.center.posY - 4,
                    this.center.posZ - this.villageRadius,
                    this.center.posX + this.villageRadius,
                    this.center.posY + 4,
                    this.center.posZ + this.villageRadius
                )
            );
        this.numVillagers = entitiesWithinAABB.size();
        if (this.numVillagers == 0) {
            this.playerReputation.clear();
        }
    }

    public ChunkCoordinates getCenter() {
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

    public boolean isInRange(int n, int n2, int n3) {
        return this.center.getDistanceSquared(n, n2, n3) < this.villageRadius * this.villageRadius;
    }

    public List getVillageDoorInfoList() {
        return this.villageDoorInfoList;
    }

    public VillageDoorInfo findNearestDoor(int n, int n2, int n3) {
        VillageDoorInfo villageDoorInfo = null;
        int n4 = Integer.MAX_VALUE;

        for (VillageDoorInfo villageDoorInfo2 : (Iterable<VillageDoorInfo>)this.villageDoorInfoList) {
            int distanceSquared = villageDoorInfo2.getDistanceSquared(n, n2, n3);
            if (distanceSquared < n4) {
                villageDoorInfo = villageDoorInfo2;
                n4 = distanceSquared;
            }
        }

        return villageDoorInfo;
    }

    public VillageDoorInfo findNearestDoorUnrestricted(int n, int n2, int n3) {
        VillageDoorInfo villageDoorInfo = null;
        int n4 = Integer.MAX_VALUE;

        for (VillageDoorInfo villageDoorInfo2 : (Iterable<VillageDoorInfo>)this.villageDoorInfoList) {
            int distanceSquared = villageDoorInfo2.getDistanceSquared(n, n2, n3);
            if (distanceSquared > 256) {
                distanceSquared *= 1000;
            } else {
                distanceSquared = villageDoorInfo2.getDoorOpeningRestrictionCounter();
            }

            if (distanceSquared < n4) {
                villageDoorInfo = villageDoorInfo2;
                n4 = distanceSquared;
            }
        }

        return villageDoorInfo;
    }

    public VillageDoorInfo getVillageDoorAt(int n, int n2, int n3) {
        if (this.center.getDistanceSquared(n, n2, n3) > this.villageRadius * this.villageRadius) {
            return null;
        }

        for (VillageDoorInfo villageDoorInfo : (Iterable<VillageDoorInfo>)this.villageDoorInfoList) {
            if (villageDoorInfo.posX == n && villageDoorInfo.posZ == n3 && Math.abs(villageDoorInfo.posY - n2) <= 1) {
                return villageDoorInfo;
            }
        }

        return null;
    }

    public void addVillageDoorInfo(VillageDoorInfo villageDoorInfo) {
        this.villageDoorInfoList.add(villageDoorInfo);
        this.centerHelper.posX = this.centerHelper.posX + villageDoorInfo.posX;
        this.centerHelper.posY = this.centerHelper.posY + villageDoorInfo.posY;
        this.centerHelper.posZ = this.centerHelper.posZ + villageDoorInfo.posZ;
        this.updateVillageRadiusAndCenter();
        this.lastAddDoorTimestamp = villageDoorInfo.lastActivityTimestamp;
    }

    public boolean isAnnihilated() {
        return this.villageDoorInfoList.isEmpty();
    }

    public void addOrRenewAgressor(EntityLivingBase entityLivingBase) {
        for (Village.VillageAgressor villageAgressor : (Iterable<Village.VillageAgressor>)this.villageAgressors) {
            if (villageAgressor.agressor == entityLivingBase) {
                villageAgressor.agressionTime = this.tickCounter;
                return;
            }
        }

        this.villageAgressors.add(new Village.VillageAgressor(entityLivingBase, this.tickCounter));
    }

    public EntityLivingBase findNearestVillageAggressor(EntityLivingBase entityLivingBase) {
        double d = Double.MAX_VALUE;
        Village.VillageAgressor villageAgressor = null;

        for (int i = 0; i < this.villageAgressors.size(); i++) {
            Village.VillageAgressor villageAgressor2 = (Village.VillageAgressor)this.villageAgressors.get(i);
            double distanceSqToEntity = villageAgressor2.agressor.getDistanceSqToEntity(entityLivingBase);
            if (distanceSqToEntity <= d) {
                villageAgressor = villageAgressor2;
                d = distanceSqToEntity;
            }
        }

        return villageAgressor != null ? villageAgressor.agressor : null;
    }

    public EntityPlayer findEntityPlayer(EntityLivingBase entityLivingBase) {
        double d = Double.MAX_VALUE;
        EntityPlayer entityPlayer = null;

        for (String str : (Iterable<String>)this.playerReputation.keySet()) {
            if (this.isPlayerReputationTooLow(str)) {
                EntityPlayer playerEntityByName = this.worldObj.getPlayerEntityByName(str);
                if (playerEntityByName != null) {
                    double distanceSqToEntity = playerEntityByName.getDistanceSqToEntity(entityLivingBase);
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
            Village.VillageAgressor villageAgressor = (Village.VillageAgressor)iterator.next();
            if (!villageAgressor.agressor.isEntityAlive() || Math.abs(this.tickCounter - villageAgressor.agressionTime) > 300) {
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

            if (!this.isBlockDoor(villageDoorInfo.posX, villageDoorInfo.posY, villageDoorInfo.posZ) || Math.abs(this.tickCounter - villageDoorInfo.lastActivityTimestamp) > 1200) {
                this.centerHelper.posX = this.centerHelper.posX - villageDoorInfo.posX;
                this.centerHelper.posY = this.centerHelper.posY - villageDoorInfo.posY;
                this.centerHelper.posZ = this.centerHelper.posZ - villageDoorInfo.posZ;
                flag = true;
                villageDoorInfo.isDetachedFromVillageFlag = true;
                iterator.remove();
            }
        }

        if (flag) {
            this.updateVillageRadiusAndCenter();
        }
    }

    public boolean isBlockDoor(int n, int n2, int n3) {
        return this.worldObj.getBlock(n, n2, n3) == Blocks.wooden_door;
    }

    public void updateVillageRadiusAndCenter() {
        int size = this.villageDoorInfoList.size();
        if (size == 0) {
            this.center.set(0, 0, 0);
            this.villageRadius = 0;
        } else {
            this.center.set(this.centerHelper.posX / size, this.centerHelper.posY / size, this.centerHelper.posZ / size);
            int n = 0;

            for (VillageDoorInfo villageDoorInfo : (Iterable<VillageDoorInfo>)this.villageDoorInfoList) {
                n = Math.max(villageDoorInfo.getDistanceSquared(this.center.posX, this.center.posY, this.center.posZ), n);
            }

            this.villageRadius = Math.max(32, (int)Math.sqrt(n) + 1);
        }
    }

    public int getReputationForPlayer(String str) {
        Integer integer = (Integer)this.playerReputation.get(str);
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

    public void readVillageDataFromNBT(NBTTagCompound nbttagCompound) {
        this.numVillagers = nbttagCompound.getInteger("PopSize");
        this.villageRadius = nbttagCompound.getInteger("Radius");
        this.numIronGolems = nbttagCompound.getInteger("Golems");
        this.lastAddDoorTimestamp = nbttagCompound.getInteger("Stable");
        this.tickCounter = nbttagCompound.getInteger("Tick");
        this.noBreedTicks = nbttagCompound.getInteger("MTick");
        this.center.posX = nbttagCompound.getInteger("CX");
        this.center.posY = nbttagCompound.getInteger("CY");
        this.center.posZ = nbttagCompound.getInteger("CZ");
        this.centerHelper.posX = nbttagCompound.getInteger("ACX");
        this.centerHelper.posY = nbttagCompound.getInteger("ACY");
        this.centerHelper.posZ = nbttagCompound.getInteger("ACZ");
        NBTTagList tagList = nbttagCompound.getTagList("Doors", 10);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            VillageDoorInfo villageDoorInfo = new VillageDoorInfo(
                compoundTagAt.getInteger("X"), compoundTagAt.getInteger("Y"), compoundTagAt.getInteger("Z"), compoundTagAt.getInteger("IDX"), compoundTagAt.getInteger("IDZ"), compoundTagAt.getInteger("TS")
            );
            this.villageDoorInfoList.add(villageDoorInfo);
        }

        NBTTagList tagList2 = nbttagCompound.getTagList("Players", 10);

        for (int i = 0; i < tagList2.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList2.getCompoundTagAt(i);
            this.playerReputation.put(compoundTagAt.getString("Name"), compoundTagAt.getInteger("S"));
        }
    }

    public void writeVillageDataToNBT(NBTTagCompound nbttagCompound) {
        nbttagCompound.setInteger("PopSize", this.numVillagers);
        nbttagCompound.setInteger("Radius", this.villageRadius);
        nbttagCompound.setInteger("Golems", this.numIronGolems);
        nbttagCompound.setInteger("Stable", this.lastAddDoorTimestamp);
        nbttagCompound.setInteger("Tick", this.tickCounter);
        nbttagCompound.setInteger("MTick", this.noBreedTicks);
        nbttagCompound.setInteger("CX", this.center.posX);
        nbttagCompound.setInteger("CY", this.center.posY);
        nbttagCompound.setInteger("CZ", this.center.posZ);
        nbttagCompound.setInteger("ACX", this.centerHelper.posX);
        nbttagCompound.setInteger("ACY", this.centerHelper.posY);
        nbttagCompound.setInteger("ACZ", this.centerHelper.posZ);
        NBTTagList nbttagList = new NBTTagList();

        for (VillageDoorInfo villageDoorInfo : (Iterable<VillageDoorInfo>)this.villageDoorInfoList) {
            NBTTagCompound nbttagCompound2 = new NBTTagCompound();
            nbttagCompound2.setInteger("X", villageDoorInfo.posX);
            nbttagCompound2.setInteger("Y", villageDoorInfo.posY);
            nbttagCompound2.setInteger("Z", villageDoorInfo.posZ);
            nbttagCompound2.setInteger("IDX", villageDoorInfo.insideDirectionX);
            nbttagCompound2.setInteger("IDZ", villageDoorInfo.insideDirectionZ);
            nbttagCompound2.setInteger("TS", villageDoorInfo.lastActivityTimestamp);
            nbttagList.appendTag(nbttagCompound2);
        }

        nbttagCompound.setTag("Doors", nbttagList);
        NBTTagList nbttagList2 = new NBTTagList();

        for (String str : (Iterable<String>)this.playerReputation.keySet()) {
            NBTTagCompound nbttagCompound2 = new NBTTagCompound();
            nbttagCompound2.setString("Name", str);
            nbttagCompound2.setInteger("S", (Integer)this.playerReputation.get(str));
            nbttagList2.appendTag(nbttagCompound2);
        }

        nbttagCompound.setTag("Players", nbttagList2);
    }

    public void endMatingSeason() {
        this.noBreedTicks = this.tickCounter;
    }

    public boolean isMatingSeason() {
        return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
    }

    public void setDefaultPlayerReputation(int n) {
        for (String str : (Iterable<String>)this.playerReputation.keySet()) {
            this.setReputationForPlayer(str, n);
        }
    }

    public class VillageAgressor {
        public EntityLivingBase agressor;
        public int agressionTime;

        public VillageAgressor(EntityLivingBase entityLivingBase, int n) {
            this.agressor = entityLivingBase;
            this.agressionTime = n;
        }
    }
}
