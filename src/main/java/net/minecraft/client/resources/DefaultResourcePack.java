package net.minecraft.client.resources;

import com.cheatbreaker.client.util.SneakyThrow;
import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.optifine.ReflectorForge;

public class DefaultResourcePack implements IResourcePack {
    public static Set defaultResourceDomains = ImmutableSet.of("minecraft", "realms", "emoticons");
    public Map assetsIndex;

    public DefaultResourcePack(Map map) {
        this.assetsIndex = map;
    }

    @Override
    public InputStream getInputStream(ResourceLocation resourceLocation) throws IOException {
        InputStream resourceStream = this.getResourceStream(resourceLocation);
        if (resourceStream != null) {
            return resourceStream;
        } else {
            InputStream openFile = this.openFile(resourceLocation);
            if (openFile != null) {
                return openFile;
            } else {
                throw new FileNotFoundException(resourceLocation.getResourcePath());
            }
        }
    }

    public InputStream openFile(ResourceLocation resourceLocation) throws FileNotFoundException {
        File file = (File)this.assetsIndex.get(resourceLocation.toString());
        return file != null && file.isFile() ? new FileInputStream(file) : null;
    }

    public InputStream getResourceStream(ResourceLocation resourceLocation) {
        String resourceDomain = resourceLocation.getResourceDomain();
        String resourcePath = resourceLocation.getResourcePath();
        if ("emoticons".equals(resourceDomain)) {
            resourceDomain = "minecraft";
            resourcePath = "emoticons/" + resourcePath;
        }

        String str = "/assets/" + resourceDomain + "/" + resourcePath;
        InputStream optiFineResourceStream = ReflectorForge.getOptiFineResourceStream(str);
        return optiFineResourceStream != null ? optiFineResourceStream : DefaultResourcePack.class.getResourceAsStream(str);
    }

    @Override
    public boolean resourceExists(ResourceLocation resourceLocation) {
        return this.getResourceStream(resourceLocation) != null || this.assetsIndex.containsKey(resourceLocation.toString());
    }

    @Override
    public Set getResourceDomains() {
        return defaultResourceDomains;
    }

    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer metadataSerializer, String str) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream((File)this.assetsIndex.get("pack.mcmeta"));
            return AbstractResourcePack.readMetadata(metadataSerializer, fileInputStream, str);
        } catch (RuntimeException | FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        try {
            return ImageIO.read(DefaultResourcePack.class.getResourceAsStream("/" + new ResourceLocation("pack.png").getResourcePath()));
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    @Override
    public String getPackName() {
        return "Default";
    }
}
