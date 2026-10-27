package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
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
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityHorse extends EntityAnimal implements IInvBasic {
   public static Predicate<Entity> horseBreedingSelector = new Predicate<Entity>() {
      public boolean apply(Entity entity) {
         return entity instanceof EntityHorse && ((EntityHorse)entity).isBreeding();
      }
   };
   public static IAttribute horseJumpStrength = new RangedAttribute((IAttribute)null, "horse.jumpStrength", 0.7, 0.0, 2.0)
      .setDescription("Jump Strength")
      .setShouldWatch(true);
   public static String[] horseArmorTextures = new String[]{
      null,
      "textures/entity/horse/armor/horse_armor_iron.png",
      "textures/entity/horse/armor/horse_armor_gold.png",
      "textures/entity/horse/armor/horse_armor_diamond.png"
   };
   public static String[] HORSE_ARMOR_TEXTURES_ABBR = new String[]{"", "meo", "goo", "dio"};
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
   public static String[] HORSE_TEXTURES_ABBR = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
   public static String[] horseMarkingTextures = new String[]{
      null,
      "textures/entity/horse/horse_markings_white.png",
      "textures/entity/horse/horse_markings_whitefield.png",
      "textures/entity/horse/horse_markings_whitedots.png",
      "textures/entity/horse/horse_markings_blackdots.png"
   };
   public static String[] HORSE_MARKING_TEXTURES_ABBR = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   public int eatingHaystackCounter;
   public int openMouthCounter;
   public int jumpRearingCounter;
   public int intValue3;
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
   public String texturePrefix;
   public String[] horseTexturesArray = new String[3];
   public boolean flag = false;

   public EntityHorse(World worldIn) {
      super(worldIn);
      this.setSize(1.4F, 1.6F);
      this.isImmuneToFire = false;
      this.setChested(false);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.2));
      this.tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.0));
      this.tasks.addTask(6, new EntityAIWander(this, 0.7));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.initHorseChest();
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, 0);
      this.dataWatcher.addObject(19, (byte)0);
      this.dataWatcher.addObject(20, 0);
      this.dataWatcher.addObject(21, String.valueOf(""));
      this.dataWatcher.addObject(22, 0);
   }

   public void setHorseType(int type) {
      this.dataWatcher.updateObject(19, (byte)type);
      this.resetTexturePrefix();
   }

   public int getHorseType() {
      return this.dataWatcher.getWatchableObjectByte(19);
   }

   public void setHorseVariant(int variant) {
      this.dataWatcher.updateObject(20, variant);
      this.resetTexturePrefix();
   }

   public int getHorseVariant() {
      return this.dataWatcher.getWatchableObjectInt(20);
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
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

   public String getOwnerId() {
      return this.dataWatcher.getWatchableObjectString(21);
   }

   public void setOwnerId(String id) {
      this.dataWatcher.updateObject(21, id);
   }

   public float getHorseSize() {
      return 0.5F;
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

   public void setHorseTamed(boolean tamed) {
      this.setHorseWatchableBoolean(2, tamed);
   }

   public void setHorseJumping(boolean jumping) {
      this.horseJumping = jumping;
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

   public int getHorseArmorIndexSynced() {
      return this.dataWatcher.getWatchableObjectInt(22);
   }

   public int getHorseArmorIndex(ItemStack itemStackIn) {
      if (itemStackIn == null) {
         return 0;
      }

      Item item = itemStackIn.getItem();
      return item == Items.iron_horse_armor ? 1 : (item == Items.golden_horse_armor ? 2 : (item == Items.diamond_horse_armor ? 3 : 0));
   }

   public boolean isEatingHaystack() {
      return this.getHorseWatchableBoolean(32);
   }

   public boolean isRearing() {
      return this.getHorseWatchableBoolean(64);
   }

   public boolean isBreeding() {
      return this.getHorseWatchableBoolean(16);
   }

   public boolean getHasReproduced() {
      return this.hasReproduced;
   }

   public void setHorseArmorStack(ItemStack itemStackIn) {
      this.dataWatcher.updateObject(22, this.getHorseArmorIndex(itemStackIn));
      this.resetTexturePrefix();
   }

   public void setBreeding(boolean breeding) {
      this.setHorseWatchableBoolean(16, breeding);
   }

   public void setChested(boolean chested) {
      this.setHorseWatchableBoolean(8, chested);
   }

   public void setHasReproduced(boolean hasReproducedIn) {
      this.hasReproduced = hasReproducedIn;
   }

   public void setHorseSaddled(boolean saddled) {
      this.setHorseWatchableBoolean(4, saddled);
   }

   public int getTemper() {
      return this.temper;
   }

   public void setTemper(int temperIn) {
      this.temper = temperIn;
   }

   public int increaseTemper(int n) {
      int n2 = MathHelper.clamp_int(this.getTemper() + n, 0, this.getMaxTemper());
      this.setTemper(n2);
      return n2;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      Entity entity = source.getEntity();
      return this.riddenByEntity != null && this.riddenByEntity.equals(entity) ? false : super.attackEntityFrom(source, amount);
   }

   @Override
   public int getTotalArmorValue() {
      return armorValues[this.getHorseArmorIndexSynced()];
   }

   @Override
   public boolean canBePushed() {
      return this.riddenByEntity == null;
   }

   public boolean prepareChunkForSpawn() {
      int n = MathHelper.floor_double(this.posX);
      int n2 = MathHelper.floor_double(this.posZ);
      this.worldObj.getBiomeGenForCoords(new BlockPos(n, 0, n2));
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
      if (!this.isSilent()) {
         this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
      }
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      if (distance > 1.0F) {
         this.playSound("mob.horse.land", 0.4F, 1.0F);
      }

      int n = MathHelper.ceiling_float_int((distance * 0.5F - 3.0F) * damageMultiplier);
      if (n > 0) {
         this.attackEntityFrom(DamageSource.fall, n);
         if (this.riddenByEntity != null) {
            this.riddenByEntity.attackEntityFrom(DamageSource.fall, n);
         }

         Block block = this.worldObj.getBlockState(new BlockPos(this.posX, this.posY - 0.2 - this.prevRotationYaw, this.posZ)).getBlock();
         if (block.getMaterial() != Material.air && !this.isSilent()) {
            SoundType stepSound = block.stepSound;
            this.worldObj.playSoundAtEntity(this, stepSound.getStepSound(), stepSound.getVolume() * 0.5F, stepSound.getFrequency() * 0.75F);
         }
      }
   }

   public int getChestSize() {
      int horseType = this.getHorseType();
      return this.isChested() && (horseType == 1 || horseType == 2) ? 17 : 2;
   }

   public void initHorseChest() {
      AnimalChest horseChest = this.horseChest;
      this.horseChest = new AnimalChest("HorseChest", this.getChestSize());
      this.horseChest.setCustomName(this.getName());
      if (horseChest != null) {
         horseChest.removeInventoryChangeListener(this);
         int min = Math.min(horseChest.getSizeInventory(), this.horseChest.getSizeInventory());

         for (int i = 0; i < min; i++) {
            ItemStack stackInSlot = horseChest.getStackInSlot(i);
            if (stackInSlot != null) {
               this.horseChest.setInventorySlotContents(i, stackInSlot.copy());
            }
         }
      }

      this.horseChest.addInventoryChangeListener(this);
      this.updateHorseSlots();
   }

   public void updateHorseSlots() {
      if (!this.worldObj.isRemote) {
         this.setHorseSaddled(this.horseChest.getStackInSlot(0) != null);
         if (this.canWearArmor()) {
            this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
         }
      }
   }

   @Override
   public void onInventoryChanged(InventoryBasic inventoryBasic) {
      int horseArmorIndexSynced = this.getHorseArmorIndexSynced();
      boolean isHorseSaddled = this.isHorseSaddled();
      this.updateHorseSlots();
      if (this.ticksExisted > 20) {
         if (horseArmorIndexSynced == 0 && horseArmorIndexSynced != this.getHorseArmorIndexSynced()) {
            this.playSound("mob.horse.armor", 0.5F, 1.0F);
         } else if (horseArmorIndexSynced != this.getHorseArmorIndexSynced()) {
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

   public EntityHorse getClosestHorse(Entity entityIn, double distance) {
      double d = Double.MAX_VALUE;
      Entity entity = null;

      for (Entity entity2 : this.worldObj
         .getEntitiesInAABBexcluding(entityIn, entityIn.getEntityBoundingBox().addCoord(distance, distance, distance), horseBreedingSelector)) {
         double distanceSq = entity2.getDistanceSq(entityIn.posX, entityIn.posY, entityIn.posZ);
         if (distanceSq < d) {
            entity = entity2;
            d = distanceSq;
         }
      }

      return (EntityHorse)entity;
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
      return horseType == 4 ? Items.bone : (horseType == 3 ? (flag ? null : Items.rotten_flesh) : Items.leather);
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
   public void playStepSound(BlockPos pos, Block blockIn) {
      SoundType stepSound = blockIn.stepSound;
      if (this.worldObj.getBlockState(pos.up()).getBlock() == Blocks.snow_layer) {
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
      return this.getHorseType() == 0 || this.getHorseArmorIndexSynced() > 0;
   }

   public void resetTexturePrefix() {
      this.texturePrefix = null;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void setHorseTexturePaths() {
      this.texturePrefix = "horse/";
      this.horseTexturesArray[0] = null;
      this.horseTexturesArray[1] = null;
      this.horseTexturesArray[2] = null;
      int horseType = this.getHorseType();
      int horseVariant = this.getHorseVariant();
      if (horseType == 0) {
         int n = horseVariant & 0xFF;
         int n2 = (horseVariant & 0xFF00) >> 8;
         if (n >= horseTextures.length) {
            this.flag = false;
            return;
         }

         this.horseTexturesArray[0] = horseTextures[n];
         this.texturePrefix = this.texturePrefix + HORSE_TEXTURES_ABBR[n];
         if (n2 >= horseMarkingTextures.length) {
            this.flag = false;
            return;
         }

         this.horseTexturesArray[1] = horseMarkingTextures[n2];
         this.texturePrefix = this.texturePrefix + HORSE_MARKING_TEXTURES_ABBR[n2];
      } else {
         this.horseTexturesArray[0] = "";
         this.texturePrefix = this.texturePrefix + "_" + horseType + "_";
      }

      int horseArmorIndexSynced = this.getHorseArmorIndexSynced();
      if (horseArmorIndexSynced >= horseArmorTextures.length) {
         this.flag = false;
      } else {
         this.horseTexturesArray[2] = horseArmorTextures[horseArmorIndexSynced];
         this.texturePrefix = this.texturePrefix + HORSE_ARMOR_TEXTURES_ABBR[horseArmorIndexSynced];
         this.flag = true;
      }
   }

   public String getHorseTexture() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.texturePrefix;
   }

   public String[] getVariantTexturePaths() {
      if (this.texturePrefix == null) {
         this.setHorseTexturePaths();
      }

      return this.horseTexturesArray;
   }

   public void openGUI(EntityPlayer playerEntity) {
      if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == playerEntity) && this.isTame()) {
         this.horseChest.setCustomName(this.getName());
         playerEntity.displayGUIHorse(this, this.horseChest);
      }
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack currentItem = player.inventory.getCurrentItem();
      if (currentItem != null && currentItem.getItem() == Items.spawn_egg) {
         return super.interact(player);
      }

      if (!this.isTame() && this.isUndead()) {
         return false;
      }

      if (this.isTame() && this.isAdultHorse() && player.isSneaking()) {
         this.openGUI(player);
         return true;
      }

      if (this.isAdultHorse2() && this.riddenByEntity != null) {
         return super.interact(player);
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

               this.openGUI(player);
               return true;
            }
         }

         if (!flag && !this.isUndead()) {
            float f = 0.0F;
            short s = 0;
            byte b = 0;
            if (currentItem.getItem() == Items.wheat) {
               f = 2.0F;
               s = 20;
               b = 3;
            } else if (currentItem.getItem() == Items.sugar) {
               f = 1.0F;
               s = 30;
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
                  this.setInLove(player);
               }
            } else if (currentItem.getItem() == Items.golden_apple) {
               f = 10.0F;
               s = 240;
               b = 10;
               if (this.isTame() && this.getGrowingAge() == 0) {
                  flag = true;
                  this.setInLove(player);
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
            if (currentItem != null && currentItem.interactWithEntity(player, this)) {
               return true;
            }

            this.makeHorseRearWithSound();
            return true;
         }

         if (!flag && this.canCarryChest() && !this.isChested() && currentItem.getItem() == Item.getItemFromBlock(Blocks.chest)) {
            this.setChested(true);
            this.playSound("mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            flag = true;
            this.initHorseChest();
         }

         if (!flag && this.isAdultHorse2() && !this.isHorseSaddled() && currentItem.getItem() == Items.saddle) {
            this.openGUI(player);
            return true;
         }

         if (flag) {
            if (!player.capabilities.isCreativeMode && --currentItem.stackSize == 0) {
               player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
            }

            return true;
         }
      }

      if (!this.isAdultHorse2() || this.riddenByEntity != null) {
         return super.interact(player);
      }

      if (currentItem != null && currentItem.interactWithEntity(player, this)) {
         return true;
      }

      this.mountTo(player);
      return true;
   }

   public void mountTo(EntityPlayer player) {
      player.rotationYaw = this.rotationYaw;
      player.rotationPitch = this.rotationPitch;
      this.setEatingHaystack(false);
      this.setRearing(false);
      if (!this.worldObj.isRemote) {
         player.mountEntity(this);
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
      return this.riddenByEntity != null && this.isHorseSaddled() ? true : this.isEatingHaystack() || this.isRearing();
   }

   public boolean isUndead() {
      int horseType = this.getHorseType();
      return horseType == 3 || horseType == 4;
   }

   public boolean isSterile() {
      return this.isUndead() || this.getHorseType() == 2;
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return false;
   }

   public void run() {
      this.intValue3 = 1;
   }

   @Override
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (!this.worldObj.isRemote) {
         this.dropChestItems();
      }
   }

   @Override
   public void onLivingUpdate() {
      if (this.rand.nextInt(200) == 0) {
         this.run();
      }

      super.onLivingUpdate();
      if (!this.worldObj.isRemote) {
         if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }

         if (!this.isEatingHaystack()
            && this.riddenByEntity == null
            && this.rand.nextInt(300) == 0
            && this.worldObj
                  .getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ)))
                  .getBlock()
               == Blocks.grass) {
            this.setEatingHaystack(true);
         }

         if (this.isEatingHaystack() && ++this.eatingHaystackCounter > 50) {
            this.eatingHaystackCounter = 0;
            this.setEatingHaystack(false);
         }

         if (this.isBreeding() && !this.isAdultHorse() && !this.isEatingHaystack()) {
            EntityHorse closestHorse = this.getClosestHorse(this, 16.0);
            if (closestHorse != null && this.getDistanceSqToEntity(closestHorse) > 4.0) {
               this.navigator.getPathToEntityLiving(closestHorse);
            }
         }
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.worldObj.isRemote && this.dataWatcher.hasObjectChanged()) {
         this.dataWatcher.clearDirty();
         this.resetTexturePrefix();
      }

      if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30) {
         this.openMouthCounter = 0;
         this.setHorseWatchableBoolean(128, false);
      }

      if (!this.worldObj.isRemote && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20) {
         this.jumpRearingCounter = 0;
         this.setRearing(false);
      }

      if (this.intValue3 > 0 && ++this.intValue3 > 8) {
         this.intValue3 = 0;
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
         && this.getHealth() >= this.getMaxHealth()
         && this.isInLove();
   }

   @Override
   public void setEating(boolean eating) {
      this.setHorseWatchableBoolean(32, eating);
   }

   public void setEatingHaystack(boolean flag) {
      this.setEating(flag);
   }

   public void setRearing(boolean rearing) {
      if (rearing) {
         this.setEatingHaystack(false);
      }

      this.setHorseWatchableBoolean(64, rearing);
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

   public void dropItemsInChest(Entity entityIn, AnimalChest animalChestIn) {
      if (animalChestIn != null && !this.worldObj.isRemote) {
         for (int i = 0; i < animalChestIn.getSizeInventory(); i++) {
            ItemStack stackInSlot = animalChestIn.getStackInSlot(i);
            if (stackInSlot != null) {
               this.entityDropItem(stackInSlot, 0.0F);
            }
         }
      }
   }

   public boolean setTamedBy(EntityPlayer player) {
      this.setOwnerId(player.getUniqueID().toString());
      this.setHorseTamed(true);
      return true;
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase && this.isHorseSaddled()) {
         this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
         this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
         this.setRotation(this.rotationYaw, this.rotationPitch);
         this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
         strafe = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
         forward = ((EntityLivingBase)this.riddenByEntity).moveForward;
         if (forward <= 0.0F) {
            forward *= 0.25F;
            this.gallopTime = 0;
         }

         if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.jumping) {
            strafe = 0.0F;
            forward = 0.0F;
         }

         if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
            this.motionY = this.getHorseJumpStrength() * this.jumpPower;
            if (this.isPotionActive(Potion.jump)) {
               this.motionY = this.motionY + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
            }

            this.setHorseJumping(true);
            this.isAirBorne = true;
            if (forward > 0.0F) {
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
            super.moveEntityWithHeading(strafe, forward);
         }

         if (this.onGround) {
            this.jumpPower = 0.0F;
            this.setHorseJumping(false);
         }

         this.prevLimbSwingAmount = this.limbSwingAmount;
         double d = this.posX - this.prevPosX;
         double d2 = this.posZ - this.prevPosZ;
         float f = MathHelper.sqrt_double(d * d + d2 * d2) * 4.0F;
         if (f > 1.0F) {
            f = 1.0F;
         }

         this.limbSwingAmount = this.limbSwingAmount + (f - this.limbSwingAmount) * 0.4F;
         this.limbSwing = this.limbSwing + this.limbSwingAmount;
      } else {
         this.stepHeight = 0.5F;
         this.jumpMovementFactor = 0.02F;
         super.moveEntityWithHeading(strafe, forward);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("EatingHaystack", this.isEatingHaystack());
      tagCompound.setBoolean("ChestedHorse", this.isChested());
      tagCompound.setBoolean("HasReproduced", this.getHasReproduced());
      tagCompound.setBoolean("Bred", this.isBreeding());
      tagCompound.setInteger("Type", this.getHorseType());
      tagCompound.setInteger("Variant", this.getHorseVariant());
      tagCompound.setInteger("Temper", this.getTemper());
      tagCompound.setBoolean("Tame", this.isTame());
      tagCompound.setString("OwnerUUID", this.getOwnerId());
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
      this.setBreeding(tagCompund.getBoolean("Bred"));
      this.setChested(tagCompund.getBoolean("ChestedHorse"));
      this.setHasReproduced(tagCompund.getBoolean("HasReproduced"));
      this.setHorseType(tagCompund.getInteger("Type"));
      this.setHorseVariant(tagCompund.getInteger("Variant"));
      this.setTemper(tagCompund.getInteger("Temper"));
      this.setHorseTamed(tagCompund.getBoolean("Tame"));
      String str = "";
      if (tagCompund.hasKey("OwnerUUID", 8)) {
         str = tagCompund.getString("OwnerUUID");
      } else {
         String string = tagCompund.getString("Owner");
         str = PreYggdrasilConverter.getStringUUIDFromName(string);
      }

      if (str.length() > 0) {
         this.setOwnerId(str);
      }

      IAttributeInstance attributeInstanceByName = this.getAttributeMap().getAttributeInstanceByName("Speed");
      if (attributeInstanceByName != null) {
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(attributeInstanceByName.getBaseValue() * 0.25);
      }

      if (this.isChested()) {
         NBTTagList tagList = tagCompund.getTagList("Items", 10);
         this.initHorseChest();

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
         if (loadItemStackFromNBT != null && isArmorItem(loadItemStackFromNBT.getItem())) {
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

      this.updateHorseSlots();
   }

   @Override
   public boolean canMateWith(EntityAnimal otherAnimal) {
      if (otherAnimal == this) {
         return false;
      } else if (otherAnimal.getClass() != this.getClass()) {
         return false;
      } else {
         EntityHorse otherAnimal2 = (EntityHorse)otherAnimal;
         if (this.canMate() && otherAnimal2.canMate()) {
            int horseType = this.getHorseType();
            int horseType2 = otherAnimal2.getHorseType();
            return horseType == horseType2 || horseType == 0 && horseType2 == 1 || horseType == 1 && horseType2 == 0;
         } else {
            return false;
         }
      }
   }

   @Override
   public EntityAgeable createChild(EntityAgeable ageable) {
      EntityHorse ageable2 = (EntityHorse)ageable;
      EntityHorse entityHorse = new EntityHorse(this.worldObj);
      int horseType = this.getHorseType();
      int horseType2 = ageable2.getHorseType();
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
            n2 = ageable2.getHorseVariant() & 0xFF;
         } else {
            n2 = this.rand.nextInt(7);
         }

         int nextInt2 = this.rand.nextInt(5);
         if (nextInt2 < 2) {
            n2 |= this.getHorseVariant() & 0xFF00;
         } else if (nextInt2 < 4) {
            n2 |= ageable2.getHorseVariant() & 0xFF00;
         } else {
            n2 |= this.rand.nextInt(5) << 8 & 0xFF00;
         }

         entityHorse.setHorseVariant(n2);
      }

      entityHorse.setHorseType(n);
      double d = this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()
         + ageable.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()
         + this.getModifiedMaxHealth();
      entityHorse.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(d / 3.0);
      double d2 = this.getEntityAttribute(horseJumpStrength).getBaseValue()
         + ageable.getEntityAttribute(horseJumpStrength).getBaseValue()
         + this.getModifiedJumpStrength();
      entityHorse.getEntityAttribute(horseJumpStrength).setBaseValue(d2 / 3.0);
      double d3 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue()
         + ageable.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue()
         + this.getModifiedMovementSpeed();
      entityHorse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(d3 / 3.0);
      return entityHorse;
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      int n = 0;
      int n2 = 0;
      if (livingdata instanceof EntityHorse.GroupData) {
         n = ((EntityHorse.GroupData)livingdata).horseType;
         n2 = ((EntityHorse.GroupData)livingdata).horseVariant & 0xFF | this.rand.nextInt(5) << 8;
      } else {
         if (this.rand.nextInt(10) == 0) {
            n = 1;
         } else {
            int nextInt = this.rand.nextInt(7);
            int nextInt2 = this.rand.nextInt(5);
            n = 0;
            n2 = nextInt | nextInt2 << 8;
         }

         livingdata = new EntityHorse.GroupData(n, n2);
      }

      this.setHorseType(n);
      this.setHorseVariant(n2);
      if (this.rand.nextInt(5) == 0) {
         this.setGrowingAge(-24000);
      }

      if (n != 4 && n != 3) {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getModifiedMaxHealth());
         if (n == 0) {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.getModifiedMovementSpeed());
         } else {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.175F);
         }
      } else {
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0);
         this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
      }

      if (n != 2 && n != 1) {
         this.getEntityAttribute(horseJumpStrength).setBaseValue(this.getModifiedJumpStrength());
      } else {
         this.getEntityAttribute(horseJumpStrength).setBaseValue(0.5);
      }

      this.setHealth(this.getMaxHealth());
      return livingdata;
   }

   public float getGrassEatingAmount(float f) {
      return this.prevHeadLean + (this.headLean - this.prevHeadLean) * f;
   }

   public float getRearingAmount(float f) {
      return this.prevRearingAmount + (this.rearingAmount - this.prevRearingAmount) * f;
   }

   public float getMouthOpennessAngle(float f) {
      return this.prevMouthOpenness + (this.mouthOpenness - this.prevMouthOpenness) * f;
   }

   public void setJumpPower(int jumpPowerIn) {
      if (this.isHorseSaddled()) {
         if (jumpPowerIn < 0) {
            jumpPowerIn = 0;
         } else {
            this.jumping = true;
            this.makeHorseRear();
         }

         if (jumpPowerIn >= 90) {
            this.jumpPower = 1.0F;
         } else {
            this.jumpPower = 0.4F + 0.4F * jumpPowerIn / 90.0F;
         }
      }
   }

   public void spawnHorseParticles(boolean flag) {
      EnumParticleTypes enumParticleTypes = flag ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL;

      for (int i = 0; i < 7; i++) {
         double d = this.rand.nextGaussian() * 0.02;
         double d2 = this.rand.nextGaussian() * 0.02;
         double d3 = this.rand.nextGaussian() * 0.02;
         this.worldObj
            .spawnParticle(
               enumParticleTypes,
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
   public void handleStatusUpdate(byte id) {
      if (id == 7) {
         this.spawnHorseParticles(true);
      } else if (id == 6) {
         this.spawnHorseParticles(false);
      } else {
         super.handleStatusUpdate(id);
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

   public float getModifiedMaxHealth() {
      return 15.0F + this.rand.nextInt(8) + this.rand.nextInt(9);
   }

   public double getModifiedJumpStrength() {
      return 0.4F + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2;
   }

   public double getModifiedMovementSpeed() {
      return (0.45F + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3) * 0.25;
   }

   public static boolean isArmorItem(Item item) {
      return item == Items.iron_horse_armor || item == Items.golden_horse_armor || item == Items.diamond_horse_armor;
   }

   @Override
   public boolean isOnLadder() {
      return false;
   }

   @Override
   public float getEyeHeight() {
      return this.height;
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      if (inventorySlot == 499 && this.canCarryChest()) {
         if (itemStackIn == null && this.isChested()) {
            this.setChested(false);
            this.initHorseChest();
            return true;
         }

         if (itemStackIn != null && itemStackIn.getItem() == Item.getItemFromBlock(Blocks.chest) && !this.isChested()) {
            this.setChested(true);
            this.initHorseChest();
            return true;
         }
      }

      int n = inventorySlot - 400;
      if (n >= 0 && n < 2 && n < this.horseChest.getSizeInventory()) {
         if (n == 0 && itemStackIn != null && itemStackIn.getItem() != Items.saddle) {
            return false;
         }

         if (n == 1 && (itemStackIn != null && !isArmorItem(itemStackIn.getItem()) || !this.canWearArmor())) {
            return false;
         }

         this.horseChest.setInventorySlotContents(n, itemStackIn);
         this.updateHorseSlots();
         return true;
      } else {
         int n2 = inventorySlot - 500 + 2;
         if (n2 >= 2 && n2 < this.horseChest.getSizeInventory()) {
            this.horseChest.setInventorySlotContents(n2, itemStackIn);
            return true;
         } else {
            return false;
         }
      }
   }

   public static class GroupData implements IEntityLivingData {
      public int horseType;
      public int horseVariant;

      public GroupData(int type, int variant) {
         this.horseType = type;
         this.horseVariant = variant;
      }
   }
}
