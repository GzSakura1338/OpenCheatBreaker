package net.minecraft.entity.ai.attributes;

public abstract class BaseAttribute implements IAttribute {
    public String unlocalizedName;
    public double defaultValue;
    public boolean shouldWatch;

    public BaseAttribute(String str, double d) {
        this.unlocalizedName = str;
        this.defaultValue = d;
        if (str == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }
    }

    @Override
    public String getAttributeUnlocalizedName() {
        return this.unlocalizedName;
    }

    @Override
    public double getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public boolean getShouldWatch() {
        return this.shouldWatch;
    }

    public BaseAttribute setShouldWatch(boolean flag) {
        this.shouldWatch = flag;
        return this;
    }

    @Override
    public int hashCode() {
        return this.unlocalizedName.hashCode();
    }
}
