package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowMan;
import net.minecraft.entity.monster.EntitySnowman;

public class ModelAdapterSnowman extends ModelAdapter {
   public ModelAdapterSnowman() {
      super(EntitySnowman.class, "snow_golem", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSnowMan();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelSnowMan)) {
         return null;
      }

      ModelSnowMan modelSnowMan = (ModelSnowMan)modelBase;
      return str.equals("body")
         ? modelSnowMan.body
         : (
            str.equals("body_bottom")
               ? modelSnowMan.bottomBody
               : (str.equals("head") ? modelSnowMan.head : (str.equals("left_hand") ? modelSnowMan.leftHand : (str.equals("right_hand") ? modelSnowMan.rightHand : null)))
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"body", "body_bottom", "head", "right_hand", "left_hand"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderSnowMan renderSnowMan = new RenderSnowMan(renderManager);
      renderSnowMan.mainModel = modelBase;
      renderSnowMan.shadowSize = f;
      return renderSnowMan;
   }
}
