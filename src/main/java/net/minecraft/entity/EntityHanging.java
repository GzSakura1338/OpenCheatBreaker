package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityHanging extends Entity {
    public int tickCounter1;
    public int hangingDirection;
    public int intValue;
    public int intValue2;
    public int intValue3;

    public EntityHanging(World world) {
        super(world);
        this.yOffset = 0.0F;
        this.setSize(0.5F, 0.5F);
    }

    public EntityHanging(World world, int n, int n2, int n3, int n4) {
        this(world);
        this.intValue = n;
        this.intValue2 = n2;
        this.intValue3 = n3;
    }

    @Override
    public void entityInit() {
    }

    public void setDirection(int n) {
        this.hangingDirection = n;
        this.prevRotationYaw = this.rotationYaw = n * 90;
        float widthPixels = this.getWidthPixels();
        float heightPixels = this.getHeightPixels();
        float widthPixels2 = this.getWidthPixels();
        if (n != 2 && n != 0) {
            widthPixels = 0.5F;
        } else {
            widthPixels2 = 0.5F;
            this.rotationYaw = this.prevRotationYaw = Direction.rotateOpposite[n] * 90;
        }

        widthPixels /= 32.0F;
        heightPixels /= 32.0F;
        widthPixels2 /= 32.0F;
        float f = this.intValue + 0.5F;
        float f2 = this.intValue2 + 0.5F;
        float f3 = this.intValue3 + 0.5F;
        float f4 = 0.5625F;
        if (n == 2) {
            f3 -= f4;
        }

        if (n == 1) {
            f -= f4;
        }

        if (n == 0) {
            f3 += f4;
        }

        if (n == 3) {
            f += f4;
        }

        if (n == 2) {
            f -= this.computeFloat(this.getWidthPixels());
        }

        if (n == 1) {
            f3 += this.computeFloat(this.getWidthPixels());
        }

        if (n == 0) {
            f += this.computeFloat(this.getWidthPixels());
        }

        if (n == 3) {
            f3 -= this.computeFloat(this.getWidthPixels());
        }

        f2 += this.computeFloat(this.getHeightPixels());
        this.setPosition(f, f2, f3);
        float f5 = -0.03125F;
        this.boundingBox.setBounds(f - widthPixels - f5, f2 - heightPixels - f5, f3 - widthPixels2 - f5, f + widthPixels + f5, f2 + heightPixels + f5, f3 + widthPixels2 + f5);
    }

    public float computeFloat(int n) {
        return n == 32 ? 0.5F : (n == 64 ? 0.5F : 0.0F);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.tickCounter1++ == 100 && !this.worldObj.isRemote) {
            this.tickCounter1 = 0;
            if (!this.isDead && !this.onValidSurface()) {
                this.setDead();
                this.onBroken(null);
            }
        }
    }

    public boolean onValidSurface() {
        if (!this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) {
            return false;
        }

        int max = Math.max(1, this.getWidthPixels() / 16);
        int max2 = Math.max(1, this.getHeightPixels() / 16);
        int intValue = this.intValue;
        int intValue2 = this.intValue2;
        int intValue3 = this.intValue3;
        if (this.hangingDirection == 2) {
            intValue = MathHelper.floor_double(this.posX - this.getWidthPixels() / 32.0F);
        }

        if (this.hangingDirection == 1) {
            intValue3 = MathHelper.floor_double(this.posZ - this.getWidthPixels() / 32.0F);
        }

        if (this.hangingDirection == 0) {
            intValue = MathHelper.floor_double(this.posX - this.getWidthPixels() / 32.0F);
        }

        if (this.hangingDirection == 3) {
            intValue3 = MathHelper.floor_double(this.posZ - this.getWidthPixels() / 32.0F);
        }

        intValue2 = MathHelper.floor_double(this.posY - this.getHeightPixels() / 32.0F);

        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max2; j++) {
                Material material;
                if (this.hangingDirection != 2 && this.hangingDirection != 0) {
                    material = this.worldObj.getBlock(this.intValue, intValue2 + j, intValue3 + i).getMaterial();
                } else {
                    material = this.worldObj.getBlock(intValue + i, intValue2 + j, this.intValue3).getMaterial();
                }

                if (!material.isSolid()) {
                    return false;
                }
            }
        }

        for (Entity entity : (Iterable<Entity>)this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox)) {
            if (entity instanceof EntityHanging) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean hitByEntity(Entity entityIn) {
        return entityIn instanceof EntityPlayer && this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityIn), 0.0F);
    }

    @Override
    public void process(int n) {
        this.worldObj.run2();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        if (!this.isDead && !this.worldObj.isRemote) {
            this.setDead();
            this.setBeenAttacked();
            this.onBroken(source.getEntity());
        }

        return true;
    }

    @Override
    public void moveEntity(double x, double y, double z) {
        if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0) {
            this.setDead();
            this.onBroken(null);
        }
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0) {
            this.setDead();
            this.onBroken(null);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setByte("Direction", (byte)this.hangingDirection);
        tagCompound.setInteger("TileX", this.intValue);
        tagCompound.setInteger("TileY", this.intValue2);
        tagCompound.setInteger("TileZ", this.intValue3);
        switch (this.hangingDirection) {
            case 0:
                tagCompound.setByte("Dir", (byte)2);
                break;
            case 1:
                tagCompound.setByte("Dir", (byte)1);
                break;
            case 2:
                tagCompound.setByte("Dir", (byte)0);
                break;
            case 3:
                tagCompound.setByte("Dir", (byte)3);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        if (tagCompund.hasKey("Direction", 99)) {
            this.hangingDirection = tagCompund.getByte("Direction");
        } else {
            switch (tagCompund.getByte("Dir")) {
                case 0:
                    this.hangingDirection = 2;
                    break;
                case 1:
                    this.hangingDirection = 1;
                    break;
                case 2:
                    this.hangingDirection = 0;
                    break;
                case 3:
                    this.hangingDirection = 3;
            }
        }

        this.intValue = tagCompund.getInteger("TileX");
        this.intValue2 = tagCompund.getInteger("TileY");
        this.intValue3 = tagCompund.getInteger("TileZ");
        this.setDirection(this.hangingDirection);
    }

    public abstract int getWidthPixels();

    public abstract int getHeightPixels();

    public abstract void onBroken(Entity entity);

    @Override
    public boolean shouldSetPosAfterLoading() {
        return false;
    }
}
