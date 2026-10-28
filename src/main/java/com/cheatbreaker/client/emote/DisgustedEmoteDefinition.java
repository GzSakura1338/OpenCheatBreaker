package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.emote.bobj.BobjArmature;
import com.cheatbreaker.client.emote.bobj.PlayerMeshRenderer;
import javax.vecmath.Vector4f;
import net.minecraft.entity.EntityLivingBase;

public class DisgustedEmoteDefinition extends EmoteDefinition {
    public DisgustedEmoteDefinition(String str, int n, boolean flag) {
        super(str, n, flag);
    }

    @Override
    public void onTick(EntityLivingBase entityLivingBase, BobjArmature bobjArmature, PlayerMeshRenderer playerMeshRenderer, int n, float f) {
        super.onTick(entityLivingBase, bobjArmature, playerMeshRenderer, n, f);
        if (n >= FrameTimeConverter.framesToTicks(117) && n < FrameTimeConverter.framesToTicks(140)) {
            for (int i = 0; i < 10; i++) {
                Vector4f bonePosition = playerMeshRenderer.getBonePosition(entityLivingBase, bobjArmature.bonesByName.get("head"), 0.0F, 0.125F, 0.25F, f);
                bonePosition.y -= 1.5078125F;
                entityLivingBase.worldObj
                    .spawnParticle(
                        "iconcrack_351_2",
                        bonePosition.x + this.randomOffset(0.1F),
                        bonePosition.y,
                        bonePosition.z + this.randomOffset(0.1F),
                        this.randomOffset(0.05F),
                        -0.125,
                        this.randomOffset(0.05F)
                    );
            }
        }
    }
}
