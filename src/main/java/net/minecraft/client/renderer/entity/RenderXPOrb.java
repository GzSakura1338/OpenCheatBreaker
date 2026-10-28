package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderXPOrb extends Render {
    public static ResourceLocation experienceOrbTextures = new ResourceLocation("textures/entity/experience_orb.png");

    public RenderXPOrb() {
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    public void doRender(EntityXPOrb entityXPOrb, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d2, (float)d3);
        this.bindEntityTexture(entityXPOrb);
        int textureByXP = entityXPOrb.getTextureByXP();
        float f3 = (textureByXP % 4 * 16 + 0) / 64.0F;
        float f4 = (textureByXP % 4 * 16 + 16) / 64.0F;
        float f5 = (textureByXP / 4 * 16 + 0) / 64.0F;
        float f6 = (textureByXP / 4 * 16 + 16) / 64.0F;
        float f7 = 1.0F;
        float f8 = 0.5F;
        float f9 = 0.25F;
        int brightnessForRender = entityXPOrb.getBrightnessForRender(f2);
        int n = brightnessForRender % 65536;
        int n2 = brightnessForRender / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f10 = 255.0F;
        float f11 = (entityXPOrb.xpColor + f2) / 2.0F;
        n2 = (int)((MathHelper.sin(f11 + 0.0F) + 1.0F) * 0.5F * f10);
        int n3 = (int)f10;
        int n4 = (int)((MathHelper.sin(f11 + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * f10);
        int n5 = n2 << 16 | n3 << 8 | n4;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        float f12 = 0.3F;
        GL11.glScalef(f12, f12, f12);
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.setColorRGBA_I(n5, 128);
        instance.setNormal(0.0F, 1.0F, 0.0F);
        instance.addVertexWithUV(0.0F - f8, 0.0F - f9, 0.0, f3, f6);
        instance.addVertexWithUV(f7 - f8, 0.0F - f9, 0.0, f4, f6);
        instance.addVertexWithUV(f7 - f8, 1.0F - f9, 0.0, f4, f5);
        instance.addVertexWithUV(0.0F - f8, 1.0F - f9, 0.0, f3, f5);
        instance.draw();
        GL11.glDisable(3042);
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }

    public ResourceLocation getEntityTexture(EntityXPOrb entityXPOrb) {
        return experienceOrbTextures;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityXPOrb)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityXPOrb)entity, d, d2, d3, f, f2);
    }
}
