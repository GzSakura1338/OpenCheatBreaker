package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityBreakingFX extends EntityFX {
    public EntityBreakingFX(World world, double d, double d2, double d3, Item item) {
        this(world, d, d2, d3, item, 0);
    }

    public EntityBreakingFX(World world, double d, double d2, double d3, Item item, int n) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.setParticleIcon(item.getIconFromDamage(n));
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleGravity = Blocks.snow.blockParticleGravity;
        this.particleScale /= 2.0F;
    }

    public EntityBreakingFX(World world, double d, double d2, double d3, double d4, double d5, double d6, Item item, int n) {
        this(world, d, d2, d3, item, n);
        this.motionX *= 0.1F;
        this.motionY *= 0.1F;
        this.motionZ *= 0.1F;
        this.motionX += d4;
        this.motionY += d5;
        this.motionZ += d6;
    }

    @Override
    public int getFXLayer() {
        return 2;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = (this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
        float f8 = f7 + 0.015609375F;
        float f9 = (this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
        float f10 = f9 + 0.015609375F;
        float f11 = 0.1F * this.particleScale;
        if (this.particleIcon != null) {
            f7 = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0F * 16.0F);
            f8 = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
            f9 = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0F * 16.0F);
            f10 = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
        }

        float f12 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - interpPosX);
        float f13 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - interpPosY);
        float f14 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - interpPosZ);
        tessellator.setColorOpaque_F(this.particleRed, this.particleGreen, this.particleBlue);
        tessellator.addVertexWithUV(f12 - f2 * f11 - f5 * f11, f13 - f3 * f11, f14 - f4 * f11 - f6 * f11, f7, f10);
        tessellator.addVertexWithUV(f12 - f2 * f11 + f5 * f11, f13 + f3 * f11, f14 - f4 * f11 + f6 * f11, f7, f9);
        tessellator.addVertexWithUV(f12 + f2 * f11 + f5 * f11, f13 + f3 * f11, f14 + f4 * f11 + f6 * f11, f8, f9);
        tessellator.addVertexWithUV(f12 + f2 * f11 - f5 * f11, f13 - f3 * f11, f14 + f4 * f11 - f6 * f11, f8, f10);
    }
}
