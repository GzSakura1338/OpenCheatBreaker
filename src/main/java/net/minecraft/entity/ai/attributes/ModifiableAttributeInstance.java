package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance {
    public Map mapByOperation = Maps.newHashMap();
    public Map mapByName = Maps.newHashMap();
    public Map mapByUUID = Maps.newHashMap();
    public boolean needsUpdate = true;
    public BaseAttributeMap attributeMap;
    public IAttribute genericAttribute;
    public double baseValue;
    public double cachedValue;

    public ModifiableAttributeInstance(BaseAttributeMap baseAttributeMap, IAttribute attribute) {
        this.attributeMap = baseAttributeMap;
        this.genericAttribute = attribute;
        this.baseValue = attribute.getDefaultValue();

        for (int i = 0; i < 3; i++) {
            this.mapByOperation.put(i, new HashSet());
        }
    }

    @Override
    public IAttribute getAttribute() {
        return this.genericAttribute;
    }

    @Override
    public double getBaseValue() {
        return this.baseValue;
    }

    @Override
    public void setBaseValue(double d) {
        if (d != this.getBaseValue()) {
            this.baseValue = d;
            this.flagForUpdate();
        }
    }

    public Collection getModifiersByOperation(int n) {
        return (Collection)this.mapByOperation.get(n);
    }

    @Override
    public Collection createHashSet() {
        HashSet set = new HashSet();

        for (int i = 0; i < 3; i++) {
            set.addAll(this.getModifiersByOperation(i));
        }

        return set;
    }

    @Override
    public AttributeModifier getModifier(UUID uuid) {
        return (AttributeModifier)this.mapByUUID.get(uuid);
    }

    @Override
    public void applyModifier(AttributeModifier attributeModifier) {
        if (this.getModifier(attributeModifier.getID()) != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }

        Object obj = this.mapByName.get(attributeModifier.getName());
        if (obj == null) {
            obj = new HashSet();
            this.mapByName.put(attributeModifier.getName(), obj);
        }

        ((Set)this.mapByOperation.get(attributeModifier.getOperation())).add(attributeModifier);
        ((Set)obj).add(attributeModifier);
        this.mapByUUID.put(attributeModifier.getID(), attributeModifier);
        this.flagForUpdate();
    }

    public void flagForUpdate() {
        this.needsUpdate = true;
        this.attributeMap.addAttributeInstance(this);
    }

    @Override
    public void removeModifier(AttributeModifier attributeModifier) {
        for (int i = 0; i < 3; i++) {
            Set set = (Set)this.mapByOperation.get(i);
            set.remove(attributeModifier);
        }

        Set set = (Set)this.mapByName.get(attributeModifier.getName());
        if (set != null) {
            set.remove(attributeModifier);
            if (set.isEmpty()) {
                this.mapByName.remove(attributeModifier.getName());
            }
        }

        this.mapByUUID.remove(attributeModifier.getID());
        this.flagForUpdate();
    }

    @Override
    public void removeAllModifiers() {
        Collection createHashSet = this.createHashSet();
        if (createHashSet != null) {
            for (AttributeModifier attributeModifier : (Iterable<AttributeModifier>)new ArrayList(createHashSet)) {
                this.removeModifier(attributeModifier);
            }
        }
    }

    @Override
    public double getAttributeValue() {
        if (this.needsUpdate) {
            this.cachedValue = this.computeValue();
            this.needsUpdate = false;
        }

        return this.cachedValue;
    }

    public double computeValue() {
        double baseValue = this.getBaseValue();

        for (AttributeModifier attributeModifier : (Iterable<AttributeModifier>)this.getModifiersByOperation(0)) {
            baseValue += attributeModifier.getAmount();
        }

        double d = baseValue;

        for (AttributeModifier attributeModifier : (Iterable<AttributeModifier>)this.getModifiersByOperation(1)) {
            d += baseValue * attributeModifier.getAmount();
        }

        for (AttributeModifier attributeModifier : (Iterable<AttributeModifier>)this.getModifiersByOperation(2)) {
            d *= 1.0 + attributeModifier.getAmount();
        }

        return this.genericAttribute.clampValue(d);
    }
}
