package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.emote.bobj.BobjArmature;
import com.cheatbreaker.client.emote.bobj.PlayerMeshRenderer;
import javax.vecmath.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public class PureSaltEmoteDefinition extends EmoteDefinition {
   public PureSaltEmoteDefinition(String str, int n, boolean flag) {
      super(str, n, flag);
   }

   @Override
   public void onTick(EntityLivingBase entityLivingBase, BobjArmature bobjArmature, PlayerMeshRenderer playerMeshRenderer, int n, float f) {
      if (n > 18 && n <= 78 && n % 2 == 0) {
         Bone bone = bobjArmature.bonesByName.get("low_right_arm.end");
         Vector4f bonePosition = playerMeshRenderer.getBonePosition(entityLivingBase, bone, 0.0F, 0.15F, 0.0F, f);
         int n2 = n == 78 ? 12 : 1;

         for (int i = 0; i < n2; i++) {
            SaltParticle saltParticle = new SaltParticle(entityLivingBase.worldObj, bonePosition.x, bonePosition.y, bonePosition.z, 0.0);
            Minecraft.getMinecraft().effectRenderer.addEffect(saltParticle);
         }
      }
   }
}
