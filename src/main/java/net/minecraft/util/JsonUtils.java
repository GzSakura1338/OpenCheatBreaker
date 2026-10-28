package net.minecraft.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {
    public static boolean jsonObjectFieldTypeIsString(JsonObject jsonObject, String str) {
        return jsonObjectFieldTypeIsPrimitive(jsonObject, str) && jsonObject.getAsJsonPrimitive(str).isString();
    }

    public static boolean jsonElementTypeIsString(JsonElement jsonElement) {
        return jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString();
    }

    public static boolean jsonObjectFieldTypeIsArray(JsonObject jsonObject, String str) {
        return jsonObjectHasNamedField(jsonObject, str) && jsonObject.get(str).isJsonArray();
    }

    public static boolean jsonObjectFieldTypeIsPrimitive(JsonObject jsonObject, String str) {
        return jsonObjectHasNamedField(jsonObject, str) && jsonObject.get(str).isJsonPrimitive();
    }

    public static boolean jsonObjectHasNamedField(JsonObject jsonObject, String str) {
        return jsonObject != null && jsonObject.get(str) != null;
    }

    public static String getJsonElementStringValue(JsonElement jsonElement, String str) {
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + str + " to be a string, was " + getJsonElementTypeDescription(jsonElement));
        }
    }

    public static String getJsonObjectStringFieldValue(JsonObject jsonObject, String str) {
        if (jsonObject.has(str)) {
            return getJsonElementStringValue(jsonObject.get(str), str);
        } else {
            throw new JsonSyntaxException("Missing " + str + ", expected to find a string");
        }
    }

    public static String getJsonObjectStringFieldValueOrDefault(JsonObject jsonObject, String str, String str2) {
        return jsonObject.has(str) ? getJsonElementStringValue(jsonObject.get(str), str) : str2;
    }

    public static boolean getJsonElementBooleanValue(JsonElement jsonElement, String str) {
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsBoolean();
        } else {
            throw new JsonSyntaxException("Expected " + str + " to be a Boolean, was " + getJsonElementTypeDescription(jsonElement));
        }
    }

    public static boolean getJsonObjectBooleanFieldValue(JsonObject jsonObject, String str) {
        if (jsonObject.has(str)) {
            return getJsonElementBooleanValue(jsonObject.get(str), str);
        } else {
            throw new JsonSyntaxException("Missing " + str + ", expected to find a Boolean");
        }
    }

    public static boolean getJsonObjectBooleanFieldValueOrDefault(JsonObject jsonObject, String str, boolean flag) {
        return jsonObject.has(str) ? getJsonElementBooleanValue(jsonObject.get(str), str) : flag;
    }

    public static float getJsonElementFloatValue(JsonElement jsonElement, String str) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsFloat();
        } else {
            throw new JsonSyntaxException("Expected " + str + " to be a Float, was " + getJsonElementTypeDescription(jsonElement));
        }
    }

    public static float getJsonObjectFloatFieldValue(JsonObject jsonObject, String str) {
        if (jsonObject.has(str)) {
            return getJsonElementFloatValue(jsonObject.get(str), str);
        } else {
            throw new JsonSyntaxException("Missing " + str + ", expected to find a Float");
        }
    }

    public static float getJsonObjectFloatFieldValueOrDefault(JsonObject jsonObject, String str, float f) {
        return jsonObject.has(str) ? getJsonElementFloatValue(jsonObject.get(str), str) : f;
    }

    public static int getJsonElementIntegerValue(JsonElement jsonElement, String str) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsInt();
        } else {
            throw new JsonSyntaxException("Expected " + str + " to be a Int, was " + getJsonElementTypeDescription(jsonElement));
        }
    }

    public static int getJsonObjectIntegerFieldValue(JsonObject jsonObject, String str) {
        if (jsonObject.has(str)) {
            return getJsonElementIntegerValue(jsonObject.get(str), str);
        } else {
            throw new JsonSyntaxException("Missing " + str + ", expected to find a Int");
        }
    }

    public static int getJsonObjectIntegerFieldValueOrDefault(JsonObject jsonObject, String str, int n) {
        return jsonObject.has(str) ? getJsonElementIntegerValue(jsonObject.get(str), str) : n;
    }

    public static JsonObject getElementAsJsonObject(JsonElement jsonElement, String str) {
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Expected " + str + " to be a JsonObject, was " + getJsonElementTypeDescription(jsonElement));
        }
    }

    public static JsonObject getJsonObject(JsonObject base, String key) {
        if (base.has(key)) {
            return getElementAsJsonObject(base.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonObject");
        }
    }

    public static JsonObject getJsonObjectFieldOrDefault(JsonObject jsonObject, String str, JsonObject jsonObject2) {
        return jsonObject.has(str) ? getElementAsJsonObject(jsonObject.get(str), str) : jsonObject2;
    }

    public static JsonArray getJsonElementAsJsonArray(JsonElement jsonElement, String str) {
        if (jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        } else {
            throw new JsonSyntaxException("Expected " + str + " to be a JsonArray, was " + getJsonElementTypeDescription(jsonElement));
        }
    }

    public static JsonArray getJsonObjectJsonArrayField(JsonObject jsonObject, String str) {
        if (jsonObject.has(str)) {
            return getJsonElementAsJsonArray(jsonObject.get(str), str);
        } else {
            throw new JsonSyntaxException("Missing " + str + ", expected to find a JsonArray");
        }
    }

    public static JsonArray getJsonObjectJsonArrayFieldOrDefault(JsonObject jsonObject, String str, JsonArray jsonArray) {
        return jsonObject.has(str) ? getJsonElementAsJsonArray(jsonObject.get(str), str) : jsonArray;
    }

    public static String getJsonElementTypeDescription(JsonElement jsonElement) {
        String abbreviateMiddle = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(jsonElement), "...", 10);
        if (jsonElement == null) {
            return "null (missing)";
        }

        if (jsonElement.isJsonNull()) {
            return "null (json)";
        }

        if (jsonElement.isJsonArray()) {
            return "an array (" + abbreviateMiddle + ")";
        }

        if (jsonElement.isJsonObject()) {
            return "an object (" + abbreviateMiddle + ")";
        }

        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive asJsonPrimitive = jsonElement.getAsJsonPrimitive();
            if (asJsonPrimitive.isNumber()) {
                return "a number (" + abbreviateMiddle + ")";
            }

            if (asJsonPrimitive.isBoolean()) {
                return "a boolean (" + abbreviateMiddle + ")";
            }
        }

        return abbreviateMiddle;
    }
}
