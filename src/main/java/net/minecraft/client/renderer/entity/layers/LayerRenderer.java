package net.minecraft.client.renderer.entity.layers;

import net.minecraft.entity.EntityLivingBase;

public interface LayerRenderer<E extends EntityLivingBase> {
   void doRenderLayer(E value, float f, float f2, float f3, float f4, float f5, float f6, float f7);

   boolean shouldCombineTextures();
}
