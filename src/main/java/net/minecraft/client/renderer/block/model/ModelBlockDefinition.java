package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class ModelBlockDefinition {
   public static Gson GSON = new GsonBuilder()
      .registerTypeAdapter(ModelBlockDefinition.class, new ModelBlockDefinition.Deserializer())
      .registerTypeAdapter(ModelBlockDefinition.Variant.class, new ModelBlockDefinition.Variant.Deserializer())
      .create();
   public Map<String, ModelBlockDefinition.Variants> mapVariants = Maps.newHashMap();

   public static ModelBlockDefinition parseFromReader(Reader reader) {
      return (ModelBlockDefinition)GSON.fromJson(reader, ModelBlockDefinition.class);
   }

   public ModelBlockDefinition(Collection<ModelBlockDefinition.Variants> collection) {
      for (ModelBlockDefinition.Variants variants : collection) {
         this.mapVariants.put(variants.name, variants);
      }
   }

   public ModelBlockDefinition(List<ModelBlockDefinition> list) {
      for (ModelBlockDefinition modelBlockDefinition : list) {
         this.mapVariants.putAll(modelBlockDefinition.mapVariants);
      }
   }

   public ModelBlockDefinition.Variants getVariants(String str) {
      ModelBlockDefinition.Variants variants = this.mapVariants.get(str);
      if (variants == null) {
         throw new ModelBlockDefinition.MissingVariantException();
      } else {
         return variants;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof ModelBlockDefinition) {
         ModelBlockDefinition modelBlockDefinition = (ModelBlockDefinition)obj;
         return this.mapVariants.equals(modelBlockDefinition.mapVariants);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.mapVariants.hashCode();
   }

   public static class Deserializer implements JsonDeserializer<ModelBlockDefinition> {
      public ModelBlockDefinition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         List parseVariantsList = this.parseVariantsList(jsonDeserializationContext, asJsonObject);
         return new ModelBlockDefinition((Collection<ModelBlockDefinition.Variants>)parseVariantsList);
      }

      public List<ModelBlockDefinition.Variants> parseVariantsList(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonObject, "variants");
         ArrayList newArrayList = Lists.newArrayList();

         for (Entry entry : jsonObject2.entrySet()) {
            newArrayList.add(this.parseVariants(jsonDeserializationContext, entry));
         }

         return newArrayList;
      }

      public ModelBlockDefinition.Variants parseVariants(JsonDeserializationContext jsonDeserializationContext, Entry<String, JsonElement> entry) {
         String key = (String)entry.getKey();
         ArrayList newArrayList = Lists.newArrayList();
         JsonElement value = (JsonElement)entry.getValue();
         if (value.isJsonArray()) {
            for (JsonElement jsonElement : value.getAsJsonArray()) {
               newArrayList.add((ModelBlockDefinition.Variant)jsonDeserializationContext.deserialize(jsonElement, ModelBlockDefinition.Variant.class));
            }
         } else {
            newArrayList.add((ModelBlockDefinition.Variant)jsonDeserializationContext.deserialize(value, ModelBlockDefinition.Variant.class));
         }

         return new ModelBlockDefinition.Variants(key, newArrayList);
      }
   }

   public class MissingVariantException extends RuntimeException {
   }

   public static class Variant {
      public ResourceLocation modelLocation;
      public ModelRotation modelRotation;
      public boolean uvLock;
      public int weight;

      public Variant(ResourceLocation modelLocationIn, ModelRotation modelRotationIn, boolean uvLockIn, int weightIn) {
         this.modelLocation = modelLocationIn;
         this.modelRotation = modelRotationIn;
         this.uvLock = uvLockIn;
         this.weight = weightIn;
      }

      public ResourceLocation getModelLocation() {
         return this.modelLocation;
      }

      public ModelRotation getRotation() {
         return this.modelRotation;
      }

      public boolean isUvLocked() {
         return this.uvLock;
      }

      public int getWeight() {
         return this.weight;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }

         if (!(obj instanceof ModelBlockDefinition.Variant)) {
            return false;
         }

         ModelBlockDefinition.Variant variant = (ModelBlockDefinition.Variant)obj;
         return this.modelLocation.equals(variant.modelLocation) && this.modelRotation == variant.modelRotation && this.uvLock == variant.uvLock;
      }

      @Override
      public int hashCode() {
         int n = this.modelLocation.hashCode();
         n = 31 * n + (this.modelRotation != null ? this.modelRotation.hashCode() : 0);
         return 31 * n + (this.uvLock ? 1 : 0);
      }

      public static class Deserializer implements JsonDeserializer<ModelBlockDefinition.Variant> {
         public ModelBlockDefinition.Variant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject asJsonObject = jsonElement.getAsJsonObject();
            String parseModel = this.parseModel(asJsonObject);
            ModelRotation parseRotation = this.parseRotation(asJsonObject);
            boolean parseUvLock = this.parseUvLock(asJsonObject);
            int parseWeight = this.parseWeight(asJsonObject);
            return new ModelBlockDefinition.Variant(this.makeModelLocation(parseModel), parseRotation, parseUvLock, parseWeight);
         }

         public ResourceLocation makeModelLocation(String str) {
            ResourceLocation resourceLocation = new ResourceLocation(str);
            return new ResourceLocation(resourceLocation.getResourceDomain(), "block/" + resourceLocation.getResourcePath());
         }

         public boolean parseUvLock(JsonObject jsonObject) {
            return JsonUtils.getBoolean(jsonObject, "uvlock", false);
         }

         public ModelRotation parseRotation(JsonObject jsonObject) {
            int int2 = JsonUtils.getInt(jsonObject, "x", 0);
            int int3 = JsonUtils.getInt(jsonObject, "y", 0);
            ModelRotation modelRotation = ModelRotation.getModelRotation(int2, int3);
            if (modelRotation == null) {
               throw new JsonParseException("Invalid BlockModelRotation x: " + int2 + ", y: " + int3);
            } else {
               return modelRotation;
            }
         }

         public String parseModel(JsonObject jsonObject) {
            return JsonUtils.getString(jsonObject, "model");
         }

         public int parseWeight(JsonObject jsonObject) {
            return JsonUtils.getInt(jsonObject, "weight", 1);
         }
      }
   }

   public static class Variants {
      public String name;
      public List<ModelBlockDefinition.Variant> listVariants;

      public Variants(String nameIn, List<ModelBlockDefinition.Variant> listVariantsIn) {
         this.name = nameIn;
         this.listVariants = listVariantsIn;
      }

      public List<ModelBlockDefinition.Variant> getVariants() {
         return this.listVariants;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }

         if (!(obj instanceof ModelBlockDefinition.Variants)) {
            return false;
         }

         ModelBlockDefinition.Variants variants = (ModelBlockDefinition.Variants)obj;
         return !this.name.equals(variants.name) ? false : this.listVariants.equals(variants.listVariants);
      }

      @Override
      public int hashCode() {
         int n = this.name.hashCode();
         return 31 * n + this.listVariants.hashCode();
      }
   }
}
