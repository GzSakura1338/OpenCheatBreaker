package com.cheatbreaker.client.network.packet;

public class CosmeticSettingData {
    public long id;
    public boolean equipped;
    public String type;
    public String name;
    public float scale;
    public String texture;

    public CosmeticSettingData(long l, boolean flag, String str, String str2, float f, String str3) {
        this.id = l;
        this.equipped = flag;
        this.type = str;
        this.name = str2;
        this.scale = f;
        this.texture = str3;
    }

    public boolean isEquipped() {
        return this.equipped;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public long getId() {
        return this.id;
    }

    public String getTexture() {
        return this.texture;
    }

    public float getScale() {
        return this.scale;
    }
}
