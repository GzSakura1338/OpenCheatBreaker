package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.AnimationAction;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;

public class AnimationActionDeserializer implements JsonDeserializer<AnimationAction> {
    public AnimationAction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        AnimationAction animationAction = new AnimationAction();
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = (JsonObject)jsonElement;
            if (jsonObject.has("name")) {
                animationAction.name = jsonObject.get("name").getAsString();
            }

            if (jsonObject.has("clamp")) {
                animationAction.clamp = jsonObject.get("clamp").getAsBoolean();
            }

            if (jsonObject.has("reset")) {
                animationAction.reset = jsonObject.get("reset").getAsBoolean();
            }

            if (jsonObject.has("speed")) {
                animationAction.speed = jsonObject.get("speed").getAsFloat();
            }

            if (jsonObject.has("fade")) {
                animationAction.fade = jsonObject.get("fade").getAsInt();
            }

            if (jsonObject.has("tick")) {
                animationAction.tick = jsonObject.get("tick").getAsInt();
            }
        } else if (jsonElement.isJsonPrimitive()) {
            animationAction.name = jsonElement.getAsString();
        }

        return animationAction;
    }
}
