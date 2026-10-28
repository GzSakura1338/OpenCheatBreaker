package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.util.ReflectionUtils;
import com.cheatbreaker.client.util.SneakyThrow;
import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class ResourcePackWrapper implements IResourcePack {
    public String packName;
    public CustomDefaultResourcePack customDefaultResourcePack;
    public static Set set = null;
    public FileResourcePack fileResourcePack;
    public FolderResourcePack folderResourcePack;

    @Override
    public Set getResourceDomains() {
        return set;
    }

    public ResourcePackWrapper(String str) {
        this.packName = str;
        set = ImmutableSet.of(str);
        File findObject = (File)ReflectionUtils.findObject(Minecraft.getMinecraft(), Minecraft.class, File.class, 2);
        this.customDefaultResourcePack = new CustomDefaultResourcePack(new ResourceIndex(findObject, str).getIndex(), str);
        this.fileResourcePack = new FileResourcePack(Minecraft.getMinecraft().mcDataDir);
        this.folderResourcePack = new FolderResourcePack(Minecraft.getMinecraft().mcDataDir);
    }

    @Override
    public InputStream getInputStream(ResourceLocation resourceLocation) {
        try {
            return this.customDefaultResourcePack.getInputStream(resourceLocation);
        } catch (IOException e) {
            try {
                return this.fileResourcePack.getInputStream(resourceLocation);
            } catch (IOException e2) {
                try {
                    return this.folderResourcePack.getInputStream(resourceLocation);
                } catch (IOException e3) {
                    throw SneakyThrow.sneakyThrow(e3);
                }
            }
        }
    }

    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer metadataSerializer, String str) {
        return null;
    }

    @Override
    public BufferedImage getPackImage() {
        return null;
    }

    @Override
    public String getPackName() {
        return this.packName + "ResourcePack";
    }

    @Override
    public boolean resourceExists(ResourceLocation resourceLocation) {
        return this.customDefaultResourcePack.resourceExists(resourceLocation) || this.fileResourcePack.resourceExists(resourceLocation) || this.folderResourcePack.resourceExists(resourceLocation);
    }
}
