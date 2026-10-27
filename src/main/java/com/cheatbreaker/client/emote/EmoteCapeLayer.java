package com.cheatbreaker.client.emote;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class EmoteCapeLayer extends LayerCape {
   public RenderPlayer renderPlayer;

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }

   public void doRenderLayer2(EntityLivingBase entityLivingBase, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
      if (entityLivingBase instanceof AbstractClientPlayer) {
         this.doRenderLayer((AbstractClientPlayer)entityLivingBase, f, f2, f3, f4, f5, f6, f7);
      }
   }

   public EmoteCapeLayer(RenderPlayer renderPlayer) {
      super(renderPlayer);
      this.renderPlayer = renderPlayer;
   }

   @Override
   public void doRenderLayer(AbstractClientPlayer abstractClientPlayer, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
      if (abstractClientPlayer.hasPlayerInfo() && !abstractClientPlayer.isInvisible() && abstractClientPlayer.isWearing(EnumPlayerModelParts.CAPE) && abstractClientPlayer.getLocationCape() != null) {
         EmoteState forEntity = PlayerEmoteState.getForEntity(abstractClientPlayer);
         if (forEntity instanceof PlayerEmoteState) {
            PlayerEmoteState playerEmoteState = (PlayerEmoteState)forEntity;
            if (playerEmoteState.emote != null && playerEmoteState.emote.rendersCape() && playerEmoteState.meshes != null && playerEmoteState.meshes.model != null && !playerEmoteState.meshes.model.meshes.isEmpty()) {
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               this.renderPlayer.bindTexture(abstractClientPlayer.getLocationCape());
               GlStateManager.pushMatrix();
               playerEmoteState.meshes.applyBoneTransform(playerEmoteState.meshes.model.meshes.get(0).armature.bonesByName.get("low_body"));
               GlStateManager.translate(0.0, 0.375, 0.125);
               GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
               double d = abstractClientPlayer.prevChasingPosX + (abstractClientPlayer.chasingPosX - abstractClientPlayer.prevChasingPosX) * f3 - (abstractClientPlayer.prevPosX + (abstractClientPlayer.posX - abstractClientPlayer.prevPosX) * f3);
               double d2 = abstractClientPlayer.prevChasingPosY + (abstractClientPlayer.chasingPosY - abstractClientPlayer.prevChasingPosY) * f3 - (abstractClientPlayer.prevPosY + (abstractClientPlayer.posY - abstractClientPlayer.prevPosY) * f3);
               double d3 = abstractClientPlayer.prevChasingPosZ + (abstractClientPlayer.chasingPosZ - abstractClientPlayer.prevChasingPosZ) * f3 - (abstractClientPlayer.prevPosZ + (abstractClientPlayer.posZ - abstractClientPlayer.prevPosZ) * f3);
               float f8 = abstractClientPlayer.prevRenderYawOffset + (abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset) * f3;
               double sin = MathHelper.sin(f8 * (float) Math.PI / 180.0F);
               double d4 = -MathHelper.cos(f8 * (float) Math.PI / 180.0F);
               float f9 = MathHelper.clamp_float((float)d2 * 10.0F, -6.0F, 32.0F);
               float f10 = (float)(d * sin + d3 * d4) * 100.0F;
               float f11 = (float)(d * d4 - d3 * sin) * 100.0F;
               if (f10 < 0.0F) {
                  f10 = 0.0F;
               }

               if (f10 > 165.0F) {
                  f10 = 165.0F;
               }

               float f12 = f9
                  + MathHelper.sin((abstractClientPlayer.prevDistanceWalkedModified + (abstractClientPlayer.distanceWalkedModified - abstractClientPlayer.prevDistanceWalkedModified) * f3) * 6.0F)
                     * 32.0F
                     * (abstractClientPlayer.prevCameraYaw + (abstractClientPlayer.cameraYaw - abstractClientPlayer.prevCameraYaw) * f3);
               GlStateManager.rotate(6.0F + f10 / 2.0F + f12, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(f11 / 2.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.rotate(-f11 / 2.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               this.renderPlayer.getMainModel().renderCape(0.0625F);
               GlStateManager.popMatrix();
            }
         }
      }
   }
}
