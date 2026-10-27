package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.world.ColorizerGrass;

public class ItemDoublePlant extends ItemMultiTexture {
   public ItemDoublePlant(Block block, Block block2, Function<ItemStack, String> nameFunction) {
      super(block, block2, nameFunction);
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      EnumPlantType byMetadata = EnumPlantType.byMetadata(stack.getMetadata());
      return byMetadata != EnumPlantType.GRASS && byMetadata != EnumPlantType.FERN
         ? super.getColorFromItemStack(stack, renderPass)
         : ColorizerGrass.getGrassColor(0.5, 1.0);
   }
}
