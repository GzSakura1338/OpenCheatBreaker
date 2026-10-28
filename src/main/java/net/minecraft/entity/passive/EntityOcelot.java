package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityOcelot extends EntityTameable {
    public EntityAITempt aiTempt;

    public EntityOcelot(World world) {
        super(world);
        this.setSize(0.6F, 0.8F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, this.aiTempt = new EntityAITempt(this, 0.6, Items.fish, true));
        this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntityPlayer.class, 16.0F, 0.8, 1.33));
        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0, 10.0F, 5.0F));
        this.tasks.addTask(6, new EntityAIOcelotSit(this, 1.33));
        this.tasks.addTask(7, new EntityAILeapAtTarget(this, 0.3F));
        this.tasks.addTask(8, new EntityAIOcelotAttack(this));
        this.tasks.addTask(9, new EntityAIMate(this, 0.8));
        this.tasks.addTask(10, new EntityAIWander(this, 0.8));
        this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        this.targetTasks.addTask(1, new EntityAITargetNonTamed(this, EntityChicken.class, 750, false));
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(18, (byte)0);
    }

    @Override
    public void updateAITick() {
        if (this.getMoveHelper().isUpdating()) {
            double speed = this.getMoveHelper().getSpeed();
            if (speed == 0.6) {
                this.setSneaking(true);
                this.setSprinting(false);
            } else if (speed == 1.33) {
                this.setSneaking(false);
                this.setSprinting(true);
            } else {
                this.setSneaking(false);
                this.setSprinting(false);
            }
        } else {
            this.setSneaking(false);
            this.setSprinting(false);
        }
    }

    @Override
    public boolean canDespawn() {
        return !this.isTamed() && this.ticksExisted > 2400;
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
    }

    @Override
    public void fall(float distance) {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("CatType", this.getTameSkin());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setTameSkin(tagCompund.getInteger("CatType"));
    }

    @Override
    public String getLivingSound() {
        return this.isTamed() ? (this.isInLove() ? "mob.cat.purr" : (this.rand.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow")) : "";
    }

    @Override
    public String getHurtSound() {
        return "mob.cat.hitt";
    }

    @Override
    public String getDeathSound() {
        return "mob.cat.hitt";
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public Item getDropItem() {
        return Items.leather;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        this.aiSit.setSitting(false);
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) throws java.io.IOException {
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        if (this.isTamed()) {
            if (this.isOwner(entityPlayer) && !this.worldObj.isRemote && !this.isBreedingItem(currentItem)) {
                this.aiSit.setSitting(!this.isSitting());
            }
        } else if (this.aiTempt.isRunning() && currentItem != null && currentItem.getItem() == Items.fish && entityPlayer.getDistanceSqToEntity(this) < 9.0) {
            if (!entityPlayer.capabilities.isCreativeMode) {
                currentItem.stackSize--;
            }

            if (currentItem.stackSize <= 0) {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }

            if (!this.worldObj.isRemote) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTamed(true);
                    this.setTameSkin(1 + this.worldObj.rand.nextInt(3));
                    this.updateObject(entityPlayer.getUniqueID().toString());
                    this.playTameEffect(true);
                    this.aiSit.setSitting(true);
                    this.worldObj.setEntityState(this, (byte)7);
                } else {
                    this.playTameEffect(false);
                    this.worldObj.setEntityState(this, (byte)6);
                }
            }

            return true;
        }

        return super.interact(entityPlayer);
    }

    public EntityOcelot createChild(EntityAgeable entityAgeable) {
        EntityOcelot entityOcelot = new EntityOcelot(this.worldObj);
        if (this.isTamed()) {
            entityOcelot.updateObject(this.getWatchableObjectString());
            entityOcelot.setTamed(true);
            entityOcelot.setTameSkin(this.getTameSkin());
        }

        return entityOcelot;
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() == Items.fish;
    }

    @Override
    public boolean canMateWith(EntityAnimal entityAnimal) {
        if (entityAnimal == this) {
            return false;
        }

        if (!this.isTamed()) {
            return false;
        }

        if (!(entityAnimal instanceof EntityOcelot)) {
            return false;
        }

        EntityOcelot entityOcelot = (EntityOcelot)entityAnimal;
        return entityOcelot.isTamed() && this.isInLove() && entityOcelot.isInLove();
    }

    public int getTameSkin() {
        return this.dataWatcher.getWatchableObjectByte(18);
    }

    public void setTameSkin(int n) {
        this.dataWatcher.updateObject(18, (byte)n);
    }

    @Override
    public boolean getCanSpawnHere() {
        if (this.worldObj.rand.nextInt(3) == 0) {
            return false;
        }

        if (this.worldObj.checkNoEntityCollision(this.boundingBox)
            && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
            && !this.worldObj.isAnyLiquid(this.boundingBox)) {
            int n = MathHelper.floor_double(this.posX);
            int n2 = MathHelper.floor_double(this.boundingBox.minY);
            int n3 = MathHelper.floor_double(this.posZ);
            if (n2 < 63) {
                return false;
            }

            Block block = this.worldObj.getBlock(n, n2 - 1, n3);
            return block == Blocks.grass || block.getMaterial() == Material.leaves;
        } else {
            return false;
        }
    }

    @Override
    public String getCommandSenderName() {
        return this.hasCustomNameTag()
            ? this.getCustomNameTag()
            : (this.isTamed() ? StatCollector.translateToLocal("entity.Cat.name") : super.getCommandSenderName());
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData) {
        entityLivingData = super.onSpawnWithEgg(entityLivingData);
        if (this.worldObj.rand.nextInt(7) == 0) {
            for (int i = 0; i < 2; i++) {
                EntityOcelot entityOcelot = new EntityOcelot(this.worldObj);
                entityOcelot.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
                entityOcelot.setGrowingAge(-24000);
                this.worldObj.spawnEntityInWorld(entityOcelot);
            }
        }

        return entityLivingData;
    }
}
