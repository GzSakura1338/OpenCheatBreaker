package net.minecraft.block;

import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockCarrot extends BlockCrops {
   @Override
   public Item getSeed() {
      return Items.carrot;
   }

   @Override
   public Item getCrop() {
      return Items.carrot;
   }
}
