package com.cheatbreaker.client.mod.setting;

import com.cheatbreaker.client.mod.AbstractMod;
import java.util.List;

public class ButtonSetting extends Setting {
    public ButtonSetting(AbstractMod abstractMod, String str) {
        super(abstractMod, str);
    }

    public ButtonSetting(List<Setting> list, String str) {
        super(list, str);
    }
}
