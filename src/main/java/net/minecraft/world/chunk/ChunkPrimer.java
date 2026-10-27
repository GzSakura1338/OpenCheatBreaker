package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ChunkPrimer {
   public short[] data = new short[65536];
   public IBlockState defaultState = Blocks.air.getDefaultState();

   public IBlockState getBlockState(int x, int y, int z) {
      int n = x << 12 | z << 8 | y;
      return this.getBlockState(n);
   }

   public IBlockState getBlockState(int index) {
      if (index >= 0 && index < this.data.length) {
         IBlockState byValue = Block.BLOCK_STATE_IDS.getByValue(this.data[index]);
         return byValue != null ? byValue : this.defaultState;
      } else {
         throw new IndexOutOfBoundsException("The coordinate is out of range");
      }
   }

   public void setBlockState(int x, int y, int z, IBlockState state) {
      int n = x << 12 | z << 8 | y;
      this.setBlockState(n, state);
   }

   public void setBlockState(int index, IBlockState state) {
      if (index >= 0 && index < this.data.length) {
         this.data[index] = (short)Block.BLOCK_STATE_IDS.get(state);
      } else {
         throw new IndexOutOfBoundsException("The coordinate is out of range");
      }
   }
}
