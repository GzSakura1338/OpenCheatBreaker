package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderPainting extends Render {
    public static ResourceLocation resourceLocation = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

    public void doRender(EntityPainting entityPainting, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        GL11.glTranslated(d, d2, d3);
        GL11.glRotatef(f, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(32826);
        this.bindEntityTexture(entityPainting);
        EnumArt art = entityPainting.art;
        float f3 = 0.0625F;
        GL11.glScalef(f3, f3, f3);
        this.handleEntityPainting(entityPainting, art.sizeX, art.sizeY, art.offsetX, art.offsetY);
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }

    public ResourceLocation getEntityTexture(EntityPainting entityPainting) {
        return resourceLocation;
    }

    public void handleEntityPainting(EntityPainting entityPainting, int n, int n2, int n3, int n4) {
        float f = -n / 2.0F;
        float f2 = -n2 / 2.0F;
        float f3 = 0.5F;
        float f4 = 0.75F;
        float f5 = 0.8125F;
        float f6 = 0.0F;
        float f7 = 0.0625F;
        float f8 = 0.75F;
        float f9 = 0.8125F;
        float f10 = 0.001953125F;
        float f11 = 0.001953125F;
        float f12 = 0.7519531F;
        float f13 = 0.7519531F;
        float f14 = 0.0F;
        float f15 = 0.0625F;

        for (int i = 0; i < n / 16; i++) {
            for (int j = 0; j < n2 / 16; j++) {
                float f16 = f + (i + 1) * 16;
                float f17 = f + i * 16;
                float f18 = f2 + (j + 1) * 16;
                float f19 = f2 + j * 16;
                this.handleEntityPainting(entityPainting, (f16 + f17) / 2.0F, (f18 + f19) / 2.0F);
                float f20 = (n3 + n - i * 16) / 256.0F;
                float f21 = (n3 + n - (i + 1) * 16) / 256.0F;
                float f22 = (n4 + n2 - j * 16) / 256.0F;
                float f23 = (n4 + n2 - (j + 1) * 16) / 256.0F;
                Tessellator instance = Tessellator.instance;
                instance.startDrawingQuads();
                instance.setNormal(0.0F, 0.0F, -1.0F);
                instance.addVertexWithUV(f16, f19, -f3, f21, f22);
                instance.addVertexWithUV(f17, f19, -f3, f20, f22);
                instance.addVertexWithUV(f17, f18, -f3, f20, f23);
                instance.addVertexWithUV(f16, f18, -f3, f21, f23);
                instance.setNormal(0.0F, 0.0F, 1.0F);
                instance.addVertexWithUV(f16, f18, f3, f4, f6);
                instance.addVertexWithUV(f17, f18, f3, f5, f6);
                instance.addVertexWithUV(f17, f19, f3, f5, f7);
                instance.addVertexWithUV(f16, f19, f3, f4, f7);
                instance.setNormal(0.0F, 1.0F, 0.0F);
                instance.addVertexWithUV(f16, f18, -f3, f8, f10);
                instance.addVertexWithUV(f17, f18, -f3, f9, f10);
                instance.addVertexWithUV(f17, f18, f3, f9, f11);
                instance.addVertexWithUV(f16, f18, f3, f8, f11);
                instance.setNormal(0.0F, -1.0F, 0.0F);
                instance.addVertexWithUV(f16, f19, f3, f8, f10);
                instance.addVertexWithUV(f17, f19, f3, f9, f10);
                instance.addVertexWithUV(f17, f19, -f3, f9, f11);
                instance.addVertexWithUV(f16, f19, -f3, f8, f11);
                instance.setNormal(-1.0F, 0.0F, 0.0F);
                instance.addVertexWithUV(f16, f18, f3, f13, f14);
                instance.addVertexWithUV(f16, f19, f3, f13, f15);
                instance.addVertexWithUV(f16, f19, -f3, f12, f15);
                instance.addVertexWithUV(f16, f18, -f3, f12, f14);
                instance.setNormal(1.0F, 0.0F, 0.0F);
                instance.addVertexWithUV(f17, f18, -f3, f13, f14);
                instance.addVertexWithUV(f17, f19, -f3, f13, f15);
                instance.addVertexWithUV(f17, f19, f3, f12, f15);
                instance.addVertexWithUV(f17, f18, f3, f12, f14);
                instance.draw();
            }
        }
    }

    public void handleEntityPainting(EntityPainting entityPainting, float f, float f2) {
        int n = MathHelper.floor_double(entityPainting.posX);
        int n2 = MathHelper.floor_double(entityPainting.posY + f2 / 16.0F);
        int n3 = MathHelper.floor_double(entityPainting.posZ);
        if (entityPainting.hangingDirection == 2) {
            n = MathHelper.floor_double(entityPainting.posX + f / 16.0F);
        }

        if (entityPainting.hangingDirection == 1) {
            n3 = MathHelper.floor_double(entityPainting.posZ - f / 16.0F);
        }

        if (entityPainting.hangingDirection == 0) {
            n = MathHelper.floor_double(entityPainting.posX - f / 16.0F);
        }

        if (entityPainting.hangingDirection == 3) {
            n3 = MathHelper.floor_double(entityPainting.posZ + f / 16.0F);
        }

        int lightBrightnessForSkyBlocks = this.renderManager.worldObj.getLightBrightnessForSkyBlocks(n, n2, n3, 0);
        int n4 = lightBrightnessForSkyBlocks % 65536;
        int n5 = lightBrightnessForSkyBlocks / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n4, n5);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityPainting)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityPainting)entity, d, d2, d3, f, f2);
    }
}
