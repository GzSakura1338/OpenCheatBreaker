package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.emote.bobj.BobjArmature;
import com.cheatbreaker.client.emote.bobj.PlayerMeshRenderer;
import javax.vecmath.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public class PopcornEmoteDefinition extends EmoteDefinition {
    @Override
    public void onStop(PlayerMeshRenderer playerMeshRenderer) {
        playerMeshRenderer.modelDefinition.meshMaterials.get("popcorn").visible = false;
    }

    @Override
    public void onStart(PlayerMeshRenderer playerMeshRenderer) {
        playerMeshRenderer.modelDefinition.meshMaterials.get("popcorn").visible = true;
    }

    @Override
    public void onTick(EntityLivingBase entityLivingBase, BobjArmature bobjArmature, PlayerMeshRenderer playerMeshRenderer, int n, float f) {
        if (n == 8 || n == 32 || n == 56 || n == 86) {
            Bone bone = bobjArmature.bonesByName.get("low_right_arm.end");
            Vector4f bonePosition = playerMeshRenderer.getBonePosition(entityLivingBase, bone, 0.0F, 0.15F, 0.0F, f);
            bonePosition.y -= 1.5078125F;

            for (int i = 0; i < 15; i++) {
                PopcornParticle popcornParticle = new PopcornParticle(entityLivingBase.worldObj, bonePosition.x, bonePosition.y, bonePosition.z, 0.1);
                Minecraft.getMinecraft().effectRenderer.addEffect(popcornParticle);
            }
        }
    }

    public PopcornEmoteDefinition(String str, int n, boolean flag) {
        super(str, n, flag);
    }
}
