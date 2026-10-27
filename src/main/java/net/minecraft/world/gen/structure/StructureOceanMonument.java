package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.gen.structure.StructureOceanMonumentPieces.MonumentBuilding;

public class StructureOceanMonument extends MapGenStructure {
   public int spacing = 32;
   public int separation = 5;
   public static List<BiomeGenBase> BIOMES = Arrays.asList(
      BiomeGenBase.ocean, BiomeGenBase.deepOcean, BiomeGenBase.river, BiomeGenBase.frozenOcean, BiomeGenBase.frozenRiver
   );
   public static List<SpawnListEntry> MONSTER_SPAWNS = Lists.newArrayList();

   public StructureOceanMonument() {
   }

   public StructureOceanMonument(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("spacing")) {
            this.spacing = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.spacing, 1);
         } else if (((String)entry.getKey()).equals("separation")) {
            this.separation = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.separation, 1);
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Monument";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int chunkX2 = chunkX;
      int chunkZ2 = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.spacing - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.spacing - 1;
      }

      int n = chunkX / this.spacing;
      int n2 = chunkZ / this.spacing;
      Random setRandomSeed = this.worldObj.setRandomSeed(n, n2, 10387313);
      n *= this.spacing;
      n2 *= this.spacing;
      n += (setRandomSeed.nextInt(this.spacing - this.separation) + setRandomSeed.nextInt(this.spacing - this.separation)) / 2;
      n2 += (setRandomSeed.nextInt(this.spacing - this.separation) + setRandomSeed.nextInt(this.spacing - this.separation)) / 2;
      if (chunkX2 == n && chunkZ2 == n2) {
         if (this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(chunkX2 * 16 + 8, 64, chunkZ2 * 16 + 8), (BiomeGenBase)null)
            != BiomeGenBase.deepOcean) {
            return false;
         }

         boolean areBiomesViable = this.worldObj.getWorldChunkManager().areBiomesViable(chunkX2 * 16 + 8, chunkZ2 * 16 + 8, 29, BIOMES);
         if (areBiomesViable) {
            return true;
         }
      }

      return false;
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new StructureOceanMonument.StartMonument(this.worldObj, this.rand, chunkX, chunkZ);
   }

   public List<SpawnListEntry> getScatteredFeatureSpawnList() {
      return MONSTER_SPAWNS;
   }

   static {
      MONSTER_SPAWNS.add(new SpawnListEntry(EntityGuardian.class, 1, 2, 4));
   }

   public static class StartMonument extends StructureStart {
      public Set<ChunkCoordIntPair> processedChunks = Sets.newHashSet();
      public boolean initialized;

      public StartMonument() {
      }

      public StartMonument(World worldIn, Random random, int n, int n2) {
         super(n, n2);
         this.init(worldIn, random, n, n2);
      }

      public void init(World worldIn, Random random, int n, int n2) {
         random.setSeed(worldIn.getSeed());
         long nextLong = random.nextLong();
         long nextLong2 = random.nextLong();
         long l = n * nextLong;
         long l2 = n2 * nextLong2;
         random.setSeed(l ^ l2 ^ worldIn.getSeed());
         int n3 = n * 16 + 8 - 29;
         int n4 = n2 * 16 + 8 - 29;
         EnumFacing random2 = Plane.HORIZONTAL.random(random);
         this.components.add(new MonumentBuilding(random, n3, n4, random2));
         this.updateBoundingBox();
         this.initialized = true;
      }

      @Override
      public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb) {
         if (!this.initialized) {
            this.components.clear();
            this.init(worldIn, rand, this.getChunkPosX(), this.getChunkPosZ());
         }

         super.generateStructure(worldIn, rand, structurebb);
      }

      @Override
      public boolean checkChunkCoordIntPair(ChunkCoordIntPair pair) {
         return this.processedChunks.contains(pair) ? false : super.checkChunkCoordIntPair(pair);
      }

      @Override
      public void handleChunkCoordIntPair(ChunkCoordIntPair pair) {
         super.handleChunkCoordIntPair(pair);
         this.processedChunks.add(pair);
      }

      @Override
      public void writeToNBT(NBTTagCompound tagCompound) {
         super.writeToNBT(tagCompound);
         NBTTagList nbttagList = new NBTTagList();

         for (ChunkCoordIntPair chunkCoordIntPair : this.processedChunks) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setInteger("X", chunkCoordIntPair.chunkXPos);
            nbttagCompound.setInteger("Z", chunkCoordIntPair.chunkZPos);
            nbttagList.appendTag(nbttagCompound);
         }

         tagCompound.setTag("Processed", nbttagList);
      }

      @Override
      public void readFromNBT(NBTTagCompound tagCompound) {
         super.readFromNBT(tagCompound);
         if (tagCompound.hasKey("Processed", 9)) {
            NBTTagList tagList = tagCompound.getTagList("Processed", 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
               NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
               this.processedChunks.add(new ChunkCoordIntPair(compoundTagAt.getInteger("X"), compoundTagAt.getInteger("Z")));
            }
         }
      }
   }
}
