package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenStronghold extends MapGenStructure {
   public ChunkCoordIntPair[] structureCoords = new ChunkCoordIntPair[3];
   public boolean ranBiomeCheck;
   public double distance = 32.0;
   public int spread = 3;
   public List<BiomeGenBase> biomes = Lists.newArrayList();

   public MapGenStronghold() {
      for (BiomeGenBase biomeGenBase : BiomeGenBase.getBiomeGenArray()) {
         if (biomeGenBase != null && biomeGenBase.minHeight > 0.0F) {
            this.biomes.add(biomeGenBase);
         }
      }
   }

   public MapGenStronghold(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("distance")) {
            this.distance = MathHelper.parseDoubleWithDefaultAndMax((String)entry.getValue(), this.distance, 1.0);
         } else if (((String)entry.getKey()).equals("count")) {
            this.structureCoords = new ChunkCoordIntPair[MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.structureCoords.length, 1)];
         } else if (((String)entry.getKey()).equals("spread")) {
            this.spread = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.spread, 1);
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Stronghold";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      if (!this.ranBiomeCheck) {
         Random random = new Random();
         random.setSeed(this.worldObj.getSeed());
         double d = random.nextDouble() * Math.PI * 2.0;
         int n = 1;

         for (int i = 0; i < this.structureCoords.length; i++) {
            double d2 = (1.25 * n + random.nextDouble()) * this.distance * n;
            int round = (int)Math.round(Math.cos(d) * d2);
            int round2 = (int)Math.round(Math.sin(d) * d2);
            BlockPos findBiomePosition = this.worldObj.getWorldChunkManager().findBiomePosition((round << 4) + 8, (round2 << 4) + 8, 112, this.biomes, random);
            if (findBiomePosition != null) {
               round = findBiomePosition.getX() >> 4;
               round2 = findBiomePosition.getZ() >> 4;
            }

            this.structureCoords[i] = new ChunkCoordIntPair(round, round2);
            d += (Math.PI * 2) * n / this.spread;
            if (i == this.spread) {
               n += 2 + random.nextInt(5);
               this.spread = this.spread + 1 + random.nextInt(2);
            }
         }

         this.ranBiomeCheck = true;
      }

      for (ChunkCoordIntPair chunkCoordIntPair : this.structureCoords) {
         if (chunkX == chunkCoordIntPair.chunkXPos && chunkZ == chunkCoordIntPair.chunkZPos) {
            return true;
         }
      }

      return false;
   }

   @Override
   public List<BlockPos> getCoordList() {
      ArrayList newArrayList = Lists.newArrayList();

      for (ChunkCoordIntPair chunkCoordIntPair : this.structureCoords) {
         if (chunkCoordIntPair != null) {
            newArrayList.add(chunkCoordIntPair.getCenterBlock(64));
         }
      }

      return newArrayList;
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      MapGenStronghold.Start start = new MapGenStronghold.Start(this.worldObj, this.rand, chunkX, chunkZ);

      while (start.getComponents().isEmpty() || ((StructureStrongholdPieces.Stairs2)start.getComponents().get(0)).strongholdPortalRoom == null) {
         start = new MapGenStronghold.Start(this.worldObj, this.rand, chunkX, chunkZ);
      }

      return start;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(World worldIn, Random random, int n, int n2) {
         super(n, n2);
         StructureStrongholdPieces.prepareStructurePieces();
         StructureStrongholdPieces.Stairs2 stairs2 = new StructureStrongholdPieces.Stairs2(0, random, (n << 4) + 2, (n2 << 4) + 2);
         this.components.add(stairs2);
         stairs2.buildComponent(stairs2, this.components, random);
         List pieces = stairs2.pieces;

         while (!pieces.isEmpty()) {
            int nextInt = random.nextInt(pieces.size());
            StructureComponent structureComponent = (StructureComponent)pieces.remove(nextInt);
            structureComponent.buildComponent(stairs2, this.components, random);
         }

         this.updateBoundingBox();
         this.markAvailableHeight(worldIn, random, 10);
      }
   }
}
