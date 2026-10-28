package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public abstract class EntityThrowable extends Entity implements IProjectile {
    public int xTile = -1;
    public int yTile = -1;
    public int zTile = -1;
    public Block inBlock;
    public boolean inGround;
    public int throwableShake;
    public EntityLivingBase thrower;
    public String throwerName;
    public int ticksInGround;
    public int ticksInAir;

    public EntityThrowable(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
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

    public EntityThrowable(World world, EntityLivingBase entityLivingBase) {
        super(world);
        this.thrower = entityLivingBase;
        this.setSize(0.25F, 0.25F);
        this.setLocationAndAngles(entityLivingBase.posX, entityLivingBase.posY + entityLivingBase.getEyeHeight(), entityLivingBase.posZ, entityLivingBase.rotationYaw, entityLivingBase.rotationPitch);
        this.posX = this.posX - MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        this.posY -= 0.1F;
        this.posZ = this.posZ - MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        float f = 0.4F;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
        this.motionY = -MathHelper.sin((this.rotationPitch + this.constantFloat2()) / 180.0F * (float) Math.PI) * f;
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, this.constantFloat(), 1.0F);
    }

    public EntityThrowable(World world, double d, double d2, double d3) {
        super(world);
        this.ticksInGround = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(d, d2, d3);
        this.yOffset = 0.0F;
    }

    public float constantFloat() {
        return 1.5F;
    }

    public float constantFloat2() {
        return 0.0F;
    }

    @Override
    public void setThrowableHeading(double d, double d2, double d3, float f, float f2) {
        float f3 = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
        d /= f3;
        d2 /= f3;
        d3 /= f3;
        d += this.rand.nextGaussian() * 0.0075F * f2;
        d2 += this.rand.nextGaussian() * 0.0075F * f2;
        d3 += this.rand.nextGaussian() * 0.0075F * f2;
        d *= f;
        d2 *= f;
        d3 *= f;
        this.motionX = d;
        this.motionY = d2;
        this.motionZ = d3;
        float f4 = MathHelper.sqrt_double(d * d + d3 * d3);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(d, d3) * 180.0 / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(d2, f4) * 180.0 / Math.PI);
        this.ticksInGround = 0;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(x * x + z * z);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, f) * 180.0 / Math.PI);
        }
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.onUpdate();
        if (this.throwableShake > 0) {
            this.throwableShake--;
        }

        if (this.inGround) {
            if (this.worldObj.getBlock(this.xTile, this.yTile, this.zTile) == this.inBlock) {
                this.ticksInGround++;
                if (this.ticksInGround == 1200) {
                    this.setDead();
                }

                return;
            }

            this.inGround = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
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

        if (!this.worldObj.isRemote) {
            Entity entity = null;
            List list = this.worldObj
                .getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            double d = 0.0;
            EntityLivingBase thrower = this.getThrower();

            for (int i = 0; i < list.size(); i++) {
                Entity entity2 = (Entity)list.get(i);
                if (entity2.canBeCollidedWith() && (entity2 != thrower || this.ticksInAir >= 5)) {
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
        }

        if (rayTraceBlocks != null) {
            if (rayTraceBlocks.typeOfHit == MovingObjectType.BLOCK && this.worldObj.getBlock(rayTraceBlocks.blockX, rayTraceBlocks.blockY, rayTraceBlocks.blockZ) == Blocks.portal) {
                this.setInPortal();
            } else {
                this.onImpact(rayTraceBlocks);
            }
        }

        this.posX = this.posX + this.motionX;
        this.posY = this.posY + this.motionY;
        this.posZ = this.posZ + this.motionZ;
        float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
        this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0 / Math.PI);

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
        float f2 = 0.99F;
        float gravityVelocity = this.getGravityVelocity();
        if (this.isInWater()) {
            for (int i = 0; i < 4; i++) {
                float f3 = 0.25F;
                this.worldObj
                    .spawnParticle(
                        "bubble",
                        this.posX - this.motionX * f3,
                        this.posY - this.motionY * f3,
                        this.posZ - this.motionZ * f3,
                        this.motionX,
                        this.motionY,
                        this.motionZ
                    );
            }

            f2 = 0.8F;
        }

        this.motionX *= f2;
        this.motionY *= f2;
        this.motionZ *= f2;
        this.motionY -= gravityVelocity;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public float getGravityVelocity() {
        return 0.03F;
    }

    public abstract void onImpact(MovingObjectPosition movingObjectPosition);

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setShort("xTile", (short)this.xTile);
        tagCompound.setShort("yTile", (short)this.yTile);
        tagCompound.setShort("zTile", (short)this.zTile);
        tagCompound.setByte("inTile", (byte)Block.getIdFromBlock(this.inBlock));
        tagCompound.setByte("shake", (byte)this.throwableShake);
        tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        if ((this.throwerName == null || this.throwerName.length() == 0) && this.thrower != null && this.thrower instanceof EntityPlayer) {
            this.throwerName = this.thrower.getCommandSenderName();
        }

        tagCompound.setString("ownerName", this.throwerName == null ? "" : this.throwerName);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.xTile = tagCompund.getShort("xTile");
        this.yTile = tagCompund.getShort("yTile");
        this.zTile = tagCompund.getShort("zTile");
        this.inBlock = Block.getBlockById(tagCompund.getByte("inTile") & 255);
        this.throwableShake = tagCompund.getByte("shake") & 255;
        this.inGround = tagCompund.getByte("inGround") == 1;
        this.throwerName = tagCompund.getString("ownerName");
        if (this.throwerName != null && this.throwerName.length() == 0) {
            this.throwerName = null;
        }
    }

    @Override
    public float getShadowSize() {
        return 0.0F;
    }

    public EntityLivingBase getThrower() {
        if (this.thrower == null && this.throwerName != null && this.throwerName.length() > 0) {
            this.thrower = this.worldObj.getPlayerEntityByName(this.throwerName);
        }

        return this.thrower;
    }
}
