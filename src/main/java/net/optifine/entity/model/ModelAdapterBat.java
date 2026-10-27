package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityBat;
import net.optifine.reflect.Reflector;

public class ModelAdapterBat extends ModelAdapter {
   public ModelAdapterBat() {
      super(EntityBat.class, "bat", 0.25F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelBat();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelBat)) {
         return null;
      }

      ModelBat modelBat = (ModelBat)modelBase;
      return str.equals("head")
         ? (ModelRenderer)Reflector.getFieldValue(modelBat, Reflector.ModelBat_ModelRenderers, 0)
         : (
            str.equals("body")
               ? (ModelRenderer)Reflector.getFieldValue(modelBat, Reflector.ModelBat_ModelRenderers, 1)
               : (
                  str.equals("right_wing")
                     ? (ModelRenderer)Reflector.getFieldValue(modelBat, Reflector.ModelBat_ModelRenderers, 2)
                     : (
                        str.equals("left_wing")
                           ? (ModelRenderer)Reflector.getFieldValue(modelBat, Reflector.ModelBat_ModelRenderers, 3)
                           : (
                              str.equals("outer_right_wing")
                                 ? (ModelRenderer)Reflector.getFieldValue(modelBat, Reflector.ModelBat_ModelRenderers, 4)
                                 : (str.equals("outer_left_wing") ? (ModelRenderer)Reflector.getFieldValue(modelBat, Reflector.ModelBat_ModelRenderers, 5) : null)
                           )
                     )
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "body", "right_wing", "left_wing", "outer_right_wing", "outer_left_wing"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderBat renderBat = new RenderBat(renderManager);
      renderBat.mainModel = modelBase;
      renderBat.shadowSize = f;
      return renderBat;
   }
}
