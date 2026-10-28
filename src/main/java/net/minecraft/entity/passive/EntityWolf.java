package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWolf extends EntityTameable {
    public float headRotationCourse;
    public float headRotationCourseOld;
    public boolean isWet;
    public boolean isShaking;
    public float timeWolfIsShaking;
    public float prevTimeWolfIsShaking;

    public EntityWolf(World world) {
        super(world);
        this.setSize(0.6F, 0.8F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0, true));
        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0, 10.0F, 2.0F));
        this.tasks.addTask(6, new EntityAIMate(this, 1.0));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0));
        this.tasks.addTask(8, new EntityAIBeg(this, 8.0F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntitySheep.class, 200, false));
        this.setTamed(false);
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
        if (this.isTamed()) {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
        }
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void setAttackTarget(EntityLivingBase entityLivingBase) {
        super.setAttackTarget(entityLivingBase);
        if (entityLivingBase == null) {
            this.setAngry(false);
        } else if (!this.isTamed()) {
            this.setAngry(true);
        }
    }

    @Override
    public void updateAITick() {
        this.dataWatcher.updateObject(18, this.getHealth());
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(18, new Float(this.getHealth()));
        this.dataWatcher.addObject(19, new Byte((byte)0));
        this.dataWatcher.addObject(20, new Byte((byte)BlockColored.computeInt(1)));
    }

    @Override
    public void playStepSound(int x, int y, int z, Block blockIn) {
        this.playSound("mob.wolf.step", 0.15F, 1.0F);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("Angry", this.isAngry());
        tagCompound.setByte("CollarColor", (byte)this.getCollarColor());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setAngry(tagCompund.getBoolean("Angry"));
        if (tagCompund.hasKey("CollarColor", 99)) {
            this.setCollarColor(tagCompund.getByte("CollarColor"));
        }
    }

    @Override
    public String getLivingSound() {
        return this.isAngry()
            ? "mob.wolf.growl"
            : (
                this.rand.nextInt(3) == 0
                    ? (this.isTamed() && this.dataWatcher.getWatchableObjectFloat(18) < 10.0F ? "mob.wolf.whine" : "mob.wolf.panting")
                    : "mob.wolf.bark"
            );
    }

    @Override
    public String getHurtSound() {
        return "mob.wolf.hurt";
    }

    @Override
    public String getDeathSound() {
        return "mob.wolf.death";
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public Item getDropItem() {
        return Item.getItemById(-1);
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && this.isWet && !this.isShaking && !this.hasPath() && this.onGround) {
            this.isShaking = true;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
            this.worldObj.setEntityState(this, (byte)8);
        }
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        this.headRotationCourseOld = this.headRotationCourse;
        if (this.getWatchableObjectByte()) {
            this.headRotationCourse = this.headRotationCourse + (1.0F - this.headRotationCourse) * 0.4F;
        } else {
            this.headRotationCourse = this.headRotationCourse + (0.0F - this.headRotationCourse) * 0.4F;
        }

        if (this.getWatchableObjectByte()) {
            this.numTicksToChaseTarget = 10;
        }

        if (this.isWet()) {
            this.isWet = true;
            this.isShaking = false;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
        } else if ((this.isWet || this.isShaking) && this.isShaking) {
            if (this.timeWolfIsShaking == 0.0F) {
                this.playSound("mob.wolf.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
            this.timeWolfIsShaking += 0.05F;
            if (this.prevTimeWolfIsShaking >= 2.0F) {
                this.isWet = false;
                this.isShaking = false;
                this.prevTimeWolfIsShaking = 0.0F;
                this.timeWolfIsShaking = 0.0F;
            }

            if (this.timeWolfIsShaking > 0.4F) {
                float minY = (float)this.boundingBox.minY;
                int n = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * (float) Math.PI) * 7.0F);

                for (int i = 0; i < n; i++) {
                    float f = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    this.worldObj.spawnParticle("splash", this.posX + f, minY + 0.8F, this.posZ + f2, this.motionX, this.motionY, this.motionZ);
                }
            }
        }
    }

    public boolean isWolfWet() {
        return this.isWet;
    }

    public float getShadingWhileWet(float f) {
        return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * f) / 2.0F * 0.25F;
    }

    public float getShakeAngle(float f, float f2) {
        float f3 = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * f + f2) / 1.8F;
        if (f3 < 0.0F) {
            f3 = 0.0F;
        } else if (f3 > 1.0F) {
            f3 = 1.0F;
        }

        return MathHelper.sin(f3 * (float) Math.PI) * MathHelper.sin(f3 * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
    }

    public float getInterestedAngle(float f) {
        return (this.headRotationCourseOld + (this.headRotationCourse - this.headRotationCourseOld) * f) * 0.15F * (float) Math.PI;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.8F;
    }

    @Override
    public int getVerticalFaceSpeed() {
        return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        Entity entity = source.getEntity();
        this.aiSit.setSitting(false);
        if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow)) {
            amount = (amount + 1.0F) / 2.0F;
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        int n = this.isTamed() ? 4 : 2;
        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), n);
    }

    @Override
    public void setTamed(boolean flag) {
        super.setTamed(flag);
        if (flag) {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) throws java.io.IOException {
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        if (this.isTamed()) {
            if (currentItem != null) {
                if (currentItem.getItem() instanceof ItemFood) {
                    ItemFood item = (ItemFood)currentItem.getItem();
                    if (item.isWolfsFavoriteMeat() && this.dataWatcher.getWatchableObjectFloat(18) < 20.0F) {
                        if (!entityPlayer.capabilities.isCreativeMode) {
                            currentItem.stackSize--;
                        }

                        this.heal(item.getHealAmount(currentItem));
                        if (currentItem.stackSize <= 0) {
                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                        }

                        return true;
                    }
                } else if (currentItem.getItem() == Items.dye) {
                    int computeInt = BlockColored.computeInt(currentItem.getMetadata());
                    if (computeInt != this.getCollarColor()) {
                        this.setCollarColor(computeInt);
                        if (!entityPlayer.capabilities.isCreativeMode && --currentItem.stackSize <= 0) {
                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                        }

                        return true;
                    }
                }
            }

            if (this.isOwner(entityPlayer) && !this.worldObj.isRemote && !this.isBreedingItem(currentItem)) {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.setPathToEntity(null);
                this.setTarget(null);
                this.setAttackTarget(null);
            }
        } else if (currentItem != null && currentItem.getItem() == Items.bone && !this.isAngry()) {
            if (!entityPlayer.capabilities.isCreativeMode) {
                currentItem.stackSize--;
            }

            if (currentItem.stackSize <= 0) {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }

            if (!this.worldObj.isRemote) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTamed(true);
                    this.setPathToEntity(null);
                    this.setAttackTarget(null);
                    this.aiSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.updateObject(entityPlayer.getUniqueID().toString());
                    this.playTameEffect(true);
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

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 8) {
            this.isShaking = true;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
        } else {
            super.handleHealthUpdate(b);
        }
    }

    public float getTailRotation() {
        return this.isAngry()
            ? 1.5393804F
            : (this.isTamed() ? (0.55F - (20.0F - this.dataWatcher.getWatchableObjectFloat(18)) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5));
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() instanceof ItemFood && ((ItemFood)itemStack.getItem()).isWolfsFavoriteMeat();
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 8;
    }

    public boolean isAngry() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
    }

    public void setAngry(boolean flag) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
        if (flag) {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 2));
        } else {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -3));
        }
    }

    public int getCollarColor() {
        return this.dataWatcher.getWatchableObjectByte(20) & 15;
    }

    public void setCollarColor(int n) {
        this.dataWatcher.updateObject(20, (byte)(n & 15));
    }

    public EntityWolf createChild(EntityAgeable entityAgeable) {
        EntityWolf entityWolf = new EntityWolf(this.worldObj);
        String watchableObjectString = this.getWatchableObjectString();
        if (watchableObjectString != null && watchableObjectString.trim().length() > 0) {
            entityWolf.updateObject(watchableObjectString);
            entityWolf.setTamed(true);
        }

        return entityWolf;
    }

    public void setBegging(boolean flag) {
        if (flag) {
            this.dataWatcher.updateObject(19, (byte)1);
        } else {
            this.dataWatcher.updateObject(19, (byte)0);
        }
    }

    @Override
    public boolean canMateWith(EntityAnimal entityAnimal) {
        if (entityAnimal == this) {
            return false;
        }

        if (!this.isTamed()) {
            return false;
        }

        if (!(entityAnimal instanceof EntityWolf)) {
            return false;
        }

        EntityWolf entityWolf = (EntityWolf)entityAnimal;
        return entityWolf.isTamed() && !entityWolf.isSitting() && this.isInLove() && entityWolf.isInLove();
    }

    public boolean getWatchableObjectByte() {
        return this.dataWatcher.getWatchableObjectByte(19) == 1;
    }

    @Override
    public boolean canDespawn() {
        return !this.isTamed() && this.ticksExisted > 2400;
    }

    @Override
    public boolean canAttackWithOwner(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        if (!(entityLivingBase instanceof EntityCreeper) && !(entityLivingBase instanceof EntityGhast)) {
            if (entityLivingBase instanceof EntityWolf) {
                EntityWolf entityWolf = (EntityWolf)entityLivingBase;
                if (entityWolf.isTamed() && entityWolf.getOwner() == entityLivingBase2) {
                    return false;
                }
            }

            return (!(entityLivingBase instanceof EntityPlayer) || !(entityLivingBase2 instanceof EntityPlayer) || ((EntityPlayer)entityLivingBase2).canAttackPlayer((EntityPlayer)entityLivingBase))
                && (!(entityLivingBase instanceof EntityHorse) || !((EntityHorse)entityLivingBase).isTame());
        } else {
            return false;
        }
    }
}
