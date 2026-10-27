package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.entity.item.EntityMinecart;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMinecart extends ModelAdapter {
   public ModelAdapterMinecart() {
      super(EntityMinecart.class, "minecart", 0.5F);
   }

   public ModelAdapterMinecart(Class clazz, String str, float f) {
      super(clazz, str, f);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelMinecart();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelMinecart)) {
         return null;
      }

      ModelMinecart modelMinecart = (ModelMinecart)modelBase;
      return str.equals("bottom")
         ? modelMinecart.sideModels[0]
         : (
            str.equals("back")
               ? modelMinecart.sideModels[1]
               : (
                  str.equals("front")
                     ? modelMinecart.sideModels[2]
                     : (
                        str.equals("right")
                           ? modelMinecart.sideModels[3]
                           : (str.equals("left") ? modelMinecart.sideModels[4] : (str.equals("dirt") ? modelMinecart.sideModels[5] : null))
                     )
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"bottom", "back", "front", "right", "left", "dirt"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderMinecart renderMinecart = new RenderMinecart(renderManager);
      if (!Reflector.RenderMinecart_modelMinecart.exists()) {
         Config.warn("Field not found: RenderMinecart.modelMinecart");
         return null;
      } else {
         Reflector.setFieldValue(renderMinecart, Reflector.RenderMinecart_modelMinecart, modelBase);
         renderMinecart.shadowSize = f;
         return renderMinecart;
      }
   }
}
