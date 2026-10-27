package net.optifine.config;

import net.minecraft.block.state.BlockStateBase;
import net.optifine.Config;

public class MatchBlock {
   public int blockId = -1;
   public int[] metadatas = null;

   public MatchBlock(int n) {
      this.blockId = n;
   }

   public MatchBlock(int n, int n2) {
      this.blockId = n;
      if (n2 >= 0 && n2 <= 15) {
         this.metadatas = new int[]{n2};
      }
   }

   public MatchBlock(int n, int[] ints) {
      this.blockId = n;
      this.metadatas = ints;
   }

   public int getBlockId() {
      return this.blockId;
   }

   public int[] getMetadatas() {
      return this.metadatas;
   }

   public boolean matches(BlockStateBase blockStateBase) {
      return blockStateBase.getBlockId() != this.blockId ? false : Matches.metadata(blockStateBase.getMetadata(), this.metadatas);
   }

   public boolean matches(int n, int n2) {
      return n != this.blockId ? false : Matches.metadata(n2, this.metadatas);
   }

   public void addMetadata(int n) {
      if (this.metadatas != null && n >= 0 && n <= 15) {
         for (int i = 0; i < this.metadatas.length; i++) {
            if (this.metadatas[i] == n) {
               return;
            }
         }

         this.metadatas = Config.addIntToArray(this.metadatas, n);
      }
   }

   @Override
   public String toString() {
      return "" + this.blockId + ":" + Config.arrayToString(this.metadatas);
   }
}
