package net.minecraft.entity.ai.attributes;

public class RangedAttribute extends BaseAttribute {
    public double minimumValue;
    public double maximumValue;
    public String description;

    public RangedAttribute(String str, double d, double d2, double d3) {
        super(str, d);
        this.minimumValue = d2;
        this.maximumValue = d3;
        if (d2 > d3) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        }

        if (d < d2) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        }

        if (d > d3) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }

    public RangedAttribute setDescription(String str) {
        this.description = str;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public double clampValue(double d) {
        if (d < this.minimumValue) {
            d = this.minimumValue;
        }

        if (d > this.maximumValue) {
            d = this.maximumValue;
        }

        return d;
    }
}
