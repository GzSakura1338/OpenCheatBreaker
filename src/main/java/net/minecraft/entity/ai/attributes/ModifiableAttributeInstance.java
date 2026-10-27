package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance {
   public Map<Integer, Set<AttributeModifier>> mapByOperation = Maps.newHashMap();
   public Map<String, Set<AttributeModifier>> mapByName = Maps.newHashMap();
   public Map<UUID, AttributeModifier> mapByUUID = Maps.newHashMap();
   public boolean needsUpdate = true;
   public BaseAttributeMap attributeMap;
   public IAttribute genericAttribute;
   public double baseValue;
   public double cachedValue;

   public ModifiableAttributeInstance(BaseAttributeMap attributeMapIn, IAttribute genericAttributeIn) {
      this.attributeMap = attributeMapIn;
      this.genericAttribute = genericAttributeIn;
      this.baseValue = genericAttributeIn.getDefaultValue();

      for (int i = 0; i < 3; i++) {
         this.mapByOperation.put(i, Sets.newHashSet());
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
   public void setBaseValue(double baseValue) {
      if (baseValue != this.getBaseValue()) {
         this.baseValue = baseValue;
         this.flagForUpdate();
      }
   }

   @Override
   public Collection<AttributeModifier> getModifiersByOperation(int operation) {
      return this.mapByOperation.get(operation);
   }

   @Override
   public Collection<AttributeModifier> getCollection() {
      HashSet newHashSet = Sets.newHashSet();

      for (int i = 0; i < 3; i++) {
         newHashSet.addAll(this.getModifiersByOperation(i));
      }

      return newHashSet;
   }

   @Override
   public AttributeModifier getModifier(UUID uuid) {
      return this.mapByUUID.get(uuid);
   }

   @Override
   public boolean hasModifier(AttributeModifier modifier) {
      return this.mapByUUID.get(modifier.getID()) != null;
   }

   @Override
   public void applyModifier(AttributeModifier modifier) {
      if (this.getModifier(modifier.getID()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      }

      Set set = this.mapByName.get(modifier.getName());
      if (set == null) {
         set = Sets.newHashSet();
         this.mapByName.put(modifier.getName(), set);
      }

      this.mapByOperation.get(modifier.getOperation()).add(modifier);
      set.add(modifier);
      this.mapByUUID.put(modifier.getID(), modifier);
      this.flagForUpdate();
   }

   public void flagForUpdate() {
      this.needsUpdate = true;
      this.attributeMap.add(this);
   }

   @Override
   public void removeModifier(AttributeModifier modifier) {
      for (int i = 0; i < 3; i++) {
         Set set = this.mapByOperation.get(i);
         set.remove(modifier);
      }

      Set set = this.mapByName.get(modifier.getName());
      if (set != null) {
         set.remove(modifier);
         if (set.isEmpty()) {
            this.mapByName.remove(modifier.getName());
         }
      }

      this.mapByUUID.remove(modifier.getID());
      this.flagForUpdate();
   }

   @Override
   public void removeAllModifiers() {
      Collection collection = this.getCollection();
      if (collection != null) {
         for (AttributeModifier attributeModifier : (Iterable<AttributeModifier>)Lists.newArrayList(collection)) {
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

      for (AttributeModifier attributeModifier : this.findCollection(0)) {
         baseValue += attributeModifier.getAmount();
      }

      double d = baseValue;

      for (AttributeModifier attributeModifier : this.findCollection(1)) {
         d += baseValue * attributeModifier.getAmount();
      }

      for (AttributeModifier attributeModifier : this.findCollection(2)) {
         d *= 1.0 + attributeModifier.getAmount();
      }

      return this.genericAttribute.clampValue(d);
   }

   public Collection<AttributeModifier> findCollection(int operation) {
      HashSet newHashSet = Sets.newHashSet(this.getModifiersByOperation(operation));

      for (IAttribute parent = this.genericAttribute.getParent(); parent != null; parent = parent.getParent()) {
         IAttributeInstance attributeInstance = this.attributeMap.getAttributeInstance(parent);
         if (attributeInstance != null) {
            newHashSet.addAll(attributeInstance.getModifiersByOperation(operation));
         }
      }

      return newHashSet;
   }
}
