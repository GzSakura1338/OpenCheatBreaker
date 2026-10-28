package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelWitch extends ModelVillager {
    public boolean flag;
    public ModelRenderer modelRenderer = new ModelRenderer(this).setTextureSize(64, 128);
    public ModelRenderer witchHat;

    public ModelWitch(float f) {
        super(f, 0.0F, 64, 128);
        this.modelRenderer.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.modelRenderer.setTextureOffset(0, 0).addBox(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
        this.villagerNose.addChild(this.modelRenderer);
        this.witchHat = new ModelRenderer(this).setTextureSize(64, 128);
        this.witchHat.setRotationPoint(-5.0F, -10.03125F, -5.0F);
        this.witchHat.setTextureOffset(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
        this.villagerHead.addChild(this.witchHat);
        ModelRenderer setTextureSize = new ModelRenderer(this).setTextureSize(64, 128);
        setTextureSize.setRotationPoint(1.75F, -4.0F, 2.0F);
        setTextureSize.setTextureOffset(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
        setTextureSize.rotateAngleX = -0.05235988F;
        setTextureSize.rotateAngleZ = 0.02617994F;
        this.witchHat.addChild(setTextureSize);
        ModelRenderer setTextureSize2 = new ModelRenderer(this).setTextureSize(64, 128);
        setTextureSize2.setRotationPoint(1.75F, -4.0F, 2.0F);
        setTextureSize2.setTextureOffset(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
        setTextureSize2.rotateAngleX = -0.10471976F;
        setTextureSize2.rotateAngleZ = 0.05235988F;
        setTextureSize.addChild(setTextureSize2);
        ModelRenderer setTextureSize3 = new ModelRenderer(this).setTextureSize(64, 128);
        setTextureSize3.setRotationPoint(1.75F, -2.0F, 2.0F);
        setTextureSize3.setTextureOffset(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
        setTextureSize3.rotateAngleX = (float) (-Math.PI / 15);
        setTextureSize3.rotateAngleZ = 0.10471976F;
        setTextureSize2.addChild(setTextureSize3);
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        super.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        this.villagerNose.offsetX = this.villagerNose.offsetY = this.villagerNose.offsetZ = 0.0F;
        float f7 = 0.01F * (entity.getEntityId() % 10);
        this.villagerNose.rotateAngleX = MathHelper.sin(entity.ticksExisted * f7) * 4.5F * (float) Math.PI / 180.0F;
        this.villagerNose.rotateAngleY = 0.0F;
        this.villagerNose.rotateAngleZ = MathHelper.cos(entity.ticksExisted * f7) * 2.5F * (float) Math.PI / 180.0F;
        if (this.flag) {
            this.villagerNose.rotateAngleX = -0.9F;
            this.villagerNose.offsetZ = -0.09375F;
            this.villagerNose.offsetY = 0.1875F;
        }
    }
}
