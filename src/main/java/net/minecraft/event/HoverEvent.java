package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.IChatComponent;

public class HoverEvent {
    public HoverEvent.Action action;
    public IChatComponent value;

    public HoverEvent(HoverEvent.Action action, IChatComponent chatComponent) {
        this.action = action;
        this.value = chatComponent;
    }

    public HoverEvent.Action getAction() {
        return this.action;
    }

    public IChatComponent getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj != null && this.getClass() == obj.getClass()) {
            HoverEvent hoverEvent = (HoverEvent)obj;
            if (this.action != hoverEvent.action) {
                return false;
            } else {
                return this.value != null ? this.value.equals(hoverEvent.value) : hoverEvent.value == null;
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }

    @Override
    public int hashCode() {
        int n = this.action.hashCode();
        return 31 * n + (this.value != null ? this.value.hashCode() : 0);
    }

    public enum Action {
        SHOW_TEXT("SHOW_TEXT", 0, "show_text", true),
        SHOW_ACHIEVEMENT("SHOW_ACHIEVEMENT", 1, "show_achievement", true),
        SHOW_ITEM("SHOW_ITEM", 2, "show_item", true);

        public String canonicalName;
        public boolean allowedInChat;
        public static Map nameMapping = Maps.newHashMap();
        public static HoverEvent.Action[] actionArray = new HoverEvent.Action[]{SHOW_TEXT, SHOW_ACHIEVEMENT, SHOW_ITEM};

        public boolean shouldAllowInChat() {
            return this.allowedInChat;
        }

        public String getCanonicalName() {
            return this.canonicalName;
        }

        public static HoverEvent.Action getValueByCanonicalName(String str) {
            return (HoverEvent.Action)nameMapping.get(str);
        }

        static {
            for (HoverEvent.Action action : values()) {
                nameMapping.put(action.getCanonicalName(), action);
            }
        }

        Action(String str, int n, String str2, boolean flag) {
            this.canonicalName = str2;
            this.allowedInChat = flag;
        }
    }
}
