package com.cheatbreaker.client.emote;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderReflectionHelper {
    public static boolean flag = true;
    public static boolean flag2 = true;
    public static boolean flag3 = true;
    public static boolean flag4 = true;
    public static boolean flag5 = true;

    public static void preRenderCallback(EntityLivingBase entityLivingBase, RendererLivingEntity rendererLivingEntity) {
        if (flag4) {
            flag4 = false;
            EntityRendererReflection.preRenderCallback(entityLivingBase, rendererLivingEntity);
        } else {
            EntityRendererReflection.preRenderCallback(entityLivingBase, rendererLivingEntity);
        }
    }

    public static ModelBase getRenderPassModel(Render render, Entity entity) {
        if (flag3) {
            flag3 = false;
            return EntityRendererReflection.shouldRenderPass(render, entity);
        } else {
            return EntityRendererReflection.shouldRenderPass(render, entity);
        }
    }

    public static ModelBase findModelBase(RendererLivingEntity rendererLivingEntity) {
        if (flag2) {
            flag2 = false;
            return EntityRendererReflection.findModelBase(rendererLivingEntity);
        } else {
            return EntityRendererReflection.findModelBase(rendererLivingEntity);
        }
    }

    public static void setLightOpacity(Block block, int n) {
        if (flag5) {
            try {
                block.setLightOpacity(n);
            } catch (IllegalAccessError e) {
                flag5 = false;
                BlockReflection.setLightOpacity(block, n);
            }
        } else {
            BlockReflection.setLightOpacity(block, n);
        }
    }

    public static ResourceLocation getEntityTexture(Render render, Entity entity) {
        if (flag4) {
            flag4 = false;
            return EntityRendererReflection.getEntityTexture(render, entity);
        } else {
            return EntityRendererReflection.getEntityTexture(render, entity);
        }
    }
}
