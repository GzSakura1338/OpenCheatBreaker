package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelArmorStand;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityArmorStand;
import net.optifine.Config;

public class ModelAdapterArmorStand extends ModelAdapterBiped {
   public ModelAdapterArmorStand() {
      super(EntityArmorStand.class, "armor_stand", 0.0F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelArmorStand();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelArmorStand)) {
         return null;
      }

      ModelArmorStand modelArmorStand = (ModelArmorStand)modelBase;
      return str.equals("right")
         ? modelArmorStand.standRightSide
         : (
            str.equals("left")
               ? modelArmorStand.standLeftSide
               : (str.equals("waist") ? modelArmorStand.standWaist : (str.equals("base") ? modelArmorStand.standBase : super.getModelRenderer(modelArmorStand, str)))
         );
   }

   @Override
   public String[] getModelRendererNames() {
      String[] modelRendererNames = super.getModelRendererNames();
      return (String[])Config.addObjectsToArray(modelRendererNames, new String[]{"right", "left", "waist", "base"});
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      ArmorStandRenderer armorStandRenderer = new ArmorStandRenderer(renderManager);
      armorStandRenderer.mainModel = modelBase;
      armorStandRenderer.shadowSize = f;
      return armorStandRenderer;
   }
}
