package net.optifine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Json {
    public static float getFloat(JsonObject jsonObject, String str, float f) {
        JsonElement jsonElement = jsonObject.get(str);
        return jsonElement == null ? f : jsonElement.getAsFloat();
    }

    public static boolean getBoolean(JsonObject jsonObject, String str, boolean flag) {
        JsonElement jsonElement = jsonObject.get(str);
        return jsonElement == null ? flag : jsonElement.getAsBoolean();
    }

    public static String getString(JsonObject jsonObject, String str) {
        return getString(jsonObject, str, (String)null);
    }

    public static String getString(JsonObject jsonObject, String str, String str2) {
        JsonElement jsonElement = jsonObject.get(str);
        return jsonElement == null ? str2 : jsonElement.getAsString();
    }

    public static float[] parseFloatArray(JsonElement jsonElement, int n) {
        return parseFloatArray(jsonElement, n, (float[])null);
    }

    public static float[] parseFloatArray(JsonElement jsonElement, int n, float[] floats) {
        if (jsonElement == null) {
            return floats;
        }

        JsonArray asJsonArray = jsonElement.getAsJsonArray();
        if (asJsonArray.size() != n) {
            throw new JsonParseException("Wrong array length: " + asJsonArray.size() + ", should be: " + n + ", array: " + asJsonArray);
        }

        float[] floats2 = new float[asJsonArray.size()];

        for (int i = 0; i < floats2.length; i++) {
            floats2[i] = asJsonArray.get(i).getAsFloat();
        }

        return floats2;
    }

    public static int[] parseIntArray(JsonElement jsonElement, int n) {
        return parseIntArray(jsonElement, n, (int[])null);
    }

    public static int[] parseIntArray(JsonElement jsonElement, int n, int[] ints) {
        if (jsonElement == null) {
            return ints;
        }

        JsonArray asJsonArray = jsonElement.getAsJsonArray();
        if (asJsonArray.size() != n) {
            throw new JsonParseException("Wrong array length: " + asJsonArray.size() + ", should be: " + n + ", array: " + asJsonArray);
        }

        int[] ints2 = new int[asJsonArray.size()];

        for (int i = 0; i < ints2.length; i++) {
            ints2[i] = asJsonArray.get(i).getAsInt();
        }

        return ints2;
    }
}
