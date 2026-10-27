package com.cheatbreaker.client.emote;

import java.lang.reflect.Method;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class EntityRendererReflection {
   public static Method methodRef;
   public static Method methodRef2;

   public static ModelBase findModelBase(RendererLivingEntity rendererLivingEntity) {
      return rendererLivingEntity.mainModel;
   }

   public static ModelBase nullValue(Render render, Entity entity) {
      return null;
   }

   public static ResourceLocation findResourceLocation(Render render, Entity entity) {
      try {
         if (methodRef == null) {
            methodRef = Render.class.getDeclaredMethod("getEntityTexture", Entity.class);
            methodRef.setAccessible(true);
         }

         return (ResourceLocation)methodRef.invoke(render, entity);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static void handleEntityLivingBase(EntityLivingBase entityLivingBase, RendererLivingEntity rendererLivingEntity) {
      try {
         if (methodRef2 == null) {
            methodRef2 = RendererLivingEntity.class.getDeclaredMethod("preRenderCallback", EntityLivingBase.class, float.class);
            methodRef2.setAccessible(true);
         }

         methodRef2.invoke(rendererLivingEntity, entityLivingBase, 1.0F);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
