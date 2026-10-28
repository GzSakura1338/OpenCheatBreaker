package net.minecraft.entity.monster;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityZombie extends EntityMob {
    public static IAttribute REINFORCEMENTS_ATTRIBUTE = new RangedAttribute("zombie.spawnReinforcements", 0.0, 0.0, 1.0)
        .setDescription("Spawn Reinforcements Chance");
    public static UUID babySpeedBoostUUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    public static AttributeModifier babySpeedBoostModifier = new AttributeModifier(babySpeedBoostUUID, "Baby speed boost", 0.5, 1);
    public EntityAIBreakDoor breakDoorsGoal = new EntityAIBreakDoor(this);
    public int conversionTime;
    public boolean canBreakDoors = false;
    public float boundWidth = -1.0F;
    public float boundHeight;

    public EntityZombie(World world) {
        super(world);
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0));
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0, false));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
        this.setSize(0.6F, 1.8F);
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23F);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0);
        this.getAttributeMap().registerAttribute(REINFORCEMENTS_ATTRIBUTE).setBaseValue(this.rand.nextDouble() * 0.1F);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.getDataWatcher().addObject(12, (byte)0);
        this.getDataWatcher().addObject(13, (byte)0);
        this.getDataWatcher().addObject(14, (byte)0);
    }

    @Override
    public int getTotalArmorValue() {
        int n = super.getTotalArmorValue() + 2;
        if (n > 20) {
            n = 20;
        }

        return n;
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    public boolean canBreakDoors() {
        return this.canBreakDoors;
    }

    public void setCanBreakDoors(boolean flag) {
        if (this.canBreakDoors != flag) {
            this.canBreakDoors = flag;
            if (flag) {
                this.tasks.addTask(1, this.breakDoorsGoal);
            } else {
                this.tasks.removeTask(this.breakDoorsGoal);
            }
        }
    }

    @Override
    public boolean isChild() {
        return this.getDataWatcher().getWatchableObjectByte(12) == 1;
    }

    @Override
    public int getExperiencePoints(EntityPlayer entityPlayer) {
        if (this.isChild()) {
            this.experienceValue = (int)(this.experienceValue * 2.5F);
        }

        return super.getExperiencePoints(entityPlayer);
    }

    public void setChild(boolean flag) {
        this.getDataWatcher().updateObject(12, (byte)(flag ? 1 : 0));
        if (this.worldObj != null && !this.worldObj.isRemote) {
            IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            entityAttribute.removeModifier(babySpeedBoostModifier);
            if (flag) {
                entityAttribute.applyModifier(babySpeedBoostModifier);
            }
        }

        this.changeType(flag);
    }

    public boolean isVillager() {
        return this.getDataWatcher().getWatchableObjectByte(13) == 1;
    }

    public void setVillager(boolean flag) {
        this.getDataWatcher().updateObject(13, (byte)(flag ? 1 : 0));
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        if (this.worldObj.isDaytime() && !this.worldObj.isRemote && !this.isChild()) {
            float brightness = this.getBrightness(1.0F);
            if (brightness > 0.5F
                && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F
                && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
                boolean flag = true;
                ItemStack equipmentInSlot = this.getEquipmentInSlot(4);
                if (equipmentInSlot != null) {
                    if (equipmentInSlot.isItemStackDamageable()) {
                        equipmentInSlot.setMetadata(equipmentInSlot.getCurrentDurability() + this.rand.nextInt(2));
                        if (equipmentInSlot.getCurrentDurability() >= equipmentInSlot.getMaxDurability()) {
                            this.renderBrokenItemStack(equipmentInSlot);
                            this.setCurrentItemOrArmor(4, null);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    this.setFire(8);
                }
            }
        }

        if (this.isRiding() && this.getAttackTarget() != null && this.ridingEntity instanceof EntityChicken) {
            ((EntityLiving)this.ridingEntity).getNavigator().setPath(this.getNavigator().getPath(), 1.5);
        }

        super.onLivingUpdate();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!super.attackEntityFrom(source, amount)) {
            return false;
        }

        EntityLivingBase attackTarget = this.getAttackTarget();
        if (attackTarget == null && this.getEntityToAttack() instanceof EntityLivingBase) {
            attackTarget = (EntityLivingBase)this.getEntityToAttack();
        }

        if (attackTarget == null && source.getEntity() instanceof EntityLivingBase) {
            attackTarget = (EntityLivingBase)source.getEntity();
        }

        if (attackTarget != null
            && this.worldObj.difficultySetting == EnumDifficulty.HARD
            && this.rand.nextFloat() < this.getEntityAttribute(REINFORCEMENTS_ATTRIBUTE).getAttributeValue()) {
            int n = MathHelper.floor_double(this.posX);
            int n2 = MathHelper.floor_double(this.posY);
            int n3 = MathHelper.floor_double(this.posZ);
            EntityZombie entityZombie = new EntityZombie(this.worldObj);

            for (int i = 0; i < 50; i++) {
                int n4 = n + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
                int n5 = n2 + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
                int n6 = n3 + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
                if (World.doesBlockHaveSolidTopSurface(this.worldObj, n4, n5 - 1, n6) && this.worldObj.getBlockLightValue(n4, n5, n6) < 10) {
                    entityZombie.setPosition(n4, n5, n6);
                    if (this.worldObj.checkNoEntityCollision(entityZombie.boundingBox)
                        && this.worldObj.getCollidingBoundingBoxes(entityZombie, entityZombie.boundingBox).isEmpty()
                        && !this.worldObj.isAnyLiquid(entityZombie.boundingBox)) {
                        this.worldObj.spawnEntityInWorld(entityZombie);
                        entityZombie.setAttackTarget(attackTarget);
                        entityZombie.onSpawnWithEgg(null);
                        this.getEntityAttribute(REINFORCEMENTS_ATTRIBUTE).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05F, 0));
                        entityZombie.getEntityAttribute(REINFORCEMENTS_ATTRIBUTE).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05F, 0));
                        break;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        if (!this.worldObj.isRemote && this.isConverting()) {
            int conversionTimeBoost = this.getConversionTimeBoost();
            this.conversionTime -= conversionTimeBoost;
            if (this.conversionTime <= 0) {
                this.convertToVillager();
            }
        }

        super.onUpdate();
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean attackEntityAsMob = super.attackEntityAsMob(entity);
        if (attackEntityAsMob) {
            int difficultyId = this.worldObj.difficultySetting.getDifficultyId();
            if (this.getHeldItem() == null && this.isBurning() && this.rand.nextFloat() < difficultyId * 0.3F) {
                entity.setFire(2 * difficultyId);
            }
        }

        return attackEntityAsMob;
    }

    @Override
    public String getLivingSound() {
        return "mob.zombie.say";
    }

    @Override
    public String getHurtSound() {
        return "mob.zombie.hurt";
    }

    @Override
    public String getDeathSound() {
        return "mob.zombie.death";
    }

    @Override
    public void playStepSound(int x, int y, int z, Block blockIn) {
        this.playSound("mob.zombie.step", 0.15F, 1.0F);
    }

    @Override
    public Item getDropItem() {
        return Items.rotten_flesh;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    public void dropRareDrop(int n) {
        switch (this.rand.nextInt(3)) {
            case 0:
                this.dropItem(Items.iron_ingot, 1);
                break;
            case 1:
                this.dropItem(Items.carrot, 1);
                break;
            case 2:
                this.dropItem(Items.potato, 1);
        }
    }

    @Override
    public void addRandomArmor() {
        super.addRandomArmor();
        if (this.rand.nextFloat() < (this.worldObj.difficultySetting == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
            int nextInt = this.rand.nextInt(3);
            if (nextInt == 0) {
                this.setCurrentItemOrArmor(0, new ItemStack(Items.iron_sword));
            } else {
                this.setCurrentItemOrArmor(0, new ItemStack(Items.iron_shovel));
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        if (this.isChild()) {
            tagCompound.setBoolean("IsBaby", true);
        }

        if (this.isVillager()) {
            tagCompound.setBoolean("IsVillager", true);
        }

        tagCompound.setInteger("ConversionTime", this.isConverting() ? this.conversionTime : -1);
        tagCompound.setBoolean("CanBreakDoors", this.canBreakDoors());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        if (tagCompund.getBoolean("IsBaby")) {
            this.setChild(true);
        }

        if (tagCompund.getBoolean("IsVillager")) {
            this.setVillager(true);
        }

        if (tagCompund.hasKey("ConversionTime", 99) && tagCompund.getInteger("ConversionTime") > -1) {
            this.startConversion(tagCompund.getInteger("ConversionTime"));
        }

        this.setCanBreakDoors(tagCompund.getBoolean("CanBreakDoors"));
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        if ((this.worldObj.difficultySetting == EnumDifficulty.NORMAL || this.worldObj.difficultySetting == EnumDifficulty.HARD)
            && entityLivingIn instanceof EntityVillager) {
            if (this.worldObj.difficultySetting != EnumDifficulty.HARD && this.rand.nextBoolean()) {
                return;
            }

            EntityZombie entityZombie = new EntityZombie(this.worldObj);
            entityZombie.copyLocationAndAnglesFrom(entityLivingIn);
            this.worldObj.removeEntity(entityLivingIn);
            entityZombie.onSpawnWithEgg(null);
            entityZombie.setVillager(true);
            if (entityLivingIn.isChild()) {
                entityZombie.setChild(true);
            }

            this.worldObj.spawnEntityInWorld(entityZombie);
            this.worldObj.playAuxSFXAtEntity(null, 1016, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
        }
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData) {
        IEntityLivingData onSpawnWithEgg = super.onSpawnWithEgg(entityLivingData);
        float tensionFactorForBlock = this.worldObj.getTensionFactorForBlock(this.posX, this.posY, this.posZ);
        this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * tensionFactorForBlock);
        if (onSpawnWithEgg == null) {
            onSpawnWithEgg = new EntityZombie.GroupData(this.worldObj.rand.nextFloat() < 0.05F, this.worldObj.rand.nextFloat() < 0.05F, null);
        }

        if (onSpawnWithEgg instanceof EntityZombie.GroupData) {
            EntityZombie.GroupData groupData = (EntityZombie.GroupData)onSpawnWithEgg;
            if (groupData.flag2) {
                this.setVillager(true);
            }

            if (groupData.flag) {
                this.setChild(true);
                if (this.worldObj.rand.nextFloat() < 0.05) {
                    List selectEntitiesWithinAABB = this.worldObj.selectEntitiesWithinAABB(EntityChicken.class, this.boundingBox.expand(5.0, 3.0, 5.0), IEntitySelector.INSTANCE);
                    if (!selectEntitiesWithinAABB.isEmpty()) {
                        EntityChicken entityChicken = (EntityChicken)selectEntitiesWithinAABB.get(0);
                        entityChicken.setHasJockey(true);
                        this.mountEntity(entityChicken);
                    }
                } else if (this.worldObj.rand.nextFloat() < 0.05) {
                    EntityChicken entityChicken = new EntityChicken(this.worldObj);
                    entityChicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
                    entityChicken.onSpawnWithEgg(null);
                    entityChicken.setHasJockey(true);
                    this.worldObj.spawnEntityInWorld(entityChicken);
                    this.mountEntity(entityChicken);
                }
            }
        }

        this.setCanBreakDoors(this.rand.nextFloat() < tensionFactorForBlock * 0.1F);
        this.addRandomArmor();
        this.enchantEquipment();
        if (this.getEquipmentInSlot(4) == null) {
            Calendar currentDate = this.worldObj.getCurrentDate();
            if (currentDate.get(2) + 1 == 10 && currentDate.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
                this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
                this.equipmentDropChances[4] = 0.0F;
            }
        }

        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance)
            .applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * 0.05F, 0));
        double d = this.rand.nextDouble() * 1.5 * this.worldObj.getTensionFactorForBlock(this.posX, this.posY, this.posZ);
        if (d > 1.0) {
            this.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random zombie-spawn bonus", d, 2));
        }

        if (this.rand.nextFloat() < tensionFactorForBlock * 0.05F) {
            this.getEntityAttribute(REINFORCEMENTS_ATTRIBUTE).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 0.25 + 0.5, 0));
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
                .applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 3.0 + 1.0, 2));
            this.setCanBreakDoors(true);
        }

        return onSpawnWithEgg;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) {
        ItemStack currentEquippedItem = entityPlayer.getCurrentEquippedItem();
        if (currentEquippedItem != null && currentEquippedItem.getItem() == Items.golden_apple && currentEquippedItem.getMetadata() == 0 && this.isVillager() && this.isPotionActive(Potion.weakness)) {
            if (!entityPlayer.capabilities.isCreativeMode) {
                currentEquippedItem.stackSize--;
            }

            if (currentEquippedItem.stackSize <= 0) {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }

            if (!this.worldObj.isRemote) {
                this.startConversion(this.rand.nextInt(2401) + 3600);
            }

            return true;
        } else {
            return false;
        }
    }

    public void startConversion(int n) {
        this.conversionTime = n;
        this.getDataWatcher().updateObject(14, (byte)1);
        this.removePotionEffect(Potion.weakness.id);
        this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, n, Math.min(this.worldObj.difficultySetting.getDifficultyId() - 1, 0)));
        this.worldObj.setEntityState(this, (byte)16);
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 16) {
            this.worldObj
                .playSound(
                    this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "mob.zombie.remedy", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false
                );
        } else {
            super.handleHealthUpdate(b);
        }
    }

    @Override
    public boolean canDespawn() {
        return !this.isConverting();
    }

    public boolean isConverting() {
        return this.getDataWatcher().getWatchableObjectByte(14) == 1;
    }

    public void convertToVillager() {
        EntityVillager entityVillager = new EntityVillager(this.worldObj);
        entityVillager.copyLocationAndAnglesFrom(this);
        entityVillager.onSpawnWithEgg(null);
        entityVillager.setLookingForHome();
        if (this.isChild()) {
            entityVillager.setGrowingAge(-24000);
        }

        this.worldObj.removeEntity(this);
        this.worldObj.spawnEntityInWorld(entityVillager);
        entityVillager.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
        this.worldObj.playAuxSFXAtEntity(null, 1017, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
    }

    public int getConversionTimeBoost() {
        int n = 1;
        if (this.rand.nextFloat() < 0.01F) {
            int n2 = 0;

            for (int i = (int)this.posX - 4; i < (int)this.posX + 4 && n2 < 14; i++) {
                for (int j = (int)this.posY - 4; j < (int)this.posY + 4 && n2 < 14; j++) {
                    for (int k = (int)this.posZ - 4; k < (int)this.posZ + 4 && n2 < 14; k++) {
                        Block block = this.worldObj.getBlock(i, j, k);
                        if (block == Blocks.iron_bars || block == Blocks.bed) {
                            if (this.rand.nextFloat() < 0.3F) {
                                n++;
                            }

                            n2++;
                        }
                    }
                }
            }
        }

        return n;
    }

    public void changeType(boolean flag) {
        this.increaseBounds(flag ? 0.5F : 1.0F);
    }

    @Override
    public void setSize(float width, float height) {
        boolean flag = this.boundWidth > 0.0F && this.boundHeight > 0.0F;
        this.boundWidth = width;
        this.boundHeight = height;
        if (!flag) {
            this.increaseBounds(1.0F);
        }
    }

    public void increaseBounds(float f) {
        super.setSize(this.boundWidth * f, this.boundHeight * f);
    }

    public class GroupData implements IEntityLivingData {
        public boolean flag = false;
        public boolean flag2 = false;

        public GroupData(boolean flag, boolean flag2) {
            this.flag = flag;
            this.flag2 = flag2;
        }

        public GroupData(boolean flag, boolean flag2, Object obj) {
            this(flag, flag2);
        }
    }
}
