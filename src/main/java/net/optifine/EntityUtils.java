package net.optifine;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class EntityUtils {
    public static ReflectorClass ForgeEntityLivingBase = new ReflectorClass(EntityLivingBase.class);
    public static ReflectorField ForgeEntityLivingBase_entityAge = new ReflectorField(ForgeEntityLivingBase, "entityAge");
    public static boolean directEntityAge = true;
    public static ReflectorClass ForgeEntityLiving = new ReflectorClass(EntityLiving.class);
    public static ReflectorMethod ForgeEntityLiving_despawnEntity = new ReflectorMethod(ForgeEntityLiving, "despawnEntity");
    public static boolean directDespawnEntity = true;

    public static int getEntityAge(EntityLivingBase entityLivingBase) {
        if (directEntityAge) {
            try {
                return entityLivingBase.entityAge;
            } catch (IllegalAccessError e) {
                directEntityAge = false;
                if (!ForgeEntityLivingBase_entityAge.exists()) {
                    throw e;
                }
            }
        }

        return (Integer)Reflector.getFieldValue(entityLivingBase, ForgeEntityLivingBase_entityAge);
    }

    public static void setEntityAge(EntityLivingBase entityLivingBase, int n) {
        if (directEntityAge) {
            try {
                entityLivingBase.entityAge = n;
                return;
            } catch (IllegalAccessError e) {
                directEntityAge = false;
                if (!ForgeEntityLivingBase_entityAge.exists()) {
                    throw e;
                }
            }
        }

        Reflector.setFieldValue(entityLivingBase, ForgeEntityLivingBase_entityAge, n);
    }

    public static void despawnEntity(EntityLiving entityLiving) {
        if (directDespawnEntity) {
            try {
                entityLiving.despawnEntity();
                return;
            } catch (IllegalAccessError e) {
                directDespawnEntity = false;
                if (!ForgeEntityLiving_despawnEntity.exists()) {
                    throw e;
                }
            }
        }

        Reflector.callVoid(entityLiving, ForgeEntityLiving_despawnEntity);
    }
}
