package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RegionFileCache {
   public static Map<File, RegionFile> regionsByFilename = Maps.newHashMap();

   public static synchronized RegionFile createOrLoadRegionFile(File worldDir, int chunkX, int chunkZ) {
      File file = new File(worldDir, "region");
      File file2 = new File(file, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");
      RegionFile regionFile = regionsByFilename.get(file2);
      if (regionFile != null) {
         return regionFile;
      }

      if (!file.exists()) {
         file.mkdirs();
      }

      if (regionsByFilename.size() >= 256) {
         clearRegionFileReferences();
      }

      RegionFile regionFile2 = new RegionFile(file2);
      regionsByFilename.put(file2, regionFile2);
      return regionFile2;
   }

   public static synchronized void clearRegionFileReferences() {
      for (RegionFile regionFile : regionsByFilename.values()) {
         try {
            if (regionFile != null) {
               regionFile.close();
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      regionsByFilename.clear();
   }

   public static DataInputStream getChunkInputStream(File worldDir, int chunkX, int chunkZ) {
      RegionFile createOrLoadRegionFile2 = createOrLoadRegionFile(worldDir, chunkX, chunkZ);
      return createOrLoadRegionFile2.getChunkDataInputStream(chunkX & 31, chunkZ & 31);
   }

   public static DataOutputStream getChunkOutputStream(File worldDir, int chunkX, int chunkZ) {
      RegionFile createOrLoadRegionFile2 = createOrLoadRegionFile(worldDir, chunkX, chunkZ);
      return createOrLoadRegionFile2.getChunkDataOutputStream(chunkX & 31, chunkZ & 31);
   }
}
