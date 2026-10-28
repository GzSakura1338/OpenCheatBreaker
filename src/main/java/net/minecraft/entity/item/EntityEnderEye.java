package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityEnderEye extends Entity {
    public double targetX;
    public double targetY;
    public double targetZ;
    public int despawnTimer;
    public boolean shatterOrDrop;

    public EntityEnderEye(World world) {
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

    public EntityEnderEye(World world, double d, double d2, double d3) {
        super(world);
        this.despawnTimer = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(d, d2, d3);
        this.yOffset = 0.0F;
    }

    public void moveTowards(double d, int n, double d2) {
        double d3 = d - this.posX;
        double d4 = d2 - this.posZ;
        float f = MathHelper.sqrt_double(d3 * d3 + d4 * d4);
        if (f > 12.0F) {
            this.targetX = this.posX + d3 / f * 12.0;
            this.targetZ = this.posZ + d4 / f * 12.0;
            this.targetY = this.posY + 8.0;
        } else {
            this.targetX = d;
            this.targetY = n;
            this.targetZ = d2;
        }

        this.despawnTimer = 0;
        this.shatterOrDrop = this.rand.nextInt(5) > 0;
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
        if (!this.worldObj.isRemote) {
            double d = this.targetX - this.posX;
            double d2 = this.targetZ - this.posZ;
            float sqrt = (float)Math.sqrt(d * d + d2 * d2);
            float atan2 = (float)Math.atan2(d2, d);
            double d3 = f + (sqrt - f) * 0.0025;
            if (sqrt < 1.0F) {
                d3 *= 0.8;
                this.motionY *= 0.8;
            }

            this.motionX = Math.cos(atan2) * d3;
            this.motionZ = Math.sin(atan2) * d3;
            if (this.posY < this.targetY) {
                this.motionY = this.motionY + (1.0 - this.motionY) * 0.015F;
            } else {
                this.motionY = this.motionY + (-1.0 - this.motionY) * 0.015F;
            }
        }

        float f2 = 0.25F;
        if (this.isInWater()) {
            for (int i = 0; i < 4; i++) {
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
        } else {
            this.worldObj
                .spawnParticle(
                    "portal",
                    this.posX - this.motionX * f2 + this.rand.nextDouble() * 0.6 - 0.3,
                    this.posY - this.motionY * f2 - 0.5,
                    this.posZ - this.motionZ * f2 + this.rand.nextDouble() * 0.6 - 0.3,
                    this.motionX,
                    this.motionY,
                    this.motionZ
                );
        }

        if (!this.worldObj.isRemote) {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.despawnTimer++;
            if (this.despawnTimer > 80 && !this.worldObj.isRemote) {
                this.setDead();
                if (this.shatterOrDrop) {
                    this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Items.ender_eye)));
                } else {
                    this.worldObj.playAuxSFX(2003, (int)Math.round(this.posX), (int)Math.round(this.posY), (int)Math.round(this.posZ), 0);
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
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

    @Override
    public boolean canAttackWithItem() {
        return false;
    }
}
