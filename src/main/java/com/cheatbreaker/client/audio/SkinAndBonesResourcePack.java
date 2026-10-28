package com.cheatbreaker.client.audio;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class SkinAndBonesResourcePack implements IResourcePack {
    public File root;
    public InputStream currentStream;
    public static Set<String> domains = ImmutableSet.of("s&b");

    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer metadataSerializer, String str) {
        return null;
    }

    @Override
    public Set<String> getResourceDomains() {
        return domains;
    }

    @Override
    public boolean resourceExists(ResourceLocation resourceLocation) {
        if (new File(this.root, resourceLocation.getResourcePath()).exists()) {
            return true;
        }

        this.currentStream = SkinAndBonesResourcePack.class.getResourceAsStream("/assets/skin_n_bones/models/" + resourceLocation.getResourcePath());
        return this.currentStream != null;
    }

    @Override
    public BufferedImage getPackImage() {
        return null;
    }

    public SkinAndBonesResourcePack(File file) {
        this.root = file;
        this.root.mkdirs();
    }

    @Override
    public InputStream getInputStream(ResourceLocation resourceLocation) throws java.io.FileNotFoundException {
        if (this.currentStream != null) {
            InputStream currentStream = this.currentStream;
            this.currentStream = null;
            return currentStream;
        } else {
            return new FileInputStream(new File(this.root, resourceLocation.getResourcePath()));
        }
    }

    @Override
    public String getPackName() {
        return "Skin&Bones animation pack";
    }
}
