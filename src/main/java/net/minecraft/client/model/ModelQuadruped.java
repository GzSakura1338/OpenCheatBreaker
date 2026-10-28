package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelQuadruped extends ModelBase {
    public ModelRenderer head = new ModelRenderer(this, 0, 0);
    public float floatValue = 8.0F;
    public float floatValue2 = 4.0F;
    public ModelRenderer body;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;

    public ModelQuadruped(int n, float f) {
        this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, f);
        this.head.setRotationPoint(0.0F, 18 - n, -6.0F);
        this.body = new ModelRenderer(this, 28, 8);
        this.body.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, f);
        this.body.setRotationPoint(0.0F, 17 - n, 2.0F);
        this.leg1 = new ModelRenderer(this, 0, 16);
        this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, n, 4, f);
        this.leg1.setRotationPoint(-3.0F, 24 - n, 7.0F);
        this.leg2 = new ModelRenderer(this, 0, 16);
        this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, n, 4, f);
        this.leg2.setRotationPoint(3.0F, 24 - n, 7.0F);
        this.leg3 = new ModelRenderer(this, 0, 16);
        this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, n, 4, f);
        this.leg3.setRotationPoint(-3.0F, 24 - n, -5.0F);
        this.leg4 = new ModelRenderer(this, 0, 16);
        this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, n, 4, f);
        this.leg4.setRotationPoint(3.0F, 24 - n, -5.0F);
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        if (this.isChild) {
            float f7 = 2.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, this.floatValue * f6, this.floatValue2 * f6);
            this.head.render(f6);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f7, 1.0F / f7, 1.0F / f7);
            GL11.glTranslatef(0.0F, 24.0F * f6, 0.0F);
            this.body.render(f6);
            this.leg1.render(f6);
            this.leg2.render(f6);
            this.leg3.render(f6);
            this.leg4.render(f6);
            GL11.glPopMatrix();
        } else {
            this.head.render(f6);
            this.body.render(f6);
            this.leg1.render(f6);
            this.leg2.render(f6);
            this.leg3.render(f6);
            this.leg4.render(f6);
        }
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        float f7 = 180.0F / (float)Math.PI;
        this.head.rotateAngleX = f5 / (180.0F / (float)Math.PI);
        this.head.rotateAngleY = f4 / (180.0F / (float)Math.PI);
        this.body.rotateAngleX = (float) (Math.PI / 2);
        this.leg1.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f2;
        this.leg2.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f2;
        this.leg3.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f2;
        this.leg4.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f2;
    }
}
