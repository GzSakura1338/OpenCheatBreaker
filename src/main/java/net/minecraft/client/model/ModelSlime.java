package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelSlime extends ModelBase {
    public ModelRenderer slimeBodies;
    public ModelRenderer slimeRightEye;
    public ModelRenderer slimeLeftEye;
    public ModelRenderer slimeMouth;

    public ModelSlime(int n) {
        this.slimeBodies = new ModelRenderer(this, 0, n);
        this.slimeBodies.addBox(-4.0F, 16.0F, -4.0F, 8, 8, 8);
        if (n > 0) {
            this.slimeBodies = new ModelRenderer(this, 0, n);
            this.slimeBodies.addBox(-3.0F, 17.0F, -3.0F, 6, 6, 6);
            this.slimeRightEye = new ModelRenderer(this, 32, 0);
            this.slimeRightEye.addBox(-3.25F, 18.0F, -3.5F, 2, 2, 2);
            this.slimeLeftEye = new ModelRenderer(this, 32, 4);
            this.slimeLeftEye.addBox(1.25F, 18.0F, -3.5F, 2, 2, 2);
            this.slimeMouth = new ModelRenderer(this, 32, 8);
            this.slimeMouth.addBox(0.0F, 21.0F, -3.5F, 1, 1, 1);
        }
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        this.slimeBodies.render(f6);
        if (this.slimeRightEye != null) {
            this.slimeRightEye.render(f6);
            this.slimeLeftEye.render(f6);
            this.slimeMouth.render(f6);
        }
    }
}
