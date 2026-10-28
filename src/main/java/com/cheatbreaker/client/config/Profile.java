package com.cheatbreaker.client.config;

public class Profile {
    public int intValue = 0;
    public String name;
    public boolean isDefault;

    public void setName(String str) {
        this.name = str;
    }

    public Profile(String str, boolean flag) {
        this.name = str;
        this.isDefault = flag;
    }

    public String getName() {
        return this.name;
    }

    public boolean isDefault() {
        return this.isDefault;
    }
}
