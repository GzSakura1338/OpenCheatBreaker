package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderEndermite;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityEndermite;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterEndermite extends ModelAdapter {
   public ModelAdapterEndermite() {
      super(EntityEndermite.class, "endermite", 0.3F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelEnderMite();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelEnderMite)) {
         return null;
      }

      ModelEnderMite modelEnderMite = (ModelEnderMite)modelBase;
      String str2 = "body";
      if (str.startsWith(str2)) {
         ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelEnderMite, Reflector.ModelEnderMite_bodyParts);
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
      return new String[]{"body1", "body2", "body3", "body4"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderEndermite renderEndermite = new RenderEndermite(renderManager);
      renderEndermite.mainModel = modelBase;
      renderEndermite.shadowSize = f;
      return renderEndermite;
   }
}
