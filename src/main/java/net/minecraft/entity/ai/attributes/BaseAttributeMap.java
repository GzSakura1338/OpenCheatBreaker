package net.minecraft.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.management.LowerStringMap;

public abstract class BaseAttributeMap {
   public Map<IAttribute, IAttributeInstance> attributes = Maps.newHashMap();
   public Map<String, IAttributeInstance> attributesByName = new LowerStringMap<>();
   public Multimap<IAttribute, IAttribute> attributeHierarchy = HashMultimap.create();

   public IAttributeInstance getAttributeInstance(IAttribute attribute) {
      return this.attributes.get(attribute);
   }

   public IAttributeInstance getAttributeInstanceByName(String attributeName) {
      return this.attributesByName.get(attributeName);
   }

   public IAttributeInstance registerAttribute(IAttribute attribute) {
      if (this.attributesByName.containsKey(attribute.getAttributeUnlocalizedName())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      }

      IAttributeInstance createInstance = this.createInstance(attribute);
      this.attributesByName.put(attribute.getAttributeUnlocalizedName(), createInstance);
      this.attributes.put(attribute, createInstance);

      for (IAttribute parent = attribute.getParent(); parent != null; parent = parent.getParent()) {
         this.attributeHierarchy.put(parent, attribute);
      }

      return createInstance;
   }

   public abstract IAttributeInstance createInstance(IAttribute attribute);

   public Collection<IAttributeInstance> getAllAttributes() {
      return this.attributesByName.values();
   }

   public void add(IAttributeInstance instance) {
   }

   public void removeAttributeModifiers(Multimap<String, AttributeModifier> modifiers) {
      for (Entry entry : modifiers.entries()) {
         IAttributeInstance attributeInstanceByName = this.getAttributeInstanceByName((String)entry.getKey());
         if (attributeInstanceByName != null) {
            attributeInstanceByName.removeModifier((AttributeModifier)entry.getValue());
         }
      }
   }

   public void applyAttributeModifiers(Multimap<String, AttributeModifier> modifiers) {
      for (Entry entry : modifiers.entries()) {
         IAttributeInstance attributeInstanceByName = this.getAttributeInstanceByName((String)entry.getKey());
         if (attributeInstanceByName != null) {
            attributeInstanceByName.removeModifier((AttributeModifier)entry.getValue());
            attributeInstanceByName.applyModifier((AttributeModifier)entry.getValue());
         }
      }
   }
}
