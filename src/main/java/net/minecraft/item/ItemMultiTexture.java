package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;

public class ItemMultiTexture extends ItemBlock {
   public Block theBlock;
   public Function<ItemStack, String> nameFunction;

   public ItemMultiTexture(Block block, Block block2, Function<ItemStack, String> nameFunction) {
      super(block);
      this.theBlock = block2;
      this.nameFunction = nameFunction;
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public ItemMultiTexture(Block block, Block block2, final String[] namesByMeta) {
      this(block, block2, new Function<ItemStack, String>() {
         public String apply(ItemStack itemStack) {
            int metadata = itemStack.getMetadata();
            if (metadata < 0 || metadata >= namesByMeta.length) {
               metadata = 0;
            }

            return namesByMeta[metadata];
         }
      });
   }

   @Override
   public int getMetadata(int damage) {
      return damage;
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + "." + (String)this.nameFunction.apply(stack);
   }
}
