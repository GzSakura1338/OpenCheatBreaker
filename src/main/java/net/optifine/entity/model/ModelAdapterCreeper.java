package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;

public class ModelAdapterCreeper extends ModelAdapter {
   public ModelAdapterCreeper() {
      super(EntityCreeper.class, "creeper", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelCreeper();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelCreeper)) {
         return null;
      }

      ModelCreeper modelCreeper = (ModelCreeper)modelBase;
      return str.equals("head")
         ? modelCreeper.head
         : (
            str.equals("armor")
               ? modelCreeper.creeperArmor
               : (
                  str.equals("body")
                     ? modelCreeper.body
                     : (
                        str.equals("leg1")
                           ? modelCreeper.leg1
                           : (str.equals("leg2") ? modelCreeper.leg2 : (str.equals("leg3") ? modelCreeper.leg3 : (str.equals("leg4") ? modelCreeper.leg4 : null)))
                     )
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "armor", "body", "leg1", "leg2", "leg3", "leg4"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderCreeper renderCreeper = new RenderCreeper(renderManager);
      renderCreeper.mainModel = modelBase;
      renderCreeper.shadowSize = f;
      return renderCreeper;
   }
}
