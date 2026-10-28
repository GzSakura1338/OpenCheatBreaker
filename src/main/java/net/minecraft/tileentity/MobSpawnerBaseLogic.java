package net.minecraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic {
    public int spawnDelay = 20;
    public String mobID = "Pig";
    public List minecartToSpawn;
    public MobSpawnerBaseLogic.WeightedRandomMinecart randomEntity;
    public double mobAngleNext;
    public double mobAngle;
    public int minSpawnDelay = 200;
    public int maxSpawnDelay = 800;
    public int spawnCount = 4;
    public Entity cachedEntity;
    public int maxNearbyEntities = 6;
    public int activatingRangeFromPlayer = 16;
    public int spawnRange = 4;

    public String getEntityNameToSpawn() {
        if (this.getRandomEntity() == null) {
            if (this.mobID.equals("Minecart")) {
                this.mobID = "MinecartRideable";
            }

            return this.mobID;
        } else {
            return this.getRandomEntity().entityType;
        }
    }

    public void setEntityName(String str) {
        this.mobID = str;
    }

    public boolean isActivated() {
        return this.getSpawnerWorld()
                .getClosestPlayer(this.getSpawnerX() + 0.5, this.getSpawnerY() + 0.5, this.getSpawnerZ() + 0.5, this.activatingRangeFromPlayer)
            != null;
    }

    public void updateSpawner() {
        if (this.isActivated()) {
            if (this.getSpawnerWorld().isRemote) {
                double d = this.getSpawnerX() + this.getSpawnerWorld().rand.nextFloat();
                double d2 = this.getSpawnerY() + this.getSpawnerWorld().rand.nextFloat();
                double d3 = this.getSpawnerZ() + this.getSpawnerWorld().rand.nextFloat();
                this.getSpawnerWorld().spawnParticle("smoke", d, d2, d3, 0.0, 0.0, 0.0);
                this.getSpawnerWorld().spawnParticle("flame", d, d2, d3, 0.0, 0.0, 0.0);
                if (this.spawnDelay > 0) {
                    this.spawnDelay--;
                }

                this.mobAngle = this.mobAngleNext;
                this.mobAngleNext = (this.mobAngleNext + 1000.0F / (this.spawnDelay + 200.0F)) % 360.0;
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
                            AxisAlignedBB.getBoundingBox(
                                    this.getSpawnerX(), this.getSpawnerY(), this.getSpawnerZ(), this.getSpawnerX() + 1, this.getSpawnerY() + 1, this.getSpawnerZ() + 1
                                )
                                .expand(this.spawnRange * 2, 4.0, this.spawnRange * 2)
                        )
                        .size();
                    if (size >= this.maxNearbyEntities) {
                        this.resetTimer();
                        return;
                    }

                    double d = this.getSpawnerX() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * this.spawnRange;
                    double d2 = this.getSpawnerY() + this.getSpawnerWorld().rand.nextInt(3) - 1;
                    double d3 = this.getSpawnerZ() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * this.spawnRange;
                    EntityLiving entityLiving = createEntityByName instanceof EntityLiving ? (EntityLiving)createEntityByName : null;
                    createEntityByName.setLocationAndAngles(d, d2, d3, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);
                    if (entityLiving == null || entityLiving.getCanSpawnHere()) {
                        this.spawnEntity(createEntityByName);
                        this.getSpawnerWorld().playAuxSFX(2004, this.getSpawnerX(), this.getSpawnerY(), this.getSpawnerZ(), 0);
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

    public Entity spawnEntity(Entity entity) {
        if (this.getRandomEntity() != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            entity.writeToNBTOptional(nbttagCompound);

            for (String str : (Iterable<String>)this.getRandomEntity().nbtTagCompound.getKeySet()) {
                NBTBase tag = this.getRandomEntity().nbtTagCompound.getTag(str);
                nbttagCompound.setTag(str, tag.copy());
            }

            entity.readFromNBT(nbttagCompound);
            if (entity.worldObj != null) {
                entity.worldObj.spawnEntityInWorld(entity);
            }

            Entity entity2 = entity;

            while (nbttagCompound.hasKey("Riding", 10)) {
                NBTTagCompound compoundTag = nbttagCompound.getCompoundTag("Riding");
                Entity createEntityByName = EntityList.createEntityByName(compoundTag.getString("id"), entity.worldObj);
                if (createEntityByName != null) {
                    NBTTagCompound nbttagCompound2 = new NBTTagCompound();
                    createEntityByName.writeToNBTOptional(nbttagCompound2);

                    for (String str : (Iterable<String>)compoundTag.getKeySet()) {
                        NBTBase tag = compoundTag.getTag(str);
                        nbttagCompound2.setTag(str, tag.copy());
                    }

                    createEntityByName.readFromNBT(nbttagCompound2);
                    createEntityByName.setLocationAndAngles(entity2.posX, entity2.posY, entity2.posZ, entity2.rotationYaw, entity2.rotationPitch);
                    if (entity.worldObj != null) {
                        entity.worldObj.spawnEntityInWorld(createEntityByName);
                    }

                    entity2.mountEntity(createEntityByName);
                }

                entity2 = createEntityByName;
                nbttagCompound = compoundTag;
            }
        } else if (entity instanceof EntityLivingBase && entity.worldObj != null) {
            ((EntityLiving)entity).onSpawnWithEgg(null);
            this.getSpawnerWorld().spawnEntityInWorld(entity);
        }

        return entity;
    }

    public void resetTimer() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int n = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(n);
        }

        if (this.minecartToSpawn != null && this.minecartToSpawn.size() > 0) {
            this.setRandomEntity((MobSpawnerBaseLogic.WeightedRandomMinecart)WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.minecartToSpawn));
        }

        this.addBlockEvent(1);
    }

    public void readFromNBT(NBTTagCompound nbttagCompound) {
        this.mobID = nbttagCompound.getString("EntityId");
        this.spawnDelay = nbttagCompound.getShort("Delay");
        if (nbttagCompound.hasKey("SpawnPotentials", 9)) {
            this.minecartToSpawn = new ArrayList();
            NBTTagList tagList = nbttagCompound.getTagList("SpawnPotentials", 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
                this.minecartToSpawn.add(new MobSpawnerBaseLogic.WeightedRandomMinecart(tagList.getCompoundTagAt(i)));
            }
        } else {
            this.minecartToSpawn = null;
        }

        if (nbttagCompound.hasKey("SpawnData", 10)) {
            this.setRandomEntity(new MobSpawnerBaseLogic.WeightedRandomMinecart(nbttagCompound.getCompoundTag("SpawnData"), this.mobID));
        } else {
            this.setRandomEntity(null);
        }

        if (nbttagCompound.hasKey("MinSpawnDelay", 99)) {
            this.minSpawnDelay = nbttagCompound.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbttagCompound.getShort("MaxSpawnDelay");
            this.spawnCount = nbttagCompound.getShort("SpawnCount");
        }

        if (nbttagCompound.hasKey("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = nbttagCompound.getShort("MaxNearbyEntities");
            this.activatingRangeFromPlayer = nbttagCompound.getShort("RequiredPlayerRange");
        }

        if (nbttagCompound.hasKey("SpawnRange", 99)) {
            this.spawnRange = nbttagCompound.getShort("SpawnRange");
        }

        if (this.getSpawnerWorld() != null && this.getSpawnerWorld().isRemote) {
            this.cachedEntity = null;
        }
    }

    public void writeToNBT(NBTTagCompound nbttagCompound) {
        nbttagCompound.setString("EntityId", this.getEntityNameToSpawn());
        nbttagCompound.setShort("Delay", (short)this.spawnDelay);
        nbttagCompound.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
        nbttagCompound.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        nbttagCompound.setShort("SpawnCount", (short)this.spawnCount);
        nbttagCompound.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        nbttagCompound.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
        nbttagCompound.setShort("SpawnRange", (short)this.spawnRange);
        if (this.getRandomEntity() != null) {
            nbttagCompound.setTag("SpawnData", this.getRandomEntity().nbtTagCompound.copy());
        }

        if (this.getRandomEntity() != null || this.minecartToSpawn != null && this.minecartToSpawn.size() > 0) {
            NBTTagList nbttagList = new NBTTagList();
            if (this.minecartToSpawn != null && this.minecartToSpawn.size() > 0) {
                for (MobSpawnerBaseLogic.WeightedRandomMinecart weightedRandomMinecart : (Iterable<MobSpawnerBaseLogic.WeightedRandomMinecart>)this.minecartToSpawn) {
                    nbttagList.appendTag(weightedRandomMinecart.toCompoundTag());
                }
            } else {
                nbttagList.appendTag(this.getRandomEntity().toCompoundTag());
            }

            nbttagCompound.setTag("SpawnPotentials", nbttagList);
        }
    }

    public Entity getEntityToRender() {
        if (this.cachedEntity == null) {
            Entity createEntityByName = EntityList.createEntityByName(this.getEntityNameToSpawn(), null);
            createEntityByName = this.spawnEntity(createEntityByName);
            this.cachedEntity = createEntityByName;
        }

        return this.cachedEntity;
    }

    public boolean setDelayToMin(int n) {
        if (n == 1 && this.getSpawnerWorld().isRemote) {
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

    public abstract int getSpawnerX();

    public abstract int getSpawnerY();

    public abstract int getSpawnerZ();

    public class WeightedRandomMinecart extends Item {
        public NBTTagCompound nbtTagCompound;
        public String entityType;

        public WeightedRandomMinecart(NBTTagCompound nbttagCompound) {
            super(nbttagCompound.getInteger("Weight"));
            NBTTagCompound compoundTag = nbttagCompound.getCompoundTag("Properties");
            String string = nbttagCompound.getString("Type");
            if (string.equals("Minecart")) {
                if (compoundTag != null) {
                    switch (compoundTag.getInteger("Type")) {
                        case 0:
                            string = "MinecartRideable";
                            break;
                        case 1:
                            string = "MinecartChest";
                            break;
                        case 2:
                            string = "MinecartFurnace";
                    }
                } else {
                    string = "MinecartRideable";
                }
            }

            this.nbtTagCompound = compoundTag;
            this.entityType = string;
        }

        public WeightedRandomMinecart(NBTTagCompound nbttagCompound, String str) {
            super(1);
            if (str.equals("Minecart")) {
                if (nbttagCompound != null) {
                    switch (nbttagCompound.getInteger("Type")) {
                        case 0:
                            str = "MinecartRideable";
                            break;
                        case 1:
                            str = "MinecartChest";
                            break;
                        case 2:
                            str = "MinecartFurnace";
                    }
                } else {
                    str = "MinecartRideable";
                }
            }

            this.nbtTagCompound = nbttagCompound;
            this.entityType = str;
        }

        public NBTTagCompound toCompoundTag() {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setTag("Properties", this.nbtTagCompound);
            nbttagCompound.setString("Type", this.entityType);
            nbttagCompound.setInteger("Weight", this.itemWeight);
            return nbttagCompound;
        }
    }
}
