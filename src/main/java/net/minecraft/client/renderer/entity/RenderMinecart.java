package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderMinecart<T extends EntityMinecart> extends Render<T> {
   public static ResourceLocation minecartTextures = new ResourceLocation("textures/entity/minecart.png");
   public ModelBase modelMinecart = new ModelMinecart();

   public RenderMinecart(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.5F;
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      this.bindEntityTexture((T)entity);
      long l = entity.getEntityId() * 493286711L;
      l = l * l * 4392167121L + l * 98761L;
      float f = (((float)(l >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float f2 = (((float)(l >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float f3 = (((float)(l >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      GlStateManager.translate(f, f2, f3);
      double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
      double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
      double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
      double d4 = 0.3F;
      Vec3 snapPositionToRail = entity.snapPositionToRail(d, d2, d3);
      float f4 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
      if (snapPositionToRail != null) {
         Vec3 snapPositionToRailWithOffset = entity.snapPositionToRailWithOffset(d, d2, d3, d4);
         Vec3 snapPositionToRailWithOffset2 = entity.snapPositionToRailWithOffset(d, d2, d3, -d4);
         if (snapPositionToRailWithOffset == null) {
            snapPositionToRailWithOffset = snapPositionToRail;
         }

         if (snapPositionToRailWithOffset2 == null) {
            snapPositionToRailWithOffset2 = snapPositionToRail;
         }

         x += snapPositionToRail.xCoord - d;
         y += (snapPositionToRailWithOffset.yCoord + snapPositionToRailWithOffset2.yCoord) / 2.0 - d2;
         z += snapPositionToRail.zCoord - d3;
         Vec3 addVector = snapPositionToRailWithOffset2.addVector(-snapPositionToRailWithOffset.xCoord, -snapPositionToRailWithOffset.yCoord, -snapPositionToRailWithOffset.zCoord);
         if (addVector.lengthVector() != 0.0) {
            addVector = addVector.normalize();
            entityYaw = (float)(Math.atan2(addVector.zCoord, addVector.xCoord) * 180.0 / Math.PI);
            f4 = (float)(Math.atan(addVector.yCoord) * 73.0);
         }
      }

      GlStateManager.translate((float)x, (float)y + 0.375F, (float)z);
      GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-f4, 0.0F, 0.0F, 1.0F);
      float f5 = entity.getRollingAmplitude() - partialTicks;
      float f6 = entity.getDamage() - partialTicks;
      if (f6 < 0.0F) {
         f6 = 0.0F;
      }

      if (f5 > 0.0F) {
         GlStateManager.rotate(MathHelper.sin(f5) * f5 * f6 / 10.0F * entity.getRollingDirection(), 1.0F, 0.0F, 0.0F);
      }

      int displayTileOffset = entity.getDisplayTileOffset();
      IBlockState displayTile = entity.getDisplayTile();
      if (displayTile.getBlock().getRenderType() != -1) {
         GlStateManager.pushMatrix();
         this.bindTexture(TextureMap.locationBlocksTexture);
         float f7 = 0.75F;
         GlStateManager.scale(f7, f7, f7);
         GlStateManager.translate(-0.5F, (displayTileOffset - 8) / 16.0F, 0.5F);
         this.handleEntityMinecartTNT((T)entity, partialTicks, displayTile);
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindEntityTexture((T)entity);
      }

      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      this.modelMinecart.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      super.doRender((T)entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(T entity) {
      return minecartTextures;
   }

   public void handleEntityMinecartTNT(T minecart, float partialTicks, IBlockState state) {
      GlStateManager.pushMatrix();
      Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, minecart.getBrightness(partialTicks));
      GlStateManager.popMatrix();
   }
}
