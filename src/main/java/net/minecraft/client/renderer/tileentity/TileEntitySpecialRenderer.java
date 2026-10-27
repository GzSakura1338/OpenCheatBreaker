package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.entity.model.IEntityRenderer;

public abstract class TileEntitySpecialRenderer<T extends TileEntity> implements IEntityRenderer {
   public static ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{
      new ResourceLocation("textures/blocks/destroy_stage_0.png"),
      new ResourceLocation("textures/blocks/destroy_stage_1.png"),
      new ResourceLocation("textures/blocks/destroy_stage_2.png"),
      new ResourceLocation("textures/blocks/destroy_stage_3.png"),
      new ResourceLocation("textures/blocks/destroy_stage_4.png"),
      new ResourceLocation("textures/blocks/destroy_stage_5.png"),
      new ResourceLocation("textures/blocks/destroy_stage_6.png"),
      new ResourceLocation("textures/blocks/destroy_stage_7.png"),
      new ResourceLocation("textures/blocks/destroy_stage_8.png"),
      new ResourceLocation("textures/blocks/destroy_stage_9.png")
   };
   public TileEntityRendererDispatcher rendererDispatcher;
   public Class tileEntityClass = null;
   public ResourceLocation locationTextureCustom = null;

   public abstract void renderTileEntityAt(T value, double d, double d2, double d3, float f, int n);

   public void bindTexture(ResourceLocation location) {
      TextureManager renderEngine = this.rendererDispatcher.renderEngine;
      if (renderEngine != null) {
         renderEngine.bindTexture(location);
      }
   }

   public World getWorld() {
      return this.rendererDispatcher.worldObj;
   }

   public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
      this.rendererDispatcher = rendererDispatcherIn;
   }

   public FontRenderer getFontRenderer() {
      return this.rendererDispatcher.getFontRenderer();
   }

   public boolean forceTileEntityRender() {
      return false;
   }

   public void renderTileEntityFast(T value, double d, double d2, double d3, float f, int n, WorldRenderer worldRenderer) {
   }

   @Override
   public Class getEntityClass() {
      return this.tileEntityClass;
   }

   @Override
   public void setEntityClass(Class clazz) {
      this.tileEntityClass = clazz;
   }

   @Override
   public ResourceLocation getLocationTextureCustom() {
      return this.locationTextureCustom;
   }

   @Override
   public void setLocationTextureCustom(ResourceLocation resourceLocation) {
      this.locationTextureCustom = resourceLocation;
   }
}
