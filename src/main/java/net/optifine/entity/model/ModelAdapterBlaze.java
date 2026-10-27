package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBlaze;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBlaze;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityBlaze;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterBlaze extends ModelAdapter {
   public ModelAdapterBlaze() {
      super(EntityBlaze.class, "blaze", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelBlaze();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelBlaze)) {
         return null;
      }

      ModelBlaze modelBlaze = (ModelBlaze)modelBase;
      if (str.equals("head")) {
         return (ModelRenderer)Reflector.getFieldValue(modelBlaze, Reflector.ModelBlaze_blazeHead);
      }

      String str2 = "stick";
      if (str.startsWith(str2)) {
         ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelBlaze, Reflector.ModelBlaze_blazeSticks);
         if (fieldValue == null) {
            return null;
         }

         String substring = str.substring(str2.length());
         int parseInt = Config.parseInt(substring, -1);
         parseInt--;
         return parseInt >= 0 && parseInt < fieldValue.length ? fieldValue[parseInt] : null;
      } else {
         return null;
      }
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head", "stick1", "stick2", "stick3", "stick4", "stick5", "stick6", "stick7", "stick8", "stick9", "stick10", "stick11", "stick12"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderBlaze renderBlaze = new RenderBlaze(renderManager);
      renderBlaze.mainModel = modelBase;
      renderBlaze.shadowSize = f;
      return renderBlaze;
   }
}
