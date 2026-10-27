package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.event.SimpleEvent11;
import com.cheatbreaker.client.ui.screen.PreviewPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

public class EntityPoseSnapshot {
   public static void process(int n, int n2, float f, EntityLivingBase entityLivingBase, float f2) {
      GlStateManager.enableDepth();
      GlStateManager.disableBlend();
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translate(n, n2, 100.0F);
      GlStateManager.scale(-f, f, f);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      boolean alwaysRenderNameTag = entityLivingBase.getAlwaysRenderNameTag();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.enableRescaleNormal();
      float renderYawOffset = entityLivingBase.renderYawOffset;
      float rotationYaw = entityLivingBase.rotationYaw;
      float rotationPitch = entityLivingBase.rotationPitch;
      float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
      float rotationYawHead = entityLivingBase.rotationYawHead;
      float prevRotationYaw = entityLivingBase.prevRotationYaw;
      float prevRotationPitch = entityLivingBase.prevRotationPitch;
      float prevRenderYawOffset = entityLivingBase.prevRenderYawOffset;
      entityLivingBase.renderYawOffset = 0.0F;
      entityLivingBase.rotationYaw = 0.0F;
      entityLivingBase.rotationPitch = 0.0F;
      entityLivingBase.rotationYawHead = 0.0F;
      entityLivingBase.prevRotationYawHead = 0.0F;
      entityLivingBase.prevRotationYaw = 0.0F;
      entityLivingBase.prevRotationPitch = 0.0F;
      entityLivingBase.prevRenderYawOffset = 0.0F;
      entityLivingBase.setAlwaysRenderNameTag(false);
      GlStateManager.translate(0.0F, 0.0F, 0.0F);
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      renderManager.setPlayerViewY(180.0F);
      renderManager.setRenderShadow(false);
      renderManager.setRenderShadow(true);
      entityLivingBase.renderYawOffset = renderYawOffset;
      entityLivingBase.rotationYaw = rotationYaw;
      entityLivingBase.rotationPitch = rotationPitch;
      entityLivingBase.prevRotationYawHead = prevRotationYawHead;
      entityLivingBase.rotationYawHead = rotationYawHead;
      entityLivingBase.prevRotationYaw = prevRotationYaw;
      entityLivingBase.prevRotationPitch = prevRotationPitch;
      entityLivingBase.prevRenderYawOffset = prevRenderYawOffset;
      entityLivingBase.setAlwaysRenderNameTag(alwaysRenderNameTag);
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.disableDepth();
   }

   public void onSimpleEvent11(SimpleEvent11 simpleEvent11) {
      if (simpleEvent11.getEntityPlayer() instanceof AbstractClientPlayer && !(simpleEvent11.getEntityPlayer() instanceof PreviewPlayer)) {
         AbstractClientPlayer entityPlayer = (AbstractClientPlayer)simpleEvent11.getEntityPlayer();
         if (!entityPlayer.isSpectator()) {
            EmoteState forEntity = PlayerEmoteState.getForEntity(entityPlayer);
            if (forEntity != null
               && forEntity.getEmote() != null
               && forEntity.renderEmote(entityPlayer, simpleEvent11.getDoubleValue1(), simpleEvent11.getDoubleValue2(), simpleEvent11.getDoubleValue3(), simpleEvent11.getFloatValue())) {
               simpleEvent11.setFlag(true);
            }
         }
      }
   }
}
