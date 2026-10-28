package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGhast extends EntityFlying implements IMob {
    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    public Entity targetedEntity;
    public int aggroCooldown;
    public int prevAttackCounter;
    public int attackCounter;
    public int explosionStrength = 1;

    public EntityGhast(World world) {
        super(world);
        this.setSize(4.0F, 4.0F);
        this.isImmuneToFire = true;
        this.experienceValue = 5;
    }

    public boolean isShooting() {
        return this.dataWatcher.getWatchableObjectByte(16) != 0;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        } else if ("fireball".equals(source.getDamageType()) && source.getEntity() instanceof EntityPlayer) {
            super.attackEntityFrom(source, 1000.0F);
            ((EntityPlayer)source.getEntity()).triggerAchievement(AchievementList.ghast);
            return true;
        } else {
            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)0);
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
    }

    @Override
    public void updateEntityActionState() {
        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            this.setDead();
        }

        this.despawnEntity();
        this.prevAttackCounter = this.attackCounter;
        double d = this.waypointX - this.posX;
        double d2 = this.waypointY - this.posY;
        double d3 = this.waypointZ - this.posZ;
        double d4 = d * d + d2 * d2 + d3 * d3;
        if (d4 < 1.0 || d4 > 3600.0) {
            this.waypointX = this.posX + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
            this.waypointY = this.posY + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
            this.waypointZ = this.posZ + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
        }

        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown = this.courseChangeCooldown + this.rand.nextInt(5) + 2;
            d4 = MathHelper.sqrt_double(d4);
            if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, d4)) {
                this.motionX += d / d4 * 0.1;
                this.motionY += d2 / d4 * 0.1;
                this.motionZ += d3 / d4 * 0.1;
            } else {
                this.waypointX = this.posX;
                this.waypointY = this.posY;
                this.waypointZ = this.posZ;
            }
        }

        if (this.targetedEntity != null && this.targetedEntity.isDead) {
            this.targetedEntity = null;
        }

        if (this.targetedEntity == null || this.aggroCooldown-- <= 0) {
            this.targetedEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, 100.0);
            if (this.targetedEntity != null) {
                this.aggroCooldown = 20;
            }
        }

        double d5 = 64.0;
        if (this.targetedEntity != null && this.targetedEntity.getDistanceSqToEntity(this) < d5 * d5) {
            double d6 = this.targetedEntity.posX - this.posX;
            double d7 = this.targetedEntity.boundingBox.minY + this.targetedEntity.height / 2.0F - (this.posY + this.height / 2.0F);
            double d8 = this.targetedEntity.posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(d6, d8)) * 180.0F / (float) Math.PI;
            if (this.canEntityBeSeen(this.targetedEntity)) {
                if (this.attackCounter == 10) {
                    this.worldObj.playAuxSFXAtEntity(null, 1007, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                }

                this.attackCounter++;
                if (this.attackCounter == 20) {
                    this.worldObj.playAuxSFXAtEntity(null, 1008, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    EntityLargeFireball entityLargeFireball = new EntityLargeFireball(this.worldObj, this, d6, d7, d8);
                    entityLargeFireball.explosionPower = this.explosionStrength;
                    double d9 = 4.0;
                    Vec3 look = this.getLook(1.0F);
                    entityLargeFireball.posX = this.posX + look.xCoord * d9;
                    entityLargeFireball.posY = this.posY + this.height / 2.0F + 0.5;
                    entityLargeFireball.posZ = this.posZ + look.zCoord * d9;
                    this.worldObj.spawnEntityInWorld(entityLargeFireball);
                    this.attackCounter = -40;
                }
            } else if (this.attackCounter > 0) {
                this.attackCounter--;
            }
        } else {
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI;
            if (this.attackCounter > 0) {
                this.attackCounter--;
            }
        }

        if (!this.worldObj.isRemote) {
            byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
            byte b = (byte)(this.attackCounter > 10 ? 1 : 0);
            if (watchableObjectByte != b) {
                this.dataWatcher.updateObject(16, b);
            }
        }
    }

    public boolean isCourseTraversable(double d, double d2, double d3, double d4) {
        double d5 = (this.waypointX - this.posX) / d4;
        double d6 = (this.waypointY - this.posY) / d4;
        double d7 = (this.waypointZ - this.posZ) / d4;
        AxisAlignedBB axisAlignedBB = this.boundingBox.copy();

        for (int i = 1; i < d4; i++) {
            axisAlignedBB.offset(d5, d6, d7);
            if (!this.worldObj.getCollidingBoundingBoxes(this, axisAlignedBB).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getLivingSound() {
        return "mob.ghast.moan";
    }

    @Override
    public String getHurtSound() {
        return "mob.ghast.scream";
    }

    @Override
    public String getDeathSound() {
        return "mob.ghast.death";
    }

    @Override
    public Item getDropItem() {
        return Items.gunpowder;
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        int n2 = this.rand.nextInt(2) + this.rand.nextInt(1 + n);

        for (int i = 0; i < n2; i++) {
            this.dropItem(Items.ghast_tear, 1);
        }

        n2 = this.rand.nextInt(3) + this.rand.nextInt(1 + n);

        for (int i = 0; i < n2; i++) {
            this.dropItem(Items.gunpowder, 1);
        }
    }

    @Override
    public float getSoundVolume() {
        return 10.0F;
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 1;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("ExplosionPower", this.explosionStrength);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        if (tagCompund.hasKey("ExplosionPower", 99)) {
            this.explosionStrength = tagCompund.getInteger("ExplosionPower");
        }
    }
}
