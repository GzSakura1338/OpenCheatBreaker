package net.minecraft.client.resources;

import com.cheatbreaker.client.ui.screen.ResourcePackScanner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.HttpUtil.DownloadListener;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository {
    public static Logger logger = LogManager.getLogger();
    public List repositoryEntriesAll = Lists.newArrayList();
    public List repositoryEntries = Lists.newArrayList();
    public File dirResourcepacks;
    public File serverResourcePackDir;
    public IResourcePack serverContainer;
    public boolean flag;
    public IResourcePack rprDefaultResourcePack;
    public IMetadataSerializer rprMetadataSerializer;
    public static FileFilter resourcePackFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            boolean flag = file.isFile() && file.getName().endsWith(".zip");
            boolean flag2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
            return flag || flag2;
        }
    };
    public long longValue;
    public String text;

    public ResourcePackRepository(File file, File file2, IResourcePack resourcePack, IMetadataSerializer metadataSerializer, GameSettings gameSettings) {
        this.dirResourcepacks = file;
        this.serverResourcePackDir = file2;
        this.rprDefaultResourcePack = resourcePack;
        this.rprMetadataSerializer = metadataSerializer;
        this.fixDirResourcepacks();
        this.updateRepositoryEntriesAll();

        for (String str : gameSettings.resourcePacks) {
            for (ResourcePackRepository.Entry entry : (Iterable<ResourcePackRepository.Entry>)this.repositoryEntriesAll) {
                if (entry.getResourcePackName().equals(str)) {
                    this.repositoryEntries.add(entry);
                    break;
                }
            }
        }
    }

    public void fixDirResourcepacks() {
        if (!this.dirResourcepacks.isDirectory()) {
            this.dirResourcepacks.delete();
            this.dirResourcepacks.mkdirs();
        }
    }

    public List getResourcePackFiles() {
        return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
    }

    public void updateRepositoryEntriesAll() {
        ArrayList newArrayList = Lists.newArrayList();

        for (File file : (Iterable<File>)this.getResourcePackFiles()) {
            ResourcePackRepository.Entry entry = new ResourcePackRepository.Entry(file, null);
            if (!this.repositoryEntriesAll.contains(entry)) {
                try {
                    if (ResourcePackScanner.isValidZipPack(entry.resourcePackFile)) {
                        entry.updateResourcePack();
                        newArrayList.add(entry);
                    }
                } catch (Exception e) {
                    newArrayList.remove(entry);
                }
            } else {
                int indexOf = this.repositoryEntriesAll.indexOf(entry);
                if (indexOf > -1 && indexOf < this.repositoryEntriesAll.size()) {
                    newArrayList.add(this.repositoryEntriesAll.get(indexOf));
                }
            }
        }

        this.repositoryEntriesAll.removeAll(newArrayList);

        for (ResourcePackRepository.Entry entry : (Iterable<ResourcePackRepository.Entry>)this.repositoryEntriesAll) {
            entry.closeResourcePack();
        }

        this.repositoryEntriesAll = newArrayList;
    }

    public List<ResourcePackRepository.Entry> getRepositoryEntriesAll() {
        return ImmutableList.copyOf(this.repositoryEntriesAll);
    }

    public List<ResourcePackRepository.Entry> getRepositoryEntries() {
        return ImmutableList.copyOf(this.repositoryEntries);
    }

    public void setSelectedResourcePacks(List list) {
        this.repositoryEntries.clear();
        this.repositoryEntries.addAll(list);
    }

    public File getDirResourcepacks() {
        return this.dirResourcepacks;
    }

    public void obtainResourcePack(String str) throws IOException {
        if (!str.equals(this.text)) {
            String substring = str.substring(str.lastIndexOf("/") + 1);
            if (substring.contains("?")) {
                substring = substring.substring(0, substring.indexOf("?"));
            }

            if (substring.endsWith(".zip")) {
                File file = new File(this.serverResourcePackDir, substring.replaceAll("\\W", ""));
                this.clear();
                this.text = str;
                this.handleString(str, file);
            }
        }
    }

    public void handleString(String str, File file) throws IOException {
        final long longValue = this.longValue;
        HashMap newHashMap = Maps.newHashMap();
        GuiScreenWorking guiScreenWorking = new GuiScreenWorking();
        newHashMap.put("X-Minecraft-Username", Minecraft.getMinecraft().getSession().getUsername());
        newHashMap.put("X-Minecraft-UUID", Minecraft.getMinecraft().getSession().getPlayerID());
        newHashMap.put("X-Minecraft-Version", "1.7.10");
        this.flag = true;
        Minecraft.getMinecraft().displayGuiScreen(guiScreenWorking);
        HttpUtil.downloadResourcePack(file, str, new DownloadListener() {
            @Override
            public void onDownloadComplete(File file2) {
                synchronized (ResourcePackRepository.this) {
                    if (ResourcePackRepository.this.flag && longValue == ResourcePackRepository.this.longValue) {
                        ResourcePackRepository.this.flag = false;

                        try (ZipFile zipFile = new ZipFile(file2)) {
                            if (zipFile.getEntry("pack.mcmeta") == null) {
                                throw new IOException("Resource pack is missing pack.mcmeta: " + file2);
                            }
                        } catch (IOException e) {
                            ResourcePackRepository.logger.warn("Couldn't apply server resource pack " + file2, e);
                            return;
                        }

                        ResourcePackRepository.this.serverContainer = new FileResourcePack(file2);
                        Minecraft.getMinecraft().scheduleResourcesRefresh();
                    }
                }
            }

            @Override
            public void handleThrowable(Throwable throwable) {
                synchronized (ResourcePackRepository.this) {
                    if (longValue == ResourcePackRepository.this.longValue) {
                        ResourcePackRepository.this.flag = false;
                    }
                }
            }
        }, newHashMap, 52428800, guiScreenWorking, Minecraft.getMinecraft().getProxy());
    }

    public IResourcePack getResourcePackInstance() {
        return this.serverContainer;
    }

    public void clear() {
        this.serverContainer = null;
        this.flag = false;
        this.text = null;
        this.longValue++;
    }

    public class Entry {
        public File resourcePackFile;
        public IResourcePack reResourcePack;
        public PackMetadataSection rePackMetadataSection;
        public BufferedImage texturePackIcon;
        public ResourceLocation locationTexturePackIcon;

        public Entry(File file) {
            this.resourcePackFile = file;
        }

        public void updateResourcePack() throws IOException {
            this.reResourcePack = this.resourcePackFile.isDirectory()
                ? new FolderResourcePack(this.resourcePackFile)
                : new FileResourcePack(this.resourcePackFile);

            try {
                this.rePackMetadataSection = (PackMetadataSection)this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.texturePackIcon = this.reResourcePack.getPackImage();
            if (this.texturePackIcon == null) {
                this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
            }

            this.closeResourcePack();
        }

        public void bindTexturePackIcon(TextureManager textureManager) {
            if (this.locationTexturePackIcon == null) {
                this.locationTexturePackIcon = textureManager.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
            }

            textureManager.bindTexture(this.locationTexturePackIcon);
        }

        public void closeResourcePack() {
            if (this.reResourcePack instanceof Closeable) {
                IOUtils.closeQuietly((Closeable)this.reResourcePack);
            }
        }

        public IResourcePack getResourcePack() {
            return this.reResourcePack;
        }

        public String getResourcePackName() {
            return this.reResourcePack.getPackName();
        }

        public String getTexturePackDescription() {
            return this.rePackMetadataSection == null
                ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing 'pack' section)"
                : this.rePackMetadataSection.getDescription().getFormattedText();
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof ResourcePackRepository.Entry && this.toString().equals(obj.toString());
        }

        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }

        @Override
        public String toString() {
            return String.format(
                "%s:%s:%d", this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", this.resourcePackFile.lastModified()
            );
        }

        public Entry(File file, Object obj) {
            this(file);
        }
    }
}
