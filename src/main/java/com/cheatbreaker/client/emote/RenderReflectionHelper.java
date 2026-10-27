package com.cheatbreaker.client.emote;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderReflectionHelper {
   public static ModelBase findModelBase(RendererLivingEntity rendererLivingEntity) {
      return EntityRendererReflection.findModelBase(rendererLivingEntity);
   }

   public static void handleEntityLivingBase(EntityLivingBase entityLivingBase, RendererLivingEntity rendererLivingEntity) {
      EntityRendererReflection.handleEntityLivingBase(entityLivingBase, rendererLivingEntity);
   }

   public static ModelBase nullValue(Render render, Entity entity) {
      return EntityRendererReflection.nullValue(render, entity);
   }

   public static ResourceLocation findResourceLocation(Render render, Entity entity) {
      return EntityRendererReflection.findResourceLocation(render, entity);
   }

   public static void handleBlock(Block block, int n) {
      BlockReflection.handleBlock(block, n);
   }
}
