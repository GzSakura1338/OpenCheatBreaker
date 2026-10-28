package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class RenderSpider extends RenderLiving {
    public static ResourceLocation spiderEyesTextures = new ResourceLocation("textures/entity/spider_eyes.png");
    public static ResourceLocation spiderTextures = new ResourceLocation("textures/entity/spider/spider.png");

    public RenderSpider() {
        super(new ModelSpider(), 1.0F);
        this.setRenderPassModel(new ModelSpider());
    }

    public float getDeathMaxRotation(EntitySpider entitySpider) {
        return 180.0F;
    }

    public int shouldRenderPass(EntitySpider entitySpider, int n, float f) {
        if (n != 0) {
            return -1;
        }

        this.bindTexture(spiderEyesTextures);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(1, 1);
        GL11.glDepthMask(!entitySpider.isInvisible());
        char c = '\uf0f0';
        int n2 = c % 65536;
        int n3 = c / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n2 / 1.0F, n3 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (Config.isShaders()) {
            Shaders.beginSpiderEyes();
        }

        return 1;
    }

    public ResourceLocation getEntityTexture(EntitySpider entitySpider) {
        return spiderTextures;
    }

    @Override
    public float getDeathMaxRotation(EntityLivingBase entityLivingBase) {
        return this.getDeathMaxRotation((EntitySpider)entityLivingBase);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntitySpider)entityLivingBase, n, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntitySpider)entity);
    }
}
