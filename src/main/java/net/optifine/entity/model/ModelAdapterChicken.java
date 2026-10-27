package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityChicken;

public class ModelAdapterChicken extends ModelAdapter {
   public ModelAdapterChicken() {
      super(EntityChicken.class, "chicken", 0.3F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelChicken();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelChicken)) {
         return null;
      }

      ModelChicken modelChicken = (ModelChicken)modelBase;
      return str.equals("head")
         ? modelChicken.head
         : (
            str.equals("body")
               ? modelChicken.body
               : (
                  str.equals("right_leg")
                     ? modelChicken.rightLeg
                     : (
                        str.equals("left_leg")
                           ? modelChicken.leftLeg
                           : (
                              str.equals("right_wing")
                                 ? modelChicken.rightWing
                                 : (str.equals("left_wing") ? modelChicken.leftWing : (str.equals("bill") ? modelChicken.bill : (str.equals("chin") ? modelChicken.chin : null)))
                           )
                     )
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "body", "right_leg", "left_leg", "right_wing", "left_wing", "bill", "chin"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      return new RenderChicken(renderManager, modelBase, f);
   }
}
