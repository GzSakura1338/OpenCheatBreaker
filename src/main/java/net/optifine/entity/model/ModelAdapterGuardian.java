package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderGuardian;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGuardian;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterGuardian extends ModelAdapter {
   public ModelAdapterGuardian() {
      super(EntityGuardian.class, "guardian", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelGuardian();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelGuardian)) {
         return null;
      }

      ModelGuardian modelGuardian = (ModelGuardian)modelBase;
      if (str.equals("body")) {
         return (ModelRenderer)Reflector.getFieldValue(modelGuardian, Reflector.ModelGuardian_body);
      }

      if (str.equals("eye")) {
         return (ModelRenderer)Reflector.getFieldValue(modelGuardian, Reflector.ModelGuardian_eye);
      }

      String str2 = "spine";
      if (str.startsWith(str2)) {
         ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelGuardian, Reflector.ModelGuardian_spines);
         if (fieldValue == null) {
            return null;
         }

         String substring = str.substring(str2.length());
         int parseInt = Config.parseInt(substring, -1);
         parseInt--;
         return parseInt >= 0 && parseInt < fieldValue.length ? fieldValue[parseInt] : null;
      } else {
         String str3 = "tail";
         if (str.startsWith(str3)) {
            ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelGuardian, Reflector.ModelGuardian_tail);
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
      return new String[]{
         "body",
         "eye",
         "spine1",
         "spine2",
         "spine3",
         "spine4",
         "spine5",
         "spine6",
         "spine7",
         "spine8",
         "spine9",
         "spine10",
         "spine11",
         "spine12",
         "tail1",
         "tail2",
         "tail3"
      };
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderGuardian renderGuardian = new RenderGuardian(renderManager);
      renderGuardian.mainModel = modelBase;
      renderGuardian.shadowSize = f;
      return renderGuardian;
   }
}
