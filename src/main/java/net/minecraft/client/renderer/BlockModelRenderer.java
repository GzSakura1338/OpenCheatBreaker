package net.minecraft.client.renderer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.BlockIntEvent;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.optifine.BetterSnow;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.model.BlockModelCustomizer;
import net.optifine.model.ListQuadsOverlay;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.shaders.Shaders;

public class BlockModelRenderer {
   public static float aoLightValueOpaque = 0.2F;
   public static boolean separateAoLightValue = false;
   public static EnumWorldBlockLayer[] OVERLAY_LAYERS = new EnumWorldBlockLayer[]{
      EnumWorldBlockLayer.CUTOUT, EnumWorldBlockLayer.CUTOUT_MIPPED, EnumWorldBlockLayer.TRANSLUCENT
   };

   public BlockModelRenderer() {
      if (Reflector.ForgeModContainer_forgeLightPipelineEnabled.exists()) {
         Reflector.setFieldValue(Reflector.ForgeModContainer_forgeLightPipelineEnabled, false);
      }
   }

   public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn) {
      Block block = blockStateIn.getBlock();
      block.setBlockBoundsBasedOnState(blockAccessIn, blockPosIn);
      return this.renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, true);
   }

   public boolean renderModel(
      IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides
   ) {
      boolean flag = Minecraft.isAmbientOcclusionEnabled() && blockStateIn.getBlock().getLightValue() == 0 && modelIn.isAmbientOcclusion();
      BlockIntEvent blockIntEvent = new BlockIntEvent(blockStateIn.getBlock(), blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ());
      Optional.ofNullable(CheatBreaker.getInstance()).ifPresent(cheatBreaker -> cheatBreaker.getEventBus().handleEvent(blockIntEvent));
      if (blockIntEvent.isFlag()) {
         return false;
      }

      try {
         if (Config.isShaders()) {
            SVertexBuilder.pushEntity(blockStateIn, blockPosIn, blockAccessIn, worldRendererIn);
         }

         RenderEnv renderEnv = worldRendererIn.getRenderEnv(blockStateIn, blockPosIn);
         modelIn = BlockModelCustomizer.getRenderModel(modelIn, blockStateIn, renderEnv);
         boolean flag2 = flag
            ? this.renderModelSmooth(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides)
            : this.renderModelFlat(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides);
         if (flag2) {
            this.renderOverlayModels(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, checkSides, 0L, renderEnv, flag);
         }

         if (Config.isShaders()) {
            SVertexBuilder.popEntity(worldRendererIn);
         }

         return flag2;
      } catch (Throwable e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Tesselating block model");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block model being tesselated");
         CrashReportCategory.addBlockInfo(makeCategory, blockPosIn, blockStateIn);
         makeCategory.addCrashSection("Using AO", flag);
         throw new ReportedException(makeCrashReport);
      }
   }

   public boolean renderModelAmbientOcclusion(
      IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides
   ) {
      IBlockState blockState = blockAccessIn.getBlockState(blockPosIn);
      return this.renderModelSmooth(blockAccessIn, modelIn, blockState, blockPosIn, worldRendererIn, checkSides);
   }

   public boolean renderModelSmooth(IBlockAccess blockAccess, IBakedModel bakedModel, IBlockState blockState, BlockPos blockPos, WorldRenderer worldRenderer, boolean flag) {
      boolean flag2 = false;
      Block block = blockState.getBlock();
      RenderEnv renderEnv = worldRenderer.getRenderEnv(blockState, blockPos);
      EnumWorldBlockLayer blockLayer = worldRenderer.getBlockLayer();

      for (EnumFacing enumFacing : EnumFacing.VALUES) {
         List faceQuads = bakedModel.getFaceQuads(enumFacing);
         if (!faceQuads.isEmpty()) {
            BlockPos offset = blockPos.offset(enumFacing);
            if (!flag || block.shouldSideBeRendered(blockAccess, offset, enumFacing)) {
               faceQuads = BlockModelCustomizer.getRenderQuads(faceQuads, blockAccess, blockState, blockPos, enumFacing, blockLayer, 0L, renderEnv);
               this.renderQuadsSmooth(blockAccess, blockState, blockPos, worldRenderer, faceQuads, renderEnv);
               flag2 = true;
            }
         }
      }

      List generalQuads = bakedModel.getGeneralQuads();
      if (generalQuads.size() > 0) {
         generalQuads = BlockModelCustomizer.getRenderQuads(generalQuads, blockAccess, blockState, blockPos, (EnumFacing)null, blockLayer, 0L, renderEnv);
         this.renderQuadsSmooth(blockAccess, blockState, blockPos, worldRenderer, generalQuads, renderEnv);
         flag2 = true;
      }

      return flag2;
   }

   public boolean renderModelStandard(
      IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides
   ) {
      IBlockState blockState = blockAccessIn.getBlockState(blockPosIn);
      return this.renderModelFlat(blockAccessIn, modelIn, blockState, blockPosIn, worldRendererIn, checkSides);
   }

   public boolean renderModelFlat(IBlockAccess blockAccess, IBakedModel bakedModel, IBlockState blockState, BlockPos blockPos, WorldRenderer worldRenderer, boolean flag) {
      boolean flag2 = false;
      Block block = blockState.getBlock();
      RenderEnv renderEnv = worldRenderer.getRenderEnv(blockState, blockPos);
      EnumWorldBlockLayer blockLayer = worldRenderer.getBlockLayer();

      for (EnumFacing enumFacing : EnumFacing.VALUES) {
         List faceQuads = bakedModel.getFaceQuads(enumFacing);
         if (!faceQuads.isEmpty()) {
            BlockPos offset = blockPos.offset(enumFacing);
            if (!flag || block.shouldSideBeRendered(blockAccess, offset, enumFacing)) {
               int mixedBrightnessForBlock = block.getMixedBrightnessForBlock(blockAccess, offset);
               faceQuads = BlockModelCustomizer.getRenderQuads(faceQuads, blockAccess, blockState, blockPos, enumFacing, blockLayer, 0L, renderEnv);
               this.renderQuadsFlat(blockAccess, blockState, blockPos, enumFacing, mixedBrightnessForBlock, false, worldRenderer, faceQuads, renderEnv);
               flag2 = true;
            }
         }
      }

      List generalQuads = bakedModel.getGeneralQuads();
      if (generalQuads.size() > 0) {
         generalQuads = BlockModelCustomizer.getRenderQuads(generalQuads, blockAccess, blockState, blockPos, (EnumFacing)null, blockLayer, 0L, renderEnv);
         this.renderQuadsFlat(blockAccess, blockState, blockPos, (EnumFacing)null, -1, true, worldRenderer, generalQuads, renderEnv);
         flag2 = true;
      }

      return flag2;
   }

   public void renderQuadsSmooth(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, WorldRenderer worldRenderer, List<BakedQuad> list, RenderEnv renderEnv) {
      Block block = blockState.getBlock();
      float[] quadBounds = renderEnv.getQuadBounds();
      BitSet boundsFlags = renderEnv.getBoundsFlags();
      BlockModelRenderer.AmbientOcclusionFace aoFace = renderEnv.getAoFace();
      double d = blockPos.getX();
      double d2 = blockPos.getY();
      double d3 = blockPos.getZ();
      EnumOffsetType offsetType = block.getOffsetType();
      if (offsetType != EnumOffsetType.NONE) {
         long positionRandom = MathHelper.getPositionRandom(blockPos);
         d += ((float)(positionRandom >> 16 & 15L) / 15.0F - 0.5) * 0.5;
         d3 += ((float)(positionRandom >> 24 & 15L) / 15.0F - 0.5) * 0.5;
         if (offsetType == EnumOffsetType.XYZ) {
            d2 += ((float)(positionRandom >> 20 & 15L) / 15.0F - 1.0) * 0.2;
         }
      }

      for (BakedQuad bakedQuad : list) {
         this.fillQuadBounds(block, bakedQuad.getVertexData(), bakedQuad.getFace(), quadBounds, boundsFlags);
         aoFace.updateVertexBrightness(blockAccess, block, blockPos, bakedQuad.getFace(), quadBounds, boundsFlags);
         if (bakedQuad.getSprite().isEmissive) {
            aoFace.setMaxBlockLight();
         }

         if (worldRenderer.isMultiTexture()) {
            worldRenderer.addVertexData(bakedQuad.getVertexDataSingle());
         } else {
            worldRenderer.addVertexData(bakedQuad.getVertexData());
         }

         worldRenderer.putSprite(bakedQuad.getSprite());
         worldRenderer.putBrightness4(aoFace.vertexBrightness[0], aoFace.vertexBrightness[1], aoFace.vertexBrightness[2], aoFace.vertexBrightness[3]);
         int colorMultiplier = CustomColors.getColorMultiplier(bakedQuad, blockState, blockAccess, blockPos, renderEnv);
         if (!bakedQuad.hasTintIndex() && colorMultiplier == -1) {
            if (separateAoLightValue) {
               worldRenderer.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, aoFace.vertexColorMultiplier[0], 4);
               worldRenderer.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, aoFace.vertexColorMultiplier[1], 3);
               worldRenderer.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, aoFace.vertexColorMultiplier[2], 2);
               worldRenderer.putColorMultiplierRgba(1.0F, 1.0F, 1.0F, aoFace.vertexColorMultiplier[3], 1);
            } else {
               worldRenderer.putColorMultiplier(aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], 4);
               worldRenderer.putColorMultiplier(aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], 3);
               worldRenderer.putColorMultiplier(aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], 2);
               worldRenderer.putColorMultiplier(aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], 1);
            }
         } else {
            int n;
            if (colorMultiplier != -1) {
               n = colorMultiplier;
            } else {
               n = block.colorMultiplier(blockAccess, blockPos, bakedQuad.getTintIndex());
            }

            if (EntityRenderer.anaglyphEnable) {
               n = TextureUtil.anaglyphColor(n);
            }

            float f = (n >> 16 & 0xFF) / 255.0F;
            float f2 = (n >> 8 & 0xFF) / 255.0F;
            float f3 = (n & 0xFF) / 255.0F;
            if (separateAoLightValue) {
               worldRenderer.putColorMultiplierRgba(f, f2, f3, aoFace.vertexColorMultiplier[0], 4);
               worldRenderer.putColorMultiplierRgba(f, f2, f3, aoFace.vertexColorMultiplier[1], 3);
               worldRenderer.putColorMultiplierRgba(f, f2, f3, aoFace.vertexColorMultiplier[2], 2);
               worldRenderer.putColorMultiplierRgba(f, f2, f3, aoFace.vertexColorMultiplier[3], 1);
            } else {
               worldRenderer.putColorMultiplier(
                  aoFace.vertexColorMultiplier[0] * f, aoFace.vertexColorMultiplier[0] * f2, aoFace.vertexColorMultiplier[0] * f3, 4
               );
               worldRenderer.putColorMultiplier(
                  aoFace.vertexColorMultiplier[1] * f, aoFace.vertexColorMultiplier[1] * f2, aoFace.vertexColorMultiplier[1] * f3, 3
               );
               worldRenderer.putColorMultiplier(
                  aoFace.vertexColorMultiplier[2] * f, aoFace.vertexColorMultiplier[2] * f2, aoFace.vertexColorMultiplier[2] * f3, 2
               );
               worldRenderer.putColorMultiplier(
                  aoFace.vertexColorMultiplier[3] * f, aoFace.vertexColorMultiplier[3] * f2, aoFace.vertexColorMultiplier[3] * f3, 1
               );
            }
         }

         worldRenderer.putPosition(d, d2, d3);
      }
   }

   public void fillQuadBounds(Block blockIn, int[] vertexData, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags) {
      float f = 32.0F;
      float f2 = 32.0F;
      float f3 = 32.0F;
      float f4 = -32.0F;
      float f5 = -32.0F;
      float f6 = -32.0F;
      int n = vertexData.length / 4;

      for (int i = 0; i < 4; i++) {
         float intBitsToFloat = Float.intBitsToFloat(vertexData[i * n]);
         float intBitsToFloat2 = Float.intBitsToFloat(vertexData[i * n + 1]);
         float intBitsToFloat3 = Float.intBitsToFloat(vertexData[i * n + 2]);
         f = Math.min(f, intBitsToFloat);
         f2 = Math.min(f2, intBitsToFloat2);
         f3 = Math.min(f3, intBitsToFloat3);
         f4 = Math.max(f4, intBitsToFloat);
         f5 = Math.max(f5, intBitsToFloat2);
         f6 = Math.max(f6, intBitsToFloat3);
      }

      if (quadBounds != null) {
         quadBounds[EnumFacing.WEST.getIndex()] = f;
         quadBounds[EnumFacing.EAST.getIndex()] = f4;
         quadBounds[EnumFacing.DOWN.getIndex()] = f2;
         quadBounds[EnumFacing.UP.getIndex()] = f5;
         quadBounds[EnumFacing.NORTH.getIndex()] = f3;
         quadBounds[EnumFacing.SOUTH.getIndex()] = f6;
         int length = EnumFacing.VALUES.length;
         quadBounds[EnumFacing.WEST.getIndex() + length] = 1.0F - f;
         quadBounds[EnumFacing.EAST.getIndex() + length] = 1.0F - f4;
         quadBounds[EnumFacing.DOWN.getIndex() + length] = 1.0F - f2;
         quadBounds[EnumFacing.UP.getIndex() + length] = 1.0F - f5;
         quadBounds[EnumFacing.NORTH.getIndex() + length] = 1.0F - f3;
         quadBounds[EnumFacing.SOUTH.getIndex() + length] = 1.0F - f6;
      }

      float f7 = 1.0E-4F;
      float f8 = 0.9999F;
      switch (facingIn) {
         case DOWN:
            boundsFlags.set(1, f >= 1.0E-4F || f3 >= 1.0E-4F || f4 <= 0.9999F || f6 <= 0.9999F);
            boundsFlags.set(0, (f2 < 1.0E-4F || blockIn.isFullCube()) && f2 == f5);
            break;
         case UP:
            boundsFlags.set(1, f >= 1.0E-4F || f3 >= 1.0E-4F || f4 <= 0.9999F || f6 <= 0.9999F);
            boundsFlags.set(0, (f5 > 0.9999F || blockIn.isFullCube()) && f2 == f5);
            break;
         case NORTH:
            boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
            boundsFlags.set(0, (f3 < 1.0E-4F || blockIn.isFullCube()) && f3 == f6);
            break;
         case SOUTH:
            boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
            boundsFlags.set(0, (f6 > 0.9999F || blockIn.isFullCube()) && f3 == f6);
            break;
         case WEST:
            boundsFlags.set(1, f2 >= 1.0E-4F || f3 >= 1.0E-4F || f5 <= 0.9999F || f6 <= 0.9999F);
            boundsFlags.set(0, (f < 1.0E-4F || blockIn.isFullCube()) && f == f4);
            break;
         case EAST:
            boundsFlags.set(1, f2 >= 1.0E-4F || f3 >= 1.0E-4F || f5 <= 0.9999F || f6 <= 0.9999F);
            boundsFlags.set(0, (f4 > 0.9999F || blockIn.isFullCube()) && f == f4);
      }
   }

   public void renderQuadsFlat(
      IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing enumFacing, int n, boolean flag, WorldRenderer worldRenderer, List<BakedQuad> list, RenderEnv renderEnv
   ) {
      Block block = blockState.getBlock();
      BitSet boundsFlags = renderEnv.getBoundsFlags();
      double d = blockPos.getX();
      double d2 = blockPos.getY();
      double d3 = blockPos.getZ();
      EnumOffsetType offsetType = block.getOffsetType();
      if (offsetType != EnumOffsetType.NONE) {
         int n2 = blockPos.getX();
         int n3 = blockPos.getZ();
         long l = n2 * 3129871 ^ n3 * 116129781L;
         l = l * l * 42317861L + l * 11L;
         d += ((float)(l >> 16 & 15L) / 15.0F - 0.5) * 0.5;
         d3 += ((float)(l >> 24 & 15L) / 15.0F - 0.5) * 0.5;
         if (offsetType == EnumOffsetType.XYZ) {
            d2 += ((float)(l >> 20 & 15L) / 15.0F - 1.0) * 0.2;
         }
      }

      for (BakedQuad bakedQuad : list) {
         if (flag) {
            this.fillQuadBounds(block, bakedQuad.getVertexData(), bakedQuad.getFace(), (float[])null, boundsFlags);
            n = boundsFlags.get(0) ? block.getMixedBrightnessForBlock(blockAccess, blockPos.offset(bakedQuad.getFace())) : block.getMixedBrightnessForBlock(blockAccess, blockPos);
         }

         if (bakedQuad.getSprite().isEmissive) {
            n |= 240;
         }

         if (worldRenderer.isMultiTexture()) {
            worldRenderer.addVertexData(bakedQuad.getVertexDataSingle());
         } else {
            worldRenderer.addVertexData(bakedQuad.getVertexData());
         }

         worldRenderer.putSprite(bakedQuad.getSprite());
         worldRenderer.putBrightness4(n, n, n, n);
         int colorMultiplier = CustomColors.getColorMultiplier(bakedQuad, blockState, blockAccess, blockPos, renderEnv);
         if (bakedQuad.hasTintIndex() || colorMultiplier != -1) {
            int n2;
            if (colorMultiplier != -1) {
               n2 = colorMultiplier;
            } else {
               n2 = block.colorMultiplier(blockAccess, blockPos, bakedQuad.getTintIndex());
            }

            if (EntityRenderer.anaglyphEnable) {
               n2 = TextureUtil.anaglyphColor(n2);
            }

            float f = (n2 >> 16 & 0xFF) / 255.0F;
            float f2 = (n2 >> 8 & 0xFF) / 255.0F;
            float f3 = (n2 & 0xFF) / 255.0F;
            worldRenderer.putColorMultiplier(f, f2, f3, 4);
            worldRenderer.putColorMultiplier(f, f2, f3, 3);
            worldRenderer.putColorMultiplier(f, f2, f3, 2);
            worldRenderer.putColorMultiplier(f, f2, f3, 1);
         }

         worldRenderer.putPosition(d, d2, d3);
      }
   }

   public void renderModelBrightnessColor(IBakedModel bakedModel, float f, float red, float green, float blue) {
      for (EnumFacing enumFacing : EnumFacing.VALUES) {
         this.renderModelBrightnessColorQuads(f, red, green, blue, bakedModel.getFaceQuads(enumFacing));
      }

      this.renderModelBrightnessColorQuads(f, red, green, blue, bakedModel.getGeneralQuads());
   }

   public void renderModelBrightness(IBakedModel model, IBlockState blockState, float brightness, boolean flag) {
      Block block = blockState.getBlock();
      block.setBlockBoundsForItemRender();
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      int renderColor = block.getRenderColor(block.getStateForEntityRender(blockState));
      if (EntityRenderer.anaglyphEnable) {
         renderColor = TextureUtil.anaglyphColor(renderColor);
      }

      float f = (renderColor >> 16 & 0xFF) / 255.0F;
      float f2 = (renderColor >> 8 & 0xFF) / 255.0F;
      float f3 = (renderColor & 0xFF) / 255.0F;
      if (!flag) {
         GlStateManager.color(brightness, brightness, brightness, 1.0F);
      }

      this.renderModelBrightnessColor(model, brightness, f, f2, f3);
   }

   public void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();

      for (BakedQuad bakedQuad : listQuads) {
         worldRenderer.begin(7, DefaultVertexFormats.ITEM);
         worldRenderer.addVertexData(bakedQuad.getVertexData());
         worldRenderer.putSprite(bakedQuad.getSprite());
         if (bakedQuad.hasTintIndex()) {
            worldRenderer.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
         } else {
            worldRenderer.putColorRGB_F4(brightness, brightness, brightness);
         }

         Vec3i directionVec = bakedQuad.getFace().getDirectionVec();
         worldRenderer.putNormal(directionVec.getX(), directionVec.getY(), directionVec.getZ());
         instance.draw();
      }
   }

   public static float fixAoLightValue(float f) {
      return f == 0.2F ? aoLightValueOpaque : f;
   }

   public static void updateAoLightValue() {
      aoLightValueOpaque = 1.0F - Config.getAmbientOcclusionLevel() * 0.8F;
      separateAoLightValue = Config.isShaders() && Shaders.isSeparateAo();
   }

   public void renderOverlayModels(
      IBlockAccess blockAccess, IBakedModel bakedModel, IBlockState blockState, BlockPos blockPos, WorldRenderer worldRenderer, boolean flag, long l, RenderEnv renderEnv, boolean flag2
   ) {
      if (renderEnv.isOverlaysRendered()) {
         for (int i = 0; i < OVERLAY_LAYERS.length; i++) {
            EnumWorldBlockLayer enumWorldBlockLayer = OVERLAY_LAYERS[i];
            ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(enumWorldBlockLayer);
            if (listQuadsOverlay.size() > 0) {
               RegionRenderCacheBuilder regionRenderCacheBuilder = renderEnv.getRegionRenderCacheBuilder();
               if (regionRenderCacheBuilder != null) {
                  WorldRenderer worldRendererByLayer = regionRenderCacheBuilder.getWorldRendererByLayer(enumWorldBlockLayer);
                  if (!worldRendererByLayer.isDrawing()) {
                     worldRendererByLayer.begin(7, DefaultVertexFormats.BLOCK);
                     worldRendererByLayer.setTranslation(worldRenderer.getXOffset(), worldRenderer.getYOffset(), worldRenderer.getZOffset());
                  }

                  for (int j = 0; j < listQuadsOverlay.size(); j++) {
                     BakedQuad quad = listQuadsOverlay.getQuad(j);
                     List listQuadsSingle = listQuadsOverlay.getListQuadsSingle(quad);
                     IBlockState blockState2 = listQuadsOverlay.getBlockState(j);
                     if (quad.getQuadEmissive() != null) {
                        listQuadsOverlay.addQuad(quad.getQuadEmissive(), blockState2);
                     }

                     renderEnv.reset(blockState2, blockPos);
                     if (flag2) {
                        this.renderQuadsSmooth(blockAccess, blockState2, blockPos, worldRendererByLayer, listQuadsSingle, renderEnv);
                     } else {
                        int mixedBrightnessForBlock = blockState2.getBlock().getMixedBrightnessForBlock(blockAccess, blockPos.offset(quad.getFace()));
                        this.renderQuadsFlat(blockAccess, blockState2, blockPos, quad.getFace(), mixedBrightnessForBlock, false, worldRendererByLayer, listQuadsSingle, renderEnv);
                     }
                  }
               }

               listQuadsOverlay.clear();
            }
         }
      }

      if (Config.isBetterSnow() && !renderEnv.isBreakingAnimation() && BetterSnow.shouldRender(blockAccess, blockState, blockPos)) {
         IBakedModel modelSnowLayer = BetterSnow.getModelSnowLayer();
         IBlockState stateSnowLayer = BetterSnow.getStateSnowLayer();
         this.renderModel(blockAccess, modelSnowLayer, stateSnowLayer, blockPos, worldRenderer, flag);
      }
   }

   public static class AmbientOcclusionFace {
      public float[] vertexColorMultiplier = new float[4];
      public int[] vertexBrightness = new int[4];

      public AmbientOcclusionFace() {
         this((BlockModelRenderer)null);
      }

      public AmbientOcclusionFace(BlockModelRenderer blockModelRenderer) {
      }

      public void setMaxBlockLight() {
         short s = 240;
         this.vertexBrightness[0] = this.vertexBrightness[0] | s;
         this.vertexBrightness[1] = this.vertexBrightness[1] | s;
         this.vertexBrightness[2] = this.vertexBrightness[2] | s;
         this.vertexBrightness[3] = this.vertexBrightness[3] | s;
         this.vertexColorMultiplier[0] = 1.0F;
         this.vertexColorMultiplier[1] = 1.0F;
         this.vertexColorMultiplier[2] = 1.0F;
         this.vertexColorMultiplier[3] = 1.0F;
      }

      public void updateVertexBrightness(
         IBlockAccess blockAccessIn, Block blockIn, BlockPos blockPosIn, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags
      ) {
         BlockPos blockPos = boundsFlags.get(0) ? blockPosIn.offset(facingIn) : blockPosIn;
         BlockModelRenderer.EnumNeighborInfo neighbourInfo = BlockModelRenderer.EnumNeighborInfo.getNeighbourInfo(facingIn);
         BlockPos offset = blockPos.offset(neighbourInfo.enumFacingArray[0]);
         BlockPos offset2 = blockPos.offset(neighbourInfo.enumFacingArray[1]);
         BlockPos offset3 = blockPos.offset(neighbourInfo.enumFacingArray[2]);
         BlockPos offset4 = blockPos.offset(neighbourInfo.enumFacingArray[3]);
         int mixedBrightnessForBlock = blockIn.getMixedBrightnessForBlock(blockAccessIn, offset);
         int mixedBrightnessForBlock2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, offset2);
         int mixedBrightnessForBlock3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, offset3);
         int mixedBrightnessForBlock4 = blockIn.getMixedBrightnessForBlock(blockAccessIn, offset4);
         float fixAoLightValue = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(offset).getBlock().getAmbientOcclusionLightValue());
         float fixAoLightValue2 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(offset2).getBlock().getAmbientOcclusionLightValue());
         float fixAoLightValue3 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(offset3).getBlock().getAmbientOcclusionLightValue());
         float fixAoLightValue4 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(offset4).getBlock().getAmbientOcclusionLightValue());
         boolean isTranslucent = blockAccessIn.getBlockState(offset.offset(facingIn)).getBlock().isTranslucent();
         boolean isTranslucent2 = blockAccessIn.getBlockState(offset2.offset(facingIn)).getBlock().isTranslucent();
         boolean isTranslucent3 = blockAccessIn.getBlockState(offset3.offset(facingIn)).getBlock().isTranslucent();
         boolean isTranslucent4 = blockAccessIn.getBlockState(offset4.offset(facingIn)).getBlock().isTranslucent();
         float f;
         int n;
         if (!isTranslucent3 && !isTranslucent) {
            f = fixAoLightValue;
            n = mixedBrightnessForBlock;
         } else {
            BlockPos offset5 = offset.offset(neighbourInfo.enumFacingArray[2]);
            f = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(offset5).getBlock().getAmbientOcclusionLightValue());
            n = blockIn.getMixedBrightnessForBlock(blockAccessIn, offset5);
         }

         float f2;
         int n2;
         if (!isTranslucent4 && !isTranslucent) {
            f2 = fixAoLightValue;
            n2 = mixedBrightnessForBlock;
         } else {
            BlockPos offset5 = offset.offset(neighbourInfo.enumFacingArray[3]);
            f2 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(offset5).getBlock().getAmbientOcclusionLightValue());
            n2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, offset5);
         }

         float f3;
         int n3;
         if (!isTranslucent3 && !isTranslucent2) {
            f3 = fixAoLightValue2;
            n3 = mixedBrightnessForBlock2;
         } else {
            BlockPos offset5 = offset2.offset(neighbourInfo.enumFacingArray[2]);
            f3 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(offset5).getBlock().getAmbientOcclusionLightValue());
            n3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, offset5);
         }

         float f4;
         int n4;
         if (!isTranslucent4 && !isTranslucent2) {
            f4 = fixAoLightValue2;
            n4 = mixedBrightnessForBlock2;
         } else {
            BlockPos offset5 = offset2.offset(neighbourInfo.enumFacingArray[3]);
            f4 = BlockModelRenderer.fixAoLightValue(blockAccessIn.getBlockState(offset5).getBlock().getAmbientOcclusionLightValue());
            n4 = blockIn.getMixedBrightnessForBlock(blockAccessIn, offset5);
         }

         int mixedBrightnessForBlock5 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn);
         if (boundsFlags.get(0) || !blockAccessIn.getBlockState(blockPosIn.offset(facingIn)).getBlock().isOpaqueCube()) {
            mixedBrightnessForBlock5 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(facingIn));
         }

         float f5 = boundsFlags.get(0)
            ? blockAccessIn.getBlockState(blockPos).getBlock().getAmbientOcclusionLightValue()
            : blockAccessIn.getBlockState(blockPosIn).getBlock().getAmbientOcclusionLightValue();
         f5 = BlockModelRenderer.fixAoLightValue(f5);
         BlockModelRenderer.VertexTranslations vertexTranslations = BlockModelRenderer.VertexTranslations.getVertexTranslations(facingIn);
         if (boundsFlags.get(1) && neighbourInfo.flag) {
            float f6 = (fixAoLightValue4 + fixAoLightValue + f2 + f5) * 0.25F;
            float f7 = (fixAoLightValue3 + fixAoLightValue + f + f5) * 0.25F;
            float f8 = (fixAoLightValue3 + fixAoLightValue2 + f3 + f5) * 0.25F;
            float f9 = (fixAoLightValue4 + fixAoLightValue2 + f4 + f5) * 0.25F;
            float f10 = quadBounds[neighbourInfo.orientationArray[0].shape] * quadBounds[neighbourInfo.orientationArray[1].shape];
            float f11 = quadBounds[neighbourInfo.orientationArray[2].shape] * quadBounds[neighbourInfo.orientationArray[3].shape];
            float f12 = quadBounds[neighbourInfo.orientationArray[4].shape] * quadBounds[neighbourInfo.orientationArray[5].shape];
            float f13 = quadBounds[neighbourInfo.orientationArray[6].shape] * quadBounds[neighbourInfo.orientationArray[7].shape];
            float f14 = quadBounds[neighbourInfo.orientationArray2[0].shape] * quadBounds[neighbourInfo.orientationArray2[1].shape];
            float f15 = quadBounds[neighbourInfo.orientationArray2[2].shape] * quadBounds[neighbourInfo.orientationArray2[3].shape];
            float f16 = quadBounds[neighbourInfo.orientationArray2[4].shape] * quadBounds[neighbourInfo.orientationArray2[5].shape];
            float f17 = quadBounds[neighbourInfo.orientationArray2[6].shape] * quadBounds[neighbourInfo.orientationArray2[7].shape];
            float f18 = quadBounds[neighbourInfo.orientationArray3[0].shape] * quadBounds[neighbourInfo.orientationArray3[1].shape];
            float f19 = quadBounds[neighbourInfo.orientationArray3[2].shape] * quadBounds[neighbourInfo.orientationArray3[3].shape];
            float f20 = quadBounds[neighbourInfo.orientationArray3[4].shape] * quadBounds[neighbourInfo.orientationArray3[5].shape];
            float f21 = quadBounds[neighbourInfo.orientationArray3[6].shape] * quadBounds[neighbourInfo.orientationArray3[7].shape];
            float f22 = quadBounds[neighbourInfo.orientationArray4[0].shape] * quadBounds[neighbourInfo.orientationArray4[1].shape];
            float f23 = quadBounds[neighbourInfo.orientationArray4[2].shape] * quadBounds[neighbourInfo.orientationArray4[3].shape];
            float f24 = quadBounds[neighbourInfo.orientationArray4[4].shape] * quadBounds[neighbourInfo.orientationArray4[5].shape];
            float f25 = quadBounds[neighbourInfo.orientationArray4[6].shape] * quadBounds[neighbourInfo.orientationArray4[7].shape];
            this.vertexColorMultiplier[vertexTranslations.firstCorner] = f6 * f10 + f7 * f11 + f8 * f12 + f9 * f13;
            this.vertexColorMultiplier[vertexTranslations.secondCorner] = f6 * f14 + f7 * f15 + f8 * f16 + f9 * f17;
            this.vertexColorMultiplier[vertexTranslations.thirdCorner] = f6 * f18 + f7 * f19 + f8 * f20 + f9 * f21;
            this.vertexColorMultiplier[vertexTranslations.fourthCorner] = f6 * f22 + f7 * f23 + f8 * f24 + f9 * f25;
            int aoBrightness = this.getAoBrightness(mixedBrightnessForBlock4, mixedBrightnessForBlock, n2, mixedBrightnessForBlock5);
            int aoBrightness2 = this.getAoBrightness(mixedBrightnessForBlock3, mixedBrightnessForBlock, n, mixedBrightnessForBlock5);
            int aoBrightness3 = this.getAoBrightness(mixedBrightnessForBlock3, mixedBrightnessForBlock2, n3, mixedBrightnessForBlock5);
            int aoBrightness4 = this.getAoBrightness(mixedBrightnessForBlock4, mixedBrightnessForBlock2, n4, mixedBrightnessForBlock5);
            this.vertexBrightness[vertexTranslations.firstCorner] = this.getVertexBrightness(aoBrightness, aoBrightness2, aoBrightness3, aoBrightness4, f10, f11, f12, f13);
            this.vertexBrightness[vertexTranslations.secondCorner] = this.getVertexBrightness(aoBrightness, aoBrightness2, aoBrightness3, aoBrightness4, f14, f15, f16, f17);
            this.vertexBrightness[vertexTranslations.thirdCorner] = this.getVertexBrightness(aoBrightness, aoBrightness2, aoBrightness3, aoBrightness4, f18, f19, f20, f21);
            this.vertexBrightness[vertexTranslations.fourthCorner] = this.getVertexBrightness(aoBrightness, aoBrightness2, aoBrightness3, aoBrightness4, f22, f23, f24, f25);
         } else {
            float f6 = (fixAoLightValue4 + fixAoLightValue + f2 + f5) * 0.25F;
            float f7 = (fixAoLightValue3 + fixAoLightValue + f + f5) * 0.25F;
            float f8 = (fixAoLightValue3 + fixAoLightValue2 + f3 + f5) * 0.25F;
            float f9 = (fixAoLightValue4 + fixAoLightValue2 + f4 + f5) * 0.25F;
            this.vertexBrightness[vertexTranslations.firstCorner] = this.getAoBrightness(mixedBrightnessForBlock4, mixedBrightnessForBlock, n2, mixedBrightnessForBlock5);
            this.vertexBrightness[vertexTranslations.secondCorner] = this.getAoBrightness(mixedBrightnessForBlock3, mixedBrightnessForBlock, n, mixedBrightnessForBlock5);
            this.vertexBrightness[vertexTranslations.thirdCorner] = this.getAoBrightness(mixedBrightnessForBlock3, mixedBrightnessForBlock2, n3, mixedBrightnessForBlock5);
            this.vertexBrightness[vertexTranslations.fourthCorner] = this.getAoBrightness(mixedBrightnessForBlock4, mixedBrightnessForBlock2, n4, mixedBrightnessForBlock5);
            this.vertexColorMultiplier[vertexTranslations.firstCorner] = f6;
            this.vertexColorMultiplier[vertexTranslations.secondCorner] = f7;
            this.vertexColorMultiplier[vertexTranslations.thirdCorner] = f8;
            this.vertexColorMultiplier[vertexTranslations.fourthCorner] = f9;
         }
      }

      public int getAoBrightness(int br1, int br2, int br3, int br4) {
         if (br1 == 0) {
            br1 = br4;
         }

         if (br2 == 0) {
            br2 = br4;
         }

         if (br3 == 0) {
            br3 = br4;
         }

         return br1 + br2 + br3 + br4 >> 2 & 16711935;
      }

      public int getVertexBrightness(int n, int n2, int n3, int n4, float f, float f2, float f3, float f4) {
         int n5 = (int)((n >> 16 & 0xFF) * f + (n2 >> 16 & 0xFF) * f2 + (n3 >> 16 & 0xFF) * f3 + (n4 >> 16 & 0xFF) * f4) & 0xFF;
         int n6 = (int)((n & 0xFF) * f + (n2 & 0xFF) * f2 + (n3 & 0xFF) * f3 + (n4 & 0xFF) * f4) & 0xFF;
         return n5 << 16 | n6;
      }
   }

   public enum EnumNeighborInfo {
      DOWN(
         new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH},
         0.5F,
         false,
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0]
      ),
      UP(
         new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH},
         1.0F,
         false,
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0],
         new BlockModelRenderer.Orientation[0]
      ),
      NORTH(
         new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST},
         0.8F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_WEST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_EAST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST
         }
      ),
      SOUTH(
         new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP},
         0.8F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.WEST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_WEST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.WEST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.WEST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.EAST
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_EAST,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.EAST,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.EAST
         }
      ),
      WEST(
         new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH},
         0.6F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.SOUTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.SOUTH
         }
      ),
      EAST(
         new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH},
         0.6F,
         true,
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.SOUTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.FLIP_DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.DOWN,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.NORTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_NORTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.NORTH
         },
         new BlockModelRenderer.Orientation[]{
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.SOUTH,
            BlockModelRenderer.Orientation.FLIP_UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.FLIP_SOUTH,
            BlockModelRenderer.Orientation.UP,
            BlockModelRenderer.Orientation.SOUTH
         }
      );

      public EnumFacing[] enumFacingArray;
      public float floatValue;
      public boolean flag;
      public BlockModelRenderer.Orientation[] orientationArray;
      public BlockModelRenderer.Orientation[] orientationArray2;
      public BlockModelRenderer.Orientation[] orientationArray3;
      public BlockModelRenderer.Orientation[] orientationArray4;
      public static BlockModelRenderer.EnumNeighborInfo[] VALUES = new BlockModelRenderer.EnumNeighborInfo[6];

      EnumNeighborInfo(
         EnumFacing[] enumFacings,
         float f,
         boolean flag,
         BlockModelRenderer.Orientation[] orientations,
         BlockModelRenderer.Orientation[] orientations2,
         BlockModelRenderer.Orientation[] orientations3,
         BlockModelRenderer.Orientation[] orientations4
      ) {
         this.enumFacingArray = enumFacings;
         this.floatValue = f;
         this.flag = flag;
         this.orientationArray = orientations;
         this.orientationArray2 = orientations2;
         this.orientationArray3 = orientations3;
         this.orientationArray4 = orientations4;
      }

      public static BlockModelRenderer.EnumNeighborInfo getNeighbourInfo(EnumFacing enumFacing) {
         return VALUES[enumFacing.getIndex()];
      }

      static {
         VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
         VALUES[EnumFacing.UP.getIndex()] = UP;
         VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
         VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
         VALUES[EnumFacing.WEST.getIndex()] = WEST;
         VALUES[EnumFacing.EAST.getIndex()] = EAST;
      }
   }

   public enum Orientation {
      DOWN(EnumFacing.DOWN, false),
      UP(EnumFacing.UP, false),
      NORTH(EnumFacing.NORTH, false),
      SOUTH(EnumFacing.SOUTH, false),
      WEST(EnumFacing.WEST, false),
      EAST(EnumFacing.EAST, false),
      FLIP_DOWN(EnumFacing.DOWN, true),
      FLIP_UP(EnumFacing.UP, true),
      FLIP_NORTH(EnumFacing.NORTH, true),
      FLIP_SOUTH(EnumFacing.SOUTH, true),
      FLIP_WEST(EnumFacing.WEST, true),
      FLIP_EAST(EnumFacing.EAST, true);

      public int shape;

      Orientation(EnumFacing enumFacing, boolean flag) {
         this.shape = enumFacing.getIndex() + (flag ? EnumFacing.values().length : 0);
      }
   }

   enum VertexTranslations {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      public int firstCorner;
      public int secondCorner;
      public int thirdCorner;
      public int fourthCorner;
      public static BlockModelRenderer.VertexTranslations[] VALUES = new BlockModelRenderer.VertexTranslations[6];

      VertexTranslations(int n, int n2, int n3, int n4) {
         this.firstCorner = n;
         this.secondCorner = n2;
         this.thirdCorner = n3;
         this.fourthCorner = n4;
      }

      public static BlockModelRenderer.VertexTranslations getVertexTranslations(EnumFacing enumFacing) {
         return VALUES[enumFacing.getIndex()];
      }

      static {
         VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
         VALUES[EnumFacing.UP.getIndex()] = UP;
         VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
         VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
         VALUES[EnumFacing.WEST.getIndex()] = WEST;
         VALUES[EnumFacing.EAST.getIndex()] = EAST;
      }
   }
}
