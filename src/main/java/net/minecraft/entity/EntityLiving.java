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
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public abstract class EntityLiving extends EntityLivingBase {
   public int livingSoundTime;
   public int experienceValue;
   public ItemStack[] equipment = new ItemStack[5];
   public float[] equipmentDropChances = new float[5];
   public UUID teamUuid = null;
   public String teamUuidString = null;
   public EntityAITasks tasks;
   public EntityAITasks targetTasks;
   public EntityLookHelper lookHelper;
   public EntityLivingBase attackTarget;
   public EntityMoveHelper moveHelper;
   public EntityJumpHelper jumpHelper;
   public EntityBodyHelper bodyHelper;
   public boolean canPickUpLoot;
   public boolean persistenceRequired;
   public boolean isLeashed;
   public Entity leashedToEntity;
   public NBTTagCompound leashNBTTag;
   public PathNavigate navigator;
   public EntitySenses senses;

   public EntityLiving(World worldIn) {
      super(worldIn);
      this.tasks = new EntityAITasks(worldIn != null && worldIn.theProfiler != null ? worldIn.theProfiler : null);
      this.targetTasks = new EntityAITasks(worldIn != null && worldIn.theProfiler != null ? worldIn.theProfiler : null);
      this.lookHelper = new EntityLookHelper(this);
      this.moveHelper = new EntityMoveHelper(this);
      this.jumpHelper = new EntityJumpHelper(this);
      this.bodyHelper = new EntityBodyHelper(this);
      this.navigator = this.getNewNavigator(worldIn);
      this.senses = new EntitySenses(this);

      for (int i = 0; i < this.equipmentDropChances.length; i++) {
         this.equipmentDropChances[i] = 0.085F;
      }
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
   }

   public PathNavigate getNewNavigator(World worldIn) {
      return new PathNavigateGround(this, worldIn);
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

   public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
      this.attackTarget = entitylivingbaseIn;
      Reflector.callVoid(Reflector.ForgeHooks_onLivingSetAttackTarget, this, entitylivingbaseIn);
   }

   public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
      return cls != EntityGhast.class;
   }

   public void eatGrassBonus() {
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(15, (byte)0);
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
   public void onEntityUpdate() {
      super.onEntityUpdate();
      this.worldObj.theProfiler.startSection("mobBaseTick");
      if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
         this.livingSoundTime = -this.getTalkInterval();
         this.playLivingSound();
      }

      this.worldObj.theProfiler.endSection();
   }

   @Override
   public int getExperiencePoints(EntityPlayer player) {
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
      if (this.worldObj.isRemote) {
         for (int i = 0; i < 20; i++) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            double d4 = 10.0;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.EXPLOSION_NORMAL,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width - d * d4,
                  this.posY + this.rand.nextFloat() * this.height - d2 * d4,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width - d3 * d4,
                  d,
                  d2,
                  d3
               );
         }
      } else {
         this.worldObj.setEntityState(this, (byte)20);
      }
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 20) {
         this.spawnExplosionParticle();
      } else {
         super.handleStatusUpdate(id);
      }
   }

   @Override
   public void onUpdate() {
      if (Config.isSmoothWorld() && this.canSkipUpdate()) {
         this.onUpdateMinimal();
      } else {
         super.onUpdate();
         if (!this.worldObj.isRemote) {
            this.updateLeashedState();
         }
      }
   }

   @Override
   public float updateDistance(float f, float f2) {
      this.bodyHelper.updateRenderAngles();
      return f2;
   }

   public String getLivingSound() {
      return null;
   }

   public Item getDropItem() {
      return null;
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      Item dropItem = this.getDropItem();
      if (dropItem != null) {
         int nextInt = this.rand.nextInt(3);
         if (lootingModifier > 0) {
            nextInt += this.rand.nextInt(lootingModifier + 1);
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
      tagCompound.setBoolean("Leashed", this.isLeashed);
      if (this.leashedToEntity != null) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         if (this.leashedToEntity instanceof EntityLivingBase) {
            nbttagCompound.setLong("UUIDMost", this.leashedToEntity.getUniqueID().getMostSignificantBits());
            nbttagCompound.setLong("UUIDLeast", this.leashedToEntity.getUniqueID().getLeastSignificantBits());
         } else if (this.leashedToEntity instanceof EntityHanging) {
            BlockPos hangingPosition = ((EntityHanging)this.leashedToEntity).getHangingPosition();
            nbttagCompound.setInteger("X", hangingPosition.getX());
            nbttagCompound.setInteger("Y", hangingPosition.getY());
            nbttagCompound.setInteger("Z", hangingPosition.getZ());
         }

         tagCompound.setTag("Leash", nbttagCompound);
      }

      if (this.isAIDisabled()) {
         tagCompound.setBoolean("NoAI", this.isAIDisabled());
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(tagCompund.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = tagCompund.getBoolean("PersistenceRequired");
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

      this.setNoAI(tagCompund.getBoolean("NoAI"));
   }

   public void setMoveForward(float f) {
      this.moveForward = f;
   }

   @Override
   public void setAIMoveSpeed(float speedIn) {
      super.setAIMoveSpeed(speedIn);
      this.setMoveForward(speedIn);
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.worldObj.theProfiler.startSection("looting");
      if (!this.worldObj.isRemote && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
         for (EntityItem entityItem : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0, 0.0, 1.0))) {
            if (!entityItem.isDead && entityItem.getEntityItem() != null && !entityItem.cannotPickup()) {
               this.updateEquipmentIfNeeded(entityItem);
            }
         }
      }

      this.worldObj.theProfiler.endSection();
   }

   public void updateEquipmentIfNeeded(EntityItem itemEntity) {
      ItemStack entityItem = itemEntity.getEntityItem();
      int armorPosition = getArmorPosition(entityItem);
      if (armorPosition > -1) {
         boolean flag = true;
         ItemStack equipmentInSlot = this.getEquipmentInSlot(armorPosition);
         if (equipmentInSlot != null) {
            if (armorPosition == 0) {
               if (entityItem.getItem() instanceof ItemSword && !(equipmentInSlot.getItem() instanceof ItemSword)) {
                  flag = true;
               } else if (entityItem.getItem() instanceof ItemSword && equipmentInSlot.getItem() instanceof ItemSword) {
                  ItemSword item = (ItemSword)entityItem.getItem();
                  ItemSword item2 = (ItemSword)equipmentInSlot.getItem();
                  if (item.getDamageVsEntity() != item2.getDamageVsEntity()) {
                     flag = item.getDamageVsEntity() > item2.getDamageVsEntity();
                  } else {
                     flag = entityItem.getMetadata() > equipmentInSlot.getMetadata() || entityItem.hasTagCompound() && !equipmentInSlot.hasTagCompound();
                  }
               } else if (entityItem.getItem() instanceof ItemBow && equipmentInSlot.getItem() instanceof ItemBow) {
                  flag = entityItem.hasTagCompound() && !equipmentInSlot.hasTagCompound();
               } else {
                  flag = false;
               }
            } else if (entityItem.getItem() instanceof ItemArmor && !(equipmentInSlot.getItem() instanceof ItemArmor)) {
               flag = true;
            } else if (entityItem.getItem() instanceof ItemArmor && equipmentInSlot.getItem() instanceof ItemArmor) {
               ItemArmor item = (ItemArmor)entityItem.getItem();
               ItemArmor item2 = (ItemArmor)equipmentInSlot.getItem();
               if (item.damageReduceAmount != item2.damageReduceAmount) {
                  flag = item.damageReduceAmount > item2.damageReduceAmount;
               } else {
                  flag = entityItem.getMetadata() > equipmentInSlot.getMetadata() || entityItem.hasTagCompound() && !equipmentInSlot.hasTagCompound();
               }
            } else {
               flag = false;
            }
         }

         if (flag && this.canPickupItem(entityItem)) {
            if (equipmentInSlot != null && this.rand.nextFloat() - 0.1F < this.equipmentDropChances[armorPosition]) {
               this.entityDropItem(equipmentInSlot, 0.0F);
            }

            if (entityItem.getItem() == Items.diamond && itemEntity.getThrower() != null) {
               EntityPlayer playerEntityByName = this.worldObj.getPlayerEntityByName(itemEntity.getThrower());
               if (playerEntityByName != null) {
                  playerEntityByName.triggerAchievement(AchievementList.diamondsToYou);
               }
            }

            this.setCurrentItemOrArmor(armorPosition, entityItem);
            this.equipmentDropChances[armorPosition] = 2.0F;
            this.persistenceRequired = true;
            this.onItemPickup(itemEntity, 1);
            itemEntity.setDead();
         }
      }
   }

   public boolean canPickupItem(ItemStack stack) {
      return true;
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
   public void updateEntityActionState() {
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
      this.updateAITasks();
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

   public void updateAITasks() {
   }

   public int getVerticalFaceSpeed() {
      return 40;
   }

   public void faceEntity(Entity entityIn, float f, float f2) {
      double d = entityIn.posX - this.posX;
      double d2 = entityIn.posZ - this.posZ;
      double d3;
      if (entityIn instanceof EntityLivingBase) {
         EntityLivingBase entityIn2 = (EntityLivingBase)entityIn;
         d3 = entityIn2.posY + entityIn2.getEyeHeight() - (this.posY + this.getEyeHeight());
      } else {
         d3 = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0 - (this.posY + this.getEyeHeight());
      }

      double d4 = MathHelper.sqrt_double(d * d + d2 * d2);
      float f3 = (float)(MathHelper.atan2(d2, d) * 180.0 / Math.PI) - 90.0F;
      float f4 = (float)(-(MathHelper.atan2(d3, d4) * 180.0 / Math.PI));
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
      return true;
   }

   public boolean isNotColliding() {
      return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this)
         && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
         && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
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
      n -= (3 - this.worldObj.getDifficulty().getDifficultyId()) * 4;
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
   public ItemStack getEquipmentInSlot(int slotIn) {
      return this.equipment[slotIn];
   }

   @Override
   public ItemStack getCurrentArmor(int slotIn) {
      return this.equipment[slotIn + 1];
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      this.equipment[slotIn] = stack;
   }

   @Override
   public ItemStack[] getInventory() {
      return this.equipment;
   }

   @Override
   public void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
      for (int i = 0; i < this.getInventory().length; i++) {
         ItemStack equipmentInSlot = this.getEquipmentInSlot(i);
         boolean flag = this.equipmentDropChances[i] > 1.0F;
         if (equipmentInSlot != null && (wasRecentlyHit || flag) && this.rand.nextFloat() - lootingModifier * 0.01F < this.equipmentDropChances[i]) {
            if (!flag && equipmentInSlot.isItemStackDamageable()) {
               int max = Math.max(equipmentInSlot.getMaxDamage() - 25, 1);
               int n = equipmentInSlot.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(max) + 1);
               if (n > max) {
                  n = max;
               }

               if (n < 1) {
                  n = 1;
               }

               equipmentInSlot.setItemDamage(n);
            }

            this.entityDropItem(equipmentInSlot, 0.0F);
         }
      }
   }

   public void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      if (this.rand.nextFloat() < 0.15F * difficulty.getClampedAdditionalDifficulty()) {
         int nextInt = this.rand.nextInt(2);
         float f = this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F;
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
            ItemStack currentArmor = this.getCurrentArmor(i);
            if (i < 3 && this.rand.nextFloat() < f) {
               break;
            }

            if (currentArmor == null) {
               Item armorItemForSlot = getArmorItemForSlot(i + 1, nextInt);
               if (armorItemForSlot != null) {
                  this.setCurrentItemOrArmor(i + 1, new ItemStack(armorItemForSlot));
               }
            }
         }
      }
   }

   public static int getArmorPosition(ItemStack stack) {
      if (stack.getItem() == Item.getItemFromBlock(Blocks.pumpkin) || stack.getItem() == Items.skull) {
         return 4;
      }

      if (stack.getItem() instanceof ItemArmor) {
         switch (((ItemArmor)stack.getItem()).armorType) {
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

   public void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
      float clampedAdditionalDifficulty = difficulty.getClampedAdditionalDifficulty();
      if (this.getHeldItem() != null && this.rand.nextFloat() < 0.25F * clampedAdditionalDifficulty) {
         EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), (int)(5.0F + clampedAdditionalDifficulty * this.rand.nextInt(18)));
      }

      for (int i = 0; i < 4; i++) {
         ItemStack currentArmor = this.getCurrentArmor(i);
         if (currentArmor != null && this.rand.nextFloat() < 0.5F * clampedAdditionalDifficulty) {
            EnchantmentHelper.addRandomEnchantment(this.rand, currentArmor, (int)(5.0F + clampedAdditionalDifficulty * this.rand.nextInt(18)));
         }
      }
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      this.getEntityAttribute(SharedMonsterAttributes.followRange)
         .applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05, 1));
      return livingdata;
   }

   public boolean canBeSteered() {
      return false;
   }

   public void enablePersistence() {
      this.persistenceRequired = true;
   }

   public void setEquipmentDropChance(int slotIn, float chance) {
      this.equipmentDropChances[slotIn] = chance;
   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean canPickup) {
      this.canPickUpLoot = canPickup;
   }

   public boolean isNoDespawnRequired() {
      return this.persistenceRequired;
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if (this.getLeashed() && this.getLeashedToEntity() == playerIn) {
         this.clearLeashed(true, !playerIn.capabilities.isCreativeMode);
         return true;
      }

      ItemStack currentItem = playerIn.inventory.getCurrentItem();
      if (currentItem != null && currentItem.getItem() == Items.lead && this.allowLeashing()) {
         if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed()) {
            this.setLeashedToEntity(playerIn, true);
            currentItem.stackSize--;
            return true;
         }

         if (((EntityTameable)this).isOwner(playerIn)) {
            this.setLeashedToEntity(playerIn, true);
            currentItem.stackSize--;
            return true;
         }
      }

      return this.interact(playerIn) ? true : super.interactFirst(playerIn);
   }

   public boolean interact(EntityPlayer player) {
      return false;
   }

   public void updateLeashedState() {
      if (this.leashNBTTag != null) {
         this.recreateLeash();
      }

      if (this.isLeashed) {
         if (!this.isEntityAlive()) {
            this.clearLeashed(true, true);
         }

         if (this.leashedToEntity == null || this.leashedToEntity.isDead) {
            this.clearLeashed(true, true);
         }
      }
   }

   public void clearLeashed(boolean sendPacket, boolean dropLead) {
      if (this.isLeashed) {
         this.isLeashed = false;
         this.leashedToEntity = null;
         if (!this.worldObj.isRemote && dropLead) {
            this.dropItem(Items.lead, 1);
         }

         if (!this.worldObj.isRemote && sendPacket && this.worldObj instanceof WorldServer) {
            ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, (Entity)null));
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

            for (EntityLivingBase entityLivingBase : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(10.0, 10.0, 10.0))) {
               if (entityLivingBase.getUniqueID().equals(uuid)) {
                  this.leashedToEntity = entityLivingBase;
                  break;
               }
            }
         } else if (this.leashNBTTag.hasKey("X", 99) && this.leashNBTTag.hasKey("Y", 99) && this.leashNBTTag.hasKey("Z", 99)) {
            BlockPos blockPos = new BlockPos(this.leashNBTTag.getInteger("X"), this.leashNBTTag.getInteger("Y"), this.leashNBTTag.getInteger("Z"));
            EntityLeashKnot knotForPosition = EntityLeashKnot.getKnotForPosition(this.worldObj, blockPos);
            if (knotForPosition == null) {
               knotForPosition = EntityLeashKnot.createKnot(this.worldObj, blockPos);
            }

            this.leashedToEntity = knotForPosition;
         } else {
            this.clearLeashed(false, true);
         }
      }

      this.leashNBTTag = null;
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      int n;
      if (inventorySlot == 99) {
         n = 0;
      } else {
         n = inventorySlot - 100 + 1;
         if (n < 0 || n >= this.equipment.length) {
            return false;
         }
      }

      if (itemStackIn != null && getArmorPosition(itemStackIn) != n && (n != 4 || !(itemStackIn.getItem() instanceof ItemBlock))) {
         return false;
      }

      this.setCurrentItemOrArmor(n, itemStackIn);
      return true;
   }

   @Override
   public boolean isServerWorld() {
      return super.isServerWorld() && !this.isAIDisabled();
   }

   public void setNoAI(boolean disable) {
      this.dataWatcher.updateObject(15, (byte)(disable ? 1 : 0));
   }

   public boolean isAIDisabled() {
      return this.dataWatcher.getWatchableObjectByte(15) != 0;
   }

   public boolean canSkipUpdate() {
      if (this.isChild()) {
         return false;
      }

      if (this.hurtTime > 0) {
         return false;
      }

      if (this.ticksExisted < 20) {
         return false;
      }

      World entityWorld = this.getEntityWorld();
      if (entityWorld == null) {
         return false;
      }

      if (entityWorld.playerEntities.size() != 1) {
         return false;
      }

      Entity entity = entityWorld.playerEntities.get(0);
      double max = Math.max(Math.abs(this.posX - entity.posX) - 16.0, 0.0);
      double max2 = Math.max(Math.abs(this.posZ - entity.posZ) - 16.0, 0.0);
      double d = max * max + max2 * max2;
      return !this.isInRangeToRenderDist(d);
   }

   public void onUpdateMinimal() {
      this.entityAge++;
      if (this instanceof EntityMob) {
         float brightness = this.getBrightness(1.0F);
         if (brightness > 0.5F) {
            this.entityAge += 2;
         }
      }

      this.despawnEntity();
   }

   @Override
   public Team getTeam() {
      UUID uniqueID = this.getUniqueID();
      if (this.teamUuid != uniqueID) {
         this.teamUuid = uniqueID;
         this.teamUuidString = uniqueID.toString();
      }

      return this.worldObj.getScoreboard().getPlayersTeam(this.teamUuidString);
   }

   public enum SpawnPlacementType {
      ON_GROUND,
      IN_AIR,
      IN_WATER;
   }
}
