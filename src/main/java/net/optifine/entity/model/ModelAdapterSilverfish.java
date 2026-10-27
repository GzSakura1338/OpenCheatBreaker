package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSilverfish;
import net.minecraft.entity.monster.EntitySilverfish;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterSilverfish extends ModelAdapter {
   public ModelAdapterSilverfish() {
      super(EntitySilverfish.class, "silverfish", 0.3F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSilverfish();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelSilverfish)) {
         return null;
      }

      ModelSilverfish modelSilverfish = (ModelSilverfish)modelBase;
      String str2 = "body";
      if (str.startsWith(str2)) {
         ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelSilverfish, Reflector.ModelSilverfish_bodyParts);
         if (fieldValue == null) {
            return null;
         }

         String substring = str.substring(str2.length());
         int parseInt = Config.parseInt(substring, -1);
         parseInt--;
         return parseInt >= 0 && parseInt < fieldValue.length ? fieldValue[parseInt] : null;
      } else {
         String str3 = "wing";
         if (str.startsWith(str3)) {
            ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelSilverfish, Reflector.ModelSilverfish_wingParts);
            if (fieldValue == null) {
               return null;
            }

            String substring = str.substring(str3.length());
            int parseInt = Config.parseInt(substring, -1);
            parseInt--;
            return parseInt >= 0 && parseInt < fieldValue.length ? fieldValue[parseInt] : null;
         } else {
            return null;
         }
      }
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"body1", "body2", "body3", "body4", "body5", "body6", "body7", "wing1", "wing2", "wing3"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderSilverfish renderSilverfish = new RenderSilverfish(renderManager);
      renderSilverfish.mainModel = modelBase;
      renderSilverfish.shadowSize = f;
      return renderSilverfish;
   }
}
