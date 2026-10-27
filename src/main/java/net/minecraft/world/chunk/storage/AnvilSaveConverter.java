package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilSaveConverter extends SaveFormatOld {
   public static Logger logger = LogManager.getLogger();

   public AnvilSaveConverter(File savesDirectoryIn) {
      super(savesDirectoryIn);
   }

   @Override
   public String getName() {
      return "Anvil";
   }

   @Override
   public List<SaveFormatComparator> getSaveList() throws AnvilConverterException {
      if (this.savesDirectory != null && this.savesDirectory.exists() && this.savesDirectory.isDirectory()) {
         ArrayList newArrayList = Lists.newArrayList();
         File[] listFiles = this.savesDirectory.listFiles();

         for (File file : listFiles) {
            if (file.isDirectory()) {
               String name = file.getName();
               WorldInfo worldInfo = this.getWorldInfo(name);
               if (worldInfo != null && (worldInfo.getSaveVersion() == 19132 || worldInfo.getSaveVersion() == 19133)) {
                  boolean flag = worldInfo.getSaveVersion() != this.getSaveVersion();
                  String worldName = worldInfo.getWorldName();
                  if (StringUtils.isEmpty(worldName)) {
                     worldName = name;
                  }

                  long l = 0L;
                  newArrayList.add(
                     new SaveFormatComparator(
                        name, worldName, worldInfo.getLastTimePlayed(), l, worldInfo.getGameType(), flag, worldInfo.isHardcoreModeEnabled(), worldInfo.areCommandsAllowed()
                     )
                  );
               }
            }
         }

         return newArrayList;
      } else {
         throw new AnvilConverterException("Unable to read or access folder where game worlds are saved!");
      }
   }

   public int getSaveVersion() {
      return 19133;
   }

   @Override
   public void flushCache() {
      RegionFileCache.clearRegionFileReferences();
   }

   @Override
   public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata) {
      return new AnvilSaveHandler(this.savesDirectory, saveName, storePlayerdata);
   }

   @Override
   public boolean isConvertible(String saveName) {
      WorldInfo worldInfo = this.getWorldInfo(saveName);
      return worldInfo != null && worldInfo.getSaveVersion() == 19132;
   }

   @Override
   public boolean isOldMapFormat(String saveName) {
      WorldInfo worldInfo = this.getWorldInfo(saveName);
      return worldInfo != null && worldInfo.getSaveVersion() != this.getSaveVersion();
   }

   @Override
   public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
      progressCallback.setLoadingProgress(0);
      ArrayList newArrayList = Lists.newArrayList();
      ArrayList newArrayList2 = Lists.newArrayList();
      ArrayList newArrayList3 = Lists.newArrayList();
      File file = new File(this.savesDirectory, filename);
      File file2 = new File(file, "DIM-1");
      File file3 = new File(file, "DIM1");
      logger.info("Scanning folders...");
      this.addRegionFilesToCollection(file, newArrayList);
      if (file2.exists()) {
         this.addRegionFilesToCollection(file2, newArrayList2);
      }

      if (file3.exists()) {
         this.addRegionFilesToCollection(file3, newArrayList3);
      }

      int n = newArrayList.size() + newArrayList2.size() + newArrayList3.size();
      logger.info("Total conversion count is " + n);
      WorldInfo worldInfo = this.getWorldInfo(filename);
      WorldChunkManager worldChunkManager = null;
      if (worldInfo.getTerrainType() == WorldType.FLAT) {
         worldChunkManager = new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F);
      } else {
         worldChunkManager = new WorldChunkManager(worldInfo.getSeed(), worldInfo.getTerrainType(), worldInfo.getGeneratorOptions());
      }

      this.convertFile(new File(file, "region"), newArrayList, worldChunkManager, 0, n, progressCallback);
      this.convertFile(new File(file2, "region"), newArrayList2, new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F), newArrayList.size(), n, progressCallback);
      this.convertFile(new File(file3, "region"), newArrayList3, new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F), newArrayList.size() + newArrayList2.size(), n, progressCallback);
      worldInfo.setSaveVersion(19133);
      if (worldInfo.getTerrainType() == WorldType.DEFAULT_1_1) {
         worldInfo.setTerrainType(WorldType.DEFAULT);
      }

      this.createFile(filename);
      ISaveHandler saveLoader = this.getSaveLoader(filename, false);
      saveLoader.saveWorldInfo(worldInfo);
      return true;
   }

   public void createFile(String filename) {
      File file = new File(this.savesDirectory, filename);
      if (!file.exists()) {
         logger.warn("Unable to create level.dat_mcr backup");
      } else {
         File file2 = new File(file, "level.dat");
         if (!file2.exists()) {
            logger.warn("Unable to create level.dat_mcr backup");
         } else {
            File file3 = new File(file, "level.dat_mcr");
            if (!file2.renameTo(file3)) {
               logger.warn("Unable to create level.dat_mcr backup");
            }
         }
      }
   }

   public void convertFile(File file, Iterable<File> iterable, WorldChunkManager worldChunkManager, int n, int n2, IProgressUpdate progressUpdate) {
      for (File file2 : iterable) {
         this.convertChunks(file, file2, worldChunkManager, n, n2, progressUpdate);
         n++;
         int round = (int)Math.round(100.0 * n / n2);
         progressUpdate.setLoadingProgress(round);
      }
   }

   public void convertChunks(File file, File file2, WorldChunkManager worldChunkManager, int n, int n2, IProgressUpdate progressCallback) {
      try {
         String name = file2.getName();
         RegionFile regionFile = new RegionFile(file2);
         RegionFile regionFile2 = new RegionFile(new File(file, name.substring(0, name.length() - ".mcr".length()) + ".mca"));

         for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
               if (regionFile.isChunkSaved(i, j) && !regionFile2.isChunkSaved(i, j)) {
                  DataInputStream chunkDataInputStream = regionFile.getChunkDataInputStream(i, j);
                  if (chunkDataInputStream == null) {
                     logger.warn("Failed to fetch input stream");
                  } else {
                     NBTTagCompound read = CompressedStreamTools.read(chunkDataInputStream);
                     chunkDataInputStream.close();
                     NBTTagCompound compoundTag = read.getCompoundTag("Level");
                     ChunkLoader.AnvilConverterData load = ChunkLoader.load(compoundTag);
                     NBTTagCompound nbttagCompound = new NBTTagCompound();
                     NBTTagCompound nbttagCompound2 = new NBTTagCompound();
                     nbttagCompound.setTag("Level", nbttagCompound2);
                     ChunkLoader.convertToAnvilFormat(load, nbttagCompound2, worldChunkManager);
                     DataOutputStream chunkDataOutputStream = regionFile2.getChunkDataOutputStream(i, j);
                     CompressedStreamTools.write(nbttagCompound, chunkDataOutputStream);
                     chunkDataOutputStream.close();
                  }
               }
            }

            int round = (int)Math.round(100.0 * (n * 1024) / (n2 * 1024));
            int round2 = (int)Math.round(100.0 * ((i + 1) * 32 + n * 1024) / (n2 * 1024));
            if (round2 > round) {
               progressCallback.setLoadingProgress(round2);
            }
         }

         regionFile.close();
         regionFile2.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void addRegionFilesToCollection(File worldDir, Collection<File> collection) {
      File file = new File(worldDir, "region");
      File[] listFiles = file.listFiles(new FilenameFilter() {
         @Override
         public boolean accept(File file2, String str) {
            return str.endsWith(".mcr");
         }
      });
      if (listFiles != null) {
         Collections.addAll(collection, listFiles);
      }
   }
}
