package net.optifine.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.init.Blocks;

public class ListQuadsOverlay {
   public List<BakedQuad> listQuads = new ArrayList<>();
   public List<IBlockState> listBlockStates = new ArrayList<>();
   public List<BakedQuad> listQuadsSingle = Arrays.asList();

   public void addQuad(BakedQuad bakedQuad, IBlockState blockState) {
      if (bakedQuad != null) {
         this.listQuads.add(bakedQuad);
         this.listBlockStates.add(blockState);
      }
   }

   public int size() {
      return this.listQuads.size();
   }

   public BakedQuad getQuad(int n) {
      return this.listQuads.get(n);
   }

   public IBlockState getBlockState(int n) {
      return n >= 0 && n < this.listBlockStates.size() ? this.listBlockStates.get(n) : Blocks.air.getDefaultState();
   }

   public List<BakedQuad> getListQuadsSingle(BakedQuad bakedQuad) {
      this.listQuadsSingle.set(0, bakedQuad);
      return this.listQuadsSingle;
   }

   public void clear() {
      this.listQuads.clear();
      this.listBlockStates.clear();
   }
}
