package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderMagmaCube;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMagmaCube extends ModelAdapter {
   public ModelAdapterMagmaCube() {
      super(EntityMagmaCube.class, "magma_cube", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelMagmaCube();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelMagmaCube)) {
         return null;
      }

      ModelMagmaCube modelMagmaCube = (ModelMagmaCube)modelBase;
      if (str.equals("core")) {
         return (ModelRenderer)Reflector.getFieldValue(modelMagmaCube, Reflector.ModelMagmaCube_core);
      }

      String str2 = "segment";
      if (str.startsWith(str2)) {
         ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelMagmaCube, Reflector.ModelMagmaCube_segments);
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
      return new String[]{"core", "segment1", "segment2", "segment3", "segment4", "segment5", "segment6", "segment7", "segment8"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderMagmaCube renderMagmaCube = new RenderMagmaCube(renderManager);
      renderMagmaCube.mainModel = modelBase;
      renderMagmaCube.shadowSize = f;
      return renderMagmaCube;
   }
}
