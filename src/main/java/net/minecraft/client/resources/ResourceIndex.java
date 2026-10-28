package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceIndex {
    public static Logger LOGGER = LogManager.getLogger();
    public Map index = Maps.newHashMap();

    public ResourceIndex(File file, String str) {
        if (str != null) {
            File file2 = new File(file, "objects");
            File file3 = new File(file, "indexes/" + str + ".json");
            BufferedReader bufferedReader = null;

            try {
                bufferedReader = Files.newReader(file3, Charsets.UTF_8);
                JsonObject asJsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
                JsonObject jsonObjectFieldOrDefault = JsonUtils.getJsonObjectFieldOrDefault(asJsonObject, "objects", null);
                if (jsonObjectFieldOrDefault != null) {
                    for (Entry entry : jsonObjectFieldOrDefault.entrySet()) {
                        JsonObject value = (JsonObject)entry.getValue();
                        String key = (String)entry.getKey();
                        String[] split = key.split("/", 2);
                        String str2 = split.length == 1 ? split[0] : split[0] + ":" + split[1];
                        String jsonObjectStringFieldValue = JsonUtils.getJsonObjectStringFieldValue(value, "hash");
                        File file4 = new File(file2, jsonObjectStringFieldValue.substring(0, 2) + "/" + jsonObjectStringFieldValue);
                        this.index.put(str2, file4);
                    }
                }
            } catch (JsonParseException e) {
                LOGGER.error("Unable to parse resource index file: " + file3);
            } catch (FileNotFoundException e) {
                LOGGER.error("Can't find the resource index file: " + file3);
            } finally {
                IOUtils.closeQuietly(bufferedReader);
            }
        }
    }

    public Map getIndex() {
        return this.index;
    }
}
