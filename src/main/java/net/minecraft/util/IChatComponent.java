package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;

public interface IChatComponent extends Iterable {
    IChatComponent setChatStyle(ChatStyle chatStyle);

    ChatStyle getChatStyle();

    IChatComponent appendText(String str);

    IChatComponent appendSibling(IChatComponent chatComponent);

    String getUnformattedTextForChat();

    String getUnformattedText();

    String getFormattedText();

    List getSiblings();

    IChatComponent createCopy();

    class Serializer implements JsonDeserializer, JsonSerializer {
        public static Gson GSON;

        public IChatComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            if (jsonElement.isJsonPrimitive()) {
                return new ChatComponentText(jsonElement.getAsString());
            }

            if (!jsonElement.isJsonObject()) {
                if (jsonElement.isJsonArray()) {
                    JsonArray asJsonArray = jsonElement.getAsJsonArray();
                    IChatComponent chatComponent = null;

                    for (JsonElement jsonElement2 : asJsonArray) {
                        IChatComponent deserialize = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                        if (chatComponent == null) {
                            chatComponent = deserialize;
                        } else {
                            chatComponent.appendSibling(deserialize);
                        }
                    }

                    return chatComponent;
                } else {
                    throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                }
            } else {
                JsonObject asJsonObject = jsonElement.getAsJsonObject();
                ChatComponentStyle chatComponentStyle;
                if (asJsonObject.has("text")) {
                    chatComponentStyle = new ChatComponentText(asJsonObject.get("text").getAsString());
                } else {
                    if (!asJsonObject.has("translate")) {
                        throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                    }

                    String asString = asJsonObject.get("translate").getAsString();
                    if (asJsonObject.has("with")) {
                        JsonArray asJsonArray = asJsonObject.getAsJsonArray("with");
                        Object[] objs = new Object[asJsonArray.size()];

                        for (int i = 0; i < objs.length; i++) {
                            objs[i] = this.deserialize(asJsonArray.get(i), type, jsonDeserializationContext);
                            if (objs[i] instanceof ChatComponentText) {
                                ChatComponentText chatComponentText = (ChatComponentText)objs[i];
                                if (chatComponentText.getChatStyle().isEmpty() && chatComponentText.getSiblings().isEmpty()) {
                                    objs[i] = chatComponentText.getChatComponentText_TextValue();
                                }
                            }
                        }

                        chatComponentStyle = new ChatComponentTranslation(asString, objs);
                    } else {
                        chatComponentStyle = new ChatComponentTranslation(asString);
                    }
                }

                if (asJsonObject.has("extra")) {
                    JsonArray asJsonArray = asJsonObject.getAsJsonArray("extra");
                    if (asJsonArray.size() <= 0) {
                        throw new JsonParseException("Unexpected empty array of components");
                    }

                    for (int i = 0; i < asJsonArray.size(); i++) {
                        chatComponentStyle.appendSibling(this.deserialize(asJsonArray.get(i), type, jsonDeserializationContext));
                    }
                }

                chatComponentStyle.setChatStyle((ChatStyle)jsonDeserializationContext.deserialize(jsonElement, ChatStyle.class));
                return chatComponentStyle;
            }
        }

        public void serializeChatStyle(ChatStyle style, JsonObject object, JsonSerializationContext ctx) {
            JsonElement serialize = ctx.serialize(style);
            if (serialize.isJsonObject()) {
                JsonObject jsonObject = (JsonObject)serialize;

                for (Entry entry : jsonObject.entrySet()) {
                    object.add((String)entry.getKey(), (JsonElement)entry.getValue());
                }
            }
        }

        public JsonElement serialize(IChatComponent chatComponent, Type type, JsonSerializationContext jsonSerializationContext) {
            if (chatComponent instanceof ChatComponentText && chatComponent.getChatStyle().isEmpty() && chatComponent.getSiblings().isEmpty()) {
                return new JsonPrimitive(((ChatComponentText)chatComponent).getChatComponentText_TextValue());
            }

            JsonObject jsonObject = new JsonObject();
            if (!chatComponent.getChatStyle().isEmpty()) {
                this.serializeChatStyle(chatComponent.getChatStyle(), jsonObject, jsonSerializationContext);
            }

            if (!chatComponent.getSiblings().isEmpty()) {
                JsonArray jsonArray = new JsonArray();

                for (IChatComponent chatComponent2 : (Iterable<IChatComponent>)chatComponent.getSiblings()) {
                    jsonArray.add(this.serialize(chatComponent2, chatComponent2.getClass(), jsonSerializationContext));
                }

                jsonObject.add("extra", jsonArray);
            }

            if (chatComponent instanceof ChatComponentText) {
                jsonObject.addProperty("text", ((ChatComponentText)chatComponent).getChatComponentText_TextValue());
            } else {
                if (!(chatComponent instanceof ChatComponentTranslation)) {
                    throw new IllegalArgumentException("Don't know how to serialize " + chatComponent + " as a Component");
                }

                ChatComponentTranslation chatComponentTranslation = (ChatComponentTranslation)chatComponent;
                jsonObject.addProperty("translate", chatComponentTranslation.getKey());
                if (chatComponentTranslation.getFormatArgs() != null && chatComponentTranslation.getFormatArgs().length > 0) {
                    JsonArray jsonArray = new JsonArray();

                    for (Object obj : chatComponentTranslation.getFormatArgs()) {
                        if (obj instanceof IChatComponent) {
                            jsonArray.add(this.serialize((IChatComponent)obj, obj.getClass(), jsonSerializationContext));
                        } else {
                            jsonArray.add(new JsonPrimitive(String.valueOf(obj)));
                        }
                    }

                    jsonObject.add("with", jsonArray);
                }
            }

            return jsonObject;
        }

        public static String componentToJson(IChatComponent component) {
            return GSON.toJson(component);
        }

        public static IChatComponent jsonToComponent(String json) {
            return (IChatComponent)GSON.fromJson(json, IChatComponent.class);
        }

        public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((IChatComponent)obj, type, jsonSerializationContext);
        }

        static {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
            gsonBuilder.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
            gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
            GSON = gsonBuilder.create();
        }
    }
}
