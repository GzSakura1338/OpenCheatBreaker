package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityIronGolem;

public class ModelAdapterIronGolem extends ModelAdapter {
   public ModelAdapterIronGolem() {
      super(EntityIronGolem.class, "iron_golem", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelIronGolem();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelIronGolem)) {
         return null;
      }

      ModelIronGolem modelIronGolem = (ModelIronGolem)modelBase;
      return str.equals("head")
         ? modelIronGolem.ironGolemHead
         : (
            str.equals("body")
               ? modelIronGolem.ironGolemBody
               : (
                  str.equals("left_arm")
                     ? modelIronGolem.ironGolemLeftArm
                     : (
                        str.equals("right_arm")
                           ? modelIronGolem.ironGolemRightArm
                           : (str.equals("left_leg") ? modelIronGolem.ironGolemLeftLeg : (str.equals("right_leg") ? modelIronGolem.ironGolemRightLeg : null))
                     )
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "body", "right_arm", "left_arm", "left_leg", "right_leg"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderIronGolem renderIronGolem = new RenderIronGolem(renderManager);
      renderIronGolem.mainModel = modelBase;
      renderIronGolem.shadowSize = f;
      return renderIronGolem;
   }
}
