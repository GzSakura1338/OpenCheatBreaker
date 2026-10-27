package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;

public abstract class ModelAdapterQuadruped extends ModelAdapter {
   public ModelAdapterQuadruped(Class clazz, String str, float f) {
      super(clazz, str, f);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelQuadruped)) {
         return null;
      }

      ModelQuadruped modelQuadruped = (ModelQuadruped)modelBase;
      return str.equals("head")
         ? modelQuadruped.head
         : (
            str.equals("body")
               ? modelQuadruped.body
               : (
                  str.equals("leg1")
                     ? modelQuadruped.leg1
                     : (str.equals("leg2") ? modelQuadruped.leg2 : (str.equals("leg3") ? modelQuadruped.leg3 : (str.equals("leg4") ? modelQuadruped.leg4 : null)))
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "body", "leg1", "leg2", "leg3", "leg4"};
   }
}
