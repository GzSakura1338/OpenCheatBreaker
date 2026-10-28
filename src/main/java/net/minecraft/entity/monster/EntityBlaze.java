package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBlaze extends EntityMob {
    public float heightOffset = 0.5F;
    public int heightOffsetUpdateTime;
    public int intValue;

    public EntityBlaze(World world) {
        super(world);
        this.isImmuneToFire = true;
        this.experienceValue = 10;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    @Override
    public String getLivingSound() {
        return "mob.blaze.breathe";
    }

    @Override
    public String getHurtSound() {
        return "mob.blaze.hit";
    }

    @Override
    public String getDeathSound() {
        return "mob.blaze.death";
    }

    @Override
    public int getBrightnessForRender(float f) {
        return 15728880;
    }

    @Override
    public float getBrightness(float f) {
        return 1.0F;
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        if (!this.worldObj.isRemote) {
            if (this.isWet()) {
                this.attackEntityFrom(DamageSource.drown, 1.0F);
            }

            this.heightOffsetUpdateTime--;
            if (this.heightOffsetUpdateTime <= 0) {
                this.heightOffsetUpdateTime = 100;
                this.heightOffset = 0.5F + (float)this.rand.nextGaussian() * 3.0F;
            }

            if (this.getEntityToAttack() != null
                && this.getEntityToAttack().posY + this.getEntityToAttack().getEyeHeight() > this.posY + this.getEyeHeight() + this.heightOffset) {
                this.motionY = this.motionY + (0.3F - this.motionY) * 0.3F;
            }
        }

        if (this.rand.nextInt(24) == 0) {
            this.worldObj
                .playSoundEffect(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "fire.fire", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F);
        }

        if (!this.onGround && this.motionY < 0.0) {
            this.motionY *= 0.6;
        }

        for (int i = 0; i < 2; i++) {
            this.worldObj
                .spawnParticle(
                    "largesmoke",
                    this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                    this.posY + this.rand.nextDouble() * this.height,
                    this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                    0.0,
                    0.0,
                    0.0
                );
        }

        super.onLivingUpdate();
    }

    @Override
    public void attackEntity(Entity entity, float f) {
        if (this.attackTime <= 0 && f < 2.0F && entity.boundingBox.maxY > this.boundingBox.minY && entity.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            this.attackEntityAsMob(entity);
        } else if (f < 30.0F) {
            double d = entity.posX - this.posX;
            double d2 = entity.boundingBox.minY + entity.height / 2.0F - (this.posY + this.height / 2.0F);
            double d3 = entity.posZ - this.posZ;
            if (this.attackTime == 0) {
                this.intValue++;
                if (this.intValue == 1) {
                    this.attackTime = 60;
                    this.setFireActive(true);
                } else if (this.intValue <= 4) {
                    this.attackTime = 6;
                } else {
                    this.attackTime = 100;
                    this.intValue = 0;
                    this.setFireActive(false);
                }

                if (this.intValue > 1) {
                    float f2 = MathHelper.sqrt_float(f) * 0.5F;
                    this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);

                    for (int i = 0; i < 1; i++) {
                        EntitySmallFireball entitySmallFireball = new EntitySmallFireball(
                            this.worldObj, this, d + this.rand.nextGaussian() * f2, d2, d3 + this.rand.nextGaussian() * f2
                        );
                        entitySmallFireball.posY = this.posY + this.height / 2.0F + 0.5;
                        this.worldObj.spawnEntityInWorld(entitySmallFireball);
                    }
                }
            }

            this.rotationYaw = (float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
            this.hasAttacked = true;
        }
    }

    @Override
    public void fall(float distance) {
    }

    @Override
    public Item getDropItem() {
        return Items.blaze_rod;
    }

    @Override
    public boolean isBurning() {
        return this.isFireActive();
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        if (flag) {
            int nextInt = this.rand.nextInt(2 + n);

            for (int i = 0; i < nextInt; i++) {
                this.dropItem(Items.blaze_rod, 1);
            }
        }
    }

    public boolean isFireActive() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setFireActive(boolean flag) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
        if (flag) {
            watchableObjectByte = (byte)(watchableObjectByte | 1);
        } else {
            watchableObjectByte = (byte)(watchableObjectByte & -2);
        }

        this.dataWatcher.updateObject(16, watchableObjectByte);
    }

    @Override
    public boolean isValidLightLevel() {
        return true;
    }
}
