package net.minecraft.client.renderer.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderCaveSpider extends RenderSpider {
    public static ResourceLocation caveSpiderTextures = new ResourceLocation("textures/entity/spider/cave_spider.png");

    public RenderCaveSpider() {
        this.shadowSize *= 0.7F;
    }

    public void preRenderCallback(EntityCaveSpider entityCaveSpider, float f) {
        GL11.glScalef(0.7F, 0.7F, 0.7F);
    }

    public ResourceLocation getEntityTexture(EntityCaveSpider entityCaveSpider) {
        return caveSpiderTextures;
    }

    @Override
    public ResourceLocation getEntityTexture(EntitySpider entitySpider) {
        return this.getEntityTexture((EntityCaveSpider)entitySpider);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityCaveSpider)entityLivingBase, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityCaveSpider)entity);
    }
}
