package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class ItemTransformVec3f {
   public static ItemTransformVec3f DEFAULT = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
   public Vector3f rotation;
   public Vector3f translation;
   public Vector3f scale;

   public ItemTransformVec3f(Vector3f rotation, Vector3f translation, Vector3f scale) {
      this.rotation = new Vector3f(rotation);
      this.translation = new Vector3f(translation);
      this.scale = new Vector3f(scale);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (this.getClass() != obj.getClass()) {
         return false;
      }

      ItemTransformVec3f itemTransformVec3f = (ItemTransformVec3f)obj;
      return !this.rotation.equals(itemTransformVec3f.rotation) ? false : (!this.scale.equals(itemTransformVec3f.scale) ? false : this.translation.equals(itemTransformVec3f.translation));
   }

   @Override
   public int hashCode() {
      int n = this.rotation.hashCode();
      n = 31 * n + this.translation.hashCode();
      return 31 * n + this.scale.hashCode();
   }

   static class Deserializer implements JsonDeserializer<ItemTransformVec3f> {
      public static Vector3f ROTATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
      public static Vector3f TRANSLATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
      public static Vector3f SCALE_DEFAULT = new Vector3f(1.0F, 1.0F, 1.0F);

      public Deserializer() {
      }

      public ItemTransformVec3f deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         Vector3f parseVector3f = this.parseVector3f(asJsonObject, "rotation", ROTATION_DEFAULT);
         Vector3f parseVector3f2 = this.parseVector3f(asJsonObject, "translation", TRANSLATION_DEFAULT);
         parseVector3f2.scale(0.0625F);
         parseVector3f2.x = MathHelper.clamp_float(parseVector3f2.x, -1.5F, 1.5F);
         parseVector3f2.y = MathHelper.clamp_float(parseVector3f2.y, -1.5F, 1.5F);
         parseVector3f2.z = MathHelper.clamp_float(parseVector3f2.z, -1.5F, 1.5F);
         Vector3f parseVector3f3 = this.parseVector3f(asJsonObject, "scale", SCALE_DEFAULT);
         parseVector3f3.x = MathHelper.clamp_float(parseVector3f3.x, -4.0F, 4.0F);
         parseVector3f3.y = MathHelper.clamp_float(parseVector3f3.y, -4.0F, 4.0F);
         parseVector3f3.z = MathHelper.clamp_float(parseVector3f3.z, -4.0F, 4.0F);
         return new ItemTransformVec3f(parseVector3f, parseVector3f2, parseVector3f3);
      }

      public Vector3f parseVector3f(JsonObject jsonObject, String key, Vector3f defaultValue) {
         if (!jsonObject.has(key)) {
            return defaultValue;
         }

         JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, key);
         if (jsonArray.size() != 3) {
            throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonArray.size());
         }

         float[] floats = new float[3];

         for (int i = 0; i < floats.length; i++) {
            floats[i] = JsonUtils.getFloat(jsonArray.get(i), key + "[" + i + "]");
         }

         return new Vector3f(floats[0], floats[1], floats[2]);
      }
   }
}
