package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.emote.bobj.BobjArmature;
import com.cheatbreaker.client.emote.bobj.PlayerMeshRenderer;
import javax.vecmath.Vector4f;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;

public class CryingEmoteDefinition extends EmoteDefinition {
   @Override
   public void onTick(EntityLivingBase entityLivingBase, BobjArmature bobjArmature, PlayerMeshRenderer playerMeshRenderer, int n, float f) {
      if (n % 2 == 0) {
         Bone bone = bobjArmature.bonesByName.get("head");
         Vector4f bonePosition = playerMeshRenderer.getBonePosition(entityLivingBase, bone, 0.0F, 0.5F, 0.35F, f);
         entityLivingBase.worldObj.spawnParticle(EnumParticleTypes.WATER_DROP, bonePosition.x, bonePosition.y, bonePosition.z, 1.0, -1.0, 1.0);
      }
   }

   public CryingEmoteDefinition(String str, int n, boolean flag) {
      super(str, n, flag);
   }
}
