package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBlaze;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.ResourceLocation;

public class RenderBlaze extends RenderLiving {
    public static ResourceLocation blazeTextures = new ResourceLocation("textures/entity/blaze.png");
    public int intValue = ((ModelBlaze)this.mainModel).constantInt();

    public RenderBlaze() {
        super(new ModelBlaze(), 0.5F);
    }

    public void doRender(EntityBlaze entityBlaze, double d, double d2, double d3, float f, float f2) {
        int constantInt = ((ModelBlaze)this.mainModel).constantInt();
        if (constantInt != this.intValue) {
            this.intValue = constantInt;
            this.mainModel = new ModelBlaze();
        }

        super.doRender(entityBlaze, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityBlaze entityBlaze) {
        return blazeTextures;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityBlaze)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityBlaze)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityBlaze)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityBlaze)entity, d, d2, d3, f, f2);
    }
}
