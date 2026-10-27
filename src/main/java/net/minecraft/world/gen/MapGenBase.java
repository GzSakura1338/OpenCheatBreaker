package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenBase {
   public int range = 8;
   public Random rand = new Random();
   public World worldObj;

   public void generate(IChunkProvider chunkProviderIn, World worldIn, int x, int z, ChunkPrimer chunkPrimerIn) {
      int range = this.range;
      this.worldObj = worldIn;
      this.rand.setSeed(worldIn.getSeed());
      long nextLong = this.rand.nextLong();
      long nextLong2 = this.rand.nextLong();

      for (int i = x - range; i <= x + range; i++) {
         for (int j = z - range; j <= z + range; j++) {
            long l = i * nextLong;
            long l2 = j * nextLong2;
            this.rand.setSeed(l ^ l2 ^ worldIn.getSeed());
            this.recursiveGenerate(worldIn, i, j, x, z, chunkPrimerIn);
         }
      }
   }

   public void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int n, int n2, ChunkPrimer chunkPrimerIn) {
   }
}
