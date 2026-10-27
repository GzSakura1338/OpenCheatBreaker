package net.optifine.model;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.optifine.Config;
import org.lwjgl.util.vector.Vector3f;

public class BlockModelUtils {
   public static final float VERTEX_COORD_ACCURACY = 1.0E-6F;

   public static IBakedModel makeModelCube(String str, int n) {
      TextureAtlasSprite atlasSprite = Config.getMinecraft().getTextureMapBlocks().getAtlasSprite(str);
      return makeModelCube(atlasSprite, n);
   }

   public static IBakedModel makeModelCube(TextureAtlasSprite textureAtlasSprite, int n) {
      ArrayList list = new ArrayList();
      EnumFacing[] enumFacings = EnumFacing.VALUES;
      ArrayList list2 = new ArrayList();

      for (int i = 0; i < enumFacings.length; i++) {
         EnumFacing enumFacing = enumFacings[i];
         ArrayList list3 = new ArrayList();
         list3.add(makeBakedQuad(enumFacing, textureAtlasSprite, n));
         list2.add(list3);
      }

      return new SimpleBakedModel(list, list2, true, true, textureAtlasSprite, ItemCameraTransforms.DEFAULT);
   }

   public static IBakedModel joinModelsCube(IBakedModel bakedModel, IBakedModel bakedModel2) {
      ArrayList list = new ArrayList();
      list.addAll(bakedModel.getGeneralQuads());
      list.addAll(bakedModel2.getGeneralQuads());
      EnumFacing[] enumFacings = EnumFacing.VALUES;
      ArrayList list2 = new ArrayList();

      for (int i = 0; i < enumFacings.length; i++) {
         EnumFacing enumFacing = enumFacings[i];
         ArrayList list3 = new ArrayList();
         list3.addAll(bakedModel.getFaceQuads(enumFacing));
         list3.addAll(bakedModel2.getFaceQuads(enumFacing));
         list2.add(list3);
      }

      boolean isAmbientOcclusion = bakedModel.isAmbientOcclusion();
      boolean isBuiltInRenderer = bakedModel.isBuiltInRenderer();
      TextureAtlasSprite particleTexture = bakedModel.getParticleTexture();
      ItemCameraTransforms itemCameraTransforms = bakedModel.getItemCameraTransforms();
      return new SimpleBakedModel(list, list2, isAmbientOcclusion, isBuiltInRenderer, particleTexture, itemCameraTransforms);
   }

   public static BakedQuad makeBakedQuad(EnumFacing enumFacing, TextureAtlasSprite textureAtlasSprite, int n) {
      Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);
      Vector3f vector3f2 = new Vector3f(16.0F, 16.0F, 16.0F);
      BlockFaceUV blockFaceUV = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
      BlockPartFace blockPartFace = new BlockPartFace(enumFacing, n, "#" + enumFacing.getName(), blockFaceUV);
      ModelRotation modelRotation = ModelRotation.X0_Y0;
      Object obj = null;
      boolean flag = false;
      boolean flag2 = true;
      FaceBakery faceBakery = new FaceBakery();
      return faceBakery.makeBakedQuad(vector3f, vector3f2, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, (BlockPartRotation)obj, flag, flag2);
   }

   public static IBakedModel makeModel(String str, String str2, String str3) {
      TextureMap textureMapBlocks = Config.getMinecraft().getTextureMapBlocks();
      TextureAtlasSprite spriteSafe = textureMapBlocks.getSpriteSafe(str2);
      TextureAtlasSprite spriteSafe2 = textureMapBlocks.getSpriteSafe(str3);
      return makeModel(str, spriteSafe, spriteSafe2);
   }

   public static IBakedModel makeModel(String str, TextureAtlasSprite textureAtlasSprite, TextureAtlasSprite textureAtlasSprite2) {
      if (textureAtlasSprite != null && textureAtlasSprite2 != null) {
         ModelManager modelManager = Config.getModelManager();
         if (modelManager == null) {
            return null;
         }

         ModelResourceLocation modelResourceLocation = new ModelResourceLocation(str, "normal");
         IBakedModel model = modelManager.getModel(modelResourceLocation);
         if (model != null && model != modelManager.getMissingModel()) {
            IBakedModel duplicateModel = ModelUtils.duplicateModel(model);
            EnumFacing[] enumFacings = EnumFacing.VALUES;

            for (int i = 0; i < enumFacings.length; i++) {
               EnumFacing enumFacing = enumFacings[i];
               List faceQuads = duplicateModel.getFaceQuads(enumFacing);
               replaceTexture(faceQuads, textureAtlasSprite, textureAtlasSprite2);
            }

            List generalQuads = duplicateModel.getGeneralQuads();
            replaceTexture(generalQuads, textureAtlasSprite, textureAtlasSprite2);
            return duplicateModel;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static void replaceTexture(List<BakedQuad> list, TextureAtlasSprite textureAtlasSprite, TextureAtlasSprite textureAtlasSprite2) {
      ArrayList list2 = new ArrayList();

      for (BakedQuad bakedQuad : list) {
         if (bakedQuad.getSprite() == textureAtlasSprite) {
            bakedQuad = new BreakingFour(bakedQuad, textureAtlasSprite2);
         }

         list2.add(bakedQuad);
      }

      list.clear();
      list.addAll(list2);
   }

   public static void snapVertexPosition(Vector3f vector3f) {
      vector3f.setX(snapVertexCoord(vector3f.getX()));
      vector3f.setY(snapVertexCoord(vector3f.getY()));
      vector3f.setZ(snapVertexCoord(vector3f.getZ()));
   }

   public static float snapVertexCoord(float f) {
      return f > -1.0E-6F && f < 1.0E-6F ? 0.0F : (f > 0.999999F && f < 1.000001F ? 1.0F : f);
   }

   public static AxisAlignedBB getOffsetBoundingBox(AxisAlignedBB axisAlignedBB, EnumOffsetType enumOffsetType, BlockPos blockPos) {
      int n = blockPos.getX();
      int n2 = blockPos.getZ();
      long l = n * 3129871 ^ n2 * 116129781L;
      l = l * l * 42317861L + l * 11L;
      double d = ((float)(l >> 16 & 15L) / 15.0F - 0.5) * 0.5;
      double d2 = ((float)(l >> 24 & 15L) / 15.0F - 0.5) * 0.5;
      double d3 = 0.0;
      if (enumOffsetType == EnumOffsetType.XYZ) {
         d3 = ((float)(l >> 20 & 15L) / 15.0F - 1.0) * 0.2;
      }

      return axisAlignedBB.offset(d, d3, d2);
   }
}
