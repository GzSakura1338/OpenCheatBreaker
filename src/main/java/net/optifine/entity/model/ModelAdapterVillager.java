package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.passive.EntityVillager;

public class ModelAdapterVillager extends ModelAdapter {
   public ModelAdapterVillager() {
      super(EntityVillager.class, "villager", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelVillager(0.0F);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelVillager)) {
         return null;
      }

      ModelVillager modelVillager = (ModelVillager)modelBase;
      return str.equals("head")
         ? modelVillager.villagerHead
         : (
            str.equals("body")
               ? modelVillager.villagerBody
               : (
                  str.equals("arms")
                     ? modelVillager.villagerArms
                     : (
                        str.equals("left_leg")
                           ? modelVillager.leftVillagerLeg
                           : (str.equals("right_leg") ? modelVillager.rightVillagerLeg : (str.equals("nose") ? modelVillager.villagerNose : null))
                     )
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "body", "arms", "right_leg", "left_leg", "nose"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderVillager renderVillager = new RenderVillager(renderManager);
      renderVillager.mainModel = modelBase;
      renderVillager.shadowSize = f;
      return renderVillager;
   }
}
