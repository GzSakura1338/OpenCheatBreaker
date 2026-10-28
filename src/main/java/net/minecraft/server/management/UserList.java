package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList {
    public static Logger logger = LogManager.getLogger();
    public Map values = Maps.newHashMap();
    public boolean lanServer = true;
    public File saveFile;
    public Gson gson;
    public static ParameterizedType saveFileFormat = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{UserListEntry.class};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };

    public UserList(File saveFile) {
        this.saveFile = saveFile;
        GsonBuilder setPrettyPrinting = new GsonBuilder().setPrettyPrinting();
        setPrettyPrinting.registerTypeHierarchyAdapter(UserListEntry.class, new UserList.Serializer(null));
        this.gson = setPrettyPrinting.create();
    }

    public boolean isLanServer() {
        return this.lanServer;
    }

    public void setLanServer(boolean state) {
        this.lanServer = state;
    }

    public void addEntry(UserListEntry entry) {
        this.values.put(this.getObjectKey(entry.getValue()), entry);

        try {
            this.writeChanges();
        } catch (IOException e) {
            logger.warn("Could not save the list after adding a user.", e);
        }
    }

    public UserListEntry getEntry(Object obj) {
        this.removeExpired();
        return (UserListEntry)this.values.get(this.getObjectKey(obj));
    }

    public void removeEntry(Object obj) {
        this.values.remove(this.getObjectKey(obj));

        try {
            this.writeChanges();
        } catch (IOException e) {
            logger.warn("Could not save the list after removing a user.", e);
        }
    }

    public String[] getKeys() {
        return (String[])this.values.keySet().toArray(new String[this.values.size()]);
    }

    public String getObjectKey(Object obj) {
        return obj.toString();
    }

    public boolean hasEntry(Object entry) {
        return this.values.containsKey(this.getObjectKey(entry));
    }

    public void removeExpired() {
        ArrayList newArrayList = Lists.newArrayList();

        for (UserListEntry userListEntry : (Iterable<UserListEntry>)this.values.values()) {
            if (userListEntry.hasBanExpired()) {
                newArrayList.add(userListEntry.getValue());
            }
        }

        for (Object obj : newArrayList) {
            this.values.remove(obj);
        }
    }

    public UserListEntry createEntry(JsonObject entryData) {
        return new UserListEntry(null, entryData);
    }

    public Map getValues() {
        return this.values;
    }

    public void writeChanges() throws IOException {
        Collection values = this.values.values();
        String toJson = this.gson.toJson(values);
        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = Files.newWriter(this.saveFile, Charsets.UTF_8);
            bufferedWriter.write(toJson);
        } finally {
            IOUtils.closeQuietly(bufferedWriter);
        }
    }

    public class Serializer implements JsonDeserializer, JsonSerializer {
        public Serializer() {
        }

        public JsonElement serialize(UserListEntry userListEntry, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            userListEntry.onSerialization(jsonObject);
            return jsonObject;
        }

        public UserListEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            if (jsonElement.isJsonObject()) {
                JsonObject asJsonObject = jsonElement.getAsJsonObject();
                return UserList.this.createEntry(asJsonObject);
            } else {
                return null;
            }
        }

        public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((UserListEntry)obj, type, jsonSerializationContext);
        }

        public Serializer(Object obj) {
            this();
        }
    }
}
