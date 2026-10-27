package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class SimpleBakedModel implements IBakedModel {
   public List<BakedQuad> generalQuads;
   public List<List<BakedQuad>> faceQuads;
   public boolean ambientOcclusion;
   public boolean gui3d;
   public TextureAtlasSprite texture;
   public ItemCameraTransforms cameraTransforms;

   public SimpleBakedModel(
      List<BakedQuad> generalQuadsIn,
      List<List<BakedQuad>> faceQuadsIn,
      boolean ambientOcclusionIn,
      boolean gui3dIn,
      TextureAtlasSprite textureIn,
      ItemCameraTransforms cameraTransformsIn
   ) {
      this.generalQuads = generalQuadsIn;
      this.faceQuads = faceQuadsIn;
      this.ambientOcclusion = ambientOcclusionIn;
      this.gui3d = gui3dIn;
      this.texture = textureIn;
      this.cameraTransforms = cameraTransformsIn;
   }

   @Override
   public List<BakedQuad> getFaceQuads(EnumFacing facing) {
      return this.faceQuads.get(facing.ordinal());
   }

   @Override
   public List<BakedQuad> getGeneralQuads() {
      return this.generalQuads;
   }

   @Override
   public boolean isAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   @Override
   public boolean isGui3d() {
      return this.gui3d;
   }

   @Override
   public boolean isBuiltInRenderer() {
      return false;
   }

   @Override
   public TextureAtlasSprite getParticleTexture() {
      return this.texture;
   }

   @Override
   public ItemCameraTransforms getItemCameraTransforms() {
      return this.cameraTransforms;
   }

   public static class Builder {
      public List<BakedQuad> builderGeneralQuads = Lists.newArrayList();
      public List<List<BakedQuad>> builderFaceQuads = Lists.newArrayListWithCapacity(6);
      public boolean builderAmbientOcclusion;
      public TextureAtlasSprite builderTexture;
      public boolean builderGui3d;
      public ItemCameraTransforms builderCameraTransforms;

      public Builder(ModelBlock model) {
         this(model.isAmbientOcclusion(), model.isGui3d(), model.getAllTransforms());
      }

      public Builder(IBakedModel bakedModel, TextureAtlasSprite texture) {
         this(bakedModel.isAmbientOcclusion(), bakedModel.isGui3d(), bakedModel.getItemCameraTransforms());
         this.builderTexture = bakedModel.getParticleTexture();

         for (EnumFacing enumFacing : EnumFacing.values()) {
            this.addFaceBreakingFours(bakedModel, texture, enumFacing);
         }

         this.addGeneralBreakingFours(bakedModel, texture);
      }

      public void addFaceBreakingFours(IBakedModel bakedModel, TextureAtlasSprite texture, EnumFacing facing) {
         for (BakedQuad bakedQuad : bakedModel.getFaceQuads(facing)) {
            this.addFaceQuad(facing, new BreakingFour(bakedQuad, texture));
         }
      }

      public void addGeneralBreakingFours(IBakedModel bakedModel, TextureAtlasSprite texture) {
         for (BakedQuad bakedQuad : bakedModel.getGeneralQuads()) {
            this.addGeneralQuad(new BreakingFour(bakedQuad, texture));
         }
      }

      public Builder(boolean ambientOcclusion, boolean gui3d, ItemCameraTransforms cameraTransforms) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            this.builderFaceQuads.add(Lists.newArrayList());
         }

         this.builderAmbientOcclusion = ambientOcclusion;
         this.builderGui3d = gui3d;
         this.builderCameraTransforms = cameraTransforms;
      }

      public SimpleBakedModel.Builder addFaceQuad(EnumFacing facing, BakedQuad quad) {
         this.builderFaceQuads.get(facing.ordinal()).add(quad);
         return this;
      }

      public SimpleBakedModel.Builder addGeneralQuad(BakedQuad quad) {
         this.builderGeneralQuads.add(quad);
         return this;
      }

      public SimpleBakedModel.Builder setTexture(TextureAtlasSprite texture) {
         this.builderTexture = texture;
         return this;
      }

      public IBakedModel makeBakedModel() {
         if (this.builderTexture == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(
               this.builderGeneralQuads,
               this.builderFaceQuads,
               this.builderAmbientOcclusion,
               this.builderGui3d,
               this.builderTexture,
               this.builderCameraTransforms
            );
         }
      }
   }
}
