package net.minecraft.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import net.minecraft.util.ResourceLocation;

public class StatList {
   public static Map<String, StatBase> oneShotStats = Maps.newHashMap();
   public static List<StatBase> allStats = Lists.newArrayList();
   public static List<StatBase> generalStats = Lists.newArrayList();
   public static List<StatCrafting> itemStats = Lists.newArrayList();
   public static List<StatCrafting> objectMineStats = Lists.newArrayList();
   public static StatBase leaveGameStat = new StatBasic("stat.leaveGame", new ChatComponentTranslation("stat.leaveGame")).initIndependentStat().registerStat();
   public static StatBase minutesPlayedStat = new StatBasic("stat.playOneMinute", new ChatComponentTranslation("stat.playOneMinute"), StatBase.timeStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase timeSinceDeathStat = new StatBasic("stat.timeSinceDeath", new ChatComponentTranslation("stat.timeSinceDeath"), StatBase.timeStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceWalkedStat = new StatBasic("stat.walkOneCm", new ChatComponentTranslation("stat.walkOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceCrouchedStat = new StatBasic("stat.crouchOneCm", new ChatComponentTranslation("stat.crouchOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceSprintedStat = new StatBasic("stat.sprintOneCm", new ChatComponentTranslation("stat.sprintOneCm"), StatBase.distanceStatType)
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
   public static StatBase distanceByHorseStat = new StatBasic("stat.horseOneCm", new ChatComponentTranslation("stat.horseOneCm"), StatBase.distanceStatType)
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
   public static StatBase junkFishedStat = new StatBasic("stat.junkFished", new ChatComponentTranslation("stat.junkFished")).registerStat();
   public static StatBase treasureFishedStat = new StatBasic("stat.treasureFished", new ChatComponentTranslation("stat.treasureFished")).registerStat();
   public static StatBase timesTalkedToVillagerStat = new StatBasic("stat.talkedToVillager", new ChatComponentTranslation("stat.talkedToVillager"))
      .registerStat();
   public static StatBase timesTradedWithVillagerStat = new StatBasic("stat.tradedWithVillager", new ChatComponentTranslation("stat.tradedWithVillager"))
      .registerStat();
   public static StatBase CAKE_SLICES_EATEN = new StatBasic("stat.cakeSlicesEaten", new ChatComponentTranslation("stat.cakeSlicesEaten")).registerStat();
   public static StatBase CAULDRONS_FILLED = new StatBasic("stat.cauldronFilled", new ChatComponentTranslation("stat.cauldronFilled")).registerStat();
   public static StatBase CAULDRONS_USED = new StatBasic("stat.cauldronUsed", new ChatComponentTranslation("stat.cauldronUsed")).registerStat();
   public static StatBase ARMOR_CLEANED = new StatBasic("stat.armorCleaned", new ChatComponentTranslation("stat.armorCleaned")).registerStat();
   public static StatBase BANNER_CLEANED = new StatBasic("stat.bannerCleaned", new ChatComponentTranslation("stat.bannerCleaned")).registerStat();
   public static StatBase INTERACTIONS_WITH_BREWING_STAND = new StatBasic(
         "stat.brewingstandInteraction", new ChatComponentTranslation("stat.brewingstandInteraction")
      )
      .registerStat();
   public static StatBase INTERACTIONS_WITH_BEACON = new StatBasic("stat.beaconInteraction", new ChatComponentTranslation("stat.beaconInteraction"))
      .registerStat();
   public static StatBase INTERACTIONS_WITH_DROPPER = new StatBasic("stat.dropperInspected", new ChatComponentTranslation("stat.dropperInspected"))
      .registerStat();
   public static StatBase INTERACTIONS_WITH_HOPPER = new StatBasic("stat.hopperInspected", new ChatComponentTranslation("stat.hopperInspected")).registerStat();
   public static StatBase INTERACTIONS_WITH_DISPENSER = new StatBasic("stat.dispenserInspected", new ChatComponentTranslation("stat.dispenserInspected"))
      .registerStat();
   public static StatBase NOTEBLOCK_PLAYED = new StatBasic("stat.noteblockPlayed", new ChatComponentTranslation("stat.noteblockPlayed")).registerStat();
   public static StatBase NOTEBLOCK_TUNED = new StatBasic("stat.noteblockTuned", new ChatComponentTranslation("stat.noteblockTuned")).registerStat();
   public static StatBase FLOWER_POTTED = new StatBasic("stat.flowerPotted", new ChatComponentTranslation("stat.flowerPotted")).registerStat();
   public static StatBase TRAPPED_CHEST_TRIGGERED = new StatBasic("stat.trappedChestTriggered", new ChatComponentTranslation("stat.trappedChestTriggered"))
      .registerStat();
   public static StatBase ENDERCHEST_OPENED = new StatBasic("stat.enderchestOpened", new ChatComponentTranslation("stat.enderchestOpened")).registerStat();
   public static StatBase ITEM_ENCHANTED = new StatBasic("stat.itemEnchanted", new ChatComponentTranslation("stat.itemEnchanted")).registerStat();
   public static StatBase RECORD_PLAYED = new StatBasic("stat.recordPlayed", new ChatComponentTranslation("stat.recordPlayed")).registerStat();
   public static StatBase FURNACE_INTERACTION = new StatBasic("stat.furnaceInteraction", new ChatComponentTranslation("stat.furnaceInteraction"))
      .registerStat();
   public static StatBase INTERACT_WITH_CRAFTING_TABLE = new StatBasic(
         "stat.craftingTableInteraction", new ChatComponentTranslation("stat.workbenchInteraction")
      )
      .registerStat();
   public static StatBase CHEST_OPENED = new StatBasic("stat.chestOpened", new ChatComponentTranslation("stat.chestOpened")).registerStat();
   public static StatBase[] mineBlockStatArray = new StatBase[4096];
   public static StatBase[] objectCraftStats = new StatBase[32000];
   public static StatBase[] objectUseStats = new StatBase[32000];
   public static StatBase[] objectBreakStats = new StatBase[32000];

   public static void init() {
      initMiningStats();
      initStats();
      initItemDepleteStats();
      initCraftableStats();
      AchievementList.init();
      EntityList.load();
   }

   public static void initCraftableStats() {
      HashSet newHashSet = Sets.newHashSet();

      for (IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
         if (recipe.getRecipeOutput() != null) {
            newHashSet.add(recipe.getRecipeOutput().getItem());
         }
      }

      for (ItemStack itemStack : FurnaceRecipes.instance().getSmeltingList().values()) {
         newHashSet.add(itemStack.getItem());
      }

      for (Item item : (Iterable<Item>)newHashSet) {
         if (item != null) {
            int idFromItem = Item.getIdFromItem(item);
            String formatText2 = formatText(item);
            if (formatText2 != null) {
               objectCraftStats[idFromItem] = new StatCrafting(
                     "stat.craftItem.", formatText2, new ChatComponentTranslation("stat.craftItem", new ItemStack(item).getChatComponent()), item
                  )
                  .registerStat();
            }
         }
      }

      replaceAllSimilarBlocks(objectCraftStats);
   }

   public static void initMiningStats() {
      for (Block block : Block.blockRegistry) {
         Item itemFromBlock = Item.getItemFromBlock(block);
         if (itemFromBlock != null) {
            int idFromBlock = Block.getIdFromBlock(block);
            String formatText2 = formatText(itemFromBlock);
            if (formatText2 != null && block.getEnableStats()) {
               mineBlockStatArray[idFromBlock] = new StatCrafting(
                     "stat.mineBlock.", formatText2, new ChatComponentTranslation("stat.mineBlock", new ItemStack(block).getChatComponent()), itemFromBlock
                  )
                  .registerStat();
               objectMineStats.add((StatCrafting)mineBlockStatArray[idFromBlock]);
            }
         }
      }

      replaceAllSimilarBlocks(mineBlockStatArray);
   }

   public static void initStats() {
      for (Item item : Item.itemRegistry) {
         if (item != null) {
            int idFromItem = Item.getIdFromItem(item);
            String formatText2 = formatText(item);
            if (formatText2 != null) {
               objectUseStats[idFromItem] = new StatCrafting(
                     "stat.useItem.", formatText2, new ChatComponentTranslation("stat.useItem", new ItemStack(item).getChatComponent()), item
                  )
                  .registerStat();
               if (!(item instanceof ItemBlock)) {
                  itemStats.add((StatCrafting)objectUseStats[idFromItem]);
               }
            }
         }
      }

      replaceAllSimilarBlocks(objectUseStats);
   }

   public static void initItemDepleteStats() {
      for (Item item : Item.itemRegistry) {
         if (item != null) {
            int idFromItem = Item.getIdFromItem(item);
            String formatText2 = formatText(item);
            if (formatText2 != null && item.isDamageable()) {
               objectBreakStats[idFromItem] = new StatCrafting(
                     "stat.breakItem.", formatText2, new ChatComponentTranslation("stat.breakItem", new ItemStack(item).getChatComponent()), item
                  )
                  .registerStat();
            }
         }
      }

      replaceAllSimilarBlocks(objectBreakStats);
   }

   public static String formatText(Item item) {
      ResourceLocation nameForObject = Item.itemRegistry.getNameForObject(item);
      return nameForObject != null ? nameForObject.toString().replace(':', '.') : null;
   }

   public static void replaceAllSimilarBlocks(StatBase[] statBases) {
      mergeStatBases(statBases, Blocks.water, Blocks.flowing_water);
      mergeStatBases(statBases, Blocks.lava, Blocks.flowing_lava);
      mergeStatBases(statBases, Blocks.lit_pumpkin, Blocks.pumpkin);
      mergeStatBases(statBases, Blocks.lit_furnace, Blocks.furnace);
      mergeStatBases(statBases, Blocks.lit_redstone_ore, Blocks.redstone_ore);
      mergeStatBases(statBases, Blocks.powered_repeater, Blocks.unpowered_repeater);
      mergeStatBases(statBases, Blocks.powered_comparator, Blocks.unpowered_comparator);
      mergeStatBases(statBases, Blocks.redstone_torch, Blocks.unlit_redstone_torch);
      mergeStatBases(statBases, Blocks.lit_redstone_lamp, Blocks.redstone_lamp);
      mergeStatBases(statBases, Blocks.double_stone_slab, Blocks.stone_slab);
      mergeStatBases(statBases, Blocks.double_wooden_slab, Blocks.wooden_slab);
      mergeStatBases(statBases, Blocks.double_stone_slab2, Blocks.stone_slab2);
      mergeStatBases(statBases, Blocks.grass, Blocks.dirt);
      mergeStatBases(statBases, Blocks.farmland, Blocks.dirt);
   }

   public static void mergeStatBases(StatBase[] statBaseIn, Block block, Block block2) {
      int idFromBlock = Block.getIdFromBlock(block);
      int idFromBlock2 = Block.getIdFromBlock(block2);
      if (statBaseIn[idFromBlock] != null && statBaseIn[idFromBlock2] == null) {
         statBaseIn[idFromBlock2] = statBaseIn[idFromBlock];
      } else {
         allStats.remove(statBaseIn[idFromBlock]);
         objectMineStats.remove(statBaseIn[idFromBlock]);
         generalStats.remove(statBaseIn[idFromBlock]);
         statBaseIn[idFromBlock] = statBaseIn[idFromBlock2];
      }
   }

   public static StatBase getStatKillEntity(EntityEggInfo eggInfo) {
      String stringFromID = EntityList.getStringFromID(eggInfo.spawnedID);
      return stringFromID == null
         ? null
         : new StatBase("stat.killEntity." + stringFromID, new ChatComponentTranslation("stat.entityKill", new ChatComponentTranslation("entity." + stringFromID + ".name")))
            .registerStat();
   }

   public static StatBase getStatEntityKilledBy(EntityEggInfo eggInfo) {
      String stringFromID = EntityList.getStringFromID(eggInfo.spawnedID);
      return stringFromID == null
         ? null
         : new StatBase(
               "stat.entityKilledBy." + stringFromID, new ChatComponentTranslation("stat.entityKilledBy", new ChatComponentTranslation("entity." + stringFromID + ".name"))
            )
            .registerStat();
   }

   public static StatBase getOneShotStat(String str) {
      return oneShotStats.get(str);
   }
}
