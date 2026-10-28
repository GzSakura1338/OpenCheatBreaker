package net.minecraft.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ChatComponentTranslation;

public class StatList {
    public static Map oneShotStats = new HashMap();
    public static List allStats = new ArrayList();
    public static List generalStats = new ArrayList();
    public static List itemStats = new ArrayList();
    public static List objectMineStats = new ArrayList();
    public static StatBase leaveGameStat = new StatBasic("stat.leaveGame", new ChatComponentTranslation("stat.leaveGame")).initIndependentStat().registerStat();
    public static StatBase minutesPlayedStat = new StatBasic("stat.playOneMinute", new ChatComponentTranslation("stat.playOneMinute"), StatBase.timeStatType)
        .initIndependentStat()
        .registerStat();
    public static StatBase distanceWalkedStat = new StatBasic("stat.walkOneCm", new ChatComponentTranslation("stat.walkOneCm"), StatBase.distanceStatType)
        .initIndependentStat()
        .registerStat();
    public static StatBase distanceSwumStat = new StatBasic("stat.swimOneCm", new ChatComponentTranslation("stat.swimOneCm"), StatBase.distanceStatType)
        .initIndependentStat()
        .registerStat();
    public static StatBase distanceFallenStat = new StatBasic("stat.fallOneCm", new ChatComponentTranslation("stat.fallOneCm"), StatBase.distanceStatType)
        .initIndependentStat()
        .registerStat();
    public static StatBase distanceClimbedStat = new StatBasic("stat.climbOneCm", new ChatComponentTranslation("stat.climbOneCm"), StatBase.distanceStatType)
        .initIndependentStat()
        .registerStat();
    public static StatBase distanceFlownStat = new StatBasic("stat.flyOneCm", new ChatComponentTranslation("stat.flyOneCm"), StatBase.distanceStatType)
        .initIndependentStat()
        .registerStat();
    public static StatBase distanceDoveStat = new StatBasic("stat.diveOneCm", new ChatComponentTranslation("stat.diveOneCm"), StatBase.distanceStatType)
        .initIndependentStat()
        .registerStat();
    public static StatBase distanceByMinecartStat = new StatBasic(
            "stat.minecartOneCm", new ChatComponentTranslation("stat.minecartOneCm"), StatBase.distanceStatType
        )
        .initIndependentStat()
        .registerStat();
    public static StatBase distanceByBoatStat = new StatBasic("stat.boatOneCm", new ChatComponentTranslation("stat.boatOneCm"), StatBase.distanceStatType)
        .initIndependentStat()
        .registerStat();
    public static StatBase distanceByPigStat = new StatBasic("stat.pigOneCm", new ChatComponentTranslation("stat.pigOneCm"), StatBase.distanceStatType)
        .initIndependentStat()
        .registerStat();
    public static StatBase CM_HORSE = new StatBasic("stat.horseOneCm", new ChatComponentTranslation("stat.horseOneCm"), StatBase.distanceStatType)
        .initIndependentStat()
        .registerStat();
    public static StatBase jumpStat = new StatBasic("stat.jump", new ChatComponentTranslation("stat.jump")).initIndependentStat().registerStat();
    public static StatBase dropStat = new StatBasic("stat.drop", new ChatComponentTranslation("stat.drop")).initIndependentStat().registerStat();
    public static StatBase damageDealtStat = new StatBasic("stat.damageDealt", new ChatComponentTranslation("stat.damageDealt"), StatBase.DAMAGE_PROVIDER)
        .registerStat();
    public static StatBase damageTakenStat = new StatBasic("stat.damageTaken", new ChatComponentTranslation("stat.damageTaken"), StatBase.DAMAGE_PROVIDER)
        .registerStat();
    public static StatBase deathsStat = new StatBasic("stat.deaths", new ChatComponentTranslation("stat.deaths")).registerStat();
    public static StatBase mobKillsStat = new StatBasic("stat.mobKills", new ChatComponentTranslation("stat.mobKills")).registerStat();
    public static StatBase animalsBredStat = new StatBasic("stat.animalsBred", new ChatComponentTranslation("stat.animalsBred")).registerStat();
    public static StatBase playerKillsStat = new StatBasic("stat.playerKills", new ChatComponentTranslation("stat.playerKills")).registerStat();
    public static StatBase fishCaughtStat = new StatBasic("stat.fishCaught", new ChatComponentTranslation("stat.fishCaught")).registerStat();
    public static StatBase JUNK_FISHED = new StatBasic("stat.junkFished", new ChatComponentTranslation("stat.junkFished")).registerStat();
    public static StatBase TREASURE_FISHED = new StatBasic("stat.treasureFished", new ChatComponentTranslation("stat.treasureFished")).registerStat();
    public static StatBase[] mineBlockStatArray = new StatBase[4096];
    public static StatBase[] objectCraftStats = new StatBase[32000];
    public static StatBase[] objectUseStats = new StatBase[32000];
    public static StatBase[] objectBreakStats = new StatBase[32000];

    public static void setup() {
        loadBlockStats();
        initStats();
        loadBreakStats();
        initCraftableStats();
        AchievementList.init();
        EntityList.load();
    }

    public static void initCraftableStats() {
        HashSet set = new HashSet();

        for (IRecipe recipe : (Iterable<IRecipe>)CraftingManager.getInstance().getRecipeList()) {
            if (recipe.getRecipeOutput() != null) {
                set.add(recipe.getRecipeOutput().getItem());
            }
        }

        for (ItemStack itemStack : (Iterable<ItemStack>)FurnaceRecipes.instance().getSmeltingList().values()) {
            set.add(itemStack.getItem());
        }

        for (Item item : (Iterable<Item>)set) {
            if (item != null) {
                int idFromItem = Item.getIdFromItem(item);
                objectCraftStats[idFromItem] = new StatCrafting(
                        "stat.craftItem." + idFromItem, new ChatComponentTranslation("stat.craftItem", new ItemStack(item).toHoverableText()), item
                    )
                    .registerStat();
            }
        }

        replaceAllSimilarBlocks(objectCraftStats);
    }

    public static void loadBlockStats() {
        for (Block block : Block.blockRegistry) {
            if (Item.getItemFromBlock(block) != null) {
                int idFromBlock = Block.getIdFromBlock(block);
                if (block.getEnableStats()) {
                    mineBlockStatArray[idFromBlock] = new StatCrafting(
                            "stat.mineBlock." + idFromBlock,
                            new ChatComponentTranslation("stat.mineBlock", new ItemStack(block).toHoverableText()),
                            Item.getItemFromBlock(block)
                        )
                        .registerStat();
                    objectMineStats.add(mineBlockStatArray[idFromBlock]);
                }
            }
        }

        replaceAllSimilarBlocks(mineBlockStatArray);
    }

    public static void initStats() {
        for (Item item : (Iterable<Item>)(Iterable)Item.itemRegistry) {
            if (item != null) {
                int idFromItem = Item.getIdFromItem(item);
                objectUseStats[idFromItem] = new StatCrafting(
                        "stat.useItem." + idFromItem, new ChatComponentTranslation("stat.useItem", new ItemStack(item).toHoverableText()), item
                    )
                    .registerStat();
                if (!(item instanceof ItemBlock)) {
                    itemStats.add(objectUseStats[idFromItem]);
                }
            }
        }

        replaceAllSimilarBlocks(objectUseStats);
    }

    public static void loadBreakStats() {
        for (Item item : (Iterable<Item>)(Iterable)Item.itemRegistry) {
            if (item != null) {
                int idFromItem = Item.getIdFromItem(item);
                if (item.isDamageable()) {
                    objectBreakStats[idFromItem] = new StatCrafting(
                            "stat.breakItem." + idFromItem, new ChatComponentTranslation("stat.breakItem", new ItemStack(item).toHoverableText()), item
                        )
                        .registerStat();
                }
            }
        }

        replaceAllSimilarBlocks(objectBreakStats);
    }

    public static void replaceAllSimilarBlocks(StatBase[] statBases) {
        process(statBases, Blocks.water, Blocks.flowing_water);
        process(statBases, Blocks.lava, Blocks.flowing_lava);
        process(statBases, Blocks.lit_pumpkin, Blocks.pumpkin);
        process(statBases, Blocks.lit_furnace, Blocks.furnace);
        process(statBases, Blocks.lit_redstone_ore, Blocks.redstone_ore);
        process(statBases, Blocks.powered_repeater, Blocks.unpowered_repeater);
        process(statBases, Blocks.powered_comparator, Blocks.unpowered_comparator);
        process(statBases, Blocks.redstone_torch, Blocks.unlit_redstone_torch);
        process(statBases, Blocks.lit_redstone_lamp, Blocks.redstone_lamp);
        process(statBases, Blocks.red_mushroom, Blocks.brown_mushroom);
        process(statBases, Blocks.double_stone_slab, Blocks.stone_slab);
        process(statBases, Blocks.double_wooden_slab, Blocks.wooden_slab);
        process(statBases, Blocks.grass, Blocks.dirt);
        process(statBases, Blocks.farmland, Blocks.dirt);
    }

    public static void process(StatBase[] statBases, Block block, Block block2) {
        int idFromBlock = Block.getIdFromBlock(block);
        int idFromBlock2 = Block.getIdFromBlock(block2);
        if (statBases[idFromBlock] != null && statBases[idFromBlock2] == null) {
            statBases[idFromBlock2] = statBases[idFromBlock];
        } else {
            allStats.remove(statBases[idFromBlock]);
            objectMineStats.remove(statBases[idFromBlock]);
            generalStats.remove(statBases[idFromBlock]);
            statBases[idFromBlock] = statBases[idFromBlock2];
        }
    }

    public static StatBase findStatBase(EntityEggInfo entityEggInfo) {
        String stringFromID = EntityList.getStringFromID(entityEggInfo.spawnedID);
        return stringFromID == null
            ? null
            : new StatBase("stat.killEntity." + stringFromID, new ChatComponentTranslation("stat.entityKill", new ChatComponentTranslation("entity." + stringFromID + ".name")))
                .registerStat();
    }

    public static StatBase findStatBase2(EntityEggInfo entityEggInfo) {
        String stringFromID = EntityList.getStringFromID(entityEggInfo.spawnedID);
        return stringFromID == null
            ? null
            : new StatBase(
                    "stat.entityKilledBy." + stringFromID, new ChatComponentTranslation("stat.entityKilledBy", new ChatComponentTranslation("entity." + stringFromID + ".name"))
                )
                .registerStat();
    }

    public static StatBase getOneShotStat(String str) {
        return (StatBase)oneShotStats.get(str);
    }
}
