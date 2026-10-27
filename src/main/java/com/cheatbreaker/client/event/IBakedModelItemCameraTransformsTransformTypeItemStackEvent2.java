package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;

public class IBakedModelItemCameraTransformsTransformTypeItemStackEvent2 extends CancellableEvent {
   public ItemStack itemStack;
   public IBakedModel bakedModel;
   public TransformType transformType;

   public IBakedModelItemCameraTransformsTransformTypeItemStackEvent2(ItemStack itemStack, IBakedModel bakedModel, TransformType transformType) {
      this.itemStack = itemStack;
      this.bakedModel = bakedModel;
      this.transformType = transformType;
   }

   public IBakedModel getBakedModel() {
      return this.bakedModel;
   }

   public TransformType getTransformType() {
      return this.transformType;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }
}
