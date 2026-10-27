package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderGhast;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGhast;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterGhast extends ModelAdapter {
   public ModelAdapterGhast() {
      super(EntityGhast.class, "ghast", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelGhast();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelGhast)) {
         return null;
      }

      ModelGhast modelGhast = (ModelGhast)modelBase;
      if (str.equals("body")) {
         return (ModelRenderer)Reflector.getFieldValue(modelGhast, Reflector.ModelGhast_body);
      }

      String str2 = "tentacle";
      if (str.startsWith(str2)) {
         ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelGhast, Reflector.ModelGhast_tentacles);
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
      return new String[]{"body", "tentacle1", "tentacle2", "tentacle3", "tentacle4", "tentacle5", "tentacle6", "tentacle7", "tentacle8", "tentacle9"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderGhast renderGhast = new RenderGhast(renderManager);
      renderGhast.mainModel = modelBase;
      renderGhast.shadowSize = f;
      return renderGhast;
   }
}
