package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.DesertPyramid;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.JunglePyramid;

public class MapGenScatteredFeature extends MapGenStructure {
   public static List<BiomeGenBase> biomelist = Arrays.asList(
      BiomeGenBase.desert, BiomeGenBase.desertHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.swampland
   );
   public List<SpawnListEntry> scatteredFeatureSpawnList = Lists.newArrayList();
   public int maxDistanceBetweenScatteredFeatures = 32;
   public int minDistanceBetweenScatteredFeatures = 8;

   public MapGenScatteredFeature() {
      this.scatteredFeatureSpawnList.add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
   }

   public MapGenScatteredFeature(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("distance")) {
            this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax(
               (String)entry.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1
            );
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Temple";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int chunkX2 = chunkX;
      int chunkZ2 = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
      }

      int n = chunkX / this.maxDistanceBetweenScatteredFeatures;
      int n2 = chunkZ / this.maxDistanceBetweenScatteredFeatures;
      Random setRandomSeed = this.worldObj.setRandomSeed(n, n2, 14357617);
      n *= this.maxDistanceBetweenScatteredFeatures;
      n2 *= this.maxDistanceBetweenScatteredFeatures;
      n += setRandomSeed.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
      n2 += setRandomSeed.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
      if (chunkX2 == n && chunkZ2 == n2) {
         BiomeGenBase biomeGenerator = this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(chunkX2 * 16 + 8, 0, chunkZ2 * 16 + 8));
         if (biomeGenerator == null) {
            return false;
         }

         for (BiomeGenBase biomeGenBase : biomelist) {
            if (biomeGenerator == biomeGenBase) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new MapGenScatteredFeature.Start(this.worldObj, this.rand, chunkX, chunkZ);
   }

   public boolean isSwampHut(BlockPos blockPos) {
      StructureStart generatorConfigAtPos = this.getGeneratorConfigAtPos(blockPos);
      if (generatorConfigAtPos != null && generatorConfigAtPos instanceof MapGenScatteredFeature.Start && !generatorConfigAtPos.components.isEmpty()) {
         StructureComponent first = generatorConfigAtPos.components.getFirst();
         return first instanceof ComponentScatteredFeaturePieces.SwampHut;
      } else {
         return false;
      }
   }

   public List<SpawnListEntry> getScatteredFeatureSpawnList() {
      return this.scatteredFeatureSpawnList;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(World worldIn, Random random, int n, int n2) {
         super(n, n2);
         BiomeGenBase biomeGenForCoords = worldIn.getBiomeGenForCoords(new BlockPos(n * 16 + 8, 0, n2 * 16 + 8));
         if (biomeGenForCoords == BiomeGenBase.jungle || biomeGenForCoords == BiomeGenBase.jungleHills) {
            JunglePyramid junglePyramid = new JunglePyramid(random, n * 16, n2 * 16);
            this.components.add(junglePyramid);
         } else if (biomeGenForCoords == BiomeGenBase.swampland) {
            ComponentScatteredFeaturePieces.SwampHut swampHut = new ComponentScatteredFeaturePieces.SwampHut(random, n * 16, n2 * 16);
            this.components.add(swampHut);
         } else if (biomeGenForCoords == BiomeGenBase.desert || biomeGenForCoords == BiomeGenBase.desertHills) {
            DesertPyramid desertPyramid = new DesertPyramid(random, n * 16, n2 * 16);
            this.components.add(desertPyramid);
         }

         this.updateBoundingBox();
      }
   }
}
