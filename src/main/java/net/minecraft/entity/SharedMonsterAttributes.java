package net.minecraft.entity;

import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
   public static Logger logger = LogManager.getLogger();
   public static IAttribute maxHealth = new RangedAttribute((IAttribute)null, "generic.maxHealth", 20.0, 0.0, 1024.0)
      .setDescription("Max Health")
      .setShouldWatch(true);
   public static IAttribute followRange = new RangedAttribute((IAttribute)null, "generic.followRange", 32.0, 0.0, 2048.0).setDescription("Follow Range");
   public static IAttribute knockbackResistance = new RangedAttribute((IAttribute)null, "generic.knockbackResistance", 0.0, 0.0, 1.0)
      .setDescription("Knockback Resistance");
   public static IAttribute movementSpeed = new RangedAttribute((IAttribute)null, "generic.movementSpeed", 0.7F, 0.0, 1024.0)
      .setDescription("Movement Speed")
      .setShouldWatch(true);
   public static IAttribute attackDamage = new RangedAttribute((IAttribute)null, "generic.attackDamage", 2.0, 0.0, 2048.0);

   public static NBTTagList writeBaseAttributeMapToNBT(BaseAttributeMap map) {
      NBTTagList nbttagList = new NBTTagList();

      for (IAttributeInstance attributeInstance : map.getAllAttributes()) {
         nbttagList.appendTag(writeAttributeInstanceToNBT(attributeInstance));
      }

      return nbttagList;
   }

   public static NBTTagCompound writeAttributeInstanceToNBT(IAttributeInstance instance) {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      IAttribute attribute = instance.getAttribute();
      nbttagCompound.setString("Name", attribute.getAttributeUnlocalizedName());
      nbttagCompound.setDouble("Base", instance.getBaseValue());
      Collection collection = instance.getCollection();
      if (collection != null && !collection.isEmpty()) {
         NBTTagList nbttagList = new NBTTagList();

         for (AttributeModifier attributeModifier : (Iterable<AttributeModifier>)collection) {
            if (attributeModifier.isSaved()) {
               nbttagList.appendTag(writeAttributeModifierToNBT(attributeModifier));
            }
         }

         nbttagCompound.setTag("Modifiers", nbttagList);
      }

      return nbttagCompound;
   }

   public static NBTTagCompound writeAttributeModifierToNBT(AttributeModifier modifier) {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      nbttagCompound.setString("Name", modifier.getName());
      nbttagCompound.setDouble("Amount", modifier.getAmount());
      nbttagCompound.setInteger("Operation", modifier.getOperation());
      nbttagCompound.setLong("UUIDMost", modifier.getID().getMostSignificantBits());
      nbttagCompound.setLong("UUIDLeast", modifier.getID().getLeastSignificantBits());
      return nbttagCompound;
   }

   public static void setAttributeModifiers(BaseAttributeMap map, NBTTagList list) {
      for (int i = 0; i < list.tagCount(); i++) {
         NBTTagCompound compoundTagAt = list.getCompoundTagAt(i);
         IAttributeInstance attributeInstanceByName = map.getAttributeInstanceByName(compoundTagAt.getString("Name"));
         if (attributeInstanceByName != null) {
            applyModifiersToAttributeInstance(attributeInstanceByName, compoundTagAt);
         } else {
            logger.warn("Ignoring unknown attribute '" + compoundTagAt.getString("Name") + "'");
         }
      }
   }

   public static void applyModifiersToAttributeInstance(IAttributeInstance instance, NBTTagCompound compound) {
      instance.setBaseValue(compound.getDouble("Base"));
      if (compound.hasKey("Modifiers", 9)) {
         NBTTagList tagList = compound.getTagList("Modifiers", 10);

         for (int i = 0; i < tagList.tagCount(); i++) {
            AttributeModifier readAttributeModifierFromNBT2 = readAttributeModifierFromNBT(tagList.getCompoundTagAt(i));
            if (readAttributeModifierFromNBT2 != null) {
               AttributeModifier modifier = instance.getModifier(readAttributeModifierFromNBT2.getID());
               if (modifier != null) {
                  instance.removeModifier(modifier);
               }

               instance.applyModifier(readAttributeModifierFromNBT2);
            }
         }
      }
   }

   public static AttributeModifier readAttributeModifierFromNBT(NBTTagCompound compound) {
      UUID uuid = new UUID(compound.getLong("UUIDMost"), compound.getLong("UUIDLeast"));

      try {
         return new AttributeModifier(uuid, compound.getString("Name"), compound.getDouble("Amount"), compound.getInteger("Operation"));
      } catch (Exception e) {
         logger.warn("Unable to create attribute: " + e.getMessage());
         return null;
      }
   }
}
