package net.minecraft.entity.ai.attributes;

import java.util.UUID;
import org.apache.commons.lang3.Validate;

public class AttributeModifier {
    public boolean isSaved = true;
    public UUID id;
    public String name;
    public double amount;
    public int operation;

    public AttributeModifier(String str, double d, int n) {
        this(UUID.randomUUID(), str, d, n);
    }

    public AttributeModifier(UUID uuid, String str, double d, int n) {
        this.id = uuid;
        this.name = str;
        this.amount = d;
        this.operation = n;
        Validate.notEmpty(str, "Modifier name cannot be empty", new Object[0]);
        Validate.inclusiveBetween(0, 2, n, "Invalid operation", new Object[0]);
    }

    public UUID getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getOperation() {
        return this.operation;
    }

    public double getAmount() {
        return this.amount;
    }

    public boolean isSaved() {
        return this.isSaved;
    }

    public AttributeModifier setSaved(boolean flag) {
        this.isSaved = flag;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            AttributeModifier attributeModifier = (AttributeModifier)obj;
            return this.id != null ? this.id.equals(attributeModifier.id) : attributeModifier.id == null;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AttributeModifier{amount="
            + this.amount
            + ", operation="
            + this.operation
            + ", name='"
            + this.name
            + '\''
            + ", id="
            + this.id
            + ", serialize="
            + this.isSaved
            + '}';
    }
}
