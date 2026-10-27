package net.minecraft.client.renderer.block.model;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.impl.PackTweaksMod;
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
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector3f;

public class ModelBlock {
   public static Logger LOGGER = LogManager.getLogger();
   public static Gson SERIALIZER = new GsonBuilder()
      .registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer())
      .registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer())
      .registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer())
      .registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer())
      .registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer())
      .registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer())
      .create();
   public String name = "";
   public List<BlockPart> elements;
   public boolean ambientOcclusion;
   public boolean gui3d;
   public Map<String, String> textures;
   public ResourceLocation parentLocation;
   public ModelBlock parent;
   public ItemCameraTransforms cameraTransforms;

   public static ModelBlock deserialize(Reader readerIn) {
      return (ModelBlock)SERIALIZER.fromJson(readerIn, ModelBlock.class);
   }

   public static ModelBlock deserialize(String jsonString) {
      return deserialize(new StringReader(jsonString));
   }

   public ModelBlock(
      List<BlockPart> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn
   ) {
      this((ResourceLocation)null, elementsIn, texturesIn, ambientOcclusionIn, gui3dIn, cameraTransformsIn);
   }

   public ModelBlock(
      ResourceLocation parentLocationIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn
   ) {
      this(parentLocationIn, Collections.emptyList(), texturesIn, ambientOcclusionIn, gui3dIn, cameraTransformsIn);
   }

   public ModelBlock(
      ResourceLocation parentLocationIn,
      List<BlockPart> elementsIn,
      Map<String, String> texturesIn,
      boolean ambientOcclusionIn,
      boolean gui3dIn,
      ItemCameraTransforms cameraTransformsIn
   ) {
      this.elements = elementsIn;
      this.ambientOcclusion = ambientOcclusionIn;
      this.gui3d = gui3dIn;
      this.textures = texturesIn;
      this.parentLocation = parentLocationIn;
      this.cameraTransforms = cameraTransformsIn;
   }

   public List<BlockPart> getElements() {
      return this.hasParent() ? this.parent.getElements() : this.elements;
   }

   public boolean hasParent() {
      return this.parent != null;
   }

   public boolean isAmbientOcclusion() {
      return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3d;
   }

   public boolean isResolved() {
      return this.parentLocation == null || this.parent != null && this.parent.isResolved();
   }

   public void getParentFromMap(Map<ResourceLocation, ModelBlock> map) {
      if (this.parentLocation != null) {
         this.parent = (ModelBlock)map.get(this.parentLocation);
      }
   }

   public boolean isTexturePresent(String textureName) {
      return !"missingno".equals(this.resolveTextureName(textureName));
   }

   public String resolveTextureName(String textureName) {
      if (!this.startsWithHash(textureName)) {
         textureName = '#' + textureName;
      }

      return this.resolveTextureName(textureName, new ModelBlock.Bookkeep(this));
   }

   public String resolveTextureName(String textureName, ModelBlock.Bookkeep bookkeep) {
      if (this.startsWithHash(textureName)) {
         if (this == bookkeep.modelExt) {
            LOGGER.warn("Unable to resolve texture due to upward reference: " + textureName + " in " + this.name);
            return "missingno";
         }

         String str = this.textures.get(textureName.substring(1));
         if (str == null && this.hasParent()) {
            str = this.parent.resolveTextureName(textureName, bookkeep);
         }

         bookkeep.modelExt = this;
         if (str != null && this.startsWithHash(str)) {
            str = bookkeep.model.resolveTextureName(str, bookkeep);
         }

         return str != null && !this.startsWithHash(str) ? str : "missingno";
      } else {
         return textureName;
      }
   }

   public boolean startsWithHash(String hash) {
      return hash.charAt(0) == '#';
   }

   public ResourceLocation getParentLocation() {
      return this.parentLocation;
   }

   public ModelBlock getRootModel() {
      return this.hasParent() ? this.parent.getRootModel() : this;
   }

   public ItemCameraTransforms getAllTransforms() {
      ItemTransformVec3f transform = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON);
      ItemTransformVec3f transform2 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON);
      ItemTransformVec3f transform3 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
      ItemTransformVec3f transform4 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
      ItemTransformVec3f transform5 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
      ItemTransformVec3f transform6 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
      PackTweaksMod packTweaksMod;
      if (CheatBreaker.getInstance() != null
         && CheatBreaker.getInstance().getModManager() != null
         && CheatBreaker.getInstance().getModManager().packTweaksMod != null
         && this.name != null
         && (packTweaksMod = CheatBreaker.getInstance().getModManager().packTweaksMod).isEnabled()
         && packTweaksMod.checkString(this.name)) {
         float computeFloat = packTweaksMod.computeFloat(this.name);
         transform5 = new ItemTransformVec3f(new Vector3f(0.0F, 10.0F, 0.0F), new Vector3f(0.0F, 0.25F, 0.0F), new Vector3f(computeFloat, computeFloat, computeFloat));
      }

      return new ItemCameraTransforms(transform, transform2, transform3, transform4, transform5, transform6);
   }

   public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type) {
      return this.parent != null && !this.cameraTransforms.isTransformationDefined(type)
         ? this.parent.getTransform(type)
         : this.cameraTransforms.getTransform(type);
   }

   public static void checkModelHierarchy(Map<ResourceLocation, ModelBlock> map) {
      for (ModelBlock modelBlock : map.values()) {
         try {
            ModelBlock parent = modelBlock.parent;

            for (ModelBlock parent2 = parent.parent; parent != parent2; parent2 = parent2.parent.parent) {
               parent = parent.parent;
            }

            throw new ModelBlock.LoopException();
         } catch (NullPointerException e) {
         }
      }
   }

   static final class Bookkeep {
      public ModelBlock model;
      public ModelBlock modelExt;

      public Bookkeep(ModelBlock modelBlock) {
         this.model = modelBlock;
      }
   }

   public static class Deserializer implements JsonDeserializer<ModelBlock> {
      public ModelBlock deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         List modelElements = this.getModelElements(jsonDeserializationContext, asJsonObject);
         String parent = this.getParent(asJsonObject);
         boolean isEmpty = StringUtils.isEmpty(parent);
         boolean isEmpty2 = modelElements.isEmpty();
         if (isEmpty2 && isEmpty) {
            throw new JsonParseException("BlockModel requires either elements or parent, found neither");
         }

         if (!isEmpty && !isEmpty2) {
            throw new JsonParseException("BlockModel requires either elements or parent, found both");
         }

         Map textures = this.getTextures(asJsonObject);
         boolean ambientOcclusionEnabled = this.getAmbientOcclusionEnabled(asJsonObject);
         ItemCameraTransforms itemCameraTransforms = ItemCameraTransforms.DEFAULT;
         if (asJsonObject.has("display")) {
            JsonObject jsonObject = JsonUtils.getJsonObject(asJsonObject, "display");
            itemCameraTransforms = (ItemCameraTransforms)jsonDeserializationContext.deserialize(jsonObject, ItemCameraTransforms.class);
         }

         return isEmpty2 ? new ModelBlock(new ResourceLocation(parent), textures, ambientOcclusionEnabled, true, itemCameraTransforms) : new ModelBlock(modelElements, textures, ambientOcclusionEnabled, true, itemCameraTransforms);
      }

      public Map<String, String> getTextures(JsonObject jsonObject) {
         HashMap newHashMap = Maps.newHashMap();
         if (jsonObject.has("textures")) {
            JsonObject asJsonObject = jsonObject.getAsJsonObject("textures");

            for (Entry entry : asJsonObject.entrySet()) {
               newHashMap.put(entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
            }
         }

         return newHashMap;
      }

      public String getParent(JsonObject jsonObject) {
         return JsonUtils.getString(jsonObject, "parent", "");
      }

      public boolean getAmbientOcclusionEnabled(JsonObject jsonObject) {
         return JsonUtils.getBoolean(jsonObject, "ambientocclusion", true);
      }

      public List<BlockPart> getModelElements(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         ArrayList newArrayList = Lists.newArrayList();
         if (jsonObject.has("elements")) {
            for (JsonElement jsonElement : JsonUtils.getJsonArray(jsonObject, "elements")) {
               newArrayList.add((BlockPart)jsonDeserializationContext.deserialize(jsonElement, BlockPart.class));
            }
         }

         return newArrayList;
      }
   }

   public static class LoopException extends RuntimeException {
   }
}
