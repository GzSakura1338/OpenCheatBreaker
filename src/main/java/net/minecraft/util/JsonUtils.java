package net.minecraft.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {
   public static boolean isString(JsonObject jsonObject, String str) {
      return !isJsonPrimitive(jsonObject, str) ? false : jsonObject.getAsJsonPrimitive(str).isString();
   }

   public static boolean isString(JsonElement jsonElement) {
      return !jsonElement.isJsonPrimitive() ? false : jsonElement.getAsJsonPrimitive().isString();
   }

   public static boolean isBoolean(JsonObject jsonObject, String str) {
      return !isJsonPrimitive(jsonObject, str) ? false : jsonObject.getAsJsonPrimitive(str).isBoolean();
   }

   public static boolean isJsonArray(JsonObject jsonObject, String str) {
      return !hasField(jsonObject, str) ? false : jsonObject.get(str).isJsonArray();
   }

   public static boolean isJsonPrimitive(JsonObject jsonObject, String str) {
      return !hasField(jsonObject, str) ? false : jsonObject.get(str).isJsonPrimitive();
   }

   public static boolean hasField(JsonObject jsonObject, String str) {
      return jsonObject == null ? false : jsonObject.get(str) != null;
   }

   public static String getString(JsonElement jsonElement, String str) {
      if (jsonElement.isJsonPrimitive()) {
         return jsonElement.getAsString();
      } else {
         throw new JsonSyntaxException("Expected " + str + " to be a string, was " + toString(jsonElement));
      }
   }

   public static String getString(JsonObject jsonObject, String str) {
      if (jsonObject.has(str)) {
         return getString(jsonObject.get(str), str);
      } else {
         throw new JsonSyntaxException("Missing " + str + ", expected to find a string");
      }
   }

   public static String getString(JsonObject jsonObject, String str, String str2) {
      return jsonObject.has(str) ? getString(jsonObject.get(str), str) : str2;
   }

   public static boolean getBoolean(JsonElement jsonElement, String str) {
      if (jsonElement.isJsonPrimitive()) {
         return jsonElement.getAsBoolean();
      } else {
         throw new JsonSyntaxException("Expected " + str + " to be a Boolean, was " + toString(jsonElement));
      }
   }

   public static boolean getBoolean(JsonObject jsonObject, String str) {
      if (jsonObject.has(str)) {
         return getBoolean(jsonObject.get(str), str);
      } else {
         throw new JsonSyntaxException("Missing " + str + ", expected to find a Boolean");
      }
   }

   public static boolean getBoolean(JsonObject jsonObject, String str, boolean flag) {
      return jsonObject.has(str) ? getBoolean(jsonObject.get(str), str) : flag;
   }

   public static float getFloat(JsonElement jsonElement, String str) {
      if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
         return jsonElement.getAsFloat();
      } else {
         throw new JsonSyntaxException("Expected " + str + " to be a Float, was " + toString(jsonElement));
      }
   }

   public static float getFloat(JsonObject jsonObject, String str) {
      if (jsonObject.has(str)) {
         return getFloat(jsonObject.get(str), str);
      } else {
         throw new JsonSyntaxException("Missing " + str + ", expected to find a Float");
      }
   }

   public static float getFloat(JsonObject jsonObject, String str, float f) {
      return jsonObject.has(str) ? getFloat(jsonObject.get(str), str) : f;
   }

   public static int getInt(JsonElement jsonElement, String str) {
      if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
         return jsonElement.getAsInt();
      } else {
         throw new JsonSyntaxException("Expected " + str + " to be a Int, was " + toString(jsonElement));
      }
   }

   public static int getInt(JsonObject jsonObject, String str) {
      if (jsonObject.has(str)) {
         return getInt(jsonObject.get(str), str);
      } else {
         throw new JsonSyntaxException("Missing " + str + ", expected to find a Int");
      }
   }

   public static int getInt(JsonObject jsonObject, String str, int n) {
      return jsonObject.has(str) ? getInt(jsonObject.get(str), str) : n;
   }

   public static JsonObject getJsonObject(JsonElement jsonElement, String str) {
      if (jsonElement.isJsonObject()) {
         return jsonElement.getAsJsonObject();
      } else {
         throw new JsonSyntaxException("Expected " + str + " to be a JsonObject, was " + toString(jsonElement));
      }
   }

   public static JsonObject getJsonObject(JsonObject base, String key) {
      if (base.has(key)) {
         return getJsonObject(base.get(key), key);
      } else {
         throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonObject");
      }
   }

   public static JsonObject getJsonObject(JsonObject jsonObject, String str, JsonObject jsonObject2) {
      return jsonObject.has(str) ? getJsonObject(jsonObject.get(str), str) : jsonObject2;
   }

   public static JsonArray getJsonArray(JsonElement jsonElement, String str) {
      if (jsonElement.isJsonArray()) {
         return jsonElement.getAsJsonArray();
      } else {
         throw new JsonSyntaxException("Expected " + str + " to be a JsonArray, was " + toString(jsonElement));
      }
   }

   public static JsonArray getJsonArray(JsonObject jsonObject, String str) {
      if (jsonObject.has(str)) {
         return getJsonArray(jsonObject.get(str), str);
      } else {
         throw new JsonSyntaxException("Missing " + str + ", expected to find a JsonArray");
      }
   }

   public static JsonArray getJsonArray(JsonObject jsonObject, String str, JsonArray jsonArray) {
      return jsonObject.has(str) ? getJsonArray(jsonObject.get(str), str) : jsonArray;
   }

   public static String toString(JsonElement jsonElement) {
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
