package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractBackgroundHudMod;
import com.cheatbreaker.client.mod.setting.FormatSetting;
import com.cheatbreaker.client.mod.setting.Setting;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateMod extends AbstractBackgroundHudMod {
   public SimpleDateFormat simpleDateFormat;
   public Setting dateFormat;

   public void run() {
      try {
         this.simpleDateFormat = new SimpleDateFormat(this.dateFormat.getStringValue());
      } catch (IllegalArgumentException e) {
         this.dateFormat.changeValue("MM/dd/yy");
      }
   }

   @Override
   public String getDynamicText() {
      return this.simpleDateFormat.format(new Date());
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.run();
   }

   public DateMod() {
      super("Date");
      this.setDescription("Displays the current date.");
      this.handleString("[9/6/2018]");
   }

   @Override
   public void createFormatOptions() {
      this.dateFormat = new FormatSetting(this, "Date Format").changeValue("MM/dd/yy").setOnUpdate(obj -> this.run()).withHierarchyParents(this.formatOptions);
   }

   @Override
   public String getLabelText() {
      return "Date";
   }
}
