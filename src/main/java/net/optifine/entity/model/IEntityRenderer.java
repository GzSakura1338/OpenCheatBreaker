package net.optifine.entity.model;

import net.minecraft.util.ResourceLocation;

public interface IEntityRenderer {
   Class getEntityClass();

   void setEntityClass(Class clazz);

   ResourceLocation getLocationTextureCustom();

   void setLocationTextureCustom(ResourceLocation resourceLocation);
}
