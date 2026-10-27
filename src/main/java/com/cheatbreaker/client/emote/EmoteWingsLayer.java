package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.cosmetic.CosmeticType;
import com.cheatbreaker.client.cosmetic.WingModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class EmoteWingsLayer {
   public WingModel wingModel = new WingModel();

   public void handleEntityLivingBase(EntityLivingBase entityLivingBase, float f) {
      if (entityLivingBase instanceof EntityPlayer) {
         EntityPlayer entityPlayer = (EntityPlayer)entityLivingBase;
         if (entityPlayer.check()
            && !entityPlayer.isInvisible()
            && entityPlayer.getCosmetic2().getCosmeticType() == CosmeticType.WINGS
            && (Boolean)CheatBreaker.getInstance().getKeybindSettings().showCheatbreakerWings.getValue()) {
            EmoteState forEntity = PlayerEmoteState.getForEntity(entityPlayer);
            if (forEntity instanceof PlayerEmoteState) {
               PlayerEmoteState playerEmoteState = (PlayerEmoteState)forEntity;
               if (playerEmoteState.emote != null && playerEmoteState.emote.rendersWings() && playerEmoteState.meshes != null && playerEmoteState.meshes.model != null && !playerEmoteState.meshes.model.meshes.isEmpty()) {
                  GlStateManager.pushMatrix();
                  playerEmoteState.meshes.applyBoneTransform(playerEmoteState.meshes.model.meshes.get(0).armature.bonesByName.get("low_body"));
                  GlStateManager.translate(0.0, 0.375, 0.125);
                  GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                  this.wingModel.render(entityPlayer, 0.0625F, entityPlayer.getCosmetic2().getScale(), entityPlayer.getCosmetic2().getTexture());
                  GlStateManager.popMatrix();
               }
            }
         }
      }
   }
}
