package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.management.LowerStringMap;

public class ServersideAttributeMap extends BaseAttributeMap {
    public Set attributeInstanceSet = Sets.newHashSet();
    public Map descriptionToAttributeInstanceMap = new LowerStringMap();

    public ModifiableAttributeInstance getAttributeInstance(IAttribute attribute) {
        return (ModifiableAttributeInstance)super.getAttributeInstance(attribute);
    }

    public ModifiableAttributeInstance getAttributeInstanceByName(String str) {
        IAttributeInstance attributeInstanceByName = super.getAttributeInstanceByName(str);
        if (attributeInstanceByName == null) {
            attributeInstanceByName = (IAttributeInstance)this.descriptionToAttributeInstanceMap.get(str);
        }

        return (ModifiableAttributeInstance)attributeInstanceByName;
    }

    @Override
    public IAttributeInstance registerAttribute(IAttribute attribute) {
        if (this.attributesByName.containsKey(attribute.getAttributeUnlocalizedName())) {
            throw new IllegalArgumentException("Attribute is already registered!");
        }

        ModifiableAttributeInstance modifiableAttributeInstance = new ModifiableAttributeInstance(this, attribute);
        this.attributesByName.put(attribute.getAttributeUnlocalizedName(), modifiableAttributeInstance);
        if (attribute instanceof RangedAttribute && ((RangedAttribute)attribute).getDescription() != null) {
            this.descriptionToAttributeInstanceMap.put(((RangedAttribute)attribute).getDescription(), modifiableAttributeInstance);
        }

        this.attributes.put(attribute, modifiableAttributeInstance);
        return modifiableAttributeInstance;
    }

    @Override
    public void addAttributeInstance(ModifiableAttributeInstance modifiableAttributeInstance) {
        if (modifiableAttributeInstance.getAttribute().getShouldWatch()) {
            this.attributeInstanceSet.add(modifiableAttributeInstance);
        }
    }

    public Set getAttributeInstanceSet() {
        return this.attributeInstanceSet;
    }

    public Collection getWatchedAttributes() {
        HashSet newHashSet = Sets.newHashSet();

        for (IAttributeInstance attributeInstance : (Iterable<IAttributeInstance>)this.getAllAttributes()) {
            if (attributeInstance.getAttribute().getShouldWatch()) {
                newHashSet.add(attributeInstance);
            }
        }

        return newHashSet;
    }
}
