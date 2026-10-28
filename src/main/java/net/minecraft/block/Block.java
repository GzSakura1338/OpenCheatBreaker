package net.minecraft.block;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.AddCollisionBoxesEvent;
import com.cheatbreaker.client.event.BlockLightValueEvent;
import com.cheatbreaker.client.event.BlockRenderPassEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Block {
    public static RegistryNamespaced blockRegistry = new RegistryNamespacedDefaultedByKey("air");
    public CreativeTabs displayOnCreativeTab;
    public String textureName;
    public static Block.SoundType soundTypeStone = new Block.SoundType("stone", 1.0F, 1.0F);
    public static Block.SoundType soundTypeWood = new Block.SoundType("wood", 1.0F, 1.0F);
    public static Block.SoundType soundTypeGravel = new Block.SoundType("gravel", 1.0F, 1.0F);
    public static Block.SoundType soundTypeGrass = new Block.SoundType("grass", 1.0F, 1.0F);
    public static Block.SoundType soundTypePiston = new Block.SoundType("stone", 1.0F, 1.0F);
    public static Block.SoundType soundTypeMetal = new Block.SoundType("stone", 1.0F, 1.5F);
    public static Block.SoundType soundTypeGlass = new Block.SoundType("stone", 1.0F, 1.0F) {
        @Override
        public String getDigResourcePath() {
            return "dig.glass";
        }

        @Override
        public String getPlaceSound() {
            return "step.stone";
        }
    };
    public static Block.SoundType soundTypeCloth = new Block.SoundType("cloth", 1.0F, 1.0F);
    public static Block.SoundType soundTypeSand = new Block.SoundType("sand", 1.0F, 1.0F);
    public static Block.SoundType soundTypeSnow = new Block.SoundType("snow", 1.0F, 1.0F);
    public static Block.SoundType soundTypeLadder = new Block.SoundType("ladder", 1.0F, 1.0F) {
        @Override
        public String getDigResourcePath() {
            return "dig.wood";
        }
    };
    public static Block.SoundType soundTypeAnvil = new Block.SoundType("anvil", 0.3F, 1.0F) {
        @Override
        public String getDigResourcePath() {
            return "dig.stone";
        }

        @Override
        public String getPlaceSound() {
            return "random.anvil_land";
        }
    };
    public boolean blockConstructorCalled = true;
    public boolean enableStats = true;
    public Block.SoundType stepSound = soundTypeStone;
    public int lightValue;
    public boolean useNeighborBrightness;
    public float blockHardness;
    public float blockResistance;
    public float blockParticleGravity = 1.0F;
    public float slipperiness = 0.6F;
    public boolean needsRandomTick;
    public boolean isBlockContainer;
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;
    public Material blockMaterial;
    public boolean fullBlock;
    public int lightOpacity;
    public boolean translucent;
    public String unlocalizedName;
    public IIcon blockIcon;
    public static IIcon iIcon;

    public static int getIdFromBlock(Block blockIn) {
        return blockRegistry.getIDForObject(blockIn);
    }

    public static Block getBlockById(int id) {
        return (Block)blockRegistry.getObjectById(id);
    }

    public static Block getBlockFromItem(Item itemIn) {
        return getBlockById(Item.getIdFromItem(itemIn));
    }

    public static Block getBlockFromName(String name) {
        if (blockRegistry.containsKey(name)) {
            return (Block)blockRegistry.getObject(name);
        }

        try {
            return (Block)blockRegistry.getObjectById(Integer.parseInt(name));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean isFullBlock() {
        return this.fullBlock;
    }

    public int getLightOpacity() {
        return this.lightOpacity;
    }

    public boolean isTranslucent() {
        return this.translucent;
    }

    public int getLightValue() {
        BlockLightValueEvent blockLightValueEvent = new BlockLightValueEvent(getIdFromBlock(this), this.lightValue);
        Optional.ofNullable(CheatBreaker.getInstance()).ifPresent(cheatBreaker -> cheatBreaker.getEventBus().handleEvent(blockLightValueEvent));
        return blockLightValueEvent.getLightValue();
    }

    public boolean getUseNeighborBrightness() {
        return this.useNeighborBrightness;
    }

    public Material getMaterial() {
        return this.blockMaterial;
    }

    public MapColor getMapColor(int meta) {
        return this.getMaterial().getMaterialMapColor();
    }

    public static void registerBlocks() {
        blockRegistry.addObject(0, "air", new BlockAir().setUnlocalizedName("air"));
        blockRegistry.addObject(
            1, "stone", new BlockStone().setHardness(1.5F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stone").setTextureName("stone")
        );
        blockRegistry.addObject(2, "grass", new BlockGrass().setHardness(0.6F).setStepSound(soundTypeGrass).setUnlocalizedName("grass").setTextureName("grass"));
        blockRegistry.addObject(3, "dirt", new BlockDirt().setHardness(0.5F).setStepSound(soundTypeGravel).setUnlocalizedName("dirt").setTextureName("dirt"));
        Block setTextureName = new Block(Material.rock)
            .setHardness(2.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("stonebrick")
            .setCreativeTab(CreativeTabs.tabBlock)
            .setTextureName("cobblestone");
        blockRegistry.addObject(4, "cobblestone", setTextureName);
        Block setTextureName2 = new BlockWood().setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("wood").setTextureName("planks");
        blockRegistry.addObject(5, "planks", setTextureName2);
        blockRegistry.addObject(
            6, "sapling", new BlockSapling().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("sapling").setTextureName("sapling")
        );
        blockRegistry.addObject(
            7,
            "bedrock",
            new Block(Material.rock)
                .setBlockUnbreakable()
                .setResistance(6000000.0F)
                .setStepSound(soundTypePiston)
                .setUnlocalizedName("bedrock")
                .disableStats()
                .setCreativeTab(CreativeTabs.tabBlock)
                .setTextureName("bedrock")
        );
        blockRegistry.addObject(
            8,
            "flowing_water",
            new BlockDynamicLiquid(Material.water).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats().setTextureName("water_flow")
        );
        blockRegistry.addObject(
            9,
            "water",
            new BlockStaticLiquid(Material.water).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats().setTextureName("water_still")
        );
        blockRegistry.addObject(
            10,
            "flowing_lava",
            new BlockDynamicLiquid(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setUnlocalizedName("lava").disableStats().setTextureName("lava_flow")
        );
        blockRegistry.addObject(
            11,
            "lava",
            new BlockStaticLiquid(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setUnlocalizedName("lava").disableStats().setTextureName("lava_still")
        );
        blockRegistry.addObject(12, "sand", new BlockSand().setHardness(0.5F).setStepSound(soundTypeSand).setUnlocalizedName("sand").setTextureName("sand"));
        blockRegistry.addObject(
            13, "gravel", new BlockGravel().setHardness(0.6F).setStepSound(soundTypeGravel).setUnlocalizedName("gravel").setTextureName("gravel")
        );
        blockRegistry.addObject(
            14,
            "gold_ore",
            new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreGold").setTextureName("gold_ore")
        );
        blockRegistry.addObject(
            15,
            "iron_ore",
            new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreIron").setTextureName("iron_ore")
        );
        blockRegistry.addObject(
            16,
            "coal_ore",
            new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreCoal").setTextureName("coal_ore")
        );
        blockRegistry.addObject(17, "log", new BlockOldLog().setUnlocalizedName("log").setTextureName("log"));
        blockRegistry.addObject(18, "leaves", new BlockOldLeaf().setUnlocalizedName("leaves").setTextureName("leaves"));
        blockRegistry.addObject(
            19, "sponge", new BlockSponge().setHardness(0.6F).setStepSound(soundTypeGrass).setUnlocalizedName("sponge").setTextureName("sponge")
        );
        blockRegistry.addObject(
            20, "glass", new BlockGlass(Material.glass, false).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("glass").setTextureName("glass")
        );
        blockRegistry.addObject(
            21,
            "lapis_ore",
            new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreLapis").setTextureName("lapis_ore")
        );
        blockRegistry.addObject(
            22,
            "lapis_block",
            new BlockCompressed(MapColor.lapisColor)
                .setHardness(3.0F)
                .setResistance(5.0F)
                .setStepSound(soundTypePiston)
                .setUnlocalizedName("blockLapis")
                .setCreativeTab(CreativeTabs.tabBlock)
                .setTextureName("lapis_block")
        );
        blockRegistry.addObject(
            23, "dispenser", new BlockDispenser().setHardness(3.5F).setStepSound(soundTypePiston).setUnlocalizedName("dispenser").setTextureName("dispenser")
        );
        Block setTextureName3 = new BlockSandStone().setStepSound(soundTypePiston).setHardness(0.8F).setUnlocalizedName("sandStone").setTextureName("sandstone");
        blockRegistry.addObject(24, "sandstone", setTextureName3);
        blockRegistry.addObject(25, "noteblock", new BlockNote().setHardness(0.8F).setUnlocalizedName("musicBlock").setTextureName("noteblock"));
        blockRegistry.addObject(26, "bed", new BlockBed().setHardness(0.2F).setUnlocalizedName("bed").disableStats().setTextureName("bed"));
        blockRegistry.addObject(
            27,
            "golden_rail",
            new BlockRailPowered().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("goldenRail").setTextureName("rail_golden")
        );
        blockRegistry.addObject(
            28,
            "detector_rail",
            new BlockRailDetector().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("detectorRail").setTextureName("rail_detector")
        );
        blockRegistry.addObject(29, "sticky_piston", new BlockPistonBase(true).setUnlocalizedName("pistonStickyBase"));
        blockRegistry.addObject(30, "web", new BlockWeb().setLightOpacity(1).setHardness(4.0F).setUnlocalizedName("web").setTextureName("web"));
        blockRegistry.addObject(31, "tallgrass", new BlockTallGrass().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("tallgrass"));
        blockRegistry.addObject(
            32, "deadbush", new BlockDeadBush().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("deadbush").setTextureName("deadbush")
        );
        blockRegistry.addObject(33, "piston", new BlockPistonBase(false).setUnlocalizedName("pistonBase"));
        blockRegistry.addObject(34, "piston_head", new BlockPistonExtension());
        blockRegistry.addObject(
            35, "wool", new BlockColored(Material.cloth).setHardness(0.8F).setStepSound(soundTypeCloth).setUnlocalizedName("cloth").setTextureName("wool_colored")
        );
        blockRegistry.addObject(36, "piston_extension", new BlockPistonMoving());
        blockRegistry.addObject(
            37,
            "yellow_flower",
            new BlockFlower(0).setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("flower1").setTextureName("flower_dandelion")
        );
        blockRegistry.addObject(
            38, "red_flower", new BlockFlower(1).setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("flower2").setTextureName("flower_rose")
        );
        blockRegistry.addObject(
            39,
            "brown_mushroom",
            new BlockMushroom()
                .setHardness(0.0F)
                .setStepSound(soundTypeGrass)
                .setLightLevel(0.125F)
                .setUnlocalizedName("mushroom")
                .setTextureName("mushroom_brown")
        );
        blockRegistry.addObject(
            40, "red_mushroom", new BlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("mushroom").setTextureName("mushroom_red")
        );
        blockRegistry.addObject(
            41,
            "gold_block",
            new BlockCompressed(MapColor.goldColor)
                .setHardness(3.0F)
                .setResistance(10.0F)
                .setStepSound(soundTypeMetal)
                .setUnlocalizedName("blockGold")
                .setTextureName("gold_block")
        );
        blockRegistry.addObject(
            42,
            "iron_block",
            new BlockCompressed(MapColor.ironColor)
                .setHardness(5.0F)
                .setResistance(10.0F)
                .setStepSound(soundTypeMetal)
                .setUnlocalizedName("blockIron")
                .setTextureName("iron_block")
        );
        blockRegistry.addObject(
            43, "double_stone_slab", new BlockStoneSlab(true).setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stoneSlab")
        );
        blockRegistry.addObject(
            44, "stone_slab", new BlockStoneSlab(false).setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stoneSlab")
        );
        Block setTextureName4 = new Block(Material.rock)
            .setHardness(2.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("brick")
            .setCreativeTab(CreativeTabs.tabBlock)
            .setTextureName("brick");
        blockRegistry.addObject(45, "brick_block", setTextureName4);
        blockRegistry.addObject(46, "tnt", new BlockTNT().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("tnt").setTextureName("tnt"));
        blockRegistry.addObject(
            47, "bookshelf", new BlockBookshelf().setHardness(1.5F).setStepSound(soundTypeWood).setUnlocalizedName("bookshelf").setTextureName("bookshelf")
        );
        blockRegistry.addObject(
            48,
            "mossy_cobblestone",
            new Block(Material.rock)
                .setHardness(2.0F)
                .setResistance(10.0F)
                .setStepSound(soundTypePiston)
                .setUnlocalizedName("stoneMoss")
                .setCreativeTab(CreativeTabs.tabBlock)
                .setTextureName("cobblestone_mossy")
        );
        blockRegistry.addObject(
            49,
            "obsidian",
            new BlockObsidian().setHardness(50.0F).setResistance(2000.0F).setStepSound(soundTypePiston).setUnlocalizedName("obsidian").setTextureName("obsidian")
        );
        blockRegistry.addObject(
            50,
            "torch",
            new BlockTorch().setHardness(0.0F).setLightLevel(0.9375F).setStepSound(soundTypeWood).setUnlocalizedName("torch").setTextureName("torch_on")
        );
        blockRegistry.addObject(
            51,
            "fire",
            new BlockFire().setHardness(0.0F).setLightLevel(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("fire").disableStats().setTextureName("fire")
        );
        blockRegistry.addObject(
            52,
            "mob_spawner",
            new BlockMobSpawner().setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("mobSpawner").disableStats().setTextureName("mob_spawner")
        );
        blockRegistry.addObject(53, "oak_stairs", new BlockStairs(setTextureName2, 0).setUnlocalizedName("stairsWood"));
        blockRegistry.addObject(54, "chest", new BlockChest(0).setHardness(2.5F).setStepSound(soundTypeWood).setUnlocalizedName("chest"));
        blockRegistry.addObject(
            55,
            "redstone_wire",
            new BlockRedstoneWire()
                .setHardness(0.0F)
                .setStepSound(soundTypeStone)
                .setUnlocalizedName("redstoneDust")
                .disableStats()
                .setTextureName("redstone_dust")
        );
        blockRegistry.addObject(
            56,
            "diamond_ore",
            new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreDiamond").setTextureName("diamond_ore")
        );
        blockRegistry.addObject(
            57,
            "diamond_block",
            new BlockCompressed(MapColor.diamondColor)
                .setHardness(5.0F)
                .setResistance(10.0F)
                .setStepSound(soundTypeMetal)
                .setUnlocalizedName("blockDiamond")
                .setTextureName("diamond_block")
        );
        blockRegistry.addObject(
            58,
            "crafting_table",
            new BlockWorkbench().setHardness(2.5F).setStepSound(soundTypeWood).setUnlocalizedName("workbench").setTextureName("crafting_table")
        );
        blockRegistry.addObject(59, "wheat", new BlockCrops().setUnlocalizedName("crops").setTextureName("wheat"));
        Block setTextureName5 = new BlockFarmland().setHardness(0.6F).setStepSound(soundTypeGravel).setUnlocalizedName("farmland").setTextureName("farmland");
        blockRegistry.addObject(60, "farmland", setTextureName5);
        blockRegistry.addObject(
            61,
            "furnace",
            new BlockFurnace(false).setHardness(3.5F).setStepSound(soundTypePiston).setUnlocalizedName("furnace").setCreativeTab(CreativeTabs.tabDecorations)
        );
        blockRegistry.addObject(
            62, "lit_furnace", new BlockFurnace(true).setHardness(3.5F).setStepSound(soundTypePiston).setLightLevel(0.875F).setUnlocalizedName("furnace")
        );
        blockRegistry.addObject(
            63, "standing_sign", new BlockSign(TileEntitySign.class, true).setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("sign").disableStats()
        );
        blockRegistry.addObject(
            64,
            "wooden_door",
            new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorWood").disableStats().setTextureName("door_wood")
        );
        blockRegistry.addObject(
            65, "ladder", new BlockLadder().setHardness(0.4F).setStepSound(soundTypeLadder).setUnlocalizedName("ladder").setTextureName("ladder")
        );
        blockRegistry.addObject(
            66, "rail", new BlockRail().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("rail").setTextureName("rail_normal")
        );
        blockRegistry.addObject(67, "stone_stairs", new BlockStairs(setTextureName, 0).setUnlocalizedName("stairsStone"));
        blockRegistry.addObject(
            68, "wall_sign", new BlockSign(TileEntitySign.class, false).setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("sign").disableStats()
        );
        blockRegistry.addObject(69, "lever", new BlockLever().setHardness(0.5F).setStepSound(soundTypeWood).setUnlocalizedName("lever").setTextureName("lever"));
        blockRegistry.addObject(
            70,
            "stone_pressure_plate",
            new BlockPressurePlate("stone", Material.rock, Sensitivity.mobs).setHardness(0.5F).setStepSound(soundTypePiston).setUnlocalizedName("pressurePlate")
        );
        blockRegistry.addObject(
            71,
            "iron_door",
            new BlockDoor(Material.iron).setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("doorIron").disableStats().setTextureName("door_iron")
        );
        blockRegistry.addObject(
            72,
            "wooden_pressure_plate",
            new BlockPressurePlate("planks_oak", Material.wood, Sensitivity.everything)
                .setHardness(0.5F)
                .setStepSound(soundTypeWood)
                .setUnlocalizedName("pressurePlate")
        );
        blockRegistry.addObject(
            73,
            "redstone_ore",
            new BlockRedstoneOre(false)
                .setHardness(3.0F)
                .setResistance(5.0F)
                .setStepSound(soundTypePiston)
                .setUnlocalizedName("oreRedstone")
                .setCreativeTab(CreativeTabs.tabBlock)
                .setTextureName("redstone_ore")
        );
        blockRegistry.addObject(
            74,
            "lit_redstone_ore",
            new BlockRedstoneOre(true)
                .setLightLevel(0.625F)
                .setHardness(3.0F)
                .setResistance(5.0F)
                .setStepSound(soundTypePiston)
                .setUnlocalizedName("oreRedstone")
                .setTextureName("redstone_ore")
        );
        blockRegistry.addObject(
            75,
            "unlit_redstone_torch",
            new BlockRedstoneTorch(false).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("notGate").setTextureName("redstone_torch_off")
        );
        blockRegistry.addObject(
            76,
            "redstone_torch",
            new BlockRedstoneTorch(true)
                .setHardness(0.0F)
                .setLightLevel(0.5F)
                .setStepSound(soundTypeWood)
                .setUnlocalizedName("notGate")
                .setCreativeTab(CreativeTabs.tabRedstone)
                .setTextureName("redstone_torch_on")
        );
        blockRegistry.addObject(77, "stone_button", new BlockButtonStone().setHardness(0.5F).setStepSound(soundTypePiston).setUnlocalizedName("button"));
        blockRegistry.addObject(
            78, "snow_layer", new BlockSnow().setHardness(0.1F).setStepSound(soundTypeSnow).setUnlocalizedName("snow").setLightOpacity(0).setTextureName("snow")
        );
        blockRegistry.addObject(
            79, "ice", new BlockIce().setHardness(0.5F).setLightOpacity(3).setStepSound(soundTypeGlass).setUnlocalizedName("ice").setTextureName("ice")
        );
        blockRegistry.addObject(80, "snow", new BlockSnowBlock().setHardness(0.2F).setStepSound(soundTypeSnow).setUnlocalizedName("snow").setTextureName("snow"));
        blockRegistry.addObject(
            81, "cactus", new BlockCactus().setHardness(0.4F).setStepSound(soundTypeCloth).setUnlocalizedName("cactus").setTextureName("cactus")
        );
        blockRegistry.addObject(82, "clay", new BlockClay().setHardness(0.6F).setStepSound(soundTypeGravel).setUnlocalizedName("clay").setTextureName("clay"));
        blockRegistry.addObject(
            83, "reeds", new BlockReed().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("reeds").disableStats().setTextureName("reeds")
        );
        blockRegistry.addObject(
            84,
            "jukebox",
            new BlockJukebox().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("jukebox").setTextureName("jukebox")
        );
        blockRegistry.addObject(
            85, "fence", new BlockFence("planks_oak", Material.wood).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("fence")
        );
        Block setTextureName6 = new BlockPumpkin(false).setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("pumpkin").setTextureName("pumpkin");
        blockRegistry.addObject(86, "pumpkin", setTextureName6);
        blockRegistry.addObject(
            87, "netherrack", new BlockNetherrack().setHardness(0.4F).setStepSound(soundTypePiston).setUnlocalizedName("hellrock").setTextureName("netherrack")
        );
        blockRegistry.addObject(
            88, "soul_sand", new BlockSoulSand().setHardness(0.5F).setStepSound(soundTypeSand).setUnlocalizedName("hellsand").setTextureName("soul_sand")
        );
        blockRegistry.addObject(
            89,
            "glowstone",
            new BlockGlowstone(Material.glass)
                .setHardness(0.3F)
                .setStepSound(soundTypeGlass)
                .setLightLevel(1.0F)
                .setUnlocalizedName("lightgem")
                .setTextureName("glowstone")
        );
        blockRegistry.addObject(
            90,
            "portal",
            new BlockPortal().setHardness(-1.0F).setStepSound(soundTypeGlass).setLightLevel(0.75F).setUnlocalizedName("portal").setTextureName("portal")
        );
        blockRegistry.addObject(
            91,
            "lit_pumpkin",
            new BlockPumpkin(true).setHardness(1.0F).setStepSound(soundTypeWood).setLightLevel(1.0F).setUnlocalizedName("litpumpkin").setTextureName("pumpkin")
        );
        blockRegistry.addObject(
            92, "cake", new BlockCake().setHardness(0.5F).setStepSound(soundTypeCloth).setUnlocalizedName("cake").disableStats().setTextureName("cake")
        );
        blockRegistry.addObject(
            93,
            "unpowered_repeater",
            new BlockRedstoneRepeater(false)
                .setHardness(0.0F)
                .setStepSound(soundTypeWood)
                .setUnlocalizedName("diode")
                .disableStats()
                .setTextureName("repeater_off")
        );
        blockRegistry.addObject(
            94,
            "powered_repeater",
            new BlockRedstoneRepeater(true)
                .setHardness(0.0F)
                .setLightLevel(0.625F)
                .setStepSound(soundTypeWood)
                .setUnlocalizedName("diode")
                .disableStats()
                .setTextureName("repeater_on")
        );
        blockRegistry.addObject(
            95,
            "stained_glass",
            new BlockStainedGlass(Material.glass).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("stainedGlass").setTextureName("glass")
        );
        blockRegistry.addObject(
            96,
            "trapdoor",
            new BlockTrapDoor(Material.wood)
                .setHardness(3.0F)
                .setStepSound(soundTypeWood)
                .setUnlocalizedName("trapdoor")
                .disableStats()
                .setTextureName("trapdoor")
        );
        blockRegistry.addObject(97, "monster_egg", new BlockSilverfish().setHardness(0.75F).setUnlocalizedName("monsterStoneEgg"));
        Block setTextureName7 = new BlockStoneBrick()
            .setHardness(1.5F)
            .setResistance(10.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("stonebricksmooth")
            .setTextureName("stonebrick");
        blockRegistry.addObject(98, "stonebrick", setTextureName7);
        blockRegistry.addObject(
            99,
            "brown_mushroom_block",
            new BlockHugeMushroom(Material.wood, 0).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom").setTextureName("mushroom_block")
        );
        blockRegistry.addObject(
            100,
            "red_mushroom_block",
            new BlockHugeMushroom(Material.wood, 1).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom").setTextureName("mushroom_block")
        );
        blockRegistry.addObject(
            101,
            "iron_bars",
            new BlockPane("iron_bars", "iron_bars", Material.iron, true)
                .setHardness(5.0F)
                .setResistance(10.0F)
                .setStepSound(soundTypeMetal)
                .setUnlocalizedName("fenceIron")
        );
        blockRegistry.addObject(
            102,
            "glass_pane",
            new BlockPane("glass", "glass_pane_top", Material.glass, false).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("thinGlass")
        );
        Block setTextureName8 = new BlockMelon().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("melon").setTextureName("melon");
        blockRegistry.addObject(103, "melon_block", setTextureName8);
        blockRegistry.addObject(
            104,
            "pumpkin_stem",
            new BlockStem(setTextureName6).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("pumpkinStem").setTextureName("pumpkin_stem")
        );
        blockRegistry.addObject(
            105, "melon_stem", new BlockStem(setTextureName8).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("pumpkinStem").setTextureName("melon_stem")
        );
        blockRegistry.addObject(106, "vine", new BlockVine().setHardness(0.2F).setStepSound(soundTypeGrass).setUnlocalizedName("vine").setTextureName("vine"));
        blockRegistry.addObject(
            107, "fence_gate", new BlockFenceGate().setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("fenceGate")
        );
        blockRegistry.addObject(108, "brick_stairs", new BlockStairs(setTextureName4, 0).setUnlocalizedName("stairsBrick"));
        blockRegistry.addObject(109, "stone_brick_stairs", new BlockStairs(setTextureName7, 0).setUnlocalizedName("stairsStoneBrickSmooth"));
        blockRegistry.addObject(
            110, "mycelium", new BlockMycelium().setHardness(0.6F).setStepSound(soundTypeGrass).setUnlocalizedName("mycel").setTextureName("mycelium")
        );
        blockRegistry.addObject(
            111, "waterlily", new BlockLilyPad().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("waterlily").setTextureName("waterlily")
        );
        Block setTextureName9 = new Block(Material.rock)
            .setHardness(2.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("netherBrick")
            .setCreativeTab(CreativeTabs.tabBlock)
            .setTextureName("nether_brick");
        blockRegistry.addObject(112, "nether_brick", setTextureName9);
        blockRegistry.addObject(
            113,
            "nether_brick_fence",
            new BlockFence("nether_brick", Material.rock).setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("netherFence")
        );
        blockRegistry.addObject(114, "nether_brick_stairs", new BlockStairs(setTextureName9, 0).setUnlocalizedName("stairsNetherBrick"));
        blockRegistry.addObject(115, "nether_wart", new BlockNetherWart().setUnlocalizedName("netherStalk").setTextureName("nether_wart"));
        blockRegistry.addObject(
            116,
            "enchanting_table",
            new BlockEnchantmentTable().setHardness(5.0F).setResistance(2000.0F).setUnlocalizedName("enchantmentTable").setTextureName("enchanting_table")
        );
        blockRegistry.addObject(
            117,
            "brewing_stand",
            new BlockBrewingStand().setHardness(0.5F).setLightLevel(0.125F).setUnlocalizedName("brewingStand").setTextureName("brewing_stand")
        );
        blockRegistry.addObject(118, "cauldron", new BlockCauldron().setHardness(2.0F).setUnlocalizedName("cauldron").setTextureName("cauldron"));
        blockRegistry.addObject(119, "end_portal", new BlockEndPortal(Material.portal).setHardness(-1.0F).setResistance(6000000.0F));
        blockRegistry.addObject(
            120,
            "end_portal_frame",
            new BlockEndPortalFrame()
                .setStepSound(soundTypeGlass)
                .setLightLevel(0.125F)
                .setHardness(-1.0F)
                .setUnlocalizedName("endPortalFrame")
                .setResistance(6000000.0F)
                .setCreativeTab(CreativeTabs.tabDecorations)
                .setTextureName("endframe")
        );
        blockRegistry.addObject(
            121,
            "end_stone",
            new Block(Material.rock)
                .setHardness(3.0F)
                .setResistance(15.0F)
                .setStepSound(soundTypePiston)
                .setUnlocalizedName("whiteStone")
                .setCreativeTab(CreativeTabs.tabBlock)
                .setTextureName("end_stone")
        );
        blockRegistry.addObject(
            122,
            "dragon_egg",
            new BlockDragonEgg()
                .setHardness(3.0F)
                .setResistance(15.0F)
                .setStepSound(soundTypePiston)
                .setLightLevel(0.125F)
                .setUnlocalizedName("dragonEgg")
                .setTextureName("dragon_egg")
        );
        blockRegistry.addObject(
            123,
            "redstone_lamp",
            new BlockRedstoneLight(false)
                .setHardness(0.3F)
                .setStepSound(soundTypeGlass)
                .setUnlocalizedName("redstoneLight")
                .setCreativeTab(CreativeTabs.tabRedstone)
                .setTextureName("redstone_lamp_off")
        );
        blockRegistry.addObject(
            124,
            "lit_redstone_lamp",
            new BlockRedstoneLight(true).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("redstoneLight").setTextureName("redstone_lamp_on")
        );
        blockRegistry.addObject(
            125, "double_wooden_slab", new BlockWoodSlab(true).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("woodSlab")
        );
        blockRegistry.addObject(
            126, "wooden_slab", new BlockWoodSlab(false).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("woodSlab")
        );
        blockRegistry.addObject(
            127, "cocoa", new BlockCocoa().setHardness(0.2F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("cocoa").setTextureName("cocoa")
        );
        blockRegistry.addObject(128, "sandstone_stairs", new BlockStairs(setTextureName3, 0).setUnlocalizedName("stairsSandStone"));
        blockRegistry.addObject(
            129,
            "emerald_ore",
            new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreEmerald").setTextureName("emerald_ore")
        );
        blockRegistry.addObject(
            130,
            "ender_chest",
            new BlockEnderChest().setHardness(22.5F).setResistance(1000.0F).setStepSound(soundTypePiston).setUnlocalizedName("enderChest").setLightLevel(0.5F)
        );
        blockRegistry.addObject(131, "tripwire_hook", new BlockTripWireHook().setUnlocalizedName("tripWireSource").setTextureName("trip_wire_source"));
        blockRegistry.addObject(132, "tripwire", new BlockTripWire().setUnlocalizedName("tripWire").setTextureName("trip_wire"));
        blockRegistry.addObject(
            133,
            "emerald_block",
            new BlockCompressed(MapColor.emeraldColor)
                .setHardness(5.0F)
                .setResistance(10.0F)
                .setStepSound(soundTypeMetal)
                .setUnlocalizedName("blockEmerald")
                .setTextureName("emerald_block")
        );
        blockRegistry.addObject(134, "spruce_stairs", new BlockStairs(setTextureName2, 1).setUnlocalizedName("stairsWoodSpruce"));
        blockRegistry.addObject(135, "birch_stairs", new BlockStairs(setTextureName2, 2).setUnlocalizedName("stairsWoodBirch"));
        blockRegistry.addObject(136, "jungle_stairs", new BlockStairs(setTextureName2, 3).setUnlocalizedName("stairsWoodJungle"));
        blockRegistry.addObject(
            137,
            "command_block",
            new BlockCommandBlock().setBlockUnbreakable().setResistance(6000000.0F).setUnlocalizedName("commandBlock").setTextureName("command_block")
        );
        blockRegistry.addObject(138, "beacon", new BlockBeacon().setUnlocalizedName("beacon").setLightLevel(1.0F).setTextureName("beacon"));
        blockRegistry.addObject(139, "cobblestone_wall", new BlockWall(setTextureName).setUnlocalizedName("cobbleWall"));
        blockRegistry.addObject(
            140, "flower_pot", new BlockFlowerPot().setHardness(0.0F).setStepSound(soundTypeStone).setUnlocalizedName("flowerPot").setTextureName("flower_pot")
        );
        blockRegistry.addObject(141, "carrots", new BlockCarrot().setUnlocalizedName("carrots").setTextureName("carrots"));
        blockRegistry.addObject(142, "potatoes", new BlockPotato().setUnlocalizedName("potatoes").setTextureName("potatoes"));
        blockRegistry.addObject(143, "wooden_button", new BlockButtonWood().setHardness(0.5F).setStepSound(soundTypeWood).setUnlocalizedName("button"));
        blockRegistry.addObject(
            144, "skull", new BlockSkull().setHardness(1.0F).setStepSound(soundTypePiston).setUnlocalizedName("skull").setTextureName("skull")
        );
        blockRegistry.addObject(145, "anvil", new BlockAnvil().setHardness(5.0F).setStepSound(soundTypeAnvil).setResistance(2000.0F).setUnlocalizedName("anvil"));
        blockRegistry.addObject(146, "trapped_chest", new BlockChest(1).setHardness(2.5F).setStepSound(soundTypeWood).setUnlocalizedName("chestTrap"));
        blockRegistry.addObject(
            147,
            "light_weighted_pressure_plate",
            new BlockPressurePlateWeighted("gold_block", Material.iron, 15)
                .setHardness(0.5F)
                .setStepSound(soundTypeWood)
                .setUnlocalizedName("weightedPlate_light")
        );
        blockRegistry.addObject(
            148,
            "heavy_weighted_pressure_plate",
            new BlockPressurePlateWeighted("iron_block", Material.iron, 150)
                .setHardness(0.5F)
                .setStepSound(soundTypeWood)
                .setUnlocalizedName("weightedPlate_heavy")
        );
        blockRegistry.addObject(
            149,
            "unpowered_comparator",
            new BlockRedstoneComparator(false)
                .setHardness(0.0F)
                .setStepSound(soundTypeWood)
                .setUnlocalizedName("comparator")
                .disableStats()
                .setTextureName("comparator_off")
        );
        blockRegistry.addObject(
            150,
            "powered_comparator",
            new BlockRedstoneComparator(true)
                .setHardness(0.0F)
                .setLightLevel(0.625F)
                .setStepSound(soundTypeWood)
                .setUnlocalizedName("comparator")
                .disableStats()
                .setTextureName("comparator_on")
        );
        blockRegistry.addObject(
            151,
            "daylight_detector",
            new BlockDaylightDetector().setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("daylightDetector").setTextureName("daylight_detector")
        );
        blockRegistry.addObject(
            152,
            "redstone_block",
            new BlockCompressedPowered(MapColor.tntColor)
                .setHardness(5.0F)
                .setResistance(10.0F)
                .setStepSound(soundTypeMetal)
                .setUnlocalizedName("blockRedstone")
                .setTextureName("redstone_block")
        );
        blockRegistry.addObject(
            153,
            "quartz_ore",
            new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("netherquartz").setTextureName("quartz_ore")
        );
        blockRegistry.addObject(
            154,
            "hopper",
            new BlockHopper().setHardness(3.0F).setResistance(8.0F).setStepSound(soundTypeWood).setUnlocalizedName("hopper").setTextureName("hopper")
        );
        Block setTextureName10 = new BlockQuartz().setStepSound(soundTypePiston).setHardness(0.8F).setUnlocalizedName("quartzBlock").setTextureName("quartz_block");
        blockRegistry.addObject(155, "quartz_block", setTextureName10);
        blockRegistry.addObject(156, "quartz_stairs", new BlockStairs(setTextureName10, 0).setUnlocalizedName("stairsQuartz"));
        blockRegistry.addObject(
            157,
            "activator_rail",
            new BlockRailPowered().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("activatorRail").setTextureName("rail_activator")
        );
        blockRegistry.addObject(
            158, "dropper", new BlockDropper().setHardness(3.5F).setStepSound(soundTypePiston).setUnlocalizedName("dropper").setTextureName("dropper")
        );
        blockRegistry.addObject(
            159,
            "stained_hardened_clay",
            new BlockColored(Material.rock)
                .setHardness(1.25F)
                .setResistance(7.0F)
                .setStepSound(soundTypePiston)
                .setUnlocalizedName("clayHardenedStained")
                .setTextureName("hardened_clay_stained")
        );
        blockRegistry.addObject(
            160,
            "stained_glass_pane",
            new BlockStainedGlassPane().setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("thinStainedGlass").setTextureName("glass")
        );
        blockRegistry.addObject(161, "leaves2", new BlockNewLeaf().setUnlocalizedName("leaves").setTextureName("leaves"));
        blockRegistry.addObject(162, "log2", new BlockNewLog().setUnlocalizedName("log").setTextureName("log"));
        blockRegistry.addObject(163, "acacia_stairs", new BlockStairs(setTextureName2, 4).setUnlocalizedName("stairsWoodAcacia"));
        blockRegistry.addObject(164, "dark_oak_stairs", new BlockStairs(setTextureName2, 5).setUnlocalizedName("stairsWoodDarkOak"));
        blockRegistry.addObject(
            170,
            "hay_block",
            new BlockHay()
                .setHardness(0.5F)
                .setStepSound(soundTypeGrass)
                .setUnlocalizedName("hayBlock")
                .setCreativeTab(CreativeTabs.tabBlock)
                .setTextureName("hay_block")
        );
        blockRegistry.addObject(
            171, "carpet", new BlockCarpet().setHardness(0.1F).setStepSound(soundTypeCloth).setUnlocalizedName("woolCarpet").setLightOpacity(0)
        );
        blockRegistry.addObject(
            172,
            "hardened_clay",
            new BlockHardenedClay()
                .setHardness(1.25F)
                .setResistance(7.0F)
                .setStepSound(soundTypePiston)
                .setUnlocalizedName("clayHardened")
                .setTextureName("hardened_clay")
        );
        blockRegistry.addObject(
            173,
            "coal_block",
            new Block(Material.rock)
                .setHardness(5.0F)
                .setResistance(10.0F)
                .setStepSound(soundTypePiston)
                .setUnlocalizedName("blockCoal")
                .setCreativeTab(CreativeTabs.tabBlock)
                .setTextureName("coal_block")
        );
        blockRegistry.addObject(
            174, "packed_ice", new BlockPackedIce().setHardness(0.5F).setStepSound(soundTypeGlass).setUnlocalizedName("icePacked").setTextureName("ice_packed")
        );
        blockRegistry.addObject(175, "double_plant", new BlockDoublePlant());

        for (Block block : blockRegistry) {
            if (block.blockMaterial == Material.air) {
                block.useNeighborBrightness = false;
            } else {
                boolean flag = false;
                boolean flag2 = block.getRenderType() == 10;
                boolean flag3 = block instanceof BlockSlab;
                boolean flag4 = block == setTextureName5;
                boolean translucent = block.translucent;
                boolean flag5 = block.lightOpacity == 0;
                if (flag2 || flag3 || flag4 || translucent || flag5) {
                    flag = true;
                }

                block.useNeighborBrightness = flag;
            }
        }
    }

    public Block(Material materialIn) {
        this.blockMaterial = materialIn;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.fullBlock = this.isOpaqueCube();
        this.lightOpacity = this.isOpaqueCube() ? 255 : 0;
        this.translucent = !materialIn.blocksLight();
    }

    public Block setStepSound(Block.SoundType sound) {
        this.stepSound = sound;
        return this;
    }

    public Block setLightOpacity(int opacity) {
        this.lightOpacity = opacity;
        return this;
    }

    public Block setLightLevel(float value) {
        this.lightValue = (int)(15.0F * value);
        return this;
    }

    public Block setResistance(float resistance) {
        this.blockResistance = resistance * 3.0F;
        return this;
    }

    public boolean isBlockNormalCube() {
        return this.blockMaterial.blocksMovement() && this.renderAsNormalBlock();
    }

    public boolean isNormalCube() {
        return this.blockMaterial.isOpaque() && this.renderAsNormalBlock() && !this.canProvidePower();
    }

    public boolean renderAsNormalBlock() {
        return true;
    }

    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z) {
        return !this.blockMaterial.blocksMovement();
    }

    public int getRenderType() {
        return 0;
    }

    public Block setHardness(float hardness) {
        this.blockHardness = hardness;
        if (this.blockResistance < hardness * 5.0F) {
            this.blockResistance = hardness * 5.0F;
        }

        return this;
    }

    public Block setBlockUnbreakable() {
        this.setHardness(-1.0F);
        return this;
    }

    public float getBlockHardness(World worldIn, int x, int y, int z) {
        return this.blockHardness;
    }

    public Block setTickRandomly(boolean shouldTick) {
        this.needsRandomTick = shouldTick;
        return this;
    }

    public boolean getTickRandomly() {
        return this.needsRandomTick;
    }

    public boolean hasTileEntity() {
        return this.isBlockContainer;
    }

    public void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public int getMixedBrightnessForBlock(IBlockAccess worldIn, int x, int y, int z) {
        Block block = worldIn.getBlock(x, y, z);
        int lightBrightnessForSkyBlocks = worldIn.getLightBrightnessForSkyBlocks(x, y, z, block.getLightValue());
        if (lightBrightnessForSkyBlocks == 0 && block instanceof BlockSlab) {
            block = worldIn.getBlock(x, --y, z);
            return worldIn.getLightBrightnessForSkyBlocks(x, y, z, block.getLightValue());
        } else {
            return lightBrightnessForSkyBlocks;
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return side == 0 && this.minY > 0.0
            || side == 1 && this.maxY < 1.0
            || side == 2 && this.minZ > 0.0
            || side == 3 && this.maxZ < 1.0
            || side == 4 && this.minX > 0.0
            || side == 5 && this.maxX < 1.0
            || !worldIn.getBlock(x, y, z).isOpaqueCube();
    }

    public boolean isBlockSolid(IBlockAccess worldIn, int x, int y, int z, int side) {
        return worldIn.getBlock(x, y, z).getMaterial().isSolid();
    }

    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        return this.getIcon(side, worldIn.getBlockMetadata(x, y, z));
    }

    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
    }

    public IIcon getBlockTextureFromSide(int side) {
        return this.getIcon(side, 0);
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        AxisAlignedBB collisionBoundingBoxFromPool = this.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
        AddCollisionBoxesEvent addCollisionBoxesEvent = new AddCollisionBoxesEvent(list, x, y, z);
        CheatBreaker.getInstance().getEventBus().handleEvent(addCollisionBoxesEvent);
        if (!addCollisionBoxesEvent.isCancelled() && collisionBoundingBoxFromPool != null && mask.intersectsWith(collisionBoundingBoxFromPool)) {
            list.add(collisionBoundingBoxFromPool);
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    public boolean isOpaqueCube() {
        return true;
    }

    public boolean canStopRayTrace(int meta, boolean includeLiquid) {
        return this.isCollidable();
    }

    public boolean isCollidable() {
        return true;
    }

    public void updateTick(World worldIn, int x, int y, int z, Random random) {
    }

    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
    }

    public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta) {
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
    }

    public int tickRate(World worldIn) {
        return 10;
    }

    public void onBlockAdded(World worldIn, int x, int y, int z) {
    }

    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
    }

    public int quantityDropped(Random random) {
        return 1;
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }

    public float getPlayerRelativeBlockHardness(EntityPlayer player, World worldIn, int x, int y, int z) {
        float blockHardness = this.getBlockHardness(worldIn, x, y, z);
        return blockHardness < 0.0F
            ? 0.0F
            : (!player.canHarvestBlock(this) ? player.getBreakSpeed(this, false) / blockHardness / 100.0F : player.getBreakSpeed(this, true) / blockHardness / 30.0F);
    }

    public void dropBlockAsItem(World worldIn, int x, int y, int z, int meta, int fortune) {
        this.dropBlockAsItemWithChance(worldIn, x, y, z, meta, 1.0F, fortune);
    }

    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        if (!worldIn.isRemote) {
            int quantityDroppedWithBonus = this.quantityDroppedWithBonus(fortune, worldIn.rand);

            for (int i = 0; i < quantityDroppedWithBonus; i++) {
                if (worldIn.rand.nextFloat() <= chance) {
                    Item itemDropped = this.getItemDropped(meta, worldIn.rand, fortune);
                    if (itemDropped != null) {
                        this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(itemDropped, 1, this.damageDropped(meta)));
                    }
                }
            }
        }
    }

    public void dropBlockAsItem(World worldIn, int x, int y, int z, ItemStack itemIn) {
        if (!worldIn.isRemote && worldIn.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
            float f = 0.7F;
            double d = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5;
            double d2 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5;
            double d3 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5;
            EntityItem entityItem = new EntityItem(worldIn, x + d, y + d2, z + d3, itemIn);
            entityItem.delayBeforeCanPickup = 10;
            worldIn.spawnEntityInWorld(entityItem);
        }
    }

    public void dropXpOnBlockBreak(World worldIn, int x, int y, int z, int amount) {
        if (!worldIn.isRemote) {
            while (amount > 0) {
                int xPSplit = EntityXPOrb.getXPSplit(amount);
                amount -= xPSplit;
                worldIn.spawnEntityInWorld(new EntityXPOrb(worldIn, x + 0.5, y + 0.5, z + 0.5, xPSplit));
            }
        }
    }

    public int damageDropped(int meta) {
        return 0;
    }

    public float getExplosionResistance(Entity exploder) {
        return this.blockResistance / 5.0F;
    }

    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        startVec = startVec.addVector(-x, -y, -z);
        endVec = endVec.addVector(-x, -y, -z);
        Vec3 intermediateWithXValue = startVec.getIntermediateWithXValue(endVec, this.minX);
        Vec3 intermediateWithXValue2 = startVec.getIntermediateWithXValue(endVec, this.maxX);
        Vec3 intermediateWithYValue = startVec.getIntermediateWithYValue(endVec, this.minY);
        Vec3 intermediateWithYValue2 = startVec.getIntermediateWithYValue(endVec, this.maxY);
        Vec3 intermediateWithZValue = startVec.getIntermediateWithZValue(endVec, this.minZ);
        Vec3 intermediateWithZValue2 = startVec.getIntermediateWithZValue(endVec, this.maxZ);
        if (!this.isVecInsideYZBounds(intermediateWithXValue)) {
            intermediateWithXValue = null;
        }

        if (!this.isVecInsideYZBounds(intermediateWithXValue2)) {
            intermediateWithXValue2 = null;
        }

        if (!this.isVecInsideXZBounds(intermediateWithYValue)) {
            intermediateWithYValue = null;
        }

        if (!this.isVecInsideXZBounds(intermediateWithYValue2)) {
            intermediateWithYValue2 = null;
        }

        if (!this.isVecInsideXYBounds(intermediateWithZValue)) {
            intermediateWithZValue = null;
        }

        if (!this.isVecInsideXYBounds(intermediateWithZValue2)) {
            intermediateWithZValue2 = null;
        }

        Vec3 vec3 = null;
        if (intermediateWithXValue != null && (vec3 == null || startVec.squareDistanceTo(intermediateWithXValue) < startVec.squareDistanceTo(vec3))) {
            vec3 = intermediateWithXValue;
        }

        if (intermediateWithXValue2 != null && (vec3 == null || startVec.squareDistanceTo(intermediateWithXValue2) < startVec.squareDistanceTo(vec3))) {
            vec3 = intermediateWithXValue2;
        }

        if (intermediateWithYValue != null && (vec3 == null || startVec.squareDistanceTo(intermediateWithYValue) < startVec.squareDistanceTo(vec3))) {
            vec3 = intermediateWithYValue;
        }

        if (intermediateWithYValue2 != null && (vec3 == null || startVec.squareDistanceTo(intermediateWithYValue2) < startVec.squareDistanceTo(vec3))) {
            vec3 = intermediateWithYValue2;
        }

        if (intermediateWithZValue != null && (vec3 == null || startVec.squareDistanceTo(intermediateWithZValue) < startVec.squareDistanceTo(vec3))) {
            vec3 = intermediateWithZValue;
        }

        if (intermediateWithZValue2 != null && (vec3 == null || startVec.squareDistanceTo(intermediateWithZValue2) < startVec.squareDistanceTo(vec3))) {
            vec3 = intermediateWithZValue2;
        }

        if (vec3 == null) {
            return null;
        }

        byte b = -1;
        if (vec3 == intermediateWithXValue) {
            b = 4;
        }

        if (vec3 == intermediateWithXValue2) {
            b = 5;
        }

        if (vec3 == intermediateWithYValue) {
            b = 0;
        }

        if (vec3 == intermediateWithYValue2) {
            b = 1;
        }

        if (vec3 == intermediateWithZValue) {
            b = 2;
        }

        if (vec3 == intermediateWithZValue2) {
            b = 3;
        }

        return new MovingObjectPosition(x, y, z, b, vec3.addVector(x, y, z));
    }

    public boolean isVecInsideYZBounds(Vec3 point) {
        return point != null && point.yCoord >= this.minY && point.yCoord <= this.maxY && point.zCoord >= this.minZ && point.zCoord <= this.maxZ;
    }

    public boolean isVecInsideXZBounds(Vec3 point) {
        return point != null && point.xCoord >= this.minX && point.xCoord <= this.maxX && point.zCoord >= this.minZ && point.zCoord <= this.maxZ;
    }

    public boolean isVecInsideXYBounds(Vec3 point) {
        return point != null && point.xCoord >= this.minX && point.xCoord <= this.maxX && point.yCoord >= this.minY && point.yCoord <= this.maxY;
    }

    public void onBlockDestroyedByExplosion(World worldIn, int x, int y, int z, Explosion explosionIn) {
    }

    public int getRenderBlockPass() {
        BlockRenderPassEvent blockRenderPassEvent = new BlockRenderPassEvent(getIdFromBlock(this));
        Optional.ofNullable(CheatBreaker.getInstance()).ifPresent(cheatBreaker -> cheatBreaker.getEventBus().handleEvent(blockRenderPassEvent));
        return blockRenderPassEvent.isCancelled() ? 1 : 0;
    }

    public boolean canReplace(World worldIn, int x, int y, int z, int side, ItemStack itemIn) {
        return this.canPlaceBlockOnSide(worldIn, x, y, z, side);
    }

    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side) {
        return this.canPlaceBlockAt(worldIn, x, y, z);
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return worldIn.getBlock(x, y, z).blockMaterial.isReplaceable();
    }

    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        return false;
    }

    public void onEntityWalking(World worldIn, int x, int y, int z, Entity entityIn) {
    }

    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        return meta;
    }

    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
    }

    public void modifyEntityVelocity(World worldIn, int x, int y, int z, Entity entityIn, Vec3 velocity) {
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
    }

    public double getBlockBoundsMinX() {
        return this.minX;
    }

    public double getBlockBoundsMaxX() {
        return this.maxX;
    }

    public double getBlockBoundsMinY() {
        return this.minY;
    }

    public double getBlockBoundsMaxY() {
        return this.maxY;
    }

    public double getBlockBoundsMinZ() {
        return this.minZ;
    }

    public double getBlockBoundsMaxZ() {
        return this.maxZ;
    }

    public int getBlockColor() {
        return 16777215;
    }

    public int getRenderColor(int meta) {
        return 16777215;
    }

    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return 16777215;
    }

    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return 0;
    }

    public boolean canProvidePower() {
        return false;
    }

    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) throws java.io.IOException {
    }

    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return 0;
    }

    public void setBlockBoundsForItemRender() {
    }

    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (this.canSilkHarvest() && EnchantmentHelper.getSilkTouchModifier(player)) {
            ItemStack createStackedBlock = this.createStackedBlock(meta);
            if (createStackedBlock != null) {
                this.dropBlockAsItem(worldIn, x, y, z, createStackedBlock);
            }
        } else {
            int fortuneModifier = EnchantmentHelper.getFortuneModifier(player);
            this.dropBlockAsItem(worldIn, x, y, z, meta, fortuneModifier);
        }
    }

    public boolean canSilkHarvest() {
        return this.renderAsNormalBlock() && !this.isBlockContainer;
    }

    public ItemStack createStackedBlock(int meta) {
        int n = 0;
        Item itemFromBlock = Item.getItemFromBlock(this);
        if (itemFromBlock != null && itemFromBlock.getHasSubtypes()) {
            n = meta;
        }

        return new ItemStack(itemFromBlock, 1, n);
    }

    public int quantityDroppedWithBonus(int maxBonus, Random random) {
        return this.quantityDropped(random);
    }

    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        return true;
    }

    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
    }

    public void onPostBlockPlaced(World worldIn, int x, int y, int z, int meta) {
    }

    public Block setUnlocalizedName(String name) {
        this.unlocalizedName = name;
        return this;
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
    }

    public String getUnlocalizedName() {
        return "tile." + this.unlocalizedName;
    }

    public boolean onBlockEventReceived(World worldIn, int x, int y, int z, int eventId, int eventData) {
        return false;
    }

    public boolean getEnableStats() {
        return this.enableStats;
    }

    public Block disableStats() {
        this.enableStats = false;
        return this;
    }

    public int getMobilityFlag() {
        return this.blockMaterial.getMaterialMobility();
    }

    public float getAmbientOcclusionLightValue() {
        return this.isBlockNormalCube() ? 0.2F : 1.0F;
    }

    public void onFallenUpon(World worldIn, int x, int y, int z, Entity entityIn, float fallDistance) {
    }

    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemFromBlock(this);
    }

    public int getDamageValue(World worldIn, int x, int y, int z) {
        return this.damageDropped(worldIn.getBlockMetadata(x, y, z));
    }

    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, 0));
    }

    public CreativeTabs getCreativeTabToDisplayOn() {
        return this.displayOnCreativeTab;
    }

    public Block setCreativeTab(CreativeTabs tab) {
        this.displayOnCreativeTab = tab;
        return this;
    }

    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player) {
    }

    public void onBlockPreDestroy(World worldIn, int x, int y, int z, int meta) {
    }

    public void fillWithRain(World worldIn, int x, int y, int z) {
    }

    public boolean isFlowerPot() {
        return false;
    }

    public boolean requiresUpdates() {
        return true;
    }

    public boolean canDropFromExplosion(Explosion explosionIn) {
        return true;
    }

    public boolean isAssociatedBlock(Block other) {
        return this == other;
    }

    public static boolean isEqualTo(Block blockIn, Block other) {
        return blockIn != null && other != null && (blockIn == other || blockIn.isAssociatedBlock(other));
    }

    public boolean hasComparatorInputOverride() {
        return false;
    }

    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        return 0;
    }

    public Block setTextureName(String textureName) {
        this.textureName = textureName;
        return this;
    }

    public String getTextureName() {
        return this.textureName == null ? "MISSING_ICON_BLOCK_" + getIdFromBlock(this) + "_" + this.unlocalizedName : this.textureName;
    }

    public IIcon getItemIcon(int side, int meta) {
        return this.getIcon(side, meta);
    }

    public void registerIcons(IIconRegister reg) {
        if (Objects.equals(this.textureName, "trip_wire")) {
            iIcon = reg.registerIcon("trip_wire_full");
        }

        this.blockIcon = reg.registerIcon(this.getTextureName());
    }

    public String getItemIconName() {
        return null;
    }

    public static class SoundType {
        public String soundName;
        public float volume;
        public float frequency;

        public SoundType(String name, float volume, float frequency) {
            this.soundName = name;
            this.volume = volume;
            this.frequency = frequency;
        }

        public float getVolume() {
            return this.volume;
        }

        public float getFrequency() {
            return this.frequency;
        }

        public String getDigResourcePath() {
            return "dig." + this.soundName;
        }

        public String getStepSound() {
            return "step." + this.soundName;
        }

        public String getPlaceSound() {
            return this.getDigResourcePath();
        }
    }
}
