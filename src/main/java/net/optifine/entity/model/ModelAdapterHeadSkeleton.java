package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntitySkull;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterHeadSkeleton extends ModelAdapter {
   public ModelAdapterHeadSkeleton() {
      super(TileEntitySkull.class, "head_skeleton", 0.0F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSkeletonHead(0, 0, 64, 32);
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelSkeletonHead)) {
         return null;
      }

      ModelSkeletonHead modelSkeletonHead = (ModelSkeletonHead)modelBase;
      return str.equals("head") ? modelSkeletonHead.skeletonHead : null;
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"head"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      TileEntityRendererDispatcher instance = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer specialRendererByClass = instance.getSpecialRendererByClass(TileEntitySkull.class);
      if (!(specialRendererByClass instanceof TileEntitySkullRenderer)) {
         return null;
      }

      if (specialRendererByClass.getEntityClass() == null) {
         specialRendererByClass = new TileEntitySkullRenderer();
         specialRendererByClass.setRendererDispatcher(instance);
      }

      if (!Reflector.TileEntitySkullRenderer_humanoidHead.exists()) {
         Config.warn("Field not found: TileEntitySkullRenderer.humanoidHead");
         return null;
      } else {
         Reflector.setFieldValue(specialRendererByClass, Reflector.TileEntitySkullRenderer_humanoidHead, modelBase);
         return specialRendererByClass;
      }
   }
}
