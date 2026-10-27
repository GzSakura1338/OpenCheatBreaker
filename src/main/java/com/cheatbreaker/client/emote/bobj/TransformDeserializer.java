package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.Transform;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;

public class TransformDeserializer implements JsonDeserializer<Transform> {
   public Transform deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      if (!jsonElement.isJsonObject()) {
         return null;
      }

      JsonObject jsonObject = (JsonObject)jsonElement;
      Transform transform = new Transform("");
      if (jsonObject.has("x")) {
         transform.x = jsonObject.get("x").getAsFloat();
      }

      if (jsonObject.has("y")) {
         transform.y = jsonObject.get("y").getAsFloat();
      }

      if (jsonObject.has("z")) {
         transform.z = jsonObject.get("z").getAsFloat();
      }

      if (jsonObject.has("sx")) {
         transform.scaleX = jsonObject.get("sx").getAsFloat();
      }

      if (jsonObject.has("sy")) {
         transform.scaleY = jsonObject.get("sy").getAsFloat();
      }

      if (jsonObject.has("sz")) {
         transform.scaleZ = jsonObject.get("sz").getAsFloat();
      }

      if (jsonObject.has("rx")) {
         transform.rotationX = jsonObject.get("rx").getAsFloat();
      }

      if (jsonObject.has("ry")) {
         transform.rotationY = jsonObject.get("ry").getAsFloat();
      }

      if (jsonObject.has("rz")) {
         transform.rotationZ = jsonObject.get("rz").getAsFloat();
      }

      return transform;
   }
}
