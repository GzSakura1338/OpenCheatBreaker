package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.passive.EntityPig;

public class ModelAdapterPig extends ModelAdapterQuadruped {
   public ModelAdapterPig() {
      super(EntityPig.class, "pig", 0.7F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelPig();
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      return new RenderPig(renderManager, modelBase, f);
   }
}
