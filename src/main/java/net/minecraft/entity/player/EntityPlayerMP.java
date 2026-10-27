package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S42PacketCombatEvent.Event;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonSerializableSet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {
   public static Logger logger = LogManager.getLogger();
   public String translator = "en_US";
   public NetHandlerPlayServer playerNetServerHandler;
   public List<ChunkCoordIntPair> loadedChunks = Lists.newLinkedList();
   public List<Integer> destroyedItemsNetCache = Lists.newLinkedList();
   public double managedPosX;
   public double managedPosZ;
   public float combinedHealth = Float.MIN_VALUE;
   public float lastHealth = -1.0E8F;
   public int lastFoodLevel = -99999999;
   public boolean wasHungry = true;
   public int lastExperience = -99999999;
   public int respawnInvulnerabilityTicks = 60;
   public boolean chatColours = true;
   public long playerLastActiveTime = System.currentTimeMillis();
   public Entity spectatingEntity = null;
   public EntityPlayer.EnumChatVisibility chatVisibility;
   public ItemInWorldManager theItemInWorldManager;
   public MinecraftServer mcServer;
   public StatisticsFile statsFile;
   public int currentWindowId;
   public boolean isChangingQuantityOnly;
   public int ping;
   public boolean playerConqueredTheEnd;

   public EntityPlayerMP(MinecraftServer server, WorldServer worldIn, GameProfile profile, ItemInWorldManager interactionManager) {
      super(worldIn, profile);
      interactionManager.thisPlayerMP = this;
      this.theItemInWorldManager = interactionManager;
      BlockPos spawnPoint = worldIn.getSpawnPoint();
      if (!worldIn.provider.getHasNoSky() && worldIn.getWorldInfo().getGameType() != GameType.ADVENTURE) {
         int max = Math.max(5, server.getSpawnProtectionSize() - 6);
         int n = MathHelper.floor_double(worldIn.getWorldBorder().getClosestDistance(spawnPoint.getX(), spawnPoint.getZ()));
         if (n < max) {
            max = n;
         }

         if (n <= 1) {
            max = 1;
         }

         spawnPoint = worldIn.getTopSolidOrLiquidBlock(spawnPoint.add(this.rand.nextInt(max * 2) - max, 0, this.rand.nextInt(max * 2) - max));
      }

      this.mcServer = server;
      this.statsFile = server.getConfigurationManager().getPlayerStatsFile(this);
      this.stepHeight = 0.0F;
      this.moveToBlockPosAndAngles(spawnPoint, 0.0F, 0.0F);

      while (!worldIn.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && this.posY < 255.0) {
         this.setPosition(this.posX, this.posY + 1.0, this.posZ);
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("playerGameType", 99)) {
         if (MinecraftServer.getServer().getForceGamemode()) {
            this.theItemInWorldManager.setGameType(MinecraftServer.getServer().getGameType());
         } else {
            this.theItemInWorldManager.setGameType(GameType.getByID(tagCompund.getInteger("playerGameType")));
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("playerGameType", this.theItemInWorldManager.getGameType().getID());
   }

   @Override
   public void addExperienceLevel(int levels) {
      super.addExperienceLevel(levels);
      this.lastExperience = -1;
   }

   @Override
   public void removeExperienceLevel(int levels) {
      super.removeExperienceLevel(levels);
      this.lastExperience = -1;
   }

   public void addSelfToInternalCraftingInventory() {
      this.openContainer.onCraftGuiOpened(this);
   }

   @Override
   public void sendEnterCombat() {
      super.sendEnterCombat();
      this.playerNetServerHandler.sendPacket(new S42PacketCombatEvent(this.getCombatTracker(), Event.ENTER_COMBAT));
   }

   @Override
   public void sendEndCombat() {
      super.sendEndCombat();
      this.playerNetServerHandler.sendPacket(new S42PacketCombatEvent(this.getCombatTracker(), Event.END_COMBAT));
   }

   @Override
   public void onUpdate() {
      this.theItemInWorldManager.updateBlockRemoving();
      this.respawnInvulnerabilityTicks--;
      if (this.hurtResistantTime > 0) {
         this.hurtResistantTime--;
      }

      this.openContainer.detectAndSendChanges();
      if (!this.worldObj.isRemote && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.inventoryContainer;
      }

      while (!this.destroyedItemsNetCache.isEmpty()) {
         int min = Math.min(this.destroyedItemsNetCache.size(), Integer.MAX_VALUE);
         int[] ints = new int[min];
         Iterator iterator = this.destroyedItemsNetCache.iterator();
         int n = 0;

         while (iterator.hasNext() && n < min) {
            ints[n++] = (Integer)iterator.next();
            iterator.remove();
         }

         this.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(ints));
      }

      if (!this.loadedChunks.isEmpty()) {
         ArrayList newArrayList = Lists.newArrayList();
         Iterator iterator = this.loadedChunks.iterator();
         ArrayList newArrayList2 = Lists.newArrayList();

         while (iterator.hasNext() && newArrayList.size() < 10) {
            ChunkCoordIntPair chunkCoordIntPair = (ChunkCoordIntPair)iterator.next();
            if (chunkCoordIntPair != null) {
               if (this.worldObj.isBlockLoaded(new BlockPos(chunkCoordIntPair.chunkXPos << 4, 0, chunkCoordIntPair.chunkZPos << 4))) {
                  Chunk chunkFromChunkCoords = this.worldObj.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
                  if (chunkFromChunkCoords.isPopulated()) {
                     newArrayList.add(chunkFromChunkCoords);
                     newArrayList2.addAll(
                        ((WorldServer)this.worldObj)
                           .getTileEntitiesIn(chunkCoordIntPair.chunkXPos * 16, 0, chunkCoordIntPair.chunkZPos * 16, chunkCoordIntPair.chunkXPos * 16 + 16, 256, chunkCoordIntPair.chunkZPos * 16 + 16)
                     );
                     iterator.remove();
                  }
               }
            } else {
               iterator.remove();
            }
         }

         if (!newArrayList.isEmpty()) {
            if (newArrayList.size() == 1) {
               this.playerNetServerHandler.sendPacket(new S21PacketChunkData((Chunk)newArrayList.get(0), true, 65535));
            } else {
               this.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(newArrayList));
            }

            for (TileEntity tileEntity : (Iterable<TileEntity>)newArrayList2) {
               this.sendTileEntityUpdate(tileEntity);
            }

            for (Chunk chunk : (Iterable<Chunk>)newArrayList) {
               this.getServerForPlayer().getEntityTracker().handleEntityPlayerMP(this, chunk);
            }
         }
      }

      Entity spectatingEntity = this.getSpectatingEntity();
      if (spectatingEntity != this) {
         if (!spectatingEntity.isEntityAlive()) {
            this.setSpectatingEntity(this);
         } else {
            this.setPositionAndRotation(spectatingEntity.posX, spectatingEntity.posY, spectatingEntity.posZ, spectatingEntity.rotationYaw, spectatingEntity.rotationPitch);
            this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this);
            if (this.isSneaking()) {
               this.setSpectatingEntity(this);
            }
         }
      }
   }

   public void onUpdateEntity() {
      try {
         super.onUpdate();

         for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
            ItemStack stackInSlot = this.inventory.getStackInSlot(i);
            if (stackInSlot != null && stackInSlot.getItem().isMap()) {
               Packet createMapDataPacket = ((ItemMapBase)stackInSlot.getItem()).createMapDataPacket(stackInSlot, this.worldObj, this);
               if (createMapDataPacket != null) {
                  this.playerNetServerHandler.sendPacket(createMapDataPacket);
               }
            }
         }

         if (this.getHealth() != this.lastHealth
            || this.lastFoodLevel != this.foodStats.getFoodLevel()
            || this.foodStats.getSaturationLevel() == 0.0F != this.wasHungry) {
            this.playerNetServerHandler
               .sendPacket(new S06PacketUpdateHealth(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
            this.lastHealth = this.getHealth();
            this.lastFoodLevel = this.foodStats.getFoodLevel();
            this.wasHungry = this.foodStats.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.combinedHealth) {
            this.combinedHealth = this.getHealth() + this.getAbsorptionAmount();

            for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.health)) {
               this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective).handleList(Arrays.asList(this));
            }
         }

         if (this.experienceTotal != this.lastExperience) {
            this.lastExperience = this.experienceTotal;
            this.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(this.experience, this.experienceTotal, this.experienceLevel));
         }

         if (this.ticksExisted % 20 * 5 == 0 && !this.getStatFile().hasAchievementUnlocked(AchievementList.exploreAllBiomes)) {
            this.updateBiomesExplored();
         }
      } catch (Throwable e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Ticking player");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("Player being ticked");
         this.addEntityCrashInfo(makeCategory);
         throw new ReportedException(makeCrashReport);
      }
   }

   public void updateBiomesExplored() {
      BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(new BlockPos(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ)));
      String biomeName = biomeGenForCoords.biomeName;
      JsonSerializableSet stat = this.getStatFile().getStat(AchievementList.exploreAllBiomes);
      if (stat == null) {
         stat = this.getStatFile().setStat(AchievementList.exploreAllBiomes, new JsonSerializableSet());
      }

      stat.add(biomeName);
      if (this.getStatFile().canUnlockAchievement(AchievementList.exploreAllBiomes) && stat.size() >= BiomeGenBase.explorationBiomesList.size()) {
         HashSet newHashSet = Sets.newHashSet(BiomeGenBase.explorationBiomesList);

         for (String str : stat) {
            Iterator iterator = newHashSet.iterator();

            while (iterator.hasNext()) {
               BiomeGenBase biomeGenBase = (BiomeGenBase)iterator.next();
               if (biomeGenBase.biomeName.equals(str)) {
                  iterator.remove();
               }
            }

            if (newHashSet.isEmpty()) {
               break;
            }
         }

         if (newHashSet.isEmpty()) {
            this.triggerAchievement(AchievementList.exploreAllBiomes);
         }
      }
   }

   @Override
   public void onDeath(DamageSource cause) {
      if (this.worldObj.getGameRules().getBoolean("showDeathMessages")) {
         Team team = this.getTeam();
         if (team == null || team.getDeathMessageVisibility() == EnumVisible.ALWAYS) {
            this.mcServer.getConfigurationManager().sendChatMsg(this.getCombatTracker().getDeathMessage());
         } else if (team.getDeathMessageVisibility() == EnumVisible.HIDE_FOR_OTHER_TEAMS) {
            this.mcServer.getConfigurationManager().sendMessageToAllTeamMembers(this, this.getCombatTracker().getDeathMessage());
         } else if (team.getDeathMessageVisibility() == EnumVisible.HIDE_FOR_OWN_TEAM) {
            this.mcServer.getConfigurationManager().sendMessageToTeamOrEvryPlayer(this, this.getCombatTracker().getDeathMessage());
         }
      }

      if (!this.worldObj.getGameRules().getBoolean("keepInventory")) {
         this.inventory.dropAllItems();
      }

      for (ScoreObjective scoreObjective : this.worldObj.getScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.deathCount)) {
         Score valueFromObjective = this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective);
         valueFromObjective.incrementScore();
      }

      EntityLivingBase attackingEntity = this.getAttackingEntity();
      if (attackingEntity != null) {
         EntityEggInfo entityEggInfo = EntityList.entityEggs.get(EntityList.getEntityID(attackingEntity));
         if (entityEggInfo != null) {
            this.triggerAchievement(entityEggInfo.killedByEntityStat);
         }

         attackingEntity.addToPlayerScore(this, this.scoreValue);
      }

      this.triggerAchievement(StatList.deathsStat);
      this.handleStatBase(StatList.timeSinceDeathStat);
      this.getCombatTracker().reset();
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      boolean flag = this.mcServer.isDedicatedServer() && this.canPlayersAttack() && "fall".equals(source.damageType);
      if (!flag && this.respawnInvulnerabilityTicks > 0 && source != DamageSource.outOfWorld) {
         return false;
      }

      if (source instanceof EntityDamageSource) {
         Entity entity = source.getEntity();
         if (entity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entity)) {
            return false;
         }

         if (entity instanceof EntityArrow) {
            EntityArrow entityArrow = (EntityArrow)entity;
            if (entityArrow.shootingEntity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entityArrow.shootingEntity)) {
               return false;
            }
         }
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public boolean canAttackPlayer(EntityPlayer other) {
      return !this.canPlayersAttack() ? false : super.canAttackPlayer(other);
   }

   public boolean canPlayersAttack() {
      return this.mcServer.isPVPEnabled();
   }

   @Override
   public void travelToDimension(int dimensionId) {
      if (this.dimension == 1 && dimensionId == 1) {
         this.triggerAchievement(AchievementList.theEnd2);
         this.worldObj.removeEntity(this);
         this.playerConqueredTheEnd = true;
         this.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(4, 0.0F));
      } else {
         if (this.dimension == 0 && dimensionId == 1) {
            this.triggerAchievement(AchievementList.theEnd);
            BlockPos spawnCoordinate = this.mcServer.worldServerForDimension(dimensionId).getSpawnCoordinate();
            if (spawnCoordinate != null) {
               this.playerNetServerHandler.setPlayerLocation(spawnCoordinate.getX(), spawnCoordinate.getY(), spawnCoordinate.getZ(), 0.0F, 0.0F);
            }

            dimensionId = 1;
         } else {
            this.triggerAchievement(AchievementList.portal);
         }

         this.mcServer.getConfigurationManager().transferPlayerToDimension(this, dimensionId);
         this.lastExperience = -1;
         this.lastHealth = -1.0F;
         this.lastFoodLevel = -1;
      }
   }

   @Override
   public boolean isSpectatedByPlayer(EntityPlayerMP player) {
      return player.isSpectator() ? this.getSpectatingEntity() == this : (this.isSpectator() ? false : super.isSpectatedByPlayer(player));
   }

   public void sendTileEntityUpdate(TileEntity tileEntity) {
      if (tileEntity != null) {
         Packet descriptionPacket = tileEntity.getDescriptionPacket();
         if (descriptionPacket != null) {
            this.playerNetServerHandler.sendPacket(descriptionPacket);
         }
      }
   }

   @Override
   public void onItemPickup(Entity entity, int n) {
      super.onItemPickup(entity, n);
      this.openContainer.detectAndSendChanges();
   }

   @Override
   public EntityPlayer.EnumStatus trySleep(BlockPos bedLocation) {
      EntityPlayer.EnumStatus trySleep = super.trySleep(bedLocation);
      if (trySleep == EntityPlayer.EnumStatus.OK) {
         S0APacketUseBed s0APacketUseBed = new S0APacketUseBed(this, bedLocation);
         this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntity(this, s0APacketUseBed);
         this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.playerNetServerHandler.sendPacket(s0APacketUseBed);
      }

      return trySleep;
   }

   @Override
   public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
      if (this.isPlayerSleeping()) {
         this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntities(this, new S0BPacketAnimation(this, 2));
      }

      super.wakeUpPlayer(immediately, updateWorldFlag, setSpawn);
      if (this.playerNetServerHandler != null) {
         this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }
   }

   @Override
   public void mountEntity(Entity entityIn) {
      Entity ridingEntity = this.ridingEntity;
      super.mountEntity(entityIn);
      if (entityIn != ridingEntity) {
         this.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this, this.ridingEntity));
         this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }
   }

   @Override
   public void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
   }

   public void handleFalling(double d, boolean flag) {
      int n = MathHelper.floor_double(this.posX);
      int n2 = MathHelper.floor_double(this.posY - 0.2F);
      int n3 = MathHelper.floor_double(this.posZ);
      BlockPos blockPos = new BlockPos(n, n2, n3);
      Block block = this.worldObj.getBlockState(blockPos).getBlock();
      if (block.getMaterial() == Material.air) {
         Block block2 = this.worldObj.getBlockState(blockPos.down()).getBlock();
         if (block2 instanceof BlockFence || block2 instanceof BlockWall || block2 instanceof BlockFenceGate) {
            blockPos = blockPos.down();
            block = this.worldObj.getBlockState(blockPos).getBlock();
         }
      }

      super.updateFallState(d, flag, block, blockPos);
   }

   @Override
   public void openEditSign(TileEntitySign signTile) {
      signTile.setPlayer(this);
      this.playerNetServerHandler.sendPacket(new S36PacketSignEditorOpen(signTile.getPos()));
   }

   public void getNextWindowId() {
      this.currentWindowId = this.currentWindowId % 100 + 1;
   }

   @Override
   public void displayGui(IInteractionObject guiOwner) {
      this.getNextWindowId();
      this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, guiOwner.getGuiID(), guiOwner.getDisplayName()));
      this.openContainer = guiOwner.createContainer(this.inventory, this);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
   }

   @Override
   public void displayGUIChest(IInventory chestInventory) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      if (chestInventory instanceof ILockableContainer) {
         ILockableContainer chestInventory2 = (ILockableContainer)chestInventory;
         if (chestInventory2.isLocked() && !this.canOpen(chestInventory2.getLockCode()) && !this.isSpectator()) {
            this.playerNetServerHandler
               .sendPacket(new S02PacketChat(new ChatComponentTranslation("container.isLocked", chestInventory.getDisplayName()), (byte)2));
            this.playerNetServerHandler.sendPacket(new S29PacketSoundEffect("random.door_close", this.posX, this.posY, this.posZ, 1.0F, 1.0F));
            return;
         }
      }

      this.getNextWindowId();
      if (chestInventory instanceof IInteractionObject) {
         this.playerNetServerHandler
            .sendPacket(
               new S2DPacketOpenWindow(
                  this.currentWindowId, ((IInteractionObject)chestInventory).getGuiID(), chestInventory.getDisplayName(), chestInventory.getSizeInventory()
               )
            );
         this.openContainer = ((IInteractionObject)chestInventory).createContainer(this.inventory, this);
      } else {
         this.playerNetServerHandler
            .sendPacket(
               new S2DPacketOpenWindow(this.currentWindowId, "minecraft:container", chestInventory.getDisplayName(), chestInventory.getSizeInventory())
            );
         this.openContainer = new ContainerChest(this.inventory, chestInventory, this);
      }

      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
   }

   @Override
   public void displayVillagerTradeGui(IMerchant villager) {
      this.getNextWindowId();
      this.openContainer = new ContainerMerchant(this.inventory, villager, this.worldObj);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
      InventoryMerchant merchantInventory = ((ContainerMerchant)this.openContainer).getMerchantInventory();
      IChatComponent displayName = villager.getDisplayName();
      this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, "minecraft:villager", displayName, merchantInventory.getSizeInventory()));
      MerchantRecipeList recipes = villager.getRecipes(this);
      if (recipes != null) {
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeInt(this.currentWindowId);
         recipes.writeToBuf(packetBuffer);
         this.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|TrList", packetBuffer));
      }
   }

   @Override
   public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      this.getNextWindowId();
      this.playerNetServerHandler
         .sendPacket(
            new S2DPacketOpenWindow(
               this.currentWindowId, "EntityHorse", horseInventory.getDisplayName(), horseInventory.getSizeInventory(), horse.getEntityId()
            )
         );
      this.openContainer = new ContainerHorseInventory(this.inventory, horseInventory, horse, this);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
   }

   @Override
   public void displayGUIBook(ItemStack bookStack) {
      Item item = bookStack.getItem();
      if (item == Items.written_book) {
         this.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|BOpen", new PacketBuffer(Unpooled.buffer())));
      }
   }

   @Override
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      if (!(containerToSend.getSlot(slotInd) instanceof SlotCrafting) && !this.isChangingQuantityOnly) {
         this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(containerToSend.windowId, slotInd, stack));
      }
   }

   public void sendContainerToPlayer(Container container) {
      this.updateCraftingInventory(container, container.getInventory());
   }

   @Override
   public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList) {
      this.playerNetServerHandler.sendPacket(new S30PacketWindowItems(containerToSend.windowId, itemsList));
      this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
   }

   @Override
   public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue) {
      this.playerNetServerHandler.sendPacket(new S31PacketWindowProperty(containerIn.windowId, varToUpdate, newValue));
   }

   @Override
   public void sendAllWindowProperties(Container container, IInventory inventory) {
      for (int i = 0; i < inventory.getFieldCount(); i++) {
         this.playerNetServerHandler.sendPacket(new S31PacketWindowProperty(container.windowId, i, inventory.getField(i)));
      }
   }

   @Override
   public void closeScreen() {
      this.playerNetServerHandler.sendPacket(new S2EPacketCloseWindow(this.openContainer.windowId));
      this.closeContainer();
   }

   public void updateHeldItem() {
      if (!this.isChangingQuantityOnly) {
         this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
      }
   }

   public void closeContainer() {
      this.openContainer.onContainerClosed(this);
      this.openContainer = this.inventoryContainer;
   }

   public void setEntityActionState(float f, float f2, boolean flag, boolean sneaking) {
      if (this.ridingEntity != null) {
         if (f >= -1.0F && f <= 1.0F) {
            this.moveStrafing = f;
         }

         if (f2 >= -1.0F && f2 <= 1.0F) {
            this.moveForward = f2;
         }

         this.isJumping = flag;
         this.setSneaking(sneaking);
      }
   }

   @Override
   public void addStat(StatBase stat, int amount) {
      if (stat != null) {
         this.statsFile.increaseStat(this, stat, amount);

         for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(stat.getCriteria())) {
            this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective).increseScore(amount);
         }

         if (this.statsFile.isFlag()) {
            this.statsFile.handleEntityPlayerMP(this);
         }
      }
   }

   @Override
   public void handleStatBase(StatBase statBase) {
      if (statBase != null) {
         this.statsFile.unlockAchievement(this, statBase, 0);

         for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(statBase.getCriteria())) {
            this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective).setScorePoints(0);
         }

         if (this.statsFile.isFlag()) {
            this.statsFile.handleEntityPlayerMP(this);
         }
      }
   }

   public void mountEntityAndWakeUp() {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.mountEntity(this);
      }

      if (this.sleeping) {
         this.wakeUpPlayer(true, false, false);
      }
   }

   public void setPlayerHealthUpdated() {
      this.lastHealth = -1.0E8F;
   }

   @Override
   public void addChatComponentMessage(IChatComponent chatComponent) {
      this.playerNetServerHandler.sendPacket(new S02PacketChat(chatComponent));
   }

   @Override
   public void onItemUseFinish() {
      this.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(this, (byte)9));
      super.onItemUseFinish();
   }

   @Override
   public void setItemInUse(ItemStack stack, int duration) {
      super.setItemInUse(stack, duration);
      if (stack != null && stack.getItem() != null && stack.getItem().getItemUseAction(stack) == EnumAction.EAT) {
         this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntities(this, new S0BPacketAnimation(this, 3));
      }
   }

   @Override
   public void clonePlayer(EntityPlayer oldPlayer, boolean respawnFromEnd) {
      super.clonePlayer(oldPlayer, respawnFromEnd);
      this.lastExperience = -1;
      this.lastHealth = -1.0F;
      this.lastFoodLevel = -1;
      this.destroyedItemsNetCache.addAll(((EntityPlayerMP)oldPlayer).destroyedItemsNetCache);
   }

   @Override
   public void onNewPotionEffect(PotionEffect id) {
      super.onNewPotionEffect(id);
      this.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.getEntityId(), id));
   }

   @Override
   public void onChangedPotionEffect(PotionEffect id, boolean flag) {
      super.onChangedPotionEffect(id, flag);
      this.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.getEntityId(), id));
   }

   @Override
   public void onFinishedPotionEffect(PotionEffect effect) {
      super.onFinishedPotionEffect(effect);
      this.playerNetServerHandler.sendPacket(new S1EPacketRemoveEntityEffect(this.getEntityId(), effect));
   }

   @Override
   public void setPositionAndUpdate(double x, double y, double z) {
      this.playerNetServerHandler.setPlayerLocation(x, y, z, this.rotationYaw, this.rotationPitch);
   }

   @Override
   public void onCriticalHit(Entity entityHit) {
      this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntities(this, new S0BPacketAnimation(entityHit, 4));
   }

   @Override
   public void onEnchantmentCritical(Entity entityHit) {
      this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntities(this, new S0BPacketAnimation(entityHit, 5));
   }

   @Override
   public void sendPlayerAbilities() {
      if (this.playerNetServerHandler != null) {
         this.playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(this.capabilities));
         this.updatePotionMetadata();
      }
   }

   public WorldServer getServerForPlayer() {
      return (WorldServer)this.worldObj;
   }

   @Override
   public void setGameType(GameType gameType) {
      this.theItemInWorldManager.setGameType(gameType);
      this.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(3, gameType.getID()));
      if (gameType == GameType.SPECTATOR) {
         this.mountEntity((Entity)null);
      } else {
         this.setSpectatingEntity(this);
      }

      this.sendPlayerAbilities();
      this.markPotionsDirty();
   }

   @Override
   public boolean isSpectator() {
      return this.theItemInWorldManager.getGameType() == GameType.SPECTATOR;
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      this.playerNetServerHandler.sendPacket(new S02PacketChat(component));
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      if ("seed".equals(commandName) && !this.mcServer.isDedicatedServer()) {
         return true;
      }

      if (!"tell".equals(commandName) && !"help".equals(commandName) && !"me".equals(commandName) && !"trigger".equals(commandName)) {
         if (this.mcServer.getConfigurationManager().canSendCommands(this.getGameProfile())) {
            UserListOpsEntry entry = this.mcServer.getConfigurationManager().getOppedPlayers().getEntry(this.getGameProfile());
            return entry != null ? entry.getPermissionLevel() >= permLevel : this.mcServer.getOpPermissionLevel() >= permLevel;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public String getPlayerIP() {
      String str = this.playerNetServerHandler.netManager.getRemoteAddress().toString();
      str = str.substring(str.indexOf("/") + 1);
      return str.substring(0, str.indexOf(":"));
   }

   public void handleClientSettings(C15PacketClientSettings packetIn) {
      this.translator = packetIn.getLang();
      this.chatVisibility = packetIn.getChatVisibility();
      this.chatColours = packetIn.isColorsEnabled();
      this.getDataWatcher().updateObject(10, (byte)packetIn.getModelPartFlags());
   }

   public EntityPlayer.EnumChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public void loadResourcePack(String url, String hash) {
      this.playerNetServerHandler.sendPacket(new S48PacketResourcePackSend(url, hash));
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX, this.posY + 0.5, this.posZ);
   }

   public void markPlayerActive() {
      this.playerLastActiveTime = MinecraftServer.getCurrentTimeMillis();
   }

   public StatisticsFile getStatFile() {
      return this.statsFile;
   }

   public void removeEntity(Entity entity) {
      if (entity instanceof EntityPlayer) {
         this.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(entity.getEntityId()));
      } else {
         this.destroyedItemsNetCache.add(entity.getEntityId());
      }
   }

   @Override
   public void updatePotionMetadata() {
      if (this.isSpectator()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(true);
      } else {
         super.updatePotionMetadata();
      }

      this.getServerForPlayer().getEntityTracker().handleEntityPlayerMP(this);
   }

   public Entity getSpectatingEntity() {
      return this.spectatingEntity == null ? this : this.spectatingEntity;
   }

   public void setSpectatingEntity(Entity entityToSpectate) {
      Entity spectatingEntity = this.getSpectatingEntity();
      this.spectatingEntity = entityToSpectate == null ? this : entityToSpectate;
      if (spectatingEntity != this.spectatingEntity) {
         this.playerNetServerHandler.sendPacket(new S43PacketCamera(this.spectatingEntity));
         this.setPositionAndUpdate(this.spectatingEntity.posX, this.spectatingEntity.posY, this.spectatingEntity.posZ);
      }
   }

   @Override
   public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
      if (this.theItemInWorldManager.getGameType() == GameType.SPECTATOR) {
         this.setSpectatingEntity(targetEntity);
      } else {
         super.attackTargetEntityWithCurrentItem(targetEntity);
      }
   }

   public long getLastActiveTime() {
      return this.playerLastActiveTime;
   }

   public IChatComponent getTabListDisplayName() {
      return null;
   }
}
