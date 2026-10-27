package net.optifine.entity.model;

import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityBanner;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterBanner extends ModelAdapter {
   public ModelAdapterBanner() {
      super(TileEntityBanner.class, "banner", 0.0F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelBanner();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelBanner)) {
         return null;
      }

      ModelBanner modelBanner = (ModelBanner)modelBase;
      return str.equals("slate") ? modelBanner.bannerSlate : (str.equals("stand") ? modelBanner.bannerStand : (str.equals("top") ? modelBanner.bannerTop : null));
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"slate", "stand", "top"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      TileEntityRendererDispatcher instance = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer specialRendererByClass = instance.getSpecialRendererByClass(TileEntityBanner.class);
      if (!(specialRendererByClass instanceof TileEntityBannerRenderer)) {
         return null;
      }

      if (specialRendererByClass.getEntityClass() == null) {
         specialRendererByClass = new TileEntityBannerRenderer();
         specialRendererByClass.setRendererDispatcher(instance);
      }

      if (!Reflector.TileEntityBannerRenderer_bannerModel.exists()) {
         Config.warn("Field not found: TileEntityBannerRenderer.bannerModel");
         return null;
      } else {
         Reflector.setFieldValue(specialRendererByClass, Reflector.TileEntityBannerRenderer_bannerModel, modelBase);
         return specialRendererByClass;
      }
   }
}
