package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public interface IResourcePack {
    InputStream getInputStream(ResourceLocation resourceLocation) throws IOException;

    boolean resourceExists(ResourceLocation resourceLocation);

    Set getResourceDomains();

    IMetadataSection getPackMetadata(IMetadataSerializer metadataSerializer, String str) throws IOException;

    BufferedImage getPackImage() throws IOException;

    String getPackName();
}
