package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.GlStateManager;

public class ItemCameraTransforms {
   public static ItemCameraTransforms DEFAULT = new ItemCameraTransforms();
   public static float floatValue = 0.0F;
   public static float floatValue2 = 0.0F;
   public static float floatValue3 = 0.0F;
   public static float floatValue4 = 0.0F;
   public static float floatValue5 = 0.0F;
   public static float floatValue6 = 0.0F;
   public static float floatValue7 = 0.0F;
   public static float floatValue8 = 0.0F;
   public static float floatValue9 = 0.0F;
   public ItemTransformVec3f thirdPerson;
   public ItemTransformVec3f firstPerson;
   public ItemTransformVec3f head;
   public ItemTransformVec3f gui;
   public ItemTransformVec3f ground;
   public ItemTransformVec3f fixed;

   public ItemCameraTransforms() {
      this(
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT,
         ItemTransformVec3f.DEFAULT
      );
   }

   public ItemCameraTransforms(ItemCameraTransforms transforms) {
      this.thirdPerson = transforms.thirdPerson;
      this.firstPerson = transforms.firstPerson;
      this.head = transforms.head;
      this.gui = transforms.gui;
      this.ground = transforms.ground;
      this.fixed = transforms.fixed;
   }

   public ItemCameraTransforms(
      ItemTransformVec3f thirdPersonIn,
      ItemTransformVec3f firstPersonIn,
      ItemTransformVec3f headIn,
      ItemTransformVec3f guiIn,
      ItemTransformVec3f groundIn,
      ItemTransformVec3f fixedIn
   ) {
      this.thirdPerson = thirdPersonIn;
      this.firstPerson = firstPersonIn;
      this.head = headIn;
      this.gui = guiIn;
      this.ground = groundIn;
      this.fixed = fixedIn;
   }

   public void applyTransform(ItemCameraTransforms.TransformType type) {
      ItemTransformVec3f transform = this.getTransform(type);
      if (transform != ItemTransformVec3f.DEFAULT) {
         GlStateManager.translate(transform.translation.x + floatValue, transform.translation.y + floatValue2, transform.translation.z + floatValue3);
         GlStateManager.rotate(transform.rotation.y + floatValue5, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(transform.rotation.x + floatValue4, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(transform.rotation.z + floatValue6, 0.0F, 0.0F, 1.0F);
         GlStateManager.scale(transform.scale.x + floatValue7, transform.scale.y + floatValue8, transform.scale.z + floatValue9);
      }
   }

   public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type) {
      switch (type) {
         case THIRD_PERSON:
            return this.thirdPerson;
         case FIRST_PERSON:
            return this.firstPerson;
         case HEAD:
            return this.head;
         case GUI:
            return this.gui;
         case GROUND:
            return this.ground;
         case FIXED:
            return this.fixed;
         default:
            return ItemTransformVec3f.DEFAULT;
      }
   }

   public boolean isTransformationDefined(ItemCameraTransforms.TransformType type) {
      return !this.getTransform(type).equals(ItemTransformVec3f.DEFAULT);
   }

   static class Deserializer implements JsonDeserializer<ItemCameraTransforms> {
      public Deserializer() {
      }

      public ItemCameraTransforms deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         ItemTransformVec3f parseModelTransformation = this.parseModelTransformation(jsonDeserializationContext, asJsonObject, "thirdperson");
         ItemTransformVec3f parseModelTransformation2 = this.parseModelTransformation(jsonDeserializationContext, asJsonObject, "firstperson");
         ItemTransformVec3f parseModelTransformation3 = this.parseModelTransformation(jsonDeserializationContext, asJsonObject, "head");
         ItemTransformVec3f parseModelTransformation4 = this.parseModelTransformation(jsonDeserializationContext, asJsonObject, "gui");
         ItemTransformVec3f parseModelTransformation5 = this.parseModelTransformation(jsonDeserializationContext, asJsonObject, "ground");
         ItemTransformVec3f parseModelTransformation6 = this.parseModelTransformation(jsonDeserializationContext, asJsonObject, "fixed");
         return new ItemCameraTransforms(parseModelTransformation, parseModelTransformation2, parseModelTransformation3, parseModelTransformation4, parseModelTransformation5, parseModelTransformation6);
      }

      public ItemTransformVec3f parseModelTransformation(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject, String str) {
         return jsonObject.has(str) ? (ItemTransformVec3f)jsonDeserializationContext.deserialize(jsonObject.get(str), ItemTransformVec3f.class) : ItemTransformVec3f.DEFAULT;
      }
   }

   public enum TransformType {
      NONE,
      THIRD_PERSON,
      FIRST_PERSON,
      HEAD,
      GUI,
      GROUND,
      FIXED;
   }
}
