package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterEnderCrystal extends ModelAdapter {
   public ModelAdapterEnderCrystal() {
      this("end_crystal");
   }

   public ModelAdapterEnderCrystal(String str) {
      super(EntityEnderCrystal.class, str, 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelEnderCrystal(0.0F, true);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelEnderCrystal)) {
         return null;
      }

      ModelEnderCrystal modelEnderCrystal = (ModelEnderCrystal)modelBase;
      return str.equals("cube")
         ? (ModelRenderer)Reflector.getFieldValue(modelEnderCrystal, Reflector.ModelEnderCrystal_ModelRenderers, 0)
         : (
            str.equals("glass")
               ? (ModelRenderer)Reflector.getFieldValue(modelEnderCrystal, Reflector.ModelEnderCrystal_ModelRenderers, 1)
               : (str.equals("base") ? (ModelRenderer)Reflector.getFieldValue(modelEnderCrystal, Reflector.ModelEnderCrystal_ModelRenderers, 2) : null)
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"cube", "glass", "base"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      Render render = renderManager.getEntityRenderMap().get(EntityEnderCrystal.class);
      if (!(render instanceof RenderEnderCrystal)) {
         Config.warn("Not an instance of RenderEnderCrystal: " + render);
         return null;
      } else {
         RenderEnderCrystal renderEnderCrystal = (RenderEnderCrystal)render;
         if (!Reflector.RenderEnderCrystal_modelEnderCrystal.exists()) {
            Config.warn("Field not found: RenderEnderCrystal.modelEnderCrystal");
            return null;
         } else {
            Reflector.setFieldValue(renderEnderCrystal, Reflector.RenderEnderCrystal_modelEnderCrystal, modelBase);
            renderEnderCrystal.shadowSize = f;
            return renderEnderCrystal;
         }
      }
   }
}
