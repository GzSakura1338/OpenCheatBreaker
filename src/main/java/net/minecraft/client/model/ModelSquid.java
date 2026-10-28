package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelSquid extends ModelBase {
    public ModelRenderer[] squidTentacles = new ModelRenderer[8];
    public ModelRenderer squidBody;

    public ModelSquid() {
        byte b = -16;
        this.squidBody = new ModelRenderer(this, 0, 0);
        this.squidBody.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
        this.squidBody.rotationPointY += 24 + b;

        for (int i = 0; i < this.squidTentacles.length; i++) {
            this.squidTentacles[i] = new ModelRenderer(this, 48, 0);
            double d = i * Math.PI * 2.0 / this.squidTentacles.length;
            float f = (float)Math.cos(d) * 5.0F;
            float f2 = (float)Math.sin(d) * 5.0F;
            this.squidTentacles[i].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
            this.squidTentacles[i].rotationPointX = f;
            this.squidTentacles[i].rotationPointZ = f2;
            this.squidTentacles[i].rotationPointY = 31 + b;
            d = i * Math.PI * -2.0 / this.squidTentacles.length + (Math.PI / 2);
            this.squidTentacles[i].rotateAngleY = (float)d;
        }
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        for (ModelRenderer modelRenderer : this.squidTentacles) {
            modelRenderer.rotateAngleX = f3;
        }
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        this.squidBody.render(f6);

        for (int i = 0; i < this.squidTentacles.length; i++) {
            this.squidTentacles[i].render(f6);
        }
    }
}
