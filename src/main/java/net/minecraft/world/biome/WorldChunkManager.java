package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManager {
   public GenLayer genBiomes;
   public GenLayer biomeIndexLayer;
   public BiomeCache biomeCache = new BiomeCache(this);
   public String generatorOptions = "";
   public List<BiomeGenBase> biomesToSpawnIn = Lists.newArrayList();

   public WorldChunkManager() {
      this.biomesToSpawnIn.add(BiomeGenBase.forest);
      this.biomesToSpawnIn.add(BiomeGenBase.plains);
      this.biomesToSpawnIn.add(BiomeGenBase.taiga);
      this.biomesToSpawnIn.add(BiomeGenBase.taigaHills);
      this.biomesToSpawnIn.add(BiomeGenBase.forestHills);
      this.biomesToSpawnIn.add(BiomeGenBase.jungle);
      this.biomesToSpawnIn.add(BiomeGenBase.jungleHills);
   }

   public WorldChunkManager(long seed, WorldType worldTypeIn, String options) {
      this();
      this.generatorOptions = options;
      GenLayer[] initializeAllBiomeGenerators = GenLayer.initializeAllBiomeGenerators(seed, worldTypeIn, options);
      this.genBiomes = initializeAllBiomeGenerators[0];
      this.biomeIndexLayer = initializeAllBiomeGenerators[1];
   }

   public WorldChunkManager(World worldIn) {
      this(worldIn.getSeed(), worldIn.getWorldInfo().getTerrainType(), worldIn.getWorldInfo().getGeneratorOptions());
   }

   public List<BiomeGenBase> getBiomesToSpawnIn() {
      return this.biomesToSpawnIn;
   }

   public BiomeGenBase getBiomeGenerator(BlockPos pos) {
      return this.getBiomeGenerator(pos, (BiomeGenBase)null);
   }

   public BiomeGenBase getBiomeGenerator(BlockPos pos, BiomeGenBase biomeGenBaseIn) {
      return this.biomeCache.findBiomeGenBase(pos.getX(), pos.getZ(), biomeGenBaseIn);
   }

   public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length) {
      IntCache.resetIntCache();
      if (listToReuse == null || listToReuse.length < width * length) {
         listToReuse = new float[width * length];
      }

      int[] ints = this.biomeIndexLayer.getInts(x, z, width, length);

      for (int i = 0; i < width * length; i++) {
         try {
            float f = BiomeGenBase.getBiomeFromBiomeList(ints[i], BiomeGenBase.DEFAULT).getIntRainfall() / 65536.0F;
            if (f > 1.0F) {
               f = 1.0F;
            }

            listToReuse[i] = f;
         } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Invalid Biome id");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("DownfallBlock");
            makeCategory.addCrashSection("biome id", i);
            makeCategory.addCrashSection("downfalls[] size", listToReuse.length);
            makeCategory.addCrashSection("x", x);
            makeCategory.addCrashSection("z", z);
            makeCategory.addCrashSection("w", width);
            makeCategory.addCrashSection("h", length);
            throw new ReportedException(makeCrashReport);
         }
      }

      return listToReuse;
   }

   public float getTemperatureAtHeight(float f, int n) {
      return f;
   }

   public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int height) {
      IntCache.resetIntCache();
      if (biomes == null || biomes.length < width * height) {
         biomes = new BiomeGenBase[width * height];
      }

      int[] ints = this.genBiomes.getInts(x, z, width, height);

      try {
         for (int i = 0; i < width * height; i++) {
            biomes[i] = BiomeGenBase.getBiomeFromBiomeList(ints[i], BiomeGenBase.DEFAULT);
         }

         return biomes;
      } catch (Throwable e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Invalid Biome id");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("RawBiomeBlock");
         makeCategory.addCrashSection("biomes[] size", biomes.length);
         makeCategory.addCrashSection("x", x);
         makeCategory.addCrashSection("z", z);
         makeCategory.addCrashSection("w", width);
         makeCategory.addCrashSection("h", height);
         throw new ReportedException(makeCrashReport);
      }
   }

   public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x, int z, int width, int depth) {
      return this.getBiomeGenAt(oldBiomeList, x, z, width, depth, true);
   }

   public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
      IntCache.resetIntCache();
      if (listToReuse == null || listToReuse.length < width * length) {
         listToReuse = new BiomeGenBase[width * length];
      }

      if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0) {
         BiomeGenBase[] cachedBiomes = this.biomeCache.getCachedBiomes(x, z);
         System.arraycopy(cachedBiomes, 0, listToReuse, 0, width * length);
         return listToReuse;
      }

      int[] ints = this.biomeIndexLayer.getInts(x, z, width, length);

      for (int i = 0; i < width * length; i++) {
         listToReuse[i] = BiomeGenBase.getBiomeFromBiomeList(ints[i], BiomeGenBase.DEFAULT);
      }

      return listToReuse;
   }

   public boolean areBiomesViable(int n, int n2, int n3, List<BiomeGenBase> list) {
      IntCache.resetIntCache();
      int n4 = n - n3 >> 2;
      int n5 = n2 - n3 >> 2;
      int n6 = n + n3 >> 2;
      int n7 = n2 + n3 >> 2;
      int n8 = n6 - n4 + 1;
      int n9 = n7 - n5 + 1;
      int[] ints = this.genBiomes.getInts(n4, n5, n8, n9);

      try {
         for (int i = 0; i < n8 * n9; i++) {
            BiomeGenBase biome = BiomeGenBase.getBiome(ints[i]);
            if (!list.contains(biome)) {
               return false;
            }
         }

         return true;
      } catch (Throwable e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Invalid Biome id");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("Layer");
         makeCategory.addCrashSection("Layer", this.genBiomes.toString());
         makeCategory.addCrashSection("x", n);
         makeCategory.addCrashSection("z", n2);
         makeCategory.addCrashSection("radius", n3);
         makeCategory.addCrashSection("allowed", list);
         throw new ReportedException(makeCrashReport);
      }
   }

   public BlockPos findBiomePosition(int x, int z, int range, List<BiomeGenBase> biomes, Random random) {
      IntCache.resetIntCache();
      int n = x - range >> 2;
      int n2 = z - range >> 2;
      int n3 = x + range >> 2;
      int n4 = z + range >> 2;
      int n5 = n3 - n + 1;
      int n6 = n4 - n2 + 1;
      int[] ints = this.genBiomes.getInts(n, n2, n5, n6);
      BlockPos blockPos = null;
      int n7 = 0;

      for (int i = 0; i < n5 * n6; i++) {
         int n8 = n + i % n5 << 2;
         int n9 = n2 + i / n5 << 2;
         BiomeGenBase biome = BiomeGenBase.getBiome(ints[i]);
         if (biomes.contains(biome) && (blockPos == null || random.nextInt(n7 + 1) == 0)) {
            blockPos = new BlockPos(n8, 0, n9);
            n7++;
         }
      }

      return blockPos;
   }

   public void cleanupCache() {
      this.biomeCache.cleanupCache();
   }
}
