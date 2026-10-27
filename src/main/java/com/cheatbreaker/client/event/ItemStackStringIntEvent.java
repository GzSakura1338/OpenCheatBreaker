package com.cheatbreaker.client.event;

import net.minecraft.item.ItemStack;

public class ItemStackStringIntEvent extends CancellableEvent {
   public String text;
   public ItemStack itemStack;
   public int intValue;

   public void setIntValue(int n) {
      this.intValue = n;
   }

   public String getText() {
      return this.text;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void setText(String str) {
      this.text = str;
   }

   public ItemStackStringIntEvent(String str, ItemStack itemStack, int n) {
      this.text = str;
      this.itemStack = itemStack;
      this.intValue = n;
   }

   public void setItemStack(ItemStack itemStack) {
      this.itemStack = itemStack;
   }
}
