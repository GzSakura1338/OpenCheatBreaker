package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelEnderman extends ModelBiped {
    public boolean isCarrying;
    public boolean isAttacking;

    public ModelEnderman() {
        super(0.0F, -14.0F, 64, 32);
        float f = -14.0F;
        float f2 = 0.0F;
        this.bipedHeadwear = new ModelRenderer(this, 0, 16);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f2 - 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + f, 0.0F);
        this.bipedBody = new ModelRenderer(this, 32, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f2);
        this.bipedBody.setRotationPoint(0.0F, 0.0F + f, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 56, 0);
        this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, f2);
        this.bipedRightArm.setRotationPoint(-3.0F, 2.0F + f, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 56, 0);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, f2);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + f, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 56, 0);
        this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, f2);
        this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F + f, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 56, 0);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, f2);
        this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F + f, 0.0F);
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        super.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        this.bipedHead.showModel = true;
        float f7 = -14.0F;
        this.bipedBody.rotateAngleX = 0.0F;
        this.bipedBody.rotationPointY = f7;
        this.bipedBody.rotationPointZ = -0.0F;
        this.bipedRightLeg.rotateAngleX -= 0.0F;
        this.bipedLeftLeg.rotateAngleX -= 0.0F;
        this.bipedRightArm.rotateAngleX = (float)(this.bipedRightArm.rotateAngleX * 0.5);
        this.bipedLeftArm.rotateAngleX = (float)(this.bipedLeftArm.rotateAngleX * 0.5);
        this.bipedRightLeg.rotateAngleX = (float)(this.bipedRightLeg.rotateAngleX * 0.5);
        this.bipedLeftLeg.rotateAngleX = (float)(this.bipedLeftLeg.rotateAngleX * 0.5);
        float f8 = 0.4F;
        if (this.bipedRightArm.rotateAngleX > f8) {
            this.bipedRightArm.rotateAngleX = f8;
        }

        if (this.bipedLeftArm.rotateAngleX > f8) {
            this.bipedLeftArm.rotateAngleX = f8;
        }

        if (this.bipedRightArm.rotateAngleX < -f8) {
            this.bipedRightArm.rotateAngleX = -f8;
        }

        if (this.bipedLeftArm.rotateAngleX < -f8) {
            this.bipedLeftArm.rotateAngleX = -f8;
        }

        if (this.bipedRightLeg.rotateAngleX > f8) {
            this.bipedRightLeg.rotateAngleX = f8;
        }

        if (this.bipedLeftLeg.rotateAngleX > f8) {
            this.bipedLeftLeg.rotateAngleX = f8;
        }

        if (this.bipedRightLeg.rotateAngleX < -f8) {
            this.bipedRightLeg.rotateAngleX = -f8;
        }

        if (this.bipedLeftLeg.rotateAngleX < -f8) {
            this.bipedLeftLeg.rotateAngleX = -f8;
        }

        if (this.isCarrying) {
            this.bipedRightArm.rotateAngleX = -0.5F;
            this.bipedLeftArm.rotateAngleX = -0.5F;
            this.bipedRightArm.rotateAngleZ = 0.05F;
            this.bipedLeftArm.rotateAngleZ = -0.05F;
        }

        this.bipedRightArm.rotationPointZ = 0.0F;
        this.bipedLeftArm.rotationPointZ = 0.0F;
        this.bipedRightLeg.rotationPointZ = 0.0F;
        this.bipedLeftLeg.rotationPointZ = 0.0F;
        this.bipedRightLeg.rotationPointY = 9.0F + f7;
        this.bipedLeftLeg.rotationPointY = 9.0F + f7;
        this.bipedHead.rotationPointZ = -0.0F;
        this.bipedHead.rotationPointY = f7 + 1.0F;
        this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX;
        this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY;
        this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ;
        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ;
        if (this.isAttacking) {
            float f9 = 1.0F;
            this.bipedHead.rotationPointY -= f9 * 5.0F;
        }
    }
}
