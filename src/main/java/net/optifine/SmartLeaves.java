package net.optifine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.optifine.model.ModelUtils;

public class SmartLeaves {
   public static IBakedModel modelLeavesCullAcacia = null;
   public static IBakedModel modelLeavesCullBirch = null;
   public static IBakedModel modelLeavesCullDarkOak = null;
   public static IBakedModel modelLeavesCullJungle = null;
   public static IBakedModel modelLeavesCullOak = null;
   public static IBakedModel modelLeavesCullSpruce = null;
   public static List generalQuadsCullAcacia = null;
   public static List generalQuadsCullBirch = null;
   public static List generalQuadsCullDarkOak = null;
   public static List generalQuadsCullJungle = null;
   public static List generalQuadsCullOak = null;
   public static List generalQuadsCullSpruce = null;
   public static IBakedModel modelLeavesDoubleAcacia = null;
   public static IBakedModel modelLeavesDoubleBirch = null;
   public static IBakedModel modelLeavesDoubleDarkOak = null;
   public static IBakedModel modelLeavesDoubleJungle = null;
   public static IBakedModel modelLeavesDoubleOak = null;
   public static IBakedModel modelLeavesDoubleSpruce = null;

   public static IBakedModel getLeavesModel(IBakedModel bakedModel, IBlockState blockState) {
      if (!Config.isTreesSmart()) {
         return bakedModel;
      }

      List generalQuads = bakedModel.getGeneralQuads();
      return generalQuads == generalQuadsCullAcacia
         ? modelLeavesDoubleAcacia
         : (
            generalQuads == generalQuadsCullBirch
               ? modelLeavesDoubleBirch
               : (
                  generalQuads == generalQuadsCullDarkOak
                     ? modelLeavesDoubleDarkOak
                     : (
                        generalQuads == generalQuadsCullJungle
                           ? modelLeavesDoubleJungle
                           : (generalQuads == generalQuadsCullOak ? modelLeavesDoubleOak : (generalQuads == generalQuadsCullSpruce ? modelLeavesDoubleSpruce : bakedModel))
                     )
               )
         );
   }

   public static boolean isSameLeaves(IBlockState blockState, IBlockState blockState2) {
      if (blockState == blockState2) {
         return true;
      }

      Block block = blockState.getBlock();
      Block block2 = blockState2.getBlock();
      return block != block2
         ? false
         : (
            block instanceof BlockOldLeaf
               ? blockState.getValue(BlockOldLeaf.VARIANT).equals(blockState2.getValue(BlockOldLeaf.VARIANT))
               : (block instanceof BlockNewLeaf ? blockState.getValue(BlockNewLeaf.VARIANT).equals(blockState2.getValue(BlockNewLeaf.VARIANT)) : false)
         );
   }

   public static void updateLeavesModels() {
      ArrayList list = new ArrayList();
      modelLeavesCullAcacia = getModelCull("acacia", list);
      modelLeavesCullBirch = getModelCull("birch", list);
      modelLeavesCullDarkOak = getModelCull("dark_oak", list);
      modelLeavesCullJungle = getModelCull("jungle", list);
      modelLeavesCullOak = getModelCull("oak", list);
      modelLeavesCullSpruce = getModelCull("spruce", list);
      generalQuadsCullAcacia = getGeneralQuadsSafe(modelLeavesCullAcacia);
      generalQuadsCullBirch = getGeneralQuadsSafe(modelLeavesCullBirch);
      generalQuadsCullDarkOak = getGeneralQuadsSafe(modelLeavesCullDarkOak);
      generalQuadsCullJungle = getGeneralQuadsSafe(modelLeavesCullJungle);
      generalQuadsCullOak = getGeneralQuadsSafe(modelLeavesCullOak);
      generalQuadsCullSpruce = getGeneralQuadsSafe(modelLeavesCullSpruce);
      modelLeavesDoubleAcacia = getModelDoubleFace(modelLeavesCullAcacia);
      modelLeavesDoubleBirch = getModelDoubleFace(modelLeavesCullBirch);
      modelLeavesDoubleDarkOak = getModelDoubleFace(modelLeavesCullDarkOak);
      modelLeavesDoubleJungle = getModelDoubleFace(modelLeavesCullJungle);
      modelLeavesDoubleOak = getModelDoubleFace(modelLeavesCullOak);
      modelLeavesDoubleSpruce = getModelDoubleFace(modelLeavesCullSpruce);
      if (list.size() > 0) {
         Config.dbg("Enable face culling: " + Config.arrayToString(list.toArray()));
      }
   }

   public static List getGeneralQuadsSafe(IBakedModel bakedModel) {
      return bakedModel == null ? null : bakedModel.getGeneralQuads();
   }

   public static IBakedModel getModelCull(String str, List list) {
      ModelManager modelManager = Config.getModelManager();
      if (modelManager == null) {
         return null;
      }

      ResourceLocation resourceLocation = new ResourceLocation("blockstates/" + str + "_leaves.json");
      if (Config.getDefiningResourcePack(resourceLocation) != Config.getDefaultResourcePack()) {
         return null;
      }

      ResourceLocation resourceLocation2 = new ResourceLocation("models/block/" + str + "_leaves.json");
      if (Config.getDefiningResourcePack(resourceLocation2) != Config.getDefaultResourcePack()) {
         return null;
      }

      ModelResourceLocation modelResourceLocation = new ModelResourceLocation(str + "_leaves", "normal");
      IBakedModel model = modelManager.getModel(modelResourceLocation);
      if (model != null && model != modelManager.getMissingModel()) {
         List generalQuads = model.getGeneralQuads();
         if (generalQuads.size() == 0) {
            return model;
         }

         if (generalQuads.size() != 6) {
            return null;
         }

         for (Object obj : generalQuads) {
            BakedQuad bakedQuad = (BakedQuad)obj;
            List faceQuads = model.getFaceQuads(bakedQuad.getFace());
            if (faceQuads.size() > 0) {
               return null;
            }

            faceQuads.add(bakedQuad);
         }

         generalQuads.clear();
         list.add(str + "_leaves");
         return model;
      } else {
         return null;
      }
   }

   public static IBakedModel getModelDoubleFace(IBakedModel bakedModel) {
      if (bakedModel == null) {
         return null;
      }

      if (bakedModel.getGeneralQuads().size() > 0) {
         Config.warn("SmartLeaves: Model is not cube, general quads: " + bakedModel.getGeneralQuads().size() + ", model: " + bakedModel);
         return bakedModel;
      }

      EnumFacing[] enumFacings = EnumFacing.VALUES;

      for (int i = 0; i < enumFacings.length; i++) {
         EnumFacing enumFacing = enumFacings[i];
         List faceQuads = bakedModel.getFaceQuads(enumFacing);
         if (faceQuads.size() != 1) {
            Config.warn("SmartLeaves: Model is not cube, side: " + enumFacing + ", quads: " + faceQuads.size() + ", model: " + bakedModel);
            return bakedModel;
         }
      }

      IBakedModel duplicateModel = ModelUtils.duplicateModel(bakedModel);
      List[] lists = new List[enumFacings.length];

      for (int i = 0; i < enumFacings.length; i++) {
         EnumFacing enumFacing = enumFacings[i];
         List faceQuads = duplicateModel.getFaceQuads(enumFacing);
         BakedQuad bakedQuad = (BakedQuad)faceQuads.get(0);
         BakedQuad bakedQuad2 = new BakedQuad((int[])bakedQuad.getVertexData().clone(), bakedQuad.getTintIndex(), bakedQuad.getFace(), bakedQuad.getSprite());
         int[] vertexData = bakedQuad2.getVertexData();
         int[] ints = (int[])vertexData.clone();
         int n = vertexData.length / 4;
         System.arraycopy(vertexData, 0 * n, ints, 3 * n, n);
         System.arraycopy(vertexData, 1 * n, ints, 2 * n, n);
         System.arraycopy(vertexData, 2 * n, ints, 1 * n, n);
         System.arraycopy(vertexData, 3 * n, ints, 0 * n, n);
         System.arraycopy(ints, 0, vertexData, 0, ints.length);
         faceQuads.add(bakedQuad2);
      }

      return duplicateModel;
   }
}
