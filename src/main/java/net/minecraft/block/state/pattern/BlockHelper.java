package net.minecraft.block.state.pattern;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockHelper implements Predicate<IBlockState> {
   public Block block;

   public BlockHelper(Block blockType) {
      this.block = blockType;
   }

   public static BlockHelper forBlock(Block blockType) {
      return new BlockHelper(blockType);
   }

   public boolean apply(IBlockState blockState) {
      return blockState != null && blockState.getBlock() == this.block;
   }
}
