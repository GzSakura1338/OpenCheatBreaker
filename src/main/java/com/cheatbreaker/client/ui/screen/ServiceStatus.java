package com.cheatbreaker.client.ui.screen;

public class ServiceStatus {
    public StatusLevel statusLevel = StatusLevel.UNKNOWN;
    public String name;
    public String text;

    public ServiceStatus(String str, String str2) {
        this.name = str;
        this.text = str2;
    }

    public void setStatusLevel(StatusLevel statusLevel) {
        this.statusLevel = statusLevel;
    }

    public String getText() {
        return this.text;
    }

    public String getName() {
        return this.name;
    }

    public StatusLevel getStatusLevel() {
        return this.statusLevel;
    }
}
