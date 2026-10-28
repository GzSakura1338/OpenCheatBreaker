package net.minecraft.entity.passive;

import java.util.Calendar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBat extends EntityAmbientCreature {
    public ChunkCoordinates spawnPosition;

    public EntityBat(World world) {
        super(world);
        this.setSize(0.5F, 0.9F);
        this.setIsBatHanging(true);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    @Override
    public float getSoundVolume() {
        return 0.1F;
    }

    @Override
    public float getSoundPitch() {
        return super.getSoundPitch() * 0.95F;
    }

    @Override
    public String getLivingSound() {
        return this.getIsBatHanging() && this.rand.nextInt(4) != 0 ? null : "mob.bat.idle";
    }

    @Override
    public String getHurtSound() {
        return "mob.bat.hurt";
    }

    @Override
    public String getDeathSound() {
        return "mob.bat.death";
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void collideWithEntity(Entity entity) {
    }

    @Override
    public void collideWithNearbyEntities() {
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0);
    }

    public boolean getIsBatHanging() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setIsBatHanging(boolean flag) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
        if (flag) {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 1));
        } else {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -2));
        }
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (this.getIsBatHanging()) {
            this.motionX = this.motionY = this.motionZ = 0.0;
            this.posY = MathHelper.floor_double(this.posY) + 1.0 - this.height;
        } else {
            this.motionY *= 0.6F;
        }
    }

    @Override
    public void updateAITasks() {
        super.updateAITasks();
        if (this.getIsBatHanging()) {
            if (!this.worldObj.getBlock(MathHelper.floor_double(this.posX), (int)this.posY + 1, MathHelper.floor_double(this.posZ)).isNormalCube()) {
                this.setIsBatHanging(false);
                this.worldObj.playAuxSFXAtEntity(null, 1015, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            } else {
                if (this.rand.nextInt(200) == 0) {
                    this.rotationYawHead = this.rand.nextInt(360);
                }

                if (this.worldObj.getClosestPlayerToEntity(this, 4.0) != null) {
                    this.setIsBatHanging(false);
                    this.worldObj.playAuxSFXAtEntity(null, 1015, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                }
            }
        } else {
            if (this.spawnPosition != null
                && (!this.worldObj.isAirBlock(this.spawnPosition.posX, this.spawnPosition.posY, this.spawnPosition.posZ) || this.spawnPosition.posY < 1)) {
                this.spawnPosition = null;
            }

            if (this.spawnPosition == null
                || this.rand.nextInt(30) == 0
                || this.spawnPosition.getDistanceSquared((int)this.posX, (int)this.posY, (int)this.posZ) < 4.0F) {
                this.spawnPosition = new ChunkCoordinates(
                    (int)this.posX + this.rand.nextInt(7) - this.rand.nextInt(7),
                    (int)this.posY + this.rand.nextInt(6) - 2,
                    (int)this.posZ + this.rand.nextInt(7) - this.rand.nextInt(7)
                );
            }

            double d = this.spawnPosition.posX + 0.5 - this.posX;
            double d2 = this.spawnPosition.posY + 0.1 - this.posY;
            double d3 = this.spawnPosition.posZ + 0.5 - this.posZ;
            this.motionX = this.motionX + (Math.signum(d) * 0.5 - this.motionX) * 0.1F;
            this.motionY = this.motionY + (Math.signum(d2) * 0.7F - this.motionY) * 0.1F;
            this.motionZ = this.motionZ + (Math.signum(d3) * 0.5 - this.motionZ) * 0.1F;
            float f = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0 / Math.PI) - 90.0F;
            float f2 = MathHelper.wrapAngleTo180_float(f - this.rotationYaw);
            this.moveForward = 0.5F;
            this.rotationYaw += f2;
            if (this.rand.nextInt(100) == 0
                && this.worldObj.getBlock(MathHelper.floor_double(this.posX), (int)this.posY + 1, MathHelper.floor_double(this.posZ)).isNormalCube()) {
                this.setIsBatHanging(true);
            }
        }
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void fall(float distance) {
    }

    @Override
    public void updateFallState(double distanceFallenThisTick, boolean isOnGround) {
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        if (!this.worldObj.isRemote && this.getIsBatHanging()) {
            this.setIsBatHanging(false);
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.dataWatcher.updateObject(16, tagCompund.getByte("BatFlags"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setByte("BatFlags", this.dataWatcher.getWatchableObjectByte(16));
    }

    @Override
    public boolean getCanSpawnHere() {
        int n = MathHelper.floor_double(this.boundingBox.minY);
        if (n >= 63) {
            return false;
        }

        int n2 = MathHelper.floor_double(this.posX);
        int n3 = MathHelper.floor_double(this.posZ);
        int blockLightValue = this.worldObj.getBlockLightValue(n2, n, n3);
        byte b = 4;
        Calendar currentDate = this.worldObj.getCurrentDate();
        if ((currentDate.get(2) + 1 != 10 || currentDate.get(5) < 20) && (currentDate.get(2) + 1 != 11 || currentDate.get(5) > 3)) {
            if (this.rand.nextBoolean()) {
                return false;
            }
        } else {
            b = 7;
        }

        return blockLightValue <= this.rand.nextInt(b) && super.getCanSpawnHere();
    }
}
