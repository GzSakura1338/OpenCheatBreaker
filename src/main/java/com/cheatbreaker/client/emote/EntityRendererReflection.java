package com.cheatbreaker.client.emote;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class EntityRendererReflection {
    public static ModelBase findModelBase(RendererLivingEntity rendererLivingEntity) {
        return rendererLivingEntity.mainModel;
    }

    public static ModelBase shouldRenderPass(Render render, Entity entity) {
        ((RendererLivingEntity)render).shouldRenderPass((EntityLivingBase)entity, 0, 0.0F);
        return ((RendererLivingEntity)render).renderPassModel;
    }

    public static void preRenderCallback(EntityLivingBase entityLivingBase, RendererLivingEntity rendererLivingEntity) {
        rendererLivingEntity.preRenderCallback(entityLivingBase, 1.0F);
    }

    public static ResourceLocation getEntityTexture(Render render, Entity entity) {
        return render.getEntityTexture(entity);
    }
}
