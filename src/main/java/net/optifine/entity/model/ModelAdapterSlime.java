package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;
import net.optifine.reflect.Reflector;

public class ModelAdapterSlime extends ModelAdapter {
   public ModelAdapterSlime() {
      super(EntitySlime.class, "slime", 0.25F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSlime(16);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelSlime)) {
         return null;
      }

      ModelSlime modelSlime = (ModelSlime)modelBase;
      return str.equals("body")
         ? (ModelRenderer)Reflector.getFieldValue(modelSlime, Reflector.ModelSlime_ModelRenderers, 0)
         : (
            str.equals("left_eye")
               ? (ModelRenderer)Reflector.getFieldValue(modelSlime, Reflector.ModelSlime_ModelRenderers, 1)
               : (
                  str.equals("right_eye")
                     ? (ModelRenderer)Reflector.getFieldValue(modelSlime, Reflector.ModelSlime_ModelRenderers, 2)
                     : (str.equals("mouth") ? (ModelRenderer)Reflector.getFieldValue(modelSlime, Reflector.ModelSlime_ModelRenderers, 3) : null)
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"body", "left_eye", "right_eye", "mouth"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      return new RenderSlime(renderManager, modelBase, f);
   }
}
