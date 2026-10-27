package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderBiped<T extends EntityLiving> extends RenderLiving<T> {
   public static ResourceLocation DEFAULT_RES_LOC = new ResourceLocation("textures/entity/steve.png");
   public ModelBiped modelBipedMain;
   public float floatValue;

   public RenderBiped(RenderManager renderManagerIn, ModelBiped modelBipedIn, float shadowSize) {
      this(renderManagerIn, modelBipedIn, shadowSize, 1.0F);
      this.addLayer(new LayerHeldItem(this));
   }

   public RenderBiped(RenderManager renderManagerIn, ModelBiped modelBipedIn, float shadowSize, float f) {
      super(renderManagerIn, modelBipedIn, shadowSize);
      this.modelBipedMain = modelBipedIn;
      this.floatValue = f;
      this.addLayer(new LayerCustomHead(modelBipedIn.bipedHead));
   }

   public ResourceLocation getEntityTexture(T entity) {
      return DEFAULT_RES_LOC;
   }

   @Override
   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.0F, 0.1875F, 0.0F);
   }
}
