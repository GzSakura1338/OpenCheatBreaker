package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderEnderCrystal extends Render<EntityEnderCrystal> {
   public static ResourceLocation enderCrystalTextures = new ResourceLocation("textures/entity/endercrystal/endercrystal.png");
   public ModelBase modelEnderCrystal = new ModelEnderCrystal(0.0F, true);

   public RenderEnderCrystal(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks) {
      float f = entity.innerRotation + partialTicks;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      this.bindTexture(enderCrystalTextures);
      float f2 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
      f2 = f2 * f2 + f2;
      this.modelEnderCrystal.render(entity, 0.0F, f * 3.0F, f2 * 0.2F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityEnderCrystal entity) {
      return enderCrystalTextures;
   }
}
