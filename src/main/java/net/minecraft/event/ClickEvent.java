package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public class ClickEvent {
    public ClickEvent.Action action;
    public String value;

    public ClickEvent(ClickEvent.Action action, String str) {
        this.action = action;
        this.value = str;
    }

    public ClickEvent.Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj != null && this.getClass() == obj.getClass()) {
            ClickEvent clickEvent = (ClickEvent)obj;
            if (this.action != clickEvent.action) {
                return false;
            } else {
                return this.value != null ? this.value.equals(clickEvent.value) : clickEvent.value == null;
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }

    @Override
    public int hashCode() {
        int n = this.action.hashCode();
        return 31 * n + (this.value != null ? this.value.hashCode() : 0);
    }

    public enum Action {
        OPEN_URL("OPEN_URL", 0, "open_url", true),
        OPEN_FILE("OPEN_FILE", 1, "open_file", false),
        RUN_COMMAND("RUN_COMMAND", 2, "run_command", true),
        TWITCH_USER_INFO("TWITCH_USER_INFO", 3, "twitch_user_info", false),
        SUGGEST_COMMAND("SUGGEST_COMMAND", 4, "suggest_command", true),
        UPLOAD_SCREENSHOT("UPLOAD_SCREENSHOT", 5, "upload_screenshot", false),
        COPY_SCREENSHOT("UPLOAD_SCREENSHOT", 6, "copy_screenshot", false);

        public String canonicalName;
        public boolean allowedInChat;
        public static Map nameMapping = Maps.newHashMap();
        public static ClickEvent.Action[] actionArray = new ClickEvent.Action[]{OPEN_URL, OPEN_FILE, RUN_COMMAND, TWITCH_USER_INFO, SUGGEST_COMMAND};

        public boolean shouldAllowInChat() {
            return this.allowedInChat;
        }

        public String getCanonicalName() {
            return this.canonicalName;
        }

        public static ClickEvent.Action getValueByCanonicalName(String str) {
            return (ClickEvent.Action)nameMapping.get(str);
        }

        static {
            for (ClickEvent.Action action : values()) {
                nameMapping.put(action.getCanonicalName(), action);
            }
        }

        Action(String str, int n, String str2, boolean flag) {
            this.canonicalName = str2;
            this.allowedInChat = flag;
        }
    }
}
