package net.minecraft.world;

public enum EnumSkyBlock {
    Sky(15),
    Block(0);

    public int defaultLightValue;

    EnumSkyBlock(int n) {
        this.defaultLightValue = n;
    }
}
