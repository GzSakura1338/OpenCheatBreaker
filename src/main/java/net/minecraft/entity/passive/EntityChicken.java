package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityChicken extends EntityAnimal {
    public float flapProgress;
    public float destPos;
    public float prevMaxWingDeviation;
    public float prevFlapProgress;
    public float flapSpeed = 1.0F;
    public int timeUntilNextEgg;
    public boolean jockey;

    public EntityChicken(World world) {
        super(world);
        this.setSize(0.3F, 0.7F);
        this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.4));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0));
        this.tasks.addTask(3, new EntityAITempt(this, 1.0, Items.wheat_seeds, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        super.onLivingUpdate();
        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.destPos;
        this.destPos = (float)(this.destPos + (this.onGround ? -1 : 4) * 0.3);
        if (this.destPos < 0.0F) {
            this.destPos = 0.0F;
        }

        if (this.destPos > 1.0F) {
            this.destPos = 1.0F;
        }

        if (!this.onGround && this.flapSpeed < 1.0F) {
            this.flapSpeed = 1.0F;
        }

        this.flapSpeed = (float)(this.flapSpeed * 0.9);
        if (!this.onGround && this.motionY < 0.0) {
            this.motionY *= 0.6;
        }

        this.flapProgress = this.flapProgress + this.flapSpeed * 2.0F;
        if (!this.worldObj.isRemote && !this.isChild() && !this.hasJockey() && --this.timeUntilNextEgg <= 0) {
            this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.dropItem(Items.egg, 1);
            this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        }
    }

    @Override
    public void fall(float distance) {
    }

    @Override
    public String getLivingSound() {
        return "mob.chicken.say";
    }

    @Override
    public String getHurtSound() {
        return "mob.chicken.hurt";
    }

    @Override
    public String getDeathSound() {
        return "mob.chicken.hurt";
    }

    @Override
    public void playStepSound(int x, int y, int z, Block blockIn) {
        this.playSound("mob.chicken.step", 0.15F, 1.0F);
    }

    @Override
    public Item getDropItem() {
        return Items.feather;
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        int n2 = this.rand.nextInt(3) + this.rand.nextInt(1 + n);

        for (int i = 0; i < n2; i++) {
            this.dropItem(Items.feather, 1);
        }

        if (this.isBurning()) {
            this.dropItem(Items.cooked_chicken, 1);
        } else {
            this.dropItem(Items.chicken, 1);
        }
    }

    public EntityChicken createChild(EntityAgeable entityAgeable) {
        return new EntityChicken(this.worldObj);
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() instanceof ItemSeeds;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.jockey = tagCompund.getBoolean("IsChickenJockey");
    }

    @Override
    public int getExperiencePoints(EntityPlayer entityPlayer) {
        return this.hasJockey() ? 10 : super.getExperiencePoints(entityPlayer);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("IsChickenJockey", this.jockey);
    }

    @Override
    public boolean canDespawn() {
        return this.hasJockey() && this.riddenByEntity == null;
    }

    @Override
    public void updateRiderPosition() {
        super.updateRiderPosition();
        float sin = MathHelper.sin(this.renderYawOffset * (float) Math.PI / 180.0F);
        float cos = MathHelper.cos(this.renderYawOffset * (float) Math.PI / 180.0F);
        float f = 0.1F;
        float f2 = 0.0F;
        this.riddenByEntity
            .setPosition(this.posX + f * sin, this.posY + this.height * 0.5F + this.riddenByEntity.getYOffset() + f2, this.posZ - f * cos);
        if (this.riddenByEntity instanceof EntityLivingBase) {
            ((EntityLivingBase)this.riddenByEntity).renderYawOffset = this.renderYawOffset;
        }
    }

    public boolean hasJockey() {
        return this.jockey;
    }

    public void setHasJockey(boolean flag) {
        this.jockey = flag;
    }
}
