package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelArmorStand;
import net.minecraft.client.model.ModelArmorStandArmor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.ResourceLocation;

public class ArmorStandRenderer extends RendererLivingEntity<EntityArmorStand> {
   public static ResourceLocation TEXTURE_ARMOR_STAND = new ResourceLocation("textures/entity/armorstand/wood.png");

   public ArmorStandRenderer(RenderManager renderManager) {
      super(renderManager, new ModelArmorStand(), 0.0F);
      LayerBipedArmor layerBipedArmor = new LayerBipedArmor(this) {
         @Override
         public void initArmor() {
            this.modelLeggings = new ModelArmorStandArmor(0.5F);
            this.modelArmor = new ModelArmorStandArmor(1.0F);
         }
      };
      this.addLayer(layerBipedArmor);
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
   }

   public ResourceLocation getEntityTexture(EntityArmorStand entity) {
      return TEXTURE_ARMOR_STAND;
   }

   public ModelArmorStand getMainModel() {
      return (ModelArmorStand)super.getMainModel();
   }

   public void rotateCorpse(EntityArmorStand bat, float f, float f2, float partialTicks) {
      GlStateManager.rotate(180.0F - f2, 0.0F, 1.0F, 0.0F);
   }

   public boolean canRenderName(EntityArmorStand entity) {
      return entity.getAlwaysRenderNameTag();
   }
}
