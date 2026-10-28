package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.BlockPos;
import net.optifine.Reflector;

public abstract class EntityLiving extends EntityLivingBase {
    public int livingSoundTime;
    public int experienceValue;
    public ItemStack[] equipment = new ItemStack[5];
    public float[] equipmentDropChances = new float[5];
    public int randomMobsId = 0;
    public BiomeGenBase spawnBiome = null;
    public BlockPos spawnPosition = null;
    public EntityAITasks tasks;
    public EntityAITasks targetTasks;
    public EntityLivingBase attackTarget;
    public EntityLookHelper lookHelper;
    public EntityMoveHelper moveHelper;
    public EntityJumpHelper jumpHelper;
    public boolean canPickUpLoot;
    public boolean persistenceRequired;
    public float defaultPitch;
    public Entity currentTarget;
    public int numTicksToChaseTarget;
    public boolean isLeashed;
    public Entity leashedToEntity;
    public NBTTagCompound leashNBTTag;
    public EntityBodyHelper bodyHelper;
    public PathNavigate navigator;
    public EntitySenses senses;

    public EntityLiving(World world) {
        super(world);
        this.tasks = new EntityAITasks(world != null && world.theProfiler != null ? world.theProfiler : null);
        this.targetTasks = new EntityAITasks(world != null && world.theProfiler != null ? world.theProfiler : null);
        this.lookHelper = new EntityLookHelper(this);
        this.moveHelper = new EntityMoveHelper(this);
        this.jumpHelper = new EntityJumpHelper(this);
        this.bodyHelper = new EntityBodyHelper(this);
        this.navigator = new PathNavigate(this, world);
        this.senses = new EntitySenses(this);

        for (int i = 0; i < this.equipmentDropChances.length; i++) {
            this.equipmentDropChances[i] = 0.085F;
        }

        UUID uniqueID = this.getUniqueID();
        long leastSignificantBits = uniqueID.getLeastSignificantBits();
        this.randomMobsId = (int)(leastSignificantBits & 2147483647L);
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
    }

    public EntityLookHelper getLookHelper() {
        return this.lookHelper;
    }

    public EntityMoveHelper getMoveHelper() {
        return this.moveHelper;
    }

    public EntityJumpHelper getJumpHelper() {
        return this.jumpHelper;
    }

    public PathNavigate getNavigator() {
        return this.navigator;
    }

    public EntitySenses getEntitySenses() {
        return this.senses;
    }

    public EntityLivingBase getAttackTarget() {
        return this.attackTarget;
    }

    public void setAttackTarget(EntityLivingBase entityLivingBase) {
        this.attackTarget = entityLivingBase;
        Reflector.callVoid(Reflector.ForgeHooks_onLivingSetAttackTarget, this, entityLivingBase);
    }

    public boolean canAttackClass(Class clazz) {
        return EntityCreeper.class != clazz && EntityGhast.class != clazz;
    }

    public void eatGrassBonus() {
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(11, (byte)0);
        this.dataWatcher.addObject(10, "");
    }

    public int getTalkInterval() {
        return 80;
    }

    public void playLivingSound() {
        String livingSound = this.getLivingSound();
        if (livingSound != null) {
            this.playSound(livingSound, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public void onEntityUpdate() throws java.io.IOException {
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("mobBaseTick");
        if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
            this.livingSoundTime = -this.getTalkInterval();
            this.playLivingSound();
        }

        this.worldObj.theProfiler.endSection();
    }

    @Override
    public int getExperiencePoints(EntityPlayer entityPlayer) {
        if (this.experienceValue > 0) {
            int experienceValue = this.experienceValue;
            ItemStack[] inventory = this.getInventory();

            for (int i = 0; i < inventory.length; i++) {
                if (inventory[i] != null && this.equipmentDropChances[i] <= 1.0F) {
                    experienceValue += 1 + this.rand.nextInt(3);
                }
            }

            return experienceValue;
        } else {
            return this.experienceValue;
        }
    }

    public void spawnExplosionParticle() {
        for (int i = 0; i < 20; i++) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            double d4 = 10.0;
            this.worldObj
                .spawnParticle(
                    "explode",
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width - d * d4,
                    this.posY + this.rand.nextFloat() * this.height - d2 * d4,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width - d3 * d4,
                    d,
                    d2,
                    d3
                );
        }
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            this.updateLeashedState();
        }
    }

    @Override
    public float computeFloat(float f, float f2) {
        if (this.isAIEnabled()) {
            this.bodyHelper.updateRenderAngles();
            return f2;
        } else {
            return super.computeFloat(f, f2);
        }
    }

    public String getLivingSound() {
        return null;
    }

    public Item getDropItem() {
        return Item.getItemById(0);
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        Item dropItem = this.getDropItem();
        if (dropItem != null) {
            int nextInt = this.rand.nextInt(3);
            if (n > 0) {
                nextInt += this.rand.nextInt(n + 1);
            }

            for (int i = 0; i < nextInt; i++) {
                this.dropItem(dropItem, 1);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("CanPickUpLoot", this.canPickUpLoot());
        tagCompound.setBoolean("PersistenceRequired", this.persistenceRequired);
        NBTTagList nbttagList = new NBTTagList();

        for (int i = 0; i < this.equipment.length; i++) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            if (this.equipment[i] != null) {
                this.equipment[i].writeToNBT(nbttagCompound);
            }

            nbttagList.appendTag(nbttagCompound);
        }

        tagCompound.setTag("Equipment", nbttagList);
        NBTTagList nbttagList2 = new NBTTagList();

        for (int i = 0; i < this.equipmentDropChances.length; i++) {
            nbttagList2.appendTag(new NBTTagFloat(this.equipmentDropChances[i]));
        }

        tagCompound.setTag("DropChances", nbttagList2);
        tagCompound.setString("CustomName", this.getCustomNameTag());
        tagCompound.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
        tagCompound.setBoolean("Leashed", this.isLeashed);
        if (this.leashedToEntity != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            if (this.leashedToEntity instanceof EntityLivingBase) {
                nbttagCompound.setLong("UUIDMost", this.leashedToEntity.getUniqueID().getMostSignificantBits());
                nbttagCompound.setLong("UUIDLeast", this.leashedToEntity.getUniqueID().getLeastSignificantBits());
            } else if (this.leashedToEntity instanceof EntityHanging) {
                EntityHanging leashedToEntity = (EntityHanging)this.leashedToEntity;
                nbttagCompound.setInteger("X", leashedToEntity.intValue);
                nbttagCompound.setInteger("Y", leashedToEntity.intValue2);
                nbttagCompound.setInteger("Z", leashedToEntity.intValue3);
            }

            tagCompound.setTag("Leash", nbttagCompound);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setCanPickUpLoot(tagCompund.getBoolean("CanPickUpLoot"));
        this.persistenceRequired = tagCompund.getBoolean("PersistenceRequired");
        if (tagCompund.hasKey("CustomName", 8) && tagCompund.getString("CustomName").length() > 0) {
            this.setCustomNameTag(tagCompund.getString("CustomName"));
        }

        this.setAlwaysRenderNameTag(tagCompund.getBoolean("CustomNameVisible"));
        if (tagCompund.hasKey("Equipment", 9)) {
            NBTTagList tagList = tagCompund.getTagList("Equipment", 10);

            for (int i = 0; i < this.equipment.length; i++) {
                this.equipment[i] = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));
            }
        }

        if (tagCompund.hasKey("DropChances", 9)) {
            NBTTagList tagList = tagCompund.getTagList("DropChances", 5);

            for (int i = 0; i < tagList.tagCount(); i++) {
                this.equipmentDropChances[i] = tagList.getFloatAt(i);
            }
        }

        this.isLeashed = tagCompund.getBoolean("Leashed");
        if (this.isLeashed && tagCompund.hasKey("Leash", 10)) {
            this.leashNBTTag = tagCompund.getCompoundTag("Leash");
        }
    }

    public void setMoveForward(float f) {
        this.moveForward = f;
    }

    @Override
    public void setAIMoveSpeed(float f) {
        super.setAIMoveSpeed(f);
        this.setMoveForward(f);
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        super.onLivingUpdate();
        this.worldObj.theProfiler.startSection("looting");
        if (!this.worldObj.isRemote && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
            for (EntityItem entityItem : (Iterable<EntityItem>)this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.0, 0.0, 1.0))) {
                if (!entityItem.isDead && entityItem.getEntityItem() != null) {
                    ItemStack entityItem2 = entityItem.getEntityItem();
                    int armorPosition = getArmorPosition(entityItem2);
                    if (armorPosition > -1) {
                        boolean flag = true;
                        ItemStack equipmentInSlot = this.getEquipmentInSlot(armorPosition);
                        if (equipmentInSlot != null) {
                            if (armorPosition == 0) {
                                if (entityItem2.getItem() instanceof ItemSword && !(equipmentInSlot.getItem() instanceof ItemSword)) {
                                    flag = true;
                                } else if (entityItem2.getItem() instanceof ItemSword && equipmentInSlot.getItem() instanceof ItemSword) {
                                    ItemSword item = (ItemSword)entityItem2.getItem();
                                    ItemSword item2 = (ItemSword)equipmentInSlot.getItem();
                                    if (item.getAttackDamage() == item2.getAttackDamage()) {
                                        flag = entityItem2.getMetadata() > equipmentInSlot.getMetadata() || entityItem2.hasTagCompound() && !equipmentInSlot.hasTagCompound();
                                    } else {
                                        flag = item.getAttackDamage() > item2.getAttackDamage();
                                    }
                                } else {
                                    flag = false;
                                }
                            } else if (entityItem2.getItem() instanceof ItemArmor && !(equipmentInSlot.getItem() instanceof ItemArmor)) {
                                flag = true;
                            } else if (entityItem2.getItem() instanceof ItemArmor && equipmentInSlot.getItem() instanceof ItemArmor) {
                                ItemArmor item = (ItemArmor)entityItem2.getItem();
                                ItemArmor item2 = (ItemArmor)equipmentInSlot.getItem();
                                if (item.damageReduceAmount == item2.damageReduceAmount) {
                                    flag = entityItem2.getMetadata() > equipmentInSlot.getMetadata() || entityItem2.hasTagCompound() && !equipmentInSlot.hasTagCompound();
                                } else {
                                    flag = item.damageReduceAmount > item2.damageReduceAmount;
                                }
                            } else {
                                flag = false;
                            }
                        }

                        if (flag) {
                            if (equipmentInSlot != null && this.rand.nextFloat() - 0.1F < this.equipmentDropChances[armorPosition]) {
                                this.entityDropItem(equipmentInSlot, 0.0F);
                            }

                            if (entityItem2.getItem() == Items.diamond && entityItem.getThrower() != null) {
                                EntityPlayer playerEntityByName = this.worldObj.getPlayerEntityByName(entityItem.getThrower());
                                if (playerEntityByName != null) {
                                    playerEntityByName.triggerAchievement(AchievementList.DIAMONDS_TO_YOU);
                                }
                            }

                            this.setCurrentItemOrArmor(armorPosition, entityItem2);
                            this.equipmentDropChances[armorPosition] = 2.0F;
                            this.persistenceRequired = true;
                            this.onItemPickup(entityItem, 1);
                            entityItem.setDead();
                        }
                    }
                }
            }
        }

        this.worldObj.theProfiler.endSection();
    }

    @Override
    public boolean isAIEnabled() {
        return false;
    }

    public boolean canDespawn() {
        return true;
    }

    public void despawnEntity() {
        Object obj = null;
        Object fieldValue = Reflector.getFieldValue(Reflector.Event_Result_DEFAULT);
        Object fieldValue2 = Reflector.getFieldValue(Reflector.Event_Result_DENY);
        if (this.persistenceRequired) {
            this.entityAge = 0;
        } else if ((this.entityAge & 31) != 31 || (obj = Reflector.call(Reflector.ForgeEventFactory_canEntityDespawn, this)) == fieldValue) {
            EntityPlayer closestPlayerToEntity = this.worldObj.getClosestPlayerToEntity(this, -1.0);
            if (closestPlayerToEntity != null) {
                double d = closestPlayerToEntity.posX - this.posX;
                double d2 = closestPlayerToEntity.posY - this.posY;
                double d3 = closestPlayerToEntity.posZ - this.posZ;
                double d4 = d * d + d2 * d2 + d3 * d3;
                if (this.canDespawn() && d4 > 16384.0) {
                    this.setDead();
                }

                if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && d4 > 1024.0 && this.canDespawn()) {
                    this.setDead();
                } else if (d4 < 1024.0) {
                    this.entityAge = 0;
                }
            }
        } else if (obj == fieldValue2) {
            this.entityAge = 0;
        } else {
            this.setDead();
        }
    }

    @Override
    public void updateAITasks() {
        this.entityAge++;
        this.worldObj.theProfiler.startSection("checkDespawn");
        this.despawnEntity();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("sensing");
        this.senses.clearSensingCache();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("targetSelector");
        this.targetTasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("goalSelector");
        this.tasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("navigation");
        this.navigator.onUpdateNavigation();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("mob tick");
        this.updateAITick();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("controls");
        this.worldObj.theProfiler.startSection("move");
        this.moveHelper.onUpdateMoveHelper();
        this.worldObj.theProfiler.endStartSection("look");
        this.lookHelper.onUpdateLook();
        this.worldObj.theProfiler.endStartSection("jump");
        this.jumpHelper.doJump();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.endSection();
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;
        this.despawnEntity();
        float f = 8.0F;
        if (this.rand.nextFloat() < 0.02F) {
            EntityPlayer closestPlayerToEntity = this.worldObj.getClosestPlayerToEntity(this, f);
            if (closestPlayerToEntity != null) {
                this.currentTarget = closestPlayerToEntity;
                this.numTicksToChaseTarget = 10 + this.rand.nextInt(20);
            } else {
                this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
            }
        }

        if (this.currentTarget != null) {
            this.faceEntity(this.currentTarget, 10.0F, this.getVerticalFaceSpeed());
            if (this.numTicksToChaseTarget-- <= 0 || this.currentTarget.isDead || this.currentTarget.getDistanceSqToEntity(this) > f * f) {
                this.currentTarget = null;
            }
        } else {
            if (this.rand.nextFloat() < 0.05F) {
                this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F;
            }

            this.rotationYaw = this.rotationYaw + this.randomYawVelocity;
            this.rotationPitch = this.defaultPitch;
        }

        boolean isInWater = this.isInWater();
        boolean handleLavaMovement = this.handleLavaMovement();
        if (isInWater || handleLavaMovement) {
            this.isJumping = this.rand.nextFloat() < 0.8F;
        }
    }

    public int getVerticalFaceSpeed() {
        return 40;
    }

    public void faceEntity(Entity entity, float f, float f2) {
        double d = entity.posX - this.posX;
        double d2 = entity.posZ - this.posZ;
        double d3;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            d3 = entityLivingBase.posY + entityLivingBase.getEyeHeight() - (this.posY + this.getEyeHeight());
        } else {
            d3 = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (this.posY + this.getEyeHeight());
        }

        double d4 = MathHelper.sqrt_double(d * d + d2 * d2);
        float f3 = (float)(Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0F;
        float f4 = (float)(-(Math.atan2(d3, d4) * 180.0 / Math.PI));
        this.rotationPitch = this.updateRotation(this.rotationPitch, f4, f2);
        this.rotationYaw = this.updateRotation(this.rotationYaw, f3, f);
    }

    public float updateRotation(float f, float f2, float f3) {
        float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
        if (f4 > f3) {
            f4 = f3;
        }

        if (f4 < -f3) {
            f4 = -f3;
        }

        return f + f4;
    }

    public boolean getCanSpawnHere() {
        return this.worldObj.checkNoEntityCollision(this.boundingBox)
            && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
            && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    public float getRenderSizeModifier() {
        return 1.0F;
    }

    public int getMaxSpawnedInChunk() {
        return 4;
    }

    @Override
    public int getMaxFallHeight() {
        if (this.getAttackTarget() == null) {
            return 3;
        }

        int n = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
        n -= (3 - this.worldObj.difficultySetting.getDifficultyId()) * 4;
        if (n < 0) {
            n = 0;
        }

        return n + 3;
    }

    @Override
    public ItemStack getHeldItem() {
        return this.equipment[0];
    }

    @Override
    public ItemStack getEquipmentInSlot(int n) {
        return this.equipment[n];
    }

    public ItemStack findItemStack(int n) {
        return this.equipment[n + 1];
    }

    @Override
    public void setCurrentItemOrArmor(int slotIn, ItemStack itemStackIn) {
        this.equipment[slotIn] = itemStackIn;
    }

    @Override
    public ItemStack[] getInventory() {
        return this.equipment;
    }

    @Override
    public void dropEquipment(boolean flag, int n) {
        for (int i = 0; i < this.getInventory().length; i++) {
            ItemStack equipmentInSlot = this.getEquipmentInSlot(i);
            boolean flag2 = this.equipmentDropChances[i] > 1.0F;
            if (equipmentInSlot != null && (flag || flag2) && this.rand.nextFloat() - n * 0.01F < this.equipmentDropChances[i]) {
                if (!flag2 && equipmentInSlot.isItemStackDamageable()) {
                    int max = Math.max(equipmentInSlot.getMaxDurability() - 25, 1);
                    int n2 = equipmentInSlot.getMaxDurability() - this.rand.nextInt(this.rand.nextInt(max) + 1);
                    if (n2 > max) {
                        n2 = max;
                    }

                    if (n2 < 1) {
                        n2 = 1;
                    }

                    equipmentInSlot.setMetadata(n2);
                }

                this.entityDropItem(equipmentInSlot, 0.0F);
            }
        }
    }

    public void addRandomArmor() {
        if (this.rand.nextFloat() < 0.15F * this.worldObj.getTensionFactorForBlock(this.posX, this.posY, this.posZ)) {
            int nextInt = this.rand.nextInt(2);
            float f = this.worldObj.difficultySetting == EnumDifficulty.HARD ? 0.1F : 0.25F;
            if (this.rand.nextFloat() < 0.095F) {
                nextInt++;
            }

            if (this.rand.nextFloat() < 0.095F) {
                nextInt++;
            }

            if (this.rand.nextFloat() < 0.095F) {
                nextInt++;
            }

            for (int i = 3; i >= 0; i--) {
                ItemStack findItemStack = this.findItemStack(i);
                if (i < 3 && this.rand.nextFloat() < f) {
                    break;
                }

                if (findItemStack == null) {
                    Item armorItemForSlot = getArmorItemForSlot(i + 1, nextInt);
                    if (armorItemForSlot != null) {
                        this.setCurrentItemOrArmor(i + 1, new ItemStack(armorItemForSlot));
                    }
                }
            }
        }
    }

    public static int getArmorPosition(ItemStack itemStack) {
        if (itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin) || itemStack.getItem() == Items.skull) {
            return 4;
        }

        if (itemStack.getItem() instanceof ItemArmor) {
            switch (((ItemArmor)itemStack.getItem()).armorType) {
                case 0:
                    return 4;
                case 1:
                    return 3;
                case 2:
                    return 2;
                case 3:
                    return 1;
            }
        }

        return 0;
    }

    public static Item getArmorItemForSlot(int armorSlot, int itemTier) {
        switch (armorSlot) {
            case 4:
                if (itemTier == 0) {
                    return Items.leather_helmet;
                } else if (itemTier == 1) {
                    return Items.golden_helmet;
                } else if (itemTier == 2) {
                    return Items.chainmail_helmet;
                } else if (itemTier == 3) {
                    return Items.iron_helmet;
                } else if (itemTier == 4) {
                    return Items.diamond_helmet;
                }
            case 3:
                if (itemTier == 0) {
                    return Items.leather_chestplate;
                } else if (itemTier == 1) {
                    return Items.golden_chestplate;
                } else if (itemTier == 2) {
                    return Items.chainmail_chestplate;
                } else if (itemTier == 3) {
                    return Items.iron_chestplate;
                } else if (itemTier == 4) {
                    return Items.diamond_chestplate;
                }
            case 2:
                if (itemTier == 0) {
                    return Items.leather_leggings;
                } else if (itemTier == 1) {
                    return Items.golden_leggings;
                } else if (itemTier == 2) {
                    return Items.chainmail_leggings;
                } else if (itemTier == 3) {
                    return Items.iron_leggings;
                } else if (itemTier == 4) {
                    return Items.diamond_leggings;
                }
            case 1:
                if (itemTier == 0) {
                    return Items.leather_boots;
                } else if (itemTier == 1) {
                    return Items.golden_boots;
                } else if (itemTier == 2) {
                    return Items.chainmail_boots;
                } else if (itemTier == 3) {
                    return Items.iron_boots;
                } else if (itemTier == 4) {
                    return Items.diamond_boots;
                }
            default:
                return null;
        }
    }

    public void enchantEquipment() {
        float tensionFactorForBlock = this.worldObj.getTensionFactorForBlock(this.posX, this.posY, this.posZ);
        if (this.getHeldItem() != null && this.rand.nextFloat() < 0.25F * tensionFactorForBlock) {
            EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), (int)(5.0F + tensionFactorForBlock * this.rand.nextInt(18)));
        }

        for (int i = 0; i < 4; i++) {
            ItemStack findItemStack = this.findItemStack(i);
            if (findItemStack != null && this.rand.nextFloat() < 0.5F * tensionFactorForBlock) {
                EnchantmentHelper.addRandomEnchantment(this.rand, findItemStack, (int)(5.0F + tensionFactorForBlock * this.rand.nextInt(18)));
            }
        }
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData) {
        this.getEntityAttribute(SharedMonsterAttributes.followRange)
            .applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05, 1));
        return entityLivingData;
    }

    public boolean canBeSteered() {
        return false;
    }

    @Override
    public String getCommandSenderName() {
        return this.hasCustomNameTag() ? this.getCustomNameTag() : super.getCommandSenderName();
    }

    public void enablePersistence() {
        this.persistenceRequired = true;
    }

    public void setCustomNameTag(String str) {
        this.dataWatcher.updateObject(10, str);
    }

    public String getCustomNameTag() {
        return this.dataWatcher.getWatchableObjectString(10);
    }

    public boolean hasCustomNameTag() {
        return this.dataWatcher.getWatchableObjectString(10).length() > 0;
    }

    public void setAlwaysRenderNameTag(boolean flag) {
        this.dataWatcher.updateObject(11, (byte)(flag ? 1 : 0));
    }

    public boolean getAlwaysRenderNameTag() {
        return this.dataWatcher.getWatchableObjectByte(11) == 1;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return this.getAlwaysRenderNameTag();
    }

    public void setEquipmentDropChance(int n, float f) {
        this.equipmentDropChances[n] = f;
    }

    public boolean canPickUpLoot() {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean flag) {
        this.canPickUpLoot = flag;
    }

    public boolean isNoDespawnRequired() {
        return this.persistenceRequired;
    }

    @Override
    public boolean interactFirst(EntityPlayer player) throws java.io.IOException {
        if (this.getLeashed() && this.getLeashedToEntity() == player) {
            this.clearLeashed(true, !player.capabilities.isCreativeMode);
            return true;
        }

        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == Items.lead && this.allowLeashing()) {
            if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed()) {
                this.setLeashedToEntity(player, true);
                currentItem.stackSize--;
                return true;
            }

            if (((EntityTameable)this).isOwner(player)) {
                this.setLeashedToEntity(player, true);
                currentItem.stackSize--;
                return true;
            }
        }

        return this.interact(player) || super.interactFirst(player);
    }

    public boolean interact(EntityPlayer entityPlayer) throws java.io.IOException {
        return false;
    }

    public void updateLeashedState() {
        if (this.leashNBTTag != null) {
            this.recreateLeash();
        }

        if (this.isLeashed && (this.leashedToEntity == null || this.leashedToEntity.isDead)) {
            this.clearLeashed(true, true);
        }
    }

    public void clearLeashed(boolean flag, boolean flag2) {
        if (this.isLeashed) {
            this.isLeashed = false;
            this.leashedToEntity = null;
            if (!this.worldObj.isRemote && flag2) {
                this.dropItem(Items.lead, 1);
            }

            if (!this.worldObj.isRemote && flag && this.worldObj instanceof WorldServer) {
                ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, null));
            }
        }
    }

    public boolean allowLeashing() {
        return !this.getLeashed() && !(this instanceof IMob);
    }

    public boolean getLeashed() {
        return this.isLeashed;
    }

    public Entity getLeashedToEntity() {
        return this.leashedToEntity;
    }

    public void setLeashedToEntity(Entity entityIn, boolean sendAttachNotification) {
        this.isLeashed = true;
        this.leashedToEntity = entityIn;
        if (!this.worldObj.isRemote && sendAttachNotification && this.worldObj instanceof WorldServer) {
            ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, this.leashedToEntity));
        }
    }

    public void recreateLeash() {
        if (this.isLeashed && this.leashNBTTag != null) {
            if (this.leashNBTTag.hasKey("UUIDMost", 4) && this.leashNBTTag.hasKey("UUIDLeast", 4)) {
                UUID uuid = new UUID(this.leashNBTTag.getLong("UUIDMost"), this.leashNBTTag.getLong("UUIDLeast"));

                for (Object obj : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(10.0, 10.0, 10.0))) {
                    EntityLivingBase entityLivingBase = (EntityLivingBase)obj;
                    if (entityLivingBase.getUniqueID().equals(uuid)) {
                        this.leashedToEntity = entityLivingBase;
                        break;
                    }
                }
            } else if (this.leashNBTTag.hasKey("X", 99) && this.leashNBTTag.hasKey("Y", 99) && this.leashNBTTag.hasKey("Z", 99)) {
                int integer = this.leashNBTTag.getInteger("X");
                int integer2 = this.leashNBTTag.getInteger("Y");
                int integer3 = this.leashNBTTag.getInteger("Z");
                EntityLeashKnot knotForBlock = EntityLeashKnot.getKnotForBlock(this.worldObj, integer, integer2, integer3);
                if (knotForBlock == null) {
                    knotForBlock = EntityLeashKnot.createEntityLeashKnot(this.worldObj, integer, integer2, integer3);
                }

                this.leashedToEntity = knotForBlock;
            } else {
                this.clearLeashed(false, true);
            }
        }

        this.leashNBTTag = null;
    }
}
