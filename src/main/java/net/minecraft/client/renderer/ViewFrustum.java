package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.render.VboRegion;

public class ViewFrustum {
   public Map<ChunkCoordIntPair, VboRegion[]> mapVboRegions = new HashMap<>();
   public RenderGlobal renderGlobal;
   public int countChunksY;
   public int countChunksX;
   public int countChunksZ;
   public RenderChunk[] renderChunks;
   public World world;

   public ViewFrustum(World worldIn, int renderDistanceChunks, RenderGlobal renderGlobal, IRenderChunkFactory renderChunkFactory) {
      this.renderGlobal = renderGlobal;
      this.world = worldIn;
      this.setCountChunksXYZ(renderDistanceChunks);
      this.createRenderChunks(renderChunkFactory);
   }

   public void createRenderChunks(IRenderChunkFactory renderChunkFactory) {
      int n = this.countChunksX * this.countChunksY * this.countChunksZ;
      this.renderChunks = new RenderChunk[n];
      int n2 = 0;

      for (int i = 0; i < this.countChunksX; i++) {
         for (int j = 0; j < this.countChunksY; j++) {
            for (int k = 0; k < this.countChunksZ; k++) {
               int n3 = (k * this.countChunksY + j) * this.countChunksX + i;
               BlockPos blockPos = new BlockPos(i * 16, j * 16, k * 16);
               this.renderChunks[n3] = renderChunkFactory.makeRenderChunk(this.world, this.renderGlobal, blockPos, n2++);
               if (Config.isVbo() && Config.isRenderRegions()) {
                  this.updateVboRegion(this.renderChunks[n3]);
               }
            }
         }
      }

      for (int i = 0; i < this.renderChunks.length; i++) {
         RenderChunk renderChunk = this.renderChunks[i];

         for (int j = 0; j < EnumFacing.VALUES.length; j++) {
            EnumFacing enumFacing = EnumFacing.VALUES[j];
            BlockPos blockPosOffset16 = renderChunk.getBlockPosOffset16(enumFacing);
            RenderChunk renderChunk2 = this.getRenderChunk(blockPosOffset16);
            renderChunk.setRenderChunkNeighbour(enumFacing, renderChunk2);
         }
      }
   }

   public void deleteGlResources() {
      for (RenderChunk renderChunk : this.renderChunks) {
         renderChunk.deleteGlResources();
      }

      this.deleteVboRegions();
   }

   public void setCountChunksXYZ(int renderDistanceChunks) {
      int n = renderDistanceChunks * 2 + 1;
      this.countChunksX = n;
      this.countChunksY = 16;
      this.countChunksZ = n;
   }

   public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
      int n = MathHelper.floor_double(viewEntityX) - 8;
      int n2 = MathHelper.floor_double(viewEntityZ) - 8;
      int n3 = this.countChunksX * 16;

      for (int i = 0; i < this.countChunksX; i++) {
         int computeInt = this.computeInt(n, n3, i);

         for (int j = 0; j < this.countChunksZ; j++) {
            int computeInt2 = this.computeInt(n2, n3, j);

            for (int k = 0; k < this.countChunksY; k++) {
               int n4 = k * 16;
               RenderChunk renderChunk = this.renderChunks[(j * this.countChunksY + k) * this.countChunksX + i];
               BlockPos position = renderChunk.getPosition();
               if (position.getX() != computeInt || position.getY() != n4 || position.getZ() != computeInt2) {
                  BlockPos blockPos = new BlockPos(computeInt, n4, computeInt2);
                  if (!blockPos.equals(renderChunk.getPosition())) {
                     renderChunk.setPosition(blockPos);
                  }
               }
            }
         }
      }
   }

   public int computeInt(int n, int n2, int n3) {
      int n4 = n3 * 16;
      int n5 = n4 - n + n2 / 2;
      if (n5 < 0) {
         n5 -= n2 - 1;
      }

      return n4 - n5 / n2 * n2;
   }

   public void markBlocksForUpdate(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
      int bucketInt = MathHelper.bucketInt(fromX, 16);
      int bucketInt2 = MathHelper.bucketInt(fromY, 16);
      int bucketInt3 = MathHelper.bucketInt(fromZ, 16);
      int bucketInt4 = MathHelper.bucketInt(toX, 16);
      int bucketInt5 = MathHelper.bucketInt(toY, 16);
      int bucketInt6 = MathHelper.bucketInt(toZ, 16);

      for (int i = bucketInt; i <= bucketInt4; i++) {
         int n = i % this.countChunksX;
         if (n < 0) {
            n += this.countChunksX;
         }

         for (int j = bucketInt2; j <= bucketInt5; j++) {
            int n2 = j % this.countChunksY;
            if (n2 < 0) {
               n2 += this.countChunksY;
            }

            for (int k = bucketInt3; k <= bucketInt6; k++) {
               int n3 = k % this.countChunksZ;
               if (n3 < 0) {
                  n3 += this.countChunksZ;
               }

               int n4 = (n3 * this.countChunksY + n2) * this.countChunksX + n;
               RenderChunk renderChunk = this.renderChunks[n4];
               renderChunk.setNeedsUpdate(true);
            }
         }
      }
   }

   public RenderChunk getRenderChunk(BlockPos pos) {
      int n = pos.getX() >> 4;
      int n2 = pos.getY() >> 4;
      int n3 = pos.getZ() >> 4;
      if (n2 >= 0 && n2 < this.countChunksY) {
         n %= this.countChunksX;
         if (n < 0) {
            n += this.countChunksX;
         }

         n3 %= this.countChunksZ;
         if (n3 < 0) {
            n3 += this.countChunksZ;
         }

         int n4 = (n3 * this.countChunksY + n2) * this.countChunksX + n;
         return this.renderChunks[n4];
      } else {
         return null;
      }
   }

   public void updateVboRegion(RenderChunk renderChunk) {
      BlockPos position = renderChunk.getPosition();
      int n = position.getX() >> 8 << 8;
      int n2 = position.getZ() >> 8 << 8;
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(n, n2);
      EnumWorldBlockLayer[] enumWorldBlockLayers = RenderChunk.ENUM_WORLD_BLOCK_LAYERS;
      VboRegion[] vboRegions = this.mapVboRegions.get(chunkCoordIntPair);
      if (vboRegions == null) {
         vboRegions = new VboRegion[enumWorldBlockLayers.length];

         for (int i = 0; i < enumWorldBlockLayers.length; i++) {
            vboRegions[i] = new VboRegion(enumWorldBlockLayers[i]);
         }

         this.mapVboRegions.put(chunkCoordIntPair, vboRegions);
      }

      for (int i = 0; i < enumWorldBlockLayers.length; i++) {
         VboRegion vboRegion = vboRegions[i];
         if (vboRegion != null) {
            renderChunk.getVertexBufferByLayer(i).setVboRegion(vboRegion);
         }
      }
   }

   public void deleteVboRegions() {
      for (ChunkCoordIntPair chunkCoordIntPair : this.mapVboRegions.keySet()) {
         VboRegion[] vboRegions = this.mapVboRegions.get(chunkCoordIntPair);

         for (int i = 0; i < vboRegions.length; i++) {
            VboRegion vboRegion = vboRegions[i];
            if (vboRegion != null) {
               vboRegion.deleteGlBuffers();
            }

            vboRegions[i] = null;
         }
      }

      this.mapVboRegions.clear();
   }
}
