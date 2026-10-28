package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.setting.Setting;
import java.util.List;

public class ArmorStatusDamageComparable implements Comparable {
    public int percent;
    public Setting color;

    public boolean checkObject(Object obj) {
        return obj instanceof ArmorStatusDamageComparable;
    }

    @Override
    public int hashCode() {
        byte b = 59;
        int n = 1;
        n = n * 59 + this.getPercent();
        Setting color = this.getColor();
        return n * 59 + (color == null ? 43 : color.hashCode());
    }

    public ArmorStatusDamageComparable(int n, Setting setting) {
        this.percent = n;
        this.color = setting;
    }

    public Setting getColor() {
        return this.color;
    }

    @Override
    public int compareTo(Object obj) {
        return this.compare((ArmorStatusDamageComparable)obj);
    }

    public void setPercent(int n) {
        this.percent = n;
    }

    @Override
    public String toString() {
        return "ArmorStatusDamageComparable(percent=" + this.getPercent() + ", color=" + this.getColor() + ")";
    }

    public static int getColorForPercent(List<ArmorStatusDamageComparable> list, int n) {
        if (!ArmorStatusMod.staticDamageColor.getBooleanValue()) {
            for (ArmorStatusDamageComparable armorStatusDamageComparable : list) {
                if (n <= armorStatusDamageComparable.percent) {
                    return armorStatusDamageComparable.color.getColorValue();
                }
            }
        }

        return ArmorStatusMod.durabilityColor.getColorValue();
    }

    public void setColor(Setting setting) {
        this.color = setting;
    }

    public int getPercent() {
        return this.percent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ArmorStatusDamageComparable)) {
            return false;
        }

        ArmorStatusDamageComparable armorStatusDamageComparable = (ArmorStatusDamageComparable)obj;
        if (!armorStatusDamageComparable.checkObject(this)) {
            return false;
        }

        if (this.getPercent() != armorStatusDamageComparable.getPercent()) {
            return false;
        }

        Setting color = this.getColor();
        Setting color2 = armorStatusDamageComparable.getColor();
        return color == null ? color2 == null : color.equals(color2);
    }

    public int compare(ArmorStatusDamageComparable armorStatusDamageComparable) {
        return Integer.compare(this.percent, armorStatusDamageComparable.percent);
    }
}
