package com.cheatbreaker.client.mod.setting;

import com.cheatbreaker.client.mod.AbstractMod;
import java.util.List;

public class ToggleTextSetting extends Setting {
   public String offText2 = "OFF";
   public String onText2 = "ON";

   public void setOffText2(String str) {
      this.offText2 = str;
   }

   public String getOffText2() {
      return this.offText2;
   }

   public String getOnText2() {
      return this.onText2;
   }

   @Override
   public Setting setToggleTexts(String str, String str2) {
      this.offText2 = str;
      this.onText2 = str2;
      return this;
   }

   public boolean isEnabled() {
      return (Boolean)this.value;
   }

   public ToggleTextSetting(AbstractMod abstractMod, String str) {
      super(abstractMod, str);
   }

   public ToggleTextSetting(List<Setting> list, String str) {
      super(list, str);
   }

   public void setOnText2(String str) {
      this.onText2 = str;
   }
}
