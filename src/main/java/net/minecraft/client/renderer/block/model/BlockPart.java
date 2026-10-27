package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.util.EnumFacing.Axis;
import org.lwjgl.util.vector.Vector3f;

public class BlockPart {
   public Vector3f positionFrom;
   public Vector3f positionTo;
   public Map<EnumFacing, BlockPartFace> mapFaces;
   public BlockPartRotation partRotation;
   public boolean shade;

   public BlockPart(
      Vector3f positionFromIn, Vector3f positionToIn, Map<EnumFacing, BlockPartFace> mapFacesIn, BlockPartRotation partRotationIn, boolean shadeIn
   ) {
      this.positionFrom = positionFromIn;
      this.positionTo = positionToIn;
      this.mapFaces = mapFacesIn;
      this.partRotation = partRotationIn;
      this.shade = shadeIn;
      this.setDefaultUvs();
   }

   public void setDefaultUvs() {
      for (Entry entry : this.mapFaces.entrySet()) {
         float[] faceUvs = this.getFaceUvs((EnumFacing)entry.getKey());
         ((BlockPartFace)entry.getValue()).blockFaceUV.setUvs(faceUvs);
      }
   }

   public float[] getFaceUvs(EnumFacing enumFacing) {
      float[] floats;
      switch (enumFacing) {
         case DOWN:
         case UP:
            floats = new float[]{this.positionFrom.x, this.positionFrom.z, this.positionTo.x, this.positionTo.z};
            break;
         case NORTH:
         case SOUTH:
            floats = new float[]{this.positionFrom.x, 16.0F - this.positionTo.y, this.positionTo.x, 16.0F - this.positionFrom.y};
            break;
         case WEST:
         case EAST:
            floats = new float[]{this.positionFrom.z, 16.0F - this.positionTo.y, this.positionTo.z, 16.0F - this.positionFrom.y};
            break;
         default:
            throw new NullPointerException();
      }

      return floats;
   }

   static class Deserializer implements JsonDeserializer<BlockPart> {
      public Deserializer() {
      }

      public BlockPart deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         Vector3f parsePositionFrom = this.parsePositionFrom(asJsonObject);
         Vector3f parsePositionTo = this.parsePositionTo(asJsonObject);
         BlockPartRotation parseRotation = this.parseRotation(asJsonObject);
         Map parseFacesCheck = this.parseFacesCheck(jsonDeserializationContext, asJsonObject);
         if (asJsonObject.has("shade") && !JsonUtils.isBoolean(asJsonObject, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         }

         boolean boolean2 = JsonUtils.getBoolean(asJsonObject, "shade", true);
         return new BlockPart(parsePositionFrom, parsePositionTo, parseFacesCheck, parseRotation, boolean2);
      }

      public BlockPartRotation parseRotation(JsonObject jsonObject) {
         BlockPartRotation blockPartRotation = null;
         if (jsonObject.has("rotation")) {
            JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonObject, "rotation");
            Vector3f parsePosition = this.parsePosition(jsonObject2, "origin");
            parsePosition.scale(0.0625F);
            Axis parseAxis = this.parseAxis(jsonObject2);
            float parseAngle = this.parseAngle(jsonObject2);
            boolean boolean2 = JsonUtils.getBoolean(jsonObject2, "rescale", false);
            blockPartRotation = new BlockPartRotation(parsePosition, parseAxis, parseAngle, boolean2);
         }

         return blockPartRotation;
      }

      public float parseAngle(JsonObject jsonObject) {
         float float2 = JsonUtils.getFloat(jsonObject, "angle");
         if (float2 != 0.0F && MathHelper.abs(float2) != 22.5F && MathHelper.abs(float2) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + float2 + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return float2;
         }
      }

      public Axis parseAxis(JsonObject jsonObject) {
         String string = JsonUtils.getString(jsonObject, "axis");
         Axis byName = Axis.byName(string.toLowerCase());
         if (byName == null) {
            throw new JsonParseException("Invalid rotation axis: " + string);
         } else {
            return byName;
         }
      }

      public Map<EnumFacing, BlockPartFace> parseFacesCheck(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         Map parseFaces = this.parseFaces(jsonDeserializationContext, jsonObject);
         if (parseFaces.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return parseFaces;
         }
      }

      public Map<EnumFacing, BlockPartFace> parseFaces(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         EnumMap newEnumMap = Maps.newEnumMap(EnumFacing.class);
         JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonObject, "faces");

         for (Entry entry : jsonObject2.entrySet()) {
            EnumFacing parseEnumFacing = this.parseEnumFacing((String)entry.getKey());
            newEnumMap.put(parseEnumFacing, (BlockPartFace)jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), BlockPartFace.class));
         }

         return newEnumMap;
      }

      public EnumFacing parseEnumFacing(String name) {
         EnumFacing byName = EnumFacing.byName(name);
         if (byName == null) {
            throw new JsonParseException("Unknown facing: " + name);
         } else {
            return byName;
         }
      }

      public Vector3f parsePositionTo(JsonObject jsonObject) {
         Vector3f parsePosition = this.parsePosition(jsonObject, "to");
         if (parsePosition.x >= -16.0F && parsePosition.y >= -16.0F && parsePosition.z >= -16.0F && parsePosition.x <= 32.0F && parsePosition.y <= 32.0F && parsePosition.z <= 32.0F) {
            return parsePosition;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + parsePosition);
         }
      }

      public Vector3f parsePositionFrom(JsonObject jsonObject) {
         Vector3f parsePosition = this.parsePosition(jsonObject, "from");
         if (parsePosition.x >= -16.0F && parsePosition.y >= -16.0F && parsePosition.z >= -16.0F && parsePosition.x <= 32.0F && parsePosition.y <= 32.0F && parsePosition.z <= 32.0F) {
            return parsePosition;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + parsePosition);
         }
      }

      public Vector3f parsePosition(JsonObject jsonObject, String str) {
         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, str);
         if (jsonArray.size() != 3) {
            throw new JsonParseException("Expected 3 " + str + " values, found: " + jsonArray.size());
         }

         float[] floats = new float[3];

         for (int i = 0; i < floats.length; i++) {
            floats[i] = JsonUtils.getFloat(jsonArray.get(i), str + "[" + i + "]");
         }

         return new Vector3f(floats[0], floats[1], floats[2]);
      }
   }
}
