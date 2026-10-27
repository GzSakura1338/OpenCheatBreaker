package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.cosmetic.Cosmetic;
import com.cheatbreaker.client.cosmetic.CosmeticType;
import com.cheatbreaker.client.util.Logging;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BlockModelParser {
   public static Map<ResourceLocation, BlockModelParser> CACHE = new HashMap<>();
   public Map<ResourceLocation, List<float[]>> quads;
   public static Set<ResourceLocation> failedModels = new HashSet<>();
   public float[] translation;
   public float[] rotation;
   public float[] scale;
   public static String[] FACE_NAMES = new String[]{"down", "up", "north", "south", "west", "east"};

   public BlockModelParser(Map<ResourceLocation, List<float[]>> map, float[] floats, float[] floats2, float[] floats3) {
      this.quads = map;
      this.translation = floats;
      this.rotation = floats2;
      this.scale = floats3;
   }

   public static float[] getDefaultUv(String str, float[] floats, float[] floats2) {
      switch (str) {
         case "down":
            return new float[]{floats[0], 16.0F - floats2[2], floats2[0], 16.0F - floats[2]};
         case "up":
            return new float[]{floats[0], floats[2], floats2[0], floats2[2]};
         case "north":
            return new float[]{16.0F - floats2[0], 16.0F - floats2[1], 16.0F - floats[0], 16.0F - floats[1]};
         case "south":
            return new float[]{floats[0], 16.0F - floats2[1], floats2[0], 16.0F - floats[1]};
         case "west":
            return new float[]{floats[2], 16.0F - floats2[1], floats2[2], 16.0F - floats[1]};
         default:
            return new float[]{16.0F - floats2[2], 16.0F - floats2[1], 16.0F - floats[2], 16.0F - floats[1]};
      }
   }

   public static float[] rotateVertex(float[] floats, String str, float f, float[] floats2, boolean flag) {
      if (str != null && f != 0.0F) {
         float[] floats3 = floats2 == null ? new float[3] : floats2;
         float f2 = floats[0] - floats3[0];
         float f3 = floats[1] - floats3[1];
         float f4 = floats[2] - floats3[2];
         double toRadians = Math.toRadians(f);
         float cos = (float)Math.cos(toRadians);
         float sin = (float)Math.sin(toRadians);
         float f5 = flag && floats2 != null ? 1.0F / Math.abs(cos) : 1.0F;
         float f6 = f2;
         float f7 = f3;
         float f8 = f4;
         switch (str) {
            case "x":
               f7 = (f3 * cos - f4 * sin) * f5;
               f8 = (f3 * sin + f4 * cos) * f5;
               break;
            case "y":
               f6 = (f2 * cos + f4 * sin) * f5;
               f8 = (-f2 * sin + f4 * cos) * f5;
               break;
            case "z":
               f6 = (f2 * cos - f3 * sin) * f5;
               f7 = (f2 * sin + f3 * cos) * f5;
               break;
            default:
               return (float[])floats.clone();
         }

         return new float[]{f6 + floats3[0], f7 + floats3[1], f8 + floats3[2]};
      } else {
         return (float[])floats.clone();
      }
   }

   public static float[] readVector(JsonObject jsonObject, String str, float[] floats) {
      return jsonObject.has(str) ? readFloats(jsonObject.getAsJsonArray(str), 3) : floats;
   }

   public static float[] getFaceNormal(String str) {
      switch (str) {
         case "down":
            return new float[]{0.0F, -1.0F, 0.0F};
         case "up":
            return new float[]{0.0F, 1.0F, 0.0F};
         case "north":
            return new float[]{0.0F, 0.0F, -1.0F};
         case "south":
            return new float[]{0.0F, 0.0F, 1.0F};
         case "west":
            return new float[]{-1.0F, 0.0F, 0.0F};
         default:
            return new float[]{1.0F, 0.0F, 0.0F};
      }
   }

   public static float[][] getFaceVertices(String str, float[] floats, float[] floats2) {
      float f = floats[0];
      float f2 = floats[1];
      float f3 = floats[2];
      float f4 = floats2[0];
      float f5 = floats2[1];
      float f6 = floats2[2];
      switch (str) {
         case "down":
            return new float[][]{{f, f2, f6}, {f, f2, f3}, {f4, f2, f3}, {f4, f2, f6}};
         case "up":
            return new float[][]{{f, f5, f3}, {f, f5, f6}, {f4, f5, f6}, {f4, f5, f3}};
         case "north":
            return new float[][]{{f4, f5, f3}, {f4, f2, f3}, {f, f2, f3}, {f, f5, f3}};
         case "south":
            return new float[][]{{f, f5, f6}, {f, f2, f6}, {f4, f2, f6}, {f4, f5, f6}};
         case "west":
            return new float[][]{{f, f5, f3}, {f, f2, f3}, {f, f2, f6}, {f, f5, f6}};
         default:
            return new float[][]{{f4, f5, f6}, {f4, f2, f6}, {f4, f2, f3}, {f4, f5, f3}};
      }
   }

   public static BlockModelParser loadModel(ResourceLocation resourceLocation, ResourceLocation resourceLocation2) throws java.io.IOException {
      JsonObject jsonObject;
      try (InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream()) {
         jsonObject = JsonParser.parseReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).getAsJsonObject();
      }

      String resourcePath = resourceLocation.getResourcePath();
      String substring = resourcePath.substring(0, resourcePath.lastIndexOf(47) + 1);
      HashMap map = new HashMap();
      if (jsonObject.has("textures")) {
         for (Entry entry : jsonObject.getAsJsonObject("textures").entrySet()) {
            map.put(entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
         }
      }

      LinkedHashMap linkedHashMap = new LinkedHashMap();
      if (jsonObject.has("elements")) {
         for (JsonElement jsonElement : jsonObject.getAsJsonArray("elements")) {
            parseElement(jsonElement.getAsJsonObject(), map, resourceLocation.getResourceDomain(), substring, linkedHashMap);
         }
      }

      if (resourceLocation2 != null) {
         ResourceLocation resourceLocation3 = new ResourceLocation(resourceLocation.getResourceDomain(), resourcePath.substring(0, resourcePath.length() - 5) + ".png");
         List list = (List)linkedHashMap.remove(resourceLocation3);
         if (list != null) {
            ((List)linkedHashMap.computeIfAbsent(resourceLocation2, obj -> new ArrayList())).addAll(list);
         }
      }

      float[] floats = new float[]{0.0F, 0.0F, 0.0F};
      float[] floats2 = new float[]{0.0F, 0.0F, 0.0F};
      float[] floats3 = new float[]{1.0F, 1.0F, 1.0F};
      if (jsonObject.has("display") && jsonObject.getAsJsonObject("display").has("head")) {
         JsonObject asJsonObject = jsonObject.getAsJsonObject("display").getAsJsonObject("head");
         floats = readVector(asJsonObject, "translation", floats);
         floats2 = readVector(asJsonObject, "rotation", floats2);
         floats3 = readVector(asJsonObject, "scale", floats3);

         for (int i = 0; i < 3; i++) {
            floats[i] = MathHelper.clamp_float(floats[i], -80.0F, 80.0F);
            floats3[i] = MathHelper.clamp_float(floats3[i], -4.0F, 4.0F);
         }
      }

      return new BlockModelParser(linkedHashMap, floats, floats2, floats3);
   }

   public static ResourceLocation resolveTexture(String str, Map<String, String> map, String str2, String str3) {
      String str4 = str;

      for (int i = 0; str4 != null && str4.startsWith("#") && i < 8; i++) {
         str4 = (String)map.get(str4.substring(1));
      }

      if (str4 != null && !str4.isEmpty() && !str4.startsWith("#")) {
         String substring = str4.substring(Math.max(str4.lastIndexOf(47), str4.lastIndexOf(58)) + 1);
         if (substring.endsWith(".png")) {
            substring = substring.substring(0, substring.length() - 4);
         }

         return new ResourceLocation(str2, str3 + substring + ".png");
      } else {
         return null;
      }
   }

   public void renderStatic() {
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 0.25F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.scale(0.625F, 0.625F, 0.625F);
      this.render();
      GlStateManager.popMatrix();
   }

   public static void parseElement(JsonObject jsonObject, Map<String, String> map, String str, String str2, Map<ResourceLocation, List<float[]>> map2) {
      float[] readVector2 = readVector(jsonObject, "from", null);
      float[] readVector3 = readVector(jsonObject, "to", null);
      if (readVector2 != null && readVector3 != null && jsonObject.has("faces")) {
         float[] floats = new float[]{Math.min(readVector2[0], readVector3[0]), Math.min(readVector2[1], readVector3[1]), Math.min(readVector2[2], readVector3[2])};
         float[] floats2 = new float[]{Math.max(readVector2[0], readVector3[0]), Math.max(readVector2[1], readVector3[1]), Math.max(readVector2[2], readVector3[2])};
         String str3 = null;
         float f = 0.0F;
         float[] floats3 = new float[]{8.0F, 8.0F, 8.0F};
         boolean flag = false;
         if (jsonObject.has("rotation")) {
            JsonObject asJsonObject = jsonObject.getAsJsonObject("rotation");
            str3 = asJsonObject.has("axis") ? asJsonObject.get("axis").getAsString() : null;
            f = asJsonObject.has("angle") ? asJsonObject.get("angle").getAsFloat() : 0.0F;
            floats3 = readVector(asJsonObject, "origin", floats3);
            flag = asJsonObject.has("rescale") && asJsonObject.get("rescale").getAsBoolean();
         }

         JsonObject asJsonObject = jsonObject.getAsJsonObject("faces");

         for (String str4 : FACE_NAMES) {
            if (asJsonObject.has(str4)) {
               JsonObject asJsonObject2 = asJsonObject.getAsJsonObject(str4);
               ResourceLocation resourceLocation = asJsonObject2.has("texture") ? resolveTexture(asJsonObject2.get("texture").getAsString(), map, str, str2) : null;
               if (resourceLocation != null) {
                  float[] floats4 = asJsonObject2.has("uv") ? readFloats(asJsonObject2.getAsJsonArray("uv"), 4) : getDefaultUv(str4, floats, floats2);
                  int n = asJsonObject2.has("rotation") ? Math.floorMod(asJsonObject2.get("rotation").getAsInt() / 90, 4) : 0;
                  float[][] faceVertices = getFaceVertices(str4, floats, floats2);
                  float[][] floatses = new float[][]{{floats4[0], floats4[1]}, {floats4[0], floats4[3]}, {floats4[2], floats4[3]}, {floats4[2], floats4[1]}};
                  float[] floats5 = new float[23];

                  for (int i = 0; i < 4; i++) {
                     float[] rotateVertex2 = rotateVertex(faceVertices[i], str3, f, floats3, flag);
                     float[] floats6 = floatses[(i + n) % 4];
                     floats5[i * 5] = rotateVertex2[0] / 16.0F;
                     floats5[i * 5 + 1] = rotateVertex2[1] / 16.0F;
                     floats5[i * 5 + 2] = rotateVertex2[2] / 16.0F;
                     floats5[i * 5 + 3] = floats6[0] / 16.0F;
                     floats5[i * 5 + 4] = floats6[1] / 16.0F;
                  }

                  float[] rotateVertex2 = rotateVertex(getFaceNormal(str4), str3, f, null, false);
                  floats5[20] = rotateVertex2[0];
                  floats5[21] = rotateVertex2[1];
                  floats5[22] = rotateVertex2[2];
                  map2.computeIfAbsent(resourceLocation, resourceLocation2 -> new ArrayList<>()).add(floats5);
               }
            }
         }
      }
   }

   public static BlockModelParser forEntity(Entity entity) {
      if (entity instanceof EntityPlayer && !entity.isInvisible() && CheatBreaker.getInstance().getKeybindSettings().showCheatbreakerHats.getBooleanValue()) {
         Cosmetic cosmetic3 = ((EntityPlayer)entity).getCosmetic3();
         return cosmetic3 != null && cosmetic3.getCosmeticType() == CosmeticType.HAT ? get(cosmetic3.getTexture()) : null;
      } else {
         return null;
      }
   }

   public void renderOnHead(ModelRenderer modelRenderer, float f) {
      GlStateManager.pushMatrix();
      modelRenderer.postRender(f);
      GlStateManager.translate(0.0F, -0.25F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.scale(0.625F, -0.625F, -0.625F);
      this.render();
      GlStateManager.popMatrix();
   }

   public void render() {
      GlStateManager.pushMatrix();
      GlStateManager.translate(this.translation[0] / 16.0F, this.translation[1] / 16.0F, this.translation[2] / 16.0F);
      GlStateManager.rotate(this.rotation[1], 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(this.rotation[0], 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(this.rotation[2], 0.0F, 0.0F, 1.0F);
      GlStateManager.scale(this.scale[0], this.scale[1], this.scale[2]);
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      boolean glIsEnabled = GL11.glIsEnabled(2884);
      GlStateManager.enableCull();

      try {
         for (Entry entry : this.quads.entrySet()) {
            Minecraft.getMinecraft().getTextureManager().bindTexture((ResourceLocation)entry.getKey());
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

            for (float[] floats : (Iterable<float[]>)(List)entry.getValue()) {
               for (int i = 0; i < 4; i++) {
                  int n = i * 5;
                  worldRenderer.pos(floats[n], floats[n + 1], floats[n + 2]).tex(floats[n + 3], floats[n + 4]).normal(floats[20], floats[21], floats[22]).endVertex();
               }
            }

            instance.draw();
         }
      } finally {
         if (!glIsEnabled) {
            GlStateManager.disableCull();
         }

         GlStateManager.popMatrix();
      }
   }

   public static float[] readFloats(JsonArray jsonArray, int n) {
      float[] floats = new float[n];

      for (int i = 0; i < n && i < jsonArray.size(); i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
      }

      return floats;
   }

   public static BlockModelParser get(ResourceLocation resourceLocation) {
      String resourcePath = resourceLocation.getResourcePath();
      ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getResourceDomain(), (resourcePath.endsWith(".png") ? resourcePath.substring(0, resourcePath.length() - 4) : resourcePath) + ".json");
      BlockModelParser blockModelParser = CACHE.get(resourceLocation);
      if (blockModelParser == null && !failedModels.contains(resourceLocation)) {
         try {
            try {
               blockModelParser = loadModel(resourceLocation2, null);
            } catch (FileNotFoundException e) {
               int lastIndexOf = resourcePath.lastIndexOf(47);
               if (lastIndexOf < 0) {
                  throw e;
               }

               String substring = resourcePath.substring(0, lastIndexOf);
               ResourceLocation resourceLocation3 = new ResourceLocation(resourceLocation.getResourceDomain(), substring + "/" + substring.substring(substring.lastIndexOf(47) + 1) + ".json");
               if (resourceLocation3.equals(resourceLocation2)) {
                  throw e;
               }

               resourceLocation2 = resourceLocation3;
               blockModelParser = loadModel(resourceLocation3, resourceLocation);
            }

            CACHE.put(resourceLocation, blockModelParser);
         } catch (Exception e) {
            failedModels.add(resourceLocation);
            Logging.errorTagged("Hats", "Could not load hat model " + resourceLocation2 + " for " + resourceLocation + ": " + e);
         }

         return blockModelParser;
      } else {
         return blockModelParser;
      }
   }
}
