package com.cheatbreaker.client.mod.setting;

import com.cheatbreaker.client.mod.AbstractMod;
import java.util.List;

public class FormatSetting extends Setting {
   public FormatSetting(AbstractMod abstractMod, String str) {
      super(abstractMod, str);
   }

   public FormatSetting(List<Setting> list, String str) {
      super(list, str);
   }
}
