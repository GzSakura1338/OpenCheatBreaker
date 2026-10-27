package net.minecraft.client.resources.model;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandom.Item;

public class WeightedBakedModel implements IBakedModel {
   public List<WeightedBakedModel.MyWeighedRandomItem> models;
   public int totalWeight;
   public IBakedModel baseModel;

   public WeightedBakedModel(List<WeightedBakedModel.MyWeighedRandomItem> list) {
      this.models = list;
      this.totalWeight = WeightedRandom.getTotalWeight(list);
      this.baseModel = ((WeightedBakedModel.MyWeighedRandomItem)list.get(0)).model;
   }

   @Override
   public List<BakedQuad> getFaceQuads(EnumFacing facing) {
      return this.baseModel.getFaceQuads(facing);
   }

   @Override
   public List<BakedQuad> getGeneralQuads() {
      return this.baseModel.getGeneralQuads();
   }

   @Override
   public boolean isAmbientOcclusion() {
      return this.baseModel.isAmbientOcclusion();
   }

   @Override
   public boolean isGui3d() {
      return this.baseModel.isGui3d();
   }

   @Override
   public boolean isBuiltInRenderer() {
      return this.baseModel.isBuiltInRenderer();
   }

   @Override
   public TextureAtlasSprite getParticleTexture() {
      return this.baseModel.getParticleTexture();
   }

   @Override
   public ItemCameraTransforms getItemCameraTransforms() {
      return this.baseModel.getItemCameraTransforms();
   }

   public IBakedModel getAlternativeModel(long l) {
      return WeightedRandom.getRandomItem(this.models, Math.abs((int)l >> 16) % this.totalWeight).model;
   }

   public static class Builder {
      public List<WeightedBakedModel.MyWeighedRandomItem> listItems = Lists.newArrayList();

      public WeightedBakedModel.Builder add(IBakedModel bakedModel, int n) {
         this.listItems.add(new WeightedBakedModel.MyWeighedRandomItem(bakedModel, n));
         return this;
      }

      public WeightedBakedModel build() {
         Collections.sort(this.listItems);
         return new WeightedBakedModel(this.listItems);
      }

      public IBakedModel first() {
         return this.listItems.get(0).model;
      }
   }

   static class MyWeighedRandomItem extends Item implements Comparable<WeightedBakedModel.MyWeighedRandomItem> {
      public IBakedModel model;

      public MyWeighedRandomItem(IBakedModel bakedModel, int n) {
         super(n);
         this.model = bakedModel;
      }

      public int compareTo(WeightedBakedModel.MyWeighedRandomItem myWeighedRandomItem) {
         return ComparisonChain.start().compare(myWeighedRandomItem.itemWeight, this.itemWeight).compare(this.getCountQuads(), myWeighedRandomItem.getCountQuads()).result();
      }

      public int getCountQuads() {
         int size = this.model.getGeneralQuads().size();

         for (EnumFacing enumFacing : EnumFacing.values()) {
            size += this.model.getFaceQuads(enumFacing).size();
         }

         return size;
      }

      @Override
      public String toString() {
         return "MyWeighedRandomItem{weight=" + this.itemWeight + ", model=" + this.model + '}';
      }
   }
}
