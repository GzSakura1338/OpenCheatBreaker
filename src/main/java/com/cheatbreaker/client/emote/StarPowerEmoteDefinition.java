package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.emote.bobj.BobjArmature;
import com.cheatbreaker.client.emote.bobj.PlayerMeshRenderer;
import javax.vecmath.Vector4f;
import net.minecraft.entity.EntityLivingBase;

public class StarPowerEmoteDefinition extends EmoteDefinition {
    public StarPowerEmoteDefinition(String str, int n, boolean flag) {
        super(str, n, flag);
    }

    @Override
    public void onTick(EntityLivingBase entityLivingBase, BobjArmature bobjArmature, PlayerMeshRenderer playerMeshRenderer, int n, float f) {
        if (n == 30) {
            Bone bone = bobjArmature.bonesByName.get("low_right_arm.end");
            Vector4f bonePosition = playerMeshRenderer.getBonePosition(entityLivingBase, bone, 0.0F, 0.15F, 0.0F, f);
            bonePosition.y -= 1.5078125F;

            for (int i = 0; i < 15; i++) {
                entityLivingBase.worldObj
                    .spawnParticle(
                        "enchantmenttable",
                        bonePosition.x,
                        bonePosition.y,
                        bonePosition.z,
                        this.random.nextDouble() * 0.05 - 0.025,
                        this.random.nextDouble() * 0.05 - 0.025,
                        this.random.nextDouble() * 0.05 - 0.025
                    );
            }
        }

        if (n >= 33 && n < 43) {
            Bone bone = bobjArmature.bonesByName.get("low_right_arm.end");
            Vector4f bonePosition = playerMeshRenderer.getBonePosition(entityLivingBase, bone, 0.0F, 0.15F, 0.0F, f);
            bonePosition.y -= 1.5078125F;
            float f2 = 1.0F;
            float f3 = 0.0F;
            float f4 = 0.0F;
            float f5 = (n - 33) / 10.0F;
            if (f5 >= 0.2) {
                if (f5 < 0.35) {
                    f3 = 0.5F;
                } else if (f5 < 0.45) {
                    f3 = 1.0F;
                } else if (f5 < 0.65) {
                    f2 = 0.25F;
                    f3 = 1.0F;
                } else if (f5 < 0.85) {
                    f2 = 0.0F;
                    f3 = 0.75F;
                    f4 = 1.0F;
                } else {
                    f2 = 0.0F;
                    f3 = 0.0F;
                    f4 = 1.0F;
                }
            }

            for (int i = 0; i < 7; i++) {
                entityLivingBase.worldObj
                    .spawnParticle(
                        "mobSpell",
                        bonePosition.x + this.random.nextDouble() * 0.05 - 0.025,
                        bonePosition.y + this.random.nextDouble() * 0.05 - 0.025,
                        bonePosition.z + this.random.nextDouble() * 0.05 - 0.025,
                        f2,
                        f3,
                        f4
                    );
            }
        }
    }
}
