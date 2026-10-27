package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel.Builder;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;

public class BlockRendererDispatcher implements IResourceManagerReloadListener {
   public BlockModelRenderer blockModelRenderer = new BlockModelRenderer();
   public ChestRenderer chestRenderer = new ChestRenderer();
   public BlockFluidRenderer fluidRenderer = new BlockFluidRenderer();
   public BlockModelShapes blockModelShapes;
   public GameSettings gameSettings;

   public BlockRendererDispatcher(BlockModelShapes blockModelShapesIn, GameSettings gameSettingsIn) {
      this.blockModelShapes = blockModelShapesIn;
      this.gameSettings = gameSettingsIn;
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.blockModelShapes;
   }

   public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess) {
      Block block = state.getBlock();
      int renderType = block.getRenderType();
      if (renderType == 3) {
         state = block.getActualState(state, blockAccess, pos);
         IBakedModel modelForState = this.blockModelShapes.getModelForState(state);
         IBakedModel makeBakedModel = new Builder(modelForState, texture).makeBakedModel();
         this.blockModelRenderer.renderModel(blockAccess, makeBakedModel, state, pos, Tessellator.getInstance().getWorldRenderer());
      }
   }

   public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, WorldRenderer worldRendererIn) {
      try {
         int renderType = state.getBlock().getRenderType();
         if (renderType == -1) {
            return false;
         }

         switch (renderType) {
            case 1:
               return this.fluidRenderer.renderFluid(blockAccess, state, pos, worldRendererIn);
            case 2:
               return false;
            case 3:
               IBakedModel modelFromBlockState = this.getModelFromBlockState(state, blockAccess, pos);
               return this.blockModelRenderer.renderModel(blockAccess, modelFromBlockState, state, pos, worldRendererIn);
            default:
               return false;
         }
      } catch (Throwable e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Tesselating block in world");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block being tesselated");
         CrashReportCategory.addBlockInfo(makeCategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
         throw new ReportedException(makeCrashReport);
      }
   }

   public BlockModelRenderer getBlockModelRenderer() {
      return this.blockModelRenderer;
   }

   public IBakedModel getBakedModel(IBlockState state, BlockPos pos) {
      IBakedModel modelForState = this.blockModelShapes.getModelForState(state);
      if (pos != null && this.gameSettings.allowBlockAlternatives && modelForState instanceof WeightedBakedModel) {
         modelForState = ((WeightedBakedModel)modelForState).getAlternativeModel(MathHelper.getPositionRandom(pos));
      }

      return modelForState;
   }

   public IBakedModel getModelFromBlockState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      Block block = state.getBlock();
      if (worldIn.getWorldType() != WorldType.DEBUG_WORLD) {
         try {
            state = block.getActualState(state, worldIn, pos);
         } catch (Exception e) {
         }
      }

      IBakedModel modelForState = this.blockModelShapes.getModelForState(state);
      if (pos != null && this.gameSettings.allowBlockAlternatives && modelForState instanceof WeightedBakedModel) {
         modelForState = ((WeightedBakedModel)modelForState).getAlternativeModel(MathHelper.getPositionRandom(pos));
      }

      return modelForState;
   }

   public void renderBlockBrightness(IBlockState state, float brightness) {
      int renderType = state.getBlock().getRenderType();
      if (renderType != -1) {
         switch (renderType) {
            case 1:
            default:
               break;
            case 2:
               this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
               break;
            case 3:
               IBakedModel bakedModel = this.getBakedModel(state, (BlockPos)null);
               this.blockModelRenderer.renderModelBrightness(bakedModel, state, brightness, true);
         }
      }
   }

   public boolean isRenderTypeChest(Block block, int n) {
      if (block == null) {
         return false;
      }

      int renderType = block.getRenderType();
      return renderType == 3 ? false : renderType == 2;
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.fluidRenderer.initAtlasSprites();
   }
}
