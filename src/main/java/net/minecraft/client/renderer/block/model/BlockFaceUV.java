package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;

public class BlockFaceUV {
   public float[] uvs;
   public int rotation;

   public BlockFaceUV(float[] uvsIn, int rotationIn) {
      this.uvs = uvsIn;
      this.rotation = rotationIn;
   }

   public float computeFloat(int n) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      }

      int rotatedUVIndex = this.getRotatedUVIndex(n);
      return rotatedUVIndex != 0 && rotatedUVIndex != 1 ? this.uvs[2] : this.uvs[0];
   }

   public float computeFloat2(int n) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      }

      int rotatedUVIndex = this.getRotatedUVIndex(n);
      return rotatedUVIndex != 0 && rotatedUVIndex != 3 ? this.uvs[3] : this.uvs[1];
   }

   public int getRotatedUVIndex(int n) {
      return (n + this.rotation / 90) % 4;
   }

   public int getDirectionIndex(int n) {
      return (n + (4 - this.rotation / 90)) % 4;
   }

   public void setUvs(float[] uvsIn) {
      if (this.uvs == null) {
         this.uvs = uvsIn;
      }
   }

   static class Deserializer implements JsonDeserializer<BlockFaceUV> {
      public Deserializer() {
      }

      public BlockFaceUV deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         float[] parseUV = this.parseUV(asJsonObject);
         int parseRotation = this.parseRotation(asJsonObject);
         return new BlockFaceUV(parseUV, parseRotation);
      }

      public int parseRotation(JsonObject jsonObject) {
         int int2 = JsonUtils.getInt(jsonObject, "rotation", 0);
         if (int2 >= 0 && int2 % 90 == 0 && int2 / 90 <= 3) {
            return int2;
         } else {
            throw new JsonParseException("Invalid rotation " + int2 + " found, only 0/90/180/270 allowed");
         }
      }

      public float[] parseUV(JsonObject jsonObject) {
         if (!jsonObject.has("uv")) {
            return null;
         }

         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "uv");
         if (jsonArray.size() != 4) {
            throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
         }

         float[] floats = new float[4];

         for (int i = 0; i < floats.length; i++) {
            floats[i] = JsonUtils.getFloat(jsonArray.get(i), "uv[" + i + "]");
         }

         return floats;
      }
   }
}
