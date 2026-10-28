package com.cheatbreaker.client.emote;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class EmoteEntityRenderer extends RendererLivingEntity {
    public static EmoteEntityRenderer emoteEntityRenderer;

    @Override
    public int getColorMultiplier(EntityLivingBase entityLivingBase, float f, float f2) {
        return 0;
    }

    public static void handleEntityLivingBase(EntityLivingBase entityLivingBase, String str, double d, double d2, double d3) {
        emoteEntityRenderer.renderLivingLabel(entityLivingBase, str, d, d2, d3, 64);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.nullValue((EntityLivingBase)entity);
    }

    public ResourceLocation nullValue(EntityLivingBase entityLivingBase) {
        return null;
    }

    public static boolean checkEntityLivingBase(EntityLivingBase entityLivingBase, float f) {
        int brightnessForRender = entityLivingBase.getBrightnessForRender(f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessForRender % 65536, brightnessForRender / 65536);
        return true;
    }

    public static void setLightmapTextureCoords() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY);
    }

    public static void init() {
        emoteEntityRenderer = new EmoteEntityRenderer(null, 0.0F);
        emoteEntityRenderer.setRenderManager(RenderManager.instance);
    }

    public static boolean b_(EntityLivingBase entityLivingBase) {
        return emoteEntityRenderer.canRenderName(entityLivingBase);
    }

    public EmoteEntityRenderer(ModelBase modelBase, float f) {
        super(modelBase, f);
    }
}
