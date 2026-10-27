package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

public class MapGenNetherBridge extends MapGenStructure {
   public List<SpawnListEntry> spawnList = Lists.newArrayList();

   public MapGenNetherBridge() {
      this.spawnList.add(new SpawnListEntry(EntityBlaze.class, 10, 2, 3));
      this.spawnList.add(new SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
      this.spawnList.add(new SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
      this.spawnList.add(new SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
   }

   @Override
   public String getStructureName() {
      return "Fortress";
   }

   public List<SpawnListEntry> getSpawnList() {
      return this.spawnList;
   }

   @Override
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int n = chunkX >> 4;
      int n2 = chunkZ >> 4;
      this.rand.setSeed(n ^ n2 << 4 ^ this.worldObj.getSeed());
      this.rand.nextInt();
      return this.rand.nextInt(3) != 0 ? false : (chunkX != (n << 4) + 4 + this.rand.nextInt(8) ? false : chunkZ == (n2 << 4) + 4 + this.rand.nextInt(8));
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new MapGenNetherBridge.Start(this.worldObj, this.rand, chunkX, chunkZ);
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(World worldIn, Random random, int n, int n2) {
         super(n, n2);
         net.minecraft.world.gen.structure.StructureNetherBridgePieces.Start start = new net.minecraft.world.gen.structure.StructureNetherBridgePieces.Start(
            random, (n << 4) + 2, (n2 << 4) + 2
         );
         this.components.add(start);
         start.buildComponent(start, this.components, random);
         List pieces = start.pieces;

         while (!pieces.isEmpty()) {
            int nextInt = random.nextInt(pieces.size());
            StructureComponent structureComponent = (StructureComponent)pieces.remove(nextInt);
            structureComponent.buildComponent(start, this.components, random);
         }

         this.updateBoundingBox();
         this.setRandomHeight(worldIn, random, 48, 70);
      }
   }
}
