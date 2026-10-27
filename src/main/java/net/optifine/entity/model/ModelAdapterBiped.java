package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public abstract class ModelAdapterBiped extends ModelAdapter {
   public ModelAdapterBiped(Class clazz, String str, float f) {
      super(clazz, str, f);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelBiped)) {
         return null;
      }

      ModelBiped modelBiped = (ModelBiped)modelBase;
      return str.equals("head")
         ? modelBiped.bipedHead
         : (
            str.equals("headwear")
               ? modelBiped.bipedHeadwear
               : (
                  str.equals("body")
                     ? modelBiped.bipedBody
                     : (
                        str.equals("left_arm")
                           ? modelBiped.bipedLeftArm
                           : (
                              str.equals("right_arm")
                                 ? modelBiped.bipedRightArm
                                 : (str.equals("left_leg") ? modelBiped.bipedLeftLeg : (str.equals("right_leg") ? modelBiped.bipedRightLeg : null))
                           )
                     )
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "headwear", "body", "left_arm", "right_arm", "left_leg", "right_leg"};
   }
}
