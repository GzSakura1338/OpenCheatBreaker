package net.minecraft.world.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

public interface ISaveHandler {
   WorldInfo loadWorldInfo();

   void checkSessionLock() throws MinecraftException;

   IChunkLoader getChunkLoader(WorldProvider worldProvider);

   void saveWorldInfoWithPlayer(WorldInfo worldInfo, NBTTagCompound nbttagCompound);

   void saveWorldInfo(WorldInfo worldInfo);

   IPlayerFileData getPlayerNBTManager();

   void flush();

   File getWorldDirectory();

   File getMapFileFromName(String str);

   String getWorldDirectoryName();
}
