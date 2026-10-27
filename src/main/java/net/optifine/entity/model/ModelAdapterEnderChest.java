package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterEnderChest extends ModelAdapter {
   public ModelAdapterEnderChest() {
      super(TileEntityEnderChest.class, "ender_chest", 0.0F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelChest();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelChest)) {
         return null;
      }

      ModelChest modelChest = (ModelChest)modelBase;
      return str.equals("lid") ? modelChest.chestLid : (str.equals("base") ? modelChest.chestBelow : (str.equals("knob") ? modelChest.chestKnob : null));
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"lid", "base", "knob"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      TileEntityRendererDispatcher instance = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer specialRendererByClass = instance.getSpecialRendererByClass(TileEntityEnderChest.class);
      if (!(specialRendererByClass instanceof TileEntityEnderChestRenderer)) {
         return null;
      }

      if (specialRendererByClass.getEntityClass() == null) {
         specialRendererByClass = new TileEntityEnderChestRenderer();
         specialRendererByClass.setRendererDispatcher(instance);
      }

      if (!Reflector.TileEntityEnderChestRenderer_modelChest.exists()) {
         Config.warn("Field not found: TileEntityEnderChestRenderer.modelChest");
         return null;
      } else {
         Reflector.setFieldValue(specialRendererByClass, Reflector.TileEntityEnderChestRenderer_modelChest, modelBase);
         return specialRendererByClass;
      }
   }
}
