package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.ClickEvent.Action;

public class ChatStyle {
    public ChatStyle parentStyle;
    public EnumChatFormatting color;
    public Boolean bold;
    public Boolean italic;
    public Boolean underlined;
    public Boolean strikethrough;
    public Boolean obfuscated;
    public ClickEvent chatClickEvent;
    public HoverEvent chatHoverEvent;
    public static ChatStyle rootStyle = new ChatStyle() {
        @Override
        public EnumChatFormatting getColor() {
            return null;
        }

        @Override
        public boolean getBold() {
            return false;
        }

        @Override
        public boolean getItalic() {
            return false;
        }

        @Override
        public boolean getStrikethrough() {
            return false;
        }

        @Override
        public boolean getUnderlined() {
            return false;
        }

        @Override
        public boolean getObfuscated() {
            return false;
        }

        @Override
        public ClickEvent getChatClickEvent() {
            return null;
        }

        @Override
        public HoverEvent getChatHoverEvent() {
            return null;
        }

        @Override
        public ChatStyle setColor(EnumChatFormatting colorIn) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChatStyle setBold(Boolean bool) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChatStyle setItalic(Boolean bool) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChatStyle setStrikethrough(Boolean bool) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChatStyle setUnderlined(Boolean bool) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChatStyle setObfuscated(Boolean bool) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChatStyle setChatClickEvent(ClickEvent clickEvent) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChatStyle setChatHoverEvent(HoverEvent hoverEvent) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChatStyle setParentStyle(ChatStyle chatStyle) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "Style.ROOT";
        }

        @Override
        public ChatStyle createShallowCopy() {
            return this;
        }

        @Override
        public ChatStyle createDeepCopy() {
            return this;
        }

        @Override
        public String getFormattingCode() {
            return "";
        }
    };

    public EnumChatFormatting getColor() {
        return this.color == null ? this.getParent().getColor() : this.color;
    }

    public boolean getBold() {
        return this.bold == null ? this.getParent().getBold() : this.bold;
    }

    public boolean getItalic() {
        return this.italic == null ? this.getParent().getItalic() : this.italic;
    }

    public boolean getStrikethrough() {
        return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough;
    }

    public boolean getUnderlined() {
        return this.underlined == null ? this.getParent().getUnderlined() : this.underlined;
    }

    public boolean getObfuscated() {
        return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated;
    }

    public boolean isEmpty() {
        return this.bold == null
            && this.italic == null
            && this.strikethrough == null
            && this.underlined == null
            && this.obfuscated == null
            && this.color == null
            && this.chatClickEvent == null
            && this.chatHoverEvent == null;
    }

    public ClickEvent getChatClickEvent() {
        return this.chatClickEvent == null ? this.getParent().getChatClickEvent() : this.chatClickEvent;
    }

    public HoverEvent getChatHoverEvent() {
        return this.chatHoverEvent == null ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
    }

    public ChatStyle setColor(EnumChatFormatting colorIn) {
        this.color = colorIn;
        return this;
    }

    public ChatStyle setBold(Boolean bool) {
        this.bold = bool;
        return this;
    }

    public ChatStyle setItalic(Boolean bool) {
        this.italic = bool;
        return this;
    }

    public ChatStyle setStrikethrough(Boolean bool) {
        this.strikethrough = bool;
        return this;
    }

    public ChatStyle setUnderlined(Boolean bool) {
        this.underlined = bool;
        return this;
    }

    public ChatStyle setObfuscated(Boolean bool) {
        this.obfuscated = bool;
        return this;
    }

    public ChatStyle setChatClickEvent(ClickEvent clickEvent) {
        this.chatClickEvent = clickEvent;
        return this;
    }

    public ChatStyle setChatHoverEvent(HoverEvent hoverEvent) {
        this.chatHoverEvent = hoverEvent;
        return this;
    }

    public ChatStyle setParentStyle(ChatStyle chatStyle) {
        this.parentStyle = chatStyle;
        return this;
    }

    public String getFormattingCode() {
        if (this.isEmpty()) {
            return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
        }

        StringBuilder sb = new StringBuilder();
        if (this.getColor() != null) {
            sb.append(this.getColor());
        }

        if (this.getBold()) {
            sb.append(EnumChatFormatting.BOLD);
        }

        if (this.getItalic()) {
            sb.append(EnumChatFormatting.ITALIC);
        }

        if (this.getUnderlined()) {
            sb.append(EnumChatFormatting.UNDERLINE);
        }

        if (this.getObfuscated()) {
            sb.append(EnumChatFormatting.OBFUSCATED);
        }

        if (this.getStrikethrough()) {
            sb.append(EnumChatFormatting.STRIKETHROUGH);
        }

        return sb.toString();
    }

    public ChatStyle getParent() {
        return this.parentStyle == null ? rootStyle : this.parentStyle;
    }

    @Override
    public String toString() {
        return "Style{hasParent="
            + (this.parentStyle != null)
            + ", color="
            + this.color
            + ", bold="
            + this.bold
            + ", italic="
            + this.italic
            + ", underlined="
            + this.underlined
            + ", obfuscated="
            + this.obfuscated
            + ", clickEvent="
            + this.getChatClickEvent()
            + ", hoverEvent="
            + this.getChatHoverEvent()
            + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ChatStyle)) {
            return false;
        }

        ChatStyle chatStyle = (ChatStyle)obj;
        return this.getBold() == chatStyle.getBold()
            && this.getColor() == chatStyle.getColor()
            && this.getItalic() == chatStyle.getItalic()
            && this.getObfuscated() == chatStyle.getObfuscated()
            && this.getStrikethrough() == chatStyle.getStrikethrough()
            && this.getUnderlined() == chatStyle.getUnderlined()
            && (this.getChatClickEvent() != null ? this.getChatClickEvent().equals(chatStyle.getChatClickEvent()) : chatStyle.getChatClickEvent() == null)
            && (this.getChatHoverEvent() != null ? this.getChatHoverEvent().equals(chatStyle.getChatHoverEvent()) : chatStyle.getChatHoverEvent() == null);
    }

    @Override
    public int hashCode() {
        int n = this.color.hashCode();
        n = 31 * n + this.bold.hashCode();
        n = 31 * n + this.italic.hashCode();
        n = 31 * n + this.underlined.hashCode();
        n = 31 * n + this.strikethrough.hashCode();
        n = 31 * n + this.obfuscated.hashCode();
        n = 31 * n + this.chatClickEvent.hashCode();
        return 31 * n + this.chatHoverEvent.hashCode();
    }

    public ChatStyle createShallowCopy() {
        ChatStyle chatStyle = new ChatStyle();
        chatStyle.bold = this.bold;
        chatStyle.italic = this.italic;
        chatStyle.strikethrough = this.strikethrough;
        chatStyle.underlined = this.underlined;
        chatStyle.obfuscated = this.obfuscated;
        chatStyle.color = this.color;
        chatStyle.chatClickEvent = this.chatClickEvent;
        chatStyle.chatHoverEvent = this.chatHoverEvent;
        chatStyle.parentStyle = this.parentStyle;
        return chatStyle;
    }

    public ChatStyle createDeepCopy() {
        ChatStyle chatStyle = new ChatStyle();
        chatStyle.setBold(this.getBold());
        chatStyle.setItalic(this.getItalic());
        chatStyle.setStrikethrough(this.getStrikethrough());
        chatStyle.setUnderlined(this.getUnderlined());
        chatStyle.setObfuscated(this.getObfuscated());
        chatStyle.setColor(this.getColor());
        chatStyle.setChatClickEvent(this.getChatClickEvent());
        chatStyle.setChatHoverEvent(this.getChatHoverEvent());
        return chatStyle;
    }

    public static class Serializer implements JsonDeserializer, JsonSerializer {
        public ChatStyle deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            if (jsonElement.isJsonObject()) {
                ChatStyle chatStyle = new ChatStyle();
                JsonObject asJsonObject = jsonElement.getAsJsonObject();
                if (asJsonObject == null) {
                    return null;
                }

                if (asJsonObject.has("bold")) {
                    chatStyle.bold = asJsonObject.get("bold").getAsBoolean();
                }

                if (asJsonObject.has("italic")) {
                    chatStyle.italic = asJsonObject.get("italic").getAsBoolean();
                }

                if (asJsonObject.has("underlined")) {
                    chatStyle.underlined = asJsonObject.get("underlined").getAsBoolean();
                }

                if (asJsonObject.has("strikethrough")) {
                    chatStyle.strikethrough = asJsonObject.get("strikethrough").getAsBoolean();
                }

                if (asJsonObject.has("obfuscated")) {
                    chatStyle.obfuscated = asJsonObject.get("obfuscated").getAsBoolean();
                }

                if (asJsonObject.has("color")) {
                    chatStyle.color = (EnumChatFormatting)jsonDeserializationContext.deserialize(asJsonObject.get("color"), EnumChatFormatting.class);
                }

                if (asJsonObject.has("clickEvent")) {
                    JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("clickEvent");
                    if (asJsonObject2 != null) {
                        JsonPrimitive asJsonPrimitive = asJsonObject2.getAsJsonPrimitive("action");
                        Action action = asJsonPrimitive == null ? null : Action.getValueByCanonicalName(asJsonPrimitive.getAsString());
                        JsonPrimitive asJsonPrimitive2 = asJsonObject2.getAsJsonPrimitive("value");
                        String str = asJsonPrimitive2 == null ? null : asJsonPrimitive2.getAsString();
                        if (action != null && str != null && action.shouldAllowInChat()) {
                            chatStyle.chatClickEvent = new ClickEvent(action, str);
                        }
                    }
                }

                if (asJsonObject.has("hoverEvent")) {
                    JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("hoverEvent");
                    if (asJsonObject2 != null) {
                        JsonPrimitive asJsonPrimitive = asJsonObject2.getAsJsonPrimitive("action");
                        net.minecraft.event.HoverEvent.Action action = asJsonPrimitive == null
                            ? null
                            : net.minecraft.event.HoverEvent.Action.getValueByCanonicalName(asJsonPrimitive.getAsString());
                        IChatComponent deserialize = (IChatComponent)jsonDeserializationContext.deserialize(asJsonObject2.get("value"), IChatComponent.class);
                        if (action != null && deserialize != null && action.shouldAllowInChat()) {
                            chatStyle.chatHoverEvent = new HoverEvent(action, deserialize);
                        }
                    }
                }

                return chatStyle;
            } else {
                return null;
            }
        }

        public JsonElement serialize(ChatStyle chatStyle, Type type, JsonSerializationContext jsonSerializationContext) {
            if (chatStyle.isEmpty()) {
                return null;
            }

            JsonObject jsonObject = new JsonObject();
            if (chatStyle.bold != null) {
                jsonObject.addProperty("bold", chatStyle.bold);
            }

            if (chatStyle.italic != null) {
                jsonObject.addProperty("italic", chatStyle.italic);
            }

            if (chatStyle.underlined != null) {
                jsonObject.addProperty("underlined", chatStyle.underlined);
            }

            if (chatStyle.strikethrough != null) {
                jsonObject.addProperty("strikethrough", chatStyle.strikethrough);
            }

            if (chatStyle.obfuscated != null) {
                jsonObject.addProperty("obfuscated", chatStyle.obfuscated);
            }

            if (chatStyle.color != null) {
                jsonObject.add("color", jsonSerializationContext.serialize(chatStyle.color));
            }

            if (chatStyle.chatClickEvent != null) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("action", chatStyle.chatClickEvent.getAction().getCanonicalName());
                jsonObject2.addProperty("value", chatStyle.chatClickEvent.getValue());
                jsonObject.add("clickEvent", jsonObject2);
            }

            if (chatStyle.chatHoverEvent != null) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("action", chatStyle.chatHoverEvent.getAction().getCanonicalName());
                jsonObject2.add("value", jsonSerializationContext.serialize(chatStyle.chatHoverEvent.getValue()));
                jsonObject.add("hoverEvent", jsonObject2);
            }

            return jsonObject;
        }

        public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((ChatStyle)obj, type, jsonSerializationContext);
        }
    }
}
