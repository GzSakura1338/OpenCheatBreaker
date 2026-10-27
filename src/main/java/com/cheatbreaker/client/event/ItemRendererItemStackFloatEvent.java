package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;

public class ItemRendererItemStackFloatEvent extends CancellableEvent {
   public ItemRenderer itemRenderer;
   public ItemStack itemStack;
   public float prevEquippedProgress;
   public float equippedProgress;
   public float partialTicks;

   public void setEquippedProgress(float f) {
      this.equippedProgress = f;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public float getEquippedProgress() {
      return this.equippedProgress;
   }

   public void setPrevEquippedProgress(float f) {
      this.prevEquippedProgress = f;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public ItemRendererItemStackFloatEvent(ItemRenderer itemRenderer, ItemStack itemStack, float f, float f2, float f3) {
      this.itemRenderer = itemRenderer;
      this.itemStack = itemStack;
      this.prevEquippedProgress = f;
      this.equippedProgress = f2;
      this.partialTicks = f3;
   }

   public float getPrevEquippedProgress() {
      return this.prevEquippedProgress;
   }
}
