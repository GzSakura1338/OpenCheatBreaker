package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.util.Logging;
import com.cheatbreaker.client.util.SneakyThrow;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;

public class ResourcePackScanner {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static Optional<Entry> createEntry(File file) {
        Entry entry;
        try {
            Constructor declaredConstructor = Entry.class.getDeclaredConstructor(ResourcePackRepository.class, File.class);
            declaredConstructor.setAccessible(true);
            entry = (Entry)declaredConstructor.newInstance(mc.getResourcePackRepository(), file);
        } catch (NoSuchMethodException e) {
            CheatBreaker.getInstance().getLogger().error("Failed to find constructor", e);
            return Optional.empty();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            CheatBreaker.getInstance().getLogger().error("Failed to create entry", e);
            return Optional.empty();
        }

        try {
            entry.updateResourcePack();
        } catch (Exception e) {
            entry.closeResourcePack();
            CheatBreaker.getInstance().getLogger().error("Failed to load resource pack " + file.getName(), e);
            return Optional.empty();
        }

        return Optional.of(entry);
    }

    public static boolean checkFile(File file) {
        return file.isDirectory() && !new File(file, "pack.mcmeta").isFile();
    }

    public static List<Entry> getEnabledEntries() {
        ArrayList list = new ArrayList();
        ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();

        for (String str : mc.gameSettings.resourcePacks) {
            for (Entry entry : resourcePackRepository.getRepositoryEntriesAll()) {
                if (!entry.getResourcePackName().equals(str)) {
                }
            }

            for (File file : listFiles(resourcePackRepository.getDirResourcepacks(), ResourcePackScanner::checkFile)) {
                list.addAll(findEntries(file, str));
            }
        }

        return list;
    }

    public static List<Entry> findEntries(File file, String str) {
        ArrayList list = new ArrayList();

        for (File file2 : listFiles(file, null)) {
            if (isValidZipPack(file2)) {
                if (file2.getName().equals(str)) {
                    createEntry(file2).ifPresent(list::add);
                }
            } else if (checkFile(file2)) {
                list.addAll(findEntries(file2, str));
            }
        }

        return list;
    }

    public static boolean isValidZipPack(File file) {
        try {
            if (file.isFile() && file.getName().endsWith(".zip")) {
                try (ZipFile zipFile = new ZipFile(file)) {
                    ZipEntry entry = zipFile.getEntry("pack.mcmeta");
                    if (entry == null) {
                        Logging.debug("Not found in: " + file.getName());
                        return false;
                    } else {
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            } else {
                return file.isDirectory() && new File(file, "pack.mcmeta").isFile();
            }
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public static File[] listFiles(File file, FileFilter fileFilter) {
        File[] listFiles = file.listFiles(fileFilter);
        return listFiles == null ? new File[0] : listFiles;
    }
}
