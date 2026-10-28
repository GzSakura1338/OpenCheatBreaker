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
    public static IAttribute maxHealth = new RangedAttribute("generic.maxHealth", 20.0, 0.0, Double.MAX_VALUE).setDescription("Max Health").setShouldWatch(true);
    public static IAttribute followRange = new RangedAttribute("generic.followRange", 32.0, 0.0, 2048.0).setDescription("Follow Range");
    public static IAttribute knockbackResistance = new RangedAttribute("generic.knockbackResistance", 0.0, 0.0, 1.0).setDescription("Knockback Resistance");
    public static IAttribute movementSpeed = new RangedAttribute("generic.movementSpeed", 0.7F, 0.0, Double.MAX_VALUE)
        .setDescription("Movement Speed")
        .setShouldWatch(true);
    public static IAttribute attackDamage = new RangedAttribute("generic.attackDamage", 2.0, 0.0, Double.MAX_VALUE);

    public static NBTTagList writeBaseAttributeMapToNBT(BaseAttributeMap baseAttributeMap) {
        NBTTagList nbttagList = new NBTTagList();

        for (IAttributeInstance attributeInstance : (Iterable<IAttributeInstance>)baseAttributeMap.getAllAttributes()) {
            nbttagList.appendTag(writeAttributeInstanceToNBT(attributeInstance));
        }

        return nbttagList;
    }

    public static NBTTagCompound writeAttributeInstanceToNBT(IAttributeInstance attributeInstance) {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        IAttribute attribute = attributeInstance.getAttribute();
        nbttagCompound.setString("Name", attribute.getAttributeUnlocalizedName());
        nbttagCompound.setDouble("Base", attributeInstance.getBaseValue());
        Collection createHashSet = attributeInstance.createHashSet();
        if (createHashSet != null && !createHashSet.isEmpty()) {
            NBTTagList nbttagList = new NBTTagList();

            for (AttributeModifier attributeModifier : (Iterable<AttributeModifier>)createHashSet) {
                if (attributeModifier.isSaved()) {
                    nbttagList.appendTag(writeAttributeModifierToNBT(attributeModifier));
                }
            }

            nbttagCompound.setTag("Modifiers", nbttagList);
        }

        return nbttagCompound;
    }

    public static NBTTagCompound writeAttributeModifierToNBT(AttributeModifier attributeModifier) {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        nbttagCompound.setString("Name", attributeModifier.getName());
        nbttagCompound.setDouble("Amount", attributeModifier.getAmount());
        nbttagCompound.setInteger("Operation", attributeModifier.getOperation());
        nbttagCompound.setLong("UUIDMost", attributeModifier.getID().getMostSignificantBits());
        nbttagCompound.setLong("UUIDLeast", attributeModifier.getID().getLeastSignificantBits());
        return nbttagCompound;
    }

    public static void fromNbt(BaseAttributeMap baseAttributeMap, NBTTagList nbttagList) {
        for (int i = 0; i < nbttagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = nbttagList.getCompoundTagAt(i);
            IAttributeInstance attributeInstanceByName = baseAttributeMap.getAttributeInstanceByName(compoundTagAt.getString("Name"));
            if (attributeInstanceByName != null) {
                applyModifiersToAttributeInstance(attributeInstanceByName, compoundTagAt);
            } else {
                logger.warn("Ignoring unknown attribute '" + compoundTagAt.getString("Name") + "'");
            }
        }
    }

    public static void applyModifiersToAttributeInstance(IAttributeInstance attributeInstance, NBTTagCompound nbttagCompound) {
        attributeInstance.setBaseValue(nbttagCompound.getDouble("Base"));
        if (nbttagCompound.hasKey("Modifiers", 9)) {
            NBTTagList tagList = nbttagCompound.getTagList("Modifiers", 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
                AttributeModifier readAttributeModifierFromNBT2 = readAttributeModifierFromNBT(tagList.getCompoundTagAt(i));
                AttributeModifier modifier = attributeInstance.getModifier(readAttributeModifierFromNBT2.getID());
                if (modifier != null) {
                    attributeInstance.removeModifier(modifier);
                }

                attributeInstance.applyModifier(readAttributeModifierFromNBT2);
            }
        }
    }

    public static AttributeModifier readAttributeModifierFromNBT(NBTTagCompound nbttagCompound) {
        UUID uuid = new UUID(nbttagCompound.getLong("UUIDMost"), nbttagCompound.getLong("UUIDLeast"));
        return new AttributeModifier(uuid, nbttagCompound.getString("Name"), nbttagCompound.getDouble("Amount"), nbttagCompound.getInteger("Operation"));
    }
}
