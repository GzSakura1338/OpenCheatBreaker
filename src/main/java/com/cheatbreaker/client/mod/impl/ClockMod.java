package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.mod.AbstractBackgroundHudMod;
import com.cheatbreaker.client.mod.setting.Setting;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockMod extends AbstractBackgroundHudMod {
    public SimpleDateFormat simpleDateFormat;
    public Setting showSeconds;
    public Date date;
    public String showMilitary24HourTimeValue = "";
    public String showSecondsValue = "";
    public String pmMarkerValue = "";
    public Setting showMilitary24HourTime;
    public Setting pmMarker;

    @Override
    public void createFormatOptions() {
        this.showMilitary24HourTime = new Setting(this, "Show Military (24 Hour) Time")
            .setDescription("Show the clock time in military time format.")
            .withHierarchyParents(this.formatOptions)
            .changeValue(false)
            .setOnUpdate(obj -> {
                this.showMilitary24HourTimeValue = (Boolean)obj ? "HH" : "h";
                this.pmMarkerValue = this.pmMarker.getBooleanValue() && !(Boolean)obj ? " a" : "";
                this.updateDateFormat();
            });
        this.pmMarker = new Setting(this, "Show Meridiem (AM/PM) Marker").setDescription("Show the AM/PM marker.").changeValue(true).setOnUpdate(obj -> {
            this.pmMarkerValue = (Boolean)obj && !this.showMilitary24HourTime.getBooleanValue() ? " a" : "";
            this.updateDateFormat();
        }).withInvertedHierarchyParents(this.showMilitary24HourTime);
        this.showSeconds = new Setting(this, "Show Seconds")
            .setDescription("Displays the seconds.")
            .withHierarchyParents(this.formatOptions)
            .changeValue(false)
            .setOnUpdate(obj -> {
                this.showSecondsValue = (Boolean)obj ? ":ss" : "";
                this.updateDateFormat();
            });
    }

    @Override
    public String getLabelText() {
        return "Time";
    }

    @Override
    public String getDynamicText() {
        return this.simpleDateFormat.format(this.date);
    }

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        this.showMilitary24HourTime.getOnUpdate().accept(this.showMilitary24HourTime.getValue());
        this.pmMarker.getOnUpdate().accept(this.pmMarker.getValue());
        this.showSeconds.getOnUpdate().accept(this.showSeconds.getValue());
        if (this.date == null) {
            this.date = new Date();
        }
    }

    public void updateDateFormat() {
        this.simpleDateFormat = new SimpleDateFormat(this.showMilitary24HourTimeValue + ":mm" + this.showSecondsValue + this.pmMarkerValue);
    }

    public ClockMod() throws java.io.IOException {
        super("Clock");
        this.setDescription("Displays the current time.");
        this.setLabel("[11:11 AM]");
        this.addEventHandler(ClientTickEvent.class, clientTickEvent -> this.date.setTime(System.currentTimeMillis()));
    }
}
