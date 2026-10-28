package com.cheatbreaker.client.emote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class EmoteCapeLayer {
    public RenderPlayer renderPlayer;

    public EmoteCapeLayer(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
    }

    public void render(EntityLivingBase entityLivingBase, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        if (entityLivingBase instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entityLivingBase;
            if (abstractClientPlayer.hasCape() && !abstractClientPlayer.isInvisible() && !abstractClientPlayer.getHideCape() && abstractClientPlayer.getLocationCape() != null) {
                EmoteState forEntity = PlayerEmoteState.getForEntity(abstractClientPlayer);
                if (forEntity instanceof PlayerEmoteState) {
                    PlayerEmoteState playerEmoteState = (PlayerEmoteState)forEntity;
                    if (playerEmoteState.emote != null
                        && playerEmoteState.emote.rendersCape()
                        && playerEmoteState.meshes != null
                        && playerEmoteState.meshes.model != null
                        && !playerEmoteState.meshes.model.meshes.isEmpty()) {
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        Minecraft.getMinecraft().getTextureManager().bindTexture(abstractClientPlayer.getLocationCape());
                        GL11.glPushMatrix();
                        playerEmoteState.meshes.applyBoneTransform(playerEmoteState.meshes.model.meshes.get(0).armature.bonesByName.get("low_body"));
                        GL11.glTranslatef(0.0F, 0.375F, 0.125F);
                        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                        double d = abstractClientPlayer.capeX + (abstractClientPlayer.prevCapeX - abstractClientPlayer.capeX) * f3 - (abstractClientPlayer.prevPosX + (abstractClientPlayer.posX - abstractClientPlayer.prevPosX) * f3);
                        double d2 = abstractClientPlayer.capeY + (abstractClientPlayer.prevCapeY - abstractClientPlayer.capeY) * f3 - (abstractClientPlayer.prevPosY + (abstractClientPlayer.posY - abstractClientPlayer.prevPosY) * f3);
                        double d3 = abstractClientPlayer.capeZ + (abstractClientPlayer.prevCapeZ - abstractClientPlayer.capeZ) * f3 - (abstractClientPlayer.prevPosZ + (abstractClientPlayer.posZ - abstractClientPlayer.prevPosZ) * f3);
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
                        GL11.glRotatef(6.0F + f10 / 2.0F + f12, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(f11 / 2.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glRotatef(-f11 / 2.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                        ((ModelBiped)this.renderPlayer.mainModel).renderCloak(0.0625F);
                        GL11.glPopMatrix();
                    }
                }
            }
        }
    }
}
