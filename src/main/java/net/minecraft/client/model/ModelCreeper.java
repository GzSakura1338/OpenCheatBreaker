package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelCreeper extends ModelBase {
    public ModelRenderer head;
    public ModelRenderer helmet;
    public ModelRenderer body;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;

    public ModelCreeper() {
        this(0.0F);
    }

    public ModelCreeper(float f) {
        byte b = 4;
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f);
        this.head.setRotationPoint(0.0F, b, 0.0F);
        this.helmet = new ModelRenderer(this, 32, 0);
        this.helmet.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f + 0.5F);
        this.helmet.setRotationPoint(0.0F, b, 0.0F);
        this.body = new ModelRenderer(this, 16, 16);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
        this.body.setRotationPoint(0.0F, b, 0.0F);
        this.leg1 = new ModelRenderer(this, 0, 16);
        this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
        this.leg1.setRotationPoint(-2.0F, 12 + b, 4.0F);
        this.leg2 = new ModelRenderer(this, 0, 16);
        this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
        this.leg2.setRotationPoint(2.0F, 12 + b, 4.0F);
        this.leg3 = new ModelRenderer(this, 0, 16);
        this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
        this.leg3.setRotationPoint(-2.0F, 12 + b, -4.0F);
        this.leg4 = new ModelRenderer(this, 0, 16);
        this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
        this.leg4.setRotationPoint(2.0F, 12 + b, -4.0F);
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        this.head.render(f6);
        this.body.render(f6);
        this.leg1.render(f6);
        this.leg2.render(f6);
        this.leg3.render(f6);
        this.leg4.render(f6);
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        this.head.rotateAngleY = f4 / (180.0F / (float)Math.PI);
        this.head.rotateAngleX = f5 / (180.0F / (float)Math.PI);
        this.leg1.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f2;
        this.leg2.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f2;
        this.leg3.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f2;
        this.leg4.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f2;
    }
}
