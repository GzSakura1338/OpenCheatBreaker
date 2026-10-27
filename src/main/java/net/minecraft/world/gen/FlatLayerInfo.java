package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class FlatLayerInfo {
   public int layerCount = 1;
   public int intValue;
   public IBlockState layerMaterial;
   public int layerMinimumY;

   public FlatLayerInfo(int n, Block block) {
      this(3, n, block);
   }

   public FlatLayerInfo(int n, int height, Block layerMaterialIn) {
      this.intValue = n;
      this.layerCount = height;
      this.layerMaterial = layerMaterialIn.getDefaultState();
   }

   public FlatLayerInfo(int n, int n2, Block block, int n3) {
      this(n, n2, block);
      this.layerMaterial = block.getStateFromMeta(n3);
   }

   public int getLayerCount() {
      return this.layerCount;
   }

   public IBlockState getLayerMaterial() {
      return this.layerMaterial;
   }

   public Block getLayerMaterialBlock() {
      return this.layerMaterial.getBlock();
   }

   public int getFillBlockMeta() {
      return this.layerMaterial.getBlock().getMetaFromState(this.layerMaterial);
   }

   public int getMinY() {
      return this.layerMinimumY;
   }

   public void setMinY(int minY) {
      this.layerMinimumY = minY;
   }

   @Override
   public String toString() {
      String str;
      if (this.intValue >= 3) {
         ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(this.getLayerMaterialBlock());
         str = nameForObject == null ? "null" : nameForObject.toString();
         if (this.layerCount > 1) {
            str = this.layerCount + "*" + str;
         }
      } else {
         str = Integer.toString(Block.getIdFromBlock(this.getLayerMaterialBlock()));
         if (this.layerCount > 1) {
            str = this.layerCount + "x" + str;
         }
      }

      int fillBlockMeta = this.getFillBlockMeta();
      if (fillBlockMeta > 0) {
         str = str + ":" + fillBlockMeta;
      }

      return str;
   }
}
