package net.minecraft.client.renderer.entity.layers;

import com.cheatbreaker.client.CheatBreaker;
import java.util.Random;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;

public class LayerArrow implements LayerRenderer<EntityLivingBase> {
   public RendererLivingEntity entityRenderer;

   public LayerArrow(RendererLivingEntity rendererLivingEntity) {
      this.entityRenderer = rendererLivingEntity;
   }

   @Override
   public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      if (!CheatBreaker.getInstance().getKeybindSettings().hideStuckArrows.getBooleanValue()) {
         int arrowCountInEntity = entitylivingbaseIn.getArrowCountInEntity();
         if (arrowCountInEntity > 0) {
            EntityArrow entityArrow = new EntityArrow(entitylivingbaseIn.worldObj, entitylivingbaseIn.posX, entitylivingbaseIn.posY, entitylivingbaseIn.posZ);
            Random random = new Random(entitylivingbaseIn.getEntityId());
            RenderHelper.disableStandardItemLighting();

            for (int i = 0; i < arrowCountInEntity; i++) {
               GlStateManager.pushMatrix();
               ModelRenderer randomModelBox = this.entityRenderer.getMainModel().getRandomModelBox(random);
               ModelBox modelBox = randomModelBox.cubeList.get(random.nextInt(randomModelBox.cubeList.size()));
               randomModelBox.postRender(0.0625F);
               float nextFloat = random.nextFloat();
               float nextFloat2 = random.nextFloat();
               float nextFloat3 = random.nextFloat();
               float f6 = (modelBox.posX1 + (modelBox.posX2 - modelBox.posX1) * nextFloat) / 16.0F;
               float f7 = (modelBox.posY1 + (modelBox.posY2 - modelBox.posY1) * nextFloat2) / 16.0F;
               float f8 = (modelBox.posZ1 + (modelBox.posZ2 - modelBox.posZ1) * nextFloat3) / 16.0F;
               GlStateManager.translate(f6, f7, f8);
               nextFloat = nextFloat * 2.0F - 1.0F;
               nextFloat2 = nextFloat2 * 2.0F - 1.0F;
               nextFloat3 = nextFloat3 * 2.0F - 1.0F;
               nextFloat *= -1.0F;
               nextFloat2 *= -1.0F;
               nextFloat3 *= -1.0F;
               float f9 = MathHelper.sqrt_float(nextFloat * nextFloat + nextFloat3 * nextFloat3);
               entityArrow.prevRotationYaw = entityArrow.rotationYaw = (float)(Math.atan2(nextFloat, nextFloat3) * 180.0 / Math.PI);
               entityArrow.prevRotationPitch = entityArrow.rotationPitch = (float)(Math.atan2(nextFloat2, f9) * 180.0 / Math.PI);
               double d = 0.0;
               double d2 = 0.0;
               double d3 = 0.0;
               this.entityRenderer.getRenderManager().renderEntityWithPosYaw(entityArrow, d, d2, d3, 0.0F, partialTicks);
               GlStateManager.popMatrix();
            }

            RenderHelper.enableStandardItemLighting();
         }
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
