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
    public AnvilSaveHandler(File file, String str, boolean flag) {
        super(file, str, flag);
    }

    @Override
    public IChunkLoader getChunkLoader(WorldProvider worldProvider) {
        File worldDirectory = this.getWorldDirectory();
        if (worldProvider instanceof WorldProviderHell) {
            File file = new File(worldDirectory, "DIM-1");
            file.mkdirs();
            return new AnvilChunkLoader(file);
        } else if (worldProvider instanceof WorldProviderEnd) {
            File file = new File(worldDirectory, "DIM1");
            file.mkdirs();
            return new AnvilChunkLoader(file);
        } else {
            return new AnvilChunkLoader(worldDirectory);
        }
    }

    @Override
    public void saveWorldInfoWithPlayer(WorldInfo worldInfo, NBTTagCompound nbttagCompound) {
        worldInfo.setSaveVersion(19133);
        super.saveWorldInfoWithPlayer(worldInfo, nbttagCompound);
    }

    @Override
    public void flush() {
        try {
            ThreadedFileIOBase.threadedIOInstance.waitForFinish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RegionFileCache.clearRegionFileReferences();
    }
}
