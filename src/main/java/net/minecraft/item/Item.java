package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockWood;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Item {
    public static RegistryNamespaced itemRegistry = new RegistryNamespaced();
    public static UUID itemModifierUUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public CreativeTabs tabToDisplayOn;
    public static Random itemRand = new Random();
    public int maxStackSize = 64;
    public int maxDurability;
    public boolean bFull3D;
    public boolean hasSubtypes;
    public Item containerItem;
    public String potionEffect;
    public String unlocalizedName;
    public IIcon itemIcon;
    public String iconString;

    public static int getIdFromItem(Item item) {
        return item == null ? 0 : itemRegistry.getIDForObject(item);
    }

    public static Item getItemById(int n) {
        return (Item)itemRegistry.getObjectById(n);
    }

    public static Item getItemFromBlock(Block block) {
        return getItemById(Block.getIdFromBlock(block));
    }

    public static void registerItems() {
        itemRegistry.addObject(256, "iron_shovel", new ItemSpade(Item.ToolMaterial.IRON).setUnlocalizedName("shovelIron").setTextureName("iron_shovel"));
        itemRegistry.addObject(257, "iron_pickaxe", new ItemPickaxe(Item.ToolMaterial.IRON).setUnlocalizedName("pickaxeIron").setTextureName("iron_pickaxe"));
        itemRegistry.addObject(258, "iron_axe", new ItemAxe(Item.ToolMaterial.IRON).setUnlocalizedName("hatchetIron").setTextureName("iron_axe"));
        itemRegistry.addObject(259, "flint_and_steel", new ItemFlintAndSteel().setUnlocalizedName("flintAndSteel").setTextureName("flint_and_steel"));
        itemRegistry.addObject(260, "apple", new ItemFood(4, 0.3F, false).setUnlocalizedName("apple").setTextureName("apple"));
        itemRegistry.addObject(261, "bow", new ItemBow().setUnlocalizedName("bow").setTextureName("bow"));
        itemRegistry.addObject(262, "arrow", new Item().setUnlocalizedName("arrow").setCreativeTab(CreativeTabs.tabCombat).setTextureName("arrow"));
        itemRegistry.addObject(263, "coal", new ItemCoal().setUnlocalizedName("coal").setTextureName("coal"));
        itemRegistry.addObject(264, "diamond", new Item().setUnlocalizedName("diamond").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("diamond"));
        itemRegistry.addObject(
            265, "iron_ingot", new Item().setUnlocalizedName("ingotIron").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("iron_ingot")
        );
        itemRegistry.addObject(
            266, "gold_ingot", new Item().setUnlocalizedName("ingotGold").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("gold_ingot")
        );
        itemRegistry.addObject(267, "iron_sword", new ItemSword(Item.ToolMaterial.IRON).setUnlocalizedName("swordIron").setTextureName("iron_sword"));
        itemRegistry.addObject(268, "wooden_sword", new ItemSword(Item.ToolMaterial.WOOD).setUnlocalizedName("swordWood").setTextureName("wood_sword"));
        itemRegistry.addObject(269, "wooden_shovel", new ItemSpade(Item.ToolMaterial.WOOD).setUnlocalizedName("shovelWood").setTextureName("wood_shovel"));
        itemRegistry.addObject(270, "wooden_pickaxe", new ItemPickaxe(Item.ToolMaterial.WOOD).setUnlocalizedName("pickaxeWood").setTextureName("wood_pickaxe"));
        itemRegistry.addObject(271, "wooden_axe", new ItemAxe(Item.ToolMaterial.WOOD).setUnlocalizedName("hatchetWood").setTextureName("wood_axe"));
        itemRegistry.addObject(272, "stone_sword", new ItemSword(Item.ToolMaterial.STONE).setUnlocalizedName("swordStone").setTextureName("stone_sword"));
        itemRegistry.addObject(273, "stone_shovel", new ItemSpade(Item.ToolMaterial.STONE).setUnlocalizedName("shovelStone").setTextureName("stone_shovel"));
        itemRegistry.addObject(274, "stone_pickaxe", new ItemPickaxe(Item.ToolMaterial.STONE).setUnlocalizedName("pickaxeStone").setTextureName("stone_pickaxe"));
        itemRegistry.addObject(275, "stone_axe", new ItemAxe(Item.ToolMaterial.STONE).setUnlocalizedName("hatchetStone").setTextureName("stone_axe"));
        itemRegistry.addObject(276, "diamond_sword", new ItemSword(Item.ToolMaterial.EMERALD).setUnlocalizedName("swordDiamond").setTextureName("diamond_sword"));
        itemRegistry.addObject(
            277, "diamond_shovel", new ItemSpade(Item.ToolMaterial.EMERALD).setUnlocalizedName("shovelDiamond").setTextureName("diamond_shovel")
        );
        itemRegistry.addObject(
            278, "diamond_pickaxe", new ItemPickaxe(Item.ToolMaterial.EMERALD).setUnlocalizedName("pickaxeDiamond").setTextureName("diamond_pickaxe")
        );
        itemRegistry.addObject(279, "diamond_axe", new ItemAxe(Item.ToolMaterial.EMERALD).setUnlocalizedName("hatchetDiamond").setTextureName("diamond_axe"));
        itemRegistry.addObject(280, "stick", new Item().setFull3D().setUnlocalizedName("stick").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("stick"));
        itemRegistry.addObject(281, "bowl", new Item().setUnlocalizedName("bowl").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("bowl"));
        itemRegistry.addObject(282, "mushroom_stew", new ItemSoup(6).setUnlocalizedName("mushroomStew").setTextureName("mushroom_stew"));
        itemRegistry.addObject(283, "golden_sword", new ItemSword(Item.ToolMaterial.GOLD).setUnlocalizedName("swordGold").setTextureName("gold_sword"));
        itemRegistry.addObject(284, "golden_shovel", new ItemSpade(Item.ToolMaterial.GOLD).setUnlocalizedName("shovelGold").setTextureName("gold_shovel"));
        itemRegistry.addObject(285, "golden_pickaxe", new ItemPickaxe(Item.ToolMaterial.GOLD).setUnlocalizedName("pickaxeGold").setTextureName("gold_pickaxe"));
        itemRegistry.addObject(286, "golden_axe", new ItemAxe(Item.ToolMaterial.GOLD).setUnlocalizedName("hatchetGold").setTextureName("gold_axe"));
        itemRegistry.addObject(
            287, "string", new ItemReed(Blocks.tripwire).setUnlocalizedName("string").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("string")
        );
        itemRegistry.addObject(288, "feather", new Item().setUnlocalizedName("feather").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("feather"));
        itemRegistry.addObject(
            289,
            "gunpowder",
            new Item()
                .setUnlocalizedName("sulphur")
                .setPotionEffect(PotionHelper.gunpowderEffect)
                .setCreativeTab(CreativeTabs.tabMaterials)
                .setTextureName("gunpowder")
        );
        itemRegistry.addObject(290, "wooden_hoe", new ItemHoe(Item.ToolMaterial.WOOD).setUnlocalizedName("hoeWood").setTextureName("wood_hoe"));
        itemRegistry.addObject(291, "stone_hoe", new ItemHoe(Item.ToolMaterial.STONE).setUnlocalizedName("hoeStone").setTextureName("stone_hoe"));
        itemRegistry.addObject(292, "iron_hoe", new ItemHoe(Item.ToolMaterial.IRON).setUnlocalizedName("hoeIron").setTextureName("iron_hoe"));
        itemRegistry.addObject(293, "diamond_hoe", new ItemHoe(Item.ToolMaterial.EMERALD).setUnlocalizedName("hoeDiamond").setTextureName("diamond_hoe"));
        itemRegistry.addObject(294, "golden_hoe", new ItemHoe(Item.ToolMaterial.GOLD).setUnlocalizedName("hoeGold").setTextureName("gold_hoe"));
        itemRegistry.addObject(295, "wheat_seeds", new ItemSeeds(Blocks.wheat, Blocks.farmland).setUnlocalizedName("seeds").setTextureName("seeds_wheat"));
        itemRegistry.addObject(296, "wheat", new Item().setUnlocalizedName("wheat").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("wheat"));
        itemRegistry.addObject(297, "bread", new ItemFood(5, 0.6F, false).setUnlocalizedName("bread").setTextureName("bread"));
        itemRegistry.addObject(298, "leather_helmet", new ItemArmor(ArmorMaterial.CLOTH, 0, 0).setUnlocalizedName("helmetCloth").setTextureName("leather_helmet"));
        itemRegistry.addObject(
            299, "leather_chestplate", new ItemArmor(ArmorMaterial.CLOTH, 0, 1).setUnlocalizedName("chestplateCloth").setTextureName("leather_chestplate")
        );
        itemRegistry.addObject(
            300, "leather_leggings", new ItemArmor(ArmorMaterial.CLOTH, 0, 2).setUnlocalizedName("leggingsCloth").setTextureName("leather_leggings")
        );
        itemRegistry.addObject(301, "leather_boots", new ItemArmor(ArmorMaterial.CLOTH, 0, 3).setUnlocalizedName("bootsCloth").setTextureName("leather_boots"));
        itemRegistry.addObject(
            302, "chainmail_helmet", new ItemArmor(ArmorMaterial.CHAIN, 1, 0).setUnlocalizedName("helmetChain").setTextureName("chainmail_helmet")
        );
        itemRegistry.addObject(
            303, "chainmail_chestplate", new ItemArmor(ArmorMaterial.CHAIN, 1, 1).setUnlocalizedName("chestplateChain").setTextureName("chainmail_chestplate")
        );
        itemRegistry.addObject(
            304, "chainmail_leggings", new ItemArmor(ArmorMaterial.CHAIN, 1, 2).setUnlocalizedName("leggingsChain").setTextureName("chainmail_leggings")
        );
        itemRegistry.addObject(
            305, "chainmail_boots", new ItemArmor(ArmorMaterial.CHAIN, 1, 3).setUnlocalizedName("bootsChain").setTextureName("chainmail_boots")
        );
        itemRegistry.addObject(306, "iron_helmet", new ItemArmor(ArmorMaterial.IRON, 2, 0).setUnlocalizedName("helmetIron").setTextureName("iron_helmet"));
        itemRegistry.addObject(
            307, "iron_chestplate", new ItemArmor(ArmorMaterial.IRON, 2, 1).setUnlocalizedName("chestplateIron").setTextureName("iron_chestplate")
        );
        itemRegistry.addObject(308, "iron_leggings", new ItemArmor(ArmorMaterial.IRON, 2, 2).setUnlocalizedName("leggingsIron").setTextureName("iron_leggings"));
        itemRegistry.addObject(309, "iron_boots", new ItemArmor(ArmorMaterial.IRON, 2, 3).setUnlocalizedName("bootsIron").setTextureName("iron_boots"));
        itemRegistry.addObject(
            310, "diamond_helmet", new ItemArmor(ArmorMaterial.DIAMOND, 3, 0).setUnlocalizedName("helmetDiamond").setTextureName("diamond_helmet")
        );
        itemRegistry.addObject(
            311, "diamond_chestplate", new ItemArmor(ArmorMaterial.DIAMOND, 3, 1).setUnlocalizedName("chestplateDiamond").setTextureName("diamond_chestplate")
        );
        itemRegistry.addObject(
            312, "diamond_leggings", new ItemArmor(ArmorMaterial.DIAMOND, 3, 2).setUnlocalizedName("leggingsDiamond").setTextureName("diamond_leggings")
        );
        itemRegistry.addObject(
            313, "diamond_boots", new ItemArmor(ArmorMaterial.DIAMOND, 3, 3).setUnlocalizedName("bootsDiamond").setTextureName("diamond_boots")
        );
        itemRegistry.addObject(314, "golden_helmet", new ItemArmor(ArmorMaterial.GOLD, 4, 0).setUnlocalizedName("helmetGold").setTextureName("gold_helmet"));
        itemRegistry.addObject(
            315, "golden_chestplate", new ItemArmor(ArmorMaterial.GOLD, 4, 1).setUnlocalizedName("chestplateGold").setTextureName("gold_chestplate")
        );
        itemRegistry.addObject(316, "golden_leggings", new ItemArmor(ArmorMaterial.GOLD, 4, 2).setUnlocalizedName("leggingsGold").setTextureName("gold_leggings"));
        itemRegistry.addObject(317, "golden_boots", new ItemArmor(ArmorMaterial.GOLD, 4, 3).setUnlocalizedName("bootsGold").setTextureName("gold_boots"));
        itemRegistry.addObject(318, "flint", new Item().setUnlocalizedName("flint").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("flint"));
        itemRegistry.addObject(319, "porkchop", new ItemFood(3, 0.3F, true).setUnlocalizedName("porkchopRaw").setTextureName("porkchop_raw"));
        itemRegistry.addObject(320, "cooked_porkchop", new ItemFood(8, 0.8F, true).setUnlocalizedName("porkchopCooked").setTextureName("porkchop_cooked"));
        itemRegistry.addObject(321, "painting", new ItemHangingEntity(EntityPainting.class).setUnlocalizedName("painting").setTextureName("painting"));
        itemRegistry.addObject(
            322,
            "golden_apple",
            new ItemAppleGold(4, 1.2F, false)
                .setAlwaysEdible()
                .setPotionEffect(Potion.regeneration.id, 5, 1, 1.0F)
                .setUnlocalizedName("appleGold")
                .setTextureName("apple_golden")
        );
        itemRegistry.addObject(323, "sign", new ItemSign().setUnlocalizedName("sign").setTextureName("sign"));
        itemRegistry.addObject(324, "wooden_door", new ItemDoor(Material.wood).setUnlocalizedName("doorWood").setTextureName("door_wood"));
        Item setTextureName = new ItemBucket(Blocks.air).setUnlocalizedName("bucket").setMaxStackSize(16).setTextureName("bucket_empty");
        itemRegistry.addObject(325, "bucket", setTextureName);
        itemRegistry.addObject(
            326, "water_bucket", new ItemBucket(Blocks.flowing_water).setUnlocalizedName("bucketWater").setContainerItem(setTextureName).setTextureName("bucket_water")
        );
        itemRegistry.addObject(
            327, "lava_bucket", new ItemBucket(Blocks.flowing_lava).setUnlocalizedName("bucketLava").setContainerItem(setTextureName).setTextureName("bucket_lava")
        );
        itemRegistry.addObject(328, "minecart", new ItemMinecart(0).setUnlocalizedName("minecart").setTextureName("minecart_normal"));
        itemRegistry.addObject(329, "saddle", new ItemSaddle().setUnlocalizedName("saddle").setTextureName("saddle"));
        itemRegistry.addObject(330, "iron_door", new ItemDoor(Material.iron).setUnlocalizedName("doorIron").setTextureName("door_iron"));
        itemRegistry.addObject(
            331, "redstone", new ItemRedstone().setUnlocalizedName("redstone").setPotionEffect(PotionHelper.redstoneEffect).setTextureName("redstone_dust")
        );
        itemRegistry.addObject(332, "snowball", new ItemSnowball().setUnlocalizedName("snowball").setTextureName("snowball"));
        itemRegistry.addObject(333, "boat", new ItemBoat().setUnlocalizedName("boat").setTextureName("boat"));
        itemRegistry.addObject(334, "leather", new Item().setUnlocalizedName("leather").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("leather"));
        itemRegistry.addObject(335, "milk_bucket", new ItemBucketMilk().setUnlocalizedName("milk").setContainerItem(setTextureName).setTextureName("bucket_milk"));
        itemRegistry.addObject(336, "brick", new Item().setUnlocalizedName("brick").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("brick"));
        itemRegistry.addObject(337, "clay_ball", new Item().setUnlocalizedName("clay").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("clay_ball"));
        itemRegistry.addObject(
            338, "reeds", new ItemReed(Blocks.reeds).setUnlocalizedName("reeds").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("reeds")
        );
        itemRegistry.addObject(339, "paper", new Item().setUnlocalizedName("paper").setCreativeTab(CreativeTabs.tabMisc).setTextureName("paper"));
        itemRegistry.addObject(340, "book", new ItemBook().setUnlocalizedName("book").setCreativeTab(CreativeTabs.tabMisc).setTextureName("book_normal"));
        itemRegistry.addObject(341, "slime_ball", new Item().setUnlocalizedName("slimeball").setCreativeTab(CreativeTabs.tabMisc).setTextureName("slimeball"));
        itemRegistry.addObject(342, "chest_minecart", new ItemMinecart(1).setUnlocalizedName("minecartChest").setTextureName("minecart_chest"));
        itemRegistry.addObject(343, "furnace_minecart", new ItemMinecart(2).setUnlocalizedName("minecartFurnace").setTextureName("minecart_furnace"));
        itemRegistry.addObject(344, "egg", new ItemEgg().setUnlocalizedName("egg").setTextureName("egg"));
        itemRegistry.addObject(345, "compass", new Item().setUnlocalizedName("compass").setCreativeTab(CreativeTabs.tabTools).setTextureName("compass"));
        itemRegistry.addObject(346, "fishing_rod", new ItemFishingRod().setUnlocalizedName("fishingRod").setTextureName("fishing_rod"));
        itemRegistry.addObject(347, "clock", new Item().setUnlocalizedName("clock").setCreativeTab(CreativeTabs.tabTools).setTextureName("clock"));
        itemRegistry.addObject(
            348,
            "glowstone_dust",
            new Item()
                .setUnlocalizedName("yellowDust")
                .setPotionEffect(PotionHelper.glowstoneEffect)
                .setCreativeTab(CreativeTabs.tabMaterials)
                .setTextureName("glowstone_dust")
        );
        itemRegistry.addObject(349, "fish", new ItemFishFood(false).setUnlocalizedName("fish").setTextureName("fish_raw").setHasSubtypes(true));
        itemRegistry.addObject(350, "cooked_fished", new ItemFishFood(true).setUnlocalizedName("fish").setTextureName("fish_cooked").setHasSubtypes(true));
        itemRegistry.addObject(351, "dye", new ItemDye().setUnlocalizedName("dyePowder").setTextureName("dye_powder"));
        itemRegistry.addObject(352, "bone", new Item().setUnlocalizedName("bone").setFull3D().setCreativeTab(CreativeTabs.tabMisc).setTextureName("bone"));
        itemRegistry.addObject(
            353,
            "sugar",
            new Item().setUnlocalizedName("sugar").setPotionEffect(PotionHelper.sugarEffect).setCreativeTab(CreativeTabs.tabMaterials).setTextureName("sugar")
        );
        itemRegistry.addObject(
            354, "cake", new ItemReed(Blocks.cake).setMaxStackSize(1).setUnlocalizedName("cake").setCreativeTab(CreativeTabs.tabFood).setTextureName("cake")
        );
        itemRegistry.addObject(355, "bed", new ItemBed().setMaxStackSize(1).setUnlocalizedName("bed").setTextureName("bed"));
        itemRegistry.addObject(
            356,
            "repeater",
            new ItemReed(Blocks.unpowered_repeater).setUnlocalizedName("diode").setCreativeTab(CreativeTabs.tabRedstone).setTextureName("repeater")
        );
        itemRegistry.addObject(357, "cookie", new ItemFood(2, 0.1F, false).setUnlocalizedName("cookie").setTextureName("cookie"));
        itemRegistry.addObject(358, "filled_map", new ItemMap().setUnlocalizedName("map").setTextureName("map_filled"));
        itemRegistry.addObject(359, "shears", new ItemShears().setUnlocalizedName("shears").setTextureName("shears"));
        itemRegistry.addObject(360, "melon", new ItemFood(2, 0.3F, false).setUnlocalizedName("melon").setTextureName("melon"));
        itemRegistry.addObject(
            361, "pumpkin_seeds", new ItemSeeds(Blocks.pumpkin_stem, Blocks.farmland).setUnlocalizedName("seeds_pumpkin").setTextureName("seeds_pumpkin")
        );
        itemRegistry.addObject(
            362, "melon_seeds", new ItemSeeds(Blocks.melon_stem, Blocks.farmland).setUnlocalizedName("seeds_melon").setTextureName("seeds_melon")
        );
        itemRegistry.addObject(363, "beef", new ItemFood(3, 0.3F, true).setUnlocalizedName("beefRaw").setTextureName("beef_raw"));
        itemRegistry.addObject(364, "cooked_beef", new ItemFood(8, 0.8F, true).setUnlocalizedName("beefCooked").setTextureName("beef_cooked"));
        itemRegistry.addObject(
            365,
            "chicken",
            new ItemFood(2, 0.3F, true).setPotionEffect(Potion.hunger.id, 30, 0, 0.3F).setUnlocalizedName("chickenRaw").setTextureName("chicken_raw")
        );
        itemRegistry.addObject(366, "cooked_chicken", new ItemFood(6, 0.6F, true).setUnlocalizedName("chickenCooked").setTextureName("chicken_cooked"));
        itemRegistry.addObject(
            367,
            "rotten_flesh",
            new ItemFood(4, 0.1F, true).setPotionEffect(Potion.hunger.id, 30, 0, 0.8F).setUnlocalizedName("rottenFlesh").setTextureName("rotten_flesh")
        );
        itemRegistry.addObject(368, "ender_pearl", new ItemEnderPearl().setUnlocalizedName("enderPearl").setTextureName("ender_pearl"));
        itemRegistry.addObject(369, "blaze_rod", new Item().setUnlocalizedName("blazeRod").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("blaze_rod"));
        itemRegistry.addObject(
            370,
            "ghast_tear",
            new Item().setUnlocalizedName("ghastTear").setPotionEffect("+0-1-2-3&4-4+13").setCreativeTab(CreativeTabs.tabBrewing).setTextureName("ghast_tear")
        );
        itemRegistry.addObject(
            371, "gold_nugget", new Item().setUnlocalizedName("goldNugget").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("gold_nugget")
        );
        itemRegistry.addObject(
            372,
            "nether_wart",
            new ItemSeeds(Blocks.nether_wart, Blocks.soul_sand).setUnlocalizedName("netherStalkSeeds").setPotionEffect("+4").setTextureName("nether_wart")
        );
        itemRegistry.addObject(373, "potion", new ItemPotion().setUnlocalizedName("potion").setTextureName("potion"));
        itemRegistry.addObject(374, "glass_bottle", new ItemGlassBottle().setUnlocalizedName("glassBottle").setTextureName("potion_bottle_empty"));
        itemRegistry.addObject(
            375,
            "spider_eye",
            new ItemFood(2, 0.8F, false)
                .setPotionEffect(Potion.poison.id, 5, 0, 1.0F)
                .setUnlocalizedName("spiderEye")
                .setPotionEffect(PotionHelper.spiderEyeEffect)
                .setTextureName("spider_eye")
        );
        itemRegistry.addObject(
            376,
            "fermented_spider_eye",
            new Item()
                .setUnlocalizedName("fermentedSpiderEye")
                .setPotionEffect(PotionHelper.fermentedSpiderEyeEffect)
                .setCreativeTab(CreativeTabs.tabBrewing)
                .setTextureName("spider_eye_fermented")
        );
        itemRegistry.addObject(
            377,
            "blaze_powder",
            new Item()
                .setUnlocalizedName("blazePowder")
                .setPotionEffect(PotionHelper.blazePowderEffect)
                .setCreativeTab(CreativeTabs.tabBrewing)
                .setTextureName("blaze_powder")
        );
        itemRegistry.addObject(
            378,
            "magma_cream",
            new Item()
                .setUnlocalizedName("magmaCream")
                .setPotionEffect(PotionHelper.magmaCreamEffect)
                .setCreativeTab(CreativeTabs.tabBrewing)
                .setTextureName("magma_cream")
        );
        itemRegistry.addObject(
            379,
            "brewing_stand",
            new ItemReed(Blocks.brewing_stand).setUnlocalizedName("brewingStand").setCreativeTab(CreativeTabs.tabBrewing).setTextureName("brewing_stand")
        );
        itemRegistry.addObject(
            380, "cauldron", new ItemReed(Blocks.cauldron).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.tabBrewing).setTextureName("cauldron")
        );
        itemRegistry.addObject(381, "ender_eye", new ItemEnderEye().setUnlocalizedName("eyeOfEnder").setTextureName("ender_eye"));
        itemRegistry.addObject(
            382,
            "speckled_melon",
            new Item()
                .setUnlocalizedName("speckledMelon")
                .setPotionEffect(PotionHelper.speckledMelonEffect)
                .setCreativeTab(CreativeTabs.tabBrewing)
                .setTextureName("melon_speckled")
        );
        itemRegistry.addObject(383, "spawn_egg", new ItemMonsterPlacer().setUnlocalizedName("monsterPlacer").setTextureName("spawn_egg"));
        itemRegistry.addObject(384, "experience_bottle", new ItemExpBottle().setUnlocalizedName("expBottle").setTextureName("experience_bottle"));
        itemRegistry.addObject(385, "fire_charge", new ItemFireball().setUnlocalizedName("fireball").setTextureName("fireball"));
        itemRegistry.addObject(
            386, "writable_book", new ItemWritableBook().setUnlocalizedName("writingBook").setCreativeTab(CreativeTabs.tabMisc).setTextureName("book_writable")
        );
        itemRegistry.addObject(387, "written_book", new ItemEditableBook().setUnlocalizedName("writtenBook").setTextureName("book_written").setMaxStackSize(16));
        itemRegistry.addObject(388, "emerald", new Item().setUnlocalizedName("emerald").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("emerald"));
        itemRegistry.addObject(389, "item_frame", new ItemHangingEntity(EntityItemFrame.class).setUnlocalizedName("frame").setTextureName("item_frame"));
        itemRegistry.addObject(
            390,
            "flower_pot",
            new ItemReed(Blocks.flower_pot).setUnlocalizedName("flowerPot").setCreativeTab(CreativeTabs.tabDecorations).setTextureName("flower_pot")
        );
        itemRegistry.addObject(391, "carrot", new ItemSeedFood(4, 0.6F, Blocks.carrots, Blocks.farmland).setUnlocalizedName("carrots").setTextureName("carrot"));
        itemRegistry.addObject(392, "potato", new ItemSeedFood(1, 0.3F, Blocks.potatoes, Blocks.farmland).setUnlocalizedName("potato").setTextureName("potato"));
        itemRegistry.addObject(393, "baked_potato", new ItemFood(6, 0.6F, false).setUnlocalizedName("potatoBaked").setTextureName("potato_baked"));
        itemRegistry.addObject(
            394,
            "poisonous_potato",
            new ItemFood(2, 0.3F, false).setPotionEffect(Potion.poison.id, 5, 0, 0.6F).setUnlocalizedName("potatoPoisonous").setTextureName("potato_poisonous")
        );
        itemRegistry.addObject(395, "map", new ItemEmptyMap().setUnlocalizedName("emptyMap").setTextureName("map_empty"));
        itemRegistry.addObject(
            396,
            "golden_carrot",
            new ItemFood(6, 1.2F, false).setUnlocalizedName("carrotGolden").setPotionEffect(PotionHelper.goldenCarrotEffect).setTextureName("carrot_golden")
        );
        itemRegistry.addObject(397, "skull", new ItemSkull().setUnlocalizedName("skull").setTextureName("skull"));
        itemRegistry.addObject(398, "carrot_on_a_stick", new ItemCarrotOnAStick().setUnlocalizedName("carrotOnAStick").setTextureName("carrot_on_a_stick"));
        itemRegistry.addObject(
            399, "nether_star", new ItemSimpleFoiled().setUnlocalizedName("netherStar").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("nether_star")
        );
        itemRegistry.addObject(
            400, "pumpkin_pie", new ItemFood(8, 0.3F, false).setUnlocalizedName("pumpkinPie").setCreativeTab(CreativeTabs.tabFood).setTextureName("pumpkin_pie")
        );
        itemRegistry.addObject(401, "fireworks", new ItemFirework().setUnlocalizedName("fireworks").setTextureName("fireworks"));
        itemRegistry.addObject(
            402,
            "firework_charge",
            new ItemFireworkCharge().setUnlocalizedName("fireworksCharge").setCreativeTab(CreativeTabs.tabMisc).setTextureName("fireworks_charge")
        );
        itemRegistry.addObject(
            403, "enchanted_book", new ItemEnchantedBook().setMaxStackSize(1).setUnlocalizedName("enchantedBook").setTextureName("book_enchanted")
        );
        itemRegistry.addObject(
            404,
            "comparator",
            new ItemReed(Blocks.unpowered_comparator).setUnlocalizedName("comparator").setCreativeTab(CreativeTabs.tabRedstone).setTextureName("comparator")
        );
        itemRegistry.addObject(
            405, "netherbrick", new Item().setUnlocalizedName("netherbrick").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("netherbrick")
        );
        itemRegistry.addObject(406, "quartz", new Item().setUnlocalizedName("netherquartz").setCreativeTab(CreativeTabs.tabMaterials).setTextureName("quartz"));
        itemRegistry.addObject(407, "tnt_minecart", new ItemMinecart(3).setUnlocalizedName("minecartTnt").setTextureName("minecart_tnt"));
        itemRegistry.addObject(408, "hopper_minecart", new ItemMinecart(5).setUnlocalizedName("minecartHopper").setTextureName("minecart_hopper"));
        itemRegistry.addObject(
            417,
            "iron_horse_armor",
            new Item().setUnlocalizedName("horsearmormetal").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc).setTextureName("iron_horse_armor")
        );
        itemRegistry.addObject(
            418,
            "golden_horse_armor",
            new Item().setUnlocalizedName("horsearmorgold").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc).setTextureName("gold_horse_armor")
        );
        itemRegistry.addObject(
            419,
            "diamond_horse_armor",
            new Item().setUnlocalizedName("horsearmordiamond").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc).setTextureName("diamond_horse_armor")
        );
        itemRegistry.addObject(420, "lead", new ItemLead().setUnlocalizedName("leash").setTextureName("lead"));
        itemRegistry.addObject(421, "name_tag", new ItemNameTag().setUnlocalizedName("nameTag").setTextureName("name_tag"));
        itemRegistry.addObject(
            422,
            "command_block_minecart",
            new ItemMinecart(6).setUnlocalizedName("minecartCommandBlock").setTextureName("minecart_command_block").setCreativeTab(null)
        );
        itemRegistry.addObject(2256, "record_13", new ItemRecord("13").setUnlocalizedName("record").setTextureName("record_13"));
        itemRegistry.addObject(2257, "record_cat", new ItemRecord("cat").setUnlocalizedName("record").setTextureName("record_cat"));
        itemRegistry.addObject(2258, "record_blocks", new ItemRecord("blocks").setUnlocalizedName("record").setTextureName("record_blocks"));
        itemRegistry.addObject(2259, "record_chirp", new ItemRecord("chirp").setUnlocalizedName("record").setTextureName("record_chirp"));
        itemRegistry.addObject(2260, "record_far", new ItemRecord("far").setUnlocalizedName("record").setTextureName("record_far"));
        itemRegistry.addObject(2261, "record_mall", new ItemRecord("mall").setUnlocalizedName("record").setTextureName("record_mall"));
        itemRegistry.addObject(2262, "record_mellohi", new ItemRecord("mellohi").setUnlocalizedName("record").setTextureName("record_mellohi"));
        itemRegistry.addObject(2263, "record_stal", new ItemRecord("stal").setUnlocalizedName("record").setTextureName("record_stal"));
        itemRegistry.addObject(2264, "record_strad", new ItemRecord("strad").setUnlocalizedName("record").setTextureName("record_strad"));
        itemRegistry.addObject(2265, "record_ward", new ItemRecord("ward").setUnlocalizedName("record").setTextureName("record_ward"));
        itemRegistry.addObject(2266, "record_11", new ItemRecord("11").setUnlocalizedName("record").setTextureName("record_11"));
        itemRegistry.addObject(2267, "record_wait", new ItemRecord("wait").setUnlocalizedName("record").setTextureName("record_wait"));
        HashSet newHashSet = Sets.newHashSet(
            new Block[]{
                Blocks.air,
                Blocks.brewing_stand,
                Blocks.bed,
                Blocks.nether_wart,
                Blocks.cauldron,
                Blocks.flower_pot,
                Blocks.wheat,
                Blocks.reeds,
                Blocks.cake,
                Blocks.skull,
                Blocks.piston_head,
                Blocks.piston_extension,
                Blocks.lit_redstone_ore,
                Blocks.powered_repeater,
                Blocks.pumpkin_stem,
                Blocks.standing_sign,
                Blocks.powered_comparator,
                Blocks.tripwire,
                Blocks.lit_redstone_lamp,
                Blocks.melon_stem,
                Blocks.unlit_redstone_torch,
                Blocks.unpowered_comparator,
                Blocks.redstone_wire,
                Blocks.wall_sign,
                Blocks.unpowered_repeater,
                Blocks.iron_door,
                Blocks.wooden_door
            }
        );

        for (String str : (Iterable<String>)Block.blockRegistry.getKeys()) {
            Block object = (Block)Block.blockRegistry.getObject(str);
            ItemBlock itemBlock;
            if (object == Blocks.wool) {
                itemBlock = new ItemCloth(Blocks.wool).setUnlocalizedName("cloth");
            } else if (object == Blocks.stained_hardened_clay) {
                itemBlock = new ItemCloth(Blocks.stained_hardened_clay).setUnlocalizedName("clayHardenedStained");
            } else if (object == Blocks.stained_glass) {
                itemBlock = new ItemCloth(Blocks.stained_glass).setUnlocalizedName("stainedGlass");
            } else if (object == Blocks.stained_glass_pane) {
                itemBlock = new ItemCloth(Blocks.stained_glass_pane).setUnlocalizedName("stainedGlassPane");
            } else if (object == Blocks.carpet) {
                itemBlock = new ItemCloth(Blocks.carpet).setUnlocalizedName("woolCarpet");
            } else if (object == Blocks.dirt) {
                itemBlock = new ItemMultiTexture(Blocks.dirt, Blocks.dirt, BlockDirt.stringArray).setUnlocalizedName("dirt");
            } else if (object == Blocks.sand) {
                itemBlock = new ItemMultiTexture(Blocks.sand, Blocks.sand, BlockSand.VARIANTS).setUnlocalizedName("sand");
            } else if (object == Blocks.log) {
                itemBlock = new ItemMultiTexture(Blocks.log, Blocks.log, BlockOldLog.stringArray).setUnlocalizedName("log");
            } else if (object == Blocks.log2) {
                itemBlock = new ItemMultiTexture(Blocks.log2, Blocks.log2, BlockNewLog.stringArray).setUnlocalizedName("log");
            } else if (object == Blocks.planks) {
                itemBlock = new ItemMultiTexture(Blocks.planks, Blocks.planks, BlockWood.stringArray).setUnlocalizedName("wood");
            } else if (object == Blocks.monster_egg) {
                itemBlock = new ItemMultiTexture(Blocks.monster_egg, Blocks.monster_egg, BlockSilverfish.stringArray).setUnlocalizedName("monsterStoneEgg");
            } else if (object == Blocks.stonebrick) {
                itemBlock = new ItemMultiTexture(Blocks.stonebrick, Blocks.stonebrick, BlockStoneBrick.stringArray).setUnlocalizedName("stonebricksmooth");
            } else if (object == Blocks.sandstone) {
                itemBlock = new ItemMultiTexture(Blocks.sandstone, Blocks.sandstone, BlockSandStone.VARIANTS).setUnlocalizedName("sandStone");
            } else if (object == Blocks.quartz_block) {
                itemBlock = new ItemMultiTexture(Blocks.quartz_block, Blocks.quartz_block, BlockQuartz.stringArray).setUnlocalizedName("quartzBlock");
            } else if (object == Blocks.stone_slab) {
                itemBlock = new ItemSlab(Blocks.stone_slab, Blocks.stone_slab, Blocks.double_stone_slab, false).setUnlocalizedName("stoneSlab");
            } else if (object == Blocks.double_stone_slab) {
                itemBlock = new ItemSlab(Blocks.double_stone_slab, Blocks.stone_slab, Blocks.double_stone_slab, true).setUnlocalizedName("stoneSlab");
            } else if (object == Blocks.wooden_slab) {
                itemBlock = new ItemSlab(Blocks.wooden_slab, Blocks.wooden_slab, Blocks.double_wooden_slab, false).setUnlocalizedName("woodSlab");
            } else if (object == Blocks.double_wooden_slab) {
                itemBlock = new ItemSlab(Blocks.double_wooden_slab, Blocks.wooden_slab, Blocks.double_wooden_slab, true).setUnlocalizedName("woodSlab");
            } else if (object == Blocks.sapling) {
                itemBlock = new ItemMultiTexture(Blocks.sapling, Blocks.sapling, BlockSapling.VARIANTS).setUnlocalizedName("sapling");
            } else if (object == Blocks.leaves) {
                itemBlock = new ItemLeaves(Blocks.leaves).setUnlocalizedName("leaves");
            } else if (object == Blocks.leaves2) {
                itemBlock = new ItemLeaves(Blocks.leaves2).setUnlocalizedName("leaves");
            } else if (object == Blocks.vine) {
                itemBlock = new ItemColored(Blocks.vine, false);
            } else if (object == Blocks.tallgrass) {
                itemBlock = new ItemColored(Blocks.tallgrass, true).setNamed(new String[]{"shrub", "grass", "fern"});
            } else if (object == Blocks.yellow_flower) {
                itemBlock = new ItemMultiTexture(Blocks.yellow_flower, Blocks.yellow_flower, BlockFlower.stringArray2).setUnlocalizedName("flower");
            } else if (object == Blocks.red_flower) {
                itemBlock = new ItemMultiTexture(Blocks.red_flower, Blocks.red_flower, BlockFlower.stringArray).setUnlocalizedName("rose");
            } else if (object == Blocks.snow_layer) {
                itemBlock = new ItemSnow(Blocks.snow_layer, Blocks.snow_layer);
            } else if (object == Blocks.waterlily) {
                itemBlock = new ItemLilyPad(Blocks.waterlily);
            } else if (object == Blocks.piston) {
                itemBlock = new ItemPiston(Blocks.piston);
            } else if (object == Blocks.sticky_piston) {
                itemBlock = new ItemPiston(Blocks.sticky_piston);
            } else if (object == Blocks.cobblestone_wall) {
                itemBlock = new ItemMultiTexture(Blocks.cobblestone_wall, Blocks.cobblestone_wall, BlockWall.stringArray).setUnlocalizedName("cobbleWall");
            } else if (object == Blocks.anvil) {
                itemBlock = new ItemAnvilBlock(Blocks.anvil).setUnlocalizedName("anvil");
            } else if (object == Blocks.double_plant) {
                itemBlock = new ItemDoublePlant(Blocks.double_plant, Blocks.double_plant, BlockDoublePlant.stringArray).setUnlocalizedName("doublePlant");
            } else {
                if (newHashSet.contains(object)) {
                    continue;
                }

                itemBlock = new ItemBlock(object);
            }

            itemRegistry.addObject(Block.getIdFromBlock(object), str, itemBlock);
        }
    }

    public Item setMaxStackSize(int n) {
        this.maxStackSize = n;
        return this;
    }

    public int getSpriteNumber() {
        return 1;
    }

    public IIcon getIconFromDamage(int n) {
        return this.itemIcon;
    }

    public IIcon getIconIndex(ItemStack itemStack) {
        return this.getIconFromDamage(itemStack.getMetadata());
    }

    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) throws java.io.IOException {
        return false;
    }

    public float getStrVsBlock(ItemStack itemStack, Block block) {
        return 1.0F;
    }

    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) throws java.io.IOException {
        return itemStackIn;
    }

    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return itemStack;
    }

    public int getItemStackLimit() {
        return this.maxStackSize;
    }

    public int getMetadata(int n) {
        return 0;
    }

    public boolean getHasSubtypes() {
        return this.hasSubtypes;
    }

    public Item setHasSubtypes(boolean flag) {
        this.hasSubtypes = flag;
        return this;
    }

    public int getMaxDurability() {
        return this.maxDurability;
    }

    public Item setMaxDurability(int n) {
        this.maxDurability = n;
        return this;
    }

    public boolean isDamageable() {
        return this.maxDurability > 0 && !this.hasSubtypes;
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        return false;
    }

    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, int n, int n2, int n3, EntityLivingBase entityLivingBase) {
        return false;
    }

    public boolean canItemHarvestBlock(Block block) {
        return false;
    }

    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        return false;
    }

    public Item setFull3D() {
        this.bFull3D = true;
        return this;
    }

    public boolean isFull3D() {
        return this.bFull3D;
    }

    public boolean shouldRotateAroundWhenRendering() {
        return false;
    }

    public Item setUnlocalizedName(String str) {
        this.unlocalizedName = str;
        return this;
    }

    public String getUnlocalizedNameInefficiently(ItemStack itemStack) {
        String unlocalizedName = this.getUnlocalizedName(itemStack);
        return unlocalizedName == null ? "" : StatCollector.translateToLocal(unlocalizedName);
    }

    public String getUnlocalizedName() {
        return "item." + this.unlocalizedName;
    }

    public String getUnlocalizedName(ItemStack stack) {
        return "item." + this.unlocalizedName;
    }

    public Item setContainerItem(Item item) {
        this.containerItem = item;
        return this;
    }

    public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemStack) {
        return true;
    }

    public boolean getShareTag() {
        return true;
    }

    public Item getContainerItem() {
        return this.containerItem;
    }

    public boolean hasContainerItem() {
        return this.containerItem != null;
    }

    public int getColorFromItemStack(ItemStack itemStack, int n) {
        return 16777215;
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int n, boolean flag) {
    }

    public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    }

    public boolean isMap() {
        return false;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.none;
    }

    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 0;
    }

    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int n) {
    }

    public Item setPotionEffect(String str) {
        this.potionEffect = str;
        return this;
    }

    public String getPotionEffect(ItemStack itemStack) {
        return this.potionEffect;
    }

    public boolean isPotionIngredient(ItemStack itemStack) {
        return this.getPotionEffect(itemStack) != null;
    }

    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean flag) {
    }

    public String getItemStackDisplayName(ItemStack itemStack) {
        return ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(itemStack) + ".name")).trim();
    }

    public boolean hasEffect(ItemStack itemStack) {
        return itemStack.isItemEnchanted();
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return itemStack.isItemEnchanted() ? EnumRarity.rare : EnumRarity.common;
    }

    public boolean isItemTool(ItemStack itemStack) {
        return this.getItemStackLimit() == 1 && this.isDamageable();
    }

    public MovingObjectPosition getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer player, boolean useLiquids) {
        float f = 1.0F;
        float f2 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f3 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d = player.prevPosX + (player.posX - player.prevPosX) * f;
        double d2 = player.prevPosY + (player.posY - player.prevPosY) * f + 1.62 - player.yOffset;
        double d3 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
        Vec3 createVectorHelper = Vec3.createVectorHelper(d, d2, d3);
        float cos = MathHelper.cos(-f3 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float sin = MathHelper.sin(-f3 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f4 = -MathHelper.cos(-f2 * (float) (Math.PI / 180.0));
        float sin2 = MathHelper.sin(-f2 * (float) (Math.PI / 180.0));
        float f5 = sin * f4;
        float f6 = cos * f4;
        double d4 = 5.0;
        Vec3 addVector = createVectorHelper.addVector(f5 * d4, sin2 * d4, f6 * d4);
        return worldIn.rayTraceBlocks(createVectorHelper, addVector, useLiquids, !useLiquids, false);
    }

    public int getItemEnchantability() {
        return 0;
    }

    public boolean requiresMultipleRenderPasses() {
        return false;
    }

    public IIcon getIconFromDamageForRenderPass(int n, int n2) {
        return this.getIconFromDamage(n);
    }

    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, 0));
    }

    public CreativeTabs getCreativeTab() {
        return this.tabToDisplayOn;
    }

    public Item setCreativeTab(CreativeTabs creativeTabs) {
        this.tabToDisplayOn = creativeTabs;
        return this;
    }

    public boolean canItemEditBlocks() {
        return true;
    }

    public boolean getIsRepairable(ItemStack itemStack, ItemStack itemStack2) {
        return false;
    }

    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(this.getIconString());
    }

    public Multimap getItemAttributeModifiers() {
        return HashMultimap.create();
    }

    public Item setTextureName(String str) {
        this.iconString = str;
        return this;
    }

    public String getIconString() {
        return this.iconString == null ? "MISSING_ICON_ITEM_" + itemRegistry.getIDForObject(this) + "_" + this.unlocalizedName : this.iconString;
    }

    public enum ToolMaterial {
        WOOD("WOOD", 0, 0, 59, 2.0F, 0.0F, 15),
        STONE("STONE", 1, 1, 131, 4.0F, 1.0F, 5),
        IRON("IRON", 2, 2, 250, 6.0F, 2.0F, 14),
        EMERALD("EMERALD", 3, 3, 1561, 8.0F, 3.0F, 10),
        GOLD("GOLD", 4, 0, 32, 12.0F, 0.0F, 22);

        public int harvestLevel;
        public int maxUses;
        public float efficiencyOnProperMaterial;
        public float damageVsEntity;
        public int enchantability;
        public static Item.ToolMaterial[] toolMaterialArray = new Item.ToolMaterial[]{WOOD, STONE, IRON, EMERALD, GOLD};

        public int getMaxUses() {
            return this.maxUses;
        }

        public float getEfficiencyOnProperMaterial() {
            return this.efficiencyOnProperMaterial;
        }

        public float getDamageVsEntity() {
            return this.damageVsEntity;
        }

        public int getHarvestLevel() {
            return this.harvestLevel;
        }

        public int getEnchantability() {
            return this.enchantability;
        }

        public Item getBaseItemForRepair() {
            return this == WOOD
                ? Item.getItemFromBlock(Blocks.planks)
                : (
                    this == STONE
                        ? Item.getItemFromBlock(Blocks.cobblestone)
                        : (this == GOLD ? Items.gold_ingot : (this == IRON ? Items.iron_ingot : (this == EMERALD ? Items.diamond : null)))
                );
        }

        ToolMaterial(String str, int n, int n2, int n3, float f, float f2, int n4) {
            this.harvestLevel = n2;
            this.maxUses = n3;
            this.efficiencyOnProperMaterial = f;
            this.damageVsEntity = f2;
            this.enchantability = n4;
        }
    }
}
