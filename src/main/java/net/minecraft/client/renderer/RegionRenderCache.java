package net.minecraft.client.renderer;

import java.util.ArrayDeque;
import java.util.Arrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.optifine.Config;
import net.optifine.DynamicLights;

public class RegionRenderCache extends ChunkCache {
   public static IBlockState DEFAULT_STATE = Blocks.air.getDefaultState();
   public BlockPos position;
   public int[] combinedLights;
   public IBlockState[] blockStates;
   public static ArrayDeque<int[]> cacheLights = new ArrayDeque<>();
   public static ArrayDeque<IBlockState[]> cacheStates = new ArrayDeque<>();
   public static int maxCacheSize = Config.limit(Runtime.getRuntime().availableProcessors(), 1, 32);

   public RegionRenderCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
      super(worldIn, posFromIn, posToIn, subIn);
      this.position = posFromIn.subtract(new Vec3i(subIn, subIn, subIn));
      short s = 8000;
      this.combinedLights = allocateLights(8000);
      Arrays.fill(this.combinedLights, -1);
      this.blockStates = allocateStates(8000);
   }

   @Override
   public TileEntity getTileEntity(BlockPos pos) {
      int n = (pos.getX() >> 4) - this.chunkX;
      int n2 = (pos.getZ() >> 4) - this.chunkZ;
      return this.chunkArray[n][n2].getTileEntity(pos, EnumCreateEntityType.QUEUED);
   }

   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int positionIndex = this.getPositionIndex(pos);
      int n = this.combinedLights[positionIndex];
      if (n == -1) {
         n = super.getCombinedLight(pos, lightValue);
         if (Config.isDynamicLights() && !this.getBlockState(pos).getBlock().isOpaqueCube()) {
            n = DynamicLights.getCombinedLight(pos, n);
         }

         this.combinedLights[positionIndex] = n;
      }

      return n;
   }

   @Override
   public IBlockState getBlockState(BlockPos pos) {
      int positionIndex = this.getPositionIndex(pos);
      IBlockState blockState = this.blockStates[positionIndex];
      if (blockState == null) {
         blockState = this.getBlockStateRaw(pos);
         this.blockStates[positionIndex] = blockState;
      }

      return blockState;
   }

   public IBlockState getBlockStateRaw(BlockPos pos) {
      return super.getBlockState(pos);
   }

   public int getPositionIndex(BlockPos blockPos) {
      int n = blockPos.getX() - this.position.getX();
      int n2 = blockPos.getY() - this.position.getY();
      int n3 = blockPos.getZ() - this.position.getZ();
      return n * 400 + n3 * 20 + n2;
   }

   public void freeBuffers() {
      freeLights(this.combinedLights);
      freeStates(this.blockStates);
   }

   public static int[] allocateLights(int n) {
      synchronized (cacheLights) {
         int[] pollLast = cacheLights.pollLast();
         if (pollLast == null || pollLast.length < n) {
            pollLast = new int[n];
         }

         return pollLast;
      }
   }

   public static void freeLights(int[] ints) {
      synchronized (cacheLights) {
         if (cacheLights.size() < maxCacheSize) {
            cacheLights.add(ints);
         }
      }
   }

   public static IBlockState[] allocateStates(int n) {
      synchronized (cacheStates) {
         IBlockState[] pollLast = cacheStates.pollLast();
         if (pollLast != null && pollLast.length >= n) {
            Arrays.fill(pollLast, null);
         } else {
            pollLast = new IBlockState[n];
         }

         return pollLast;
      }
   }

   public static void freeStates(IBlockState[] blockStates) {
      synchronized (cacheStates) {
         if (cacheStates.size() < maxCacheSize) {
            cacheStates.add(blockStates);
         }
      }
   }
}
