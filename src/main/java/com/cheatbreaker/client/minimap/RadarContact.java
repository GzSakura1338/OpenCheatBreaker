package com.cheatbreaker.client.minimap;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

public class RadarContact {
    public int yOffset = 0;
    public String name = "_";
    public int y;
    public String text = "";
    public int[] intArray;
    public double z;
    public double distance;
    public Entity entity = null;
    public float floatValue;
    public int intValue = -1;
    public int intValue2 = -1;
    public float floatValue2;
    public double x;
    public Item item = null;
    public int intValue3 = -1;
    public int intValue4 = -1;
    public MobType mobType;

    public void setName(String str) {
        this.name = str;
    }

    public void setIntValue3(int n) {
        this.intValue3 = n;
    }

    public void setIntValue(int n) {
        this.intValue = n;
    }

    public RadarContact(Entity entity, MobType mobType) {
        this.entity = entity;
        this.mobType = mobType;
    }

    public void setIntValue4(int n) {
        this.intValue4 = n;
    }

    public void updatePosition() {
        this.x = this.entity.posX;
        this.y = (int)this.entity.posY + this.yOffset;
        this.z = this.entity.posZ;
    }

    public void setIntValue2(int n) {
        this.intValue2 = n;
    }
}
