package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.AnimationAction;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.Map.Entry;

public class ActionMapDeserializer implements JsonDeserializer<ActionMap> {
   public ActionMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      if (!jsonElement.isJsonObject()) {
         return null;
      }

      JsonObject asJsonObject = jsonElement.getAsJsonObject();
      ActionMap actionMap = new ActionMap();

      for (Entry entry : asJsonObject.entrySet()) {
         JsonElement value = (JsonElement)entry.getValue();
         String toSnakeCase = actionMap.toSnakeCase((String)entry.getKey());
         if (value.isJsonObject()) {
            ((JsonObject)value).addProperty("name", toSnakeCase);
         }

         actionMap.actions.put(toSnakeCase, (AnimationAction)jsonDeserializationContext.deserialize(value, AnimationAction.class));
      }

      return actionMap;
   }
}
