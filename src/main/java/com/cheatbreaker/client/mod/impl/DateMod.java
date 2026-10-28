package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractBackgroundHudMod;
import com.cheatbreaker.client.mod.setting.FormatSetting;
import com.cheatbreaker.client.mod.setting.Setting;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateMod extends AbstractBackgroundHudMod {
    public Setting dateFormat;
    public SimpleDateFormat simpleDateFormat;

    @Override
    public String getDynamicText() {
        return this.simpleDateFormat.format(new Date());
    }

    @Override
    public String getLabelText() {
        return "Date";
    }

    public void updateDateFormat() {
        try {
            this.simpleDateFormat = new SimpleDateFormat(this.dateFormat.getStringValue());
        } catch (IllegalArgumentException e) {
            this.dateFormat.changeValue("MM/dd/yy");
        }
    }

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        this.updateDateFormat();
    }

    @Override
    public void createFormatOptions() {
        this.dateFormat = new FormatSetting(this, "Date Format")
            .changeValue("MM/dd/yy")
            .setOnUpdate(this::lambda$createFormatOptions$0)
            .withHierarchyParents(this.formatOptions);
    }

    public void lambda$createFormatOptions$0(Object obj) {
        this.updateDateFormat();
    }

    public DateMod() throws java.io.IOException {
        super("Date");
        this.setDescription("Displays the current date.");
        this.setLabel("[9/6/2018]");
    }
}
