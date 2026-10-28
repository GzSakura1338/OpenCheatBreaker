package net.minecraft.entity.ai.attributes;

public interface IAttribute {
    String getAttributeUnlocalizedName();

    double clampValue(double d);

    double getDefaultValue();

    boolean getShouldWatch();
}
