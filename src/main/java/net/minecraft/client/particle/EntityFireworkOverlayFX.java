package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkOverlayFX extends EntityFX {
    public EntityFireworkOverlayFX(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
        this.particleMaxAge = 4;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = 0.25F;
        float f8 = f7 + 0.25F;
        float f9 = 0.125F;
        float f10 = f9 + 0.25F;
        float f11 = 7.1F * MathHelper.sin((this.particleAge + f - 1.0F) * 0.25F * (float) Math.PI);
        this.particleAlpha = 0.6F - (this.particleAge + f - 1.0F) * 0.25F * 0.5F;
        float f12 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - interpPosX);
        float f13 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - interpPosY);
        float f14 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - interpPosZ);
        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tessellator.addVertexWithUV(f12 - f2 * f11 - f5 * f11, f13 - f3 * f11, f14 - f4 * f11 - f6 * f11, f8, f10);
        tessellator.addVertexWithUV(f12 - f2 * f11 + f5 * f11, f13 + f3 * f11, f14 - f4 * f11 + f6 * f11, f8, f9);
        tessellator.addVertexWithUV(f12 + f2 * f11 + f5 * f11, f13 + f3 * f11, f14 + f4 * f11 + f6 * f11, f7, f9);
        tessellator.addVertexWithUV(f12 + f2 * f11 - f5 * f11, f13 - f3 * f11, f14 + f4 * f11 - f6 * f11, f7, f10);
    }
}
