package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntitySpider extends EntityMob {
    public EntitySpider(World world) {
        super(world);
        this.setSize(1.4F, 0.9F);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            this.setBesideClimbableBlock(this.isCollidedHorizontally);
        }
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.8F);
    }

    @Override
    public Entity findPlayerToAttack() {
        float brightness = this.getBrightness(1.0F);
        if (brightness < 0.5F) {
            double d = 16.0;
            return this.worldObj.getClosestVulnerablePlayerToEntity(this, d);
        } else {
            return null;
        }
    }

    @Override
    public String getLivingSound() {
        return "mob.spider.say";
    }

    @Override
    public String getHurtSound() {
        return "mob.spider.say";
    }

    @Override
    public String getDeathSound() {
        return "mob.spider.death";
    }

    @Override
    public void playStepSound(int x, int y, int z, Block blockIn) {
        this.playSound("mob.spider.step", 0.15F, 1.0F);
    }

    @Override
    public void attackEntity(Entity entity, float f) {
        float brightness = this.getBrightness(1.0F);
        if (brightness > 0.5F && this.rand.nextInt(100) == 0) {
            this.entityToAttack = null;
        } else if (!(f > 2.0F) || !(f < 6.0F) || this.rand.nextInt(10) != 0) {
            super.attackEntity(entity, f);
        } else if (this.onGround) {
            double d = entity.posX - this.posX;
            double d2 = entity.posZ - this.posZ;
            float f2 = MathHelper.sqrt_double(d * d + d2 * d2);
            this.motionX = d / f2 * 0.5 * 0.8F + this.motionX * 0.2F;
            this.motionZ = d2 / f2 * 0.5 * 0.8F + this.motionZ * 0.2F;
            this.motionY = 0.4F;
        }
    }

    @Override
    public Item getDropItem() {
        return Items.string;
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        super.dropFewItems(flag, n);
        if (flag && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + n) > 0)) {
            this.dropItem(Items.spider_eye, 1);
        }
    }

    @Override
    public boolean isOnLadder() {
        return this.isBesideClimbableBlock();
    }

    @Override
    public void setInWeb() {
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        return potionEffect.getPotionID() != Potion.poison.id && super.isPotionApplicable(potionEffect);
    }

    public boolean isBesideClimbableBlock() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean flag) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
        if (flag) {
            watchableObjectByte = (byte)(watchableObjectByte | 1);
        } else {
            watchableObjectByte = (byte)(watchableObjectByte & -2);
        }

        this.dataWatcher.updateObject(16, watchableObjectByte);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData) {
        IEntityLivingData onSpawnWithEgg = super.onSpawnWithEgg(entityLivingData);
        if (this.worldObj.rand.nextInt(100) == 0) {
            EntitySkeleton entitySkeleton = new EntitySkeleton(this.worldObj);
            entitySkeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            entitySkeleton.onSpawnWithEgg(null);
            this.worldObj.spawnEntityInWorld(entitySkeleton);
            entitySkeleton.mountEntity(this);
        }

        if (onSpawnWithEgg == null) {
            onSpawnWithEgg = new EntitySpider.GroupData();
            if (this.worldObj.difficultySetting == EnumDifficulty.HARD
                && this.worldObj.rand.nextFloat() < 0.1F * this.worldObj.getTensionFactorForBlock(this.posX, this.posY, this.posZ)) {
                ((EntitySpider.GroupData)onSpawnWithEgg).handleRandom(this.worldObj.rand);
            }
        }

        if (onSpawnWithEgg instanceof EntitySpider.GroupData) {
            int intValue = ((EntitySpider.GroupData)onSpawnWithEgg).intValue;
            if (intValue > 0 && Potion.potionTypes[intValue] != null) {
                this.addPotionEffect(new PotionEffect(intValue, Integer.MAX_VALUE));
            }
        }

        return onSpawnWithEgg;
    }

    public static class GroupData implements IEntityLivingData {
        public int intValue;

        public void handleRandom(Random random) {
            int nextInt = random.nextInt(5);
            if (nextInt <= 1) {
                this.intValue = Potion.moveSpeed.id;
            } else if (nextInt <= 2) {
                this.intValue = Potion.damageBoost.id;
            } else if (nextInt <= 3) {
                this.intValue = Potion.regeneration.id;
            } else if (nextInt <= 4) {
                this.intValue = Potion.invisibility.id;
            }
        }
    }
}
