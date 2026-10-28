package com.cheatbreaker.client.mod.impl;

public class FoodValue {
    public int healAmount;
    public float saturationModifier;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FoodValue)) {
            return false;
        }

        FoodValue foodValue = (FoodValue)obj;
        return this.healAmount == foodValue.healAmount && Float.compare(foodValue.saturationModifier, this.saturationModifier) == 0;
    }

    @Override
    public int hashCode() {
        int healAmount = this.healAmount;
        return 31 * healAmount + (this.saturationModifier != 0.0F ? Float.floatToIntBits(this.saturationModifier) : 0);
    }

    public float getFloatValue() {
        return this.healAmount * this.saturationModifier * 2.0F;
    }

    public FoodValue(int n, float f) {
        this.healAmount = n;
        this.saturationModifier = f;
    }
}
