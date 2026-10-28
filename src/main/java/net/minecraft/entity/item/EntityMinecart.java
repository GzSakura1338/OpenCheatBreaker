package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityMinecart extends Entity {
    public boolean isInReverse;
    public String entityName;
    public static int[][][] matrix = new int[][][]{
        {{0, 0, -1}, {0, 0, 1}},
        {{-1, 0, 0}, {1, 0, 0}},
        {{-1, -1, 0}, {1, 0, 0}},
        {{-1, 0, 0}, {1, -1, 0}},
        {{0, 0, -1}, {0, -1, 1}},
        {{0, -1, -1}, {0, 0, 1}},
        {{0, 0, 1}, {1, 0, 0}},
        {{0, 0, 1}, {-1, 0, 0}},
        {{0, 0, -1}, {-1, 0, 0}},
        {{0, 0, -1}, {1, 0, 0}}
    };
    public int turnProgress;
    public double minecartX;
    public double minecartY;
    public double minecartZ;
    public double minecartYaw;
    public double minecartPitch;
    public double velocityX;
    public double velocityY;
    public double velocityZ;

    public EntityMinecart(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.7F);
        this.yOffset = this.height / 2.0F;
    }

    public static EntityMinecart createMinecart(World world, double d, double d2, double d3, int n) {
        switch (n) {
            case 1:
                return new EntityMinecartChest(world, d, d2, d3);
            case 2:
                return new EntityMinecartFurnace(world, d, d2, d3);
            case 3:
                return new EntityMinecartTNT(world, d, d2, d3);
            case 4:
                return new EntityMinecartMobSpawner(world, d, d2, d3);
            case 5:
                return new EntityMinecartHopper(world, d, d2, d3);
            case 6:
                return new EntityMinecartCommandBlock(world, d, d2, d3);
            default:
                return new EntityMinecartEmpty(world, d, d2, d3);
        }
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void entityInit() {
        this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(1));
        this.dataWatcher.addObject(19, new Float(0.0F));
        this.dataWatcher.addObject(20, new Integer(0));
        this.dataWatcher.addObject(21, new Integer(6));
        this.dataWatcher.addObject(22, (byte)0);
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return entityIn.canBePushed() ? entityIn.boundingBox : null;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    public EntityMinecart(World world, double d, double d2, double d3) {
        this(world);
        this.setPosition(d, d2, d3);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevPosX = d;
        this.prevPosY = d2;
        this.prevPosZ = d3;
    }

    @Override
    public double getMountedYOffset() {
        return this.height * 0.0 - 0.3F;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.worldObj.isRemote || this.isDead) {
            return true;
        }

        if (this.isEntityInvulnerable()) {
            return false;
        }

        this.setRollingDirection(-this.getRollingDirection());
        this.setRollingAmplitude(10);
        this.setBeenAttacked();
        this.setDamage(this.getDamage() + amount * 10.0F);
        boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;
        if (flag || this.getDamage() > 40.0F) {
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity(this);
            }

            if (flag && !this.isCustomInventoryName()) {
                this.setDead();
            } else {
                this.killMinecart(source);
            }
        }

        return true;
    }

    public void killMinecart(DamageSource damageSource) {
        this.setDead();
        ItemStack itemStack = new ItemStack(Items.minecart, 1);
        if (this.entityName != null) {
            itemStack.setStackDisplayName(this.entityName);
        }

        this.entityDropItem(itemStack, 0.0F);
    }

    @Override
    public void performHurtAnimation() {
        this.setRollingDirection(-this.getRollingDirection());
        this.setRollingAmplitude(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public void setDead() {
        super.setDead();
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        if (this.getRollingAmplitude() > 0) {
            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.posY < -64.0) {
            this.kill();
        }

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

        if (this.worldObj.isRemote) {
            if (this.turnProgress > 0) {
                double d = this.posX + (this.minecartX - this.posX) / this.turnProgress;
                double d2 = this.posY + (this.minecartY - this.posY) / this.turnProgress;
                double d3 = this.posZ + (this.minecartZ - this.posZ) / this.turnProgress;
                double d4 = MathHelper.wrapAngleTo180_double(this.minecartYaw - this.rotationYaw);
                this.rotationYaw = (float)(this.rotationYaw + d4 / this.turnProgress);
                this.rotationPitch = (float)(this.rotationPitch + (this.minecartPitch - this.rotationPitch) / this.turnProgress);
                this.turnProgress--;
                this.setPosition(d, d2, d3);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            } else {
                this.setPosition(this.posX, this.posY, this.posZ);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
        } else {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.04F;
            int n = MathHelper.floor_double(this.posX);
            int n2 = MathHelper.floor_double(this.posY);
            int n3 = MathHelper.floor_double(this.posZ);
            if (BlockRailBase.isRailBlockAt(this.worldObj, n, n2 - 1, n3)) {
                n2--;
            }

            double d = 0.4;
            double d2 = 0.0078125;
            Block block = this.worldObj.getBlock(n, n2, n3);
            if (BlockRailBase.isRailBlock(block)) {
                int blockMetadata = this.worldObj.getBlockMetadata(n, n2, n3);
                this.process(n, n2, n3, d, d2, block, blockMetadata);
                if (block == Blocks.activator_rail) {
                    this.onActivatorRailPass(n, n2, n3, (blockMetadata & 8) != 0);
                }
            } else {
                this.process(d);
            }

            this.doBlockCollisions();
            this.rotationPitch = 0.0F;
            double d3 = this.prevPosX - this.posX;
            double d4 = this.prevPosZ - this.posZ;
            if (d3 * d3 + d4 * d4 > 0.001) {
                this.rotationYaw = (float)(Math.atan2(d4, d3) * 180.0 / Math.PI);
                if (this.isInReverse) {
                    this.rotationYaw += 180.0F;
                }
            }

            double d5 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);
            if (d5 < -170.0 || d5 >= 170.0) {
                this.rotationYaw += 180.0F;
                this.isInReverse = !this.isInReverse;
            }

            this.setRotation(this.rotationYaw, this.rotationPitch);
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2F, 0.0, 0.2F));
            if (list != null && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    Entity entity = (Entity)list.get(i);
                    if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityMinecart) {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
                if (this.riddenByEntity.ridingEntity == this) {
                    this.riddenByEntity.ridingEntity = null;
                }

                this.riddenByEntity = null;
            }
        }
    }

    public void onActivatorRailPass(int n, int n2, int n3, boolean flag) {
    }

    public void process(double d) {
        if (this.motionX < -d) {
            this.motionX = -d;
        }

        if (this.motionX > d) {
            this.motionX = d;
        }

        if (this.motionZ < -d) {
            this.motionZ = -d;
        }

        if (this.motionZ > d) {
            this.motionZ = d;
        }

        if (this.onGround) {
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (!this.onGround) {
            this.motionX *= 0.95F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.95F;
        }
    }

    public void process(int n, int n2, int n3, double d, double d2, Block block, int n4) {
        this.fallDistance = 0.0F;
        Vec3 snapPositionToRail = this.snapPositionToRail(this.posX, this.posY, this.posZ);
        this.posY = n2;
        boolean flag = false;
        boolean flag2 = false;
        if (block == Blocks.golden_rail) {
            flag = (n4 & 8) != 0;
            flag2 = !flag;
        }

        if (((BlockRailBase)block).isPowered()) {
            n4 &= 7;
        }

        if (n4 >= 2 && n4 <= 5) {
            this.posY = n2 + 1;
        }

        if (n4 == 2) {
            this.motionX -= d2;
        }

        if (n4 == 3) {
            this.motionX += d2;
        }

        if (n4 == 4) {
            this.motionZ += d2;
        }

        if (n4 == 5) {
            this.motionZ -= d2;
        }

        int[][] intses = matrix[n4];
        double d3 = intses[1][0] - intses[0][0];
        double d4 = intses[1][2] - intses[0][2];
        double sqrt = Math.sqrt(d3 * d3 + d4 * d4);
        double d5 = this.motionX * d3 + this.motionZ * d4;
        if (d5 < 0.0) {
            d3 = -d3;
            d4 = -d4;
        }

        double sqrt2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (sqrt2 > 2.0) {
            sqrt2 = 2.0;
        }

        this.motionX = sqrt2 * d3 / sqrt;
        this.motionZ = sqrt2 * d4 / sqrt;
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
            double moveForward = ((EntityLivingBase)this.riddenByEntity).moveForward;
            if (moveForward > 0.0) {
                double d6 = -Math.sin(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
                double cos = Math.cos(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
                double d7 = this.motionX * this.motionX + this.motionZ * this.motionZ;
                if (d7 < 0.01) {
                    this.motionX += d6 * 0.1;
                    this.motionZ += cos * 0.1;
                    flag2 = false;
                }
            }
        }

        if (flag2) {
            double sqrt3 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (sqrt3 < 0.03) {
                this.motionX *= 0.0;
                this.motionY *= 0.0;
                this.motionZ *= 0.0;
            } else {
                this.motionX *= 0.5;
                this.motionY *= 0.0;
                this.motionZ *= 0.5;
            }
        }

        double d6 = 0.0;
        double d7 = n + 0.5 + intses[0][0] * 0.5;
        double d8 = n3 + 0.5 + intses[0][2] * 0.5;
        double d9 = n + 0.5 + intses[1][0] * 0.5;
        double d10 = n3 + 0.5 + intses[1][2] * 0.5;
        d3 = d9 - d7;
        d4 = d10 - d8;
        if (d3 == 0.0) {
            this.posX = n + 0.5;
            d6 = this.posZ - n3;
        } else if (d4 == 0.0) {
            this.posZ = n3 + 0.5;
            d6 = this.posX - n;
        } else {
            double d11 = this.posX - d7;
            double d12 = this.posZ - d8;
            d6 = (d11 * d3 + d12 * d4) * 2.0;
        }

        this.posX = d7 + d3 * d6;
        this.posZ = d8 + d4 * d6;
        this.setPosition(this.posX, this.posY + this.yOffset, this.posZ);
        double motionX = this.motionX;
        double motionZ = this.motionZ;
        if (this.riddenByEntity != null) {
            motionX *= 0.75;
            motionZ *= 0.75;
        }

        if (motionX < -d) {
            motionX = -d;
        }

        if (motionX > d) {
            motionX = d;
        }

        if (motionZ < -d) {
            motionZ = -d;
        }

        if (motionZ > d) {
            motionZ = d;
        }

        this.moveEntity(motionX, 0.0, motionZ);
        if (intses[0][1] != 0 && MathHelper.floor_double(this.posX) - n == intses[0][0] && MathHelper.floor_double(this.posZ) - n3 == intses[0][2]) {
            this.setPosition(this.posX, this.posY + intses[0][1], this.posZ);
        } else if (intses[1][1] != 0 && MathHelper.floor_double(this.posX) - n == intses[1][0] && MathHelper.floor_double(this.posZ) - n3 == intses[1][2]) {
            this.setPosition(this.posX, this.posY + intses[1][1], this.posZ);
        }

        this.applyDrag();
        Vec3 snapPositionToRail2 = this.snapPositionToRail(this.posX, this.posY, this.posZ);
        if (snapPositionToRail2 != null && snapPositionToRail != null) {
            double d11 = (snapPositionToRail.yCoord - snapPositionToRail2.yCoord) * 0.05;
            sqrt2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (sqrt2 > 0.0) {
                this.motionX = this.motionX / sqrt2 * (sqrt2 + d11);
                this.motionZ = this.motionZ / sqrt2 * (sqrt2 + d11);
            }

            this.setPosition(this.posX, snapPositionToRail2.yCoord, this.posZ);
        }

        int n5 = MathHelper.floor_double(this.posX);
        int n6 = MathHelper.floor_double(this.posZ);
        if (n5 != n || n6 != n3) {
            sqrt2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.motionX = sqrt2 * (n5 - n);
            this.motionZ = sqrt2 * (n6 - n3);
        }

        if (flag) {
            double sqrt3 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (sqrt3 > 0.01) {
                double d11 = 0.06;
                this.motionX = this.motionX + this.motionX / sqrt3 * d11;
                this.motionZ = this.motionZ + this.motionZ / sqrt3 * d11;
            } else if (n4 == 1) {
                if (this.worldObj.getBlock(n - 1, n2, n3).isNormalCube()) {
                    this.motionX = 0.02;
                } else if (this.worldObj.getBlock(n + 1, n2, n3).isNormalCube()) {
                    this.motionX = -0.02;
                }
            } else if (n4 == 0) {
                if (this.worldObj.getBlock(n, n2, n3 - 1).isNormalCube()) {
                    this.motionZ = 0.02;
                } else if (this.worldObj.getBlock(n, n2, n3 + 1).isNormalCube()) {
                    this.motionZ = -0.02;
                }
            }
        }
    }

    public void applyDrag() {
        if (this.riddenByEntity != null) {
            this.motionX *= 0.997F;
            this.motionY *= 0.0;
            this.motionZ *= 0.997F;
        } else {
            this.motionX *= 0.96F;
            this.motionY *= 0.0;
            this.motionZ *= 0.96F;
        }
    }

    public Vec3 snapPositionToRailWithOffset(double d, double d2, double d3, double d4) {
        int n = MathHelper.floor_double(d);
        int n2 = MathHelper.floor_double(d2);
        int n3 = MathHelper.floor_double(d3);
        if (BlockRailBase.isRailBlockAt(this.worldObj, n, n2 - 1, n3)) {
            n2--;
        }

        Block block = this.worldObj.getBlock(n, n2, n3);
        if (!BlockRailBase.isRailBlock(block)) {
            return null;
        }

        int blockMetadata = this.worldObj.getBlockMetadata(n, n2, n3);
        if (((BlockRailBase)block).isPowered()) {
            blockMetadata &= 7;
        }

        d2 = n2;
        if (blockMetadata >= 2 && blockMetadata <= 5) {
            d2 = n2 + 1;
        }

        int[][] intses = matrix[blockMetadata];
        double d5 = intses[1][0] - intses[0][0];
        double d6 = intses[1][2] - intses[0][2];
        double sqrt = Math.sqrt(d5 * d5 + d6 * d6);
        d5 /= sqrt;
        d6 /= sqrt;
        d += d5 * d4;
        d3 += d6 * d4;
        if (intses[0][1] != 0 && MathHelper.floor_double(d) - n == intses[0][0] && MathHelper.floor_double(d3) - n3 == intses[0][2]) {
            d2 += intses[0][1];
        } else if (intses[1][1] != 0 && MathHelper.floor_double(d) - n == intses[1][0] && MathHelper.floor_double(d3) - n3 == intses[1][2]) {
            d2 += intses[1][1];
        }

        return this.snapPositionToRail(d, d2, d3);
    }

    public Vec3 snapPositionToRail(double d, double d2, double d3) {
        int n = MathHelper.floor_double(d);
        int n2 = MathHelper.floor_double(d2);
        int n3 = MathHelper.floor_double(d3);
        if (BlockRailBase.isRailBlockAt(this.worldObj, n, n2 - 1, n3)) {
            n2--;
        }

        Block block = this.worldObj.getBlock(n, n2, n3);
        if (BlockRailBase.isRailBlock(block)) {
            int blockMetadata = this.worldObj.getBlockMetadata(n, n2, n3);
            d2 = n2;
            if (((BlockRailBase)block).isPowered()) {
                blockMetadata &= 7;
            }

            if (blockMetadata >= 2 && blockMetadata <= 5) {
                d2 = n2 + 1;
            }

            int[][] intses = matrix[blockMetadata];
            double d4 = 0.0;
            double d5 = n + 0.5 + intses[0][0] * 0.5;
            double d6 = n2 + 0.5 + intses[0][1] * 0.5;
            double d7 = n3 + 0.5 + intses[0][2] * 0.5;
            double d8 = n + 0.5 + intses[1][0] * 0.5;
            double d9 = n2 + 0.5 + intses[1][1] * 0.5;
            double d10 = n3 + 0.5 + intses[1][2] * 0.5;
            double d11 = d8 - d5;
            double d12 = (d9 - d6) * 2.0;
            double d13 = d10 - d7;
            if (d11 == 0.0) {
                d = n + 0.5;
                d4 = d3 - n3;
            } else if (d13 == 0.0) {
                d3 = n3 + 0.5;
                d4 = d - n;
            } else {
                double d14 = d - d5;
                double d15 = d3 - d7;
                d4 = (d14 * d11 + d15 * d13) * 2.0;
            }

            d = d5 + d11 * d4;
            d2 = d6 + d12 * d4;
            d3 = d7 + d13 * d4;
            if (d12 < 0.0) {
                d2++;
            }

            if (d12 > 0.0) {
                d2 += 0.5;
            }

            return Vec3.createVectorHelper(d, d2, d3);
        } else {
            return null;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        if (tagCompund.getBoolean("CustomDisplayTile")) {
            this.process9(tagCompund.getInteger("DisplayTile"));
            this.setDisplayTileData(tagCompund.getInteger("DisplayData"));
            this.setDisplayTileOffset(tagCompund.getInteger("DisplayOffset"));
        }

        if (tagCompund.hasKey("CustomName", 8) && tagCompund.getString("CustomName").length() > 0) {
            this.entityName = tagCompund.getString("CustomName");
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        if (this.hasDisplayTile()) {
            tagCompound.setBoolean("CustomDisplayTile", true);
            tagCompound.setInteger("DisplayTile", this.getDisplayTile().getMaterial() == Material.air ? 0 : Block.getIdFromBlock(this.getDisplayTile()));
            tagCompound.setInteger("DisplayData", this.getDisplayTileData());
            tagCompound.setInteger("DisplayOffset", this.getDisplayTileOffset());
        }

        if (this.entityName != null && this.entityName.length() > 0) {
            tagCompound.setString("CustomName", this.entityName);
        }
    }

    @Override
    public float getShadowSize() {
        return 0.0F;
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        if (!this.worldObj.isRemote && entityIn != this.riddenByEntity) {
            if (entityIn instanceof EntityLivingBase
                && !(entityIn instanceof EntityPlayer)
                && !(entityIn instanceof EntityIronGolem)
                && this.getMinecartType() == 0
                && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01
                && this.riddenByEntity == null
                && entityIn.ridingEntity == null) {
                entityIn.mountEntity(this);
            }

            double d = entityIn.posX - this.posX;
            double d2 = entityIn.posZ - this.posZ;
            double d3 = d * d + d2 * d2;
            if (d3 >= 1.0E-4F) {
                d3 = MathHelper.sqrt_double(d3);
                d /= d3;
                d2 /= d3;
                double d4 = 1.0 / d3;
                if (d4 > 1.0) {
                    d4 = 1.0;
                }

                d *= d4;
                d2 *= d4;
                d *= 0.1F;
                d2 *= 0.1F;
                d *= 1.0F - this.entityCollisionReduction;
                d2 *= 1.0F - this.entityCollisionReduction;
                d *= 0.5;
                d2 *= 0.5;
                if (entityIn instanceof EntityMinecart) {
                    double d5 = entityIn.posX - this.posX;
                    double d6 = entityIn.posZ - this.posZ;
                    Vec3 normalize = Vec3.createVectorHelper(d5, 0.0, d6).normalize();
                    Vec3 normalize2 = Vec3.createVectorHelper(
                            MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F), 0.0, MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F)
                        )
                        .normalize();
                    double abs = Math.abs(normalize.dotProduct(normalize2));
                    if (abs < 0.8F) {
                        return;
                    }

                    double d7 = entityIn.motionX + this.motionX;
                    double d8 = entityIn.motionZ + this.motionZ;
                    if (((EntityMinecart)entityIn).getMinecartType() == 2 && this.getMinecartType() != 2) {
                        this.motionX *= 0.2F;
                        this.motionZ *= 0.2F;
                        this.addVelocity(entityIn.motionX - d, 0.0, entityIn.motionZ - d2);
                        entityIn.motionX *= 0.95F;
                        entityIn.motionZ *= 0.95F;
                    } else if (((EntityMinecart)entityIn).getMinecartType() != 2 && this.getMinecartType() == 2) {
                        entityIn.motionX *= 0.2F;
                        entityIn.motionZ *= 0.2F;
                        entityIn.addVelocity(this.motionX + d, 0.0, this.motionZ + d2);
                        this.motionX *= 0.95F;
                        this.motionZ *= 0.95F;
                    } else {
                        d7 /= 2.0;
                        d8 /= 2.0;
                        this.motionX *= 0.2F;
                        this.motionZ *= 0.2F;
                        this.addVelocity(d7 - d, 0.0, d8 - d2);
                        entityIn.motionX *= 0.2F;
                        entityIn.motionZ *= 0.2F;
                        entityIn.addVelocity(d7 + d, 0.0, d8 + d2);
                    }
                } else {
                    this.addVelocity(-d, 0.0, -d2);
                    entityIn.addVelocity(d / 4.0, 0.0, d2 / 4.0);
                }
            }
        }
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
        this.minecartX = x;
        this.minecartY = y;
        this.minecartZ = z;
        this.minecartYaw = yaw;
        this.minecartPitch = pitch;
        this.turnProgress = rotationIncrements + 2;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.velocityX = this.motionX = x;
        this.velocityY = this.motionY = y;
        this.velocityZ = this.motionZ = z;
    }

    public void setDamage(float f) {
        this.dataWatcher.updateObject(19, f);
    }

    public float getDamage() {
        return this.dataWatcher.getWatchableObjectFloat(19);
    }

    public void setRollingAmplitude(int n) {
        this.dataWatcher.updateObject(17, n);
    }

    public int getRollingAmplitude() {
        return this.dataWatcher.getWatchableObjectInt(17);
    }

    public void setRollingDirection(int n) {
        this.dataWatcher.updateObject(18, n);
    }

    public int getRollingDirection() {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    public abstract int getMinecartType();

    public Block getDisplayTile() {
        if (!this.hasDisplayTile()) {
            return this.getDefaultDisplayTile();
        }

        int n = this.getDataWatcher().getWatchableObjectInt(20) & 65535;
        return Block.getBlockById(n);
    }

    public Block getDefaultDisplayTile() {
        return Blocks.air;
    }

    public int getDisplayTileData() {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTileData() : this.getDataWatcher().getWatchableObjectInt(20) >> 16;
    }

    public int getDefaultDisplayTileData() {
        return 0;
    }

    public int getDisplayTileOffset() {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataWatcher().getWatchableObjectInt(21);
    }

    public int getDefaultDisplayTileOffset() {
        return 6;
    }

    public void process9(int n) {
        this.getDataWatcher().updateObject(20, n & 65535 | this.getDisplayTileData() << 16);
        this.setHasDisplayTile(true);
    }

    public void setDisplayTileData(int n) {
        this.getDataWatcher().updateObject(20, Block.getIdFromBlock(this.getDisplayTile()) & 65535 | n << 16);
        this.setHasDisplayTile(true);
    }

    public void setDisplayTileOffset(int n) {
        this.getDataWatcher().updateObject(21, n);
        this.setHasDisplayTile(true);
    }

    public boolean hasDisplayTile() {
        return this.getDataWatcher().getWatchableObjectByte(22) == 1;
    }

    public void setHasDisplayTile(boolean flag) {
        this.getDataWatcher().updateObject(22, (byte)(flag ? 1 : 0));
    }

    public void setMinecartName(String str) {
        this.entityName = str;
    }

    @Override
    public String getCommandSenderName() {
        return this.entityName != null ? this.entityName : super.getCommandSenderName();
    }

    public boolean isCustomInventoryName() {
        return this.entityName != null;
    }

    public String getEntityName() {
        return this.entityName;
    }
}
