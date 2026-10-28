package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent extends CancellableEvent {
    public RendererLivingEntity rendererLivingEntity;
    public EntityLivingBase entityLivingBase;
    public double x;
    public double y;
    public double z;
    public float entityYaw;
    public float partialTicks;
    public int colorMultiplier;
    public float floatValue3;
    public float floatValue4;
    public float floatValue5;
    public float floatValue6;
    public float floatValue7;
    public float floatValue8;
    public float floatValue9;

    public float getEntityYaw() {
        return this.entityYaw;
    }

    public RendererLivingEntity getRendererLivingEntity() {
        return this.rendererLivingEntity;
    }

    public EntityLivingBase getEntityLivingBase() {
        return this.entityLivingBase;
    }

    public float getFloatValue7() {
        return this.floatValue7;
    }

    public double getZ() {
        return this.z;
    }

    public float getFloatValue9() {
        return this.floatValue9;
    }

    public float getFloatValue4() {
        return this.floatValue4;
    }

    public double getX() {
        return this.x;
    }

    public int getColorMultiplier() {
        return this.colorMultiplier;
    }

    public float getFloatValue3() {
        return this.floatValue3;
    }

    public float getFloatValue8() {
        return this.floatValue8;
    }

    public float getFloatValue5() {
        return this.floatValue5;
    }

    public double getY() {
        return this.y;
    }

    public EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent(
        RendererLivingEntity rendererLivingEntity,
        EntityLivingBase entityLivingBase,
        double d,
        double d2,
        double d3,
        float f,
        float f2,
        int n,
        float f3,
        float f4,
        float f5,
        float f6,
        float f7,
        float f8,
        float f9
    ) {
        this.rendererLivingEntity = rendererLivingEntity;
        this.entityLivingBase = entityLivingBase;
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.entityYaw = f;
        this.partialTicks = f2;
        this.colorMultiplier = n;
        this.floatValue3 = f3;
        this.floatValue4 = f4;
        this.floatValue5 = f5;
        this.floatValue6 = f6;
        this.floatValue7 = f7;
        this.floatValue8 = f8;
        this.floatValue9 = f9;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public float getFloatValue6() {
        return this.floatValue6;
    }
}
