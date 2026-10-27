package com.cheatbreaker.client.event;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;

public class EntityPlayerSPItemStackBooleanFloatIntEvent extends CancellableEvent {
   public boolean flag2;
   public EntityPlayerSP entityPlayerSP;
   public ItemStack currentItem;
   public ItemStack itemToRender;
   public float prevEquippedProgress;
   public float equippedProgress;
   public int equippedItemSlot;

   public float getEquippedProgress() {
      return this.equippedProgress;
   }

   public void setCurrentItem(ItemStack itemStack) {
      this.currentItem = itemStack;
   }

   public float getPrevEquippedProgress() {
      return this.prevEquippedProgress;
   }

   public void setEquippedProgress(float f) {
      this.equippedProgress = f;
   }

   public int getEquippedItemSlot() {
      return this.equippedItemSlot;
   }

   public boolean isFlag4() {
      return this.flag2;
   }

   public ItemStack getItemToRender() {
      return this.itemToRender;
   }

   public void setItemToRender(ItemStack itemStack) {
      this.itemToRender = itemStack;
   }

   public EntityPlayerSPItemStackBooleanFloatIntEvent(boolean flag, EntityPlayerSP entityPlayerSP, ItemStack itemStack, ItemStack itemStack2, float f, float f2, int n) {
      this.flag2 = flag;
      this.entityPlayerSP = entityPlayerSP;
      this.currentItem = itemStack;
      this.itemToRender = itemStack2;
      this.prevEquippedProgress = f;
      this.equippedProgress = f2;
      this.equippedItemSlot = n;
   }

   public EntityPlayerSP getEntityPlayerSP() {
      return this.entityPlayerSP;
   }

   public void setPrevEquippedProgress(float f) {
      this.prevEquippedProgress = f;
   }

   public ItemStack getCurrentItem() {
      return this.currentItem;
   }

   public void setFlag3(boolean flag) {
      this.flag2 = flag;
   }
}
