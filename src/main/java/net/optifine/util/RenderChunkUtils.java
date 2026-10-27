package net.optifine.util;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class RenderChunkUtils {
   public static int getCountBlocks(RenderChunk renderChunk) {
      ExtendedBlockStorage[] blockStorageArray = renderChunk.getChunk().getBlockStorageArray();
      if (blockStorageArray == null) {
         return 0;
      }

      int n = renderChunk.getPosition().getY() >> 4;
      ExtendedBlockStorage extendedBlockStorage = blockStorageArray[n];
      return extendedBlockStorage == null ? 0 : extendedBlockStorage.getBlockRefCount();
   }

   public static double getRelativeBufferSize(RenderChunk renderChunk) {
      int countBlocks = getCountBlocks(renderChunk);
      return getRelativeBufferSize(countBlocks);
   }

   public static double getRelativeBufferSize(int n) {
      double d = n / 4096.0;
      d *= 0.995;
      double d2 = d * 2.0 - 1.0;
      d2 = MathHelper.clamp_double(d2, -1.0, 1.0);
      return MathHelper.sqrt_double(1.0 - d2 * d2);
   }
}
