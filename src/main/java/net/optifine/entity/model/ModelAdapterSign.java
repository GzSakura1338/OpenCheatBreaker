package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntitySign;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterSign extends ModelAdapter {
   public ModelAdapterSign() {
      super(TileEntitySign.class, "sign", 0.0F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSign();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelSign)) {
         return null;
      }

      ModelSign modelSign = (ModelSign)modelBase;
      return str.equals("board") ? modelSign.signBoard : (str.equals("stick") ? modelSign.signStick : null);
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"board", "stick"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      TileEntityRendererDispatcher instance = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer specialRendererByClass = instance.getSpecialRendererByClass(TileEntitySign.class);
      if (!(specialRendererByClass instanceof TileEntitySignRenderer)) {
         return null;
      }

      if (specialRendererByClass.getEntityClass() == null) {
         specialRendererByClass = new TileEntitySignRenderer();
         specialRendererByClass.setRendererDispatcher(instance);
      }

      if (!Reflector.TileEntitySignRenderer_model.exists()) {
         Config.warn("Field not found: TileEntitySignRenderer.model");
         return null;
      } else {
         Reflector.setFieldValue(specialRendererByClass, Reflector.TileEntitySignRenderer_model, modelBase);
         return specialRendererByClass;
      }
   }
}
