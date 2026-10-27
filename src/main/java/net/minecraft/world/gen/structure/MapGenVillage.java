package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureVillagePieces.Road;

public class MapGenVillage extends MapGenStructure {
   public static List<BiomeGenBase> villageSpawnBiomes = Arrays.asList(BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.savanna);
   public int terrainType;
   public int distance = 32;
   public int intValue = 8;

   public MapGenVillage() {
   }

   public MapGenVillage(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("size")) {
            this.terrainType = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.terrainType, 0);
         } else if (((String)entry.getKey()).equals("distance")) {
            this.distance = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.distance, this.intValue + 1);
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Village";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int chunkX2 = chunkX;
      int chunkZ2 = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.distance - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.distance - 1;
      }

      int n = chunkX / this.distance;
      int n2 = chunkZ / this.distance;
      Random setRandomSeed = this.worldObj.setRandomSeed(n, n2, 10387312);
      n *= this.distance;
      n2 *= this.distance;
      n += setRandomSeed.nextInt(this.distance - this.intValue);
      n2 += setRandomSeed.nextInt(this.distance - this.intValue);
      if (chunkX2 == n && chunkZ2 == n2) {
         boolean areBiomesViable = this.worldObj.getWorldChunkManager().areBiomesViable(chunkX2 * 16 + 8, chunkZ2 * 16 + 8, 0, villageSpawnBiomes);
         if (areBiomesViable) {
            return true;
         }
      }

      return false;
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new MapGenVillage.Start(this.worldObj, this.rand, chunkX, chunkZ, this.terrainType);
   }

   public static class Start extends StructureStart {
      public boolean hasMoreThanTwoComponents;

      public Start() {
      }

      public Start(World worldIn, Random rand, int x, int z, int size) {
         super(x, z);
         List list = StructureVillagePieces.getStructureVillageWeightedPieceList(rand, size);
         net.minecraft.world.gen.structure.StructureVillagePieces.Start start = new net.minecraft.world.gen.structure.StructureVillagePieces.Start(
            worldIn.getWorldChunkManager(), 0, rand, (x << 4) + 2, (z << 4) + 2, list, size
         );
         this.components.add(start);
         start.buildComponent(start, this.components, rand);
         List structureComponentList2 = start.structureComponentList2;
         List structureComponentList = start.structureComponentList;

         while (!structureComponentList2.isEmpty() || !structureComponentList.isEmpty()) {
            if (structureComponentList2.isEmpty()) {
               int nextInt = rand.nextInt(structureComponentList.size());
               StructureComponent structureComponent = (StructureComponent)structureComponentList.remove(nextInt);
               structureComponent.buildComponent(start, this.components, rand);
            } else {
               int nextInt = rand.nextInt(structureComponentList2.size());
               StructureComponent structureComponent = (StructureComponent)structureComponentList2.remove(nextInt);
               structureComponent.buildComponent(start, this.components, rand);
            }
         }

         this.updateBoundingBox();
         int n = 0;

         for (StructureComponent structureComponent : this.components) {
            if (!(structureComponent instanceof Road)) {
               n++;
            }
         }

         this.hasMoreThanTwoComponents = n > 2;
      }

      @Override
      public boolean isSizeableStructure() {
         return this.hasMoreThanTwoComponents;
      }

      @Override
      public void writeToNBT(NBTTagCompound tagCompound) {
         super.writeToNBT(tagCompound);
         tagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
      }

      @Override
      public void readFromNBT(NBTTagCompound tagCompound) {
         super.readFromNBT(tagCompound);
         this.hasMoreThanTwoComponents = tagCompound.getBoolean("Valid");
      }
   }
}
