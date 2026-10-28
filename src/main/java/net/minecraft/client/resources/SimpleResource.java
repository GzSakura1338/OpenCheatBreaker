package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class SimpleResource implements IResource {
    public Map mapMetadataSections = Maps.newHashMap();
    public ResourceLocation srResourceLocation;
    public InputStream resourceInputStream;
    public InputStream mcmetaInputStream;
    public IMetadataSerializer srMetadataSerializer;
    public boolean mcmetaJsonChecked;
    public JsonObject mcmetaJson;

    public SimpleResource(ResourceLocation resourceLocation, InputStream inputStream, InputStream inputStream2, IMetadataSerializer metadataSerializer) {
        this.srResourceLocation = resourceLocation;
        this.resourceInputStream = inputStream;
        this.mcmetaInputStream = inputStream2;
        this.srMetadataSerializer = metadataSerializer;
    }

    @Override
    public InputStream getInputStream() {
        return this.resourceInputStream;
    }

    @Override
    public boolean hasMetadata() {
        return this.mcmetaInputStream != null;
    }

    @Override
    public IMetadataSection getMetadata(String str) {
        if (!this.hasMetadata()) {
            return null;
        }

        if (this.mcmetaJson == null && !this.mcmetaJsonChecked) {
            this.mcmetaJsonChecked = true;
            BufferedReader bufferedReader = null;

            try {
                bufferedReader = new BufferedReader(new InputStreamReader(this.mcmetaInputStream));
                this.mcmetaJson = new JsonParser().parse(bufferedReader).getAsJsonObject();
            } finally {
                IOUtils.closeQuietly(bufferedReader);
            }
        }

        IMetadataSection metadataSection = (IMetadataSection)this.mapMetadataSections.get(str);
        if (metadataSection == null) {
            metadataSection = this.srMetadataSerializer.parseMetadataSection(str, this.mcmetaJson);
        }

        return metadataSection;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof SimpleResource) {
            SimpleResource simpleResource = (SimpleResource)obj;
            return this.srResourceLocation != null ? this.srResourceLocation.equals(simpleResource.srResourceLocation) : simpleResource.srResourceLocation == null;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.srResourceLocation == null ? 0 : this.srResourceLocation.hashCode();
    }
}
