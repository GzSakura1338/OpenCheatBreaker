package com.cheatbreaker.client.mod.setting;

import com.cheatbreaker.client.mod.AbstractMod;
import java.util.List;

public class ToggleTextSetting extends Setting {
    public String customOffText = "OFF";
    public String customOnText = "ON";

    public ToggleTextSetting(AbstractMod abstractMod, String str) {
        super(abstractMod, str);
    }

    public String getCustomOnText() {
        return this.customOnText;
    }

    @Override
    public Setting setToggleTexts(String str, String str2) {
        this.customOffText = str;
        this.customOnText = str2;
        return this;
    }

    public void setCustomOffText(String str) {
        this.customOffText = str;
    }

    public ToggleTextSetting(List<Setting> list, String str) {
        super(list, str);
    }

    public String getCustomOffText() {
        return this.customOffText;
    }

    public void setCustomOnText(String str) {
        this.customOnText = str;
    }
}
