package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.optifine.BlockPosM;
import net.optifine.Config;
import net.optifine.CustomBlockLayers;
import net.optifine.override.ChunkCacheOF;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.render.AabbFrame;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;

public class RenderChunk {
   public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
   public ReentrantLock lockCompileTask = new ReentrantLock();
   public static int renderChunksUpdated;
   public BlockPos position;
   public ReentrantLock lockCompiledChunk = new ReentrantLock();
   public ChunkCompileTaskGenerator compileTask = null;
   public Set<TileEntity> setTileEntities = Sets.newHashSet();
   public FloatBuffer modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
   public VertexBuffer[] vertexBuffers = new VertexBuffer[EnumWorldBlockLayer.values().length];
   public int frameIndex = -1;
   public boolean needsUpdate = true;
   public EnumMap<EnumFacing, BlockPos> mapEnumFacing = null;
   public AxisAlignedBB boundingBox;
   public BlockPos[] positionOffsets16 = new BlockPos[EnumFacing.VALUES.length];
   public EnumWorldBlockLayer[] blockLayersSingle = new EnumWorldBlockLayer[1];
   public boolean isMipmaps = Config.isMipmaps();
   public boolean fixBlockLayer = !Reflector.BetterFoliageClient.exists();
   public static EnumWorldBlockLayer[] ENUM_WORLD_BLOCK_LAYERS = EnumWorldBlockLayer.values();
   public boolean playerUpdate = false;
   public RenderChunk[] renderChunksOfset16 = new RenderChunk[6];
   public boolean renderChunksOffset16Updated = false;
   public RenderChunk[] renderChunkNeighbours = new RenderChunk[EnumFacing.VALUES.length];
   public int regionX;
   public int regionZ;
   public RenderChunk[] renderChunkNeighboursValid = new RenderChunk[EnumFacing.VALUES.length];
   public boolean renderChunkNeighboursUpated = false;
   public Chunk chunk;
   public ContainerLocalRenderInformation renderInfo = new ContainerLocalRenderInformation(this, (EnumFacing)null, 0);
   public World world;
   public RenderGlobal renderGlobal;
   public int index;
   public AabbFrame boundingBoxParent;

   public RenderChunk(World worldIn, RenderGlobal renderGlobalIn, BlockPos blockPosIn, int indexIn) {
      this.world = worldIn;
      this.renderGlobal = renderGlobalIn;
      this.index = indexIn;
      if (!blockPosIn.equals(this.getPosition())) {
         this.setPosition(blockPosIn);
      }

      if (OpenGlHelper.useVbo()) {
         for (int i = 0; i < EnumWorldBlockLayer.values().length; i++) {
            this.vertexBuffers[i] = new VertexBuffer(DefaultVertexFormats.BLOCK);
         }
      }
   }

   public boolean setFrameIndex(int frameIndexIn) {
      if (this.frameIndex == frameIndexIn) {
         return false;
      }

      this.frameIndex = frameIndexIn;
      return true;
   }

   public VertexBuffer getVertexBufferByLayer(int layer) {
      return this.vertexBuffers[layer];
   }

   public void setPosition(BlockPos pos) {
      this.stopCompileTask();
      this.position = pos;
      byte b = 8;
      this.regionX = pos.getX() >> b << b;
      this.regionZ = pos.getZ() >> b << b;
      this.boundingBox = new AxisAlignedBB(pos, pos.add(16, 16, 16));
      this.initModelviewMatrix();

      for (int i = 0; i < this.positionOffsets16.length; i++) {
         this.positionOffsets16[i] = null;
      }

      this.renderChunksOffset16Updated = false;
      this.renderChunkNeighboursUpated = false;

      for (int i = 0; i < this.renderChunkNeighbours.length; i++) {
         RenderChunk renderChunk = this.renderChunkNeighbours[i];
         if (renderChunk != null) {
            renderChunk.renderChunkNeighboursUpated = false;
         }
      }

      this.chunk = null;
      this.boundingBoxParent = null;
   }

   public void resortTransparency(float x, float y, float z, ChunkCompileTaskGenerator generator) {
      CompiledChunk compiledChunk = generator.getCompiledChunk();
      if (compiledChunk.getState() != null && !compiledChunk.isLayerEmpty(EnumWorldBlockLayer.TRANSLUCENT)) {
         WorldRenderer worldRendererByLayer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT);
         this.preRenderBlocks(worldRendererByLayer, this.position);
         worldRendererByLayer.setVertexState(compiledChunk.getState());
         this.postRenderBlocks(EnumWorldBlockLayer.TRANSLUCENT, x, y, z, worldRendererByLayer, compiledChunk);
      }
   }

   public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator) {
      CompiledChunk compiledChunk = new CompiledChunk();
      boolean flag = true;
      BlockPos blockPos = new BlockPos(this.position);
      BlockPos blockPos2 = blockPos.add(15, 15, 15);
      generator.getLock().lock();

      try {
         if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
            return;
         }

         generator.setCompiledChunk(compiledChunk);
      } finally {
         generator.getLock().unlock();
      }

      VisGraph visGraph = new VisGraph();
      HashSet newHashSet = Sets.newHashSet();
      if (!this.isChunkRegionEmpty(blockPos)) {
         renderChunksUpdated++;
         ChunkCacheOF makeChunkCacheOF = this.makeChunkCacheOF(blockPos);
         makeChunkCacheOF.renderStart();
         boolean[] flags = new boolean[ENUM_WORLD_BLOCK_LAYERS.length];
         BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
         boolean exists = Reflector.ForgeBlock_canRenderInLayer.exists();
         boolean exists2 = Reflector.ForgeHooksClient_setRenderLayer.exists();

         for (Object obj : BlockPosM.getAllInBoxMutable(blockPos, blockPos2)) {
            BlockPosM blockPosM = (BlockPosM)obj;
            IBlockState blockState = makeChunkCacheOF.getBlockState(blockPosM);
            Block block = blockState.getBlock();
            if (block.isOpaqueCube()) {
               visGraph.markClosed(blockPosM);
            }

            if (ReflectorForge.blockHasTileEntity(blockState)) {
               TileEntity tileEntity = makeChunkCacheOF.getTileEntity(new BlockPos(blockPosM));
               TileEntitySpecialRenderer specialRenderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tileEntity);
               if (tileEntity != null && specialRenderer != null) {
                  compiledChunk.addTileEntity(tileEntity);
                  if (specialRenderer.forceTileEntityRender()) {
                     newHashSet.add(tileEntity);
                  }
               }
            }

            EnumWorldBlockLayer[] enumWorldBlockLayers;
            if (exists) {
               enumWorldBlockLayers = ENUM_WORLD_BLOCK_LAYERS;
            } else {
               enumWorldBlockLayers = this.blockLayersSingle;
               enumWorldBlockLayers[0] = block.getBlockLayer();
            }

            for (int i = 0; i < enumWorldBlockLayers.length; i++) {
               EnumWorldBlockLayer enumWorldBlockLayer = enumWorldBlockLayers[i];
               if (exists) {
                  boolean callBoolean = Reflector.callBoolean(block, Reflector.ForgeBlock_canRenderInLayer, enumWorldBlockLayer);
                  if (!callBoolean) {
                     continue;
                  }
               }

               if (exists2) {
                  Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, enumWorldBlockLayer);
               }

               enumWorldBlockLayer = this.fixBlockLayer(blockState, enumWorldBlockLayer);
               int n = enumWorldBlockLayer.ordinal();
               if (block.getRenderType() != -1) {
                  WorldRenderer worldRendererByLayerId = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(n);
                  worldRendererByLayerId.setBlockLayer(enumWorldBlockLayer);
                  RenderEnv renderEnv = worldRendererByLayerId.getRenderEnv(blockState, blockPosM);
                  renderEnv.setRegionRenderCacheBuilder(generator.getRegionRenderCacheBuilder());
                  if (!compiledChunk.isLayerStarted(enumWorldBlockLayer)) {
                     compiledChunk.setLayerStarted(enumWorldBlockLayer);
                     this.preRenderBlocks(worldRendererByLayerId, blockPos);
                  }

                  flags[n] |= blockRendererDispatcher.renderBlock(blockState, blockPosM, makeChunkCacheOF, worldRendererByLayerId);
                  if (renderEnv.isOverlaysRendered()) {
                     this.postRenderOverlays(generator.getRegionRenderCacheBuilder(), compiledChunk, flags);
                     renderEnv.setOverlaysRendered(false);
                  }
               }
            }

            if (exists2) {
               Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, new Object[]{null});
            }
         }

         for (EnumWorldBlockLayer enumWorldBlockLayer : ENUM_WORLD_BLOCK_LAYERS) {
            if (flags[enumWorldBlockLayer.ordinal()]) {
               compiledChunk.setLayerUsed(enumWorldBlockLayer);
            }

            if (compiledChunk.isLayerStarted(enumWorldBlockLayer)) {
               if (Config.isShaders()) {
                  SVertexBuilder.calcNormalChunkLayer(generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumWorldBlockLayer));
               }

               WorldRenderer worldRendererByLayer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumWorldBlockLayer);
               this.postRenderBlocks(enumWorldBlockLayer, x, y, z, worldRendererByLayer, compiledChunk);
               if (worldRendererByLayer.animatedSprites != null) {
                  compiledChunk.setAnimatedSprites(enumWorldBlockLayer, (BitSet)worldRendererByLayer.animatedSprites.clone());
               }
            } else {
               compiledChunk.setAnimatedSprites(enumWorldBlockLayer, (BitSet)null);
            }
         }

         makeChunkCacheOF.renderFinish();
      }

      compiledChunk.setVisibility(visGraph.computeVisibility());
      this.lockCompileTask.lock();

      try {
         HashSet newHashSet2 = Sets.newHashSet(newHashSet);
         HashSet newHashSet3 = Sets.newHashSet(this.setTileEntities);
         newHashSet2.removeAll(this.setTileEntities);
         newHashSet3.removeAll(newHashSet);
         this.setTileEntities.clear();
         this.setTileEntities.addAll(newHashSet);
         this.renderGlobal.updateTileEntities(newHashSet3, newHashSet2);
      } finally {
         this.lockCompileTask.unlock();
      }
   }

   public void finishCompileTask() {
      this.lockCompileTask.lock();

      try {
         if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
            this.compileTask.finish();
            this.compileTask = null;
         }
      } finally {
         this.lockCompileTask.unlock();
      }
   }

   public ReentrantLock getLockCompileTask() {
      return this.lockCompileTask;
   }

   public ChunkCompileTaskGenerator makeCompileTaskChunk() {
      this.lockCompileTask.lock();

      ChunkCompileTaskGenerator chunkCompileTaskGenerator;
      try {
         this.finishCompileTask();
         this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
         chunkCompileTaskGenerator = this.compileTask;
      } finally {
         this.lockCompileTask.unlock();
      }

      return chunkCompileTaskGenerator;
   }

   public ChunkCompileTaskGenerator makeCompileTaskTransparency() {
      this.lockCompileTask.lock();

      ChunkCompileTaskGenerator chunkCompileTaskGenerator;
      try {
         if (this.compileTask != null && this.compileTask.getStatus() == ChunkCompileTaskGenerator.Status.PENDING) {
            return null;
         }

         if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
            this.compileTask.finish();
            this.compileTask = null;
         }

         this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY);
         this.compileTask.setCompiledChunk(this.compiledChunk);
         ChunkCompileTaskGenerator compileTask = this.compileTask;
         chunkCompileTaskGenerator = compileTask;
      } finally {
         this.lockCompileTask.unlock();
      }

      return chunkCompileTaskGenerator;
   }

   public void preRenderBlocks(WorldRenderer worldRendererIn, BlockPos pos) {
      worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
      if (Config.isRenderRegions()) {
         byte b = 8;
         int n = pos.getX() >> b << b;
         int n2 = pos.getY() >> b << b;
         int n3 = pos.getZ() >> b << b;
         n = this.regionX;
         n3 = this.regionZ;
         worldRendererIn.setTranslation(-n, -n2, -n3);
      } else {
         worldRendererIn.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
      }
   }

   public void postRenderBlocks(EnumWorldBlockLayer layer, float x, float y, float z, WorldRenderer worldRendererIn, CompiledChunk compiledChunkIn) {
      if (layer == EnumWorldBlockLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer)) {
         worldRendererIn.sortVertexData(x, y, z);
         compiledChunkIn.setState(worldRendererIn.getVertexState());
      }

      worldRendererIn.finishDrawing();
   }

   public void initModelviewMatrix() {
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      float f = 1.000001F;
      GlStateManager.translate(-8.0F, -8.0F, -8.0F);
      GlStateManager.scale(f, f, f);
      GlStateManager.translate(8.0F, 8.0F, 8.0F);
      GlStateManager.getFloat(2982, this.modelviewMatrix);
      GlStateManager.popMatrix();
   }

   public void multModelviewMatrix() {
      GlStateManager.multMatrix(this.modelviewMatrix);
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk compiledChunkIn) {
      this.lockCompiledChunk.lock();

      try {
         this.compiledChunk = compiledChunkIn;
      } finally {
         this.lockCompiledChunk.unlock();
      }
   }

   public void stopCompileTask() {
      this.finishCompileTask();
      this.compiledChunk = CompiledChunk.DUMMY;
   }

   public void deleteGlResources() {
      this.stopCompileTask();

      for (int i = 0; i < EnumWorldBlockLayer.values().length; i++) {
         if (this.vertexBuffers[i] != null) {
            this.vertexBuffers[i].deleteGlBuffers();
         }
      }
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void setNeedsUpdate(boolean needsUpdateIn) {
      this.needsUpdate = needsUpdateIn;
      if (needsUpdateIn) {
         if (this.isWorldPlayerUpdate()) {
            this.playerUpdate = true;
         }
      } else {
         this.playerUpdate = false;
      }
   }

   public boolean isNeedsUpdate() {
      return this.needsUpdate;
   }

   public BlockPos getBlockPosOffset16(EnumFacing enumFacing) {
      return this.getPositionOffset16(enumFacing);
   }

   public BlockPos getPositionOffset16(EnumFacing enumFacing) {
      int index = enumFacing.getIndex();
      BlockPos blockPos = this.positionOffsets16[index];
      if (blockPos == null) {
         blockPos = this.getPosition().offset(enumFacing, 16);
         this.positionOffsets16[index] = blockPos;
      }

      return blockPos;
   }

   public boolean isWorldPlayerUpdate() {
      if (this.world instanceof WorldClient) {
         WorldClient world = (WorldClient)this.world;
         return world.isPlayerUpdate();
      } else {
         return false;
      }
   }

   public boolean isPlayerUpdate() {
      return this.playerUpdate;
   }

   public RegionRenderCache createRegionRenderCache(World world, BlockPos blockPos, BlockPos blockPos2, int n) {
      return new RegionRenderCache(world, blockPos, blockPos2, n);
   }

   public EnumWorldBlockLayer fixBlockLayer(IBlockState blockState, EnumWorldBlockLayer enumWorldBlockLayer) {
      if (CustomBlockLayers.isActive()) {
         EnumWorldBlockLayer renderLayer = CustomBlockLayers.getRenderLayer(blockState);
         if (renderLayer != null) {
            return renderLayer;
         }
      }

      if (!this.fixBlockLayer) {
         return enumWorldBlockLayer;
      }

      if (this.isMipmaps) {
         if (enumWorldBlockLayer == EnumWorldBlockLayer.CUTOUT) {
            Block block = blockState.getBlock();
            if (block instanceof BlockRedstoneWire) {
               return enumWorldBlockLayer;
            }

            if (block instanceof BlockCactus) {
               return enumWorldBlockLayer;
            }

            return EnumWorldBlockLayer.CUTOUT_MIPPED;
         }
      } else if (enumWorldBlockLayer == EnumWorldBlockLayer.CUTOUT_MIPPED) {
         return EnumWorldBlockLayer.CUTOUT;
      }

      return enumWorldBlockLayer;
   }

   public void postRenderOverlays(RegionRenderCacheBuilder regionRenderCacheBuilder, CompiledChunk compiledChunk, boolean[] flags) {
      this.postRenderOverlay(EnumWorldBlockLayer.CUTOUT, regionRenderCacheBuilder, compiledChunk, flags);
      this.postRenderOverlay(EnumWorldBlockLayer.CUTOUT_MIPPED, regionRenderCacheBuilder, compiledChunk, flags);
      this.postRenderOverlay(EnumWorldBlockLayer.TRANSLUCENT, regionRenderCacheBuilder, compiledChunk, flags);
   }

   public void postRenderOverlay(EnumWorldBlockLayer enumWorldBlockLayer, RegionRenderCacheBuilder regionRenderCacheBuilder, CompiledChunk compiledChunk, boolean[] flags) {
      WorldRenderer worldRendererByLayer = regionRenderCacheBuilder.getWorldRendererByLayer(enumWorldBlockLayer);
      if (worldRendererByLayer.isDrawing()) {
         compiledChunk.setLayerStarted(enumWorldBlockLayer);
         flags[enumWorldBlockLayer.ordinal()] = true;
      }
   }

   public ChunkCacheOF makeChunkCacheOF(BlockPos blockPos) {
      BlockPos blockPos2 = blockPos.add(-1, -1, -1);
      BlockPos blockPos3 = blockPos.add(16, 16, 16);
      RegionRenderCache createRegionRenderCache = this.createRegionRenderCache(this.world, blockPos2, blockPos3, 1);
      if (Reflector.MinecraftForgeClient_onRebuildChunk.exists()) {
         Reflector.call(Reflector.MinecraftForgeClient_onRebuildChunk, this.world, blockPos, createRegionRenderCache);
      }

      return new ChunkCacheOF(createRegionRenderCache, blockPos2, blockPos3, 1);
   }

   public RenderChunk getRenderChunkOffset16(ViewFrustum viewFrustum, EnumFacing enumFacing) {
      if (!this.renderChunksOffset16Updated) {
         for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            EnumFacing enumFacing2 = EnumFacing.VALUES[i];
            BlockPos blockPosOffset16 = this.getBlockPosOffset16(enumFacing2);
            this.renderChunksOfset16[i] = viewFrustum.getRenderChunk(blockPosOffset16);
         }

         this.renderChunksOffset16Updated = true;
      }

      return this.renderChunksOfset16[enumFacing.ordinal()];
   }

   public Chunk getChunk() {
      return this.getChunk(this.position);
   }

   public Chunk getChunk(BlockPos blockPos) {
      Chunk chunk = this.chunk;
      if (chunk != null && chunk.isLoaded()) {
         return chunk;
      }

      chunk = this.world.getChunkFromBlockCoords(blockPos);
      this.chunk = chunk;
      return chunk;
   }

   public boolean isChunkRegionEmpty() {
      return this.isChunkRegionEmpty(this.position);
   }

   public boolean isChunkRegionEmpty(BlockPos blockPos) {
      int n = blockPos.getY();
      int n2 = n + 15;
      return this.getChunk(blockPos).getAreLevelsEmpty(n, n2);
   }

   public void setRenderChunkNeighbour(EnumFacing enumFacing, RenderChunk renderChunk) {
      this.renderChunkNeighbours[enumFacing.ordinal()] = renderChunk;
      this.renderChunkNeighboursValid[enumFacing.ordinal()] = renderChunk;
   }

   public RenderChunk getRenderChunkNeighbour(EnumFacing enumFacing) {
      if (!this.renderChunkNeighboursUpated) {
         this.updateRenderChunkNeighboursValid();
      }

      return this.renderChunkNeighboursValid[enumFacing.ordinal()];
   }

   public ContainerLocalRenderInformation getRenderInfo() {
      return this.renderInfo;
   }

   public void updateRenderChunkNeighboursValid() {
      int n = this.getPosition().getX();
      int n2 = this.getPosition().getZ();
      int n3 = EnumFacing.NORTH.ordinal();
      int n4 = EnumFacing.SOUTH.ordinal();
      int n5 = EnumFacing.WEST.ordinal();
      int n6 = EnumFacing.EAST.ordinal();
      this.renderChunkNeighboursValid[n3] = this.renderChunkNeighbours[n3].getPosition().getZ() == n2 - 16 ? this.renderChunkNeighbours[n3] : null;
      this.renderChunkNeighboursValid[n4] = this.renderChunkNeighbours[n4].getPosition().getZ() == n2 + 16 ? this.renderChunkNeighbours[n4] : null;
      this.renderChunkNeighboursValid[n5] = this.renderChunkNeighbours[n5].getPosition().getX() == n - 16 ? this.renderChunkNeighbours[n5] : null;
      this.renderChunkNeighboursValid[n6] = this.renderChunkNeighbours[n6].getPosition().getX() == n + 16 ? this.renderChunkNeighbours[n6] : null;
      this.renderChunkNeighboursUpated = true;
   }

   public boolean isBoundingBoxInFrustum(ICamera camera, int n) {
      return this.getBoundingBoxParent().isBoundingBoxInFrustumFully(camera, n) ? true : camera.isBoundingBoxInFrustum(this.boundingBox);
   }

   public AabbFrame getBoundingBoxParent() {
      if (this.boundingBoxParent == null) {
         BlockPos position = this.getPosition();
         int n = position.getX();
         int n2 = position.getY();
         int n3 = position.getZ();
         byte b = 5;
         int n4 = n >> b << b;
         int n5 = n2 >> b << b;
         int n6 = n3 >> b << b;
         if (n4 != n || n5 != n2 || n6 != n3) {
            AabbFrame boundingBoxParent = this.renderGlobal.getRenderChunk(new BlockPos(n4, n5, n6)).getBoundingBoxParent();
            if (boundingBoxParent != null && boundingBoxParent.minX == n4 && boundingBoxParent.minY == n5 && boundingBoxParent.minZ == n6) {
               this.boundingBoxParent = boundingBoxParent;
            }
         }

         if (this.boundingBoxParent == null) {
            int n7 = 1 << b;
            this.boundingBoxParent = new AabbFrame(n4, n5, n6, n4 + n7, n5 + n7, n6 + n7);
         }
      }

      return this.boundingBoxParent;
   }

   @Override
   public String toString() {
      return "pos: " + this.getPosition() + ", frameIndex: " + this.frameIndex;
   }
}
