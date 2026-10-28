package net.minecraft.entity.player;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.cosmetic.Cosmetic;
import com.cheatbreaker.client.emote.PlayerEmoteState;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.chunk.IChunkProvider;

public abstract class EntityPlayer extends EntityLivingBase implements ICommandSender {
    public InventoryPlayer inventory = new InventoryPlayer(this);
    public InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();
    public FoodStats foodStats = new FoodStats();
    public Cosmetic cosmetic = null;
    public Cosmetic cosmetic2 = null;
    public int flyToggleTimer;
    public float prevCameraYaw;
    public float cameraYaw;
    public int xpCooldown;
    public double capeX;
    public double capeY;
    public double capeZ;
    public double prevCapeX;
    public double prevCapeY;
    public double prevCapeZ;
    public boolean sleeping;
    public ChunkCoordinates playerLocation;
    public int sleepTimer;
    public float floatValue5;
    public float floatValue2;
    public float floatValue3;
    public ChunkCoordinates spawnChunk;
    public boolean spawnForced;
    public ChunkCoordinates startMinecartRidingCoordinate;
    public Cosmetic cosmetic3 = null;
    public int experienceLevel;
    public int experienceTotal;
    public float experience;
    public ItemStack itemInUse;
    public int itemInUseCount;
    public float floatValue4 = 0.0F;
    public PlayerCapabilities capabilities = new PlayerCapabilities();
    public int lastPlayedLevelUpSoundTime;
    public float speedOnGround = 0.1F;
    public EntityFishHook fishEntity;
    public float speedInAir = 0.02F;
    public GameProfile gameProfile;
    public Container inventoryContainer;
    public Container openContainer;

    public EntityPlayer(World world, GameProfile gameProfile) {
        super(world);
        this.entityUniqueID = getUUID(gameProfile);
        this.gameProfile = gameProfile;
        this.inventoryContainer = new ContainerPlayer(this.inventory, !world.isRemote, this);
        this.openContainer = this.inventoryContainer;
        this.yOffset = 1.62F;
        ChunkCoordinates spawnPoint = world.getSpawnPoint();
        this.setLocationAndAngles(spawnPoint.posX + 0.5, spawnPoint.posY + 1, spawnPoint.posZ + 0.5, 0.0F, 0.0F);
        this.floatValue = 180.0F;
        this.fireResistance = 20;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)0);
        this.dataWatcher.addObject(17, 0.0F);
        this.dataWatcher.addObject(18, 0);
        this.dataWatcher.addObject(19, (byte)127);
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
        return this.isUsingItem() && this.itemInUse.getItem().getItemUseAction(this.itemInUse) == EnumAction.block;
    }

    @Override
    public void onUpdate() throws java.io.IOException {
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

        this.capeX = this.prevCapeX;
        this.capeY = this.prevCapeY;
        this.capeZ = this.prevCapeZ;
        double d = this.posX - this.prevCapeX;
        double d2 = this.posY - this.prevCapeY;
        double d3 = this.posZ - this.prevCapeZ;
        double d4 = 10.0;
        if (d > d4) {
            this.capeX = this.prevCapeX = this.posX;
        }

        if (d3 > d4) {
            this.capeZ = this.prevCapeZ = this.posZ;
        }

        if (d2 > d4) {
            this.capeY = this.prevCapeY = this.posY;
        }

        if (d < -d4) {
            this.capeX = this.prevCapeX = this.posX;
        }

        if (d3 < -d4) {
            this.capeZ = this.prevCapeZ = this.posZ;
        }

        if (d2 < -d4) {
            this.capeY = this.prevCapeY = this.posY;
        }

        this.prevCapeX += d * 0.25;
        this.prevCapeZ += d3 * 0.25;
        this.prevCapeY += d2 * 0.25;
        if (this.ridingEntity == null) {
            this.startMinecartRidingCoordinate = null;
        }

        if (!this.worldObj.isRemote) {
            this.foodStats.onUpdate(this);
            this.addStat(StatList.minutesPlayedStat, 1);
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
        if (itemStackIn.getItemUseAction() == EnumAction.drink) {
            this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (itemStackIn.getItemUseAction() == EnumAction.eat) {
            for (int i = 0; i < n; i++) {
                Vec3 createVectorHelper = Vec3.createVectorHelper((this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
                createVectorHelper.rotateAroundX(-this.rotationPitch * (float) Math.PI / 180.0F);
                createVectorHelper.rotateAroundY(-this.rotationYaw * (float) Math.PI / 180.0F);
                Vec3 createVectorHelper2 = Vec3.createVectorHelper((this.rand.nextFloat() - 0.5) * 0.3, -this.rand.nextFloat() * 0.6 - 0.3, 0.6);
                createVectorHelper2.rotateAroundX(-this.rotationPitch * (float) Math.PI / 180.0F);
                createVectorHelper2.rotateAroundY(-this.rotationYaw * (float) Math.PI / 180.0F);
                createVectorHelper2 = createVectorHelper2.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
                String str = "iconcrack_" + Item.getIdFromItem(itemStackIn.getItem());
                if (itemStackIn.getHasSubtypes()) {
                    str = str + "_" + itemStackIn.getMetadata();
                }

                this.worldObj.spawnParticle(str, createVectorHelper2.xCoord, createVectorHelper2.yCoord, createVectorHelper2.zCoord, createVectorHelper.xCoord, createVectorHelper.yCoord + 0.05, createVectorHelper.zCoord);
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
    public void handleHealthUpdate(byte b) {
        if (b == 9) {
            this.onItemUseFinish();
        } else {
            super.handleHealthUpdate(b);
        }
    }

    @Override
    public boolean isMovementBlocked() {
        return this.getHealth() <= 0.0F || this.isPlayerSleeping();
    }

    public void closeScreen() throws java.io.IOException {
        this.openContainer = this.inventoryContainer;
    }

    @Override
    public void mountEntity(Entity entityIn) {
        if (this.ridingEntity != null && entityIn == null) {
            if (!this.worldObj.isRemote) {
                this.dismountEntity(this.ridingEntity);
            }

            if (this.ridingEntity != null) {
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        } else {
            super.mountEntity(entityIn);
        }
    }

    @Override
    public void updateRidden() throws java.io.IOException {
        if (!this.worldObj.isRemote && this.isSneaking()) {
            this.mountEntity(null);
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
        this.yOffset = 1.62F;
        this.setSize(0.6F, 1.8F);
        super.preparePlayerToSpawn();
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();
        this.updateArmSwingProgress();
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        if (this.flyToggleTimer > 0) {
            this.flyToggleTimer--;
        }

        if (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL
            && this.getHealth() < this.getMaxHealth()
            && this.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration")
            && this.ticksExisted % 20 * 12 == 0) {
            this.heal(1.0F);
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
        float f2 = (float)Math.atan(-this.motionY * 0.2F) * 15.0F;
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
        if (this.getHealth() > 0.0F) {
            AxisAlignedBB axisAlignedBB = null;
            if (this.ridingEntity != null && !this.ridingEntity.isDead) {
                axisAlignedBB = this.boundingBox.union(this.ridingEntity.boundingBox).expand(1.0, 0.0, 1.0);
            } else {
                axisAlignedBB = this.boundingBox.expand(1.0, 0.5, 1.0);
            }

            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, axisAlignedBB);
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Entity entity = (Entity)list.get(i);
                    if (!entity.isDead) {
                        this.collideWithPlayer(entity);
                    }
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
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.setSize(0.2F, 0.2F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionY = 0.1F;
        if (this.getCommandSenderName().equals("Notch")) {
            this.dropStack(new ItemStack(Items.apple, 1), true, false);
        }

        if (!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
            this.inventory.dropAllItems();
        }

        if (damageSource != null) {
            this.motionX = -MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F;
            this.motionZ = -MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F;
        } else {
            this.motionX = this.motionZ = 0.0;
        }

        this.yOffset = 0.1F;
        this.addStat(StatList.deathsStat, 1);
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
        Collection objective = this.getWorldScoreboard().getObjective(IScoreObjectiveCriteria.totalKillCount);
        if (entityIn instanceof EntityPlayer) {
            this.addStat(StatList.playerKillsStat, 1);
            objective.addAll(this.getWorldScoreboard().getObjective(IScoreObjectiveCriteria.playerKillCount));
        } else {
            this.addStat(StatList.mobKillsStat, 1);
        }

        for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)objective) {
            Score valueFromObjective = this.getWorldScoreboard().getValueFromObjective(this.getCommandSenderName(), scoreObjective);
            valueFromObjective.incrementScore();
        }
    }

    public EntityItem dropOneItem(boolean flag) {
        return this.dropStack(
            this.inventory
                .decrStackSize(this.inventory.currentItem, flag && this.inventory.getCurrentItem() != null ? this.inventory.getCurrentItem().stackSize : 1),
            false,
            true
        );
    }

    public EntityItem dropPlayerItemWithRandomChoice(ItemStack itemStackIn, boolean flag) {
        return this.dropStack(itemStackIn, false, false);
    }

    public EntityItem dropStack(ItemStack itemStack, boolean flag, boolean flag2) {
        if (itemStack == null) {
            return null;
        }

        if (itemStack.stackSize == 0) {
            return null;
        }

        EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY - 0.3F + this.getEyeHeight(), this.posZ, itemStack);
        entityItem.delayBeforeCanPickup = 40;
        if (flag2) {
            entityItem.setThrower(this.getCommandSenderName());
        }

        float f = 0.1F;
        if (flag) {
            float f2 = this.rand.nextFloat() * 0.5F;
            float f3 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            entityItem.motionX = -MathHelper.sin(f3) * f2;
            entityItem.motionZ = MathHelper.cos(f3) * f2;
            entityItem.motionY = 0.2F;
        } else {
            f = 0.3F;
            entityItem.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
            entityItem.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
            entityItem.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f + 0.1F;
            f = 0.02F;
            float f2 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            f *= this.rand.nextFloat();
            entityItem.motionX = entityItem.motionX + Math.cos(f2) * f;
            entityItem.motionY = entityItem.motionY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
            entityItem.motionZ = entityItem.motionZ + Math.sin(f2) * f;
        }

        this.joinEntityItemWithWorld(entityItem);
        this.addStat(StatList.dropStat, 1);
        return entityItem;
    }

    public void joinEntityItemWithWorld(EntityItem entityItem) {
        this.worldObj.spawnEntityInWorld(entityItem);
    }

    public float getBreakSpeed(Block block, boolean flag) {
        float strVsBlock = this.inventory.getStrVsBlock(block);
        if (strVsBlock > 1.0F) {
            int efficiencyModifier = EnchantmentHelper.getEfficiencyModifier(this);
            ItemStack currentItem = this.inventory.getCurrentItem();
            if (efficiencyModifier > 0 && currentItem != null) {
                float f = efficiencyModifier * efficiencyModifier + 1;
                if (!currentItem.canItemHarvestBlock(block) && strVsBlock <= 1.0F) {
                    strVsBlock += f * 0.08F;
                } else {
                    strVsBlock += f;
                }
            }
        }

        if (this.isPotionActive(Potion.digSpeed)) {
            strVsBlock *= 1.0F + (this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (this.isPotionActive(Potion.digSlowdown)) {
            strVsBlock *= 1.0F - (this.getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1) * 0.2F;
        }

        if (this.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
            strVsBlock /= 5.0F;
        }

        if (!this.onGround) {
            strVsBlock /= 5.0F;
        }

        return strVsBlock;
    }

    public boolean canHarvestBlock(Block block) {
        return this.inventory.canToolBreak(block);
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
        this.setScore(tagCompund.getInteger("Score"));
        if (this.sleeping) {
            this.playerLocation = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
            this.wakeUpPlayer(true, true, false);
        }

        if (tagCompund.hasKey("SpawnX", 99) && tagCompund.hasKey("SpawnY", 99) && tagCompund.hasKey("SpawnZ", 99)) {
            this.spawnChunk = new ChunkCoordinates(tagCompund.getInteger("SpawnX"), tagCompund.getInteger("SpawnY"), tagCompund.getInteger("SpawnZ"));
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
        tagCompound.setInteger("Score", this.getScore());
        if (this.spawnChunk != null) {
            tagCompound.setInteger("SpawnX", this.spawnChunk.posX);
            tagCompound.setInteger("SpawnY", this.spawnChunk.posY);
            tagCompound.setInteger("SpawnZ", this.spawnChunk.posZ);
            tagCompound.setBoolean("SpawnForced", this.spawnForced);
        }

        this.foodStats.writeNBT(tagCompound);
        this.capabilities.writeCapabilitiesToNBT(tagCompound);
        tagCompound.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
    }

    public void displayGUIChest(IInventory inventory) throws java.io.IOException {
    }

    public void handleTileEntityHopper(TileEntityHopper tileEntityHopper) throws java.io.IOException {
    }

    public void displayGUIHopperMinecart(EntityMinecartHopper entityMinecartHopper) throws java.io.IOException {
    }

    public void displayGUIHorse(EntityHorse entityHorse, IInventory inventory) throws java.io.IOException {
    }

    public void displayGUIEnchantment(int n, int n2, int n3, String str) throws java.io.IOException {
    }

    public void displayGUIAnvil(int n, int n2, int n3) throws java.io.IOException {
    }

    public void displayGUIWorkbench(int n, int n2, int n3) throws java.io.IOException {
    }

    @Override
    public float getEyeHeight() {
        return 0.12F;
    }

    public void resetHeight() {
        this.yOffset = 1.62F;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
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
            if (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
                amount = 0.0F;
            }

            if (this.worldObj.difficultySetting == EnumDifficulty.EASY) {
                amount = amount / 2.0F + 1.0F;
            }

            if (this.worldObj.difficultySetting == EnumDifficulty.HARD) {
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

        this.addStat(StatList.damageTakenStat, Math.round(amount * 10.0F));
        return super.attackEntityFrom(source, amount);
    }

    public boolean canAttackPlayer(EntityPlayer entityPlayer) {
        Team team = this.getTeam();
        Team team2 = entityPlayer.getTeam();
        return team == null || !team.isSameTeam(team2) || team.getAllowFriendlyFire();
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
    public void damageEntity(DamageSource damageSource, float f) {
        if (!this.isEntityInvulnerable()) {
            if (!damageSource.isUnblockable() && this.isBlocking() && f > 0.0F) {
                f = (1.0F + f) * 0.5F;
            }

            f = this.applyArmorCalculations(damageSource, f);
            f = this.applyPotionDamageCalculations(damageSource, f);
            float f2 = f;
            f = Math.max(f - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f2 - f));
            if (f != 0.0F) {
                this.addExhaustion(damageSource.getHungerDamage());
                float health = this.getHealth();
                this.setHealth(this.getHealth() - f);
                this.getCombatTracker().onDamage(damageSource, health, f);
            }
        }
    }

    public void handleTileEntityFurnace(TileEntityFurnace tileEntityFurnace) throws java.io.IOException {
    }

    public void handleTileEntityDispenser(TileEntityDispenser tileEntityDispenser) throws java.io.IOException {
    }

    public void displayGUIEditSign(TileEntity tileEntity) throws java.io.IOException {
    }

    public void handleCommandBlockLogic(CommandBlockLogic commandBlockLogic) throws java.io.IOException {
    }

    public void handleTileEntityBrewingStand(TileEntityBrewingStand tileEntityBrewingStand) throws java.io.IOException {
    }

    public void handleTileEntityBeacon(TileEntityBeacon tileEntityBeacon) throws java.io.IOException {
    }

    public void displayGUIMerchant(IMerchant merchant, String str) throws java.io.IOException {
    }

    public void displayGUIBook(ItemStack itemStack) throws java.io.IOException {
    }

    public boolean interactWith(Entity entity) throws java.io.IOException {
        ItemStack currentEquippedItem = this.getCurrentEquippedItem();
        ItemStack itemStack = currentEquippedItem != null ? currentEquippedItem.copy() : null;
        if (!entity.interactFirst(this)) {
            if (currentEquippedItem != null && entity instanceof EntityLivingBase) {
                if (this.capabilities.isCreativeMode) {
                    currentEquippedItem = itemStack;
                }

                if (currentEquippedItem.interactWithEntity(this, (EntityLivingBase)entity)) {
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

    public ItemStack getCurrentEquippedItem() {
        return this.inventory.getCurrentItem();
    }

    public void destroyCurrentEquippedItem() {
        this.inventory.setInventorySlotContents(this.inventory.currentItem, null);
    }

    @Override
    public double getYOffset() {
        return this.yOffset - 0.5F;
    }

    public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
        if (targetEntity.canAttackWithItem() && !targetEntity.hitByEntity(this)) {
            float attributeValue = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
            int n = 0;
            float f = 0.0F;
            if (targetEntity instanceof EntityLivingBase) {
                f = EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)targetEntity);
                n += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)targetEntity);
            }

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
                                EntityCrit2FX entityCrit2FX = new EntityCrit2FX(
                                    Minecraft.getMinecraft().theWorld,
                                    targetEntity,
                                    "crit",
                                    (Float)CheatBreaker.getInstance().getModManager().particlesMod.critMultiplier.getValue()
                                );
                                Minecraft.getMinecraft().effectRenderer.addEffect(entityCrit2FX);
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
                                EntityCrit2FX entityCrit2FX = new EntityCrit2FX(
                                    Minecraft.getMinecraft().theWorld,
                                    targetEntity,
                                    "magicCrit",
                                    (Float)CheatBreaker.getInstance().getModManager().particlesMod.sharpnessMultiplier.getValue()
                                );
                                Minecraft.getMinecraft().effectRenderer.addEffect(entityCrit2FX);
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
                        EnchantmentHelper.onUserDamaged((EntityLivingBase)targetEntity, this);
                    }

                    EnchantmentHelper.onTargetDamaged(this, targetEntity);
                    ItemStack currentEquippedItem = this.getCurrentEquippedItem();
                    Object targetEntity2 = targetEntity;
                    if (targetEntity instanceof EntityDragonPart) {
                        IEntityMultiPart entityDragonObj = ((EntityDragonPart)targetEntity).entityDragonObj;
                        if (entityDragonObj != null && entityDragonObj instanceof EntityLivingBase) {
                            targetEntity2 = entityDragonObj;
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

    public void onCriticalHit(Entity entity) {
    }

    public void onEnchantmentCritical(Entity entity) {
    }

    public void respawnPlayer() {
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

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public EntityPlayer.EnumStatus sleepInBedAt(int x, int y, int z) {
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

            if (Math.abs(this.posX - x) > 3.0 || Math.abs(this.posY - y) > 2.0 || Math.abs(this.posZ - z) > 3.0) {
                return EntityPlayer.EnumStatus.TOO_FAR_AWAY;
            }

            double d = 8.0;
            double d2 = 5.0;
            List entitiesWithinAABB = this.worldObj
                .getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(x - d, y - d2, z - d, x + d, y + d2, z + d));
            if (!entitiesWithinAABB.isEmpty()) {
                return EntityPlayer.EnumStatus.NOT_SAFE;
            }
        }

        if (this.isRiding()) {
            this.mountEntity(null);
        }

        this.setSize(0.2F, 0.2F);
        this.yOffset = 0.2F;
        if (this.worldObj.blockExists(x, y, z)) {
            int blockMetadata = this.worldObj.getBlockMetadata(x, y, z);
            int direction = BlockBed.getDirection(blockMetadata);
            float f = 0.5F;
            float f2 = 0.5F;
            switch (direction) {
                case 0:
                    f2 = 0.9F;
                    break;
                case 1:
                    f = 0.1F;
                    break;
                case 2:
                    f2 = 0.1F;
                    break;
                case 3:
                    f = 0.9F;
            }

            this.decrementXp(direction);
            this.setPosition(x + f, y + 0.9375F, z + f2);
        } else {
            this.setPosition(x + 0.5F, y + 0.9375F, z + 0.5F);
        }

        this.sleeping = true;
        this.sleepTimer = 0;
        this.playerLocation = new ChunkCoordinates(x, y, z);
        this.motionX = this.motionZ = this.motionY = 0.0;
        if (!this.worldObj.isRemote) {
            this.worldObj.updateAllPlayersSleepingFlag();
        }

        return EntityPlayer.EnumStatus.OK;
    }

    public void decrementXp(int n) {
        this.floatValue5 = 0.0F;
        this.floatValue3 = 0.0F;
        switch (n) {
            case 0:
                this.floatValue3 = -1.8F;
                break;
            case 1:
                this.floatValue5 = 1.8F;
                break;
            case 2:
                this.floatValue3 = 1.8F;
                break;
            case 3:
                this.floatValue5 = -1.8F;
        }
    }

    public void wakeUpPlayer(boolean flag, boolean updateWorldFlag, boolean setSpawn) {
        this.setSize(0.6F, 1.8F);
        this.resetHeight();
        ChunkCoordinates playerLocation = this.playerLocation;
        ChunkCoordinates playerLocation2 = this.playerLocation;
        if (playerLocation != null && this.worldObj.getBlock(playerLocation.posX, playerLocation.posY, playerLocation.posZ) == Blocks.bed) {
            BlockBed.setBedOccupied(this.worldObj, playerLocation.posX, playerLocation.posY, playerLocation.posZ, false);
            playerLocation2 = BlockBed.getSafeExitLocation(this.worldObj, playerLocation.posX, playerLocation.posY, playerLocation.posZ, 0);
            if (playerLocation2 == null) {
                playerLocation2 = new ChunkCoordinates(playerLocation.posX, playerLocation.posY + 1, playerLocation.posZ);
            }

            this.setPosition(playerLocation2.posX + 0.5F, playerLocation2.posY + this.yOffset + 0.1F, playerLocation2.posZ + 0.5F);
        }

        this.sleeping = false;
        if (!this.worldObj.isRemote && updateWorldFlag) {
            this.worldObj.updateAllPlayersSleepingFlag();
        }

        if (flag) {
            this.sleepTimer = 0;
        } else {
            this.sleepTimer = 100;
        }

        if (setSpawn) {
            this.setSpawnChunk(this.playerLocation, false);
        }
    }

    public boolean isInBed() {
        return this.worldObj.getBlock(this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ) == Blocks.bed;
    }

    public static ChunkCoordinates verifyRespawnCoordinates(World world, ChunkCoordinates chunkCoordinates, boolean flag) {
        IChunkProvider chunkProvider = world.getChunkProvider();
        chunkProvider.loadChunk(chunkCoordinates.posX - 3 >> 4, chunkCoordinates.posZ - 3 >> 4);
        chunkProvider.loadChunk(chunkCoordinates.posX + 3 >> 4, chunkCoordinates.posZ - 3 >> 4);
        chunkProvider.loadChunk(chunkCoordinates.posX - 3 >> 4, chunkCoordinates.posZ + 3 >> 4);
        chunkProvider.loadChunk(chunkCoordinates.posX + 3 >> 4, chunkCoordinates.posZ + 3 >> 4);
        if (world.getBlock(chunkCoordinates.posX, chunkCoordinates.posY, chunkCoordinates.posZ) == Blocks.bed) {
            return BlockBed.getSafeExitLocation(world, chunkCoordinates.posX, chunkCoordinates.posY, chunkCoordinates.posZ, 0);
        }

        Material material = world.getBlock(chunkCoordinates.posX, chunkCoordinates.posY, chunkCoordinates.posZ).getMaterial();
        Material material2 = world.getBlock(chunkCoordinates.posX, chunkCoordinates.posY + 1, chunkCoordinates.posZ).getMaterial();
        boolean flag2 = !material.isSolid() && !material.isLiquid();
        boolean flag3 = !material2.isSolid() && !material2.isLiquid();
        return flag && flag2 && flag3 ? chunkCoordinates : null;
    }

    public float getBedOrientationInDegrees() {
        if (this.playerLocation != null) {
            int blockMetadata = this.worldObj.getBlockMetadata(this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ);
            int direction = BlockBed.getDirection(blockMetadata);
            switch (direction) {
                case 0:
                    return 90.0F;
                case 1:
                    return 0.0F;
                case 2:
                    return 270.0F;
                case 3:
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

    public boolean getHideCape(int n) {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1 << n) != 0;
    }

    public void setHideCape(int n, boolean flag) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
        if (flag) {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 1 << n));
        } else {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & ~(1 << n)));
        }
    }

    public void addChatComponentMessage(IChatComponent chatComponent) {
    }

    public ChunkCoordinates getBedLocation() {
        return this.spawnChunk;
    }

    public boolean isSpawnForced() {
        return this.spawnForced;
    }

    public void setSpawnChunk(ChunkCoordinates chunkCoordinates, boolean flag) {
        if (chunkCoordinates != null) {
            this.spawnChunk = new ChunkCoordinates(chunkCoordinates);
            this.spawnForced = flag;
        } else {
            this.spawnChunk = null;
            this.spawnForced = false;
        }
    }

    public void triggerAchievement(StatBase statBase) {
        this.addStat(statBase, 1);
    }

    public void addStat(StatBase statBase, int n) {
    }

    @Override
    public void jump() {
        super.jump();
        this.addStat(StatList.jumpStat, 1);
        if (this.isSprinting()) {
            this.addExhaustion(0.8F);
        } else {
            this.addExhaustion(0.2F);
        }
    }

    @Override
    public void moveEntityWithHeading(float f, float f2) {
        double posX = this.posX;
        double posY = this.posY;
        double posZ = this.posZ;
        if (this.capabilities.isFlying && this.ridingEntity == null) {
            double motionY = this.motionY;
            float jumpMovementFactor = this.jumpMovementFactor;
            this.jumpMovementFactor = this.capabilities.getFlySpeed();
            super.moveEntityWithHeading(f, f2);
            this.motionY = motionY * 0.6;
            this.jumpMovementFactor = jumpMovementFactor;
        } else {
            super.moveEntityWithHeading(f, f2);
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
                        this.addExhaustion(0.099999994F * round * 0.01F);
                    } else {
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
                        this.startMinecartRidingCoordinate = new ChunkCoordinates(
                            MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)
                        );
                    } else if (this.startMinecartRidingCoordinate
                            .getDistanceSquared(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                        >= 1000000.0) {
                        this.addStat(AchievementList.onARail, 1);
                    }
                } else if (this.ridingEntity instanceof EntityBoat) {
                    this.addStat(StatList.distanceByBoatStat, round);
                } else if (this.ridingEntity instanceof EntityPig) {
                    this.addStat(StatList.distanceByPigStat, round);
                } else if (this.ridingEntity instanceof EntityHorse) {
                    this.addStat(StatList.CM_HORSE, round);
                }
            }
        }
    }

    @Override
    public void fall(float distance) {
        if (!this.capabilities.allowFlying) {
            if (distance >= 2.0F) {
                this.addStat(StatList.distanceFallenStat, (int)Math.round(distance * 100.0));
            }

            super.fall(distance);
        }
    }

    @Override
    public String formatText(int n) {
        return n > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        if (entityLivingIn instanceof IMob) {
            this.triggerAchievement(AchievementList.killEnemy);
        }

        int entityID = EntityList.getEntityID(entityLivingIn);
        EntityEggInfo entityEggInfo = (EntityEggInfo)EntityList.entityEggs.get(entityID);
        if (entityEggInfo != null) {
            this.addStat(entityEggInfo.statBase, 1);
        }
    }

    @Override
    public void setInWeb() {
        if (!this.capabilities.isFlying) {
            super.setInWeb();
        }
    }

    @Override
    public IIcon getItemIcon(ItemStack itemStackIn, int n) {
        IIcon itemIcon = super.getItemIcon(itemStackIn, n);
        if (itemStackIn.getItem() == Items.fishing_rod && this.fishEntity != null) {
            itemIcon = Items.fishing_rod.getCastTexture();
        } else {
            if (itemStackIn.getItem().requiresMultipleRenderPasses()) {
                return itemStackIn.getItem().getIconFromDamageForRenderPass(itemStackIn.getMetadata(), n);
            }

            if (this.itemInUse != null && itemStackIn.getItem() == Items.bow) {
                int n2 = itemStackIn.getMaxItemUseDuration() - this.itemInUseCount;
                if (n2 >= 18) {
                    return Items.bow.getItemIconForUseDuration(2);
                }

                if (n2 > 13) {
                    return Items.bow.getItemIconForUseDuration(1);
                }

                if (n2 > 0) {
                    return Items.bow.getItemIconForUseDuration(0);
                }
            }
        }

        return itemIcon;
    }

    public ItemStack getCurrentArmor(int n) {
        return this.inventory.armorItemInSlot(n);
    }

    public void addExperience(int n) {
        this.addScore(n);
        int n2 = Integer.MAX_VALUE - this.experienceTotal;
        if (n > n2) {
            n = n2;
        }

        this.experience = this.experience + (float)n / this.xpBarCap();

        for (this.experienceTotal += n; this.experience >= 1.0F; this.experience = this.experience / this.xpBarCap()) {
            this.experience = (this.experience - 1.0F) * this.xpBarCap();
            this.addExperienceLevel(1);
        }
    }

    public void addExperienceLevel(int n) {
        this.experienceLevel += n;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experience = 0.0F;
            this.experienceTotal = 0;
        }

        if (n > 0 && this.experienceLevel % 5 == 0 && this.lastPlayedLevelUpSoundTime < this.ticksExisted - 100.0F) {
            float f = this.experienceLevel > 30 ? 1.0F : this.experienceLevel / 30.0F;
            this.worldObj.playSoundAtEntity(this, "random.levelup", f * 0.75F, 1.0F);
            this.lastPlayedLevelUpSoundTime = this.ticksExisted;
        }
    }

    public int xpBarCap() {
        return this.experienceLevel >= 30 ? 62 + (this.experienceLevel - 30) * 7 : (this.experienceLevel >= 15 ? 17 + (this.experienceLevel - 15) * 3 : 17);
    }

    public void addExhaustion(float f) {
        if (!this.capabilities.disableDamage && !this.worldObj.isRemote) {
            this.foodStats.addExhaustion(f);
        }
    }

    public FoodStats getFoodStats() {
        return this.foodStats;
    }

    public boolean canEat(boolean flag) {
        return (flag || this.foodStats.needFood()) && !this.capabilities.disableDamage;
    }

    public boolean shouldHeal() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    public void setItemInUse(ItemStack itemStack, int n) {
        if (itemStack != this.itemInUse) {
            this.itemInUse = itemStack;
            this.itemInUseCount = n;
            if (!this.worldObj.isRemote) {
                this.setEating(true);
            }
        }
    }

    public boolean isCurrentToolAdventureModeExempt(int n, int n2, int n3) {
        if (this.capabilities.allowEdit) {
            return true;
        }

        Block block = this.worldObj.getBlock(n, n2, n3);
        if (block.getMaterial() != Material.air) {
            if (block.getMaterial().isAdventureModeExempt()) {
                return true;
            }

            if (this.getCurrentEquippedItem() != null) {
                ItemStack currentEquippedItem = this.getCurrentEquippedItem();
                return currentEquippedItem.canItemHarvestBlock(block) || currentEquippedItem.getStrVsBlock(block) > 1.0F;
            }
        }

        return false;
    }

    public boolean canPlayerEdit(int n, int n2, int n3, int n4, ItemStack itemStack) {
        return this.capabilities.allowEdit || itemStack != null && itemStack.canEditBlocks();
    }

    @Override
    public int getExperiencePoints(EntityPlayer entityPlayer) {
        if (this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
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

    public void clonePlayer(EntityPlayer entityPlayer, boolean flag) {
        if (flag) {
            this.inventory.copyInventory(entityPlayer.inventory);
            this.setHealth(entityPlayer.getHealth());
            this.foodStats = entityPlayer.foodStats;
            this.experienceLevel = entityPlayer.experienceLevel;
            this.experienceTotal = entityPlayer.experienceTotal;
            this.experience = entityPlayer.experience;
            this.setScore(entityPlayer.getScore());
            this.teleportDirection = entityPlayer.teleportDirection;
        } else if (this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
            this.inventory.copyInventory(entityPlayer.inventory);
            this.experienceLevel = entityPlayer.experienceLevel;
            this.experienceTotal = entityPlayer.experienceTotal;
            this.experience = entityPlayer.experience;
            this.setScore(entityPlayer.getScore());
        }

        this.theInventoryEnderChest = entityPlayer.theInventoryEnderChest;
    }

    @Override
    public boolean canTriggerWalking() {
        return !this.capabilities.isFlying;
    }

    public void sendPlayerAbilities() {
    }

    public void setGameType(GameType gameType) {
    }

    @Override
    public String getCommandSenderName() {
        return this.gameProfile.getName();
    }

    @Override
    public World getEntityWorld() {
        return this.worldObj;
    }

    public InventoryEnderChest getInventoryEnderChest() {
        return this.theInventoryEnderChest;
    }

    @Override
    public ItemStack getEquipmentInSlot(int n) {
        return n == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[n - 1];
    }

    @Override
    public ItemStack getHeldItem() {
        return this.inventory.getCurrentItem();
    }

    @Override
    public void setCurrentItemOrArmor(int slotIn, ItemStack itemStackIn) {
        try {
            this.inventory.armorInventory[slotIn] = itemStackIn;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        if (!this.isInvisible()) {
            return false;
        }

        Team team = this.getTeam();
        return team == null || player == null || player.getTeam() != team || !team.shouldShowFriendlyInvisibles();
    }

    @Override
    public ItemStack[] getInventory() {
        return this.inventory.armorInventory;
    }

    public boolean getHideCape() {
        return this.getHideCape(1);
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
        return this.getWorldScoreboard().getPlayersTeam(this.getCommandSenderName());
    }

    @Override
    public IChatComponent getFormattedCommandSenderName() {
        ChatComponentText chatComponentText = new ChatComponentText(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getCommandSenderName()));
        chatComponentText.getChatStyle().setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/msg " + this.getCommandSenderName() + " "));
        return chatComponentText;
    }

    @Override
    public void setAbsorptionAmount(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.getDataWatcher().updateObject(17, f);
    }

    @Override
    public float getAbsorptionAmount() {
        return this.getDataWatcher().getWatchableObjectFloat(17);
    }

    public static UUID getUUID(GameProfile gameProfile) {
        UUID id = gameProfile.getId();
        if (id == null) {
            id = UUID.nameUUIDFromBytes(("OfflinePlayer:" + gameProfile.getName()).getBytes(Charsets.UTF_8));
        }

        return id;
    }

    public boolean hasCosmetic3() {
        return this.cosmetic3 != null;
    }

    public Cosmetic getCosmetic() {
        return this.cosmetic;
    }

    public void setModelPartsMask(int n) {
        this.dataWatcher.updateObject(19, (byte)n);
    }

    public void setCosmetic3(Cosmetic cosmetic) {
        this.cosmetic3 = cosmetic;
    }

    public void setHideCape(boolean flag) {
        this.setHideCape(1, flag);
    }

    public Cosmetic getCosmetic3() {
        return this.cosmetic3;
    }

    public boolean hasCosmetic2() {
        return this.cosmetic2 != null;
    }

    public boolean checkEnumPlayerModelParts(EnumPlayerModelParts enumPlayerModelParts) {
        return enumPlayerModelParts == EnumPlayerModelParts.CAPE ? !this.getHideCape() : (this.dataWatcher.getWatchableObjectByte(19) & enumPlayerModelParts.getPartMask()) == enumPlayerModelParts.getPartMask();
    }

    public Cosmetic getCosmetic2() {
        return this.cosmetic2;
    }

    public void setCosmetic(Cosmetic cosmetic) {
        this.cosmetic = cosmetic;
    }

    public void setCosmetic2(Cosmetic cosmetic) {
        this.cosmetic2 = cosmetic;
    }

    public boolean hasCosmetic() {
        return this.cosmetic != null;
    }

    public enum EnumChatVisibility {
        FULL("FULL", 0, 0, "options.chat.visibility.full"),
        SYSTEM("SYSTEM", 1, 1, "options.chat.visibility.system"),
        HIDDEN("HIDDEN", 2, 2, "options.chat.visibility.hidden");

        public int chatVisibility;
        public String resourceKey;
        public static EntityPlayer.EnumChatVisibility[] TYPE = new EntityPlayer.EnumChatVisibility[values().length];
        public static EntityPlayer.EnumChatVisibility[] enumChatVisibilityArray = new EntityPlayer.EnumChatVisibility[]{FULL, SYSTEM, HIDDEN};

        public int getChatVisibility() {
            return this.chatVisibility;
        }

        public static EntityPlayer.EnumChatVisibility getEnumChatVisibility(int n) {
            return TYPE[n % TYPE.length];
        }

        public String getResourceKey() {
            return this.resourceKey;
        }

        static {
            for (EntityPlayer.EnumChatVisibility enumChatVisibility : values()) {
                TYPE[enumChatVisibility.chatVisibility] = enumChatVisibility;
            }
        }

        EnumChatVisibility(String str, int n, int n2, String str2) {
            this.chatVisibility = n2;
            this.resourceKey = str2;
        }
    }

    public enum EnumStatus {
        OK("OK", 0),
        NOT_POSSIBLE_HERE("NOT_POSSIBLE_HERE", 1),
        NOT_POSSIBLE_NOW("NOT_POSSIBLE_NOW", 2),
        TOO_FAR_AWAY("TOO_FAR_AWAY", 3),
        OTHER_PROBLEM("OTHER_PROBLEM", 4),
        NOT_SAFE("NOT_SAFE", 5);

        EnumStatus(String str, int n) {
        }
    }
}
