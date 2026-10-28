package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSnowMan extends ModelBase {
    public ModelRenderer head;
    public ModelRenderer rightHand;
    public ModelRenderer leftHand;
    public ModelRenderer body;
    public ModelRenderer bottomBody;

    public ModelSnowMan() {
        float f = 4.0F;
        float f2 = 0.0F;
        this.head = new ModelRenderer(this, 0, 0).setTextureSize(64, 64);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f2 - 0.5F);
        this.head.setRotationPoint(0.0F, 0.0F + f, 0.0F);
        this.rightHand = new ModelRenderer(this, 32, 0).setTextureSize(64, 64);
        this.rightHand.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, f2 - 0.5F);
        this.rightHand.setRotationPoint(0.0F, 0.0F + f + 9.0F - 7.0F, 0.0F);
        this.leftHand = new ModelRenderer(this, 32, 0).setTextureSize(64, 64);
        this.leftHand.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, f2 - 0.5F);
        this.leftHand.setRotationPoint(0.0F, 0.0F + f + 9.0F - 7.0F, 0.0F);
        this.body = new ModelRenderer(this, 0, 16).setTextureSize(64, 64);
        this.body.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, f2 - 0.5F);
        this.body.setRotationPoint(0.0F, 0.0F + f + 9.0F, 0.0F);
        this.bottomBody = new ModelRenderer(this, 0, 36).setTextureSize(64, 64);
        this.bottomBody.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12, f2 - 0.5F);
        this.bottomBody.setRotationPoint(0.0F, 0.0F + f + 20.0F, 0.0F);
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        super.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        this.head.rotateAngleY = f4 / (180.0F / (float)Math.PI);
        this.head.rotateAngleX = f5 / (180.0F / (float)Math.PI);
        this.body.rotateAngleY = f4 / (180.0F / (float)Math.PI) * 0.25F;
        float sin = MathHelper.sin(this.body.rotateAngleY);
        float cos = MathHelper.cos(this.body.rotateAngleY);
        this.rightHand.rotateAngleZ = 1.0F;
        this.leftHand.rotateAngleZ = -1.0F;
        this.rightHand.rotateAngleY = 0.0F + this.body.rotateAngleY;
        this.leftHand.rotateAngleY = (float) Math.PI + this.body.rotateAngleY;
        this.rightHand.rotationPointX = cos * 5.0F;
        this.rightHand.rotationPointZ = -sin * 5.0F;
        this.leftHand.rotationPointX = -cos * 5.0F;
        this.leftHand.rotationPointZ = sin * 5.0F;
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        this.body.render(f6);
        this.bottomBody.render(f6);
        this.head.render(f6);
        this.rightHand.render(f6);
        this.leftHand.render(f6);
    }
}
