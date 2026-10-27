package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractBackgroundHudMod;
import com.cheatbreaker.client.mod.setting.Setting;

public class PlaytimeMod extends AbstractBackgroundHudMod {
   public Setting displayType;

   @Override
   public void createFormatOptions() {
      this.displayType = new Setting(this, "Display Type")
         .withHierarchyParents(this.formatOptions)
         .changeValue("Default")
         .setStringValues(
            "Default",
            "Compact 1",
            "Compact 2",
            "Compact 3",
            "Spread 1",
            "Spread 2",
            "Spread 3",
            "Spread 4",
            "Easy 1",
            "Easy 2",
            "Easy 3",
            "Stopwatch",
            "Milliseconds"
         );
   }

   @Override
   public String getLabelText() {
      return "Playtime";
   }

   public PlaytimeMod() {
      super("Playtime");
      this.handleString("[12:34:56.789]");
   }

   public String formatText(PlaytimeDisplayType playtimeDisplayType, long l) {
      if (playtimeDisplayType.flag) {
         long l2 = l / 3600000L % 24L;
         long l3 = l / 60000L % 60L;
         if (playtimeDisplayType.flag2) {
            long l4 = l / 1000L % 60L;
            return playtimeDisplayType.flag3 ? String.format(playtimeDisplayType.text2, l2, l3, l4, l % 1000L) : String.format(playtimeDisplayType.text2, l2, l3, l4);
         } else {
            return String.format(playtimeDisplayType.text2, l2, l3);
         }
      } else if (playtimeDisplayType.flag2) {
         long l2 = l / 1000L % 60L;
         return playtimeDisplayType.flag3 ? String.format(playtimeDisplayType.text2, l2, l % 1000L) : String.format(playtimeDisplayType.text2, l2);
      } else {
         return String.format(playtimeDisplayType.text2, l);
      }
   }

   @Override
   public String getDynamicText() {
      return this.formatText(
         PlaytimeDisplayType.valueOf(this.displayType.getStringValue().replaceAll(" ", "_").toUpperCase()),
         System.currentTimeMillis() - CheatBreaker.getInstance().getStartTime()
      );
   }
}
