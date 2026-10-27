package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.boss.EntityDragon;
import net.optifine.reflect.Reflector;

public class ModelAdapterDragon extends ModelAdapter {
   public ModelAdapterDragon() {
      super(EntityDragon.class, "dragon", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelDragon(0.0F);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelDragon)) {
         return null;
      }

      ModelDragon modelDragon = (ModelDragon)modelBase;
      return str.equals("head")
         ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 0)
         : (
            str.equals("spine")
               ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 1)
               : (
                  str.equals("jaw")
                     ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 2)
                     : (
                        str.equals("body")
                           ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 3)
                           : (
                              str.equals("rear_leg")
                                 ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 4)
                                 : (
                                    str.equals("front_leg")
                                       ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 5)
                                       : (
                                          str.equals("rear_leg_tip")
                                             ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 6)
                                             : (
                                                str.equals("front_leg_tip")
                                                   ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 7)
                                                   : (
                                                      str.equals("rear_foot")
                                                         ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 8)
                                                         : (
                                                            str.equals("front_foot")
                                                               ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 9)
                                                               : (
                                                                  str.equals("wing")
                                                                     ? (ModelRenderer)Reflector.getFieldValue(modelDragon, Reflector.ModelDragon_ModelRenderers, 10)
                                                                     : (
                                                                        str.equals("wing_tip")
                                                                           ? (ModelRenderer)Reflector.getFieldValue(
                                                                              modelDragon, Reflector.ModelDragon_ModelRenderers, 11
                                                                           )
                                                                           : null
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{
         "head", "spine", "jaw", "body", "rear_leg", "front_leg", "rear_leg_tip", "front_leg_tip", "rear_foot", "front_foot", "wing", "wing_tip"
      };
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderDragon renderDragon = new RenderDragon(renderManager);
      renderDragon.mainModel = modelBase;
      renderDragon.shadowSize = f;
      return renderDragon;
   }
}
