package com.cheatbreaker.client.mod.setting;

import com.cheatbreaker.client.mod.AbstractMod;
import java.util.List;

public class SettingGroup extends Setting {
   public SettingGroup(List<Setting> list, String str) {
      super(list, str);
   }

   public SettingGroup(AbstractMod abstractMod, String str) {
      super(abstractMod, str);
   }
}
