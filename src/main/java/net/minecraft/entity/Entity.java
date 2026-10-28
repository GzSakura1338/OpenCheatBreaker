package net.minecraft.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityDoubleEvent;
import com.cheatbreaker.client.mod.impl.ParticlesMod;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class Entity {
    public static int nextEntityID;
    public int entityId;
    public double renderDistanceWeight;
    public boolean preventEntitySpawning;
    public Entity riddenByEntity;
    public Entity ridingEntity;
    public boolean forceSpawn;
    public AxisAlignedBB boundingBox;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;
    public double posX;
    public double posY;
    public double posZ;
    public double motionX;
    public double motionY;
    public double motionZ;
    public float rotationYaw;
    public float rotationPitch;
    public float prevRotationYaw;
    public float prevRotationPitch;
    public boolean flag;
    public boolean onGround;
    public boolean isCollidedHorizontally;
    public boolean isCollidedVertically;
    public boolean isCollided;
    public boolean velocityChanged;
    public boolean isInWeb;
    public float width;
    public boolean isDead;
    public float yOffset;
    public float height;
    public int nextStepDistance;
    public float prevDistanceWalkedModified;
    public float distanceWalkedModified;
    public float distanceWalkedOnStepModified;
    public float fallDistance;
    public Random rand;
    public double lastTickPosX;
    public double lastTickPosY;
    public double lastTickPosZ;
    public float yOffset2;
    public float stepHeight;
    public boolean noClip;
    public float entityCollisionReduction;
    public int fireResistance;
    public int ticksExisted;
    public boolean firstUpdate;
    public int fire;
    public boolean inWater;
    public int hurtResistantTime;
    public UUID entityUniqueID;
    public boolean isImmuneToFire;
    public Entity.EnumEntitySize myEntitySize;
    public double entityRiderPitchDelta;
    public double entityRiderYawDelta;
    public boolean addedToChunk;
    public int chunkCoordX;
    public int chunkCoordY;
    public int chunkCoordZ;
    public int serverPosX;
    public int serverPosY;
    public int serverPosZ;
    public boolean ignoreFrustumCheck;
    public boolean isAirBorne;
    public int timeUntilPortal;
    public boolean inPortal;
    public int portalCounter;
    public World worldObj;
    public int teleportDirection;
    public boolean invulnerable;
    public int dimension;
    public DataWatcher dataWatcher;

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int id) {
        this.entityId = id;
    }

    public Entity(World worldIn) {
        this.entityId = nextEntityID++;
        this.renderDistanceWeight = 1.0;
        this.boundingBox = AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        this.flag = true;
        this.width = 0.6F;
        this.height = 1.8F;
        this.nextStepDistance = 1;
        this.rand = new Random();
        this.fireResistance = 1;
        this.firstUpdate = true;
        this.entityUniqueID = UUID.randomUUID();
        this.myEntitySize = Entity.EnumEntitySize.SIZE_2;
        this.worldObj = worldIn;
        this.setPosition(0.0, 0.0, 0.0);
        if (worldIn != null) {
            this.dimension = worldIn.provider.dimensionId;
        }

        this.dataWatcher = new DataWatcher(this);
        this.dataWatcher.addObject(0, (byte)0);
        this.dataWatcher.addObject(1, (short)300);
        this.entityInit();
    }

    public abstract void entityInit();

    public DataWatcher getDataWatcher() {
        return this.dataWatcher;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Entity && ((Entity)obj).entityId == this.entityId;
    }

    @Override
    public int hashCode() {
        return this.entityId;
    }

    public void preparePlayerToSpawn() {
        if (this.worldObj != null) {
            while (true) {
                if (this.posY > 0.0) {
                    this.setPosition(this.posX, this.posY, this.posZ);
                    if (!this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) {
                        this.posY++;
                        continue;
                    }
                }

                this.motionX = this.motionY = this.motionZ = 0.0;
                this.rotationPitch = 0.0F;
                break;
            }
        }
    }

    public void setDead() {
        this.isDead = true;
    }

    public void setSize(float width, float height) {
        if (width != this.width || height != this.height) {
            float width2 = this.width;
            this.width = width;
            this.height = height;
            this.boundingBox.maxX = this.boundingBox.minX + this.width;
            this.boundingBox.maxZ = this.boundingBox.minZ + this.width;
            this.boundingBox.maxY = this.boundingBox.minY + this.height;
            if (this.width > width2 && !this.firstUpdate && !this.worldObj.isRemote) {
                this.moveEntity(width2 - this.width, 0.0, width2 - this.width);
            }
        }

        float f = width % 2.0F;
        if (f < 0.375) {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_1;
        } else if (f < 0.75) {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_2;
        } else if (f < 1.0) {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_3;
        } else if (f < 1.375) {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_4;
        } else if (f < 1.75) {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_5;
        } else {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_6;
        }
    }

    public void setRotation(float yaw, float pitch) {
        this.rotationYaw = yaw % 360.0F;
        this.rotationPitch = pitch % 360.0F;
    }

    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float f = this.width / 2.0F;
        float height = this.height;
        this.boundingBox.setBounds(x - f, y - this.yOffset + this.yOffset2, z - f, x + f, y - this.yOffset + this.yOffset2 + height, z + f);
    }

    public void setAngles(float yaw, float pitch) {
        float rotationPitch = this.rotationPitch;
        float rotationYaw = this.rotationYaw;
        this.rotationYaw = (float)(this.rotationYaw + yaw * 0.15);
        this.rotationPitch = (float)(this.rotationPitch - pitch * 0.15);
        if (this.rotationPitch < -90.0F) {
            this.rotationPitch = -90.0F;
        }

        if (this.rotationPitch > 90.0F) {
            this.rotationPitch = 90.0F;
        }

        this.prevRotationPitch = this.prevRotationPitch + (this.rotationPitch - rotationPitch);
        this.prevRotationYaw = this.prevRotationYaw + (this.rotationYaw - rotationYaw);
    }

    public void onUpdate() throws java.io.IOException {
        this.onEntityUpdate();
    }

    public void onEntityUpdate() throws java.io.IOException {
        this.worldObj.theProfiler.startSection("entityBaseTick");
        if (this.ridingEntity != null && this.ridingEntity.isDead) {
            this.ridingEntity = null;
        }

        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
            this.worldObj.theProfiler.startSection("portal");
            MinecraftServer server = ((WorldServer)this.worldObj).getServer();
            int maxInPortalTime = this.getMaxInPortalTime();
            if (this.inPortal) {
                if (server.getAllowNether()) {
                    if (this.ridingEntity == null && this.portalCounter++ >= maxInPortalTime) {
                        this.portalCounter = maxInPortalTime;
                        this.timeUntilPortal = this.getPortalCooldown();
                        byte b;
                        if (this.worldObj.provider.dimensionId == -1) {
                            b = 0;
                        } else {
                            b = -1;
                        }

                        this.travelToDimension(b);
                    }

                    this.inPortal = false;
                }
            } else {
                if (this.portalCounter > 0) {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0) {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0) {
                this.timeUntilPortal--;
            }

            this.worldObj.theProfiler.endSection();
        }

        if (this.isSprinting() && !this.isInWater()) {
            int n = MathHelper.floor_double(this.posX);
            int n2 = MathHelper.floor_double(this.posY - 0.2F - this.yOffset);
            int n3 = MathHelper.floor_double(this.posZ);
            Block block = this.worldObj.getBlock(n, n2, n3);
            ParticlesMod particlesMod = CheatBreaker.getInstance().getModManager().particlesMod;
            if ((!particlesMod.isEnabled() || particlesMod.showSprintingParticles.getBooleanValue()) && block.getMaterial() != Material.air) {
                this.worldObj
                    .spawnParticle(
                        "blockcrack_" + Block.getIdFromBlock(block) + "_" + this.worldObj.getBlockMetadata(n, n2, n3),
                        this.posX + (this.rand.nextFloat() - 0.5) * this.width,
                        this.boundingBox.minY + 0.1,
                        this.posZ + (this.rand.nextFloat() - 0.5) * this.width,
                        -this.motionX * 4.0,
                        1.5,
                        -this.motionZ * 4.0
                    );
            }
        }

        this.handleWaterMovement();
        if (this.worldObj.isRemote) {
            this.fire = 0;
        } else if (this.fire > 0) {
            if (this.isImmuneToFire) {
                this.fire -= 4;
                if (this.fire < 0) {
                    this.fire = 0;
                }
            } else {
                if (this.fire % 20 == 0) {
                    this.attackEntityFrom(DamageSource.onFire, 1.0F);
                }

                this.fire--;
            }
        }

        if (this.handleLavaMovement()) {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.posY < -64.0) {
            this.kill();
        }

        if (!this.worldObj.isRemote) {
            this.setFlag(0, this.fire > 0);
        }

        this.firstUpdate = false;
        this.worldObj.theProfiler.endSection();
    }

    public int getMaxInPortalTime() {
        return 0;
    }

    public void setOnFireFromLava() {
        if (!this.isImmuneToFire) {
            this.attackEntityFrom(DamageSource.lava, 4.0F);
            this.setFire(15);
        }
    }

    public void setFire(int seconds) {
        int n = seconds * 20;
        n = EnchantmentProtection.getFireTimeForEntity(this, n);
        if (this.fire < n) {
            this.fire = n;
        }
    }

    public void extinguish() {
        this.fire = 0;
    }

    public void kill() {
        this.setDead();
    }

    public boolean isOffsetPositionInLiquid(double x, double y, double z) {
        AxisAlignedBB offsetBoundingBox = this.boundingBox.getOffsetBoundingBox(x, y, z);
        List collidingBoundingBoxes = this.worldObj.getCollidingBoundingBoxes(this, offsetBoundingBox);
        return collidingBoundingBoxes.isEmpty() && !this.worldObj.isAnyLiquid(offsetBoundingBox);
    }

    public void moveEntity(double x, double y, double z) {
        if (this.noClip) {
            this.boundingBox.offset(x, y, z);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
            this.posY = this.boundingBox.minY + this.yOffset - this.yOffset2;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
        } else {
            this.worldObj.theProfiler.startSection("move");
            this.yOffset2 *= 0.4F;
            double posX = this.posX;
            double posY = this.posY;
            double posZ = this.posZ;
            if (this.isInWeb) {
                this.isInWeb = false;
                x *= 0.25;
                y *= 0.05F;
                z *= 0.25;
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
            }

            double d = x;
            double d2 = y;
            double d3 = z;
            AxisAlignedBB axisAlignedBB = this.boundingBox.copy();
            boolean flag = this.onGround && this.isSneaking() && this instanceof EntityPlayer;
            if (flag) {
                double d4 = 0.05;

                while (x != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(x, -1.0, 0.0)).isEmpty()) {
                    if (x < d4 && x >= -d4) {
                        x = 0.0;
                    } else if (x > 0.0) {
                        x -= d4;
                    } else {
                        x += d4;
                    }

                    d = x;
                }

                for (; z != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0, -1.0, z)).isEmpty(); d3 = z) {
                    if (z < d4 && z >= -d4) {
                        z = 0.0;
                    } else if (z > 0.0) {
                        z -= d4;
                    } else {
                        z += d4;
                    }
                }

                while (x != 0.0 && z != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(x, -1.0, z)).isEmpty()) {
                    if (x < d4 && x >= -d4) {
                        x = 0.0;
                    } else if (x > 0.0) {
                        x -= d4;
                    } else {
                        x += d4;
                    }

                    if (z < d4 && z >= -d4) {
                        z = 0.0;
                    } else if (z > 0.0) {
                        z -= d4;
                    } else {
                        z += d4;
                    }

                    d = x;
                    d3 = z;
                }
            }

            List collidingBoundingBoxes = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(x, y, z));

            for (int i = 0; i < collidingBoundingBoxes.size(); i++) {
                y = ((AxisAlignedBB)collidingBoundingBoxes.get(i)).calculateYOffset(this.boundingBox, y);
            }

            this.boundingBox.offset(0.0, y, 0.0);
            if (!this.flag && d2 != y) {
                z = 0.0;
                y = 0.0;
                x = 0.0;
            }

            boolean flag2 = this.onGround || d2 != y && d2 < 0.0;

            for (int i = 0; i < collidingBoundingBoxes.size(); i++) {
                x = ((AxisAlignedBB)collidingBoundingBoxes.get(i)).calculateXOffset(this.boundingBox, x);
            }

            this.boundingBox.offset(x, 0.0, 0.0);
            if (!this.flag && d != x) {
                z = 0.0;
                y = 0.0;
                x = 0.0;
            }

            for (int i = 0; i < collidingBoundingBoxes.size(); i++) {
                z = ((AxisAlignedBB)collidingBoundingBoxes.get(i)).calculateZOffset(this.boundingBox, z);
            }

            this.boundingBox.offset(0.0, 0.0, z);
            if (!this.flag && d3 != z) {
                z = 0.0;
                y = 0.0;
                x = 0.0;
            }

            if (this.stepHeight > 0.0F && flag2 && (flag || this.yOffset2 < 0.05F) && (d != x || d3 != z)) {
                double d4 = x;
                double d5 = y;
                double d6 = z;
                x = d;
                y = this.stepHeight;
                z = d3;
                AxisAlignedBB axisAlignedBB2 = this.boundingBox.copy();
                this.boundingBox.setBB(axisAlignedBB);
                collidingBoundingBoxes = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(d, y, d3));

                for (int i = 0; i < collidingBoundingBoxes.size(); i++) {
                    y = ((AxisAlignedBB)collidingBoundingBoxes.get(i)).calculateYOffset(this.boundingBox, y);
                }

                this.boundingBox.offset(0.0, y, 0.0);
                if (!this.flag && d2 != y) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                for (int i = 0; i < collidingBoundingBoxes.size(); i++) {
                    x = ((AxisAlignedBB)collidingBoundingBoxes.get(i)).calculateXOffset(this.boundingBox, x);
                }

                this.boundingBox.offset(x, 0.0, 0.0);
                if (!this.flag && d != x) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                for (int i = 0; i < collidingBoundingBoxes.size(); i++) {
                    z = ((AxisAlignedBB)collidingBoundingBoxes.get(i)).calculateZOffset(this.boundingBox, z);
                }

                this.boundingBox.offset(0.0, 0.0, z);
                if (!this.flag && d3 != z) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                if (!this.flag && d2 != y) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                } else {
                    y = -this.stepHeight;

                    for (int i = 0; i < collidingBoundingBoxes.size(); i++) {
                        y = ((AxisAlignedBB)collidingBoundingBoxes.get(i)).calculateYOffset(this.boundingBox, y);
                    }

                    this.boundingBox.offset(0.0, y, 0.0);
                }

                if (d4 * d4 + d6 * d6 >= x * x + z * z) {
                    x = d4;
                    y = d5;
                    z = d6;
                    this.boundingBox.setBB(axisAlignedBB2);
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
            this.posY = this.boundingBox.minY + this.yOffset - this.yOffset2;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
            this.isCollidedHorizontally = d != x || d3 != z;
            this.isCollidedVertically = d2 != y;
            this.onGround = d2 != y && d2 < 0.0;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            this.updateFallState(y, this.onGround);
            if (d != x) {
                this.motionX = 0.0;
            }

            if (d2 != y) {
                this.motionY = 0.0;
            }

            if (d3 != z) {
                this.motionZ = 0.0;
            }

            double d4 = this.posX - posX;
            double d5 = this.posY - posY;
            double d6 = this.posZ - posZ;
            if (this.canTriggerWalking() && !flag && this.ridingEntity == null) {
                int n = MathHelper.floor_double(this.posX);
                int n2 = MathHelper.floor_double(this.posY - 0.2F - this.yOffset);
                int n3 = MathHelper.floor_double(this.posZ);
                Block block = this.worldObj.getBlock(n, n2, n3);
                int renderType = this.worldObj.getBlock(n, n2 - 1, n3).getRenderType();
                if (renderType == 11 || renderType == 32 || renderType == 21) {
                    block = this.worldObj.getBlock(n, n2 - 1, n3);
                }

                if (block != Blocks.ladder) {
                    d5 = 0.0;
                }

                this.distanceWalkedModified = (float)(this.distanceWalkedModified + MathHelper.sqrt_double(d4 * d4 + d6 * d6) * 0.6);
                this.distanceWalkedOnStepModified = (float)(
                    this.distanceWalkedOnStepModified + MathHelper.sqrt_double(d4 * d4 + d5 * d5 + d6 * d6) * 0.6
                );
                if (this.distanceWalkedOnStepModified > this.nextStepDistance && block.getMaterial() != Material.air) {
                    this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
                    if (this.isInWater()) {
                        float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F)
                            * 0.35F;
                        if (f > 1.0F) {
                            f = 1.0F;
                        }

                        this.playSound(this.getSwimSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                    }

                    this.playStepSound(n, n2, n3, block);
                    block.onEntityWalking(this.worldObj, n, n2, n3, this);
                }
            }

            try {
                this.doBlockCollisions();
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Checking entity block collision");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(makeCategory);
                throw new ReportedException(makeCrashReport);
            }

            boolean isWet = this.isWet();
            if (this.worldObj.containsFireSource(this.boundingBox.contract(0.001, 0.001, 0.001))) {
                this.dealFireDamage(1);
                if (!isWet) {
                    this.fire++;
                    if (this.fire == 0) {
                        this.setFire(8);
                    }
                }
            } else if (this.fire <= 0) {
                this.fire = -this.fireResistance;
            }

            if (isWet && this.fire > 0) {
                this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.fire = -this.fireResistance;
            }

            this.worldObj.theProfiler.endSection();
        }
    }

    public String getSwimSound() {
        return "game.neutral.swim";
    }

    public void doBlockCollisions() {
        int n = MathHelper.floor_double(this.boundingBox.minX + 0.001);
        int n2 = MathHelper.floor_double(this.boundingBox.minY + 0.001);
        int n3 = MathHelper.floor_double(this.boundingBox.minZ + 0.001);
        int n4 = MathHelper.floor_double(this.boundingBox.maxX - 0.001);
        int n5 = MathHelper.floor_double(this.boundingBox.maxY - 0.001);
        int n6 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001);
        if (this.worldObj.checkChunksExist(n, n2, n3, n4, n5, n6)) {
            for (int i = n; i <= n4; i++) {
                for (int j = n2; j <= n5; j++) {
                    for (int k = n3; k <= n6; k++) {
                        Block block = this.worldObj.getBlock(i, j, k);

                        try {
                            block.onEntityCollidedWithBlock(this.worldObj, i, j, k, this);
                        } catch (Throwable e) {
                            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Colliding entity with block");
                            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block being collided with");
                            CrashReportCategory.addBlockInfo(makeCategory, i, j, k, block, this.worldObj.getBlockMetadata(i, j, k));
                            throw new ReportedException(makeCrashReport);
                        }
                    }
                }
            }
        }
    }

    public void playStepSound(int x, int y, int z, Block blockIn) {
        SoundType stepSound = blockIn.stepSound;
        if (this.worldObj.getBlock(x, y + 1, z) == Blocks.snow_layer) {
            stepSound = Blocks.snow_layer.stepSound;
            this.playSound(stepSound.getStepSound(), stepSound.getVolume() * 0.15F, stepSound.getFrequency());
        } else if (!blockIn.getMaterial().isLiquid()) {
            this.playSound(stepSound.getStepSound(), stepSound.getVolume() * 0.15F, stepSound.getFrequency());
        }
    }

    public void playSound(String name, float volume, float pitch) {
        this.worldObj.playSoundAtEntity(this, name, volume, pitch);
    }

    public boolean canTriggerWalking() {
        return true;
    }

    public void updateFallState(double distanceFallenThisTick, boolean isOnGround) {
        if (isOnGround) {
            if (this.fallDistance > 0.0F) {
                this.fall(this.fallDistance);
                this.fallDistance = 0.0F;
            }
        } else if (distanceFallenThisTick < 0.0) {
            this.fallDistance = (float)(this.fallDistance - distanceFallenThisTick);
        }
    }

    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    public void dealFireDamage(int amount) {
        if (!this.isImmuneToFire) {
            this.attackEntityFrom(DamageSource.inFire, amount);
        }
    }

    public boolean isImmuneToFire() {
        return this.isImmuneToFire;
    }

    public void fall(float distance) {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.fall(distance);
        }
    }

    public boolean isWet() {
        return this.inWater
            || this.worldObj.isRainingAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
            || this.worldObj.isRainingAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + this.height), MathHelper.floor_double(this.posZ));
    }

    public boolean isInWater() {
        return this.inWater;
    }

    public boolean handleWaterMovement() {
        if (this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0, -0.4F, 0.0).contract(0.001, 0.001, 0.001), Material.water, this)) {
            if (!this.inWater && !this.firstUpdate) {
                float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F) * 0.2F;
                if (f > 1.0F) {
                    f = 1.0F;
                }

                this.playSound(this.getSplashSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                float f2 = MathHelper.floor_double(this.boundingBox.minY);

                for (int i = 0; i < 1.0F + this.width * 20.0F; i++) {
                    float f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj
                        .spawnParticle(
                            "bubble", this.posX + f3, f2 + 1.0F, this.posZ + f4, this.motionX, this.motionY - this.rand.nextFloat() * 0.2F, this.motionZ
                        );
                }

                for (int i = 0; i < 1.0F + this.width * 20.0F; i++) {
                    float f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("splash", this.posX + f3, f2 + 1.0F, this.posZ + f4, this.motionX, this.motionY, this.motionZ);
                }
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fire = 0;
        } else {
            this.inWater = false;
        }

        return this.inWater;
    }

    public String getSplashSound() {
        return "game.neutral.swim.splash";
    }

    public boolean isInsideOfMaterial(Material materialIn) {
        double d = this.posY + this.getEyeHeight();
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_float(MathHelper.floor_double(d));
        int n3 = MathHelper.floor_double(this.posZ);
        Block block = this.worldObj.getBlock(n, n2, n3);
        if (block.getMaterial() == materialIn) {
            float f = BlockLiquid.getLiquidHeightPercent(this.worldObj.getBlockMetadata(n, n2, n3)) - 0.11111111F;
            float f2 = n2 + 1 - f;
            return d < f2;
        } else {
            return false;
        }
    }

    public float getEyeHeight() {
        return 0.0F;
    }

    public boolean handleLavaMovement() {
        return this.worldObj.isMaterialInBB(this.boundingBox.expand(-0.1F, -0.4F, -0.1F), Material.lava);
    }

    public void moveFlying(float strafe, float forward, float friction) {
        float f = strafe * strafe + forward * forward;
        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);
            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe *= f;
            forward *= f;
            float sin = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
            float cos = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
            this.motionX += strafe * cos - forward * sin;
            this.motionZ += forward * cos + strafe * sin;
        }
    }

    public int getBrightnessForRender(float f) {
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.posZ);
        if (this.worldObj.blockExists(n, 0, n2)) {
            double d = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66;
            int n3 = MathHelper.floor_double(this.posY - this.yOffset + d);
            return this.worldObj.getLightBrightnessForSkyBlocks(n, n3, n2, 0);
        } else {
            return 0;
        }
    }

    public float getBrightness(float f) {
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.posZ);
        if (this.worldObj.blockExists(n, 0, n2)) {
            double d = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66;
            int n3 = MathHelper.floor_double(this.posY - this.yOffset + d);
            return this.worldObj.getLightBrightness(n, n3, n2);
        } else {
            return 0.0F;
        }
    }

    public void setWorld(World worldIn) {
        this.worldObj = worldIn;
    }

    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        this.prevPosX = this.posX = x;
        this.prevPosY = this.posY = y;
        this.prevPosZ = this.posZ = z;
        this.prevRotationYaw = this.rotationYaw = yaw;
        this.prevRotationPitch = this.rotationPitch = pitch;
        this.yOffset2 = 0.0F;
        double d = this.prevRotationYaw - yaw;
        if (d < -180.0) {
            this.prevRotationYaw += 360.0F;
        }

        if (d >= 180.0) {
            this.prevRotationYaw -= 360.0F;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(yaw, pitch);
    }

    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.lastTickPosX = this.prevPosX = this.posX = x;
        this.lastTickPosY = this.prevPosY = this.posY = y + this.yOffset;
        this.lastTickPosZ = this.prevPosZ = this.posZ = z;
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public float getDistanceToEntity(Entity entityIn) {
        float f = (float)(this.posX - entityIn.posX);
        float f2 = (float)(this.posY - entityIn.posY);
        float f3 = (float)(this.posZ - entityIn.posZ);
        return MathHelper.sqrt_float(f * f + f2 * f2 + f3 * f3);
    }

    public double getDistanceSq(double x, double y, double z) {
        double d = this.posX - x;
        double d2 = this.posY - y;
        double d3 = this.posZ - z;
        return d * d + d2 * d2 + d3 * d3;
    }

    public double getDistance(double x, double y, double z) {
        double d = this.posX - x;
        double d2 = this.posY - y;
        double d3 = this.posZ - z;
        return MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
    }

    public double getDistanceSqToEntity(Entity entityIn) {
        double d = this.posX - entityIn.posX;
        double d2 = this.posY - entityIn.posY;
        double d3 = this.posZ - entityIn.posZ;
        return d * d + d2 * d2 + d3 * d3;
    }

    public void onCollideWithPlayer(EntityPlayer entityIn) {
    }

    public void applyEntityCollision(Entity entityIn) {
        if (entityIn.riddenByEntity != this && entityIn.ridingEntity != this) {
            double d = entityIn.posX - this.posX;
            double d2 = entityIn.posZ - this.posZ;
            double d3 = MathHelper.abs_max(d, d2);
            if (d3 >= 0.01F) {
                d3 = MathHelper.sqrt_double(d3);
                d /= d3;
                d2 /= d3;
                double d4 = 1.0 / d3;
                if (d4 > 1.0) {
                    d4 = 1.0;
                }

                d *= d4;
                d2 *= d4;
                d *= 0.05F;
                d2 *= 0.05F;
                d *= 1.0F - this.entityCollisionReduction;
                d2 *= 1.0F - this.entityCollisionReduction;
                this.addVelocity(-d, 0.0, -d2);
                entityIn.addVelocity(d, 0.0, d2);
            }
        }
    }

    public void addVelocity(double x, double y, double z) {
        this.motionX += x;
        this.motionY += y;
        this.motionZ += z;
        this.isAirBorne = true;
    }

    public void setBeenAttacked() {
        this.velocityChanged = true;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        this.setBeenAttacked();
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean canBePushed() {
        return false;
    }

    public void addToPlayerScore(Entity entityIn, int amount) {
    }

    public boolean isInRangeToRender3d(double x, double y, double z) {
        double d = this.posX - x;
        double d2 = this.posY - y;
        double d3 = this.posZ - z;
        double d4 = d * d + d2 * d2 + d3 * d3;
        return this.isInRangeToRenderDist(d4);
    }

    public boolean isInRangeToRenderDist(double distance) {
        double averageEdgeLength = this.boundingBox.getAverageEdgeLength();
        averageEdgeLength *= 64.0 * this.renderDistanceWeight;
        return distance < averageEdgeLength * averageEdgeLength;
    }

    public boolean writeMountToNBT(NBTTagCompound tagCompund) {
        String entityString = this.getEntityString();
        if (!this.isDead && entityString != null) {
            tagCompund.setString("id", entityString);
            this.writeToNBT(tagCompund);
            return true;
        } else {
            return false;
        }
    }

    public boolean writeToNBTOptional(NBTTagCompound tagCompund) {
        String entityString = this.getEntityString();
        if (!this.isDead && entityString != null && this.riddenByEntity == null) {
            tagCompund.setString("id", entityString);
            this.writeToNBT(tagCompund);
            return true;
        } else {
            return false;
        }
    }

    public void writeToNBT(NBTTagCompound tagCompund) {
        try {
            tagCompund.setTag("Pos", this.newDoubleNBTList(this.posX, this.posY + this.yOffset2, this.posZ));
            tagCompund.setTag("Motion", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
            tagCompund.setTag("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
            tagCompund.setFloat("FallDistance", this.fallDistance);
            tagCompund.setShort("Fire", (short)this.fire);
            tagCompund.setShort("Air", (short)this.getAir());
            tagCompund.setBoolean("OnGround", this.onGround);
            tagCompund.setInteger("Dimension", this.dimension);
            tagCompund.setBoolean("Invulnerable", this.invulnerable);
            tagCompund.setInteger("PortalCooldown", this.timeUntilPortal);
            tagCompund.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
            tagCompund.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
            this.writeEntityToNBT(tagCompund);
            if (this.ridingEntity != null) {
                NBTTagCompound nbttagCompound = new NBTTagCompound();
                if (this.ridingEntity.writeMountToNBT(nbttagCompound)) {
                    tagCompund.setTag("Riding", nbttagCompound);
                }
            }
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Saving entity NBT");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Entity being saved");
            this.addEntityCrashInfo(makeCategory);
            throw new ReportedException(makeCrashReport);
        }
    }

    public void readFromNBT(NBTTagCompound tagCompund) {
        try {
            NBTTagList tagList = tagCompund.getTagList("Pos", 6);
            NBTTagList tagList2 = tagCompund.getTagList("Motion", 6);
            NBTTagList tagList3 = tagCompund.getTagList("Rotation", 5);
            this.motionX = tagList2.getDoubleAt(0);
            this.motionY = tagList2.getDoubleAt(1);
            this.motionZ = tagList2.getDoubleAt(2);
            if (Math.abs(this.motionX) > 10.0) {
                this.motionX = 0.0;
            }

            if (Math.abs(this.motionY) > 10.0) {
                this.motionY = 0.0;
            }

            if (Math.abs(this.motionZ) > 10.0) {
                this.motionZ = 0.0;
            }

            this.prevPosX = this.lastTickPosX = this.posX = tagList.getDoubleAt(0);
            this.prevPosY = this.lastTickPosY = this.posY = tagList.getDoubleAt(1);
            this.prevPosZ = this.lastTickPosZ = this.posZ = tagList.getDoubleAt(2);
            this.prevRotationYaw = this.rotationYaw = tagList3.getFloatAt(0);
            this.prevRotationPitch = this.rotationPitch = tagList3.getFloatAt(1);
            this.fallDistance = tagCompund.getFloat("FallDistance");
            this.fire = tagCompund.getShort("Fire");
            this.setAir(tagCompund.getShort("Air"));
            this.onGround = tagCompund.getBoolean("OnGround");
            this.dimension = tagCompund.getInteger("Dimension");
            this.invulnerable = tagCompund.getBoolean("Invulnerable");
            this.timeUntilPortal = tagCompund.getInteger("PortalCooldown");
            if (tagCompund.hasKey("UUIDMost", 4) && tagCompund.hasKey("UUIDLeast", 4)) {
                this.entityUniqueID = new UUID(tagCompund.getLong("UUIDMost"), tagCompund.getLong("UUIDLeast"));
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.readEntityFromNBT(tagCompund);
            if (this.shouldSetPosAfterLoading()) {
                this.setPosition(this.posX, this.posY, this.posZ);
            }
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Loading entity NBT");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Entity being loaded");
            this.addEntityCrashInfo(makeCategory);
            throw new ReportedException(makeCrashReport);
        }
    }

    public boolean shouldSetPosAfterLoading() {
        return true;
    }

    public String getEntityString() {
        return EntityList.getEntityString(this);
    }

    public abstract void readEntityFromNBT(NBTTagCompound nbttagCompound);

    public abstract void writeEntityToNBT(NBTTagCompound nbttagCompound);

    public void onChunkLoad() {
    }

    public NBTTagList newDoubleNBTList(double... numbers) {
        NBTTagList nbttagList = new NBTTagList();
        double[] numbers2 = numbers;
        int length = numbers.length;

        for (int i = 0; i < length; i++) {
            double d = numbers2[i];
            nbttagList.appendTag(new NBTTagDouble(d));
        }

        return nbttagList;
    }

    public NBTTagList newFloatNBTList(float... numbers) {
        NBTTagList nbttagList = new NBTTagList();
        float[] numbers2 = numbers;
        int length = numbers.length;

        for (int i = 0; i < length; i++) {
            float f = numbers2[i];
            nbttagList.appendTag(new NBTTagFloat(f));
        }

        return nbttagList;
    }

    public float getShadowSize() {
        return this.height / 2.0F;
    }

    public EntityItem dropItem(Item itemIn, int size) {
        return this.dropItemWithOffset(itemIn, size, 0.0F);
    }

    public EntityItem dropItemWithOffset(Item itemIn, int size, float f) {
        return this.entityDropItem(new ItemStack(itemIn, size, 0), f);
    }

    public EntityItem entityDropItem(ItemStack itemStackIn, float offsetY) {
        if (itemStackIn.stackSize != 0 && itemStackIn.getItem() != null) {
            EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY + offsetY, this.posZ, itemStackIn);
            entityItem.delayBeforeCanPickup = 10;
            this.worldObj.spawnEntityInWorld(entityItem);
            return entityItem;
        } else {
            return null;
        }
    }

    public boolean isEntityAlive() {
        return !this.isDead;
    }

    public boolean isEntityInsideOpaqueBlock() {
        for (int i = 0; i < 8; i++) {
            float f = ((i >> 0) % 2 - 0.5F) * this.width * 0.8F;
            float f2 = ((i >> 1) % 2 - 0.5F) * 0.1F;
            float f3 = ((i >> 2) % 2 - 0.5F) * this.width * 0.8F;
            int n = MathHelper.floor_double(this.posX + f);
            int n2 = MathHelper.floor_double(this.posY + this.getEyeHeight() + f2);
            int n3 = MathHelper.floor_double(this.posZ + f3);
            if (this.worldObj.getBlock(n, n2, n3).isNormalCube()) {
                return true;
            }
        }

        return false;
    }

    public boolean interactFirst(EntityPlayer player) throws java.io.IOException {
        return false;
    }

    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    public void updateRidden() throws java.io.IOException {
        if (this.ridingEntity.isDead) {
            this.ridingEntity = null;
        } else {
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            this.onUpdate();
            if (this.ridingEntity != null) {
                this.ridingEntity.updateRiderPosition();
                this.entityRiderYawDelta = this.entityRiderYawDelta + (this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);
                this.entityRiderPitchDelta = this.entityRiderPitchDelta + (this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch);

                while (this.entityRiderYawDelta >= 180.0) {
                    this.entityRiderYawDelta -= 360.0;
                }

                while (this.entityRiderYawDelta < -180.0) {
                    this.entityRiderYawDelta += 360.0;
                }

                while (this.entityRiderPitchDelta >= 180.0) {
                    this.entityRiderPitchDelta -= 360.0;
                }

                while (this.entityRiderPitchDelta < -180.0) {
                    this.entityRiderPitchDelta += 360.0;
                }

                double d = this.entityRiderYawDelta * 0.5;
                double d2 = this.entityRiderPitchDelta * 0.5;
                float f = 10.0F;
                if (d > f) {
                    d = f;
                }

                if (d < -f) {
                    d = -f;
                }

                if (d2 > f) {
                    d2 = f;
                }

                if (d2 < -f) {
                    d2 = -f;
                }

                this.entityRiderYawDelta -= d;
                this.entityRiderPitchDelta -= d2;
            }
        }
    }

    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
        }
    }

    public double getYOffset() {
        return this.yOffset;
    }

    public double getMountedYOffset() {
        return this.height * 0.75;
    }

    public void mountEntity(Entity entityIn) {
        this.entityRiderPitchDelta = 0.0;
        this.entityRiderYawDelta = 0.0;
        if (entityIn == null) {
            if (this.ridingEntity != null) {
                this.setLocationAndAngles(
                    this.ridingEntity.posX,
                    this.ridingEntity.boundingBox.minY + this.ridingEntity.height,
                    this.ridingEntity.posZ,
                    this.rotationYaw,
                    this.rotationPitch
                );
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        } else {
            if (this.ridingEntity != null) {
                this.ridingEntity.riddenByEntity = null;
            }

            if (entityIn != null) {
                for (Entity ridingEntity = entityIn.ridingEntity; ridingEntity != null; ridingEntity = ridingEntity.ridingEntity) {
                    if (ridingEntity == this) {
                        return;
                    }
                }
            }

            this.ridingEntity = entityIn;
            entityIn.riddenByEntity = this;
        }
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
        List collidingBoundingBoxes = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.contract(0.03125, 0.0, 0.03125));
        if (!collidingBoundingBoxes.isEmpty()) {
            double d = 0.0;

            for (int i = 0; i < collidingBoundingBoxes.size(); i++) {
                AxisAlignedBB axisAlignedBB = (AxisAlignedBB)collidingBoundingBoxes.get(i);
                if (axisAlignedBB.maxY > d) {
                    d = axisAlignedBB.maxY;
                }
            }

            y += d - this.boundingBox.minY;
            this.setPosition(x, y, z);
        }
    }

    public float getCollisionBorderSize() {
        return 0.1F;
    }

    public Vec3 getLookVec() {
        return null;
    }

    public void setInPortal() {
        if (this.timeUntilPortal > 0) {
            this.timeUntilPortal = this.getPortalCooldown();
        } else {
            double d = this.prevPosX - this.posX;
            double d2 = this.prevPosZ - this.posZ;
            if (!this.worldObj.isRemote && !this.inPortal) {
                this.teleportDirection = Direction.getMovementDirection(d, d2);
            }

            this.inPortal = true;
        }
    }

    public int getPortalCooldown() {
        return 300;
    }

    public void setVelocity(double x, double y, double z) {
        CheatBreaker.getInstance().getEventBus().handleEvent(new EntityDoubleEvent(this, x, y, z));
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public void handleHealthUpdate(byte b) {
    }

    public void performHurtAnimation() {
    }

    public ItemStack[] getInventory() {
        return null;
    }

    public void setCurrentItemOrArmor(int slotIn, ItemStack itemStackIn) {
    }

    public boolean isBurning() {
        boolean flag = this.worldObj != null && this.worldObj.isRemote;
        return !this.isImmuneToFire && (this.fire > 0 || flag && this.getFlag(0));
    }

    public boolean isRiding() {
        return this.ridingEntity != null;
    }

    public boolean isSneaking() {
        return this.getFlag(1);
    }

    public void setSneaking(boolean sneaking) {
        this.setFlag(1, sneaking);
    }

    public boolean isSprinting() {
        return this.getFlag(3);
    }

    public void setSprinting(boolean sprinting) {
        this.setFlag(3, sprinting);
    }

    public boolean isInvisible() {
        return this.getFlag(5);
    }

    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return this.isInvisible();
    }

    public void setInvisible(boolean invisible) {
        this.setFlag(5, invisible);
    }

    public boolean isEating() {
        return this.getFlag(4);
    }

    public void setEating(boolean eating) {
        this.setFlag(4, eating);
    }

    public boolean getFlag(int flag) {
        return (this.dataWatcher.getWatchableObjectByte(0) & 1 << flag) != 0;
    }

    public void setFlag(int flag, boolean set) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(0);
        if (set) {
            this.dataWatcher.updateObject(0, (byte)(watchableObjectByte | 1 << flag));
        } else {
            this.dataWatcher.updateObject(0, (byte)(watchableObjectByte & ~(1 << flag)));
        }
    }

    public int getAir() {
        return this.dataWatcher.getWatchableObjectShort(1);
    }

    public void setAir(int air) {
        this.dataWatcher.updateObject(1, (short)air);
    }

    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
        this.dealFireDamage(5);
        this.fire++;
        if (this.fire == 0) {
            this.setFire(8);
        }
    }

    public void onKillEntity(EntityLivingBase entityLivingIn) {
    }

    public boolean pushOutOfBlocks(double x, double y, double z) {
        int n = MathHelper.floor_double(x);
        int n2 = MathHelper.floor_double(y);
        int n3 = MathHelper.floor_double(z);
        double d = x - n;
        double d2 = y - n2;
        double d3 = z - n3;
        List findList = this.worldObj.findList(this.boundingBox);
        if (findList.isEmpty() && !this.worldObj.isBlockFullCube(n, n2, n3)) {
            return false;
        }

        boolean flag = !this.worldObj.isBlockFullCube(n - 1, n2, n3);
        boolean flag2 = !this.worldObj.isBlockFullCube(n + 1, n2, n3);
        boolean flag3 = !this.worldObj.isBlockFullCube(n, n2 - 1, n3);
        boolean flag4 = !this.worldObj.isBlockFullCube(n, n2 + 1, n3);
        boolean flag5 = !this.worldObj.isBlockFullCube(n, n2, n3 - 1);
        boolean flag6 = !this.worldObj.isBlockFullCube(n, n2, n3 + 1);
        byte b = 3;
        double d4 = 9999.0;
        if (flag && d < d4) {
            d4 = d;
            b = 0;
        }

        if (flag2 && 1.0 - d < d4) {
            d4 = 1.0 - d;
            b = 1;
        }

        if (flag4 && 1.0 - d2 < d4) {
            d4 = 1.0 - d2;
            b = 3;
        }

        if (flag5 && d3 < d4) {
            d4 = d3;
            b = 4;
        }

        if (flag6 && 1.0 - d3 < d4) {
            d4 = 1.0 - d3;
            b = 5;
        }

        float f = this.rand.nextFloat() * 0.2F + 0.1F;
        if (b == 0) {
            this.motionX = -f;
        }

        if (b == 1) {
            this.motionX = f;
        }

        if (b == 2) {
            this.motionY = -f;
        }

        if (b == 3) {
            this.motionY = f;
        }

        if (b == 4) {
            this.motionZ = -f;
        }

        if (b == 5) {
            this.motionZ = f;
        }

        return true;
    }

    public void setInWeb() {
        this.isInWeb = true;
        this.fallDistance = 0.0F;
    }

    public String getCommandSenderName() {
        String entityString = EntityList.getEntityString(this);
        if (entityString == null) {
            entityString = "generic";
        }

        return StatCollector.translateToLocal("entity." + entityString + ".name");
    }

    public Entity[] getParts() {
        return null;
    }

    public boolean isEntityEqual(Entity entityIn) {
        return this == entityIn;
    }

    public float getRotationYawHead() {
        return 0.0F;
    }

    public void setRotationYawHead(float rotation) {
    }

    public boolean canAttackWithItem() {
        return true;
    }

    public boolean hitByEntity(Entity entityIn) {
        return false;
    }

    @Override
    public String toString() {
        return String.format(
            "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
            this.getClass().getSimpleName(),
            this.getCommandSenderName(),
            this.entityId,
            this.worldObj == null ? "~NULL~" : this.worldObj.getWorldInfo().getWorldName(),
            this.posX,
            this.posY,
            this.posZ
        );
    }

    public boolean isEntityInvulnerable() {
        return this.invulnerable;
    }

    public void copyLocationAndAnglesFrom(Entity entityIn) {
        this.setLocationAndAngles(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.rotationYaw, entityIn.rotationPitch);
    }

    public void copyDataFrom(Entity entityIn, boolean unused) {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        entityIn.writeToNBT(nbttagCompound);
        this.readFromNBT(nbttagCompound);
        this.timeUntilPortal = entityIn.timeUntilPortal;
        this.teleportDirection = entityIn.teleportDirection;
    }

    public void travelToDimension(int dimensionId) throws java.io.IOException {
        if (!this.worldObj.isRemote && !this.isDead) {
            this.worldObj.theProfiler.startSection("changeDimension");
            MinecraftServer server = MinecraftServer.getServer();
            int dimension = this.dimension;
            WorldServer worldServerForDimension = server.worldServerForDimension(dimension);
            WorldServer worldServerForDimension2 = server.worldServerForDimension(dimensionId);
            this.dimension = dimensionId;
            if (dimension == 1 && dimensionId == 1) {
                worldServerForDimension2 = server.worldServerForDimension(0);
                this.dimension = 0;
            }

            this.worldObj.removeEntity(this);
            this.isDead = false;
            this.worldObj.theProfiler.startSection("reposition");
            server.getConfigurationManager().transferEntityToWorld(this, dimension, worldServerForDimension, worldServerForDimension2);
            this.worldObj.theProfiler.endStartSection("reloading");
            Entity createEntityByName = EntityList.createEntityByName(EntityList.getEntityString(this), worldServerForDimension2);
            if (createEntityByName != null) {
                createEntityByName.copyDataFrom(this, true);
                if (dimension == 1 && dimensionId == 1) {
                    ChunkCoordinates spawnPoint = worldServerForDimension2.getSpawnPoint();
                    spawnPoint.posY = this.worldObj.getTopSolidOrLiquidBlock(spawnPoint.posX, spawnPoint.posZ);
                    createEntityByName.setLocationAndAngles(spawnPoint.posX, spawnPoint.posY, spawnPoint.posZ, createEntityByName.rotationYaw, createEntityByName.rotationPitch);
                }

                worldServerForDimension2.spawnEntityInWorld(createEntityByName);
            }

            this.isDead = true;
            this.worldObj.theProfiler.endSection();
            worldServerForDimension.resetUpdateEntityTick();
            worldServerForDimension2.resetUpdateEntityTick();
            this.worldObj.theProfiler.endSection();
        }
    }

    public float getExplosionResistance(Explosion explosionIn, World worldIn, int x, int y, int z, Block blockIn) {
        return blockIn.getExplosionResistance(this);
    }

    public boolean checkExplosion(Explosion explosionIn, World worldIn, int x, int y, int z, Block blockIn, float unused) {
        return true;
    }

    public int getMaxFallHeight() {
        return 3;
    }

    public int getTeleportDirection() {
        return this.teleportDirection;
    }

    public boolean doesEntityNotTriggerPressurePlate() {
        return false;
    }

    public void addEntityCrashInfo(CrashReportCategory category) {
        category.addCrashSectionCallable("Entity Type", new Callable() {
            public String call() {
                return EntityList.getEntityString(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
            }
        });
        category.addCrashSection("Entity ID", this.entityId);
        category.addCrashSectionCallable("Entity Name", new Callable() {
            public String call() {
                return Entity.this.getCommandSenderName();
            }
        });
        category.addCrashSection("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.posX, this.posY, this.posZ));
        category.addCrashSection(
            "Entity's Block location",
            CrashReportCategory.getLocationInfo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
        );
        category.addCrashSection("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.motionX, this.motionY, this.motionZ));
    }

    public boolean canRenderOnFire() {
        return this.isBurning();
    }

    public UUID getUniqueID() {
        return this.entityUniqueID;
    }

    public boolean isPushedByWater() {
        return true;
    }

    public IChatComponent getFormattedCommandSenderName() {
        return new ChatComponentText(this.getCommandSenderName());
    }

    public void process(int n) {
    }

    public Vec3 getLook(float f) {
        if (f == 1.0F) {
            return this.createVec3(this.rotationPitch, this.rotationYaw);
        }

        float f2 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * f;
        float f3 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * f;
        return this.createVec3(f2, f3);
    }

    public Vec3 createVec3(float f, float f2) {
        float cos = MathHelper.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float sin = MathHelper.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f3 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
        float sin2 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
        return new Vec3(sin * f3, sin2, cos * f3);
    }

    public enum EnumEntitySize {
        SIZE_1("SIZE_1", 0),
        SIZE_2("SIZE_2", 1),
        SIZE_3("SIZE_3", 2),
        SIZE_4("SIZE_4", 3),
        SIZE_5("SIZE_5", 4),
        SIZE_6("SIZE_6", 5);

        public static Entity.EnumEntitySize[] enumEntitySizeArray = new Entity.EnumEntitySize[]{SIZE_1, SIZE_2, SIZE_3, SIZE_4, SIZE_5, SIZE_6};

        public int multiplyBy32AndRound(double d) {
            double d2 = d - (MathHelper.floor_double(d) + 0.5);
            switch (this) {
                case SIZE_1:
                    if (d2 < 0.0) {
                        if (d2 < -0.3125) {
                            return MathHelper.ceiling_double_int(d * 32.0);
                        }
                    } else if (d2 < 0.3125) {
                        return MathHelper.ceiling_double_int(d * 32.0);
                    }

                    return MathHelper.floor_double(d * 32.0);
                case SIZE_2:
                    if (d2 < 0.0) {
                        if (d2 < -0.3125) {
                            return MathHelper.floor_double(d * 32.0);
                        }
                    } else if (d2 < 0.3125) {
                        return MathHelper.floor_double(d * 32.0);
                    }

                    return MathHelper.ceiling_double_int(d * 32.0);
                case SIZE_3:
                    if (d2 > 0.0) {
                        return MathHelper.floor_double(d * 32.0);
                    }

                    return MathHelper.ceiling_double_int(d * 32.0);
                case SIZE_4:
                    if (d2 < 0.0) {
                        if (d2 < -0.1875) {
                            return MathHelper.ceiling_double_int(d * 32.0);
                        }
                    } else if (d2 < 0.1875) {
                        return MathHelper.ceiling_double_int(d * 32.0);
                    }

                    return MathHelper.floor_double(d * 32.0);
                case SIZE_5:
                    if (d2 < 0.0) {
                        if (d2 < -0.1875) {
                            return MathHelper.floor_double(d * 32.0);
                        }
                    } else if (d2 < 0.1875) {
                        return MathHelper.floor_double(d * 32.0);
                    }

                    return MathHelper.ceiling_double_int(d * 32.0);
                case SIZE_6:
                default:
                    return d2 > 0.0 ? MathHelper.ceiling_double_int(d * 32.0) : MathHelper.floor_double(d * 32.0);
            }
        }

        EnumEntitySize(String str, int n) {
        }
    }
}
