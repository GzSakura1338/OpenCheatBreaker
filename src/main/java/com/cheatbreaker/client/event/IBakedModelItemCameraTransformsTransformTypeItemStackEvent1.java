package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;

public class IBakedModelItemCameraTransformsTransformTypeItemStackEvent1 extends CancellableEvent {
   public ItemStack itemStack;
   public IBakedModel bakedModel;
   public TransformType transformType;

   public TransformType getTransformType() {
      return this.transformType;
   }

   public IBakedModelItemCameraTransformsTransformTypeItemStackEvent1(ItemStack itemStack, IBakedModel bakedModel, TransformType transformType) {
      this.itemStack = itemStack;
      this.bakedModel = bakedModel;
      this.transformType = transformType;
   }

   public IBakedModel getBakedModel() {
      return this.bakedModel;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }
}
