package com.cheatbreaker.client.mod.setting;

import com.cheatbreaker.client.mod.AbstractMod;
import java.util.List;

public class CrosshairSetting extends Setting {
   public CrosshairSetting(AbstractMod abstractMod, String str) {
      super(abstractMod, str);
   }

   public CrosshairSetting(List<Setting> list, String str) {
      super(list, str);
   }
}
