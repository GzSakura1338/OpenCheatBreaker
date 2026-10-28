package net.minecraft.world.biome;

import com.google.common.collect.Sets;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BiomeGenBase {
    public static Logger logger = LogManager.getLogger();
    public static BiomeGenBase.Height height_Default = new BiomeGenBase.Height(0.1F, 0.2F);
    public static BiomeGenBase.Height height_ShallowWaters = new BiomeGenBase.Height(-0.5F, 0.0F);
    public static BiomeGenBase.Height height_Oceans = new BiomeGenBase.Height(-1.0F, 0.1F);
    public static BiomeGenBase.Height height_DeepOceans = new BiomeGenBase.Height(-1.8F, 0.1F);
    public static BiomeGenBase.Height height_LowPlains = new BiomeGenBase.Height(0.125F, 0.05F);
    public static BiomeGenBase.Height height_MidPlains = new BiomeGenBase.Height(0.2F, 0.2F);
    public static BiomeGenBase.Height height_LowHills = new BiomeGenBase.Height(0.45F, 0.3F);
    public static BiomeGenBase.Height height_HighPlateaus = new BiomeGenBase.Height(1.5F, 0.025F);
    public static BiomeGenBase.Height height_MidHills = new BiomeGenBase.Height(1.0F, 0.5F);
    public static BiomeGenBase.Height height_Shores = new BiomeGenBase.Height(0.0F, 0.025F);
    public static BiomeGenBase.Height height_RockyWaters = new BiomeGenBase.Height(0.1F, 0.8F);
    public static BiomeGenBase.Height height_LowIslands = new BiomeGenBase.Height(0.2F, 0.3F);
    public static BiomeGenBase.Height height_PartiallySubmerged = new BiomeGenBase.Height(-0.2F, 0.1F);
    public static BiomeGenBase[] biomeList = new BiomeGenBase[256];
    public static Set explorationBiomesList = Sets.newHashSet();
    public static BiomeGenBase ocean = new BiomeGenOcean(0).setColor(112).setBiomeName("Ocean").setHeight(height_Oceans);
    public static BiomeGenBase plains = new BiomeGenPlains(1).setColor(9286496).setBiomeName("Plains");
    public static BiomeGenBase desert = new BiomeGenDesert(2)
        .setColor(16421912)
        .setBiomeName("Desert")
        .setDisableRain()
        .setTemperatureRainfall(2.0F, 0.0F)
        .setHeight(height_LowPlains);
    public static BiomeGenBase extremeHills = new BiomeGenHills(3, false)
        .setColor(6316128)
        .setBiomeName("Extreme Hills")
        .setHeight(height_MidHills)
        .setTemperatureRainfall(0.2F, 0.3F);
    public static BiomeGenBase forest = new BiomeGenForest(4, 0).setColor(353825).setBiomeName("Forest");
    public static BiomeGenBase taiga = new BiomeGenTaiga(5, 0)
        .setColor(747097)
        .setBiomeName("Taiga")
        .setFillerBlockMetadata(5159473)
        .setTemperatureRainfall(0.25F, 0.8F)
        .setHeight(height_MidPlains);
    public static BiomeGenBase swampland = new BiomeGenSwamp(6)
        .setColor(522674)
        .setBiomeName("Swampland")
        .setFillerBlockMetadata(9154376)
        .setHeight(height_PartiallySubmerged)
        .setTemperatureRainfall(0.8F, 0.9F);
    public static BiomeGenBase river = new BiomeGenRiver(7).setColor(255).setBiomeName("River").setHeight(height_ShallowWaters);
    public static BiomeGenBase hell = new BiomeGenHell(8).setColor(16711680).setBiomeName("Hell").setDisableRain().setTemperatureRainfall(2.0F, 0.0F);
    public static BiomeGenBase sky = new BiomeGenEnd(9).setColor(8421631).setBiomeName("Sky").setDisableRain();
    public static BiomeGenBase frozenOcean = new BiomeGenOcean(10)
        .setColor(9474208)
        .setBiomeName("FrozenOcean")
        .setEnableSnow()
        .setHeight(height_Oceans)
        .setTemperatureRainfall(0.0F, 0.5F);
    public static BiomeGenBase frozenRiver = new BiomeGenRiver(11)
        .setColor(10526975)
        .setBiomeName("FrozenRiver")
        .setEnableSnow()
        .setHeight(height_ShallowWaters)
        .setTemperatureRainfall(0.0F, 0.5F);
    public static BiomeGenBase icePlains = new BiomeGenSnow(12, false)
        .setColor(16777215)
        .setBiomeName("Ice Plains")
        .setEnableSnow()
        .setTemperatureRainfall(0.0F, 0.5F)
        .setHeight(height_LowPlains);
    public static BiomeGenBase iceMountains = new BiomeGenSnow(13, false)
        .setColor(10526880)
        .setBiomeName("Ice Mountains")
        .setEnableSnow()
        .setHeight(height_LowHills)
        .setTemperatureRainfall(0.0F, 0.5F);
    public static BiomeGenBase mushroomIsland = new BiomeGenMushroomIsland(14)
        .setColor(16711935)
        .setBiomeName("MushroomIsland")
        .setTemperatureRainfall(0.9F, 1.0F)
        .setHeight(height_LowIslands);
    public static BiomeGenBase mushroomIslandShore = new BiomeGenMushroomIsland(15)
        .setColor(10486015)
        .setBiomeName("MushroomIslandShore")
        .setTemperatureRainfall(0.9F, 1.0F)
        .setHeight(height_Shores);
    public static BiomeGenBase beach = new BiomeGenBeach(16)
        .setColor(16440917)
        .setBiomeName("Beach")
        .setTemperatureRainfall(0.8F, 0.4F)
        .setHeight(height_Shores);
    public static BiomeGenBase desertHills = new BiomeGenDesert(17)
        .setColor(13786898)
        .setBiomeName("DesertHills")
        .setDisableRain()
        .setTemperatureRainfall(2.0F, 0.0F)
        .setHeight(height_LowHills);
    public static BiomeGenBase forestHills = new BiomeGenForest(18, 0).setColor(2250012).setBiomeName("ForestHills").setHeight(height_LowHills);
    public static BiomeGenBase taigaHills = new BiomeGenTaiga(19, 0)
        .setColor(1456435)
        .setBiomeName("TaigaHills")
        .setFillerBlockMetadata(5159473)
        .setTemperatureRainfall(0.25F, 0.8F)
        .setHeight(height_LowHills);
    public static BiomeGenBase extremeHillsEdge = new BiomeGenHills(20, true)
        .setColor(7501978)
        .setBiomeName("Extreme Hills Edge")
        .setHeight(height_MidHills.attenuate())
        .setTemperatureRainfall(0.2F, 0.3F);
    public static BiomeGenBase jungle = new BiomeGenJungle(21, false)
        .setColor(5470985)
        .setBiomeName("Jungle")
        .setFillerBlockMetadata(5470985)
        .setTemperatureRainfall(0.95F, 0.9F);
    public static BiomeGenBase jungleHills = new BiomeGenJungle(22, false)
        .setColor(2900485)
        .setBiomeName("JungleHills")
        .setFillerBlockMetadata(5470985)
        .setTemperatureRainfall(0.95F, 0.9F)
        .setHeight(height_LowHills);
    public static BiomeGenBase jungleEdge = new BiomeGenJungle(23, true)
        .setColor(6458135)
        .setBiomeName("JungleEdge")
        .setFillerBlockMetadata(5470985)
        .setTemperatureRainfall(0.95F, 0.8F);
    public static BiomeGenBase deepOcean = new BiomeGenOcean(24).setColor(48).setBiomeName("Deep Ocean").setHeight(height_DeepOceans);
    public static BiomeGenBase stoneBeach = new BiomeGenStoneBeach(25)
        .setColor(10658436)
        .setBiomeName("Stone Beach")
        .setTemperatureRainfall(0.2F, 0.3F)
        .setHeight(height_RockyWaters);
    public static BiomeGenBase coldBeach = new BiomeGenBeach(26)
        .setColor(16445632)
        .setBiomeName("Cold Beach")
        .setTemperatureRainfall(0.05F, 0.3F)
        .setHeight(height_Shores)
        .setEnableSnow();
    public static BiomeGenBase birchForest = new BiomeGenForest(27, 2).setBiomeName("Birch Forest").setColor(3175492);
    public static BiomeGenBase birchForestHills = new BiomeGenForest(28, 2).setBiomeName("Birch Forest Hills").setColor(2055986).setHeight(height_LowHills);
    public static BiomeGenBase roofedForest = new BiomeGenForest(29, 3).setColor(4215066).setBiomeName("Roofed Forest");
    public static BiomeGenBase coldTaiga = new BiomeGenTaiga(30, 0)
        .setColor(3233098)
        .setBiomeName("Cold Taiga")
        .setFillerBlockMetadata(5159473)
        .setEnableSnow()
        .setTemperatureRainfall(-0.5F, 0.4F)
        .setHeight(height_MidPlains)
        .setIntValue(16777215);
    public static BiomeGenBase coldTaigaHills = new BiomeGenTaiga(31, 0)
        .setColor(2375478)
        .setBiomeName("Cold Taiga Hills")
        .setFillerBlockMetadata(5159473)
        .setEnableSnow()
        .setTemperatureRainfall(-0.5F, 0.4F)
        .setHeight(height_LowHills)
        .setIntValue(16777215);
    public static BiomeGenBase megaTaiga = new BiomeGenTaiga(32, 1)
        .setColor(5858897)
        .setBiomeName("Mega Taiga")
        .setFillerBlockMetadata(5159473)
        .setTemperatureRainfall(0.3F, 0.8F)
        .setHeight(height_MidPlains);
    public static BiomeGenBase megaTaigaHills = new BiomeGenTaiga(33, 1)
        .setColor(4542270)
        .setBiomeName("Mega Taiga Hills")
        .setFillerBlockMetadata(5159473)
        .setTemperatureRainfall(0.3F, 0.8F)
        .setHeight(height_LowHills);
    public static BiomeGenBase extremeHillsPlus = new BiomeGenHills(34, true)
        .setColor(5271632)
        .setBiomeName("Extreme Hills+")
        .setHeight(height_MidHills)
        .setTemperatureRainfall(0.2F, 0.3F);
    public static BiomeGenBase savanna = new BiomeGenSavanna(35)
        .setColor(12431967)
        .setBiomeName("Savanna")
        .setTemperatureRainfall(1.2F, 0.0F)
        .setDisableRain()
        .setHeight(height_LowPlains);
    public static BiomeGenBase savannaPlateau = new BiomeGenSavanna(36)
        .setColor(10984804)
        .setBiomeName("Savanna Plateau")
        .setTemperatureRainfall(1.0F, 0.0F)
        .setDisableRain()
        .setHeight(height_HighPlateaus);
    public static BiomeGenBase mesa = new BiomeGenMesa(37, false, false).setColor(14238997).setBiomeName("Mesa");
    public static BiomeGenBase mesaPlateau_F = new BiomeGenMesa(38, false, true)
        .setColor(11573093)
        .setBiomeName("Mesa Plateau F")
        .setHeight(height_HighPlateaus);
    public static BiomeGenBase mesaPlateau = new BiomeGenMesa(39, false, false).setColor(13274213).setBiomeName("Mesa Plateau").setHeight(height_HighPlateaus);
    public static NoiseGeneratorPerlin temperatureNoise;
    public static NoiseGeneratorPerlin plantNoise;
    public static WorldGenDoublePlant genTallFlowers;
    public String biomeName;
    public int color;
    public int intValue;
    public Block topBlock = Blocks.grass;
    public int topBlockMetadata = 0;
    public Block fillerBlock = Blocks.dirt;
    public int fillerBlockMetadata = 5169201;
    public float minHeight = height_Default.rootHeight;
    public float maxHeight = height_Default.variation;
    public float temperature = 0.5F;
    public float rainfall = 0.5F;
    public int waterColorMultiplier = 16777215;
    public List spawnableMonsterList = new ArrayList();
    public List spawnableCreatureList = new ArrayList();
    public List spawnableWaterCreatureList = new ArrayList();
    public List spawnableCaveCreatureList = new ArrayList();
    public boolean enableRain = true;
    public boolean enableSnow;
    public WorldGenTrees worldGeneratorTrees = new WorldGenTrees(false);
    public WorldGenBigTree worldGeneratorBigTree = new WorldGenBigTree(false);
    public WorldGenSwamp worldGeneratorSwamp = new WorldGenSwamp();
    public int biomeID;
    public BiomeDecorator theBiomeDecorator;

    public BiomeGenBase(int n) {
        this.biomeID = n;
        biomeList[n] = this;
        this.theBiomeDecorator = this.createBiomeDecorator();
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySheep.class, 12, 4, 4));
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityPig.class, 10, 4, 4));
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityChicken.class, 10, 4, 4));
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityCow.class, 8, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySpider.class, 100, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityZombie.class, 100, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 100, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 100, 4, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityEnderman.class, 10, 1, 4));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 5, 1, 1));
        this.spawnableWaterCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySquid.class, 10, 4, 4));
        this.spawnableCaveCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityBat.class, 10, 8, 8));
    }

    public BiomeDecorator createBiomeDecorator() {
        return new BiomeDecorator();
    }

    public BiomeGenBase setTemperatureRainfall(float f, float f2) {
        if (f > 0.1F && f < 0.2F) {
            throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
        }

        this.temperature = f;
        this.rainfall = f2;
        return this;
    }

    public BiomeGenBase setHeight(BiomeGenBase.Height height) {
        this.minHeight = height.rootHeight;
        this.maxHeight = height.variation;
        return this;
    }

    public BiomeGenBase setDisableRain() {
        this.enableRain = false;
        return this;
    }

    public WorldGenAbstractTree findWorldGenAbstractTree(Random random) {
        return random.nextInt(10) == 0 ? this.worldGeneratorBigTree : this.worldGeneratorTrees;
    }

    public WorldGenerator getRandomWorldGenForGrass(Random random) {
        return new WorldGenTallGrass(Blocks.tallgrass, 1);
    }

    public String formatText(Random random, int n, int n2, int n3) {
        return random.nextInt(3) > 0 ? BlockFlower.stringArray2[0] : BlockFlower.stringArray[0];
    }

    public BiomeGenBase setEnableSnow() {
        this.enableSnow = true;
        return this;
    }

    public BiomeGenBase setBiomeName(String str) {
        this.biomeName = str;
        return this;
    }

    public BiomeGenBase setFillerBlockMetadata(int n) {
        this.fillerBlockMetadata = n;
        return this;
    }

    public BiomeGenBase setColor(int n) {
        this.findBiomeGenBase(n, false);
        return this;
    }

    public BiomeGenBase setIntValue(int n) {
        this.intValue = n;
        return this;
    }

    public BiomeGenBase findBiomeGenBase(int n, boolean flag) {
        this.color = n;
        if (flag) {
            this.intValue = (n & 16711422) >> 1;
        } else {
            this.intValue = n;
        }

        return this;
    }

    public int getSkyColorByTemp(float f) {
        f /= 3.0F;
        if (f < -1.0F) {
            f = -1.0F;
        }

        if (f > 1.0F) {
            f = 1.0F;
        }

        return Color.getHSBColor(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F).getRGB();
    }

    public List getSpawnableList(EnumCreatureType enumCreatureType) {
        return enumCreatureType == EnumCreatureType.monster
            ? this.spawnableMonsterList
            : (
                enumCreatureType == EnumCreatureType.creature
                    ? this.spawnableCreatureList
                    : (
                        enumCreatureType == EnumCreatureType.waterCreature
                            ? this.spawnableWaterCreatureList
                            : (enumCreatureType == EnumCreatureType.ambient ? this.spawnableCaveCreatureList : null)
                    )
            );
    }

    public boolean getEnableSnow() {
        return this.isMutatedBiome();
    }

    public boolean canSpawnLightningBolt() {
        return !this.isMutatedBiome() && this.enableRain;
    }

    public boolean isHighHumidity() {
        return this.rainfall > 0.85F;
    }

    public float getSpawningChance() {
        return 0.1F;
    }

    public int getIntRainfall() {
        return (int)(this.rainfall * 65536.0F);
    }

    public float getFloatRainfall() {
        return this.rainfall;
    }

    public float getFloatTemperature(int n, int n2, int n3) {
        if (n2 > 64) {
            float f = (float)temperatureNoise.noise(n * 1.0 / 8.0, n3 * 1.0 / 8.0) * 4.0F;
            return this.temperature - (f + n2 - 64.0F) * 0.05F / 30.0F;
        } else {
            return this.temperature;
        }
    }

    public void decorate(World world, Random random, int n, int n2) {
        this.theBiomeDecorator.decorateChunk(world, random, this, n, n2);
    }

    public int getBiomeGrassColor(int n, int n2, int n3) {
        double d = MathHelper.clamp_float(this.getFloatTemperature(n, n2, n3), 0.0F, 1.0F);
        double d2 = MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
        return ColorizerGrass.getGrassColor(d, d2);
    }

    public int getBiomeFoliageColor(int n, int n2, int n3) {
        double d = MathHelper.clamp_float(this.getFloatTemperature(n, n2, n3), 0.0F, 1.0F);
        double d2 = MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
        return ColorizerFoliage.getFoliageColor(d, d2);
    }

    public boolean isMutatedBiome() {
        return this.enableSnow;
    }

    public void genTerrainBlocks(World world, Random random, Block[] blocks, byte[] bytes, int n, int n2, double d) {
        this.genBiomeTerrain(world, random, blocks, bytes, n, n2, d);
    }

    public void genBiomeTerrain(World world, Random random, Block[] blocks, byte[] bytes, int n, int n2, double d) {
        boolean flag = true;
        Block topBlock = this.topBlock;
        byte b = (byte)(this.topBlockMetadata & 0xFF);
        Block fillerBlock = this.fillerBlock;
        int n3 = -1;
        int n4 = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int n5 = n & 15;
        int n6 = n2 & 15;
        int n7 = blocks.length / 256;

        for (int i = 255; i >= 0; i--) {
            int n8 = (n6 * 16 + n5) * n7 + i;
            if (i <= 0 + random.nextInt(5)) {
                blocks[n8] = Blocks.bedrock;
            } else {
                Block block = blocks[n8];
                if (block == null || block.getMaterial() == Material.air) {
                    n3 = -1;
                } else if (block == Blocks.stone) {
                    if (n3 == -1) {
                        if (n4 <= 0) {
                            topBlock = null;
                            b = 0;
                            fillerBlock = Blocks.stone;
                        } else if (i >= 59 && i <= 64) {
                            topBlock = this.topBlock;
                            b = (byte)(this.topBlockMetadata & 0xFF);
                            fillerBlock = this.fillerBlock;
                        }

                        if (i < 63 && (topBlock == null || topBlock.getMaterial() == Material.air)) {
                            if (this.getFloatTemperature(n, i, n2) < 0.15F) {
                                topBlock = Blocks.ice;
                                b = 0;
                            } else {
                                topBlock = Blocks.water;
                                b = 0;
                            }
                        }

                        n3 = n4;
                        if (i >= 62) {
                            blocks[n8] = topBlock;
                            bytes[n8] = b;
                        } else if (i < 56 - n4) {
                            topBlock = null;
                            fillerBlock = Blocks.stone;
                            blocks[n8] = Blocks.gravel;
                        } else {
                            blocks[n8] = fillerBlock;
                        }
                    } else if (n3 > 0) {
                        n3--;
                        blocks[n8] = fillerBlock;
                        if (n3 == 0 && fillerBlock == Blocks.sand) {
                            n3 = random.nextInt(4) + Math.max(0, i - 63);
                            fillerBlock = Blocks.sandstone;
                        }
                    }
                }
            }
        }
    }

    public BiomeGenBase createMutation() {
        return new BiomeGenMutated(this.biomeID + 128, this);
    }

    public Class getBiomeClass() {
        return this.getClass();
    }

    public boolean isEqualTo(BiomeGenBase biomeGenBase) {
        return biomeGenBase == this || biomeGenBase != null && this.getBiomeClass() == biomeGenBase.getBiomeClass();
    }

    public BiomeGenBase.TempCategory getTempCategory() {
        return this.temperature < 0.2
            ? BiomeGenBase.TempCategory.COLD
            : (this.temperature < 1.0 ? BiomeGenBase.TempCategory.MEDIUM : BiomeGenBase.TempCategory.WARM);
    }

    public static BiomeGenBase[] getBiomeGenArray() {
        return biomeList;
    }

    public static BiomeGenBase getBiome(int n) {
        if (n >= 0 && n <= biomeList.length) {
            return biomeList[n];
        }

        logger.warn("Biome ID is out of bounds: " + n + ", defaulting to 0 (Ocean)");
        return ocean;
    }

    static {
        plains.createMutation();
        desert.createMutation();
        forest.createMutation();
        taiga.createMutation();
        swampland.createMutation();
        icePlains.createMutation();
        jungle.createMutation();
        jungleEdge.createMutation();
        coldTaiga.createMutation();
        savanna.createMutation();
        savannaPlateau.createMutation();
        mesa.createMutation();
        mesaPlateau_F.createMutation();
        mesaPlateau.createMutation();
        birchForest.createMutation();
        birchForestHills.createMutation();
        roofedForest.createMutation();
        megaTaiga.createMutation();
        extremeHills.createMutation();
        extremeHillsPlus.createMutation();
        biomeList[megaTaigaHills.biomeID + 128] = biomeList[megaTaiga.biomeID + 128];

        for (BiomeGenBase biomeGenBase : biomeList) {
            if (biomeGenBase != null && biomeGenBase.biomeID < 128) {
                explorationBiomesList.add(biomeGenBase);
            }
        }

        explorationBiomesList.remove(hell);
        explorationBiomesList.remove(sky);
        explorationBiomesList.remove(frozenOcean);
        explorationBiomesList.remove(extremeHillsEdge);
        temperatureNoise = new NoiseGeneratorPerlin(new Random(1234L), 1);
        plantNoise = new NoiseGeneratorPerlin(new Random(2345L), 1);
        genTallFlowers = new WorldGenDoublePlant();
    }

    public static class Height {
        public float rootHeight;
        public float variation;

        public Height(float f, float f2) {
            this.rootHeight = f;
            this.variation = f2;
        }

        public BiomeGenBase.Height attenuate() {
            return new BiomeGenBase.Height(this.rootHeight * 0.8F, this.variation * 0.6F);
        }
    }

    public static class SpawnListEntry extends Item {
        public Class entityClass;
        public int minGroupCount;
        public int maxGroupCount;

        public SpawnListEntry(Class clazz, int n, int n2, int n3) {
            super(n);
            this.entityClass = clazz;
            this.minGroupCount = n2;
            this.maxGroupCount = n3;
        }

        @Override
        public String toString() {
            return this.entityClass.getSimpleName() + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
        }
    }

    public enum TempCategory {
        OCEAN("OCEAN", 0),
        COLD("COLD", 1),
        MEDIUM("MEDIUM", 2),
        WARM("WARM", 3);

        TempCategory(String str, int n) {
        }
    }
}
