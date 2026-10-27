package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.optifine.reflect.Reflector;

public class ModelAdapterWolf extends ModelAdapter {
   public ModelAdapterWolf() {
      super(EntityWolf.class, "wolf", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelWolf();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelWolf)) {
         return null;
      }

      ModelWolf modelWolf = (ModelWolf)modelBase;
      return str.equals("head")
         ? modelWolf.wolfHeadMain
         : (
            str.equals("body")
               ? modelWolf.wolfBody
               : (
                  str.equals("leg1")
                     ? modelWolf.wolfLeg1
                     : (
                        str.equals("leg2")
                           ? modelWolf.wolfLeg2
                           : (
                              str.equals("leg3")
                                 ? modelWolf.wolfLeg3
                                 : (
                                    str.equals("leg4")
                                       ? modelWolf.wolfLeg4
                                       : (
                                          str.equals("tail")
                                             ? (ModelRenderer)Reflector.getFieldValue(modelWolf, Reflector.ModelWolf_tail)
                                             : (str.equals("mane") ? (ModelRenderer)Reflector.getFieldValue(modelWolf, Reflector.ModelWolf_mane) : null)
                                       )
                                 )
                           )
                     )
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "body", "leg1", "leg2", "leg3", "leg4", "tail", "mane"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      return new RenderWolf(renderManager, modelBase, f);
   }
}
