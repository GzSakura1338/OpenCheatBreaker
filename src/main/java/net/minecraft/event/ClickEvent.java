package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public class ClickEvent {
   public ClickEvent.Action action;
   public String value;

   public ClickEvent(ClickEvent.Action theAction, String theValue) {
      this.action = theAction;
      this.value = theValue;
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
         }

         if (this.value != null) {
            if (!this.value.equals(clickEvent.value)) {
               return false;
            }
         } else if (clickEvent.value != null) {
            return false;
         }

         return true;
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
      OPEN_URL("open_url", true),
      OPEN_FILE("open_file", false),
      RUN_COMMAND("run_command", true),
      TWITCH_USER_INFO("twitch_user_info", false),
      SUGGEST_COMMAND("suggest_command", true),
      CHANGE_PAGE("change_page", true),
      UPLOAD_SCREENSHOT("upload_screenshot", false),
      COPY_SCREENSHOT("copy_screenshot", false);

      public String canonicalName;
      public boolean allowedInChat;
      public static Map<String, ClickEvent.Action> nameMapping = Maps.newHashMap();

      Action(String canonicalNameIn, boolean allowedInChatIn) {
         this.canonicalName = canonicalNameIn;
         this.allowedInChat = allowedInChatIn;
      }

      public boolean shouldAllowInChat() {
         return this.allowedInChat;
      }

      public String getCanonicalName() {
         return this.canonicalName;
      }

      public static ClickEvent.Action getValueByCanonicalName(String canonicalNameIn) {
         return nameMapping.get(canonicalNameIn);
      }

      static {
         for (ClickEvent.Action action : values()) {
            nameMapping.put(action.getCanonicalName(), action);
         }
      }
   }
}
