package net.minecraft.entity.passive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityVillager extends EntityAgeable implements INpc, IMerchant {
    public int randomTickDivider;
    public boolean isMating;
    public boolean isPlaying;
    public Village villageObj;
    public EntityPlayer buyingPlayer;
    public MerchantRecipeList buyingList;
    public int timeUntilReset;
    public boolean needsInitilization;
    public int wealth;
    public String lastBuyingPlayer;
    public boolean isLookingForHome;
    public float floatValue4;
    public static Map villagersSellingList = new HashMap();
    public static Map blacksmithSellingList = new HashMap();

    public EntityVillager(World world) {
        this(world, 0);
    }

    public EntityVillager(World world, int n) {
        super(world);
        this.setProfession(n);
        this.setSize(0.6F, 1.8F);
        this.getNavigator().setBreakDoors(true);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6, 0.6));
        this.tasks.addTask(1, new EntityAITradePlayer(this));
        this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6));
        this.tasks.addTask(6, new EntityAIVillagerMate(this));
        this.tasks.addTask(7, new EntityAIFollowGolem(this));
        this.tasks.addTask(8, new EntityAIPlay(this, 0.32));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityVillager.class, 5.0F, 0.02F));
        this.tasks.addTask(9, new EntityAIWander(this, 0.6));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void updateAITick() {
        if (--this.randomTickDivider <= 0) {
            this.worldObj
                .villageCollectionObj
                .addVillagerPosition(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
            this.randomTickDivider = 70 + this.rand.nextInt(50);
            this.villageObj = this.worldObj
                .villageCollectionObj
                .findNearestVillage(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 32);
            if (this.villageObj == null) {
                this.detachHome();
            } else {
                ChunkCoordinates center = this.villageObj.getCenter();
                this.setHomeArea(center.posX, center.posY, center.posZ, (int)(this.villageObj.getVillageRadius() * 0.6F));
                if (this.isLookingForHome) {
                    this.isLookingForHome = false;
                    this.villageObj.setDefaultPlayerReputation(5);
                }
            }
        }

        if (!this.isTrading() && this.timeUntilReset > 0) {
            this.timeUntilReset--;
            if (this.timeUntilReset <= 0) {
                if (this.needsInitilization) {
                    if (this.buyingList.size() > 1) {
                        for (MerchantRecipe merchantRecipe : (Iterable<MerchantRecipe>)this.buyingList) {
                            if (merchantRecipe.isRecipeDisabled()) {
                                merchantRecipe.increaseSpecialPrice(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                            }
                        }
                    }

                    this.addDefaultEquipmentAndRecipies(1);
                    this.needsInitilization = false;
                    if (this.villageObj != null && this.lastBuyingPlayer != null) {
                        this.worldObj.setEntityState(this, (byte)14);
                        this.villageObj.setReputationForPlayer(this.lastBuyingPlayer, 1);
                    }
                }

                this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 0));
            }
        }

        super.updateAITick();
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) throws java.io.IOException {
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        boolean flag = currentItem != null && currentItem.getItem() == Items.spawn_egg;
        if (!flag && this.isEntityAlive() && !this.isTrading() && !this.isChild()) {
            if (!this.worldObj.isRemote) {
                this.setCustomer(entityPlayer);
                entityPlayer.displayGUIMerchant(this, this.getCustomNameTag());
            }

            return true;
        } else {
            return super.interact(entityPlayer);
        }
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, 0);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Profession", this.getProfession());
        tagCompound.setInteger("Riches", this.wealth);
        if (this.buyingList != null) {
            tagCompound.setTag("Offers", this.buyingList.getRecipiesAsTags());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setProfession(tagCompund.getInteger("Profession"));
        this.wealth = tagCompund.getInteger("Riches");
        if (tagCompund.hasKey("Offers", 10)) {
            NBTTagCompound compoundTag = tagCompund.getCompoundTag("Offers");
            this.buyingList = new MerchantRecipeList(compoundTag);
        }
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public String getLivingSound() {
        return this.isTrading() ? "mob.villager.haggle" : "mob.villager.idle";
    }

    @Override
    public String getHurtSound() {
        return "mob.villager.hit";
    }

    @Override
    public String getDeathSound() {
        return "mob.villager.death";
    }

    public void setProfession(int n) {
        this.dataWatcher.updateObject(16, n);
    }

    public int getProfession() {
        return this.dataWatcher.getWatchableObjectInt(16);
    }

    public boolean isMating() {
        return this.isMating;
    }

    public void setMating(boolean flag) {
        this.isMating = flag;
    }

    public void setPlaying(boolean flag) {
        this.isPlaying = flag;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    @Override
    public void setRevengeTarget(EntityLivingBase entityLivingBase) {
        super.setRevengeTarget(entityLivingBase);
        if (this.villageObj != null && entityLivingBase != null) {
            this.villageObj.addOrRenewAgressor(entityLivingBase);
            if (entityLivingBase instanceof EntityPlayer) {
                byte b = -1;
                if (this.isChild()) {
                    b = -3;
                }

                this.villageObj.setReputationForPlayer(entityLivingBase.getCommandSenderName(), b);
                if (this.isEntityAlive()) {
                    this.worldObj.setEntityState(this, (byte)13);
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (this.villageObj != null) {
            Entity entity = damageSource.getEntity();
            if (entity != null) {
                if (entity instanceof EntityPlayer) {
                    this.villageObj.setReputationForPlayer(entity.getCommandSenderName(), -2);
                } else if (entity instanceof IMob) {
                    this.villageObj.endMatingSeason();
                }
            } else if (entity == null) {
                EntityPlayer closestPlayerToEntity = this.worldObj.getClosestPlayerToEntity(this, 16.0);
                if (closestPlayerToEntity != null) {
                    this.villageObj.endMatingSeason();
                }
            }
        }

        super.onDeath(damageSource);
    }

    @Override
    public void setCustomer(EntityPlayer entityPlayer) {
        this.buyingPlayer = entityPlayer;
    }

    @Override
    public EntityPlayer getCustomer() {
        return this.buyingPlayer;
    }

    public boolean isTrading() {
        return this.buyingPlayer != null;
    }

    @Override
    public void useRecipe(MerchantRecipe merchantRecipe) {
        merchantRecipe.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
        if (merchantRecipe.hasSameIDsAs((MerchantRecipe)this.buyingList.get(this.buyingList.size() - 1))) {
            this.timeUntilReset = 40;
            this.needsInitilization = true;
            if (this.buyingPlayer != null) {
                this.lastBuyingPlayer = this.buyingPlayer.getCommandSenderName();
            } else {
                this.lastBuyingPlayer = null;
            }
        }

        if (merchantRecipe.getItemToBuy().getItem() == Items.emerald) {
            this.wealth = this.wealth + merchantRecipe.getItemToBuy().stackSize;
        }
    }

    @Override
    public void verifySellingItem(ItemStack itemStack) {
        if (!this.worldObj.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
            this.livingSoundTime = -this.getTalkInterval();
            if (itemStack != null) {
                this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
            } else {
                this.playSound("mob.villager.no", this.getSoundVolume(), this.getSoundPitch());
            }
        }
    }

    @Override
    public MerchantRecipeList getRecipes(EntityPlayer entityPlayer) {
        if (this.buyingList == null) {
            this.addDefaultEquipmentAndRecipies(1);
        }

        return this.buyingList;
    }

    public float adjustProbability(float f) {
        float f2 = f + this.floatValue4;
        return f2 > 0.9F ? 0.9F - (f2 - 0.9F) : f2;
    }

    public void addDefaultEquipmentAndRecipies(int n) {
        if (this.buyingList != null) {
            this.floatValue4 = MathHelper.sqrt_float(this.buyingList.size()) * 0.2F;
        } else {
            this.floatValue4 = 0.0F;
        }

        MerchantRecipeList merchantRecipeList = new MerchantRecipeList();
        switch (this.getProfession()) {
            case 0:
                handleMerchantRecipeList(merchantRecipeList, Items.wheat, this.rand, this.adjustProbability(0.9F));
                handleMerchantRecipeList(merchantRecipeList, Item.getItemFromBlock(Blocks.wool), this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList(merchantRecipeList, Items.chicken, this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList(merchantRecipeList, Items.cooked_fish, this.rand, this.adjustProbability(0.4F));
                handleMerchantRecipeList2(merchantRecipeList, Items.bread, this.rand, this.adjustProbability(0.9F));
                handleMerchantRecipeList2(merchantRecipeList, Items.melon, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.apple, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.cookie, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.shears, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.flint_and_steel, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.cooked_chicken, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.arrow, this.rand, this.adjustProbability(0.5F));
                if (this.rand.nextFloat() < this.adjustProbability(0.5F)) {
                    merchantRecipeList.add(
                        new MerchantRecipe(new ItemStack(Blocks.gravel, 10), new ItemStack(Items.emerald), new ItemStack(Items.flint, 4 + this.rand.nextInt(2), 0))
                    );
                }
                break;
            case 1:
                handleMerchantRecipeList(merchantRecipeList, Items.paper, this.rand, this.adjustProbability(0.8F));
                handleMerchantRecipeList(merchantRecipeList, Items.book, this.rand, this.adjustProbability(0.8F));
                handleMerchantRecipeList(merchantRecipeList, Items.written_book, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Item.getItemFromBlock(Blocks.bookshelf), this.rand, this.adjustProbability(0.8F));
                handleMerchantRecipeList2(merchantRecipeList, Item.getItemFromBlock(Blocks.glass), this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.compass, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.clock, this.rand, this.adjustProbability(0.2F));
                if (this.rand.nextFloat() < this.adjustProbability(0.07F)) {
                    Enchantment enchantment = Enchantment.enchantmentsBookList[this.rand.nextInt(Enchantment.enchantmentsBookList.length)];
                    int randomIntegerInRange = MathHelper.getRandomIntegerInRange(this.rand, enchantment.getMinLevel(), enchantment.getMaxLevel());
                    ItemStack enchantedItemStack = Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, randomIntegerInRange));
                    int n2 = 2 + this.rand.nextInt(5 + randomIntegerInRange * 10) + 3 * randomIntegerInRange;
                    merchantRecipeList.add(new MerchantRecipe(new ItemStack(Items.book), new ItemStack(Items.emerald, n2), enchantedItemStack));
                }
                break;
            case 2:
                handleMerchantRecipeList2(merchantRecipeList, Items.ender_eye, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.experience_bottle, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.redstone, this.rand, this.adjustProbability(0.4F));
                handleMerchantRecipeList2(merchantRecipeList, Item.getItemFromBlock(Blocks.glowstone), this.rand, this.adjustProbability(0.3F));
                Item[] items = new Item[]{
                    Items.iron_sword,
                    Items.diamond_sword,
                    Items.iron_chestplate,
                    Items.diamond_chestplate,
                    Items.iron_axe,
                    Items.diamond_axe,
                    Items.iron_pickaxe,
                    Items.diamond_pickaxe
                };
                Item[] items2 = items;
                int length = items.length;

                for (int i = 0; i < length; i++) {
                    Item item = items2[i];
                    if (this.rand.nextFloat() < this.adjustProbability(0.05F)) {
                        merchantRecipeList.add(
                            new MerchantRecipe(
                                new ItemStack(item, 1, 0),
                                new ItemStack(Items.emerald, 2 + this.rand.nextInt(3), 0),
                                EnchantmentHelper.addRandomEnchantment(this.rand, new ItemStack(item, 1, 0), 5 + this.rand.nextInt(15))
                            )
                        );
                    }
                }
                break;
            case 3:
                handleMerchantRecipeList(merchantRecipeList, Items.coal, this.rand, this.adjustProbability(0.7F));
                handleMerchantRecipeList(merchantRecipeList, Items.iron_ingot, this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList(merchantRecipeList, Items.gold_ingot, this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList(merchantRecipeList, Items.diamond, this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList2(merchantRecipeList, Items.iron_sword, this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList2(merchantRecipeList, Items.diamond_sword, this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList2(merchantRecipeList, Items.iron_axe, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.diamond_axe, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.iron_pickaxe, this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList2(merchantRecipeList, Items.diamond_pickaxe, this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList2(merchantRecipeList, Items.iron_shovel, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.diamond_shovel, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.iron_hoe, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.diamond_hoe, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.iron_boots, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.diamond_boots, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.iron_helmet, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.diamond_helmet, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.iron_chestplate, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.diamond_chestplate, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.iron_leggings, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.diamond_leggings, this.rand, this.adjustProbability(0.2F));
                handleMerchantRecipeList2(merchantRecipeList, Items.chainmail_boots, this.rand, this.adjustProbability(0.1F));
                handleMerchantRecipeList2(merchantRecipeList, Items.chainmail_helmet, this.rand, this.adjustProbability(0.1F));
                handleMerchantRecipeList2(merchantRecipeList, Items.chainmail_chestplate, this.rand, this.adjustProbability(0.1F));
                handleMerchantRecipeList2(merchantRecipeList, Items.chainmail_leggings, this.rand, this.adjustProbability(0.1F));
                break;
            case 4:
                handleMerchantRecipeList(merchantRecipeList, Items.coal, this.rand, this.adjustProbability(0.7F));
                handleMerchantRecipeList(merchantRecipeList, Items.porkchop, this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList(merchantRecipeList, Items.beef, this.rand, this.adjustProbability(0.5F));
                handleMerchantRecipeList2(merchantRecipeList, Items.saddle, this.rand, this.adjustProbability(0.1F));
                handleMerchantRecipeList2(merchantRecipeList, Items.leather_chestplate, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.leather_boots, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.leather_helmet, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.leather_leggings, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.cooked_porkchop, this.rand, this.adjustProbability(0.3F));
                handleMerchantRecipeList2(merchantRecipeList, Items.cooked_beef, this.rand, this.adjustProbability(0.3F));
        }

        if (merchantRecipeList.isEmpty()) {
            handleMerchantRecipeList(merchantRecipeList, Items.gold_ingot, this.rand, 1.0F);
        }

        Collections.shuffle(merchantRecipeList);
        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }

        for (int i = 0; i < n && i < merchantRecipeList.size(); i++) {
            this.buyingList.addToListWithCheck((MerchantRecipe)merchantRecipeList.get(i));
        }
    }

    @Override
    public void setRecipes(MerchantRecipeList merchantRecipeList) {
    }

    public static void handleMerchantRecipeList(MerchantRecipeList merchantRecipeList, Item item, Random random, float f) {
        if (random.nextFloat() < f) {
            merchantRecipeList.add(new MerchantRecipe(createItemStack(item, random), Items.emerald));
        }
    }

    public static ItemStack createItemStack(Item item, Random random) {
        return new ItemStack(item, computeInt(item, random), 0);
    }

    public static int computeInt(Item item, Random random) {
        Tuple tuple = (Tuple)villagersSellingList.get(item);
        return tuple == null
            ? 1
            : (
                (Integer)tuple.getFirst() >= (Integer)tuple.getSecond()
                    ? (Integer)tuple.getFirst()
                    : (Integer)tuple.getFirst() + random.nextInt((Integer)tuple.getSecond() - (Integer)tuple.getFirst())
            );
    }

    public static void handleMerchantRecipeList2(MerchantRecipeList merchantRecipeList, Item item, Random random, float f) {
        if (random.nextFloat() < f) {
            int computeInt22 = computeInt2(item, random);
            ItemStack itemStack;
            ItemStack itemStack2;
            if (computeInt22 < 0) {
                itemStack = new ItemStack(Items.emerald, 1, 0);
                itemStack2 = new ItemStack(item, -computeInt22, 0);
            } else {
                itemStack = new ItemStack(Items.emerald, computeInt22, 0);
                itemStack2 = new ItemStack(item, 1, 0);
            }

            merchantRecipeList.add(new MerchantRecipe(itemStack, itemStack2));
        }
    }

    public static int computeInt2(Item item, Random random) {
        Tuple tuple = (Tuple)blacksmithSellingList.get(item);
        return tuple == null
            ? 1
            : (
                (Integer)tuple.getFirst() >= (Integer)tuple.getSecond()
                    ? (Integer)tuple.getFirst()
                    : (Integer)tuple.getFirst() + random.nextInt((Integer)tuple.getSecond() - (Integer)tuple.getFirst())
            );
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 12) {
            this.generateRandomParticles("heart");
        } else if (b == 13) {
            this.generateRandomParticles("angryVillager");
        } else if (b == 14) {
            this.generateRandomParticles("happyVillager");
        } else {
            super.handleHealthUpdate(b);
        }
    }

    public void generateRandomParticles(String str) {
        for (int i = 0; i < 5; i++) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            this.worldObj
                .spawnParticle(
                    str,
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.posY + 1.0 + this.rand.nextFloat() * this.height,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    d,
                    d2,
                    d3
                );
        }
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData) {
        entityLivingData = super.onSpawnWithEgg(entityLivingData);
        this.setProfession(this.worldObj.rand.nextInt(5));
        return entityLivingData;
    }

    public void setLookingForHome() {
        this.isLookingForHome = true;
    }

    public EntityVillager createChild(EntityAgeable entityAgeable) {
        EntityVillager entityVillager = new EntityVillager(this.worldObj);
        entityVillager.onSpawnWithEgg(null);
        return entityVillager;
    }

    @Override
    public boolean allowLeashing() {
        return false;
    }

    static {
        villagersSellingList.put(Items.coal, new Tuple(16, 24));
        villagersSellingList.put(Items.iron_ingot, new Tuple(8, 10));
        villagersSellingList.put(Items.gold_ingot, new Tuple(8, 10));
        villagersSellingList.put(Items.diamond, new Tuple(4, 6));
        villagersSellingList.put(Items.paper, new Tuple(24, 36));
        villagersSellingList.put(Items.book, new Tuple(11, 13));
        villagersSellingList.put(Items.written_book, new Tuple(1, 1));
        villagersSellingList.put(Items.ender_pearl, new Tuple(3, 4));
        villagersSellingList.put(Items.ender_eye, new Tuple(2, 3));
        villagersSellingList.put(Items.porkchop, new Tuple(14, 18));
        villagersSellingList.put(Items.beef, new Tuple(14, 18));
        villagersSellingList.put(Items.chicken, new Tuple(14, 18));
        villagersSellingList.put(Items.cooked_fish, new Tuple(9, 13));
        villagersSellingList.put(Items.wheat_seeds, new Tuple(34, 48));
        villagersSellingList.put(Items.melon_seeds, new Tuple(30, 38));
        villagersSellingList.put(Items.pumpkin_seeds, new Tuple(30, 38));
        villagersSellingList.put(Items.wheat, new Tuple(18, 22));
        villagersSellingList.put(Item.getItemFromBlock(Blocks.wool), new Tuple(14, 22));
        villagersSellingList.put(Items.rotten_flesh, new Tuple(36, 64));
        blacksmithSellingList.put(Items.flint_and_steel, new Tuple(3, 4));
        blacksmithSellingList.put(Items.shears, new Tuple(3, 4));
        blacksmithSellingList.put(Items.iron_sword, new Tuple(7, 11));
        blacksmithSellingList.put(Items.diamond_sword, new Tuple(12, 14));
        blacksmithSellingList.put(Items.iron_axe, new Tuple(6, 8));
        blacksmithSellingList.put(Items.diamond_axe, new Tuple(9, 12));
        blacksmithSellingList.put(Items.iron_pickaxe, new Tuple(7, 9));
        blacksmithSellingList.put(Items.diamond_pickaxe, new Tuple(10, 12));
        blacksmithSellingList.put(Items.iron_shovel, new Tuple(4, 6));
        blacksmithSellingList.put(Items.diamond_shovel, new Tuple(7, 8));
        blacksmithSellingList.put(Items.iron_hoe, new Tuple(4, 6));
        blacksmithSellingList.put(Items.diamond_hoe, new Tuple(7, 8));
        blacksmithSellingList.put(Items.iron_boots, new Tuple(4, 6));
        blacksmithSellingList.put(Items.diamond_boots, new Tuple(7, 8));
        blacksmithSellingList.put(Items.iron_helmet, new Tuple(4, 6));
        blacksmithSellingList.put(Items.diamond_helmet, new Tuple(7, 8));
        blacksmithSellingList.put(Items.iron_chestplate, new Tuple(10, 14));
        blacksmithSellingList.put(Items.diamond_chestplate, new Tuple(16, 19));
        blacksmithSellingList.put(Items.iron_leggings, new Tuple(8, 10));
        blacksmithSellingList.put(Items.diamond_leggings, new Tuple(11, 14));
        blacksmithSellingList.put(Items.chainmail_boots, new Tuple(5, 7));
        blacksmithSellingList.put(Items.chainmail_helmet, new Tuple(5, 7));
        blacksmithSellingList.put(Items.chainmail_chestplate, new Tuple(11, 15));
        blacksmithSellingList.put(Items.chainmail_leggings, new Tuple(9, 11));
        blacksmithSellingList.put(Items.bread, new Tuple(-4, -2));
        blacksmithSellingList.put(Items.melon, new Tuple(-8, -4));
        blacksmithSellingList.put(Items.apple, new Tuple(-8, -4));
        blacksmithSellingList.put(Items.cookie, new Tuple(-10, -7));
        blacksmithSellingList.put(Item.getItemFromBlock(Blocks.glass), new Tuple(-5, -3));
        blacksmithSellingList.put(Item.getItemFromBlock(Blocks.bookshelf), new Tuple(3, 4));
        blacksmithSellingList.put(Items.leather_chestplate, new Tuple(4, 5));
        blacksmithSellingList.put(Items.leather_boots, new Tuple(2, 4));
        blacksmithSellingList.put(Items.leather_helmet, new Tuple(2, 4));
        blacksmithSellingList.put(Items.leather_leggings, new Tuple(2, 4));
        blacksmithSellingList.put(Items.saddle, new Tuple(6, 8));
        blacksmithSellingList.put(Items.experience_bottle, new Tuple(-4, -1));
        blacksmithSellingList.put(Items.redstone, new Tuple(-4, -1));
        blacksmithSellingList.put(Items.compass, new Tuple(10, 12));
        blacksmithSellingList.put(Items.clock, new Tuple(10, 12));
        blacksmithSellingList.put(Item.getItemFromBlock(Blocks.glowstone), new Tuple(-3, -1));
        blacksmithSellingList.put(Items.cooked_porkchop, new Tuple(-7, -5));
        blacksmithSellingList.put(Items.cooked_beef, new Tuple(-7, -5));
        blacksmithSellingList.put(Items.cooked_chicken, new Tuple(-8, -6));
        blacksmithSellingList.put(Items.ender_eye, new Tuple(7, 11));
        blacksmithSellingList.put(Items.arrow, new Tuple(-12, -8));
    }
}
