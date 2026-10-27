package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderFallingBlock extends Render<EntityFallingBlock> {
   public RenderFallingBlock(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityFallingBlock entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (CheatBreaker.getInstance().getKeybindSettings().shouldRenderEntity(entity)) {
         if (entity.getBlock() != null) {
            this.bindTexture(TextureMap.locationBlocksTexture);
            IBlockState block = entity.getBlock();
            Block block2 = block.getBlock();
            BlockPos blockPos = new BlockPos(entity);
            World worldObj = entity.getWorldObj();
            if (block != worldObj.getBlockState(blockPos) && block2.getRenderType() != -1 && block2.getRenderType() == 3) {
               GlStateManager.pushMatrix();
               GlStateManager.translate((float)x, (float)y, (float)z);
               GlStateManager.disableLighting();
               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               worldRenderer.begin(7, DefaultVertexFormats.BLOCK);
               int n = blockPos.getX();
               int n2 = blockPos.getY();
               int n3 = blockPos.getZ();
               worldRenderer.setTranslation(-n - 0.5F, -n2, -n3 - 0.5F);
               BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
               IBakedModel modelFromBlockState = blockRendererDispatcher.getModelFromBlockState(block, worldObj, (BlockPos)null);
               blockRendererDispatcher.getBlockModelRenderer().renderModel(worldObj, modelFromBlockState, block, blockPos, worldRenderer, false);
               worldRenderer.setTranslation(0.0, 0.0, 0.0);
               instance.draw();
               GlStateManager.enableLighting();
               GlStateManager.popMatrix();
               super.doRender(entity, x, y, z, entityYaw, partialTicks);
            }
         }
      }
   }

   public ResourceLocation getEntityTexture(EntityFallingBlock entity) {
      return TextureMap.locationBlocksTexture;
   }
}
