package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntitySkull;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterHeadHumanoid extends ModelAdapter {
   public ModelAdapterHeadHumanoid() {
      super(TileEntitySkull.class, "head_humanoid", 0.0F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelHumanoidHead();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelHumanoidHead)) {
         return null;
      }

      ModelHumanoidHead modelHumanoidHead = (ModelHumanoidHead)modelBase;
      return str.equals("head")
         ? modelHumanoidHead.skeletonHead
         : (
            str.equals("head2")
               ? (!Reflector.ModelHumanoidHead_head.exists() ? null : (ModelRenderer)Reflector.getFieldValue(modelHumanoidHead, Reflector.ModelHumanoidHead_head))
               : null
         );
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
