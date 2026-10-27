package net.minecraft.scoreboard;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;

public abstract class Team {
   public boolean isSameTeam(Team other) {
      return other == null ? false : this == other;
   }

   public abstract String getRegisteredName();

   public abstract String formatString(String str);

   public abstract boolean getSeeFriendlyInvisiblesEnabled();

   public abstract boolean getAllowFriendlyFire();

   public abstract Team.EnumVisible getNameTagVisibility();

   public abstract Collection<String> getMembershipCollection();

   public abstract Team.EnumVisible getDeathMessageVisibility();

   public enum EnumVisible {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      public String internalName;
      public int id;
      public static Map<String, Team.EnumVisible> RULES = Maps.newHashMap();

      public static String[] getValuesAsArray() {
         return RULES.keySet().toArray(new String[RULES.size()]);
      }

      public static Team.EnumVisible getRuleByName(String str) {
         return RULES.get(str);
      }

      EnumVisible(String str, int n) {
         this.internalName = str;
         this.id = n;
      }

      static {
         for (Team.EnumVisible enumVisible : values()) {
            RULES.put(enumVisible.internalName, enumVisible);
         }
      }
   }
}
