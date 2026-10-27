package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.entity.boss.EntityWither;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterWither extends ModelAdapter {
   public ModelAdapterWither() {
      super(EntityWither.class, "wither", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelWither(0.0F);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelWither)) {
         return null;
      }

      ModelWither modelWither = (ModelWither)modelBase;
      String str2 = "body";
      if (str.startsWith(str2)) {
         ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelWither, Reflector.ModelWither_bodyParts);
         if (fieldValue == null) {
            return null;
         }

         String substring = str.substring(str2.length());
         int parseInt = Config.parseInt(substring, -1);
         parseInt--;
         return parseInt >= 0 && parseInt < fieldValue.length ? fieldValue[parseInt] : null;
      } else {
         String str3 = "head";
         if (str.startsWith(str3)) {
            ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelWither, Reflector.ModelWither_heads);
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
      return new String[]{"body1", "body2", "body3", "head1", "head2", "head3"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderWither renderWither = new RenderWither(renderManager);
      renderWither.mainModel = modelBase;
      renderWither.shadowSize = f;
      return renderWither;
   }
}
