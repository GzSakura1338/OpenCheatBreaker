package net.minecraft.entity.player;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.EnumAction;
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
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonSerializableSet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {
    public static Logger logger = LogManager.getLogger();
    public String translator = "en_US";
    public NetHandlerPlayServer playerNetServerHandler;
    public List loadedChunks = new LinkedList();
    public List destroyedItemsNetCache = new LinkedList();
    public double managedPosX;
    public double managedPosZ;
    public float syncedHealth = Float.MIN_VALUE;
    public float lastHealth = -1.0E8F;
    public int lastFoodLevel = -99999999;
    public boolean wasHungry = true;
    public int lastExperience = -99999999;
    public int spawnProtectionTicks = 60;
    public boolean chatColours = true;
    public long playerLastActiveTime = System.currentTimeMillis();
    public ItemInWorldManager theItemInWorldManager;
    public EnumChatVisibility chatVisibility;
    public MinecraftServer mcServer;
    public StatisticsFile statHandler;
    public int currentWindowId;
    public boolean isChangingQuantityOnly;
    public int ping;
    public boolean playerConqueredTheEnd;

    public EntityPlayerMP(MinecraftServer minecraftServer, WorldServer worldServer, GameProfile gameProfile, ItemInWorldManager itemInWorldManager) {
        super(worldServer, gameProfile);
        itemInWorldManager.thisPlayerMP = this;
        this.theItemInWorldManager = itemInWorldManager;
        ChunkCoordinates spawnPoint = worldServer.getSpawnPoint();
        int posX = spawnPoint.posX;
        int posZ = spawnPoint.posZ;
        int posY = spawnPoint.posY;
        if (!worldServer.provider.hasNoSky && worldServer.getWorldInfo().getGameType() != GameType.ADVENTURE) {
            int max = Math.max(5, minecraftServer.getSpawnProtectionSize() - 6);
            posX += this.rand.nextInt(max * 2) - max;
            posZ += this.rand.nextInt(max * 2) - max;
            posY = worldServer.getTopSolidOrLiquidBlock(posX, posZ);
        }

        this.mcServer = minecraftServer;
        this.statHandler = minecraftServer.getConfigurationManager().getPlayerStatsFile(this);
        this.stepHeight = 0.0F;
        this.yOffset = 0.0F;
        this.setLocationAndAngles(posX + 0.5, posY, posZ + 0.5, 0.0F, 0.0F);

        while (!worldServer.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) {
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
    public void addExperienceLevel(int n) {
        super.addExperienceLevel(n);
        this.lastExperience = -1;
    }

    public void addSelfToInternalCraftingInventory() {
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void resetHeight() {
        this.yOffset = 0.0F;
    }

    @Override
    public float getEyeHeight() {
        return 1.62F;
    }

    @Override
    public void onUpdate() {
        this.theItemInWorldManager.updateBlockRemoving();
        this.spawnProtectionTicks--;
        if (this.hurtResistantTime > 0) {
            this.hurtResistantTime--;
        }

        this.openContainer.detectAndSendChanges();
        if (!this.worldObj.isRemote && !this.openContainer.canInteractWith(this)) {
            this.closeScreen();
            this.openContainer = this.inventoryContainer;
        }

        while (!this.destroyedItemsNetCache.isEmpty()) {
            int min = Math.min(this.destroyedItemsNetCache.size(), 127);
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
            ArrayList list = new ArrayList();
            Iterator iterator = this.loadedChunks.iterator();
            ArrayList list2 = new ArrayList();

            while (iterator.hasNext() && list.size() < S26PacketMapChunkBulk.constantInt()) {
                ChunkCoordIntPair chunkCoordIntPair = (ChunkCoordIntPair)iterator.next();
                if (chunkCoordIntPair != null) {
                    if (this.worldObj.blockExists(chunkCoordIntPair.chunkXPos << 4, 0, chunkCoordIntPair.chunkZPos << 4)) {
                        Chunk chunkFromChunkCoords = this.worldObj.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
                        if (chunkFromChunkCoords.isPopulated()) {
                            list.add(chunkFromChunkCoords);
                            list2.addAll(
                                ((WorldServer)this.worldObj)
                                    .createArrayList(chunkCoordIntPair.chunkXPos * 16, 0, chunkCoordIntPair.chunkZPos * 16, chunkCoordIntPair.chunkXPos * 16 + 16, 256, chunkCoordIntPair.chunkZPos * 16 + 16)
                            );
                            iterator.remove();
                        }
                    }
                } else {
                    iterator.remove();
                }
            }

            if (!list.isEmpty()) {
                this.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(list));

                for (TileEntity tileEntity : (Iterable<TileEntity>)list2) {
                    this.updateBlockEntity(tileEntity);
                }

                for (Chunk chunk : (Iterable<Chunk>)list) {
                    this.getServerForPlayer().getEntityTracker().handleEntityPlayerMP(this, chunk);
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

            if (this.getHealth() + this.getAbsorptionAmount() != this.syncedHealth) {
                this.syncedHealth = this.getHealth() + this.getAbsorptionAmount();

                for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)this.getWorldScoreboard().getObjective(IScoreObjectiveCriteria.health)) {
                    this.getWorldScoreboard().getValueFromObjective(this.getCommandSenderName(), scoreObjective).handleList(Arrays.asList(this));
                }
            }

            if (this.experienceTotal != this.lastExperience) {
                this.lastExperience = this.experienceTotal;
                this.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(this.experience, this.experienceTotal, this.experienceLevel));
            }

            if (this.ticksExisted % 20 * 5 == 0 && !this.getStatFile().hasAchievementUnlocked(AchievementList.exploreAllBiomes)) {
                this.updateExploredBiomes();
            }
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Ticking player");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Player being ticked");
            this.addEntityCrashInfo(makeCategory);
            throw new ReportedException(makeCrashReport);
        }
    }

    public void updateExploredBiomes() {
        BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
        if (biomeGenForCoords != null) {
            String biomeName = biomeGenForCoords.biomeName;
            JsonSerializableSet stat = (JsonSerializableSet)this.getStatFile().getStat(AchievementList.exploreAllBiomes);
            if (stat == null) {
                stat = (JsonSerializableSet)this.getStatFile().setStat(AchievementList.exploreAllBiomes, new JsonSerializableSet());
            }

            stat.add(biomeName);
            if (this.getStatFile().canUnlockAchievement(AchievementList.exploreAllBiomes) && stat.size() == BiomeGenBase.explorationBiomesList.size()) {
                HashSet newHashSet = Sets.newHashSet(BiomeGenBase.explorationBiomesList);

                for (String str : (Iterable<String>)stat) {
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
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.mcServer.getConfigurationManager().sendChatMsg(this.getCombatTracker().getDeathMessage());
        if (!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
            this.inventory.dropAllItems();
        }

        for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)this.worldObj.getScoreboard().getObjective(IScoreObjectiveCriteria.deathCount)) {
            Score valueFromObjective = this.getWorldScoreboard().getValueFromObjective(this.getCommandSenderName(), scoreObjective);
            valueFromObjective.incrementScore();
        }

        EntityLivingBase opponent = this.getOpponent();
        if (opponent != null) {
            int entityID = EntityList.getEntityID(opponent);
            EntityEggInfo entityEggInfo = (EntityEggInfo)EntityList.entityEggs.get(entityID);
            if (entityEggInfo != null) {
                this.addStat(entityEggInfo.statBase2, 1);
            }

            opponent.addToPlayerScore(this, this.scoreValue);
        }

        this.addStat(StatList.deathsStat, 1);
        this.getCombatTracker().update();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        boolean flag = this.mcServer.isDedicatedServer() && this.mcServer.isPVPEnabled() && "fall".equals(source.damageType);
        if (!flag && this.spawnProtectionTicks > 0 && source != DamageSource.outOfWorld) {
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
    public boolean canAttackPlayer(EntityPlayer entityPlayer) {
        return this.mcServer.isPVPEnabled() && super.canAttackPlayer(entityPlayer);
    }

    @Override
    public void travelToDimension(int dimensionId) throws IOException {
        if (this.dimension == 1 && dimensionId == 1) {
            this.triggerAchievement(AchievementList.theEnd2);
            this.worldObj.removeEntity(this);
            this.playerConqueredTheEnd = true;
            this.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(4, 0.0F));
        } else {
            if (this.dimension == 0 && dimensionId == 1) {
                this.triggerAchievement(AchievementList.theEnd);
                ChunkCoordinates entrancePortalLocation = this.mcServer.worldServerForDimension(dimensionId).getEntrancePortalLocation();
                if (entrancePortalLocation != null) {
                    this.playerNetServerHandler.setPlayerLocation(entrancePortalLocation.posX, entrancePortalLocation.posY, entrancePortalLocation.posZ, 0.0F, 0.0F);
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

    public void updateBlockEntity(TileEntity tileEntity) {
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
    public EnumStatus sleepInBedAt(int x, int y, int z) {
        EnumStatus sleepInBedAt = super.sleepInBedAt(x, y, z);
        if (sleepInBedAt == EnumStatus.OK) {
            S0APacketUseBed s0APacketUseBed = new S0APacketUseBed(this, x, y, z);
            this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntity(this, s0APacketUseBed);
            this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.playerNetServerHandler.sendPacket(s0APacketUseBed);
        }

        return sleepInBedAt;
    }

    @Override
    public void wakeUpPlayer(boolean flag, boolean updateWorldFlag, boolean setSpawn) {
        if (this.isPlayerSleeping()) {
            this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntities(this, new S0BPacketAnimation(this, 2));
        }

        super.wakeUpPlayer(flag, updateWorldFlag, setSpawn);
        if (this.playerNetServerHandler != null) {
            this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    public void mountEntity(Entity entityIn) {
        super.mountEntity(entityIn);
        this.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this, this.ridingEntity));
        this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
    }

    @Override
    public void updateFallState(double distanceFallenThisTick, boolean isOnGround) {
    }

    public void handleFalling(double d, boolean flag) {
        super.updateFallState(d, flag);
    }

    @Override
    public void displayGUIEditSign(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntitySign) {
            ((TileEntitySign)tileEntity).setEditor(this);
            this.playerNetServerHandler.sendPacket(new S36PacketSignEditorOpen(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
        }
    }

    public void getNextWindowId() {
        this.currentWindowId = this.currentWindowId % 100 + 1;
    }

    @Override
    public void displayGUIWorkbench(int n, int n2, int n3) {
        this.getNextWindowId();
        this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, 1, "Crafting", 9, true));
        this.openContainer = new ContainerWorkbench(this.inventory, this.worldObj, n, n2, n3);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void displayGUIEnchantment(int n, int n2, int n3, String str) {
        this.getNextWindowId();
        this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, 4, str == null ? "" : str, 9, str != null));
        this.openContainer = new ContainerEnchantment(this.inventory, this.worldObj, n, n2, n3);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void displayGUIAnvil(int n, int n2, int n3) {
        this.getNextWindowId();
        this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, 8, "Repairing", 9, true));
        this.openContainer = new ContainerRepair(this.inventory, this.worldObj, n, n2, n3, this);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void displayGUIChest(IInventory inventory) {
        if (this.openContainer != this.inventoryContainer) {
            this.closeScreen();
        }

        this.getNextWindowId();
        this.playerNetServerHandler
            .sendPacket(new S2DPacketOpenWindow(this.currentWindowId, 0, inventory.getInventoryName(), inventory.getSizeInventory(), inventory.isCustomInventoryName()));
        this.openContainer = new ContainerChest(this.inventory, inventory);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void handleTileEntityHopper(TileEntityHopper tileEntityHopper) {
        this.getNextWindowId();
        this.playerNetServerHandler
            .sendPacket(new S2DPacketOpenWindow(this.currentWindowId, 9, tileEntityHopper.getInventoryName(), tileEntityHopper.getSizeInventory(), tileEntityHopper.isCustomInventoryName()));
        this.openContainer = new ContainerHopper(this.inventory, tileEntityHopper);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void displayGUIHopperMinecart(EntityMinecartHopper entityMinecartHopper) {
        this.getNextWindowId();
        this.playerNetServerHandler
            .sendPacket(new S2DPacketOpenWindow(this.currentWindowId, 9, entityMinecartHopper.getInventoryName(), entityMinecartHopper.getSizeInventory(), entityMinecartHopper.isCustomInventoryName()));
        this.openContainer = new ContainerHopper(this.inventory, entityMinecartHopper);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void handleTileEntityFurnace(TileEntityFurnace tileEntityFurnace) {
        this.getNextWindowId();
        this.playerNetServerHandler
            .sendPacket(new S2DPacketOpenWindow(this.currentWindowId, 2, tileEntityFurnace.getInventoryName(), tileEntityFurnace.getSizeInventory(), tileEntityFurnace.isCustomInventoryName()));
        this.openContainer = new ContainerFurnace(this.inventory, tileEntityFurnace);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void handleTileEntityDispenser(TileEntityDispenser tileEntityDispenser) {
        this.getNextWindowId();
        this.playerNetServerHandler
            .sendPacket(
                new S2DPacketOpenWindow(
                    this.currentWindowId, tileEntityDispenser instanceof TileEntityDropper ? 10 : 3, tileEntityDispenser.getInventoryName(), tileEntityDispenser.getSizeInventory(), tileEntityDispenser.isCustomInventoryName()
                )
            );
        this.openContainer = new ContainerDispenser(this.inventory, tileEntityDispenser);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void handleTileEntityBrewingStand(TileEntityBrewingStand tileEntityBrewingStand) {
        this.getNextWindowId();
        this.playerNetServerHandler
            .sendPacket(new S2DPacketOpenWindow(this.currentWindowId, 5, tileEntityBrewingStand.getInventoryName(), tileEntityBrewingStand.getSizeInventory(), tileEntityBrewingStand.isCustomInventoryName()));
        this.openContainer = new ContainerBrewingStand(this.inventory, tileEntityBrewingStand);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void handleTileEntityBeacon(TileEntityBeacon tileEntityBeacon) {
        this.getNextWindowId();
        this.playerNetServerHandler
            .sendPacket(new S2DPacketOpenWindow(this.currentWindowId, 7, tileEntityBeacon.getInventoryName(), tileEntityBeacon.getSizeInventory(), tileEntityBeacon.isCustomInventoryName()));
        this.openContainer = new ContainerBeacon(this.inventory, tileEntityBeacon);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void displayGUIMerchant(IMerchant merchant, String str) {
        this.getNextWindowId();
        this.openContainer = new ContainerMerchant(this.inventory, merchant, this.worldObj);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
        InventoryMerchant merchantInventory = ((ContainerMerchant)this.openContainer).getMerchantInventory();
        this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, 6, str == null ? "" : str, merchantInventory.getSizeInventory(), str != null));
        MerchantRecipeList recipes = merchant.getRecipes(this);
        if (recipes != null) {
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());

            try {
                packetBuffer.writeInt(this.currentWindowId);
                recipes.toPacket(packetBuffer);
                this.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|TrList", packetBuffer));
            } catch (IOException e) {
                logger.error("Couldn't send trade list", e);
            } finally {
                packetBuffer.release();
            }
        }
    }

    @Override
    public void displayGUIHorse(EntityHorse entityHorse, IInventory inventory) {
        if (this.openContainer != this.inventoryContainer) {
            this.closeScreen();
        }

        this.getNextWindowId();
        this.playerNetServerHandler
            .sendPacket(
                new S2DPacketOpenWindow(
                    this.currentWindowId, 11, inventory.getInventoryName(), inventory.getSizeInventory(), inventory.isCustomInventoryName(), entityHorse.getEntityId()
                )
            );
        this.openContainer = new ContainerHorseInventory(this.inventory, inventory, entityHorse);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.onCraftGuiOpened(this);
    }

    @Override
    public void sendSlotContents(Container container, int n, ItemStack itemStack) {
        if (!(container.getSlot(n) instanceof SlotCrafting) && !this.isChangingQuantityOnly) {
            this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(container.windowId, n, itemStack));
        }
    }

    public void sendContainerToPlayer(Container container) {
        this.updateCraftingInventory(container, container.getInventory());
    }

    @Override
    public void updateCraftingInventory(Container container, List list) {
        this.playerNetServerHandler.sendPacket(new S30PacketWindowItems(container.windowId, list));
        this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
    }

    @Override
    public void sendProgressBarUpdate(Container container, int n, int n2) {
        this.playerNetServerHandler.sendPacket(new S31PacketWindowProperty(container.windowId, n, n2));
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

    public void setEntityActionState(float f, float f2, boolean flag, boolean flag2) {
        if (this.ridingEntity != null) {
            if (f >= -1.0F && f <= 1.0F) {
                this.moveStrafing = f;
            }

            if (f2 >= -1.0F && f2 <= 1.0F) {
                this.moveForward = f2;
            }

            this.isJumping = flag;
            this.setSneaking(flag2);
        }
    }

    @Override
    public void addStat(StatBase statBase, int n) {
        if (statBase != null) {
            this.statHandler.addStatLevel(this, statBase, n);

            for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)this.getWorldScoreboard().getObjective(statBase.getCriterion())) {
                this.getWorldScoreboard().getValueFromObjective(this.getCommandSenderName(), scoreObjective).incrementScore();
            }

            if (this.statHandler.isFlag()) {
                this.statHandler.handleEntityPlayerMP(this);
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
    public void setItemInUse(ItemStack itemStack, int n) {
        super.setItemInUse(itemStack, n);
        if (itemStack != null && itemStack.getItem() != null && itemStack.getItem().getItemUseAction(itemStack) == EnumAction.eat) {
            this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntities(this, new S0BPacketAnimation(this, 3));
        }
    }

    @Override
    public void clonePlayer(EntityPlayer entityPlayer, boolean flag) {
        super.clonePlayer(entityPlayer, flag);
        this.lastExperience = -1;
        this.lastHealth = -1.0F;
        this.lastFoodLevel = -1;
        this.destroyedItemsNetCache.addAll(((EntityPlayerMP)entityPlayer).destroyedItemsNetCache);
    }

    @Override
    public void onNewPotionEffect(PotionEffect potionEffect) {
        super.onNewPotionEffect(potionEffect);
        this.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.getEntityId(), potionEffect));
    }

    @Override
    public void onChangedPotionEffect(PotionEffect potionEffect, boolean flag) {
        super.onChangedPotionEffect(potionEffect, flag);
        this.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.getEntityId(), potionEffect));
    }

    @Override
    public void onFinishedPotionEffect(PotionEffect potionEffect) {
        super.onFinishedPotionEffect(potionEffect);
        this.playerNetServerHandler.sendPacket(new S1EPacketRemoveEntityEffect(this.getEntityId(), potionEffect));
    }

    @Override
    public void setPositionAndUpdate(double d, double d2, double d3) {
        this.playerNetServerHandler.setPlayerLocation(d, d2, d3, this.rotationYaw, this.rotationPitch);
    }

    @Override
    public void onCriticalHit(Entity entity) {
        this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntities(this, new S0BPacketAnimation(entity, 4));
    }

    @Override
    public void onEnchantmentCritical(Entity entity) {
        this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntities(this, new S0BPacketAnimation(entity, 5));
    }

    @Override
    public void sendPlayerAbilities() {
        if (this.playerNetServerHandler != null) {
            this.playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(this.capabilities));
        }
    }

    public WorldServer getServerForPlayer() {
        return (WorldServer)this.worldObj;
    }

    @Override
    public void setGameType(GameType gameType) {
        this.theItemInWorldManager.setGameType(gameType);
        this.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(3, gameType.getID()));
    }

    @Override
    public void addChatMessage(IChatComponent message) {
        this.playerNetServerHandler.sendPacket(new S02PacketChat(message));
    }

    @Override
    public boolean canCommandSenderUseCommand(int permissionLevel, String command) {
        if ("seed".equals(command) && !this.mcServer.isDedicatedServer()) {
            return true;
        }

        if (!"tell".equals(command) && !"help".equals(command) && !"me".equals(command)) {
            if (this.mcServer.getConfigurationManager().canSendCommands(this.getGameProfile())) {
                UserListOpsEntry entry = (UserListOpsEntry)this.mcServer.getConfigurationManager().getOppedPlayers().getEntry(this.getGameProfile());
                return entry != null ? entry.getPermissionLevel() >= permissionLevel : this.mcServer.getOpPermissionLevel() >= permissionLevel;
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

    public void handleC15PacketClientSettings(C15PacketClientSettings c15PacketClientSettings) {
        this.translator = c15PacketClientSettings.getLang();
        int n = 256 >> c15PacketClientSettings.getView();
        if (n > 3 && n < 20) {
        }

        this.chatVisibility = c15PacketClientSettings.getChatVisibility();
        this.chatColours = c15PacketClientSettings.isColorsEnabled();
        if (this.mcServer.isSinglePlayer() && this.mcServer.getServerOwner().equals(this.getCommandSenderName())) {
            this.mcServer.setDifficultyForAllWorlds(c15PacketClientSettings.getDifficulty());
        }

        this.setHideCape(1, !c15PacketClientSettings.isShowCape());
    }

    public EnumChatVisibility getChatVisibility() {
        return this.chatVisibility;
    }

    public void requestTexturePackLoad(String str) {
        this.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|RPack", str.getBytes(Charsets.UTF_8)));
    }

    @Override
    public ChunkCoordinates getCommandSenderPosition() {
        return new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.5), MathHelper.floor_double(this.posZ));
    }

    public void markPlayerActive() {
        this.playerLastActiveTime = MinecraftServer.getCurrentTimeMillis();
    }

    public StatisticsFile getStatFile() {
        return this.statHandler;
    }

    public void stopTracking(Entity entity) {
        if (entity instanceof EntityPlayer) {
            this.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(entity.getEntityId()));
        } else {
            this.destroyedItemsNetCache.add(entity.getEntityId());
        }
    }

    public long getLastActiveTime() {
        return this.playerLastActiveTime;
    }
}
