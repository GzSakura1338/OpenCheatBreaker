package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterWitherSkull extends ModelAdapter {
   public ModelAdapterWitherSkull() {
      super(EntityWitherSkull.class, "wither_skull", 0.0F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSkeletonHead();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelSkeletonHead)) {
         return null;
      }

      ModelSkeletonHead modelSkeletonHead = (ModelSkeletonHead)modelBase;
      return str.equals("head") ? modelSkeletonHead.skeletonHead : null;
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderWitherSkull renderWitherSkull = new RenderWitherSkull(renderManager);
      if (!Reflector.RenderWitherSkull_model.exists()) {
         Config.warn("Field not found: RenderWitherSkull_model");
         return null;
      } else {
         Reflector.setFieldValue(renderWitherSkull, Reflector.RenderWitherSkull_model, modelBase);
         renderWitherSkull.shadowSize = f;
         return renderWitherSkull;
      }
   }
}
