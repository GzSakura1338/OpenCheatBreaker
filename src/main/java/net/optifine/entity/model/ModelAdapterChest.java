package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterChest extends ModelAdapter {
   public ModelAdapterChest() {
      super(TileEntityChest.class, "chest", 0.0F);
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
      TileEntitySpecialRenderer specialRendererByClass = instance.getSpecialRendererByClass(TileEntityChest.class);
      if (!(specialRendererByClass instanceof TileEntityChestRenderer)) {
         return null;
      }

      if (specialRendererByClass.getEntityClass() == null) {
         specialRendererByClass = new TileEntityChestRenderer();
         specialRendererByClass.setRendererDispatcher(instance);
      }

      if (!Reflector.TileEntityChestRenderer_simpleChest.exists()) {
         Config.warn("Field not found: TileEntityChestRenderer.simpleChest");
         return null;
      } else {
         Reflector.setFieldValue(specialRendererByClass, Reflector.TileEntityChestRenderer_simpleChest, modelBase);
         return specialRendererByClass;
      }
   }
}
