package com.cheatbreaker.client.audio;

public class AudioDevice {
    public String description;
    public String name;

    public String getName() {
        return this.name;
    }

    public AudioDevice(String str, String str2) {
        this.description = str;
        this.name = str2;
    }

    public String getDescription() {
        return this.description;
    }
}
