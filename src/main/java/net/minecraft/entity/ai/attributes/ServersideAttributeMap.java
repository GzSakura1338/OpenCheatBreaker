package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.management.LowerStringMap;

public class ServersideAttributeMap extends BaseAttributeMap {
   public Set<IAttributeInstance> attributeInstanceSet = Sets.newHashSet();
   public Map<String, IAttributeInstance> descriptionToAttributeInstanceMap = new LowerStringMap<>();

   public ModifiableAttributeInstance getAttributeInstance(IAttribute attribute) {
      return (ModifiableAttributeInstance)super.getAttributeInstance(attribute);
   }

   public ModifiableAttributeInstance getAttributeInstanceByName(String attributeName) {
      IAttributeInstance attributeInstanceByName = super.getAttributeInstanceByName(attributeName);
      if (attributeInstanceByName == null) {
         attributeInstanceByName = this.descriptionToAttributeInstanceMap.get(attributeName);
      }

      return (ModifiableAttributeInstance)attributeInstanceByName;
   }

   @Override
   public IAttributeInstance registerAttribute(IAttribute attribute) {
      IAttributeInstance registerAttribute = super.registerAttribute(attribute);
      if (attribute instanceof RangedAttribute && ((RangedAttribute)attribute).getDescription() != null) {
         this.descriptionToAttributeInstanceMap.put(((RangedAttribute)attribute).getDescription(), registerAttribute);
      }

      return registerAttribute;
   }

   @Override
   public IAttributeInstance createInstance(IAttribute attribute) {
      return new ModifiableAttributeInstance(this, attribute);
   }

   @Override
   public void add(IAttributeInstance instance) {
      if (instance.getAttribute().getShouldWatch()) {
         this.attributeInstanceSet.add(instance);
      }

      for (IAttribute attribute : this.attributeHierarchy.get(instance.getAttribute())) {
         ModifiableAttributeInstance attributeInstance = this.getAttributeInstance(attribute);
         if (attributeInstance != null) {
            attributeInstance.flagForUpdate();
         }
      }
   }

   public Set<IAttributeInstance> getAttributeInstanceSet() {
      return this.attributeInstanceSet;
   }

   public Collection<IAttributeInstance> getWatchedAttributes() {
      HashSet newHashSet = Sets.newHashSet();

      for (IAttributeInstance attributeInstance : this.getAllAttributes()) {
         if (attributeInstance.getAttribute().getShouldWatch()) {
            newHashSet.add(attributeInstance);
         }
      }

      return newHashSet;
   }
}
