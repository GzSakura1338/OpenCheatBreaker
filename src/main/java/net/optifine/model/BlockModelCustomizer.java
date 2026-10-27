package net.optifine.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.optifine.BetterGrass;
import net.optifine.Config;
import net.optifine.ConnectedTextures;
import net.optifine.NaturalTextures;
import net.optifine.SmartLeaves;
import net.optifine.render.RenderEnv;

public class BlockModelCustomizer {
   public static List<BakedQuad> NO_QUADS = ImmutableList.of();

   public static IBakedModel getRenderModel(IBakedModel bakedModel, IBlockState blockState, RenderEnv renderEnv) {
      if (renderEnv.isSmartLeaves()) {
         bakedModel = SmartLeaves.getLeavesModel(bakedModel, blockState);
      }

      return bakedModel;
   }

   public static List<BakedQuad> getRenderQuads(
      List<BakedQuad> list, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing enumFacing, EnumWorldBlockLayer enumWorldBlockLayer, long l, RenderEnv renderEnv
   ) {
      if (enumFacing != null) {
         if (renderEnv.isSmartLeaves() && SmartLeaves.isSameLeaves(blockAccess.getBlockState(blockPos.offset(enumFacing)), blockState)) {
            return NO_QUADS;
         }

         if (!renderEnv.isBreakingAnimation(list) && Config.isBetterGrass()) {
            list = BetterGrass.getFaceQuads(blockAccess, blockState, blockPos, enumFacing, list);
         }
      }

      List listQuadsCustomizer = renderEnv.getListQuadsCustomizer();
      listQuadsCustomizer.clear();

      for (int i = 0; i < list.size(); i++) {
         BakedQuad bakedQuad = (BakedQuad)list.get(i);
         BakedQuad[] renderQuads = getRenderQuads(bakedQuad, blockAccess, blockState, blockPos, enumFacing, l, renderEnv);
         if (i == 0 && list.size() == 1 && renderQuads.length == 1 && renderQuads[0] == bakedQuad && bakedQuad.getQuadEmissive() == null) {
            return list;
         }

         for (int j = 0; j < renderQuads.length; j++) {
            BakedQuad bakedQuad2 = renderQuads[j];
            listQuadsCustomizer.add(bakedQuad2);
            if (bakedQuad2.getQuadEmissive() != null) {
               renderEnv.getListQuadsOverlay(getEmissiveLayer(enumWorldBlockLayer)).addQuad(bakedQuad2.getQuadEmissive(), blockState);
               renderEnv.setOverlaysRendered(true);
            }
         }
      }

      return listQuadsCustomizer;
   }

   public static EnumWorldBlockLayer getEmissiveLayer(EnumWorldBlockLayer enumWorldBlockLayer) {
      return enumWorldBlockLayer != null && enumWorldBlockLayer != EnumWorldBlockLayer.SOLID ? enumWorldBlockLayer : EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   public static BakedQuad[] getRenderQuads(BakedQuad bakedQuad, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing enumFacing, long l, RenderEnv renderEnv) {
      if (renderEnv.isBreakingAnimation(bakedQuad)) {
         return renderEnv.getArrayQuadsCtm(bakedQuad);
      }

      BakedQuad bakedQuad2 = bakedQuad;
      if (Config.isConnectedTextures()) {
         BakedQuad[] connectedTexture = ConnectedTextures.getConnectedTexture(blockAccess, blockState, blockPos, bakedQuad, renderEnv);
         if (connectedTexture.length != 1 || connectedTexture[0] != bakedQuad) {
            return connectedTexture;
         }
      }

      if (Config.isNaturalTextures()) {
         bakedQuad = NaturalTextures.getNaturalTexture(blockPos, bakedQuad);
         if (bakedQuad != bakedQuad2) {
            return renderEnv.getArrayQuadsCtm(bakedQuad);
         }
      }

      return renderEnv.getArrayQuadsCtm(bakedQuad);
   }
}
