package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class EntityDiggingFX extends EntityFX {
    public Block block;

    public EntityDiggingFX(World world, double d, double d2, double d3, double d4, double d5, double d6, Block block, int n) {
        super(world, d, d2, d3, d4, d5, d6);
        this.block = block;
        this.setParticleIcon(block.getIcon(0, n));
        this.particleGravity = block.blockParticleGravity;
        this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
        this.particleScale /= 2.0F;
    }

    public EntityDiggingFX applyColourMultiplier(int n, int n2, int n3) {
        if (this.block == Blocks.grass) {
            return this;
        }

        int colorMultiplier = this.block.colorMultiplier(this.worldObj, n, n2, n3);
        this.particleRed *= (colorMultiplier >> 16 & 0xFF) / 255.0F;
        this.particleGreen *= (colorMultiplier >> 8 & 0xFF) / 255.0F;
        this.particleBlue *= (colorMultiplier & 0xFF) / 255.0F;
        return this;
    }

    public EntityDiggingFX applyRenderColor(int n) {
        if (this.block == Blocks.grass) {
            return this;
        }

        int renderColor = this.block.getRenderColor(n);
        this.particleRed *= (renderColor >> 16 & 0xFF) / 255.0F;
        this.particleGreen *= (renderColor >> 8 & 0xFF) / 255.0F;
        this.particleBlue *= (renderColor & 0xFF) / 255.0F;
        return this;
    }

    @Override
    public int getFXLayer() {
        return 1;
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
