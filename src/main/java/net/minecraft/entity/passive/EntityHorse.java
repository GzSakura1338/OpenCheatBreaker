package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityHorse extends EntityAnimal implements IInvBasic {
    public static IEntitySelector horseBreedingSelector = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity instanceof EntityHorse && ((EntityHorse)entity).isReadyToBreed();
        }
    };
    public static IAttribute horseJumpStrength = new RangedAttribute("horse.jumpStrength", 0.7, 0.0, 2.0).setDescription("Jump Strength").setShouldWatch(true);
    public static String[] horseArmorTextures = new String[]{
        null,
        "textures/entity/horse/armor/horse_armor_iron.png",
        "textures/entity/horse/armor/horse_armor_gold.png",
        "textures/entity/horse/armor/horse_armor_diamond.png"
    };
    public static String[] stringArray = new String[]{"", "meo", "goo", "dio"};
    public static int[] armorValues = new int[]{0, 5, 7, 11};
    public static String[] horseTextures = new String[]{
        "textures/entity/horse/horse_white.png",
        "textures/entity/horse/horse_creamy.png",
        "textures/entity/horse/horse_chestnut.png",
        "textures/entity/horse/horse_brown.png",
        "textures/entity/horse/horse_black.png",
        "textures/entity/horse/horse_gray.png",
        "textures/entity/horse/horse_darkbrown.png"
    };
    public static String[] stringArray2 = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
    public static String[] horseMarkingTextures = new String[]{
        null,
        "textures/entity/horse/horse_markings_white.png",
        "textures/entity/horse/horse_markings_whitefield.png",
        "textures/entity/horse/horse_markings_whitedots.png",
        "textures/entity/horse/horse_markings_blackdots.png"
    };
    public static String[] stringArray3 = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
    public int eatingHaystackCounter;
    public int openMouthCounter;
    public int jumpRearingCounter;
    public int intValue;
    public int intValue2;
    public boolean horseJumping;
    public AnimalChest horseChest;
    public boolean hasReproduced;
    public int temper;
    public float jumpPower;
    public boolean jumping;
    public float headLean;
    public float prevHeadLean;
    public float rearingAmount;
    public float prevRearingAmount;
    public float mouthOpenness;
    public float prevMouthOpenness;
    public int gallopTime;
    public String text;
    public String[] stringArray4 = new String[3];

    public EntityHorse(World world) {
        super(world);
        this.setSize(1.4F, 1.6F);
        this.isImmuneToFire = false;
        this.setChested(false);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.2));
        this.tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.0));
        this.tasks.addTask(6, new EntityAIWander(this, 0.7));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.run();
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, 0);
        this.dataWatcher.addObject(19, (byte)0);
        this.dataWatcher.addObject(20, 0);
        this.dataWatcher.addObject(21, "");
        this.dataWatcher.addObject(22, 0);
    }

    public void setHorseType(int n) {
        this.dataWatcher.updateObject(19, (byte)n);
        this.run2();
    }

    public int getHorseType() {
        return this.dataWatcher.getWatchableObjectByte(19);
    }

    public void setHorseVariant(int n) {
        this.dataWatcher.updateObject(20, n);
        this.run2();
    }

    public int getHorseVariant() {
        return this.dataWatcher.getWatchableObjectInt(20);
    }

    @Override
    public String getCommandSenderName() {
        if (this.hasCustomNameTag()) {
            return this.getCustomNameTag();
        }

        int horseType = this.getHorseType();
        switch (horseType) {
            case 0:
            default:
                return StatCollector.translateToLocal("entity.horse.name");
            case 1:
                return StatCollector.translateToLocal("entity.donkey.name");
            case 2:
                return StatCollector.translateToLocal("entity.mule.name");
            case 3:
                return StatCollector.translateToLocal("entity.zombiehorse.name");
            case 4:
                return StatCollector.translateToLocal("entity.skeletonhorse.name");
        }
    }

    public boolean getHorseWatchableBoolean(int n) {
        return (this.dataWatcher.getWatchableObjectInt(16) & n) != 0;
    }

    public void setHorseWatchableBoolean(int n, boolean flag) {
        int watchableObjectInt = this.dataWatcher.getWatchableObjectInt(16);
        if (flag) {
            this.dataWatcher.updateObject(16, watchableObjectInt | n);
        } else {
            this.dataWatcher.updateObject(16, watchableObjectInt & ~n);
        }
    }

    public boolean isAdultHorse() {
        return !this.isChild();
    }

    public boolean isTame() {
        return this.getHorseWatchableBoolean(2);
    }

    public boolean isAdultHorse2() {
        return this.isAdultHorse();
    }

    public String getOwnerUuid() {
        return this.dataWatcher.getWatchableObjectString(21);
    }

    public void updateObject(String str) {
        this.dataWatcher.updateObject(21, str);
    }

    public float getHorseSize() {
        int growingAge = this.getGrowingAge();
        return growingAge >= 0 ? 1.0F : 0.5F + (-24000 - growingAge) / -24000.0F * 0.5F;
    }

    @Override
    public void setScaleForAge(boolean flag) {
        if (flag) {
            this.setScale(this.getHorseSize());
        } else {
            this.setScale(1.0F);
        }
    }

    public boolean isHorseJumping() {
        return this.horseJumping;
    }

    public void setHorseTamed(boolean flag) {
        this.setHorseWatchableBoolean(2, flag);
    }

    public void setHorseJumping(boolean flag) {
        this.horseJumping = flag;
    }

    @Override
    public boolean allowLeashing() {
        return !this.isUndead() && super.allowLeashing();
    }

    @Override
    public void process(float f) {
        if (f > 6.0F && this.isEatingHaystack()) {
            this.setEatingHaystack(false);
        }
    }

    public boolean isChested() {
        return this.getHorseWatchableBoolean(8);
    }

    public int getWatchableObjectInt() {
        return this.dataWatcher.getWatchableObjectInt(22);
    }

    public int getHorseArmorIndex(ItemStack itemStack) {
        if (itemStack == null) {
            return 0;
        }

        Item item = itemStack.getItem();
        return item == Items.iron_horse_armor ? 1 : (item == Items.golden_horse_armor ? 2 : (item == Items.diamond_horse_armor ? 3 : 0));
    }

    public boolean isEatingHaystack() {
        return this.getHorseWatchableBoolean(32);
    }

    public boolean isRearing() {
        return this.getHorseWatchableBoolean(64);
    }

    public boolean isReadyToBreed() {
        return this.getHorseWatchableBoolean(16);
    }

    public boolean getHasReproduced() {
        return this.hasReproduced;
    }

    public void setHorseArmorStack(ItemStack itemStack) {
        this.dataWatcher.updateObject(22, this.getHorseArmorIndex(itemStack));
        this.run2();
    }

    public void setBred(boolean flag) {
        this.setHorseWatchableBoolean(16, flag);
    }

    public void setChested(boolean flag) {
        this.setHorseWatchableBoolean(8, flag);
    }

    public void setHasReproduced(boolean flag) {
        this.hasReproduced = flag;
    }

    public void setHorseSaddled(boolean flag) {
        this.setHorseWatchableBoolean(4, flag);
    }

    public int getTemper() {
        return this.temper;
    }

    public void setTemper(int n) {
        this.temper = n;
    }

    public int increaseTemper(int n) {
        int n2 = MathHelper.clamp_int(this.getTemper() + n, 0, this.getMaxTemper());
        this.setTemper(n2);
        return n2;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        Entity entity = source.getEntity();
        return (this.riddenByEntity == null || !this.riddenByEntity.equals(entity)) && super.attackEntityFrom(source, amount);
    }

    @Override
    public int getTotalArmorValue() {
        return armorValues[this.getWatchableObjectInt()];
    }

    @Override
    public boolean canBePushed() {
        return this.riddenByEntity == null;
    }

    public boolean prepareChunkForSpawn() {
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.posZ);
        this.worldObj.getBiomeGenForCoords(n, n2);
        return true;
    }

    public void dropChests() {
        if (!this.worldObj.isRemote && this.isChested()) {
            this.dropItem(Item.getItemFromBlock(Blocks.chest), 1);
            this.setChested(false);
        }
    }

    public void playEatingAnimation() {
        this.openHorseMouth();
        this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
    }

    @Override
    public void fall(float distance) {
        if (distance > 1.0F) {
            this.playSound("mob.horse.land", 0.4F, 1.0F);
        }

        int n = MathHelper.ceiling_float_int(distance * 0.5F - 3.0F);
        if (n > 0) {
            this.attackEntityFrom(DamageSource.fall, n);
            if (this.riddenByEntity != null) {
                this.riddenByEntity.attackEntityFrom(DamageSource.fall, n);
            }

            Block block = this.worldObj
                .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 0.2 - this.prevRotationYaw), MathHelper.floor_double(this.posZ));
            if (block.getMaterial() != Material.air) {
                SoundType stepSound = block.stepSound;
                this.worldObj.playSoundAtEntity(this, stepSound.getStepSound(), stepSound.getVolume() * 0.5F, stepSound.getFrequency() * 0.75F);
            }
        }
    }

    public int getIntValue() {
        int horseType = this.getHorseType();
        return !this.isChested() || horseType != 1 && horseType != 2 ? 2 : 17;
    }

    public void run() {
        AnimalChest horseChest = this.horseChest;
        this.horseChest = new AnimalChest("HorseChest", this.getIntValue());
        this.horseChest.setName(this.getCommandSenderName());
        if (horseChest != null) {
            horseChest.removeListener(this);
            int min = Math.min(horseChest.getSizeInventory(), this.horseChest.getSizeInventory());

            for (int i = 0; i < min; i++) {
                ItemStack stackInSlot = horseChest.getStackInSlot(i);
                if (stackInSlot != null) {
                    this.horseChest.setInventorySlotContents(i, stackInSlot.copy());
                }
            }

            Object obj = null;
        }

        this.horseChest.addListener(this);
        this.updateSaddle();
    }

    public void updateSaddle() {
        if (!this.worldObj.isRemote) {
            this.setHorseSaddled(this.horseChest.getStackInSlot(0) != null);
            if (this.canWearArmor()) {
                this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
            }
        }
    }

    @Override
    public void onInventoryChanged(InventoryBasic inventoryBasic) {
        int watchableObjectInt = this.getWatchableObjectInt();
        boolean isHorseSaddled = this.isHorseSaddled();
        this.updateSaddle();
        if (this.ticksExisted > 20) {
            if (watchableObjectInt == 0 && watchableObjectInt != this.getWatchableObjectInt()) {
                this.playSound("mob.horse.armor", 0.5F, 1.0F);
            } else if (watchableObjectInt != this.getWatchableObjectInt()) {
                this.playSound("mob.horse.armor", 0.5F, 1.0F);
            }

            if (!isHorseSaddled && this.isHorseSaddled()) {
                this.playSound("mob.horse.leather", 0.5F, 1.0F);
            }
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        this.prepareChunkForSpawn();
        return super.getCanSpawnHere();
    }

    public EntityHorse getClosestHorse(Entity entity, double d) {
        double d2 = Double.MAX_VALUE;
        Entity entity2 = null;

        for (Entity entity3 : (Iterable<Entity>)this.worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.addCoord(d, d, d), horseBreedingSelector)) {
            double distanceSq = entity3.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (distanceSq < d2) {
                entity2 = entity3;
                d2 = distanceSq;
            }
        }

        return (EntityHorse)entity2;
    }

    public double getHorseJumpStrength() {
        return this.getEntityAttribute(horseJumpStrength).getAttributeValue();
    }

    @Override
    public String getDeathSound() {
        this.openHorseMouth();
        int horseType = this.getHorseType();
        return horseType == 3
            ? "mob.horse.zombie.death"
            : (horseType == 4 ? "mob.horse.skeleton.death" : (horseType != 1 && horseType != 2 ? "mob.horse.death" : "mob.horse.donkey.death"));
    }

    @Override
    public Item getDropItem() {
        boolean flag = this.rand.nextInt(4) == 0;
        int horseType = this.getHorseType();
        return horseType == 4 ? Items.bone : (horseType == 3 ? (flag ? Item.getItemById(0) : Items.rotten_flesh) : Items.leather);
    }

    @Override
    public String getHurtSound() {
        this.openHorseMouth();
        if (this.rand.nextInt(3) == 0) {
            this.makeHorseRear();
        }

        int horseType = this.getHorseType();
        return horseType == 3 ? "mob.horse.zombie.hit" : (horseType == 4 ? "mob.horse.skeleton.hit" : (horseType != 1 && horseType != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit"));
    }

    public boolean isHorseSaddled() {
        return this.getHorseWatchableBoolean(4);
    }

    @Override
    public String getLivingSound() {
        this.openHorseMouth();
        if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked()) {
            this.makeHorseRear();
        }

        int horseType = this.getHorseType();
        return horseType == 3
            ? "mob.horse.zombie.idle"
            : (horseType == 4 ? "mob.horse.skeleton.idle" : (horseType != 1 && horseType != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle"));
    }

    public String getAngrySoundName() {
        this.openHorseMouth();
        this.makeHorseRear();
        int horseType = this.getHorseType();
        return horseType != 3 && horseType != 4 ? (horseType != 1 && horseType != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry") : null;
    }

    @Override
    public void playStepSound(int x, int y, int z, Block blockIn) {
        SoundType stepSound = blockIn.stepSound;
        if (this.worldObj.getBlock(x, y + 1, z) == Blocks.snow_layer) {
            stepSound = Blocks.snow_layer.stepSound;
        }

        if (!blockIn.getMaterial().isLiquid()) {
            int horseType = this.getHorseType();
            if (this.riddenByEntity != null && horseType != 1 && horseType != 2) {
                this.gallopTime++;
                if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
                    this.playSound("mob.horse.gallop", stepSound.getVolume() * 0.15F, stepSound.getFrequency());
                    if (horseType == 0 && this.rand.nextInt(10) == 0) {
                        this.playSound("mob.horse.breathe", stepSound.getVolume() * 0.6F, stepSound.getFrequency());
                    }
                } else if (this.gallopTime <= 5) {
                    this.playSound("mob.horse.wood", stepSound.getVolume() * 0.15F, stepSound.getFrequency());
                }
            } else if (stepSound == Block.soundTypeWood) {
                this.playSound("mob.horse.wood", stepSound.getVolume() * 0.15F, stepSound.getFrequency());
            } else {
                this.playSound("mob.horse.soft", stepSound.getVolume() * 0.15F, stepSound.getFrequency());
            }
        }
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(horseJumpStrength);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(53.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.225F);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 6;
    }

    public int getMaxTemper() {
        return 100;
    }

    @Override
    public float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public int getTalkInterval() {
        return 400;
    }

    public boolean check() {
        return this.getHorseType() == 0 || this.getWatchableObjectInt() > 0;
    }

    public void run2() {
        this.text = null;
    }

    public void setHorseTexturePaths() {
        this.text = "horse/";
        this.stringArray4[0] = null;
        this.stringArray4[1] = null;
        this.stringArray4[2] = null;
        int horseType = this.getHorseType();
        int horseVariant = this.getHorseVariant();
        if (horseType == 0) {
            int n = horseVariant & 0xFF;
            int n2 = (horseVariant & 0xFF00) >> 8;
            this.stringArray4[0] = horseTextures[n];
            this.text = this.text + stringArray2[n];
            this.stringArray4[1] = horseMarkingTextures[n2];
            this.text = this.text + stringArray3[n2];
        } else {
            this.stringArray4[0] = "";
            this.text = this.text + "_" + horseType + "_";
        }

        int watchableObjectInt = this.getWatchableObjectInt();
        this.stringArray4[2] = horseArmorTextures[watchableObjectInt];
        this.text = this.text + stringArray[watchableObjectInt];
    }

    public String getHorseTexture() {
        if (this.text == null) {
            this.setHorseTexturePaths();
        }

        return this.text;
    }

    public String[] getVariantTexturePaths() {
        if (this.text == null) {
            this.setHorseTexturePaths();
        }

        return this.stringArray4;
    }

    public void openGUI(EntityPlayer entityPlayer) throws java.io.IOException {
        if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == entityPlayer) && this.isTame()) {
            this.horseChest.setName(this.getCommandSenderName());
            entityPlayer.displayGUIHorse(this, this.horseChest);
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) throws java.io.IOException {
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == Items.spawn_egg) {
            return super.interact(entityPlayer);
        }

        if (!this.isTame() && this.isUndead()) {
            return false;
        }

        if (this.isTame() && this.isAdultHorse() && entityPlayer.isSneaking()) {
            this.openGUI(entityPlayer);
            return true;
        }

        if (this.isAdultHorse2() && this.riddenByEntity != null) {
            return super.interact(entityPlayer);
        }

        if (currentItem != null) {
            boolean flag = false;
            if (this.canWearArmor()) {
                byte b = -1;
                if (currentItem.getItem() == Items.iron_horse_armor) {
                    b = 1;
                } else if (currentItem.getItem() == Items.golden_horse_armor) {
                    b = 2;
                } else if (currentItem.getItem() == Items.diamond_horse_armor) {
                    b = 3;
                }

                if (b >= 0) {
                    if (!this.isTame()) {
                        this.makeHorseRearWithSound();
                        return true;
                    }

                    this.openGUI(entityPlayer);
                    return true;
                }
            }

            if (!flag && !this.isUndead()) {
                float f = 0.0F;
                short s = 0;
                byte b = 0;
                if (currentItem.getItem() == Items.wheat) {
                    f = 2.0F;
                    s = 60;
                    b = 3;
                } else if (currentItem.getItem() == Items.sugar) {
                    f = 1.0F;
                    s = 30;
                    b = 3;
                } else if (currentItem.getItem() == Items.bread) {
                    f = 7.0F;
                    s = 180;
                    b = 3;
                } else if (Block.getBlockFromItem(currentItem.getItem()) == Blocks.hay_block) {
                    f = 20.0F;
                    s = 180;
                } else if (currentItem.getItem() == Items.apple) {
                    f = 3.0F;
                    s = 60;
                    b = 3;
                } else if (currentItem.getItem() == Items.golden_carrot) {
                    f = 4.0F;
                    s = 60;
                    b = 5;
                    if (this.isTame() && this.getGrowingAge() == 0) {
                        flag = true;
                        this.setInLove(entityPlayer);
                    }
                } else if (currentItem.getItem() == Items.golden_apple) {
                    f = 10.0F;
                    s = 240;
                    b = 10;
                    if (this.isTame() && this.getGrowingAge() == 0) {
                        flag = true;
                        this.setInLove(entityPlayer);
                    }
                }

                if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
                    this.heal(f);
                    flag = true;
                }

                if (!this.isAdultHorse() && s > 0) {
                    this.addGrowth(s);
                    flag = true;
                }

                if (b > 0 && (flag || !this.isTame()) && b < this.getMaxTemper()) {
                    flag = true;
                    this.increaseTemper(b);
                }

                if (flag) {
                    this.playEatingAnimation();
                }
            }

            if (!this.isTame() && !flag) {
                if (currentItem != null && currentItem.interactWithEntity(entityPlayer, this)) {
                    return true;
                }

                this.makeHorseRearWithSound();
                return true;
            }

            if (!flag && this.canCarryChest() && !this.isChested() && currentItem.getItem() == Item.getItemFromBlock(Blocks.chest)) {
                this.setChested(true);
                this.playSound("mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                flag = true;
                this.run();
            }

            if (!flag && this.isAdultHorse2() && !this.isHorseSaddled() && currentItem.getItem() == Items.saddle) {
                this.openGUI(entityPlayer);
                return true;
            }

            if (flag) {
                if (!entityPlayer.capabilities.isCreativeMode && --currentItem.stackSize == 0) {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                }

                return true;
            }
        }

        if (!this.isAdultHorse2() || this.riddenByEntity != null) {
            return super.interact(entityPlayer);
        }

        if (currentItem != null && currentItem.interactWithEntity(entityPlayer, this)) {
            return true;
        }

        this.putPlayerOnBack(entityPlayer);
        return true;
    }

    public void putPlayerOnBack(EntityPlayer entityPlayer) {
        entityPlayer.rotationYaw = this.rotationYaw;
        entityPlayer.rotationPitch = this.rotationPitch;
        this.setEatingHaystack(false);
        this.setRearing(false);
        if (!this.worldObj.isRemote) {
            entityPlayer.mountEntity(this);
        }
    }

    public boolean canWearArmor() {
        return this.getHorseType() == 0;
    }

    public boolean canCarryChest() {
        int horseType = this.getHorseType();
        return horseType == 2 || horseType == 1;
    }

    @Override
    public boolean isMovementBlocked() {
        return this.riddenByEntity != null && this.isHorseSaddled() || this.isEatingHaystack() || this.isRearing();
    }

    public boolean isUndead() {
        int horseType = this.getHorseType();
        return horseType == 3 || horseType == 4;
    }

    public boolean isSterile() {
        return this.isUndead() || this.getHorseType() == 2;
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return false;
    }

    public void run3() {
        this.intValue = 1;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.worldObj.isRemote) {
            this.dropChestItems();
        }
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        if (this.rand.nextInt(200) == 0) {
            this.run3();
        }

        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
                this.heal(1.0F);
            }

            if (!this.isEatingHaystack()
                && this.riddenByEntity == null
                && this.rand.nextInt(300) == 0
                && this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ))
                    == Blocks.grass) {
                this.setEatingHaystack(true);
            }

            if (this.isEatingHaystack() && ++this.eatingHaystackCounter > 50) {
                this.eatingHaystackCounter = 0;
                this.setEatingHaystack(false);
            }

            if (this.isReadyToBreed() && !this.isAdultHorse() && !this.isEatingHaystack()) {
                EntityHorse closestHorse = this.getClosestHorse(this, 16.0);
                if (closestHorse != null && this.getDistanceSqToEntity(closestHorse) > 4.0) {
                    PathEntity pathEntityToEntity = this.worldObj.getPathEntityToEntity(this, closestHorse, 16.0F, true, false, false, true);
                    this.setPathToEntity(pathEntityToEntity);
                }
            }
        }
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (this.worldObj.isRemote && this.dataWatcher.hasObjectChanged()) {
            this.dataWatcher.clearDirty();
            this.run2();
        }

        if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30) {
            this.openMouthCounter = 0;
            this.setHorseWatchableBoolean(128, false);
        }

        if (!this.worldObj.isRemote && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20) {
            this.jumpRearingCounter = 0;
            this.setRearing(false);
        }

        if (this.intValue > 0 && ++this.intValue > 8) {
            this.intValue = 0;
        }

        if (this.intValue2 > 0) {
            this.intValue2++;
            if (this.intValue2 > 300) {
                this.intValue2 = 0;
            }
        }

        this.prevHeadLean = this.headLean;
        if (this.isEatingHaystack()) {
            this.headLean = this.headLean + ((1.0F - this.headLean) * 0.4F + 0.05F);
            if (this.headLean > 1.0F) {
                this.headLean = 1.0F;
            }
        } else {
            this.headLean = this.headLean + ((0.0F - this.headLean) * 0.4F - 0.05F);
            if (this.headLean < 0.0F) {
                this.headLean = 0.0F;
            }
        }

        this.prevRearingAmount = this.rearingAmount;
        if (this.isRearing()) {
            this.prevHeadLean = this.headLean = 0.0F;
            this.rearingAmount = this.rearingAmount + ((1.0F - this.rearingAmount) * 0.4F + 0.05F);
            if (this.rearingAmount > 1.0F) {
                this.rearingAmount = 1.0F;
            }
        } else {
            this.jumping = false;
            this.rearingAmount = this.rearingAmount + ((0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F - 0.05F);
            if (this.rearingAmount < 0.0F) {
                this.rearingAmount = 0.0F;
            }
        }

        this.prevMouthOpenness = this.mouthOpenness;
        if (this.getHorseWatchableBoolean(128)) {
            this.mouthOpenness = this.mouthOpenness + ((1.0F - this.mouthOpenness) * 0.7F + 0.05F);
            if (this.mouthOpenness > 1.0F) {
                this.mouthOpenness = 1.0F;
            }
        } else {
            this.mouthOpenness = this.mouthOpenness + ((0.0F - this.mouthOpenness) * 0.7F - 0.05F);
            if (this.mouthOpenness < 0.0F) {
                this.mouthOpenness = 0.0F;
            }
        }
    }

    public void openHorseMouth() {
        if (!this.worldObj.isRemote) {
            this.openMouthCounter = 1;
            this.setHorseWatchableBoolean(128, true);
        }
    }

    public boolean canMate() {
        return this.riddenByEntity == null
            && this.ridingEntity == null
            && this.isTame()
            && this.isAdultHorse()
            && !this.isSterile()
            && this.getHealth() >= this.getMaxHealth();
    }

    @Override
    public void setEating(boolean eating) {
        this.setHorseWatchableBoolean(32, eating);
    }

    public void setEatingHaystack(boolean flag) {
        this.setEating(flag);
    }

    public void setRearing(boolean flag) {
        if (flag) {
            this.setEatingHaystack(false);
        }

        this.setHorseWatchableBoolean(64, flag);
    }

    public void makeHorseRear() {
        if (!this.worldObj.isRemote) {
            this.jumpRearingCounter = 1;
            this.setRearing(true);
        }
    }

    public void makeHorseRearWithSound() {
        this.makeHorseRear();
        String angrySoundName = this.getAngrySoundName();
        if (angrySoundName != null) {
            this.playSound(angrySoundName, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    public void dropChestItems() {
        this.dropItemsInChest(this, this.horseChest);
        this.dropChests();
    }

    public void dropItemsInChest(Entity entity, AnimalChest animalChest) {
        if (animalChest != null && !this.worldObj.isRemote) {
            for (int i = 0; i < animalChest.getSizeInventory(); i++) {
                ItemStack stackInSlot = animalChest.getStackInSlot(i);
                if (stackInSlot != null) {
                    this.entityDropItem(stackInSlot, 0.0F);
                }
            }
        }
    }

    public boolean setTamedBy(EntityPlayer entityPlayer) {
        this.updateObject(entityPlayer.getUniqueID().toString());
        this.setHorseTamed(true);
        return true;
    }

    @Override
    public void moveEntityWithHeading(float f, float f2) {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase && this.isHorseSaddled()) {
            this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
            this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            f = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
            f2 = ((EntityLivingBase)this.riddenByEntity).moveForward;
            if (f2 <= 0.0F) {
                f2 *= 0.25F;
                this.gallopTime = 0;
            }

            if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.jumping) {
                f = 0.0F;
                f2 = 0.0F;
            }

            if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
                this.motionY = this.getHorseJumpStrength() * this.jumpPower;
                if (this.isPotionActive(Potion.jump)) {
                    this.motionY = this.motionY + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
                }

                this.setHorseJumping(true);
                this.isAirBorne = true;
                if (f2 > 0.0F) {
                    float sin = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
                    float cos = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
                    this.motionX = this.motionX + -0.4F * sin * this.jumpPower;
                    this.motionZ = this.motionZ + 0.4F * cos * this.jumpPower;
                    this.playSound("mob.horse.jump", 0.4F, 1.0F);
                }

                this.jumpPower = 0.0F;
            }

            this.stepHeight = 1.0F;
            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
            if (!this.worldObj.isRemote) {
                this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
                super.moveEntityWithHeading(f, f2);
            }

            if (this.onGround) {
                this.jumpPower = 0.0F;
                this.setHorseJumping(false);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d = this.posX - this.prevPosX;
            double d2 = this.posZ - this.prevPosZ;
            float f3 = MathHelper.sqrt_double(d * d + d2 * d2) * 4.0F;
            if (f3 > 1.0F) {
                f3 = 1.0F;
            }

            this.limbSwingAmount = this.limbSwingAmount + (f3 - this.limbSwingAmount) * 0.4F;
            this.limbSwing = this.limbSwing + this.limbSwingAmount;
        } else {
            this.stepHeight = 0.5F;
            this.jumpMovementFactor = 0.02F;
            super.moveEntityWithHeading(f, f2);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("EatingHaystack", this.isEatingHaystack());
        tagCompound.setBoolean("ChestedHorse", this.isChested());
        tagCompound.setBoolean("HasReproduced", this.getHasReproduced());
        tagCompound.setBoolean("Bred", this.isReadyToBreed());
        tagCompound.setInteger("Type", this.getHorseType());
        tagCompound.setInteger("Variant", this.getHorseVariant());
        tagCompound.setInteger("Temper", this.getTemper());
        tagCompound.setBoolean("Tame", this.isTame());
        tagCompound.setString("OwnerUUID", this.getOwnerUuid());
        if (this.isChested()) {
            NBTTagList nbttagList = new NBTTagList();

            for (int i = 2; i < this.horseChest.getSizeInventory(); i++) {
                ItemStack stackInSlot = this.horseChest.getStackInSlot(i);
                if (stackInSlot != null) {
                    NBTTagCompound nbttagCompound = new NBTTagCompound();
                    nbttagCompound.setByte("Slot", (byte)i);
                    stackInSlot.writeToNBT(nbttagCompound);
                    nbttagList.appendTag(nbttagCompound);
                }
            }

            tagCompound.setTag("Items", nbttagList);
        }

        if (this.horseChest.getStackInSlot(1) != null) {
            tagCompound.setTag("ArmorItem", this.horseChest.getStackInSlot(1).writeToNBT(new NBTTagCompound()));
        }

        if (this.horseChest.getStackInSlot(0) != null) {
            tagCompound.setTag("SaddleItem", this.horseChest.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setEatingHaystack(tagCompund.getBoolean("EatingHaystack"));
        this.setBred(tagCompund.getBoolean("Bred"));
        this.setChested(tagCompund.getBoolean("ChestedHorse"));
        this.setHasReproduced(tagCompund.getBoolean("HasReproduced"));
        this.setHorseType(tagCompund.getInteger("Type"));
        this.setHorseVariant(tagCompund.getInteger("Variant"));
        this.setTemper(tagCompund.getInteger("Temper"));
        this.setHorseTamed(tagCompund.getBoolean("Tame"));
        if (tagCompund.hasKey("OwnerUUID", 8)) {
            this.updateObject(tagCompund.getString("OwnerUUID"));
        }

        IAttributeInstance attributeInstanceByName = this.getAttributeMap().getAttributeInstanceByName("Speed");
        if (attributeInstanceByName != null) {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(attributeInstanceByName.getBaseValue() * 0.25);
        }

        if (this.isChested()) {
            NBTTagList tagList = tagCompund.getTagList("Items", 10);
            this.run();

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                int n = compoundTagAt.getByte("Slot") & 255;
                if (n >= 2 && n < this.horseChest.getSizeInventory()) {
                    this.horseChest.setInventorySlotContents(n, ItemStack.loadItemStackFromNBT(compoundTagAt));
                }
            }
        }

        if (tagCompund.hasKey("ArmorItem", 10)) {
            ItemStack loadItemStackFromNBT = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("ArmorItem"));
            if (loadItemStackFromNBT != null && isHorseArmor(loadItemStackFromNBT.getItem())) {
                this.horseChest.setInventorySlotContents(1, loadItemStackFromNBT);
            }
        }

        if (tagCompund.hasKey("SaddleItem", 10)) {
            ItemStack loadItemStackFromNBT = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("SaddleItem"));
            if (loadItemStackFromNBT != null && loadItemStackFromNBT.getItem() == Items.saddle) {
                this.horseChest.setInventorySlotContents(0, loadItemStackFromNBT);
            }
        } else if (tagCompund.getBoolean("Saddle")) {
            this.horseChest.setInventorySlotContents(0, new ItemStack(Items.saddle));
        }

        this.updateSaddle();
    }

    @Override
    public boolean canMateWith(EntityAnimal entityAnimal) {
        if (entityAnimal == this) {
            return false;
        } else if (entityAnimal.getClass() != this.getClass()) {
            return false;
        } else {
            EntityHorse entityHorse = (EntityHorse)entityAnimal;
            if (this.canMate() && entityHorse.canMate()) {
                int horseType = this.getHorseType();
                int horseType2 = entityHorse.getHorseType();
                return horseType == horseType2 || horseType == 0 && horseType2 == 1 || horseType == 1 && horseType2 == 0;
            } else {
                return false;
            }
        }
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        EntityHorse entityHorse = (EntityHorse)entityAgeable;
        EntityHorse entityHorse2 = new EntityHorse(this.worldObj);
        int horseType = this.getHorseType();
        int horseType2 = entityHorse.getHorseType();
        int n = 0;
        if (horseType == horseType2) {
            n = horseType;
        } else if (horseType == 0 && horseType2 == 1 || horseType == 1 && horseType2 == 0) {
            n = 2;
        }

        if (n == 0) {
            int nextInt = this.rand.nextInt(9);
            int n2;
            if (nextInt < 4) {
                n2 = this.getHorseVariant() & 0xFF;
            } else if (nextInt < 8) {
                n2 = entityHorse.getHorseVariant() & 0xFF;
            } else {
                n2 = this.rand.nextInt(7);
            }

            int nextInt2 = this.rand.nextInt(5);
            if (nextInt2 < 2) {
                n2 |= this.getHorseVariant() & 0xFF00;
            } else if (nextInt2 < 4) {
                n2 |= entityHorse.getHorseVariant() & 0xFF00;
            } else {
                n2 |= this.rand.nextInt(5) << 8 & 0xFF00;
            }

            entityHorse2.setHorseVariant(n2);
        }

        entityHorse2.setHorseType(n);
        double d = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()
            + entityAgeable.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()
            + this.getChildHealthBonus();
        entityHorse2.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(d / 3.0);
        double d2 = this.getEntityAttribute(horseJumpStrength).getBaseValue()
            + entityAgeable.getEntityAttribute(horseJumpStrength).getBaseValue()
            + this.getChildJumpStrengthBonus();
        entityHorse2.getEntityAttribute(horseJumpStrength).setBaseValue(d2 / 3.0);
        double d3 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue()
            + entityAgeable.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue()
            + this.getDoubleValue();
        entityHorse2.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(d3 / 3.0);
        return entityHorse2;
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData) {
        IEntityLivingData onSpawnWithEgg = super.onSpawnWithEgg(entityLivingData);
        boolean flag = false;
        int n = 0;
        int n2;
        if (onSpawnWithEgg instanceof EntityHorse.GroupData) {
            n2 = ((EntityHorse.GroupData)onSpawnWithEgg).intValue;
            n = ((EntityHorse.GroupData)onSpawnWithEgg).intValue2 & 0xFF | this.rand.nextInt(5) << 8;
        } else {
            if (this.rand.nextInt(10) == 0) {
                n2 = 1;
            } else {
                int nextInt = this.rand.nextInt(7);
                int nextInt2 = this.rand.nextInt(5);
                n2 = 0;
                n = nextInt | nextInt2 << 8;
            }

            onSpawnWithEgg = new EntityHorse.GroupData(n2, n);
        }

        this.setHorseType(n2);
        this.setHorseVariant(n);
        if (this.rand.nextInt(5) == 0) {
            this.setGrowingAge(-24000);
        }

        if (n2 != 4 && n2 != 3) {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getChildHealthBonus());
            if (n2 == 0) {
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.getDoubleValue());
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.175F);
            }
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
        }

        if (n2 != 2 && n2 != 1) {
            this.getEntityAttribute(horseJumpStrength).setBaseValue(this.getChildJumpStrengthBonus());
        } else {
            this.getEntityAttribute(horseJumpStrength).setBaseValue(0.5);
        }

        this.setHealth(this.getMaxHealth());
        return onSpawnWithEgg;
    }

    public float getGrassEatingAmount(float f) {
        return this.prevHeadLean + (this.headLean - this.prevHeadLean) * f;
    }

    public float getRearingAmount(float f) {
        return this.prevRearingAmount + (this.rearingAmount - this.prevRearingAmount) * f;
    }

    public float getEatingAnimationProgress(float f) {
        return this.prevMouthOpenness + (this.mouthOpenness - this.prevMouthOpenness) * f;
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    public void setJumpPower(int n) {
        if (this.isHorseSaddled()) {
            if (n < 0) {
                n = 0;
            } else {
                this.jumping = true;
                this.makeHorseRear();
            }

            if (n >= 90) {
                this.jumpPower = 1.0F;
            } else {
                this.jumpPower = 0.4F + 0.4F * n / 90.0F;
            }
        }
    }

    public void spawnHorseParticles(boolean flag) {
        String str = flag ? "heart" : "smoke";

        for (int i = 0; i < 7; i++) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            this.worldObj
                .spawnParticle(
                    str,
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.posY + 0.5 + this.rand.nextFloat() * this.height,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    d,
                    d2,
                    d3
                );
        }
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 7) {
            this.spawnHorseParticles(true);
        } else if (b == 6) {
            this.spawnHorseParticles(false);
        } else {
            super.handleHealthUpdate(b);
        }
    }

    @Override
    public void updateRiderPosition() {
        super.updateRiderPosition();
        if (this.prevRearingAmount > 0.0F) {
            float sin = MathHelper.sin(this.renderYawOffset * (float) Math.PI / 180.0F);
            float cos = MathHelper.cos(this.renderYawOffset * (float) Math.PI / 180.0F);
            float f = 0.7F * this.prevRearingAmount;
            float f2 = 0.15F * this.prevRearingAmount;
            this.riddenByEntity
                .setPosition(this.posX + f * sin, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset() + f2, this.posZ - f * cos);
            if (this.riddenByEntity instanceof EntityLivingBase) {
                ((EntityLivingBase)this.riddenByEntity).renderYawOffset = this.renderYawOffset;
            }
        }
    }

    public float getChildHealthBonus() {
        return 15.0F + this.rand.nextInt(8) + this.rand.nextInt(9);
    }

    public double getChildJumpStrengthBonus() {
        return 0.4F + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2;
    }

    public double getDoubleValue() {
        return (0.45F + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3) * 0.25;
    }

    public static boolean isHorseArmor(Item item) {
        return item == Items.iron_horse_armor || item == Items.golden_horse_armor || item == Items.diamond_horse_armor;
    }

    @Override
    public boolean isOnLadder() {
        return false;
    }

    public static class GroupData implements IEntityLivingData {
        public int intValue;
        public int intValue2;

        public GroupData(int n, int n2) {
            this.intValue = n;
            this.intValue2 = n2;
        }
    }
}
