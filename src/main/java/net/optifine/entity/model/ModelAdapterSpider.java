package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.monster.EntitySpider;

public class ModelAdapterSpider extends ModelAdapter {
   public ModelAdapterSpider() {
      super(EntitySpider.class, "spider", 1.0F);
   }

   public ModelAdapterSpider(Class clazz, String str, float f) {
      super(clazz, str, f);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSpider();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelSpider)) {
         return null;
      }

      ModelSpider modelSpider = (ModelSpider)modelBase;
      return str.equals("head")
         ? modelSpider.spiderHead
         : (
            str.equals("neck")
               ? modelSpider.spiderNeck
               : (
                  str.equals("body")
                     ? modelSpider.spiderBody
                     : (
                        str.equals("leg1")
                           ? modelSpider.spiderLeg1
                           : (
                              str.equals("leg2")
                                 ? modelSpider.spiderLeg2
                                 : (
                                    str.equals("leg3")
                                       ? modelSpider.spiderLeg3
                                       : (
                                          str.equals("leg4")
                                             ? modelSpider.spiderLeg4
                                             : (
                                                str.equals("leg5")
                                                   ? modelSpider.spiderLeg5
                                                   : (
                                                      str.equals("leg6")
                                                         ? modelSpider.spiderLeg6
                                                         : (str.equals("leg7") ? modelSpider.spiderLeg7 : (str.equals("leg8") ? modelSpider.spiderLeg8 : null))
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
      return new String[]{"head", "neck", "body", "leg1", "leg2", "leg3", "leg4", "leg5", "leg6", "leg7", "leg8"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderSpider renderSpider = new RenderSpider(renderManager);
      renderSpider.mainModel = modelBase;
      renderSpider.shadowSize = f;
      return renderSpider;
   }
}
