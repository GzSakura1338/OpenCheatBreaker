package net.minecraft.world.chunk.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler {
   public AnvilSaveHandler(File savesDirectory, String directoryName, boolean storePlayerdata) {
      super(savesDirectory, directoryName, storePlayerdata);
   }

   @Override
   public IChunkLoader getChunkLoader(WorldProvider provider) {
      File worldDirectory = this.getWorldDirectory();
      if (provider instanceof WorldProviderHell) {
         File file = new File(worldDirectory, "DIM-1");
         file.mkdirs();
         return new AnvilChunkLoader(file);
      } else if (provider instanceof WorldProviderEnd) {
         File file = new File(worldDirectory, "DIM1");
         file.mkdirs();
         return new AnvilChunkLoader(file);
      } else {
         return new AnvilChunkLoader(worldDirectory);
      }
   }

   @Override
   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
      worldInformation.setSaveVersion(19133);
      super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
   }

   @Override
   public void flush() {
      try {
         ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      RegionFileCache.clearRegionFileReferences();
   }
}
