package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderHorse extends RenderLiving {
    public static Map MODEL_IDENTIFIERS = Maps.newHashMap();
    public static ResourceLocation whiteHorseTextures = new ResourceLocation("textures/entity/horse/horse_white.png");
    public static ResourceLocation mulePng = new ResourceLocation("textures/entity/horse/mule.png");
    public static ResourceLocation donkeyPng = new ResourceLocation("textures/entity/horse/donkey.png");
    public static ResourceLocation zombieHorseTextures = new ResourceLocation("textures/entity/horse/horse_zombie.png");
    public static ResourceLocation skeletonHorseTextures = new ResourceLocation("textures/entity/horse/horse_skeleton.png");

    public RenderHorse(ModelBase modelBase, float f) {
        super(modelBase, f);
    }

    public void preRenderCallback(EntityHorse entityHorse, float f) {
        float f2 = 1.0F;
        int horseType = entityHorse.getHorseType();
        if (horseType == 1) {
            f2 *= 0.87F;
        } else if (horseType == 2) {
            f2 *= 0.92F;
        }

        GL11.glScalef(f2, f2, f2);
        super.preRenderCallback(entityHorse, f);
    }

    public void renderModel(EntityHorse entityHorse, float f, float f2, float f3, float f4, float f5, float f6) {
        if (entityHorse.isInvisible()) {
            this.mainModel.setRotationAngles(f, f2, f3, f4, f5, f6, entityHorse);
        } else {
            this.bindEntityTexture(entityHorse);
            this.mainModel.render(entityHorse, f, f2, f3, f4, f5, f6);
        }
    }

    public ResourceLocation getEntityTexture(EntityHorse entityHorse) {
        if (!entityHorse.check()) {
            switch (entityHorse.getHorseType()) {
                case 0:
                default:
                    return whiteHorseTextures;
                case 1:
                    return donkeyPng;
                case 2:
                    return mulePng;
                case 3:
                    return zombieHorseTextures;
                case 4:
                    return skeletonHorseTextures;
            }
        } else {
            return this.findResourceLocation(entityHorse);
        }
    }

    public ResourceLocation findResourceLocation(EntityHorse entityHorse) {
        String horseTexture = entityHorse.getHorseTexture();
        ResourceLocation resourceLocation = (ResourceLocation)MODEL_IDENTIFIERS.get(horseTexture);
        if (resourceLocation == null) {
            resourceLocation = new ResourceLocation(horseTexture);
            Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, new LayeredTexture(entityHorse.getVariantTexturePaths()));
            MODEL_IDENTIFIERS.put(horseTexture, resourceLocation);
        }

        return resourceLocation;
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityHorse)entityLivingBase, f);
    }

    @Override
    public void renderModel(EntityLivingBase entityLivingBase, float f, float f2, float f3, float f4, float f5, float f6) {
        this.renderModel((EntityHorse)entityLivingBase, f, f2, f3, f4, f5, f6);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityHorse)entity);
    }
}
