package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class EntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent extends CancellableEvent {
   public RenderItem renderItem;
   public TransformType transformType;
   public IBakedModel bakedModel;
   public Entity entity;
   public String text;
   public ItemStack itemStack;
   public boolean renderItemGui;

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public String getText() {
      return this.text;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public EntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent(
      RenderItem renderItem, TransformType transformType, IBakedModel bakedModel, Entity entity, String str, ItemStack itemStack, boolean flag
   ) {
      this.renderItem = renderItem;
      this.transformType = transformType;
      this.bakedModel = bakedModel;
      this.entity = entity;
      this.text = str;
      this.itemStack = itemStack;
      this.renderItemGui = flag;
   }

   public TransformType getTransformType() {
      return this.transformType;
   }

   public boolean renderItemGui() {
      return this.renderItemGui;
   }

   public RenderItem getRenderItem() {
      return this.renderItem;
   }

   public IBakedModel getBakedModel() {
      return this.bakedModel;
   }
}
