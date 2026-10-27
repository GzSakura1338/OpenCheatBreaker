package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderSnowball<T extends Entity> extends Render<T> {
   public Item item;
   public RenderItem itemRenderer;

   public RenderSnowball(RenderManager renderManagerIn, Item item, RenderItem renderItem) {
      super(renderManagerIn);
      this.item = item;
      this.itemRenderer = renderItem;
   }

   @Override
   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      this.bindTexture(TextureMap.locationBlocksTexture);
      GlStateManager.scale(0.8, 0.8, 0.8);
      GlStateManager.translate(0.0, 0.4, 0.0);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      this.itemRenderer.renderItem(this.createStack((T)entity), TransformType.GROUND);
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender((T)entity, x, y, z, entityYaw, partialTicks);
   }

   public ItemStack createStack(T entityIn) {
      return new ItemStack(this.item, 1, 0);
   }

   @Override
   public ResourceLocation getEntityTexture(Entity entity) {
      return TextureMap.locationBlocksTexture;
   }
}
