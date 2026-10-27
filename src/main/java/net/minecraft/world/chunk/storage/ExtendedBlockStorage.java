package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;
import net.optifine.reflect.Reflector;

public class ExtendedBlockStorage {
   public int yBase;
   public int blockRefCount;
   public int tickRefCount;
   public char[] data;
   public NibbleArray blocklightArray;
   public NibbleArray skylightArray;

   public ExtendedBlockStorage(int y, boolean storeSkylight) {
      this.yBase = y;
      this.data = new char[4096];
      this.blocklightArray = new NibbleArray();
      if (storeSkylight) {
         this.skylightArray = new NibbleArray();
      }
   }

   public IBlockState get(int x, int y, int z) {
      IBlockState byValue = Block.BLOCK_STATE_IDS.getByValue(this.data[y << 8 | z << 4 | x]);
      return byValue != null ? byValue : Blocks.air.getDefaultState();
   }

   public void set(int x, int y, int z, IBlockState state) {
      if (Reflector.IExtendedBlockState.isInstance(state)) {
         state = (IBlockState)Reflector.call(state, Reflector.IExtendedBlockState_getClean);
      }

      IBlockState blockState = this.get(x, y, z);
      Block block = blockState.getBlock();
      Block block2 = state.getBlock();
      if (block != Blocks.air) {
         this.blockRefCount--;
         if (block.getTickRandomly()) {
            this.tickRefCount--;
         }
      }

      if (block2 != Blocks.air) {
         this.blockRefCount++;
         if (block2.getTickRandomly()) {
            this.tickRefCount++;
         }
      }

      this.data[y << 8 | z << 4 | x] = (char)Block.BLOCK_STATE_IDS.get(state);
   }

   public Block getBlockByExtId(int x, int y, int z) {
      return this.get(x, y, z).getBlock();
   }

   public int getExtBlockMetadata(int x, int y, int z) {
      IBlockState blockState = this.get(x, y, z);
      return blockState.getBlock().getMetaFromState(blockState);
   }

   public boolean isEmpty() {
      return this.blockRefCount == 0;
   }

   public boolean getNeedsRandomTick() {
      return this.tickRefCount > 0;
   }

   public int getYLocation() {
      return this.yBase;
   }

   public void setExtSkylightValue(int x, int y, int z, int value) {
      this.skylightArray.set(x, y, z, value);
   }

   public int getExtSkylightValue(int x, int y, int z) {
      return this.skylightArray.get(x, y, z);
   }

   public void setExtBlocklightValue(int x, int y, int z, int value) {
      this.blocklightArray.set(x, y, z, value);
   }

   public int getExtBlocklightValue(int x, int y, int z) {
      return this.blocklightArray.get(x, y, z);
   }

   public void removeInvalidBlocks() {
      IBlockState defaultState = Blocks.air.getDefaultState();
      int n = 0;
      int n2 = 0;

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
               Block blockByExtId = this.getBlockByExtId(k, i, j);
               if (blockByExtId != Blocks.air) {
                  n++;
                  if (blockByExtId.getTickRandomly()) {
                     n2++;
                  }
               }
            }
         }
      }

      this.blockRefCount = n;
      this.tickRefCount = n2;
   }

   public char[] getData() {
      return this.data;
   }

   public void setData(char[] dataArray) {
      this.data = dataArray;
   }

   public NibbleArray getBlocklightArray() {
      return this.blocklightArray;
   }

   public NibbleArray getSkylightArray() {
      return this.skylightArray;
   }

   public void setBlocklightArray(NibbleArray newBlocklightArray) {
      this.blocklightArray = newBlocklightArray;
   }

   public void setSkylightArray(NibbleArray newSkylightArray) {
      this.skylightArray = newSkylightArray;
   }

   public int getBlockRefCount() {
      return this.blockRefCount;
   }
}
