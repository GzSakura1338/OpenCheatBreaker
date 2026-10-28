package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelZombie extends ModelBiped {
    public ModelZombie() {
        this(0.0F, false);
    }

    public ModelZombie(float f, float f2, int n, int n2) {
        super(f, f2, n, n2);
    }

    public ModelZombie(float f, boolean flag) {
        super(f, 0.0F, 64, flag ? 32 : 64);
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        super.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        float sin = MathHelper.sin(this.swingProgress * (float) Math.PI);
        float sin2 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        this.bipedRightArm.rotateAngleY = -(0.1F - sin * 0.6F);
        this.bipedLeftArm.rotateAngleY = 0.1F - sin * 0.6F;
        this.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2);
        this.bipedLeftArm.rotateAngleX = (float) (-Math.PI / 2);
        this.bipedRightArm.rotateAngleX -= sin * 1.2F - sin2 * 0.4F;
        this.bipedLeftArm.rotateAngleX -= sin * 1.2F - sin2 * 0.4F;
        this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + (MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F);
        this.bipedLeftArm.rotateAngleZ = this.bipedLeftArm.rotateAngleZ - (MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F);
        this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX + MathHelper.sin(f3 * 0.067F) * 0.05F;
        this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX - MathHelper.sin(f3 * 0.067F) * 0.05F;
    }
}
