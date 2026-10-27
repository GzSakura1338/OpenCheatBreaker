package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSquid;
import net.minecraft.entity.passive.EntitySquid;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterSquid extends ModelAdapter {
   public ModelAdapterSquid() {
      super(EntitySquid.class, "squid", 0.7F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSquid();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelSquid)) {
         return null;
      }

      ModelSquid modelSquid = (ModelSquid)modelBase;
      if (str.equals("body")) {
         return (ModelRenderer)Reflector.getFieldValue(modelSquid, Reflector.ModelSquid_body);
      }

      String str2 = "tentacle";
      if (str.startsWith(str2)) {
         ModelRenderer[] fieldValue = (ModelRenderer[])Reflector.getFieldValue(modelSquid, Reflector.ModelSquid_tentacles);
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
      return new String[]{"body", "tentacle1", "tentacle2", "tentacle3", "tentacle4", "tentacle5", "tentacle6", "tentacle7", "tentacle8"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      return new RenderSquid(renderManager, modelBase, f);
   }
}
