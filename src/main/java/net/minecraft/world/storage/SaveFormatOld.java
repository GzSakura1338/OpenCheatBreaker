package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat {
   public static Logger logger = LogManager.getLogger();
   public File savesDirectory;

   public SaveFormatOld(File savesDirectoryIn) {
      if (!savesDirectoryIn.exists()) {
         savesDirectoryIn.mkdirs();
      }

      this.savesDirectory = savesDirectoryIn;
   }

   @Override
   public String getName() {
      return "Old Format";
   }

   @Override
   public List<SaveFormatComparator> getSaveList() throws AnvilConverterException {
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < 5; i++) {
         String str = "World" + (i + 1);
         WorldInfo worldInfo = this.getWorldInfo(str);
         if (worldInfo != null) {
            newArrayList.add(
               new SaveFormatComparator(
                  str, "", worldInfo.getLastTimePlayed(), worldInfo.getSizeOnDisk(), worldInfo.getGameType(), false, worldInfo.isHardcoreModeEnabled(), worldInfo.areCommandsAllowed()
               )
            );
         }
      }

      return newArrayList;
   }

   @Override
   public void flushCache() {
   }

   @Override
   public WorldInfo getWorldInfo(String saveName) {
      File file = new File(this.savesDirectory, saveName);
      if (!file.exists()) {
         return null;
      }

      File file2 = new File(file, "level.dat");
      if (file2.exists()) {
         try {
            NBTTagCompound readCompressed = CompressedStreamTools.readCompressed(new FileInputStream(file2));
            NBTTagCompound compoundTag = readCompressed.getCompoundTag("Data");
            return new WorldInfo(compoundTag);
         } catch (Exception e) {
            logger.error("Exception reading " + file2, e);
         }
      }

      file2 = new File(file, "level.dat_old");
      if (file2.exists()) {
         try {
            NBTTagCompound readCompressed = CompressedStreamTools.readCompressed(new FileInputStream(file2));
            NBTTagCompound compoundTag = readCompressed.getCompoundTag("Data");
            return new WorldInfo(compoundTag);
         } catch (Exception e) {
            logger.error("Exception reading " + file2, e);
         }
      }

      return null;
   }

   @Override
   public void renameWorld(String dirName, String newName) {
      File file = new File(this.savesDirectory, dirName);
      if (file.exists()) {
         File file2 = new File(file, "level.dat");
         if (file2.exists()) {
            try {
               NBTTagCompound readCompressed = CompressedStreamTools.readCompressed(new FileInputStream(file2));
               NBTTagCompound compoundTag = readCompressed.getCompoundTag("Data");
               compoundTag.setString("LevelName", newName);
               CompressedStreamTools.writeCompressed(readCompressed, new FileOutputStream(file2));
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }

   @Override
   public boolean isNewLevelIdAcceptable(String saveName) {
      File file = new File(this.savesDirectory, saveName);
      if (file.exists()) {
         return false;
      }

      try {
         file.mkdir();
         file.delete();
         return true;
      } catch (Throwable e) {
         logger.warn("Couldn't make new level", e);
         return false;
      }
   }

   @Override
   public boolean deleteWorldDirectory(String saveName) {
      File file = new File(this.savesDirectory, saveName);
      if (!file.exists()) {
         return true;
      }

      logger.info("Deleting level " + saveName);

      for (int i = 1; i <= 5; i++) {
         logger.info("Attempt " + i + "...");
         if (deleteFiles(file.listFiles())) {
            break;
         }

         logger.warn("Unsuccessful in deleting contents.");
         if (i < 5) {
            try {
               Thread.sleep(500L);
            } catch (InterruptedException e) {
            }
         }
      }

      return file.delete();
   }

   public static boolean deleteFiles(File[] files) {
      for (int i = 0; i < files.length; i++) {
         File file = files[i];
         logger.debug("Deleting " + file);
         if (file.isDirectory() && !deleteFiles(file.listFiles())) {
            logger.warn("Couldn't delete directory " + file);
            return false;
         }

         if (!file.delete()) {
            logger.warn("Couldn't delete file " + file);
            return false;
         }
      }

      return true;
   }

   @Override
   public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata) {
      return new SaveHandler(this.savesDirectory, saveName, storePlayerdata);
   }

   @Override
   public boolean isConvertible(String saveName) {
      return false;
   }

   @Override
   public boolean isOldMapFormat(String saveName) {
      return false;
   }

   @Override
   public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
      return false;
   }

   @Override
   public boolean canLoadWorld(String saveName) {
      File file = new File(this.savesDirectory, saveName);
      return file.isDirectory();
   }
}
