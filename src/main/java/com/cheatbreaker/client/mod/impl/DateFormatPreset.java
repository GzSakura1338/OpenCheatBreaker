package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.impl.DateMod;
import com.cheatbreaker.client.mod.setting.ButtonSetting;
import com.cheatbreaker.client.mod.setting.Setting;

public class DateFormatPreset {
    public /* synthetic */ DateMod dateMod;
    public Setting setting;

    public DateFormatPreset(DateMod dateMod, String string) {
        this.dateMod = dateMod;
        this.setting = new ButtonSetting(dateMod, string).changeValue(string).setOnUpdate(object -> this.dateMod.dateFormat.changeValue(string)).withHierarchyParents(dateMod.formatOptions);
    }
}
