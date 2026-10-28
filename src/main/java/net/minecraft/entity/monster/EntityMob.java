package net.minecraft.entity.monster;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public abstract class EntityMob extends EntityCreature implements IMob {
    public EntityMob(World world) {
        super(world);
        this.experienceValue = 5;
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        this.updateArmSwingProgress();
        float brightness = this.getBrightness(1.0F);
        if (brightness > 0.5F) {
            this.entityAge += 2;
        }

        super.onLivingUpdate();
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            this.setDead();
        }
    }

    @Override
    public String getSwimSound() {
        return "game.hostile.swim";
    }

    @Override
    public String getSplashSound() {
        return "game.hostile.swim.splash";
    }

    @Override
    public Entity findPlayerToAttack() {
        EntityPlayer closestVulnerablePlayerToEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0);
        return closestVulnerablePlayerToEntity != null && this.canEntityBeSeen(closestVulnerablePlayerToEntity) ? closestVulnerablePlayerToEntity : null;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        if (super.attackEntityFrom(source, amount)) {
            Entity entity = source.getEntity();
            if (this.riddenByEntity != entity && this.ridingEntity != entity) {
                if (entity != this) {
                    this.entityToAttack = entity;
                }

                return true;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public String getHurtSound() {
        return "game.hostile.hurt";
    }

    @Override
    public String getDeathSound() {
        return "game.hostile.die";
    }

    @Override
    public String formatText(int n) {
        return n > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        float attributeValue = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int n = 0;
        if (entity instanceof EntityLivingBase) {
            attributeValue += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)entity);
            n += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)entity);
        }

        boolean attackEntityFrom = entity.attackEntityFrom(DamageSource.causeMobDamage(this), attributeValue);
        if (attackEntityFrom) {
            if (n > 0) {
                entity.addVelocity(
                    -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * n * 0.5F,
                    0.1,
                    MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * n * 0.5F
                );
                this.motionX *= 0.6;
                this.motionZ *= 0.6;
            }

            int fireAspectModifier = EnchantmentHelper.getFireAspectModifier(this);
            if (fireAspectModifier > 0) {
                entity.setFire(fireAspectModifier * 4);
            }

            if (entity instanceof EntityLivingBase) {
                EnchantmentHelper.onUserDamaged((EntityLivingBase)entity, this);
            }

            EnchantmentHelper.onTargetDamaged(this, entity);
        }

        return attackEntityFrom;
    }

    @Override
    public void attackEntity(Entity entity, float f) {
        if (this.attackTime <= 0 && f < 2.0F && entity.boundingBox.maxY > this.boundingBox.minY && entity.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            this.attackEntityAsMob(entity);
        }
    }

    @Override
    public float getBlockPathWeight(int n, int n2, int n3) {
        return 0.5F - this.worldObj.getLightBrightness(n, n2, n3);
    }

    public boolean isValidLightLevel() {
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.boundingBox.minY);
        int n3 = MathHelper.floor_double(this.posZ);
        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, n, n2, n3) > this.rand.nextInt(32)) {
            return false;
        }

        int blockLightValue = this.worldObj.getBlockLightValue(n, n2, n3);
        if (this.worldObj.isThundering()) {
            int skylightSubtracted = this.worldObj.skylightSubtracted;
            this.worldObj.skylightSubtracted = 10;
            blockLightValue = this.worldObj.getBlockLightValue(n, n2, n3);
            this.worldObj.skylightSubtracted = skylightSubtracted;
        }

        return blockLightValue <= this.rand.nextInt(8);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL && this.isValidLightLevel() && super.getCanSpawnHere();
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
    }

    @Override
    public boolean isChild2() {
        return true;
    }
}
