package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TileEntityPistonRenderer extends TileEntitySpecialRenderer<TileEntityPiston> {
   public BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

   public void renderTileEntityAt(TileEntityPiston te, double x, double y, double z, float partialTicks, int destroyStage) {
      BlockPos pos = te.getPos();
      IBlockState pistonState = te.getPistonState();
      Block block = pistonState.getBlock();
      if (block.getMaterial() != Material.air && te.getProgress(partialTicks) < 1.0F) {
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         this.bindTexture(TextureMap.locationBlocksTexture);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableBlend();
         GlStateManager.disableCull();
         if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
         } else {
            GlStateManager.shadeModel(7424);
         }

         worldRenderer.begin(7, DefaultVertexFormats.BLOCK);
         worldRenderer.setTranslation(
            (float)x - pos.getX() + te.getOffsetX(partialTicks),
            (float)y - pos.getY() + te.getOffsetY(partialTicks),
            (float)z - pos.getZ() + te.getOffsetZ(partialTicks)
         );
         World world = this.getWorld();
         if (block == Blocks.piston_head && te.getProgress(partialTicks) < 0.5F) {
            pistonState = pistonState.withProperty(BlockPistonExtension.SHORT, true);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world, this.blockRenderer.getModelFromBlockState(pistonState, world, pos), pistonState, pos, worldRenderer, true);
         } else if (te.shouldPistonHeadBeRendered() && !te.isExtending()) {
            EnumPistonType enumPistonType = block == Blocks.sticky_piston
               ? EnumPistonType.STICKY
               : EnumPistonType.DEFAULT;
            IBlockState withProperty = Blocks.piston_head
               .getDefaultState()
               .withProperty(BlockPistonExtension.TYPE, enumPistonType)
               .withProperty(BlockPistonExtension.FACING, pistonState.getValue(BlockPistonBase.FACING));
            withProperty = withProperty.withProperty(BlockPistonExtension.SHORT, te.getProgress(partialTicks) >= 0.5F);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world, this.blockRenderer.getModelFromBlockState(withProperty, world, pos), withProperty, pos, worldRenderer, true);
            worldRenderer.setTranslation((float)x - pos.getX(), (float)y - pos.getY(), (float)z - pos.getZ());
            pistonState.withProperty(BlockPistonBase.EXTENDED, true);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world, this.blockRenderer.getModelFromBlockState(pistonState, world, pos), pistonState, pos, worldRenderer, true);
         } else {
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world, this.blockRenderer.getModelFromBlockState(pistonState, world, pos), pistonState, pos, worldRenderer, false);
         }

         worldRenderer.setTranslation(0.0, 0.0, 0.0);
         instance.draw();
         RenderHelper.enableStandardItemLighting();
      }
   }
}
