package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderDebug;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
   public static Logger logger = LogManager.getLogger();
   public ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
   public byte[] blockBiomeArray = new byte[256];
   public int[] precipitationHeightMap = new int[256];
   public boolean[] updateSkylightColumns = new boolean[256];
   public boolean isChunkLoaded;
   public Map<BlockPos, TileEntity> chunkTileEntityMap = Maps.newHashMap();
   public int queuedLightChecks = 4096;
   public ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue = Queues.newConcurrentLinkedQueue();
   public ClassInheritanceMultiMap<Entity>[] entityLists = new ClassInheritanceMultiMap[16];
   public boolean isGapLightingUpdated;
   public World worldObj;
   public int xPosition;
   public boolean isTerrainPopulated;
   public boolean isLightPopulated;
   public boolean blockEntitiesPopulated;
   public boolean isModified;
   public boolean hasEntities;
   public long lastSaveTime;
   public int heightMapMinimum;
   public long inhabitedTime;
   public int zPosition;
   public int[] heightMap;

   public Chunk(World worldIn, int x, int z) {
      this.worldObj = worldIn;
      this.xPosition = x;
      this.zPosition = z;
      this.heightMap = new int[256];

      for (int i = 0; i < this.entityLists.length; i++) {
         this.entityLists[i] = new ClassInheritanceMultiMap<>(Entity.class);
      }

      Arrays.fill(this.precipitationHeightMap, -999);
      Arrays.fill(this.blockBiomeArray, (byte)-1);
   }

   public Chunk(World worldIn, ChunkPrimer primer, int x, int z) {
      this(worldIn, x, z);
      short s = 256;
      boolean flag = !worldIn.provider.getHasNoSky();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < s; k++) {
               int n = i * s * 16 | j * s | k;
               IBlockState blockState = primer.getBlockState(n);
               if (blockState.getBlock().getMaterial() != Material.air) {
                  int n2 = k >> 4;
                  if (this.storageArrays[n2] == null) {
                     this.storageArrays[n2] = new ExtendedBlockStorage(n2 << 4, flag);
                  }

                  this.storageArrays[n2].set(i, k & 15, j, blockState);
               }
            }
         }
      }
   }

   public boolean isAtLocation(int x, int z) {
      return x == this.xPosition && z == this.zPosition;
   }

   public int getHeight(BlockPos pos) {
      return this.getHeightValue(pos.getX() & 15, pos.getZ() & 15);
   }

   public int getHeightValue(int x, int z) {
      return this.heightMap[z << 4 | x];
   }

   public int getTopFilledSegment() {
      for (int i = this.storageArrays.length - 1; i >= 0; i--) {
         if (this.storageArrays[i] != null) {
            return this.storageArrays[i].getYLocation();
         }
      }

      return 0;
   }

   public ExtendedBlockStorage[] getBlockStorageArray() {
      return this.storageArrays;
   }

   public void generateHeightMap() {
      int topFilledSegment = this.getTopFilledSegment();
      this.heightMapMinimum = Integer.MAX_VALUE;

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            this.precipitationHeightMap[i + (j << 4)] = -999;

            for (int k = topFilledSegment + 16; k > 0; k--) {
               Block block0 = this.getBlock0(i, k - 1, j);
               if (block0.getLightOpacity() != 0) {
                  this.heightMap[j << 4 | i] = k;
                  if (k < this.heightMapMinimum) {
                     this.heightMapMinimum = k;
                  }
                  break;
               }
            }
         }
      }

      this.isModified = true;
   }

   public void generateSkylightMap() {
      int topFilledSegment = this.getTopFilledSegment();
      this.heightMapMinimum = Integer.MAX_VALUE;

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            this.precipitationHeightMap[i + (j << 4)] = -999;

            for (int k = topFilledSegment + 16; k > 0; k--) {
               if (this.getBlockLightOpacity(i, k - 1, j) != 0) {
                  this.heightMap[j << 4 | i] = k;
                  if (k < this.heightMapMinimum) {
                     this.heightMapMinimum = k;
                  }
                  break;
               }
            }

            if (!this.worldObj.provider.getHasNoSky()) {
               int n = 15;
               int n2 = topFilledSegment + 16 - 1;

               while (true) {
                  int blockLightOpacity = this.getBlockLightOpacity(i, n2, j);
                  if (blockLightOpacity == 0 && n != 15) {
                     blockLightOpacity = 1;
                  }

                  n -= blockLightOpacity;
                  if (n > 0) {
                     ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
                     if (extendedBlockStorage != null) {
                        extendedBlockStorage.setExtSkylightValue(i, n2 & 15, j, n);
                        this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + i, n2, (this.zPosition << 4) + j));
                     }
                  }

                  if (--n2 <= 0 || n <= 0) {
                     break;
                  }
               }
            }
         }
      }

      this.isModified = true;
   }

   public void propagateSkylightOcclusion(int x, int z) {
      this.updateSkylightColumns[x + z * 16] = true;
      this.isGapLightingUpdated = true;
   }

   public void recheckGaps(boolean flag) {
      this.worldObj.theProfiler.startSection("recheckGaps");
      if (this.worldObj.isAreaLoaded(new BlockPos(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8), 16)) {
         for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               if (this.updateSkylightColumns[i + j * 16]) {
                  this.updateSkylightColumns[i + j * 16] = false;
                  int heightValue = this.getHeightValue(i, j);
                  int n = this.xPosition * 16 + i;
                  int n2 = this.zPosition * 16 + j;
                  int n3 = Integer.MAX_VALUE;

                  for (EnumFacing enumFacing : Plane.HORIZONTAL) {
                     n3 = Math.min(n3, this.worldObj.getChunksLowestHorizon(n + enumFacing.getFrontOffsetX(), n2 + enumFacing.getFrontOffsetZ()));
                  }

                  this.checkSkylightNeighborHeight(n, n2, n3);

                  for (EnumFacing enumFacing : Plane.HORIZONTAL) {
                     this.checkSkylightNeighborHeight(n + enumFacing.getFrontOffsetX(), n2 + enumFacing.getFrontOffsetZ(), heightValue);
                  }

                  if (flag) {
                     this.worldObj.theProfiler.endSection();
                     return;
                  }
               }
            }
         }

         this.isGapLightingUpdated = false;
      }

      this.worldObj.theProfiler.endSection();
   }

   public void checkSkylightNeighborHeight(int x, int z, int maxValue) {
      int n = this.worldObj.getHeight(new BlockPos(x, 0, z)).getY();
      if (n > maxValue) {
         this.updateSkylightNeighborHeight(x, z, maxValue, n + 1);
      } else if (n < maxValue) {
         this.updateSkylightNeighborHeight(x, z, n, maxValue + 1);
      }
   }

   public void updateSkylightNeighborHeight(int x, int z, int startY, int endY) {
      if (endY > startY && this.worldObj.isAreaLoaded(new BlockPos(x, 0, z), 16)) {
         for (int startY2 = startY; startY2 < endY; startY2++) {
            this.worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, startY2, z));
         }

         this.isModified = true;
      }
   }

   public void relightBlock(int x, int y, int z) {
      int n = this.heightMap[z << 4 | x] & 0xFF;
      int n2 = n;
      if (y > n) {
         n2 = y;
      }

      while (n2 > 0 && this.getBlockLightOpacity(x, n2 - 1, z) == 0) {
         n2--;
      }

      if (n2 != n) {
         this.worldObj.markBlocksDirtyVertical(x + this.xPosition * 16, z + this.zPosition * 16, n2, n);
         this.heightMap[z << 4 | x] = n2;
         int n3 = this.xPosition * 16 + x;
         int n4 = this.zPosition * 16 + z;
         if (!this.worldObj.provider.getHasNoSky()) {
            if (n2 < n) {
               for (int i = n2; i < n; i++) {
                  ExtendedBlockStorage extendedBlockStorage = this.storageArrays[i >> 4];
                  if (extendedBlockStorage != null) {
                     extendedBlockStorage.setExtSkylightValue(x, i & 15, z, 15);
                     this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, i, (this.zPosition << 4) + z));
                  }
               }
            } else {
               for (int i = n; i < n2; i++) {
                  ExtendedBlockStorage extendedBlockStorage = this.storageArrays[i >> 4];
                  if (extendedBlockStorage != null) {
                     extendedBlockStorage.setExtSkylightValue(x, i & 15, z, 0);
                     this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, i, (this.zPosition << 4) + z));
                  }
               }
            }

            int n5 = 15;

            while (n2 > 0 && n5 > 0) {
               int blockLightOpacity = this.getBlockLightOpacity(x, --n2, z);
               if (blockLightOpacity == 0) {
                  blockLightOpacity = 1;
               }

               n5 -= blockLightOpacity;
               if (n5 < 0) {
                  n5 = 0;
               }

               ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
               if (extendedBlockStorage != null) {
                  extendedBlockStorage.setExtSkylightValue(x, n2 & 15, z, n5);
               }
            }
         }

         int n5 = this.heightMap[z << 4 | x];
         int n6 = n;
         int n7 = n5;
         if (n5 < n) {
            n6 = n5;
            n7 = n;
         }

         if (n5 < this.heightMapMinimum) {
            this.heightMapMinimum = n5;
         }

         if (!this.worldObj.provider.getHasNoSky()) {
            for (EnumFacing enumFacing : Plane.HORIZONTAL) {
               this.updateSkylightNeighborHeight(n3 + enumFacing.getFrontOffsetX(), n4 + enumFacing.getFrontOffsetZ(), n6, n7);
            }

            this.updateSkylightNeighborHeight(n3, n4, n6, n7);
         }

         this.isModified = true;
      }
   }

   public int getBlockLightOpacity(BlockPos pos) {
      return this.getBlock(pos).getLightOpacity();
   }

   public int getBlockLightOpacity(int x, int y, int z) {
      return this.getBlock0(x, y, z).getLightOpacity();
   }

   public Block getBlock0(int x, int y, int z) {
      Block air = Blocks.air;
      if (y >= 0 && y >> 4 < this.storageArrays.length) {
         ExtendedBlockStorage extendedBlockStorage = this.storageArrays[y >> 4];
         if (extendedBlockStorage != null) {
            try {
               air = extendedBlockStorage.getBlockByExtId(x, y & 15, z);
            } catch (Throwable e) {
               CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Getting block");
               throw new ReportedException(makeCrashReport);
            }
         }
      }

      return air;
   }

   public Block getBlock(final int x, final int y, final int z) {
      try {
         return this.getBlock0(x & 15, y, z & 15);
      } catch (ReportedException e) {
         CrashReportCategory makeCategory = e.getCrashReport().makeCategory("Block being got");
         makeCategory.addCrashSectionCallable("Location", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(new BlockPos(Chunk.this.xPosition * 16 + x, y, Chunk.this.zPosition * 16 + z));
            }
         });
         throw e;
      }
   }

   public Block getBlock(final BlockPos pos) {
      try {
         return this.getBlock0(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
      } catch (ReportedException e) {
         CrashReportCategory makeCategory = e.getCrashReport().makeCategory("Block being got");
         makeCategory.addCrashSectionCallable("Location", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(pos);
            }
         });
         throw e;
      }
   }

   public IBlockState getBlockState(final BlockPos pos) {
      if (this.worldObj.getWorldType() == WorldType.DEBUG_WORLD) {
         IBlockState blockState = null;
         if (pos.getY() == 60) {
            blockState = Blocks.barrier.getDefaultState();
         }

         if (pos.getY() == 70) {
            blockState = ChunkProviderDebug.findIBlockState(pos.getX(), pos.getZ());
         }

         return blockState == null ? Blocks.air.getDefaultState() : blockState;
      } else {
         try {
            if (pos.getY() >= 0 && pos.getY() >> 4 < this.storageArrays.length) {
               ExtendedBlockStorage extendedBlockStorage = this.storageArrays[pos.getY() >> 4];
               if (extendedBlockStorage != null) {
                  int n = pos.getX() & 15;
                  int n2 = pos.getY() & 15;
                  int n3 = pos.getZ() & 15;
                  return extendedBlockStorage.get(n, n2, n3);
               }
            }

            return Blocks.air.getDefaultState();
         } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Getting block state");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block being got");
            makeCategory.addCrashSectionCallable("Location", new Callable<String>() {
               public String call() throws Exception {
                  return CrashReportCategory.getCoordinateInfo(pos);
               }
            });
            throw new ReportedException(makeCrashReport);
         }
      }
   }

   public int getBlockMetadata(int x, int y, int z) {
      if (y >> 4 >= this.storageArrays.length) {
         return 0;
      }

      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[y >> 4];
      return extendedBlockStorage != null ? extendedBlockStorage.getExtBlockMetadata(x, y & 15, z) : 0;
   }

   public int getBlockMetadata(BlockPos pos) {
      return this.getBlockMetadata(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
   }

   public IBlockState setBlockState(BlockPos pos, IBlockState state) {
      int n = pos.getX() & 15;
      int n2 = pos.getY();
      int n3 = pos.getZ() & 15;
      int n4 = n3 << 4 | n;
      if (n2 >= this.precipitationHeightMap[n4] - 1) {
         this.precipitationHeightMap[n4] = -999;
      }

      int n5 = this.heightMap[n4];
      IBlockState blockState = this.getBlockState(pos);
      if (blockState == state) {
         return null;
      }

      Block block = state.getBlock();
      Block block2 = blockState.getBlock();
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
      boolean flag = false;
      if (extendedBlockStorage == null) {
         if (block == Blocks.air) {
            return null;
         }

         extendedBlockStorage = this.storageArrays[n2 >> 4] = new ExtendedBlockStorage(n2 >> 4 << 4, !this.worldObj.provider.getHasNoSky());
         flag = n2 >= n5;
      }

      extendedBlockStorage.set(n, n2 & 15, n3, state);
      if (block2 != block) {
         if (!this.worldObj.isRemote) {
            block2.breakBlock(this.worldObj, pos, blockState);
         } else if (block2 instanceof ITileEntityProvider) {
            this.worldObj.removeTileEntity(pos);
         }
      }

      if (extendedBlockStorage.getBlockByExtId(n, n2 & 15, n3) != block) {
         return null;
      }

      if (flag) {
         this.generateSkylightMap();
      } else {
         int lightOpacity = block.getLightOpacity();
         int lightOpacity2 = block2.getLightOpacity();
         if (lightOpacity > 0) {
            if (n2 >= n5) {
               this.relightBlock(n, n2 + 1, n3);
            }
         } else if (n2 == n5 - 1) {
            this.relightBlock(n, n2, n3);
         }

         if (lightOpacity != lightOpacity2 && (lightOpacity < lightOpacity2 || this.getLightFor(EnumSkyBlock.SKY, pos) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, pos) > 0)) {
            this.propagateSkylightOcclusion(n, n3);
         }
      }

      if (block2 instanceof ITileEntityProvider) {
         TileEntity tileEntity = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
         if (tileEntity != null) {
            tileEntity.updateContainingBlockInfo();
         }
      }

      if (!this.worldObj.isRemote && block2 != block) {
         block.onBlockAdded(this.worldObj, pos, state);
      }

      if (block instanceof ITileEntityProvider) {
         TileEntity tileEntity = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
         if (tileEntity == null) {
            tileEntity = ((ITileEntityProvider)block).createNewTileEntity(this.worldObj, block.getMetaFromState(state));
            this.worldObj.setTileEntity(pos, tileEntity);
         }

         if (tileEntity != null) {
            tileEntity.updateContainingBlockInfo();
         }
      }

      this.isModified = true;
      return blockState;
   }

   public int getLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos) {
      int n = pos.getX() & 15;
      int n2 = pos.getY();
      int n3 = pos.getZ() & 15;
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
      return extendedBlockStorage == null
         ? (this.canSeeSky(pos) ? enumSkyBlock.defaultLightValue : 0)
         : (
            enumSkyBlock == EnumSkyBlock.SKY
               ? (this.worldObj.provider.getHasNoSky() ? 0 : extendedBlockStorage.getExtSkylightValue(n, n2 & 15, n3))
               : (enumSkyBlock == EnumSkyBlock.BLOCK ? extendedBlockStorage.getExtBlocklightValue(n, n2 & 15, n3) : enumSkyBlock.defaultLightValue)
         );
   }

   public void setLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos, int value) {
      int n = pos.getX() & 15;
      int n2 = pos.getY();
      int n3 = pos.getZ() & 15;
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
      if (extendedBlockStorage == null) {
         extendedBlockStorage = this.storageArrays[n2 >> 4] = new ExtendedBlockStorage(n2 >> 4 << 4, !this.worldObj.provider.getHasNoSky());
         this.generateSkylightMap();
      }

      this.isModified = true;
      if (enumSkyBlock == EnumSkyBlock.SKY) {
         if (!this.worldObj.provider.getHasNoSky()) {
            extendedBlockStorage.setExtSkylightValue(n, n2 & 15, n3, value);
         }
      } else if (enumSkyBlock == EnumSkyBlock.BLOCK) {
         extendedBlockStorage.setExtBlocklightValue(n, n2 & 15, n3, value);
      }
   }

   public int getLightSubtracted(BlockPos pos, int amount) {
      int n = pos.getX() & 15;
      int n2 = pos.getY();
      int n3 = pos.getZ() & 15;
      ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
      if (extendedBlockStorage != null) {
         int n4 = this.worldObj.provider.getHasNoSky() ? 0 : extendedBlockStorage.getExtSkylightValue(n, n2 & 15, n3);
         n4 -= amount;
         int extBlocklightValue = extendedBlockStorage.getExtBlocklightValue(n, n2 & 15, n3);
         if (extBlocklightValue > n4) {
            n4 = extBlocklightValue;
         }

         return n4;
      } else {
         return !this.worldObj.provider.getHasNoSky() && amount < EnumSkyBlock.SKY.defaultLightValue ? EnumSkyBlock.SKY.defaultLightValue - amount : 0;
      }
   }

   public void addEntity(Entity entityIn) {
      this.hasEntities = true;
      int n = MathHelper.floor_double(entityIn.posX / 16.0);
      int n2 = MathHelper.floor_double(entityIn.posZ / 16.0);
      if (n != this.xPosition || n2 != this.zPosition) {
         logger.warn(
            "Wrong location! (" + n + ", " + n2 + ") should be (" + this.xPosition + ", " + this.zPosition + "), " + entityIn, new Object[]{entityIn}
         );
         entityIn.setDead();
      }

      int n3 = MathHelper.floor_double(entityIn.posY / 16.0);
      if (n3 < 0) {
         n3 = 0;
      }

      if (n3 >= this.entityLists.length) {
         n3 = this.entityLists.length - 1;
      }

      entityIn.addedToChunk = true;
      entityIn.chunkCoordX = this.xPosition;
      entityIn.chunkCoordY = n3;
      entityIn.chunkCoordZ = this.zPosition;
      this.entityLists[n3].add(entityIn);
   }

   public void removeEntity(Entity entityIn) {
      this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
   }

   public void removeEntityAtIndex(Entity entityIn, int n) {
      if (n < 0) {
         n = 0;
      }

      if (n >= this.entityLists.length) {
         n = this.entityLists.length - 1;
      }

      this.entityLists[n].remove(entityIn);
   }

   public boolean canSeeSky(BlockPos pos) {
      int n = pos.getX() & 15;
      int n2 = pos.getY();
      int n3 = pos.getZ() & 15;
      return n2 >= this.heightMap[n3 << 4 | n];
   }

   public TileEntity createNewTileEntity(BlockPos pos) {
      Block block = this.getBlock(pos);
      return !block.hasTileEntity() ? null : ((ITileEntityProvider)block).createNewTileEntity(this.worldObj, this.getBlockMetadata(pos));
   }

   public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType enumCreateEntityType) {
      TileEntity tileEntity = this.chunkTileEntityMap.get(pos);
      if (tileEntity == null) {
         if (enumCreateEntityType == Chunk.EnumCreateEntityType.IMMEDIATE) {
            tileEntity = this.createNewTileEntity(pos);
            this.worldObj.setTileEntity(pos, tileEntity);
         } else if (enumCreateEntityType == Chunk.EnumCreateEntityType.QUEUED) {
            this.tileEntityPosQueue.add(pos);
         }
      } else if (tileEntity.isInvalid()) {
         this.chunkTileEntityMap.remove(pos);
         return null;
      }

      return tileEntity;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
      this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
      if (this.isChunkLoaded) {
         this.worldObj.addTileEntity(tileEntityIn);
      }
   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
      tileEntityIn.setWorldObj(this.worldObj);
      tileEntityIn.setPos(pos);
      if (this.getBlock(pos) instanceof ITileEntityProvider) {
         if (this.chunkTileEntityMap.containsKey(pos)) {
            this.chunkTileEntityMap.get(pos).invalidate();
         }

         tileEntityIn.validate();
         this.chunkTileEntityMap.put(pos, tileEntityIn);
      }
   }

   public void removeTileEntity(BlockPos pos) {
      if (this.isChunkLoaded) {
         TileEntity tileEntity = this.chunkTileEntityMap.remove(pos);
         if (tileEntity != null) {
            tileEntity.invalidate();
         }
      }
   }

   public void onChunkLoad() {
      this.isChunkLoaded = true;
      this.worldObj.addTileEntities(this.chunkTileEntityMap.values());

      for (int i = 0; i < this.entityLists.length; i++) {
         for (Entity entity : this.entityLists[i]) {
            entity.onChunkLoad();
         }

         this.worldObj.loadEntities(this.entityLists[i]);
      }
   }

   public void onChunkUnload() {
      this.isChunkLoaded = false;

      for (TileEntity tileEntity : this.chunkTileEntityMap.values()) {
         this.worldObj.markTileEntityForRemoval(tileEntity);
      }

      for (int i = 0; i < this.entityLists.length; i++) {
         this.worldObj.unloadEntities(this.entityLists[i]);
      }
   }

   public void setChunkModified() {
      this.isModified = true;
   }

   public void getEntitiesWithinAABBForEntity(Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> predicate) {
      int n = MathHelper.floor_double((aabb.minY - 2.0) / 16.0);
      int n2 = MathHelper.floor_double((aabb.maxY + 2.0) / 16.0);
      n = MathHelper.clamp_int(n, 0, this.entityLists.length - 1);
      n2 = MathHelper.clamp_int(n2, 0, this.entityLists.length - 1);

      for (int i = n; i <= n2; i++) {
         if (!this.entityLists[i].isEmpty()) {
            for (Entity entity : this.entityLists[i]) {
               if (entity.getEntityBoundingBox().intersectsWith(aabb) && entity != entityIn) {
                  if (predicate == null || predicate.apply(entity)) {
                     listToFill.add(entity);
                  }

                  Entity[] parts = entity.getParts();
                  if (parts != null) {
                     for (int j = 0; j < parts.length; j++) {
                        entity = parts[j];
                        if (entity != entityIn && entity.getEntityBoundingBox().intersectsWith(aabb) && (predicate == null || predicate.apply(entity))) {
                           listToFill.add(entity);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> predicate) {
      int n = MathHelper.floor_double((aabb.minY - 2.0) / 16.0);
      int n2 = MathHelper.floor_double((aabb.maxY + 2.0) / 16.0);
      n = MathHelper.clamp_int(n, 0, this.entityLists.length - 1);
      n2 = MathHelper.clamp_int(n2, 0, this.entityLists.length - 1);

      for (int i = n; i <= n2; i++) {
         for (Entity entity : this.entityLists[i].getByClass(entityClass)) {
            if (entity.getEntityBoundingBox().intersectsWith(aabb) && (predicate == null || predicate.apply((T)entity))) {
               listToFill.add((T)entity);
            }
         }
      }
   }

   public boolean needsSaving(boolean flag) {
      if (flag) {
         if (this.hasEntities && this.worldObj.getTotalWorldTime() != this.lastSaveTime || this.isModified) {
            return true;
         }
      } else if (this.hasEntities && this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L) {
         return true;
      }

      return this.isModified;
   }

   public Random getRandomWithSeed(long seed) {
      return new Random(
         this.worldObj.getSeed()
               + this.xPosition * this.xPosition * 4987142
               + this.xPosition * 5947611
               + this.zPosition * this.zPosition * 4392871L
               + this.zPosition * 389711
            ^ seed
      );
   }

   public boolean isEmpty() {
      return false;
   }

   public void populateChunk(IChunkProvider chunkProvider, IChunkProvider chunkProvider2, int x, int z) {
      boolean chunkExists = chunkProvider.chunkExists(x, z - 1);
      boolean chunkExists2 = chunkProvider.chunkExists(x + 1, z);
      boolean chunkExists3 = chunkProvider.chunkExists(x, z + 1);
      boolean chunkExists4 = chunkProvider.chunkExists(x - 1, z);
      boolean chunkExists5 = chunkProvider.chunkExists(x - 1, z - 1);
      boolean chunkExists6 = chunkProvider.chunkExists(x + 1, z + 1);
      boolean chunkExists7 = chunkProvider.chunkExists(x - 1, z + 1);
      boolean chunkExists8 = chunkProvider.chunkExists(x + 1, z - 1);
      if (chunkExists2 && chunkExists3 && chunkExists6) {
         if (!this.isTerrainPopulated) {
            chunkProvider.populate(chunkProvider2, x, z);
         } else {
            chunkProvider.populateChunk(chunkProvider2, this, x, z);
         }
      }

      if (chunkExists4 && chunkExists3 && chunkExists7) {
         Chunk provideChunk = chunkProvider.provideChunk(x - 1, z);
         if (!provideChunk.isTerrainPopulated) {
            chunkProvider.populate(chunkProvider2, x - 1, z);
         } else {
            chunkProvider.populateChunk(chunkProvider2, provideChunk, x - 1, z);
         }
      }

      if (chunkExists && chunkExists2 && chunkExists8) {
         Chunk provideChunk = chunkProvider.provideChunk(x, z - 1);
         if (!provideChunk.isTerrainPopulated) {
            chunkProvider.populate(chunkProvider2, x, z - 1);
         } else {
            chunkProvider.populateChunk(chunkProvider2, provideChunk, x, z - 1);
         }
      }

      if (chunkExists5 && chunkExists && chunkExists4) {
         Chunk provideChunk = chunkProvider.provideChunk(x - 1, z - 1);
         if (!provideChunk.isTerrainPopulated) {
            chunkProvider.populate(chunkProvider2, x - 1, z - 1);
         } else {
            chunkProvider.populateChunk(chunkProvider2, provideChunk, x - 1, z - 1);
         }
      }
   }

   public BlockPos getPrecipitationHeight(BlockPos pos) {
      int n = pos.getX() & 15;
      int n2 = pos.getZ() & 15;
      int n3 = n | n2 << 4;
      BlockPos blockPos = new BlockPos(pos.getX(), this.precipitationHeightMap[n3], pos.getZ());
      if (blockPos.getY() == -999) {
         int n4 = this.getTopFilledSegment() + 15;
         blockPos = new BlockPos(pos.getX(), n4, pos.getZ());
         int n5 = -1;

         while (blockPos.getY() > 0 && n5 == -1) {
            Block block = this.getBlock(blockPos);
            Material material = block.getMaterial();
            if (!material.blocksMovement() && !material.isLiquid()) {
               blockPos = blockPos.down();
            } else {
               n5 = blockPos.getY() + 1;
            }
         }

         this.precipitationHeightMap[n3] = n5;
      }

      return new BlockPos(pos.getX(), this.precipitationHeightMap[n3], pos.getZ());
   }

   public void populateBlockEntities(boolean flag) {
      if (this.isGapLightingUpdated && !this.worldObj.provider.getHasNoSky() && !flag) {
         this.recheckGaps(this.worldObj.isRemote);
      }

      this.blockEntitiesPopulated = true;
      if (!this.isLightPopulated && this.isTerrainPopulated) {
         this.populate();
      }

      while (!this.tileEntityPosQueue.isEmpty()) {
         BlockPos poll = this.tileEntityPosQueue.poll();
         if (this.getTileEntity(poll, Chunk.EnumCreateEntityType.CHECK) == null && this.getBlock(poll).hasTileEntity()) {
            TileEntity createNewTileEntity = this.createNewTileEntity(poll);
            this.worldObj.setTileEntity(poll, createNewTileEntity);
            this.worldObj.markBlockRangeForRenderUpdate(poll, poll);
         }
      }
   }

   public boolean isPopulated() {
      return this.blockEntitiesPopulated && this.isTerrainPopulated && this.isLightPopulated;
   }

   public ChunkCoordIntPair getChunkCoordIntPair() {
      return new ChunkCoordIntPair(this.xPosition, this.zPosition);
   }

   public boolean getAreLevelsEmpty(int startY, int endY) {
      if (startY < 0) {
         startY = 0;
      }

      if (endY >= 256) {
         endY = 255;
      }

      for (int startY2 = startY; startY2 <= endY; startY2 += 16) {
         ExtendedBlockStorage extendedBlockStorage = this.storageArrays[startY2 >> 4];
         if (extendedBlockStorage != null && !extendedBlockStorage.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void setStorageArrays(ExtendedBlockStorage[] newStorageArrays) {
      if (this.storageArrays.length != newStorageArrays.length) {
         logger.warn("Could not set level chunk sections, array length is " + newStorageArrays.length + " instead of " + this.storageArrays.length);
      } else {
         for (int i = 0; i < this.storageArrays.length; i++) {
            this.storageArrays[i] = newStorageArrays[i];
         }
      }
   }

   public void fillChunk(byte[] bytes, int n, boolean flag) {
      int n2 = 0;
      boolean flag2 = !this.worldObj.provider.getHasNoSky();

      for (int i = 0; i < this.storageArrays.length; i++) {
         if ((n & 1 << i) != 0) {
            if (this.storageArrays[i] == null) {
               this.storageArrays[i] = new ExtendedBlockStorage(i << 4, flag2);
            }

            char[] data = this.storageArrays[i].getData();

            for (int j = 0; j < data.length; j++) {
               data[j] = (char)((bytes[n2 + 1] & 255) << 8 | bytes[n2] & 0xFF);
               n2 += 2;
            }
         } else if (flag && this.storageArrays[i] != null) {
            this.storageArrays[i] = null;
         }
      }

      for (int i = 0; i < this.storageArrays.length; i++) {
         if ((n & 1 << i) != 0 && this.storageArrays[i] != null) {
            NibbleArray blocklightArray = this.storageArrays[i].getBlocklightArray();
            System.arraycopy(bytes, n2, blocklightArray.getData(), 0, blocklightArray.getData().length);
            n2 += blocklightArray.getData().length;
         }
      }

      if (flag2) {
         for (int i = 0; i < this.storageArrays.length; i++) {
            if ((n & 1 << i) != 0 && this.storageArrays[i] != null) {
               NibbleArray skylightArray = this.storageArrays[i].getSkylightArray();
               System.arraycopy(bytes, n2, skylightArray.getData(), 0, skylightArray.getData().length);
               n2 += skylightArray.getData().length;
            }
         }
      }

      if (flag) {
         System.arraycopy(bytes, n2, this.blockBiomeArray, 0, this.blockBiomeArray.length);
         int n3 = n2 + this.blockBiomeArray.length;
      }

      for (int i = 0; i < this.storageArrays.length; i++) {
         if (this.storageArrays[i] != null && (n & 1 << i) != 0) {
            this.storageArrays[i].removeInvalidBlocks();
         }
      }

      this.isLightPopulated = true;
      this.isTerrainPopulated = true;
      this.generateHeightMap();

      for (TileEntity tileEntity : this.chunkTileEntityMap.values()) {
         tileEntity.updateContainingBlockInfo();
      }
   }

   public BiomeGenBase getBiome(BlockPos pos, WorldChunkManager chunkManager) {
      int n = pos.getX() & 15;
      int n2 = pos.getZ() & 15;
      int n3 = this.blockBiomeArray[n2 << 4 | n] & 255;
      if (n3 == 255) {
         BiomeGenBase biomeGenerator = chunkManager.getBiomeGenerator(pos, BiomeGenBase.plains);
         n3 = biomeGenerator.biomeID;
         this.blockBiomeArray[n2 << 4 | n] = (byte)(n3 & 0xFF);
      }

      BiomeGenBase biome = BiomeGenBase.getBiome(n3);
      return biome == null ? BiomeGenBase.plains : biome;
   }

   public byte[] getBiomeArray() {
      return this.blockBiomeArray;
   }

   public void setBiomeArray(byte[] biomeArray) {
      if (this.blockBiomeArray.length != biomeArray.length) {
         logger.warn("Could not set level chunk biomes, array length is " + biomeArray.length + " instead of " + this.blockBiomeArray.length);
      } else {
         for (int i = 0; i < this.blockBiomeArray.length; i++) {
            this.blockBiomeArray[i] = biomeArray[i];
         }
      }
   }

   public void resetRelightChecks() {
      this.queuedLightChecks = 0;
   }

   public void enqueueRelightChecks() {
      BlockPos blockPos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);

      for (int i = 0; i < 8; i++) {
         if (this.queuedLightChecks >= 4096) {
            return;
         }

         int n = this.queuedLightChecks % 16;
         int n2 = this.queuedLightChecks / 16 % 16;
         int n3 = this.queuedLightChecks / 256;
         this.queuedLightChecks++;

         for (int j = 0; j < 16; j++) {
            BlockPos blockPos2 = blockPos.add(n2, (n << 4) + j, n3);
            boolean flag = j == 0 || j == 15 || n2 == 0 || n2 == 15 || n3 == 0 || n3 == 15;
            if (this.storageArrays[n] == null && flag
               || this.storageArrays[n] != null && this.storageArrays[n].getBlockByExtId(n2, j, n3).getMaterial() == Material.air) {
               for (EnumFacing enumFacing : EnumFacing.values()) {
                  BlockPos offset = blockPos2.offset(enumFacing);
                  if (this.worldObj.getBlockState(offset).getBlock().getLightValue() > 0) {
                     this.worldObj.checkLight(offset);
                  }
               }

               this.worldObj.checkLight(blockPos2);
            }
         }
      }
   }

   public void populate() {
      this.isTerrainPopulated = true;
      this.isLightPopulated = true;
      BlockPos blockPos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);
      if (!this.worldObj.provider.getHasNoSky()) {
         if (this.worldObj.isAreaLoaded(blockPos.add(-1, 0, -1), blockPos.add(16, this.worldObj.getSeaLevel(), 16))) {
            label44:
            for (int i = 0; i < 16; i++) {
               for (int j = 0; j < 16; j++) {
                  if (!this.updateLight(i, j)) {
                     this.isLightPopulated = false;
                     break label44;
                  }
               }
            }

            if (this.isLightPopulated) {
               for (EnumFacing enumFacing : Plane.HORIZONTAL) {
                  int n = enumFacing.getAxisDirection() == AxisDirection.POSITIVE ? 16 : 1;
                  this.worldObj.getChunkFromBlockCoords(blockPos.offset(enumFacing, n)).updateLight(enumFacing.getOpposite());
               }

               this.recheckGaps();
            }
         } else {
            this.isLightPopulated = false;
         }
      }
   }

   public void recheckGaps() {
      for (int i = 0; i < this.updateSkylightColumns.length; i++) {
         this.updateSkylightColumns[i] = true;
      }

      this.recheckGaps(false);
   }

   public void updateLight(EnumFacing facing) {
      if (this.isTerrainPopulated) {
         if (facing == EnumFacing.EAST) {
            for (int i = 0; i < 16; i++) {
               this.updateLight(15, i);
            }
         } else if (facing == EnumFacing.WEST) {
            for (int i = 0; i < 16; i++) {
               this.updateLight(0, i);
            }
         } else if (facing == EnumFacing.SOUTH) {
            for (int i = 0; i < 16; i++) {
               this.updateLight(i, 15);
            }
         } else if (facing == EnumFacing.NORTH) {
            for (int i = 0; i < 16; i++) {
               this.updateLight(i, 0);
            }
         }
      }
   }

   public boolean updateLight(int x, int z) {
      int topFilledSegment = this.getTopFilledSegment();
      boolean flag = false;
      boolean flag2 = false;
      MutableBlockPos mutableBlockPos = new MutableBlockPos((this.xPosition << 4) + x, 0, (this.zPosition << 4) + z);

      for (int i = topFilledSegment + 16 - 1; i > this.worldObj.getSeaLevel() || i > 0 && !flag2; i--) {
         mutableBlockPos.set(mutableBlockPos.getX(), i, mutableBlockPos.getZ());
         int blockLightOpacity = this.getBlockLightOpacity(mutableBlockPos);
         if (blockLightOpacity == 255 && mutableBlockPos.getY() < this.worldObj.getSeaLevel()) {
            flag2 = true;
         }

         if (!flag && blockLightOpacity > 0) {
            flag = true;
         } else if (flag && blockLightOpacity == 0 && !this.worldObj.checkLight(mutableBlockPos)) {
            return false;
         }
      }

      for (int i = mutableBlockPos.getY(); i > 0; i--) {
         mutableBlockPos.set(mutableBlockPos.getX(), i, mutableBlockPos.getZ());
         if (this.getBlock(mutableBlockPos).getLightValue() > 0) {
            this.worldObj.checkLight(mutableBlockPos);
         }
      }

      return true;
   }

   public boolean isLoaded() {
      return this.isChunkLoaded;
   }

   public void setChunkLoaded(boolean loaded) {
      this.isChunkLoaded = loaded;
   }

   public World getWorld() {
      return this.worldObj;
   }

   public int[] getHeightMap() {
      return this.heightMap;
   }

   public void setHeightMap(int[] newHeightMap) {
      if (this.heightMap.length != newHeightMap.length) {
         logger.warn("Could not set level chunk heightmap, array length is " + newHeightMap.length + " instead of " + this.heightMap.length);
      } else {
         for (int i = 0; i < this.heightMap.length; i++) {
            this.heightMap[i] = newHeightMap[i];
         }
      }
   }

   public Map<BlockPos, TileEntity> getTileEntityMap() {
      return this.chunkTileEntityMap;
   }

   public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
      return this.entityLists;
   }

   public boolean isTerrainPopulated() {
      return this.isTerrainPopulated;
   }

   public void setTerrainPopulated(boolean terrainPopulated) {
      this.isTerrainPopulated = terrainPopulated;
   }

   public boolean isLightPopulated() {
      return this.isLightPopulated;
   }

   public void setLightPopulated(boolean lightPopulated) {
      this.isLightPopulated = lightPopulated;
   }

   public void setModified(boolean modified) {
      this.isModified = modified;
   }

   public void setHasEntities(boolean hasEntitiesIn) {
      this.hasEntities = hasEntitiesIn;
   }

   public void setLastSaveTime(long saveTime) {
      this.lastSaveTime = saveTime;
   }

   public int getLowestHeight() {
      return this.heightMapMinimum;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long newInhabitedTime) {
      this.inhabitedTime = newInhabitedTime;
   }

   public enum EnumCreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }
}
