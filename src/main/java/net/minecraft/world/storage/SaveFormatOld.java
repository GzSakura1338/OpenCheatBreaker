package net.minecraft.world.storage;

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

    public SaveFormatOld(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }

        this.savesDirectory = file;
    }

    @Override
    public String constantText() {
        return "Old Format";
    }

    @Override
    public List getSaveList() throws AnvilConverterException {
        ArrayList list = new ArrayList();

        for (int i = 0; i < 5; i++) {
            String str = "World" + (i + 1);
            WorldInfo worldInfo = this.getWorldInfo(str);
            if (worldInfo != null) {
                list.add(
                    new SaveFormatComparator(
                        str, "", worldInfo.getLastTimePlayed(), worldInfo.getSizeOnDisk(), worldInfo.getGameType(), false, worldInfo.isHardcoreModeEnabled(), worldInfo.areCommandsAllowed()
                    )
                );
            }
        }

        return list;
    }

    @Override
    public void flushCache() {
    }

    @Override
    public WorldInfo getWorldInfo(String str) {
        File file = new File(this.savesDirectory, str);
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
    public void renameWorld(String str, String str2) {
        File file = new File(this.savesDirectory, str);
        if (file.exists()) {
            File file2 = new File(file, "level.dat");
            if (file2.exists()) {
                try {
                    NBTTagCompound readCompressed = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                    NBTTagCompound compoundTag = readCompressed.getCompoundTag("Data");
                    compoundTag.setString("LevelName", str2);
                    CompressedStreamTools.writeCompressed(readCompressed, new FileOutputStream(file2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean checkString2(String str) {
        File file = new File(this.savesDirectory, str);
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
    public boolean deleteWorldDirectory(String str) {
        File file = new File(this.savesDirectory, str);
        if (!file.exists()) {
            return true;
        }

        logger.info("Deleting level " + str);

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
    public ISaveHandler getSaveLoader(String str, boolean flag) {
        return new SaveHandler(this.savesDirectory, str, flag);
    }

    @Override
    public boolean checkString(String str) {
        return false;
    }

    @Override
    public boolean isOldMapFormat(String str) {
        return false;
    }

    @Override
    public boolean convertMapFormat(String str, IProgressUpdate progressUpdate) throws java.io.IOException {
        return false;
    }

    @Override
    public boolean canLoadWorld(String str) {
        File file = new File(this.savesDirectory, str);
        return file.isDirectory();
    }
}
