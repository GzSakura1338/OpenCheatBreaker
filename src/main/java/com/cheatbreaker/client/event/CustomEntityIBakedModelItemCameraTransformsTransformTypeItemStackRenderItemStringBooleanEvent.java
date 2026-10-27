package com.cheatbreaker.client.event;

import com.cheatbreaker.client.mod.HudElementType;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent extends CancellableEvent {
   public RenderItem renderItem;
   public HudElementType hudElementType;
   public TransformType transformType;
   public IBakedModel bakedModel;
   public Entity entity;
   public String text;
   public ItemStack itemStack;
   public boolean renderItemGui;

   public Entity getEntity() {
      return this.entity;
   }

   public HudElementType getHudElementType() {
      return this.hudElementType;
   }

   public IBakedModel getBakedModel() {
      return this.bakedModel;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public String getText() {
      return this.text;
   }

   public TransformType getTransformType() {
      return this.transformType;
   }

   public RenderItem getRenderItem() {
      return this.renderItem;
   }

   public CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent(
      RenderItem renderItem, HudElementType hudElementType, TransformType transformType, IBakedModel bakedModel, Entity entity, String str, ItemStack itemStack, boolean flag
   ) {
      this.renderItem = renderItem;
      this.hudElementType = hudElementType;
      this.transformType = transformType;
      this.bakedModel = bakedModel;
      this.entity = entity;
      this.text = str;
      this.itemStack = itemStack;
      this.renderItemGui = flag;
   }

   public boolean renderItemGui() {
      return this.renderItemGui;
   }
}
