package net.minecraft.client.particle;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EntityLargeExplodeFX extends EntityFX {
    public static ResourceLocation EXPLOSION = new ResourceLocation("textures/entity/explosion.png");
    public int intValue;
    public TextureManager theRenderEngine;
    public int intValue2;
    public float floatValue;

    public EntityLargeExplodeFX(TextureManager textureManager, World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.theRenderEngine = textureManager;
        this.intValue2 = 6 + this.rand.nextInt(4);
        this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.6F + 0.4F;
        this.floatValue = 1.0F - (float)d4 * 0.5F;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
        int n = (int)((this.intValue + f) * 15.0F / this.intValue2);
        if (n <= 15) {
            this.theRenderEngine.bindTexture(EXPLOSION);
            float f7 = n % 4 / 4.0F;
            float f8 = f7 + 0.24975F;
            float f9 = n / 4 / 4.0F;
            float f10 = f9 + 0.24975F;
            float f11 = 2.0F * this.floatValue;
            float f12 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - interpPosX);
            float f13 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - interpPosY);
            float f14 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - interpPosZ);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(2896);
            RenderHelper.disableStandardItemLighting();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 1.0F);
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            tessellator.setBrightness(240);
            tessellator.addVertexWithUV(f12 - f2 * f11 - f5 * f11, f13 - f3 * f11, f14 - f4 * f11 - f6 * f11, f8, f10);
            tessellator.addVertexWithUV(f12 - f2 * f11 + f5 * f11, f13 + f3 * f11, f14 - f4 * f11 + f6 * f11, f8, f9);
            tessellator.addVertexWithUV(f12 + f2 * f11 + f5 * f11, f13 + f3 * f11, f14 + f4 * f11 + f6 * f11, f7, f9);
            tessellator.addVertexWithUV(f12 + f2 * f11 - f5 * f11, f13 - f3 * f11, f14 + f4 * f11 - f6 * f11, f7, f10);
            tessellator.draw();
            GL11.glPolygonOffset(0.0F, 0.0F);
            GL11.glEnable(2896);
        }
    }

    @Override
    public int getBrightnessForRender(float f) {
        return 61680;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.intValue++;
        if (this.intValue == this.intValue2) {
            this.setDead();
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
