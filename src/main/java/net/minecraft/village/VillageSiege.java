package net.minecraft.village;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
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

    public VillageSiege(World world) {
        this.worldObj = world;
    }

    public void tick() {
        boolean flag = false;
        if (flag) {
            if (this.intValue == 2) {
                this.intValue2 = 100;
                return;
            }
        } else {
            if (this.worldObj.isDaytime()) {
                this.intValue = 0;
                return;
            }

            if (this.intValue == 2) {
                return;
            }

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
        }

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

    public boolean check() {
        for (EntityPlayer entityPlayer : this.worldObj.playerEntities) {
            this.theVillage = this.worldObj.villageCollectionObj.findNearestVillage((int)entityPlayer.posX, (int)entityPlayer.posY, (int)entityPlayer.posZ, 1);
            if (this.theVillage != null
                && this.theVillage.getNumVillageDoors() >= 10
                && this.theVillage.getTicksSinceLastDoorAdding() >= 20
                && this.theVillage.getNumVillagers() >= 20) {
                ChunkCoordinates center = this.theVillage.getCenter();
                float villageRadius = this.theVillage.getVillageRadius();
                boolean flag = false;

                for (int i = 0; i < 10; i++) {
                    this.intValue4 = center.posX + (int)(MathHelper.cos(this.worldObj.rand.nextFloat() * (float) Math.PI * 2.0F) * villageRadius * 0.9);
                    this.intValue5 = center.posY;
                    this.intValue6 = center.posZ + (int)(MathHelper.sin(this.worldObj.rand.nextFloat() * (float) Math.PI * 2.0F) * villageRadius * 0.9);
                    flag = false;

                    for (Village village : (Iterable<Village>)this.worldObj.villageCollectionObj.getVillageList()) {
                        if (village != this.theVillage && village.isInRange(this.intValue4, this.intValue5, this.intValue6)) {
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

                Vec3 findVec3 = this.findVec3(this.intValue4, this.intValue5, this.intValue6);
                if (findVec3 != null) {
                    this.intValue3 = 0;
                    this.intValue2 = 20;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean spawnZombie() {
        Vec3 findVec3 = this.findVec3(this.intValue4, this.intValue5, this.intValue6);
        if (findVec3 == null) {
            return false;
        }

        EntityZombie entityZombie;
        try {
            entityZombie = new EntityZombie(this.worldObj);
            entityZombie.onSpawnWithEgg(null);
            entityZombie.setVillager(false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        entityZombie.setLocationAndAngles(findVec3.xCoord, findVec3.yCoord, findVec3.zCoord, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
        this.worldObj.spawnEntityInWorld(entityZombie);
        ChunkCoordinates center = this.theVillage.getCenter();
        entityZombie.setHomeArea(center.posX, center.posY, center.posZ, this.theVillage.getVillageRadius());
        return true;
    }

    public Vec3 findVec3(int n, int n2, int n3) {
        for (int i = 0; i < 10; i++) {
            int n4 = n + this.worldObj.rand.nextInt(16) - 8;
            int n5 = n2 + this.worldObj.rand.nextInt(6) - 3;
            int n6 = n3 + this.worldObj.rand.nextInt(16) - 8;
            if (this.theVillage.isInRange(n4, n5, n6)
                && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, this.worldObj, n4, n5, n6)) {
                Vec3.createVectorHelper(n4, n5, n6);
            }
        }

        return null;
    }
}
