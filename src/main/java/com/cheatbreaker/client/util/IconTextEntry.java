package com.cheatbreaker.client.util;

import java.util.HashMap;
import java.util.Map;

public class IconTextEntry {
    public String text;
    public String text2;
    public static Map<String, IconTextEntry> stringToIconTextEntryMap = new HashMap<>();
    public String text3;
    public String text4;
    public boolean flag;

    public String getText4() {
        return this.text4;
    }

    public static Map<String, IconTextEntry> getStringToIconTextEntryMap() {
        return stringToIconTextEntryMap;
    }

    public String getText() {
        return this.text;
    }

    public void setText3(String str) {
        this.text3 = str;
    }

    public void setText4(String str) {
        this.text4 = str;
    }

    public void setText(String str) {
        this.text = str;
    }

    public String getText3() {
        return this.text3;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setText2(String str) {
        this.text2 = str;
    }

    public IconTextEntry(String str, String str2, String str3, String str4) {
        this.text = str;
        this.text2 = str2;
        this.text3 = str3;
        this.text4 = str4;
        this.flag = false;
    }

    public String getText2() {
        return this.text2;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public IconTextEntry(String str, String str2, String str3) {
        this.text = str;
        this.text2 = str2;
        this.text3 = str3;
        this.text4 = "client/logo_white_108.png";
        this.flag = true;
    }
}
