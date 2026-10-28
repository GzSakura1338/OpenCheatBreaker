package net.minecraft.client.resources.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.IChatComponent.Serializer;

public class IMetadataSerializer {
    public IRegistry metadataSectionSerializerRegistry = new RegistrySimple();
    public GsonBuilder gsonBuilder = new GsonBuilder();
    public Gson gson;

    public IMetadataSerializer() {
        this.gsonBuilder.registerTypeHierarchyAdapter(IChatComponent.class, new Serializer());
        this.gsonBuilder.registerTypeHierarchyAdapter(ChatStyle.class, new net.minecraft.util.ChatStyle.Serializer());
        this.gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
    }

    public void registerMetadataSectionType(IMetadataSectionSerializer metadataSectionSerializer, Class clazz) {
        this.metadataSectionSerializerRegistry.putObject(metadataSectionSerializer.getSectionName(), new IMetadataSerializer.Registration(metadataSectionSerializer, clazz, null));
        this.gsonBuilder.registerTypeAdapter(clazz, metadataSectionSerializer);
        this.gson = null;
    }

    public IMetadataSection parseMetadataSection(String str, JsonObject jsonObject) {
        if (str == null) {
            throw new IllegalArgumentException("Metadata section name cannot be null");
        } else if (!jsonObject.has(str)) {
            return null;
        } else if (!jsonObject.get(str).isJsonObject()) {
            throw new IllegalArgumentException("Invalid metadata for '" + str + "' - expected object, found " + jsonObject.get(str));
        } else {
            IMetadataSerializer.Registration object = (IMetadataSerializer.Registration)this.metadataSectionSerializerRegistry.getObject(str);
            if (object == null) {
                throw new IllegalArgumentException("Don't know how to handle metadata section '" + str + "'");
            } else {
                return (IMetadataSection)this.getGson().fromJson(jsonObject.getAsJsonObject(str), object.classValue);
            }
        }
    }

    public Gson getGson() {
        if (this.gson == null) {
            this.gson = this.gsonBuilder.create();
        }

        return this.gson;
    }

    public class Registration {
        public IMetadataSectionSerializer iMetadataSectionSerializer;
        public Class classValue;

        public Registration(IMetadataSectionSerializer metadataSectionSerializer, Class clazz) {
            this.iMetadataSectionSerializer = metadataSectionSerializer;
            this.classValue = clazz;
        }

        public Registration(IMetadataSectionSerializer metadataSectionSerializer, Class clazz, Object obj) {
            this(metadataSectionSerializer, clazz);
        }
    }
}
