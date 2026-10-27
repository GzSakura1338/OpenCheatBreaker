package net.minecraft.entity.monster;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
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
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityZombie extends EntityMob {
   public static IAttribute reinforcementChance = new RangedAttribute((IAttribute)null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0)
      .setDescription("Spawn Reinforcements Chance");
   public static UUID babySpeedBoostUUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   public static AttributeModifier babySpeedBoostModifier = new AttributeModifier(babySpeedBoostUUID, "Baby speed boost", 0.5, 1);
   public EntityAIBreakDoor breakDoor = new EntityAIBreakDoor(this);
   public int conversionTime;
   public boolean isBreakDoorsTaskSet = false;
   public float zombieWidth = -1.0F;
   public float zombieHeight;

   public EntityZombie(World worldIn) {
      super(worldIn);
      ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.tasks.addTask(7, new EntityAIWander(this, 1.0));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.applyEntityAI();
      this.setSize(0.6F, 1.95F);
   }

   public void applyEntityAI() {
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityIronGolem.class, 1.0, true));
      this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0, false));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityPigZombie.class));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(35.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23F);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0);
      this.getAttributeMap().registerAttribute(reinforcementChance).setBaseValue(this.rand.nextDouble() * 0.1F);
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

   public boolean isBreakDoorsTaskSet() {
      return this.isBreakDoorsTaskSet;
   }

   public void setBreakDoorsAItask(boolean par1) {
      if (this.isBreakDoorsTaskSet != par1) {
         this.isBreakDoorsTaskSet = par1;
         if (par1) {
            this.tasks.addTask(1, this.breakDoor);
         } else {
            this.tasks.removeTask(this.breakDoor);
         }
      }
   }

   @Override
   public boolean isChild() {
      return this.getDataWatcher().getWatchableObjectByte(12) == 1;
   }

   @Override
   public int getExperiencePoints(EntityPlayer player) {
      if (this.isChild()) {
         this.experienceValue = (int)(this.experienceValue * 2.5F);
      }

      return super.getExperiencePoints(player);
   }

   public void setChild(boolean childZombie) {
      this.getDataWatcher().updateObject(12, (byte)(childZombie ? 1 : 0));
      if (this.worldObj != null && !this.worldObj.isRemote) {
         IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
         entityAttribute.removeModifier(babySpeedBoostModifier);
         if (childZombie) {
            entityAttribute.applyModifier(babySpeedBoostModifier);
         }
      }

      this.setChildSize(childZombie);
   }

   public boolean isVillager() {
      return this.getDataWatcher().getWatchableObjectByte(13) == 1;
   }

   public void setVillager(boolean villager) {
      this.getDataWatcher().updateObject(13, (byte)(villager ? 1 : 0));
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isDaytime() && !this.worldObj.isRemote && !this.isChild()) {
         float brightness = this.getBrightness(1.0F);
         BlockPos blockPos = new BlockPos(this.posX, Math.round(this.posY), this.posZ);
         if (brightness > 0.5F && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && this.worldObj.canSeeSky(blockPos)) {
            boolean flag = true;
            ItemStack equipmentInSlot = this.getEquipmentInSlot(4);
            if (equipmentInSlot != null) {
               if (equipmentInSlot.isItemStackDamageable()) {
                  equipmentInSlot.setItemDamage(equipmentInSlot.getItemDamage() + this.rand.nextInt(2));
                  if (equipmentInSlot.getItemDamage() >= equipmentInSlot.getMaxDamage()) {
                     this.renderBrokenItemStack(equipmentInSlot);
                     this.setCurrentItemOrArmor(4, (ItemStack)null);
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
      if (super.attackEntityFrom(source, amount)) {
         EntityLivingBase attackTarget = this.getAttackTarget();
         if (attackTarget == null && source.getEntity() instanceof EntityLivingBase) {
            attackTarget = (EntityLivingBase)source.getEntity();
         }

         if (attackTarget != null
            && this.worldObj.getDifficulty() == EnumDifficulty.HARD
            && this.rand.nextFloat() < this.getEntityAttribute(reinforcementChance).getAttributeValue()) {
            int n = MathHelper.floor_double(this.posX);
            int n2 = MathHelper.floor_double(this.posY);
            int n3 = MathHelper.floor_double(this.posZ);
            EntityZombie entityZombie = new EntityZombie(this.worldObj);

            for (int i = 0; i < 50; i++) {
               int n4 = n + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
               int n5 = n2 + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
               int n6 = n3 + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
               if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(n4, n5 - 1, n6))
                  && this.worldObj.getLightFromNeighbors(new BlockPos(n4, n5, n6)) < 10) {
                  entityZombie.setPosition(n4, n5, n6);
                  if (!this.worldObj.isAnyPlayerWithinRangeAt(n4, n5, n6, 7.0)
                     && this.worldObj.checkNoEntityCollision(entityZombie.getEntityBoundingBox(), entityZombie)
                     && this.worldObj.getCollidingBoundingBoxes(entityZombie, entityZombie.getEntityBoundingBox()).isEmpty()
                     && !this.worldObj.isAnyLiquid(entityZombie.getEntityBoundingBox())) {
                     this.worldObj.spawnEntityInWorld(entityZombie);
                     entityZombie.setAttackTarget(attackTarget);
                     entityZombie.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityZombie)), (IEntityLivingData)null);
                     this.getEntityAttribute(reinforcementChance).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05F, 0));
                     entityZombie.getEntityAttribute(reinforcementChance).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05F, 0));
                     break;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onUpdate() {
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
   public boolean attackEntityAsMob(Entity entityIn) {
      boolean attackEntityAsMob = super.attackEntityAsMob(entityIn);
      if (attackEntityAsMob) {
         int difficultyId = this.worldObj.getDifficulty().getDifficultyId();
         if (this.getHeldItem() == null && this.isBurning() && this.rand.nextFloat() < difficultyId * 0.3F) {
            entityIn.setFire(2 * difficultyId);
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
   public void playStepSound(BlockPos pos, Block blockIn) {
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
   public void addRandomDrop() {
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
   public void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      super.setEquipmentBasedOnDifficulty(difficulty);
      if (this.rand.nextFloat() < (this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
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
      tagCompound.setBoolean("CanBreakDoors", this.isBreakDoorsTaskSet());
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

      this.setBreakDoorsAItask(tagCompund.getBoolean("CanBreakDoors"));
   }

   @Override
   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      if ((this.worldObj.getDifficulty() == EnumDifficulty.NORMAL || this.worldObj.getDifficulty() == EnumDifficulty.HARD)
         && entityLivingIn instanceof EntityVillager) {
         if (this.worldObj.getDifficulty() != EnumDifficulty.HARD && this.rand.nextBoolean()) {
            return;
         }

         EntityLiving entityLivingIn2 = (EntityLiving)entityLivingIn;
         EntityZombie entityZombie = new EntityZombie(this.worldObj);
         entityZombie.copyLocationAndAnglesFrom(entityLivingIn);
         this.worldObj.removeEntity(entityLivingIn);
         entityZombie.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityZombie)), (IEntityLivingData)null);
         entityZombie.setVillager(true);
         if (entityLivingIn.isChild()) {
            entityZombie.setChild(true);
         }

         entityZombie.setNoAI(entityLivingIn2.isAIDisabled());
         if (entityLivingIn2.hasCustomName()) {
            entityZombie.setCustomNameTag(entityLivingIn2.getCustomNameTag());
            entityZombie.setAlwaysRenderNameTag(entityLivingIn2.getAlwaysRenderNameTag());
         }

         this.worldObj.spawnEntityInWorld(entityZombie);
         this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);
      }
   }

   @Override
   public float getEyeHeight() {
      float f = 1.74F;
      if (this.isChild()) {
         f = (float)(f - 0.81);
      }

      return f;
   }

   @Override
   public boolean canPickupItem(ItemStack stack) {
      return stack.getItem() == Items.egg && this.isChild() && this.isRiding() ? false : super.canPickupItem(stack);
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      float clampedAdditionalDifficulty = difficulty.getClampedAdditionalDifficulty();
      this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * clampedAdditionalDifficulty);
      if (livingdata == null) {
         livingdata = new EntityZombie.GroupData(this.worldObj.rand.nextFloat() < 0.05F, this.worldObj.rand.nextFloat() < 0.05F);
      }

      if (livingdata instanceof EntityZombie.GroupData) {
         EntityZombie.GroupData livingdata2 = (EntityZombie.GroupData)livingdata;
         if (livingdata2.isVillager) {
            this.setVillager(true);
         }

         if (livingdata2.isChild) {
            this.setChild(true);
            if (this.worldObj.rand.nextFloat() < 0.05) {
               List entitiesWithinAABB = this.worldObj
                  .getEntitiesWithinAABB(EntityChicken.class, this.getEntityBoundingBox().expand(5.0, 3.0, 5.0), EntitySelectors.IS_STANDALONE);
               if (!entitiesWithinAABB.isEmpty()) {
                  EntityChicken entityChicken = (EntityChicken)entitiesWithinAABB.get(0);
                  entityChicken.setChickenJockey(true);
                  this.mountEntity(entityChicken);
               }
            } else if (this.worldObj.rand.nextFloat() < 0.05) {
               EntityChicken entityChicken = new EntityChicken(this.worldObj);
               entityChicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
               entityChicken.onInitialSpawn(difficulty, (IEntityLivingData)null);
               entityChicken.setChickenJockey(true);
               this.worldObj.spawnEntityInWorld(entityChicken);
               this.mountEntity(entityChicken);
            }
         }
      }

      this.setBreakDoorsAItask(this.rand.nextFloat() < clampedAdditionalDifficulty * 0.1F);
      this.setEquipmentBasedOnDifficulty(difficulty);
      this.setEnchantmentBasedOnDifficulty(difficulty);
      if (this.getEquipmentInSlot(4) == null) {
         Calendar currentDate = this.worldObj.getCurrentDate();
         if (currentDate.get(2) + 1 == 10 && currentDate.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
            this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
            this.equipmentDropChances[4] = 0.0F;
         }
      }

      this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance)
         .applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * 0.05F, 0));
      double d = this.rand.nextDouble() * 1.5 * clampedAdditionalDifficulty;
      if (d > 1.0) {
         this.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random zombie-spawn bonus", d, 2));
      }

      if (this.rand.nextFloat() < clampedAdditionalDifficulty * 0.05F) {
         this.getEntityAttribute(reinforcementChance).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 0.25 + 0.5, 0));
         this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 3.0 + 1.0, 2));
         this.setBreakDoorsAItask(true);
      }

      return livingdata;
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack currentEquippedItem = player.getCurrentEquippedItem();
      if (currentEquippedItem != null && currentEquippedItem.getItem() == Items.golden_apple && currentEquippedItem.getMetadata() == 0 && this.isVillager() && this.isPotionActive(Potion.weakness)) {
         if (!player.capabilities.isCreativeMode) {
            currentEquippedItem.stackSize--;
         }

         if (currentEquippedItem.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
         }

         if (!this.worldObj.isRemote) {
            this.startConversion(this.rand.nextInt(2401) + 3600);
         }

         return true;
      } else {
         return false;
      }
   }

   public void startConversion(int ticks) {
      this.conversionTime = ticks;
      this.getDataWatcher().updateObject(14, (byte)1);
      this.removePotionEffect(Potion.weakness.id);
      this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, ticks, Math.min(this.worldObj.getDifficulty().getDifficultyId() - 1, 0)));
      this.worldObj.setEntityState(this, (byte)16);
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 16) {
         if (!this.isSilent()) {
            this.worldObj
               .playSound(
                  this.posX + 0.5,
                  this.posY + 0.5,
                  this.posZ + 0.5,
                  "mob.zombie.remedy",
                  1.0F + this.rand.nextFloat(),
                  this.rand.nextFloat() * 0.7F + 0.3F,
                  false
               );
         }
      } else {
         super.handleStatusUpdate(id);
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
      entityVillager.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityVillager)), (IEntityLivingData)null);
      entityVillager.setLookingForHome();
      if (this.isChild()) {
         entityVillager.setGrowingAge(-24000);
      }

      this.worldObj.removeEntity(this);
      entityVillager.setNoAI(this.isAIDisabled());
      if (this.hasCustomName()) {
         entityVillager.setCustomNameTag(this.getCustomNameTag());
         entityVillager.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
      }

      this.worldObj.spawnEntityInWorld(entityVillager);
      entityVillager.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
      this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1017, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);
   }

   public int getConversionTimeBoost() {
      int n = 1;
      if (this.rand.nextFloat() < 0.01F) {
         int n2 = 0;
         MutableBlockPos mutableBlockPos = new MutableBlockPos();

         for (int i = (int)this.posX - 4; i < (int)this.posX + 4 && n2 < 14; i++) {
            for (int j = (int)this.posY - 4; j < (int)this.posY + 4 && n2 < 14; j++) {
               for (int k = (int)this.posZ - 4; k < (int)this.posZ + 4 && n2 < 14; k++) {
                  Block block = this.worldObj.getBlockState(mutableBlockPos.set(i, j, k)).getBlock();
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

   public void setChildSize(boolean isChild) {
      this.multiplySize(isChild ? 0.5F : 1.0F);
   }

   @Override
   public void setSize(float width, float height) {
      boolean flag = this.zombieWidth > 0.0F && this.zombieHeight > 0.0F;
      this.zombieWidth = width;
      this.zombieHeight = height;
      if (!flag) {
         this.multiplySize(1.0F);
      }
   }

   public void multiplySize(float size) {
      super.setSize(this.zombieWidth * size, this.zombieHeight * size);
   }

   @Override
   public double getYOffset() {
      return this.isChild() ? 0.0 : -0.35;
   }

   @Override
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (cause.getEntity() instanceof EntityCreeper
         && !(this instanceof EntityPigZombie)
         && ((EntityCreeper)cause.getEntity()).getPowered()
         && ((EntityCreeper)cause.getEntity()).isAIEnabled()) {
         ((EntityCreeper)cause.getEntity()).onHeadDropped();
         this.entityDropItem(new ItemStack(Items.skull, 1, 2), 0.0F);
      }
   }

   class GroupData implements IEntityLivingData {
      public boolean isChild = false;
      public boolean isVillager = false;

      public GroupData(boolean isBaby, boolean isVillagerZombie) {
         this.isChild = isBaby;
         this.isVillager = isVillagerZombie;
      }
   }
}
