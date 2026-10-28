package net.minecraft.world.chunk.storage;

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
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.storage.ChunkLoader.AnvilConverterData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilSaveConverter extends SaveFormatOld {
    public static Logger logger = LogManager.getLogger();

    public AnvilSaveConverter(File file) {
        super(file);
    }

    @Override
    public String constantText() {
        return "Anvil";
    }

    @Override
    public List getSaveList() throws AnvilConverterException {
        if (this.savesDirectory != null && this.savesDirectory.exists() && this.savesDirectory.isDirectory()) {
            ArrayList list = new ArrayList();
            File[] listFiles = this.savesDirectory.listFiles();
            File[] files = listFiles;
            int length = listFiles.length;

            for (int i = 0; i < length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    String name = file.getName();
                    WorldInfo worldInfo = this.getWorldInfo(name);
                    if (worldInfo != null && (worldInfo.getSaveVersion() == 19132 || worldInfo.getSaveVersion() == 19133)) {
                        boolean flag = worldInfo.getSaveVersion() != this.getSaveVersion();
                        String worldName = worldInfo.getWorldName();
                        if (worldName == null || MathHelper.stringNullOrLengthZero(worldName)) {
                            worldName = name;
                        }

                        long l = 0L;
                        list.add(
                            new SaveFormatComparator(
                                name, worldName, worldInfo.getLastTimePlayed(), l, worldInfo.getGameType(), flag, worldInfo.isHardcoreModeEnabled(), worldInfo.areCommandsAllowed()
                            )
                        );
                    }
                }
            }

            return list;
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
    public ISaveHandler getSaveLoader(String str, boolean flag) {
        return new AnvilSaveHandler(this.savesDirectory, str, flag);
    }

    @Override
    public boolean checkString(String str) {
        WorldInfo worldInfo = this.getWorldInfo(str);
        return worldInfo != null && worldInfo.getSaveVersion() == 19132;
    }

    @Override
    public boolean isOldMapFormat(String str) {
        WorldInfo worldInfo = this.getWorldInfo(str);
        return worldInfo != null && worldInfo.getSaveVersion() != this.getSaveVersion();
    }

    @Override
    public boolean convertMapFormat(String str, IProgressUpdate progressUpdate) throws IOException {
        progressUpdate.setLoadingProgress(0);
        ArrayList list = new ArrayList();
        ArrayList list2 = new ArrayList();
        ArrayList list3 = new ArrayList();
        File file = new File(this.savesDirectory, str);
        File file2 = new File(file, "DIM-1");
        File file3 = new File(file, "DIM1");
        logger.info("Scanning folders...");
        this.addRegionFilesToCollection(file, list);
        if (file2.exists()) {
            this.addRegionFilesToCollection(file2, list2);
        }

        if (file3.exists()) {
            this.addRegionFilesToCollection(file3, list3);
        }

        int n = list.size() + list2.size() + list3.size();
        logger.info("Total conversion count is " + n);
        WorldInfo worldInfo = this.getWorldInfo(str);
        WorldChunkManager worldChunkManager = null;
        if (worldInfo.getTerrainType() == WorldType.FLAT) {
            worldChunkManager = new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F);
        } else {
            worldChunkManager = new WorldChunkManager(worldInfo.getSeed(), worldInfo.getTerrainType());
        }

        this.convertFile(new File(file, "region"), list, worldChunkManager, 0, n, progressUpdate);
        this.convertFile(new File(file2, "region"), list2, new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F), list.size(), n, progressUpdate);
        this.convertFile(new File(file3, "region"), list3, new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F), list.size() + list2.size(), n, progressUpdate);
        worldInfo.setSaveVersion(19133);
        if (worldInfo.getTerrainType() == WorldType.DEFAULT_1_1) {
            worldInfo.setTerrainType(WorldType.DEFAULT);
        }

        this.createFile(str);
        ISaveHandler saveLoader = this.getSaveLoader(str, false);
        saveLoader.saveWorldInfo(worldInfo);
        return true;
    }

    public void createFile(String str) {
        File file = new File(this.savesDirectory, str);
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

    public void convertFile(File file, Iterable iterable, WorldChunkManager worldChunkManager, int n, int n2, IProgressUpdate progressUpdate) throws IOException {
        for (File file2 : (Iterable<File>)iterable) {
            this.convertChunks(file, file2, worldChunkManager, n, n2, progressUpdate);
            n++;
            int round = (int)Math.round(100.0 * n / n2);
            progressUpdate.setLoadingProgress(round);
        }
    }

    public void convertChunks(File file, File file2, WorldChunkManager worldChunkManager, int n, int n2, IProgressUpdate progressUpdate) {
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
                            AnvilConverterData load = ChunkLoader.load(compoundTag);
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
                    progressUpdate.setLoadingProgress(round2);
                }
            }

            regionFile.close();
            regionFile2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRegionFilesToCollection(File file, Collection collection) {
        File file2 = new File(file, "region");
        File[] listFiles = file2.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file3, String str) {
                return str.endsWith(".mcr");
            }
        });
        if (listFiles != null) {
            Collections.addAll(collection, listFiles);
        }
    }
}
