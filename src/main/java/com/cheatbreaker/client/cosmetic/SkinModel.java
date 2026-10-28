package com.cheatbreaker.client.cosmetic;

public enum SkinModel {
    STEVE("classic"),
    ALEX("slim");

    public String modelName;

    SkinModel(String str) {
        this.modelName = str;
    }

    public String getModelName() {
        return this.modelName;
    }
}
