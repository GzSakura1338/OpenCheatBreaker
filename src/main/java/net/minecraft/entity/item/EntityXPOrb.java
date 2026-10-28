package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityXPOrb extends Entity {
    public int xpColor;
    public int xpOrbAge;
    public int pickupDelay;
    public int xpOrbHealth = 5;
    public int xpValue;
    public EntityPlayer closestPlayer;
    public int xpTargetColor;

    public EntityXPOrb(World world, double d, double d2, double d3, int n) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(d, d2, d3);
        this.rotationYaw = (float)(Math.random() * 360.0);
        this.motionX = (float)(Math.random() * 0.2F - 0.1F) * 2.0F;
        this.motionY = (float)(Math.random() * 0.2) * 2.0F;
        this.motionZ = (float)(Math.random() * 0.2F - 0.1F) * 2.0F;
        this.xpValue = n;
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    public EntityXPOrb(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
    }

    @Override
    public void entityInit() {
    }

    @Override
    public int getBrightnessForRender(float f) {
        float f2 = 0.5F;
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        int brightnessForRender = super.getBrightnessForRender(f);
        int n = brightnessForRender & 0xFF;
        int n2 = brightnessForRender >> 16 & 0xFF;
        n += (int)(f2 * 15.0F * 16.0F);
        if (n > 240) {
            n = 240;
        }

        return n | n2 << 16;
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (this.pickupDelay > 0) {
            this.pickupDelay--;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03F;
        if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial()
            == Material.lava) {
            this.motionY = 0.2F;
            this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
            this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
            this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        this.pushOutOfBlocks(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0, this.posZ);
        double d = 8.0;
        if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
            if (this.closestPlayer == null || this.closestPlayer.getDistanceSqToEntity(this) > d * d) {
                this.closestPlayer = this.worldObj.getClosestPlayerToEntity(this, d);
            }

            this.xpTargetColor = this.xpColor;
        }

        if (this.closestPlayer != null) {
            double d2 = (this.closestPlayer.posX - this.posX) / d;
            double d3 = (this.closestPlayer.posY + this.closestPlayer.getEyeHeight() - this.posY) / d;
            double d4 = (this.closestPlayer.posZ - this.posZ) / d;
            double sqrt = Math.sqrt(d2 * d2 + d3 * d3 + d4 * d4);
            double d5 = 1.0 - sqrt;
            if (d5 > 0.0) {
                d5 *= d5;
                this.motionX += d2 / sqrt * d5 * 0.1;
                this.motionY += d3 / sqrt * d5 * 0.1;
                this.motionZ += d4 / sqrt * d5 * 0.1;
            }
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        float f = 0.98F;
        if (this.onGround) {
            f = this.worldObj
                    .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ))
                    .slipperiness
                * 0.98F;
        }

        this.motionX *= f;
        this.motionY *= 0.98F;
        this.motionZ *= f;
        if (this.onGround) {
            this.motionY *= -0.9F;
        }

        this.xpColor++;
        this.xpOrbAge++;
        if (this.xpOrbAge >= 6000) {
            this.setDead();
        }
    }

    @Override
    public boolean handleWaterMovement() {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
    }

    @Override
    public void dealFireDamage(int amount) {
        this.attackEntityFrom(DamageSource.inFire, amount);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        this.setBeenAttacked();
        this.xpOrbHealth = (int)(this.xpOrbHealth - amount);
        if (this.xpOrbHealth <= 0) {
            this.setDead();
        }

        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setShort("Health", (byte)this.xpOrbHealth);
        tagCompound.setShort("Age", (short)this.xpOrbAge);
        tagCompound.setShort("Value", (short)this.xpValue);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.xpOrbHealth = tagCompund.getShort("Health") & 255;
        this.xpOrbAge = tagCompund.getShort("Age");
        this.xpValue = tagCompund.getShort("Value");
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        if (!this.worldObj.isRemote && this.pickupDelay == 0 && entityIn.xpCooldown == 0) {
            entityIn.xpCooldown = 2;
            this.worldObj.playSoundAtEntity(entityIn, "random.orb", 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
            entityIn.onItemPickup(this, 1);
            entityIn.addExperience(this.xpValue);
            this.setDead();
        }
    }

    public int getXpValue() {
        return this.xpValue;
    }

    public int getTextureByXP() {
        return this.xpValue >= 2477
            ? 10
            : (
                this.xpValue >= 1237
                    ? 9
                    : (
                        this.xpValue >= 617
                            ? 8
                            : (
                                this.xpValue >= 307
                                    ? 7
                                    : (
                                        this.xpValue >= 149
                                            ? 6
                                            : (
                                                this.xpValue >= 73
                                                    ? 5
                                                    : (this.xpValue >= 37 ? 4 : (this.xpValue >= 17 ? 3 : (this.xpValue >= 7 ? 2 : (this.xpValue >= 3 ? 1 : 0))))
                                            )
                                    )
                            )
                    )
            );
    }

    public static int getXPSplit(int n) {
        return n >= 2477
            ? 2477
            : (
                n >= 1237
                    ? 1237
                    : (
                        n >= 617
                            ? 617
                            : (
                                n >= 307
                                    ? 307
                                    : (n >= 149 ? 149 : (n >= 73 ? 73 : (n >= 37 ? 37 : (n >= 17 ? 17 : (n >= 7 ? 7 : (n >= 3 ? 3 : 1))))))
                            )
                    )
            );
    }

    @Override
    public boolean canAttackWithItem() {
        return false;
    }
}
