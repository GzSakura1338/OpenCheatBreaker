package net.minecraft.entity.player;

public enum EnumPlayerModelParts {
    CAPE(0, "cape", "Cape"),
    JACKET(1, "jacket", "Jacket"),
    LEFT_SLEEVE(2, "left_sleeve", "Left Sleeve"),
    RIGHT_SLEEVE(3, "right_sleeve", "Right Sleeve"),
    LEFT_PANTS_LEG(4, "left_pants_leg", "Left Pants Leg"),
    RIGHT_PANTS_LEG(5, "right_pants_leg", "Right Pants Leg"),
    HAT(6, "hat", "Hat");

    public int partId;
    public int partMask;
    public String text;
    public String text2;

    public int getPartMask() {
        return this.partMask;
    }

    public int getPartId() {
        return this.partId;
    }

    EnumPlayerModelParts(int n, String str, String str2) {
        this.partId = n;
        this.partMask = 1 << n;
        this.text = str;
        this.text2 = str2;
    }

    public String getText2() {
        return this.text2;
    }

    public String getText() {
        return this.text;
    }
}
