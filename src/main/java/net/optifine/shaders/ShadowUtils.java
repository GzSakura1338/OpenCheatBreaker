package net.optifine.shaders;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class ShadowUtils {
   public static Iterator<RenderChunk> makeShadowChunkIterator(WorldClient worldClient, double d, Entity entity, int n, ViewFrustum viewFrustum) {
      float shadowRenderDistance = Shaders.getShadowRenderDistance();
      if (shadowRenderDistance > 0.0F && shadowRenderDistance < (n - 1) * 16) {
         int n2 = MathHelper.ceiling_float_int(shadowRenderDistance / 16.0F) + 1;
         float celestialAngleRadians = worldClient.getCelestialAngleRadians((float)d);
         float f = Shaders.sunPathRotation * MathHelper.deg2Rad;
         float f2 = celestialAngleRadians > MathHelper.PId2 && celestialAngleRadians < 3.0F * MathHelper.PId2 ? celestialAngleRadians + MathHelper.floatValue : celestialAngleRadians;
         float f3 = -MathHelper.sin(f2);
         float f4 = MathHelper.cos(f2) * MathHelper.cos(f);
         float f5 = -MathHelper.cos(f2) * MathHelper.sin(f);
         BlockPos blockPos = new BlockPos(
            MathHelper.floor_double(entity.posX) >> 4, MathHelper.floor_double(entity.posY) >> 4, MathHelper.floor_double(entity.posZ) >> 4
         );
         BlockPos blockPos2 = blockPos.add(-f3 * n2, -f4 * n2, -f5 * n2);
         BlockPos blockPos3 = blockPos.add(f3 * n, f4 * n, f5 * n);
         return new IteratorRenderChunks(viewFrustum, blockPos2, blockPos3, n2, n2);
      } else {
         List asList = Arrays.asList(viewFrustum.renderChunks);
         return asList.iterator();
      }
   }
}
