package net.minecraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveHandler implements IPlayerFileData, ISaveHandler {
    public static Logger logger = LogManager.getLogger();
    public long initializationTime = MinecraftServer.getCurrentTimeMillis();
    public File worldDirectory;
    public File playersDirectory;
    public File mapDataDir;
    public String saveDirectoryName;

    public SaveHandler(File file, String str, boolean flag) {
        this.worldDirectory = new File(file, str);
        this.worldDirectory.mkdirs();
        this.playersDirectory = new File(this.worldDirectory, "playerdata");
        this.mapDataDir = new File(this.worldDirectory, "data");
        this.mapDataDir.mkdirs();
        this.saveDirectoryName = str;
        if (flag) {
            this.playersDirectory.mkdirs();
        }

        this.setSessionLock();
    }

    public void setSessionLock() {
        try {
            File file = new File(this.worldDirectory, "session.lock");
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

            try {
                dataOutputStream.writeLong(this.initializationTime);
            } finally {
                dataOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }

    @Override
    public File getWorldDirectory() {
        return this.worldDirectory;
    }

    @Override
    public void checkSessionLock() throws MinecraftException {
        try {
            File file = new File(this.worldDirectory, "session.lock");
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

            try {
                if (dataInputStream.readLong() != this.initializationTime) {
                    throw new MinecraftException("The save is being accessed from another location, aborting");
                }
            } finally {
                dataInputStream.close();
            }
        } catch (IOException e) {
            throw new MinecraftException("Failed to check session lock, aborting");
        }
    }

    @Override
    public IChunkLoader getChunkLoader(WorldProvider worldProvider) {
        throw new RuntimeException("Old Chunk Storage is no longer supported.");
    }

    @Override
    public WorldInfo loadWorldInfo() {
        File file = new File(this.worldDirectory, "level.dat");
        if (file.exists()) {
            try {
                NBTTagCompound readCompressed = CompressedStreamTools.readCompressed(new FileInputStream(file));
                NBTTagCompound compoundTag = readCompressed.getCompoundTag("Data");
                return new WorldInfo(compoundTag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        file = new File(this.worldDirectory, "level.dat_old");
        if (file.exists()) {
            try {
                NBTTagCompound readCompressed = CompressedStreamTools.readCompressed(new FileInputStream(file));
                NBTTagCompound compoundTag = readCompressed.getCompoundTag("Data");
                return new WorldInfo(compoundTag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void saveWorldInfoWithPlayer(WorldInfo worldInfo, NBTTagCompound nbttagCompound) {
        NBTTagCompound cloneNBTCompound = worldInfo.cloneNBTCompound(nbttagCompound);
        NBTTagCompound nbttagCompound2 = new NBTTagCompound();
        nbttagCompound2.setTag("Data", cloneNBTCompound);

        try {
            File file = new File(this.worldDirectory, "level.dat_new");
            File file2 = new File(this.worldDirectory, "level.dat_old");
            File file3 = new File(this.worldDirectory, "level.dat");
            CompressedStreamTools.writeCompressed(nbttagCompound2, new FileOutputStream(file));
            if (file2.exists()) {
                file2.delete();
            }

            file3.renameTo(file2);
            if (file3.exists()) {
                file3.delete();
            }

            file.renameTo(file3);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveWorldInfo(WorldInfo worldInfo) {
        NBTTagCompound nBTTagCompound = worldInfo.getNBTTagCompound();
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        nbttagCompound.setTag("Data", nBTTagCompound);

        try {
            File file = new File(this.worldDirectory, "level.dat_new");
            File file2 = new File(this.worldDirectory, "level.dat_old");
            File file3 = new File(this.worldDirectory, "level.dat");
            CompressedStreamTools.writeCompressed(nbttagCompound, new FileOutputStream(file));
            if (file2.exists()) {
                file2.delete();
            }

            file3.renameTo(file2);
            if (file3.exists()) {
                file3.delete();
            }

            file.renameTo(file3);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writePlayerData(EntityPlayer entityPlayer) {
        try {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            entityPlayer.writeToNBT(nbttagCompound);
            File file = new File(this.playersDirectory, entityPlayer.getUniqueID().toString() + ".dat.tmp");
            File file2 = new File(this.playersDirectory, entityPlayer.getUniqueID().toString() + ".dat");
            CompressedStreamTools.writeCompressed(nbttagCompound, new FileOutputStream(file));
            if (file2.exists()) {
                file2.delete();
            }

            file.renameTo(file2);
        } catch (Exception e) {
            logger.warn("Failed to save player data for " + entityPlayer.getCommandSenderName());
        }
    }

    @Override
    public NBTTagCompound readPlayerData(EntityPlayer entityPlayer) {
        NBTTagCompound nbttagCompound = null;

        try {
            File file = new File(this.playersDirectory, entityPlayer.getUniqueID().toString() + ".dat");
            if (file.exists() && file.isFile()) {
                nbttagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file));
            }
        } catch (Exception e) {
            logger.warn("Failed to load player data for " + entityPlayer.getCommandSenderName());
        }

        if (nbttagCompound != null) {
            entityPlayer.readFromNBT(nbttagCompound);
        }

        return nbttagCompound;
    }

    @Override
    public IPlayerFileData getPlayerNBTManager() {
        return this;
    }

    @Override
    public String[] getAvailablePlayerDat() {
        String[] list = this.playersDirectory.list();

        for (int i = 0; i < list.length; i++) {
            if (list[i].endsWith(".dat")) {
                list[i] = list[i].substring(0, list[i].length() - 4);
            }
        }

        return list;
    }

    @Override
    public void flush() {
    }

    @Override
    public File getMapFileFromName(String str) {
        return new File(this.mapDataDir, str + ".dat");
    }

    @Override
    public String getWorldDirectoryName() {
        return this.saveDirectoryName;
    }
}
