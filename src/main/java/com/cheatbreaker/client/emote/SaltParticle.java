package com.cheatbreaker.client.emote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class SaltParticle extends EntityFX {
    public static ModelRenderer modelRenderer;
    public static ResourceLocation minecraft = new ResourceLocation("minecraft", "emoticons/textures/particles.png");

    @Override
    public int getFXLayer() {
        return 3;
    }

    public SaltParticle(World world, double d, double d2, double d3, double d4) {
        super(world, d, d2, d3);
        this.particleGravity = 0.5F;
        this.particleScale = 0.5F;
        this.particleMaxAge = 20 + this.rand.nextInt(10);
        this.motionX = this.rand.nextFloat() * 0.05F;
        this.motionZ = this.rand.nextFloat() * 0.05F;
        this.motionY = d4;
        if (modelRenderer == null) {
            EmptyParticleModel emptyParticleModel = new EmptyParticleModel();
            emptyParticleModel.textureWidth = 64;
            emptyParticleModel.textureHeight = 64;
            modelRenderer = new ModelRenderer(emptyParticleModel, 0, 0);
            modelRenderer.addBox(-0.25F, -0.25F, 0.25F, 1, 1, 1);
        }
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - interpPosX);
        float f8 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - interpPosY);
        float f9 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - interpPosZ);
        int n = this.particleMaxAge - this.particleAge;
        float f10 = 0.5F * (n < 5 ? n / 5.0F : 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(minecraft);
        int brightnessForRender = this.getBrightnessForRender(f);
        if (this.isBurning()) {
            brightnessForRender = 15728880;
        }

        int n2 = brightnessForRender % 65536;
        int n3 = brightnessForRender / 65536;
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n2, n3);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef(f7, f8, f9);
        GL11.glScalef(f10, f10, f10);
        RenderHelper.enableStandardItemLighting();
        modelRenderer.render(0.0625F);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
    }
}
