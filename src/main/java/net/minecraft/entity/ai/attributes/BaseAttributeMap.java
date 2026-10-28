package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.management.LowerStringMap;

public abstract class BaseAttributeMap {
    public Map attributes = new HashMap();
    public Map attributesByName = new LowerStringMap();

    public IAttributeInstance getAttributeInstance(IAttribute attribute) {
        return (IAttributeInstance)this.attributes.get(attribute);
    }

    public IAttributeInstance getAttributeInstanceByName(String str) {
        return (IAttributeInstance)this.attributesByName.get(str);
    }

    public abstract IAttributeInstance registerAttribute(IAttribute attribute);

    public Collection getAllAttributes() {
        return this.attributesByName.values();
    }

    public void addAttributeInstance(ModifiableAttributeInstance modifiableAttributeInstance) {
    }

    public void removeAttributeModifiers(Multimap multimap) {
        for (Entry entry : (Iterable<Entry>)multimap.entries()) {
            IAttributeInstance attributeInstanceByName = this.getAttributeInstanceByName((String)entry.getKey());
            if (attributeInstanceByName != null) {
                attributeInstanceByName.removeModifier((AttributeModifier)entry.getValue());
            }
        }
    }

    public void applyAttributeModifiers(Multimap multimap) {
        for (Entry entry : (Iterable<Entry>)multimap.entries()) {
            IAttributeInstance attributeInstanceByName = this.getAttributeInstanceByName((String)entry.getKey());
            if (attributeInstanceByName != null) {
                attributeInstanceByName.removeModifier((AttributeModifier)entry.getValue());
                attributeInstanceByName.applyModifier((AttributeModifier)entry.getValue());
            }
        }
    }
}
