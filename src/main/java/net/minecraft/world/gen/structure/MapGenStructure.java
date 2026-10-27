package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public abstract class MapGenStructure extends MapGenBase {
   public MapGenStructureData structureData;
   public Map<Long, StructureStart> structureMap = Maps.newHashMap();

   public abstract String getStructureName();

   @Override
   public void recursiveGenerate(World worldIn, final int chunkX, final int chunkZ, int n, int n2, ChunkPrimer chunkPrimerIn) {
      this.initializeStructureData(worldIn);
      if (!this.structureMap.containsKey(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ))) {
         this.rand.nextInt();

         try {
            if (this.canSpawnStructureAtCoords(chunkX, chunkZ)) {
               StructureStart structureStart = this.getStructureStart(chunkX, chunkZ);
               this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ), structureStart);
               this.setStructureStart(chunkX, chunkZ, structureStart);
            }
         } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception preparing structure feature");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Feature being prepared");
            makeCategory.addCrashSectionCallable("Is feature chunk", new Callable<String>() {
               public String call() throws Exception {
                  return MapGenStructure.this.canSpawnStructureAtCoords(chunkX, chunkZ) ? "True" : "False";
               }
            });
            makeCategory.addCrashSection("Chunk location", String.format("%d,%d", chunkX, chunkZ));
            makeCategory.addCrashSectionCallable("Chunk pos hash", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ));
               }
            });
            makeCategory.addCrashSectionCallable("Structure type", new Callable<String>() {
               public String call() throws Exception {
                  return MapGenStructure.this.getClass().getCanonicalName();
               }
            });
            throw new ReportedException(makeCrashReport);
         }
      }
   }

   public boolean generateStructure(World worldIn, Random randomIn, ChunkCoordIntPair chunkCoord) {
      this.initializeStructureData(worldIn);
      int n = (chunkCoord.chunkXPos << 4) + 8;
      int n2 = (chunkCoord.chunkZPos << 4) + 8;
      boolean flag = false;

      for (StructureStart structureStart : this.structureMap.values()) {
         if (structureStart.isSizeableStructure() && structureStart.checkChunkCoordIntPair(chunkCoord) && structureStart.getBoundingBox().intersectsWith(n, n2, n + 15, n2 + 15)) {
            structureStart.generateStructure(worldIn, randomIn, new StructureBoundingBox(n, n2, n + 15, n2 + 15));
            structureStart.handleChunkCoordIntPair(chunkCoord);
            flag = true;
            this.setStructureStart(structureStart.getChunkPosX(), structureStart.getChunkPosZ(), structureStart);
         }
      }

      return flag;
   }

   public boolean checkBlockPos(BlockPos pos) {
      this.initializeStructureData(this.worldObj);
      return this.getGeneratorConfigAtPos(pos) != null;
   }

   public StructureStart getGeneratorConfigAtPos(BlockPos pos) {
      for (StructureStart structureStart : this.structureMap.values()) {
         if (structureStart.isSizeableStructure() && structureStart.getBoundingBox().isVecInside(pos)) {
            for (StructureComponent structureComponent : structureStart.getComponents()) {
               if (structureComponent.getBoundingBox().isVecInside(pos)) {
                  return structureStart;
               }
            }
         }
      }

      return null;
   }

   public boolean isPositionInStructure(World worldIn, BlockPos pos) {
      this.initializeStructureData(worldIn);

      for (StructureStart structureStart : this.structureMap.values()) {
         if (structureStart.isSizeableStructure() && structureStart.getBoundingBox().isVecInside(pos)) {
            return true;
         }
      }

      return false;
   }

   public BlockPos getClosestStrongholdPos(World worldIn, BlockPos pos) {
      this.worldObj = worldIn;
      this.initializeStructureData(worldIn);
      this.rand.setSeed(worldIn.getSeed());
      long nextLong = this.rand.nextLong();
      long nextLong2 = this.rand.nextLong();
      long l = (pos.getX() >> 4) * nextLong;
      long l2 = (pos.getZ() >> 4) * nextLong2;
      this.rand.setSeed(l ^ l2 ^ worldIn.getSeed());
      this.recursiveGenerate(worldIn, pos.getX() >> 4, pos.getZ() >> 4, 0, 0, (ChunkPrimer)null);
      double d = Double.MAX_VALUE;
      BlockPos blockPos = null;

      for (StructureStart structureStart : this.structureMap.values()) {
         if (structureStart.isSizeableStructure()) {
            StructureComponent structureComponent = structureStart.getComponents().get(0);
            BlockPos boundingBoxCenter = structureComponent.getBoundingBoxCenter();
            double distanceSq = boundingBoxCenter.distanceSq(pos);
            if (distanceSq < d) {
               d = distanceSq;
               blockPos = boundingBoxCenter;
            }
         }
      }

      if (blockPos != null) {
         return blockPos;
      }

      List coordList = this.getCoordList();
      if (coordList != null) {
         BlockPos blockPos2 = null;

         for (BlockPos blockPos3 : (Iterable<BlockPos>)coordList) {
            double distanceSq = blockPos3.distanceSq(pos);
            if (distanceSq < d) {
               d = distanceSq;
               blockPos2 = blockPos3;
            }
         }

         return blockPos2;
      } else {
         return null;
      }
   }

   public List<BlockPos> getCoordList() {
      return null;
   }

   public void initializeStructureData(World worldIn) {
      if (this.structureData == null) {
         this.structureData = (MapGenStructureData)worldIn.loadItemData(MapGenStructureData.class, this.getStructureName());
         if (this.structureData == null) {
            this.structureData = new MapGenStructureData(this.getStructureName());
            worldIn.setItemData(this.getStructureName(), this.structureData);
         } else {
            NBTTagCompound tagCompound = this.structureData.getTagCompound();

            for (String str : tagCompound.getKeySet()) {
               NBTBase tag = tagCompound.getTag(str);
               if (tag.getId() == 10) {
                  NBTTagCompound nbttagCompound = (NBTTagCompound)tag;
                  if (nbttagCompound.hasKey("ChunkX") && nbttagCompound.hasKey("ChunkZ")) {
                     int integer = nbttagCompound.getInteger("ChunkX");
                     int integer2 = nbttagCompound.getInteger("ChunkZ");
                     StructureStart structureStart = MapGenStructureIO.getStructureStart(nbttagCompound, worldIn);
                     if (structureStart != null) {
                        this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(integer, integer2), structureStart);
                     }
                  }
               }
            }
         }
      }
   }

   public void setStructureStart(int chunkX, int chunkZ, StructureStart start) {
      this.structureData.writeInstance(start.writeStructureComponentsToNBT(chunkX, chunkZ), chunkX, chunkZ);
      this.structureData.markDirty();
   }

   public abstract boolean canSpawnStructureAtCoords(int n, int n2);

   public abstract StructureStart getStructureStart(int n, int n2);
}
