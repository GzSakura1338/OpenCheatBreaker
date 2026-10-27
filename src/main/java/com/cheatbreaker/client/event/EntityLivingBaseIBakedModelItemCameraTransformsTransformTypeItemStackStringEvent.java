package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityLivingBaseIBakedModelItemCameraTransformsTransformTypeItemStackStringEvent extends CancellableEvent {
   public ItemStack itemStack;
   public IBakedModel bakedModel;
   public String text;
   public TransformType transformType;
   public EntityLivingBase entityLivingBase;

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public EntityLivingBase getEntityLivingBase() {
      return this.entityLivingBase;
   }

   public IBakedModel getBakedModel() {
      return this.bakedModel;
   }

   public TransformType getTransformType() {
      return this.transformType;
   }

   public String getText() {
      return this.text;
   }

   public EntityLivingBaseIBakedModelItemCameraTransformsTransformTypeItemStackStringEvent(
      ItemStack itemStack, IBakedModel bakedModel, String str, TransformType transformType, EntityLivingBase entityLivingBase
   ) {
      this.itemStack = itemStack;
      this.bakedModel = bakedModel;
      this.text = str;
      this.transformType = transformType;
      this.entityLivingBase = entityLivingBase;
   }
}
