package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityFireball extends Entity {
    public int blockX = -1;
    public int blockY = -1;
    public int blockZ = -1;
    public Block inBlock;
    public boolean inGround;
    public EntityLivingBase shootingEntity;
    public int ticksAlive;
    public int ticksInAir;
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;

    public EntityFireball(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
    }

    @Override
    public void entityInit() {
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d = this.boundingBox.getAverageEdgeLength() * 4.0;
        d *= 64.0;
        return distance < d * d;
    }

    public EntityFireball(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.setLocationAndAngles(d, d2, d3, this.rotationYaw, this.rotationPitch);
        this.setPosition(d, d2, d3);
        double d7 = MathHelper.sqrt_double(d4 * d4 + d5 * d5 + d6 * d6);
        this.accelerationX = d4 / d7 * 0.1;
        this.accelerationY = d5 / d7 * 0.1;
        this.accelerationZ = d6 / d7 * 0.1;
    }

    public EntityFireball(World world, EntityLivingBase entityLivingBase, double d, double d2, double d3) {
        super(world);
        this.shootingEntity = entityLivingBase;
        this.setSize(1.0F, 1.0F);
        this.setLocationAndAngles(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, entityLivingBase.rotationYaw, entityLivingBase.rotationPitch);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = this.motionY = this.motionZ = 0.0;
        d += this.rand.nextGaussian() * 0.4;
        d2 += this.rand.nextGaussian() * 0.4;
        d3 += this.rand.nextGaussian() * 0.4;
        double d4 = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
        this.accelerationX = d / d4 * 0.1;
        this.accelerationY = d2 / d4 * 0.1;
        this.accelerationZ = d3 / d4 * 0.1;
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        if (this.worldObj.isRemote
            || (this.shootingEntity == null || !this.shootingEntity.isDead) && this.worldObj.blockExists((int)this.posX, (int)this.posY, (int)this.posZ)) {
            super.onUpdate();
            this.setFire(1);
            if (this.inGround) {
                if (this.worldObj.getBlock(this.blockX, this.blockY, this.blockZ) == this.inBlock) {
                    this.ticksAlive++;
                    if (this.ticksAlive == 600) {
                        this.setDead();
                    }

                    return;
                }

                this.inGround = false;
                this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
                this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
                this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
                this.ticksAlive = 0;
                this.ticksInAir = 0;
            } else {
                this.ticksInAir++;
            }

            Vec3 createVectorHelper = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 createVectorHelper2 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition rayTraceBlocks = this.worldObj.rayTraceBlocks(createVectorHelper, createVectorHelper2);
            createVectorHelper = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            createVectorHelper2 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            if (rayTraceBlocks != null) {
                createVectorHelper2 = Vec3.createVectorHelper(rayTraceBlocks.hitVec.xCoord, rayTraceBlocks.hitVec.yCoord, rayTraceBlocks.hitVec.zCoord);
            }

            Entity entity = null;
            List list = this.worldObj
                .getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            double d = 0.0;

            for (int i = 0; i < list.size(); i++) {
                Entity entity2 = (Entity)list.get(i);
                if (entity2.canBeCollidedWith() && (!entity2.isEntityEqual(this.shootingEntity) || this.ticksInAir >= 25)) {
                    float f = 0.3F;
                    AxisAlignedBB expand = entity2.boundingBox.expand(f, f, f);
                    MovingObjectPosition calculateIntercept = expand.calculateIntercept(createVectorHelper, createVectorHelper2);
                    if (calculateIntercept != null) {
                        double distanceTo = createVectorHelper.distanceTo(calculateIntercept.hitVec);
                        if (distanceTo < d || d == 0.0) {
                            entity = entity2;
                            d = distanceTo;
                        }
                    }
                }
            }

            if (entity != null) {
                rayTraceBlocks = new MovingObjectPosition(entity);
            }

            if (rayTraceBlocks != null) {
                this.onImpact(rayTraceBlocks);
            }

            this.posX = this.posX + this.motionX;
            this.posY = this.posY + this.motionY;
            this.posZ = this.posZ + this.motionZ;
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) + 90.0F;
            this.rotationPitch = (float)(Math.atan2(f, this.motionY) * 180.0 / Math.PI) - 90.0F;

            while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
                this.prevRotationPitch -= 360.0F;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float motionFactor = this.getMotionFactor();
            if (this.isInWater()) {
                for (int i = 0; i < 4; i++) {
                    float f2 = 0.25F;
                    this.worldObj
                        .spawnParticle(
                            "bubble",
                            this.posX - this.motionX * f2,
                            this.posY - this.motionY * f2,
                            this.posZ - this.motionZ * f2,
                            this.motionX,
                            this.motionY,
                            this.motionZ
                        );
                }

                motionFactor = 0.8F;
            }

            this.motionX = this.motionX + this.accelerationX;
            this.motionY = this.motionY + this.accelerationY;
            this.motionZ = this.motionZ + this.accelerationZ;
            this.motionX *= motionFactor;
            this.motionY *= motionFactor;
            this.motionZ *= motionFactor;
            this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
            this.setPosition(this.posX, this.posY, this.posZ);
        } else {
            this.setDead();
        }
    }

    public float getMotionFactor() {
        return 0.95F;
    }

    public abstract void onImpact(MovingObjectPosition movingObjectPosition);

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setShort("xTile", (short)this.blockX);
        tagCompound.setShort("yTile", (short)this.blockY);
        tagCompound.setShort("zTile", (short)this.blockZ);
        tagCompound.setByte("inTile", (byte)Block.getIdFromBlock(this.inBlock));
        tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        tagCompound.setTag("direction", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.blockX = tagCompund.getShort("xTile");
        this.blockY = tagCompund.getShort("yTile");
        this.blockZ = tagCompund.getShort("zTile");
        this.inBlock = Block.getBlockById(tagCompund.getByte("inTile") & 255);
        this.inGround = tagCompund.getByte("inGround") == 1;
        if (tagCompund.hasKey("direction", 9)) {
            NBTTagList tagList = tagCompund.getTagList("direction", 6);
            this.motionX = tagList.getDoubleAt(0);
            this.motionY = tagList.getDoubleAt(1);
            this.motionZ = tagList.getDoubleAt(2);
        } else {
            this.setDead();
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public float getCollisionBorderSize() {
        return 1.0F;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        this.setBeenAttacked();
        if (source.getEntity() != null) {
            Vec3 lookVec = source.getEntity().getLookVec();
            if (lookVec != null) {
                this.motionX = lookVec.xCoord;
                this.motionY = lookVec.yCoord;
                this.motionZ = lookVec.zCoord;
                this.accelerationX = this.motionX * 0.1;
                this.accelerationY = this.motionY * 0.1;
                this.accelerationZ = this.motionZ * 0.1;
            }

            if (source.getEntity() instanceof EntityLivingBase) {
                this.shootingEntity = (EntityLivingBase)source.getEntity();
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public float getShadowSize() {
        return 0.0F;
    }

    @Override
    public float getBrightness(float f) {
        return 1.0F;
    }

    @Override
    public int getBrightnessForRender(float f) {
        return 15728880;
    }
}
