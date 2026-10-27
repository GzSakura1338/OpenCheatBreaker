package net.minecraft.entity.player;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.cosmetic.Cosmetic;
import com.cheatbreaker.client.emote.PlayerEmoteState;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

public abstract class EntityPlayer extends EntityLivingBase {
   public InventoryPlayer inventory = new InventoryPlayer(this);
   public InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();
   public FoodStats foodStats = new FoodStats();
   public PlayerCapabilities capabilities = new PlayerCapabilities();
   public float speedOnGround = 0.1F;
   public int flyToggleTimer;
   public float prevCameraYaw;
   public float cameraYaw;
   public int xpCooldown;
   public double prevChasingPosX;
   public double prevChasingPosY;
   public double prevChasingPosZ;
   public double chasingPosX;
   public double chasingPosY;
   public double chasingPosZ;
   public boolean sleeping;
   public BlockPos playerLocation;
   public int sleepTimer;
   public float renderOffsetX;
   public float renderOffsetY;
   public float renderOffsetZ;
   public BlockPos spawnChunk;
   public boolean spawnForced;
   public BlockPos startMinecartRidingCoordinate;
   public float speedInAir = 0.02F;
   public int experienceLevel;
   public int experienceTotal;
   public float experience;
   public int xpSeed;
   public ItemStack itemInUse;
   public int itemInUseCount;
   public boolean hasReducedDebug = false;
   public Cosmetic cosmetic = null;
   public int lastXPSound;
   public Cosmetic cosmetic2 = null;
   public Cosmetic cosmetic3 = null;
   public EntityFishHook fishEntity;
   public GameProfile gameProfile;
   public Container inventoryContainer;
   public Container openContainer;

   public EntityPlayer(World worldIn, GameProfile gameProfileIn) {
      super(worldIn);
      this.entityUniqueID = getUUID(gameProfileIn);
      this.gameProfile = gameProfileIn;
      this.inventoryContainer = new ContainerPlayer(this.inventory, !worldIn.isRemote, this);
      this.openContainer = this.inventoryContainer;
      BlockPos spawnPoint = worldIn.getSpawnPoint();
      this.setLocationAndAngles(spawnPoint.getX() + 0.5, spawnPoint.getY() + 1, spawnPoint.getZ() + 0.5, 0.0F, 0.0F);
      this.unused180 = 180.0F;
      this.fireResistance = 20;
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.1F);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, (byte)0);
      this.dataWatcher.addObject(17, 0.0F);
      this.dataWatcher.addObject(18, 0);
      this.dataWatcher.addObject(10, (byte)0);
   }

   public ItemStack getItemInUse() {
      return this.itemInUse;
   }

   public int getItemInUseCount() {
      return this.itemInUseCount;
   }

   public boolean isUsingItem() {
      return this.itemInUse != null;
   }

   public int getItemInUseDuration() {
      return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
   }

   public void stopUsingItem() {
      if (this.itemInUse != null) {
         this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
      }

      this.clearItemInUse();
   }

   public void clearItemInUse() {
      this.itemInUse = null;
      this.itemInUseCount = 0;
      if (!this.worldObj.isRemote) {
         this.setEating(false);
      }
   }

   public boolean isBlocking() {
      return this.isUsingItem() && this.itemInUse.getItem().getItemUseAction(this.itemInUse) == EnumAction.BLOCK;
   }

   @Override
   public void onUpdate() {
      this.noClip = this.isSpectator();
      if (this.isSpectator()) {
         this.onGround = false;
      }

      if (this.itemInUse != null) {
         ItemStack currentItem = this.inventory.getCurrentItem();
         if (currentItem == this.itemInUse) {
            if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0) {
               this.updateItemUse(currentItem, 5);
            }

            if (--this.itemInUseCount == 0 && !this.worldObj.isRemote) {
               this.onItemUseFinish();
            }
         } else {
            this.clearItemInUse();
         }
      }

      if (this.xpCooldown > 0) {
         this.xpCooldown--;
      }

      if (this.isPlayerSleeping()) {
         this.sleepTimer++;
         if (this.sleepTimer > 100) {
            this.sleepTimer = 100;
         }

         if (!this.worldObj.isRemote) {
            if (!this.isInBed()) {
               this.wakeUpPlayer(true, true, false);
            } else if (this.worldObj.isDaytime()) {
               this.wakeUpPlayer(false, true, true);
            }
         }
      } else if (this.sleepTimer > 0) {
         this.sleepTimer++;
         if (this.sleepTimer >= 110) {
            this.sleepTimer = 0;
         }
      }

      super.onUpdate();
      if (!this.worldObj.isRemote && this.openContainer != null && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.inventoryContainer;
      }

      if (this.isBurning() && this.capabilities.disableDamage) {
         this.extinguish();
      }

      this.prevChasingPosX = this.chasingPosX;
      this.prevChasingPosY = this.chasingPosY;
      this.prevChasingPosZ = this.chasingPosZ;
      double d = this.posX - this.chasingPosX;
      double d2 = this.posY - this.chasingPosY;
      double d3 = this.posZ - this.chasingPosZ;
      double d4 = 10.0;
      if (d > d4) {
         this.prevChasingPosX = this.chasingPosX = this.posX;
      }

      if (d3 > d4) {
         this.prevChasingPosZ = this.chasingPosZ = this.posZ;
      }

      if (d2 > d4) {
         this.prevChasingPosY = this.chasingPosY = this.posY;
      }

      if (d < -d4) {
         this.prevChasingPosX = this.chasingPosX = this.posX;
      }

      if (d3 < -d4) {
         this.prevChasingPosZ = this.chasingPosZ = this.posZ;
      }

      if (d2 < -d4) {
         this.prevChasingPosY = this.chasingPosY = this.posY;
      }

      this.chasingPosX += d * 0.25;
      this.chasingPosZ += d3 * 0.25;
      this.chasingPosY += d2 * 0.25;
      if (this.ridingEntity == null) {
         this.startMinecartRidingCoordinate = null;
      }

      if (!this.worldObj.isRemote) {
         this.foodStats.onUpdate(this);
         this.triggerAchievement(StatList.minutesPlayedStat);
         if (this.isEntityAlive()) {
            this.triggerAchievement(StatList.timeSinceDeathStat);
         }
      }

      int n = 29999999;
      double d5 = MathHelper.clamp_double(this.posX, -2.9999999E7, 2.9999999E7);
      double d6 = MathHelper.clamp_double(this.posZ, -2.9999999E7, 2.9999999E7);
      if (d5 != this.posX || d6 != this.posZ) {
         this.setPosition(d5, this.posY, d6);
      }
   }

   @Override
   public int getMaxInPortalTime() {
      return this.capabilities.disableDamage ? 0 : 80;
   }

   @Override
   public String getSwimSound() {
      return "game.player.swim";
   }

   @Override
   public String getSplashSound() {
      return "game.player.swim.splash";
   }

   @Override
   public int getPortalCooldown() {
      return 10;
   }

   @Override
   public void playSound(String name, float volume, float pitch) {
      this.worldObj.playSoundToNearExcept(this, name, volume, pitch);
   }

   public void updateItemUse(ItemStack itemStackIn, int n) {
      if (itemStackIn.getItemUseAction() == EnumAction.DRINK) {
         this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (itemStackIn.getItemUseAction() == EnumAction.EAT) {
         for (int i = 0; i < n; i++) {
            Vec3 vec3 = new Vec3((this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3 = vec3.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
            vec3 = vec3.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
            double d = -this.rand.nextFloat() * 0.6 - 0.3;
            Vec3 vec32 = new Vec3((this.rand.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec32 = vec32.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
            vec32 = vec32.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
            vec32 = vec32.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
            if (itemStackIn.getHasSubtypes()) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.ITEM_CRACK,
                     vec32.xCoord,
                     vec32.yCoord,
                     vec32.zCoord,
                     vec3.xCoord,
                     vec3.yCoord + 0.05,
                     vec3.zCoord,
                     Item.getIdFromItem(itemStackIn.getItem()),
                     itemStackIn.getMetadata()
                  );
            } else {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.ITEM_CRACK,
                     vec32.xCoord,
                     vec32.yCoord,
                     vec32.zCoord,
                     vec3.xCoord,
                     vec3.yCoord + 0.05,
                     vec3.zCoord,
                     Item.getIdFromItem(itemStackIn.getItem())
                  );
            }
         }

         this.playSound("random.eat", 0.5F + 0.5F * this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
      }
   }

   public void onItemUseFinish() {
      if (this.itemInUse != null) {
         this.updateItemUse(this.itemInUse, 16);
         int stackSize = this.itemInUse.stackSize;
         ItemStack onItemUseFinish = this.itemInUse.onItemUseFinish(this.worldObj, this);
         if (onItemUseFinish != this.itemInUse || onItemUseFinish != null && onItemUseFinish.stackSize != stackSize) {
            this.inventory.mainInventory[this.inventory.currentItem] = onItemUseFinish;
            if (onItemUseFinish.stackSize == 0) {
               this.inventory.mainInventory[this.inventory.currentItem] = null;
            }
         }

         this.clearItemInUse();
      }
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 9) {
         this.onItemUseFinish();
      } else if (id == 23) {
         this.hasReducedDebug = false;
      } else if (id == 22) {
         this.hasReducedDebug = true;
      } else {
         super.handleStatusUpdate(id);
      }
   }

   @Override
   public boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F || this.isPlayerSleeping();
   }

   public void closeScreen() {
      this.openContainer = this.inventoryContainer;
   }

   @Override
   public void updateRidden() {
      if (!this.worldObj.isRemote && this.isSneaking()) {
         this.mountEntity((Entity)null);
         this.setSneaking(false);
      } else {
         double posX = this.posX;
         double posY = this.posY;
         double posZ = this.posZ;
         float rotationYaw = this.rotationYaw;
         float rotationPitch = this.rotationPitch;
         super.updateRidden();
         this.prevCameraYaw = this.cameraYaw;
         this.cameraYaw = 0.0F;
         this.addMountedMovementStat(this.posX - posX, this.posY - posY, this.posZ - posZ);
         if (this.ridingEntity instanceof EntityPig) {
            this.rotationPitch = rotationPitch;
            this.rotationYaw = rotationYaw;
            this.renderYawOffset = ((EntityPig)this.ridingEntity).renderYawOffset;
         }
      }
   }

   @Override
   public void preparePlayerToSpawn() {
      this.setSize(0.6F, 1.8F);
      super.preparePlayerToSpawn();
      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   @Override
   public void updateEntityActionState() {
      super.updateEntityActionState();
      this.updateArmSwingProgress();
      this.rotationYawHead = this.rotationYaw;
   }

   @Override
   public void onLivingUpdate() {
      if (this.flyToggleTimer > 0) {
         this.flyToggleTimer--;
      }

      if (this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL && this.worldObj.getGameRules().getBoolean("naturalRegeneration")) {
         if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
            this.heal(1.0F);
         }

         if (this.foodStats.needFood() && this.ticksExisted % 10 == 0) {
            this.foodStats.setFoodLevel(this.foodStats.getFoodLevel() + 1);
         }
      }

      this.inventory.decrementAnimations();
      this.prevCameraYaw = this.cameraYaw;
      super.onLivingUpdate();
      PlayerEmoteState.tickPlayer(this);
      IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (!this.worldObj.isRemote) {
         entityAttribute.setBaseValue(this.capabilities.getWalkSpeed());
      }

      this.jumpMovementFactor = this.speedInAir;
      if (this.isSprinting()) {
         this.jumpMovementFactor = (float)(this.jumpMovementFactor + this.speedInAir * 0.3);
      }

      this.setAIMoveSpeed((float)entityAttribute.getAttributeValue());
      float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      float f2 = (float)(Math.atan(-this.motionY * 0.2F) * 15.0);
      if (f > 0.1F) {
         f = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0.0F) {
         f = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         f2 = 0.0F;
      }

      this.cameraYaw = this.cameraYaw + (f - this.cameraYaw) * 0.4F;
      this.cameraPitch = this.cameraPitch + (f2 - this.cameraPitch) * 0.8F;
      if (this.getHealth() > 0.0F && !this.isSpectator()) {
         AxisAlignedBB axisAlignedBB = null;
         if (this.ridingEntity != null && !this.ridingEntity.isDead) {
            axisAlignedBB = this.getEntityBoundingBox().union(this.ridingEntity.getEntityBoundingBox()).expand(1.0, 0.0, 1.0);
         } else {
            axisAlignedBB = this.getEntityBoundingBox().expand(1.0, 0.5, 1.0);
         }

         List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, axisAlignedBB);

         for (int i = 0; i < list.size(); i++) {
            Entity entity = (Entity)list.get(i);
            if (!entity.isDead) {
               this.collideWithPlayer(entity);
            }
         }
      }
   }

   public void collideWithPlayer(Entity entity) {
      entity.onCollideWithPlayer(this);
   }

   public int getScore() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public void setScore(int n) {
      this.dataWatcher.updateObject(18, n);
   }

   public void addScore(int n) {
      int score = this.getScore();
      this.dataWatcher.updateObject(18, score + n);
   }

   @Override
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      this.setSize(0.2F, 0.2F);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.motionY = 0.1F;
      if (this.getName().equals("Notch")) {
         this.dropItem(new ItemStack(Items.apple, 1), true, false);
      }

      if (!this.worldObj.getGameRules().getBoolean("keepInventory")) {
         this.inventory.dropAllItems();
      }

      if (cause != null) {
         this.motionX = -MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F;
         this.motionZ = -MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F;
      } else {
         this.motionX = this.motionZ = 0.0;
      }

      this.triggerAchievement(StatList.deathsStat);
      this.handleStatBase(StatList.timeSinceDeathStat);
   }

   @Override
   public String getHurtSound() {
      return "game.player.hurt";
   }

   @Override
   public String getDeathSound() {
      return "game.player.die";
   }

   @Override
   public void addToPlayerScore(Entity entityIn, int amount) {
      this.addScore(amount);
      Collection objectivesFromCriteria = this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.totalKillCount);
      if (entityIn instanceof EntityPlayer) {
         this.triggerAchievement(StatList.playerKillsStat);
         objectivesFromCriteria.addAll(this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.playerKillCount));
         objectivesFromCriteria.addAll(this.findCollection(entityIn));
      } else {
         this.triggerAchievement(StatList.mobKillsStat);
      }

      for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)objectivesFromCriteria) {
         Score valueFromObjective = this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective);
         valueFromObjective.incrementScore();
      }
   }

   public Collection<ScoreObjective> findCollection(Entity entity) {
      ScorePlayerTeam playersTeam = this.getWorldScoreboard().getPlayersTeam(this.getName());
      if (playersTeam != null) {
         int colorIndex = playersTeam.getChatFormat().getColorIndex();
         if (colorIndex >= 0 && colorIndex < IScoreObjectiveCriteria.KILLED_BY_TEAM.length) {
            for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.KILLED_BY_TEAM[colorIndex])) {
               Score valueFromObjective = this.getWorldScoreboard().getValueFromObjective(entity.getName(), scoreObjective);
               valueFromObjective.incrementScore();
            }
         }
      }

      ScorePlayerTeam playersTeam2 = this.getWorldScoreboard().getPlayersTeam(entity.getName());
      if (playersTeam2 != null) {
         int colorIndex = playersTeam2.getChatFormat().getColorIndex();
         if (colorIndex >= 0 && colorIndex < IScoreObjectiveCriteria.TEAM_KILLS.length) {
            return this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.TEAM_KILLS[colorIndex]);
         }
      }

      return Lists.newArrayList();
   }

   public EntityItem dropOneItem(boolean dropAll) {
      return this.dropItem(
         this.inventory
            .decrStackSize(this.inventory.currentItem, dropAll && this.inventory.getCurrentItem() != null ? this.inventory.getCurrentItem().stackSize : 1),
         false,
         true
      );
   }

   public EntityItem dropPlayerItemWithRandomChoice(ItemStack itemStackIn, boolean unused) {
      return this.dropItem(itemStackIn, false, false);
   }

   public EntityItem dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem) {
      if (droppedItem == null) {
         return null;
      }

      if (droppedItem.stackSize == 0) {
         return null;
      }

      double d = this.posY - 0.3F + this.getEyeHeight();
      EntityItem entityItem = new EntityItem(this.worldObj, this.posX, d, this.posZ, droppedItem);
      entityItem.setPickupDelay(40);
      if (traceItem) {
         entityItem.setThrower(this.getName());
      }

      if (dropAround) {
         float f = this.rand.nextFloat() * 0.5F;
         float f2 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
         entityItem.motionX = -MathHelper.sin(f2) * f;
         entityItem.motionZ = MathHelper.cos(f2) * f;
         entityItem.motionY = 0.2F;
      } else {
         float f = 0.3F;
         entityItem.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
         entityItem.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
         entityItem.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f + 0.1F;
         float f2 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
         f = 0.02F * this.rand.nextFloat();
         entityItem.motionX = entityItem.motionX + Math.cos(f2) * f;
         entityItem.motionY = entityItem.motionY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
         entityItem.motionZ = entityItem.motionZ + Math.sin(f2) * f;
      }

      this.joinEntityItemWithWorld(entityItem);
      if (traceItem) {
         this.triggerAchievement(StatList.dropStat);
      }

      return entityItem;
   }

   public void joinEntityItemWithWorld(EntityItem itemIn) {
      this.worldObj.spawnEntityInWorld(itemIn);
   }

   public float getToolDigEfficiency(Block block) {
      float strVsBlock = this.inventory.getStrVsBlock(block);
      if (strVsBlock > 1.0F) {
         int efficiencyModifier = EnchantmentHelper.getEfficiencyModifier(this);
         ItemStack currentItem = this.inventory.getCurrentItem();
         if (efficiencyModifier > 0 && currentItem != null) {
            strVsBlock += efficiencyModifier * efficiencyModifier + 1;
         }
      }

      if (this.isPotionActive(Potion.digSpeed)) {
         strVsBlock *= 1.0F + (this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
      }

      if (this.isPotionActive(Potion.digSlowdown)) {
         float f = 1.0F;
         switch (this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
            case 0:
               f = 0.3F;
               break;
            case 1:
               f = 0.09F;
               break;
            case 2:
               f = 0.0027F;
               break;
            case 3:
            default:
               f = 8.1E-4F;
         }

         strVsBlock *= f;
      }

      if (this.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
         strVsBlock /= 5.0F;
      }

      if (!this.onGround) {
         strVsBlock /= 5.0F;
      }

      return strVsBlock;
   }

   public boolean canHarvestBlock(Block blockToHarvest) {
      return this.inventory.canHeldItemHarvest(blockToHarvest);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.entityUniqueID = getUUID(this.gameProfile);
      NBTTagList tagList = tagCompund.getTagList("Inventory", 10);
      this.inventory.readFromNBT(tagList);
      this.inventory.currentItem = tagCompund.getInteger("SelectedItemSlot");
      this.sleeping = tagCompund.getBoolean("Sleeping");
      this.sleepTimer = tagCompund.getShort("SleepTimer");
      this.experience = tagCompund.getFloat("XpP");
      this.experienceLevel = tagCompund.getInteger("XpLevel");
      this.experienceTotal = tagCompund.getInteger("XpTotal");
      this.xpSeed = tagCompund.getInteger("XpSeed");
      if (this.xpSeed == 0) {
         this.xpSeed = this.rand.nextInt();
      }

      this.setScore(tagCompund.getInteger("Score"));
      if (this.sleeping) {
         this.playerLocation = new BlockPos(this);
         this.wakeUpPlayer(true, true, false);
      }

      if (tagCompund.hasKey("SpawnX", 99) && tagCompund.hasKey("SpawnY", 99) && tagCompund.hasKey("SpawnZ", 99)) {
         this.spawnChunk = new BlockPos(tagCompund.getInteger("SpawnX"), tagCompund.getInteger("SpawnY"), tagCompund.getInteger("SpawnZ"));
         this.spawnForced = tagCompund.getBoolean("SpawnForced");
      }

      this.foodStats.readNBT(tagCompund);
      this.capabilities.readCapabilitiesFromNBT(tagCompund);
      if (tagCompund.hasKey("EnderItems", 9)) {
         NBTTagList tagList2 = tagCompund.getTagList("EnderItems", 10);
         this.theInventoryEnderChest.loadInventoryFromNBT(tagList2);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
      tagCompound.setInteger("SelectedItemSlot", this.inventory.currentItem);
      tagCompound.setBoolean("Sleeping", this.sleeping);
      tagCompound.setShort("SleepTimer", (short)this.sleepTimer);
      tagCompound.setFloat("XpP", this.experience);
      tagCompound.setInteger("XpLevel", this.experienceLevel);
      tagCompound.setInteger("XpTotal", this.experienceTotal);
      tagCompound.setInteger("XpSeed", this.xpSeed);
      tagCompound.setInteger("Score", this.getScore());
      if (this.spawnChunk != null) {
         tagCompound.setInteger("SpawnX", this.spawnChunk.getX());
         tagCompound.setInteger("SpawnY", this.spawnChunk.getY());
         tagCompound.setInteger("SpawnZ", this.spawnChunk.getZ());
         tagCompound.setBoolean("SpawnForced", this.spawnForced);
      }

      this.foodStats.writeNBT(tagCompound);
      this.capabilities.writeCapabilitiesToNBT(tagCompound);
      tagCompound.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
      ItemStack currentItem = this.inventory.getCurrentItem();
      if (currentItem != null && currentItem.getItem() != null) {
         tagCompound.setTag("SelectedItem", currentItem.writeToNBT(new NBTTagCompound()));
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (this.capabilities.disableDamage && !source.canHarmInCreative()) {
         return false;
      }

      this.entityAge = 0;
      if (this.getHealth() <= 0.0F) {
         return false;
      }

      if (this.isPlayerSleeping() && !this.worldObj.isRemote) {
         this.wakeUpPlayer(true, true, false);
      }

      if (source.isDifficultyScaled()) {
         if (this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
            amount = 0.0F;
         }

         if (this.worldObj.getDifficulty() == EnumDifficulty.EASY) {
            amount = amount / 2.0F + 1.0F;
         }

         if (this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
            amount = amount * 3.0F / 2.0F;
         }
      }

      if (amount == 0.0F) {
         return false;
      }

      Entity entity = source.getEntity();
      if (entity instanceof EntityArrow && ((EntityArrow)entity).shootingEntity != null) {
         entity = ((EntityArrow)entity).shootingEntity;
      }

      return super.attackEntityFrom(source, amount);
   }

   public boolean canAttackPlayer(EntityPlayer other) {
      Team team = this.getTeam();
      Team team2 = other.getTeam();
      return team == null ? true : (!team.isSameTeam(team2) ? true : team.getAllowFriendlyFire());
   }

   @Override
   public void damageArmor(float f) {
      this.inventory.damageArmor(f);
   }

   @Override
   public int getTotalArmorValue() {
      return this.inventory.getTotalArmorValue();
   }

   public float getArmorVisibility() {
      int n = 0;

      for (ItemStack itemStack : this.inventory.armorInventory) {
         if (itemStack != null) {
            n++;
         }
      }

      return (float)n / this.inventory.armorInventory.length;
   }

   @Override
   public void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isEntityInvulnerable(damageSrc)) {
         if (!damageSrc.isUnblockable() && this.isBlocking() && damageAmount > 0.0F) {
            damageAmount = (1.0F + damageAmount) * 0.5F;
         }

         damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
         damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
         float damageAmount2 = damageAmount;
         damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (damageAmount2 - damageAmount));
         if (damageAmount != 0.0F) {
            this.addExhaustion(damageSrc.getHungerDamage());
            float health = this.getHealth();
            this.setHealth(this.getHealth() - damageAmount);
            this.getCombatTracker().trackDamage(damageSrc, health, damageAmount);
            if (damageAmount < 3.4028235E37F) {
               this.addStat(StatList.damageTakenStat, Math.round(damageAmount * 10.0F));
            }
         }
      }
   }

   public void openEditSign(TileEntitySign signTile) {
   }

   public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic) {
   }

   public void displayVillagerTradeGui(IMerchant villager) {
   }

   public void displayGUIChest(IInventory chestInventory) {
   }

   public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
   }

   public void displayGui(IInteractionObject guiOwner) {
   }

   public void displayGUIBook(ItemStack bookStack) {
   }

   public boolean interactWith(Entity targetEntity) {
      if (this.isSpectator()) {
         if (targetEntity instanceof IInventory) {
            this.displayGUIChest((IInventory)targetEntity);
         }

         return false;
      } else {
         ItemStack currentEquippedItem = this.getCurrentEquippedItem();
         ItemStack itemStack = currentEquippedItem != null ? currentEquippedItem.copy() : null;
         if (!targetEntity.interactFirst(this)) {
            if (currentEquippedItem != null && targetEntity instanceof EntityLivingBase) {
               if (this.capabilities.isCreativeMode) {
                  currentEquippedItem = itemStack;
               }

               if (currentEquippedItem.interactWithEntity(this, (EntityLivingBase)targetEntity)) {
                  if (currentEquippedItem.stackSize <= 0 && !this.capabilities.isCreativeMode) {
                     this.destroyCurrentEquippedItem();
                  }

                  return true;
               }
            }

            return false;
         } else {
            if (currentEquippedItem != null && currentEquippedItem == this.getCurrentEquippedItem()) {
               if (currentEquippedItem.stackSize <= 0 && !this.capabilities.isCreativeMode) {
                  this.destroyCurrentEquippedItem();
               } else if (currentEquippedItem.stackSize < itemStack.stackSize && this.capabilities.isCreativeMode) {
                  currentEquippedItem.stackSize = itemStack.stackSize;
               }
            }

            return true;
         }
      }
   }

   public ItemStack getCurrentEquippedItem() {
      return this.inventory.getCurrentItem();
   }

   public void destroyCurrentEquippedItem() {
      this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack)null);
   }

   @Override
   public double getYOffset() {
      return -0.35;
   }

   public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
      if (targetEntity.canAttackWithItem() && !targetEntity.hitByEntity(this)) {
         float attributeValue = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
         int n = 0;
         float f = 0.0F;
         if (targetEntity instanceof EntityLivingBase) {
            f = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), ((EntityLivingBase)targetEntity).getCreatureAttribute());
         } else {
            f = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
         }

         n += EnchantmentHelper.getKnockbackModifier(this);
         if (this.isSprinting()) {
            n++;
         }

         if (attributeValue > 0.0F || f > 0.0F) {
            boolean flag = this.fallDistance > 0.0F
               && !this.onGround
               && !this.isOnLadder()
               && !this.isInWater()
               && !this.isPotionActive(Potion.blindness)
               && this.ridingEntity == null
               && targetEntity instanceof EntityLivingBase;
            if (flag && attributeValue > 0.0F) {
               attributeValue *= 1.5F;
            }

            attributeValue += f;
            boolean flag2 = false;
            int fireAspectModifier = EnchantmentHelper.getFireAspectModifier(this);
            if (targetEntity instanceof EntityLivingBase && fireAspectModifier > 0 && !targetEntity.isBurning()) {
               flag2 = true;
               targetEntity.setFire(1);
            }

            double motionX = targetEntity.motionX;
            double motionY = targetEntity.motionY;
            double motionZ = targetEntity.motionZ;
            boolean attackEntityFrom = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(this), attributeValue);
            if (attackEntityFrom) {
               if (n > 0) {
                  targetEntity.addVelocity(
                     -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * n * 0.5F,
                     0.1,
                     MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * n * 0.5F
                  );
                  this.motionX *= 0.6;
                  this.motionZ *= 0.6;
                  this.setSprinting(false);
               }

               if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                  ((EntityPlayerMP)targetEntity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(targetEntity));
                  targetEntity.velocityChanged = false;
                  targetEntity.motionX = motionX;
                  targetEntity.motionY = motionY;
                  targetEntity.motionZ = motionZ;
               }

               boolean flag3 = Minecraft.getMinecraft().isSingleplayer()
                  ? CheatBreaker.getInstance().getModManager().particlesMod.showWhenDamaged.getValue().equals("Override")
                  : CheatBreaker.getInstance().getModManager().particlesMod.showWhenAttacking.getValue().equals("Override");
               boolean flag4 = Minecraft.getMinecraft().isSingleplayer()
                  ? CheatBreaker.getInstance().getModManager().particlesMod.showWhenDamaged.getValue().equals("Vanilla")
                  : CheatBreaker.getInstance().getModManager().particlesMod.showWhenAttacking.getValue().equals("Vanilla");
               if (flag
                  || CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
                     && CheatBreaker.getInstance().getModManager().particlesMod.critParticles.getValue().equals("Always")
                     && !flag4) {
                  if (CheatBreaker.getInstance().getModManager().particlesMod.isEnabled() && !flag4) {
                     if (!CheatBreaker.getInstance().getModManager().particlesMod.critParticles.getValue().equals("Never") && flag3) {
                        float value = (Float)CheatBreaker.getInstance().getModManager().particlesMod.critMultiplier.getValue();
                        Minecraft.getMinecraft().effectRenderer.handleEntity(targetEntity, EnumParticleTypes.CRIT, value);
                     }
                  } else {
                     this.onCriticalHit(targetEntity);
                  }
               }

               if (f > 0.0F
                  || CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
                     && CheatBreaker.getInstance().getModManager().particlesMod.sharpnessParticles.getValue().equals("Always")
                     && !flag4) {
                  if (CheatBreaker.getInstance().getModManager().particlesMod.isEnabled() && !flag4) {
                     if (!CheatBreaker.getInstance().getModManager().particlesMod.sharpnessParticles.getValue().equals("Never") && flag3) {
                        float value = (Float)CheatBreaker.getInstance().getModManager().particlesMod.sharpnessMultiplier.getValue();
                        Minecraft.getMinecraft().effectRenderer.handleEntity(targetEntity, EnumParticleTypes.CRIT_MAGIC, value);
                     }
                  } else {
                     this.onEnchantmentCritical(targetEntity);
                  }
               }

               if (attributeValue >= 18.0F) {
                  this.triggerAchievement(AchievementList.overkill);
               }

               this.setLastAttacker(targetEntity);
               if (targetEntity instanceof EntityLivingBase) {
                  EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, this);
               }

               EnchantmentHelper.applyArthropodEnchantments(this, targetEntity);
               ItemStack currentEquippedItem = this.getCurrentEquippedItem();
               Entity targetEntity2 = targetEntity;
               if (targetEntity instanceof EntityDragonPart) {
                  IEntityMultiPart entityDragonObj = ((EntityDragonPart)targetEntity).entityDragonObj;
                  if (entityDragonObj instanceof EntityLivingBase) {
                     targetEntity2 = (EntityLivingBase)entityDragonObj;
                  }
               }

               if (currentEquippedItem != null && targetEntity2 instanceof EntityLivingBase) {
                  currentEquippedItem.hitEntity((EntityLivingBase)targetEntity2, this);
                  if (currentEquippedItem.stackSize <= 0) {
                     this.destroyCurrentEquippedItem();
                  }
               }

               if (targetEntity instanceof EntityLivingBase) {
                  this.addStat(StatList.damageDealtStat, Math.round(attributeValue * 10.0F));
                  if (fireAspectModifier > 0) {
                     targetEntity.setFire(fireAspectModifier * 4);
                  }
               }

               this.addExhaustion(0.3F);
            } else if (flag2) {
               targetEntity.extinguish();
            }
         }
      }
   }

   public void onCriticalHit(Entity entityHit) {
   }

   public void onEnchantmentCritical(Entity entityHit) {
   }

   @Override
   public void setDead() {
      super.setDead();
      this.inventoryContainer.onContainerClosed(this);
      if (this.openContainer != null) {
         this.openContainer.onContainerClosed(this);
      }
   }

   @Override
   public boolean isEntityInsideOpaqueBlock() {
      return !this.sleeping && super.isEntityInsideOpaqueBlock();
   }

   public boolean isUser() {
      return false;
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public EntityPlayer.EnumStatus trySleep(BlockPos bedLocation) {
      if (!this.worldObj.isRemote) {
         if (this.isPlayerSleeping() || !this.isEntityAlive()) {
            return EntityPlayer.EnumStatus.OTHER_PROBLEM;
         }

         if (!this.worldObj.provider.isSurfaceWorld()) {
            return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
         }

         if (this.worldObj.isDaytime()) {
            return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
         }

         if (Math.abs(this.posX - bedLocation.getX()) > 3.0 || Math.abs(this.posY - bedLocation.getY()) > 2.0 || Math.abs(this.posZ - bedLocation.getZ()) > 3.0
            )
          {
            return EntityPlayer.EnumStatus.TOO_FAR_AWAY;
         }

         double d = 8.0;
         double d2 = 5.0;
         List entitiesWithinAABB = this.worldObj
            .getEntitiesWithinAABB(
               EntityMob.class,
               new AxisAlignedBB(
                  bedLocation.getX() - d,
                  bedLocation.getY() - d2,
                  bedLocation.getZ() - d,
                  bedLocation.getX() + d,
                  bedLocation.getY() + d2,
                  bedLocation.getZ() + d
               )
            );
         if (!entitiesWithinAABB.isEmpty()) {
            return EntityPlayer.EnumStatus.NOT_SAFE;
         }
      }

      if (this.isRiding()) {
         this.mountEntity((Entity)null);
      }

      this.setSize(0.2F, 0.2F);
      if (this.worldObj.isBlockLoaded(bedLocation)) {
         EnumFacing value = this.worldObj.getBlockState(bedLocation).getValue(BlockDirectional.FACING);
         float f = 0.5F;
         float f2 = 0.5F;
         switch (value) {
            case SOUTH:
               f2 = 0.9F;
               break;
            case NORTH:
               f2 = 0.1F;
               break;
            case WEST:
               f = 0.1F;
               break;
            case EAST:
               f = 0.9F;
         }

         this.handleEnumFacing(value);
         this.setPosition(bedLocation.getX() + f, bedLocation.getY() + 0.6875F, bedLocation.getZ() + f2);
      } else {
         this.setPosition(bedLocation.getX() + 0.5F, bedLocation.getY() + 0.6875F, bedLocation.getZ() + 0.5F);
      }

      this.sleeping = true;
      this.sleepTimer = 0;
      this.playerLocation = bedLocation;
      this.motionX = this.motionZ = this.motionY = 0.0;
      if (!this.worldObj.isRemote) {
         this.worldObj.updateAllPlayersSleepingFlag();
      }

      return EntityPlayer.EnumStatus.OK;
   }

   public void handleEnumFacing(EnumFacing enumFacing) {
      this.renderOffsetX = 0.0F;
      this.renderOffsetZ = 0.0F;
      switch (enumFacing) {
         case SOUTH:
            this.renderOffsetZ = -1.8F;
            break;
         case NORTH:
            this.renderOffsetZ = 1.8F;
            break;
         case WEST:
            this.renderOffsetX = 1.8F;
            break;
         case EAST:
            this.renderOffsetX = -1.8F;
      }
   }

   public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
      this.setSize(0.6F, 1.8F);
      IBlockState blockState = this.worldObj.getBlockState(this.playerLocation);
      if (this.playerLocation != null && blockState.getBlock() == Blocks.bed) {
         this.worldObj.setBlockState(this.playerLocation, blockState.withProperty(BlockBed.OCCUPIED, false), 4);
         BlockPos safeExitLocation = BlockBed.getSafeExitLocation(this.worldObj, this.playerLocation, 0);
         if (safeExitLocation == null) {
            safeExitLocation = this.playerLocation.up();
         }

         this.setPosition(safeExitLocation.getX() + 0.5F, safeExitLocation.getY() + 0.1F, safeExitLocation.getZ() + 0.5F);
      }

      this.sleeping = false;
      if (!this.worldObj.isRemote && updateWorldFlag) {
         this.worldObj.updateAllPlayersSleepingFlag();
      }

      this.sleepTimer = immediately ? 0 : 100;
      if (setSpawn) {
         this.setSpawnPoint(this.playerLocation, false);
      }
   }

   public boolean isInBed() {
      return this.worldObj.getBlockState(this.playerLocation).getBlock() == Blocks.bed;
   }

   public static BlockPos getBedSpawnLocation(World worldIn, BlockPos bedLocation, boolean forceSpawn) {
      Block block = worldIn.getBlockState(bedLocation).getBlock();
      if (block != Blocks.bed) {
         if (!forceSpawn) {
            return null;
         }

         boolean canSpawnInBlock = block.canSpawnInBlock();
         boolean canSpawnInBlock2 = worldIn.getBlockState(bedLocation.up()).getBlock().canSpawnInBlock();
         return canSpawnInBlock && canSpawnInBlock2 ? bedLocation : null;
      } else {
         return BlockBed.getSafeExitLocation(worldIn, bedLocation, 0);
      }
   }

   public float getBedOrientationInDegrees() {
      if (this.playerLocation != null) {
         EnumFacing value = this.worldObj.getBlockState(this.playerLocation).getValue(BlockDirectional.FACING);
         switch (value) {
            case SOUTH:
               return 90.0F;
            case NORTH:
               return 270.0F;
            case WEST:
               return 0.0F;
            case EAST:
               return 180.0F;
         }
      }

      return 0.0F;
   }

   @Override
   public boolean isPlayerSleeping() {
      return this.sleeping;
   }

   public boolean isPlayerFullyAsleep() {
      return this.sleeping && this.sleepTimer >= 100;
   }

   public int getSleepTimer() {
      return this.sleepTimer;
   }

   public void addChatComponentMessage(IChatComponent chatComponent) {
   }

   public BlockPos getBedLocation() {
      return this.spawnChunk;
   }

   public boolean isSpawnForced() {
      return this.spawnForced;
   }

   public void setSpawnPoint(BlockPos pos, boolean forced) {
      if (pos != null) {
         this.spawnChunk = pos;
         this.spawnForced = forced;
      } else {
         this.spawnChunk = null;
         this.spawnForced = false;
      }
   }

   public void triggerAchievement(StatBase achievementIn) {
      this.addStat(achievementIn, 1);
   }

   public void addStat(StatBase stat, int amount) {
   }

   public void handleStatBase(StatBase statBase) {
   }

   @Override
   public void jump() {
      super.jump();
      this.triggerAchievement(StatList.jumpStat);
      if (this.isSprinting()) {
         this.addExhaustion(0.8F);
      } else {
         this.addExhaustion(0.2F);
      }
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      double posX = this.posX;
      double posY = this.posY;
      double posZ = this.posZ;
      if (this.capabilities.isFlying && this.ridingEntity == null) {
         double motionY = this.motionY;
         float jumpMovementFactor = this.jumpMovementFactor;
         this.jumpMovementFactor = this.capabilities.getFlySpeed() * (this.isSprinting() ? 2 : 1);
         super.moveEntityWithHeading(strafe, forward);
         this.motionY = motionY * 0.6;
         this.jumpMovementFactor = jumpMovementFactor;
      } else {
         super.moveEntityWithHeading(strafe, forward);
      }

      this.addMovementStat(this.posX - posX, this.posY - posY, this.posZ - posZ);
   }

   @Override
   public float getAIMoveSpeed() {
      return (float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
   }

   public void addMovementStat(double d, double d2, double d3) {
      if (this.ridingEntity == null) {
         if (this.isInsideOfMaterial(Material.water)) {
            int round = Math.round(MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3) * 100.0F);
            if (round > 0) {
               this.addStat(StatList.distanceDoveStat, round);
               this.addExhaustion(0.015F * round * 0.01F);
            }
         } else if (this.isInWater()) {
            int round = Math.round(MathHelper.sqrt_double(d * d + d3 * d3) * 100.0F);
            if (round > 0) {
               this.addStat(StatList.distanceSwumStat, round);
               this.addExhaustion(0.015F * round * 0.01F);
            }
         } else if (this.isOnLadder()) {
            if (d2 > 0.0) {
               this.addStat(StatList.distanceClimbedStat, (int)Math.round(d2 * 100.0));
            }
         } else if (this.onGround) {
            int round = Math.round(MathHelper.sqrt_double(d * d + d3 * d3) * 100.0F);
            if (round > 0) {
               this.addStat(StatList.distanceWalkedStat, round);
               if (this.isSprinting()) {
                  this.addStat(StatList.distanceSprintedStat, round);
                  this.addExhaustion(0.099999994F * round * 0.01F);
               } else {
                  if (this.isSneaking()) {
                     this.addStat(StatList.distanceCrouchedStat, round);
                  }

                  this.addExhaustion(0.01F * round * 0.01F);
               }
            }
         } else {
            int round = Math.round(MathHelper.sqrt_double(d * d + d3 * d3) * 100.0F);
            if (round > 25) {
               this.addStat(StatList.distanceFlownStat, round);
            }
         }
      }
   }

   public void addMountedMovementStat(double d, double d2, double d3) {
      if (this.ridingEntity != null) {
         int round = Math.round(MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3) * 100.0F);
         if (round > 0) {
            if (this.ridingEntity instanceof EntityMinecart) {
               this.addStat(StatList.distanceByMinecartStat, round);
               if (this.startMinecartRidingCoordinate == null) {
                  this.startMinecartRidingCoordinate = new BlockPos(this);
               } else if (this.startMinecartRidingCoordinate
                     .distanceSq(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                  >= 1000000.0) {
                  this.triggerAchievement(AchievementList.onARail);
               }
            } else if (this.ridingEntity instanceof EntityBoat) {
               this.addStat(StatList.distanceByBoatStat, round);
            } else if (this.ridingEntity instanceof EntityPig) {
               this.addStat(StatList.distanceByPigStat, round);
            } else if (this.ridingEntity instanceof EntityHorse) {
               this.addStat(StatList.distanceByHorseStat, round);
            }
         }
      }
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      if (!this.capabilities.allowFlying) {
         if (distance >= 2.0F) {
            this.addStat(StatList.distanceFallenStat, (int)Math.round(distance * 100.0));
         }

         super.fall(distance, damageMultiplier);
      }
   }

   @Override
   public void resetHeight() {
      if (!this.isSpectator()) {
         super.resetHeight();
      }
   }

   @Override
   public String getFallSoundString(int damageValue) {
      return damageValue > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
   }

   @Override
   public void onKillEntity(EntityLivingBase entityLivingIn) {
      if (entityLivingIn instanceof IMob) {
         this.triggerAchievement(AchievementList.killEnemy);
      }

      EntityEggInfo entityEggInfo = EntityList.entityEggs.get(EntityList.getEntityID(entityLivingIn));
      if (entityEggInfo != null) {
         this.triggerAchievement(entityEggInfo.killEntityStat);
      }
   }

   @Override
   public void setInWeb() {
      if (!this.capabilities.isFlying) {
         super.setInWeb();
      }
   }

   @Override
   public ItemStack getCurrentArmor(int slotIn) {
      return this.inventory.armorItemInSlot(slotIn);
   }

   public void addExperience(int amount) {
      this.addScore(amount);
      int n = Integer.MAX_VALUE - this.experienceTotal;
      if (amount > n) {
         amount = n;
      }

      this.experience = this.experience + (float)amount / this.xpBarCap();

      for (this.experienceTotal += amount; this.experience >= 1.0F; this.experience = this.experience / this.xpBarCap()) {
         this.experience = (this.experience - 1.0F) * this.xpBarCap();
         this.addExperienceLevel(1);
      }
   }

   public int getXPSeed() {
      return this.xpSeed;
   }

   public void removeExperienceLevel(int levels) {
      this.experienceLevel -= levels;
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experience = 0.0F;
         this.experienceTotal = 0;
      }

      this.xpSeed = this.rand.nextInt();
   }

   public void addExperienceLevel(int levels) {
      this.experienceLevel += levels;
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experience = 0.0F;
         this.experienceTotal = 0;
      }

      if (levels > 0 && this.experienceLevel % 5 == 0 && this.lastXPSound < this.ticksExisted - 100.0F) {
         float f = this.experienceLevel > 30 ? 1.0F : this.experienceLevel / 30.0F;
         this.worldObj.playSoundAtEntity(this, "random.levelup", f * 0.75F, 1.0F);
         this.lastXPSound = this.ticksExisted;
      }
   }

   public int xpBarCap() {
      return this.experienceLevel >= 30
         ? 112 + (this.experienceLevel - 30) * 9
         : (this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2);
   }

   public void addExhaustion(float f) {
      if (!this.capabilities.disableDamage && !this.worldObj.isRemote) {
         this.foodStats.addExhaustion(f);
      }
   }

   public FoodStats getFoodStats() {
      return this.foodStats;
   }

   public boolean canEat(boolean ignoreHunger) {
      return (ignoreHunger || this.foodStats.needFood()) && !this.capabilities.disableDamage;
   }

   public boolean shouldHeal() {
      return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
   }

   public void setItemInUse(ItemStack stack, int duration) {
      if (stack != this.itemInUse) {
         this.itemInUse = stack;
         this.itemInUseCount = duration;
         if (!this.worldObj.isRemote) {
            this.setEating(true);
         }
      }
   }

   public boolean isAllowEdit() {
      return this.capabilities.allowEdit;
   }

   public boolean canPlayerEdit(BlockPos blockPos, EnumFacing enumFacing, ItemStack itemStack) {
      if (this.capabilities.allowEdit) {
         return true;
      }

      if (itemStack == null) {
         return false;
      }

      BlockPos offset = blockPos.offset(enumFacing.getOpposite());
      Block block = this.worldObj.getBlockState(offset).getBlock();
      return itemStack.canPlaceOn(block) || itemStack.canEditBlocks();
   }

   @Override
   public int getExperiencePoints(EntityPlayer player) {
      if (this.worldObj.getGameRules().getBoolean("keepInventory")) {
         return 0;
      }

      int n = this.experienceLevel * 7;
      return n > 100 ? 100 : n;
   }

   @Override
   public boolean isPlayer() {
      return true;
   }

   @Override
   public boolean getAlwaysRenderNameTagForRender() {
      return true;
   }

   public void clonePlayer(EntityPlayer oldPlayer, boolean respawnFromEnd) {
      if (respawnFromEnd) {
         this.inventory.copyInventory(oldPlayer.inventory);
         this.setHealth(oldPlayer.getHealth());
         this.foodStats = oldPlayer.foodStats;
         this.experienceLevel = oldPlayer.experienceLevel;
         this.experienceTotal = oldPlayer.experienceTotal;
         this.experience = oldPlayer.experience;
         this.setScore(oldPlayer.getScore());
         this.lastPortalPos = oldPlayer.lastPortalPos;
         this.lastPortalVec = oldPlayer.lastPortalVec;
         this.teleportDirection = oldPlayer.teleportDirection;
      } else if (this.worldObj.getGameRules().getBoolean("keepInventory")) {
         this.inventory.copyInventory(oldPlayer.inventory);
         this.experienceLevel = oldPlayer.experienceLevel;
         this.experienceTotal = oldPlayer.experienceTotal;
         this.experience = oldPlayer.experience;
         this.setScore(oldPlayer.getScore());
      }

      this.xpSeed = oldPlayer.xpSeed;
      this.theInventoryEnderChest = oldPlayer.theInventoryEnderChest;
      this.getDataWatcher().updateObject(10, oldPlayer.getDataWatcher().getWatchableObjectByte(10));
   }

   @Override
   public boolean canTriggerWalking() {
      return !this.capabilities.isFlying;
   }

   public void setGameType(GameType gameType) {
   }

   @Override
   public String getName() {
      return this.gameProfile.getName();
   }

   public InventoryEnderChest getInventoryEnderChest() {
      return this.theInventoryEnderChest;
   }

   @Override
   public ItemStack getEquipmentInSlot(int slotIn) {
      return slotIn == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[slotIn - 1];
   }

   @Override
   public ItemStack getHeldItem() {
      return this.inventory.getCurrentItem();
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      this.inventory.armorInventory[slotIn] = stack;
   }

   @Override
   public boolean isInvisibleToPlayer(EntityPlayer player) {
      if (!this.isInvisible()) {
         return false;
      }

      if (player.isSpectator()) {
         return false;
      }

      Team team = this.getTeam();
      return team == null || player == null || player.getTeam() != team || !team.getSeeFriendlyInvisiblesEnabled();
   }

   public abstract boolean isSpectator();

   @Override
   public ItemStack[] getInventory() {
      return this.inventory.armorInventory;
   }

   @Override
   public boolean isPushedByWater() {
      return !this.capabilities.isFlying;
   }

   public Scoreboard getWorldScoreboard() {
      return this.worldObj.getScoreboard();
   }

   @Override
   public Team getTeam() {
      return this.getWorldScoreboard().getPlayersTeam(this.getName());
   }

   @Override
   public IChatComponent getDisplayName() {
      ChatComponentText chatComponentText = new ChatComponentText(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getName()));
      chatComponentText.getChatStyle().setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/msg " + this.getName() + " "));
      chatComponentText.getChatStyle().setChatHoverEvent(this.getHoverEvent());
      chatComponentText.getChatStyle().setInsertion(this.getName());
      return chatComponentText;
   }

   @Override
   public float getEyeHeight() {
      float f = 1.62F;
      if (this.isPlayerSleeping()) {
         f = 0.2F;
      }

      if (this.isSneaking()) {
         f -= 0.08F;
      }

      return f;
   }

   @Override
   public void setAbsorptionAmount(float amount) {
      if (amount < 0.0F) {
         amount = 0.0F;
      }

      this.getDataWatcher().updateObject(17, amount);
   }

   @Override
   public float getAbsorptionAmount() {
      return this.getDataWatcher().getWatchableObjectFloat(17);
   }

   public static UUID getUUID(GameProfile profile) {
      UUID id = profile.getId();
      if (id == null) {
         id = getOfflineUUID(profile.getName());
      }

      return id;
   }

   public static UUID getOfflineUUID(String username) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));
   }

   public boolean canOpen(LockCode code) {
      if (code.isEmpty()) {
         return true;
      }

      ItemStack currentEquippedItem = this.getCurrentEquippedItem();
      return currentEquippedItem != null && currentEquippedItem.hasDisplayName() ? currentEquippedItem.getDisplayName().equals(code.getLock()) : false;
   }

   public boolean isWearing(EnumPlayerModelParts enumPlayerModelParts) {
      return (this.getDataWatcher().getWatchableObjectByte(10) & enumPlayerModelParts.getPartMask()) == enumPlayerModelParts.getPartMask();
   }

   @Override
   public boolean sendCommandFeedback() {
      return MinecraftServer.getServer().worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      if (inventorySlot >= 0 && inventorySlot < this.inventory.mainInventory.length) {
         this.inventory.setInventorySlotContents(inventorySlot, itemStackIn);
         return true;
      }

      int n = inventorySlot - 100;
      if (n >= 0 && n < this.inventory.armorInventory.length) {
         int n2 = n + 1;
         if (itemStackIn != null && itemStackIn.getItem() != null) {
            if (itemStackIn.getItem() instanceof ItemArmor) {
               if (EntityLiving.getArmorPosition(itemStackIn) != n2) {
                  return false;
               }
            } else if (n2 != 4 || itemStackIn.getItem() != Items.skull && !(itemStackIn.getItem() instanceof ItemBlock)) {
               return false;
            }
         }

         this.inventory.setInventorySlotContents(n + this.inventory.mainInventory.length, itemStackIn);
         return true;
      } else {
         int n2 = inventorySlot - 200;
         if (n2 >= 0 && n2 < this.theInventoryEnderChest.getSizeInventory()) {
            this.theInventoryEnderChest.setInventorySlotContents(n2, itemStackIn);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean hasReducedDebug() {
      return this.hasReducedDebug;
   }

   public void setReducedDebug(boolean reducedDebug) {
      this.hasReducedDebug = reducedDebug;
   }

   public boolean check() {
      return this.cosmetic2 != null;
   }

   public void setCosmetic(Cosmetic cosmetic) {
      this.cosmetic = cosmetic;
   }

   public Cosmetic getCosmetic() {
      return this.cosmetic;
   }

   public boolean check2() {
      return this.cosmetic3 != null;
   }

   public boolean check3() {
      return this.cosmetic != null;
   }

   public void respawnPlayer() {
   }

   public Cosmetic getCosmetic3() {
      return this.cosmetic3;
   }

   public void sendPlayerAbilities() {
   }

   public Cosmetic getCosmetic2() {
      return this.cosmetic2;
   }

   public void setCosmetic2(Cosmetic cosmetic) {
      this.cosmetic2 = cosmetic;
   }

   public void setCosmetic3(Cosmetic cosmetic) {
      this.cosmetic3 = cosmetic;
   }

   public enum EnumChatVisibility {
      FULL(0, "options.chat.visibility.full"),
      SYSTEM(1, "options.chat.visibility.system"),
      HIDDEN(2, "options.chat.visibility.hidden");

      public int chatVisibility;
      public String resourceKey;
      public static EntityPlayer.EnumChatVisibility[] ID_LOOKUP = new EntityPlayer.EnumChatVisibility[values().length];

      EnumChatVisibility(int id, String resourceKey) {
         this.chatVisibility = id;
         this.resourceKey = resourceKey;
      }

      public int getChatVisibility() {
         return this.chatVisibility;
      }

      public static EntityPlayer.EnumChatVisibility getEnumChatVisibility(int id) {
         return ID_LOOKUP[id % ID_LOOKUP.length];
      }

      public String getResourceKey() {
         return this.resourceKey;
      }

      static {
         for (EntityPlayer.EnumChatVisibility enumChatVisibility : values()) {
            ID_LOOKUP[enumChatVisibility.chatVisibility] = enumChatVisibility;
         }
      }
   }

   public enum EnumStatus {
      OK,
      NOT_POSSIBLE_HERE,
      NOT_POSSIBLE_NOW,
      TOO_FAR_AWAY,
      OTHER_PROBLEM,
      NOT_SAFE;
   }
}
