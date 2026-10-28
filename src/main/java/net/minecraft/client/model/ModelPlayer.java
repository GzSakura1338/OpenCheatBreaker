package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelPlayer extends ModelBiped {
    public boolean flag3;
    public ModelRenderer modelRenderer;
    public ModelRenderer modelRenderer2;
    public ModelRenderer modelRenderer3;
    public ModelRenderer modelRenderer4;
    public ModelRenderer modelRenderer5;

    public ModelPlayer(float f, boolean flag) {
        super(f, 0.0F, 64, 64);
        this.flag3 = flag;
        if (flag) {
            this.bipedLeftArm = new ModelRenderer(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
            this.bipedRightArm = new ModelRenderer(this, 40, 16);
            this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
            this.modelRenderer = new ModelRenderer(this, 48, 48);
            this.modelRenderer.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
            this.modelRenderer.setRotationPoint(5.0F, 2.5F, 0.0F);
            this.modelRenderer2 = new ModelRenderer(this, 40, 32);
            this.modelRenderer2.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
            this.modelRenderer2.setRotationPoint(-5.0F, 2.5F, 0.0F);
        } else {
            this.bipedLeftArm = new ModelRenderer(this, 32, 48);
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.modelRenderer = new ModelRenderer(this, 48, 48);
            this.modelRenderer.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
            this.modelRenderer.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.modelRenderer2 = new ModelRenderer(this, 40, 32);
            this.modelRenderer2.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
            this.modelRenderer2.setRotationPoint(-5.0F, 2.0F, 0.0F);
        }

        this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.modelRenderer3 = new ModelRenderer(this, 0, 48);
        this.modelRenderer3.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
        this.modelRenderer3.setRotationPoint(1.9F, 12.0F, 0.0F);
        this.modelRenderer4 = new ModelRenderer(this, 0, 32);
        this.modelRenderer4.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
        this.modelRenderer4.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.modelRenderer5 = new ModelRenderer(this, 16, 32);
        this.modelRenderer5.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.25F);
        this.modelRenderer5.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    public ModelPlayer(float f) {
        this(f, false);
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        super.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        ModelBase.handleModelRenderer(this.bipedLeftLeg, this.modelRenderer3);
        ModelBase.handleModelRenderer(this.bipedRightLeg, this.modelRenderer4);
        ModelBase.handleModelRenderer(this.bipedLeftArm, this.modelRenderer);
        ModelBase.handleModelRenderer(this.bipedRightArm, this.modelRenderer2);
        ModelBase.handleModelRenderer(this.bipedBody, this.modelRenderer5);
    }

    public void process(float f) {
        if (this.flag3) {
            this.bipedRightArm.rotationPointX++;
            this.bipedRightArm.postRender(f);
            this.bipedRightArm.rotationPointX--;
        } else {
            this.bipedRightArm.postRender(f);
        }
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        super.render(entity, f, f2, f3, f4, f5, f6);
        GL11.glPushMatrix();
        if (this.isChild) {
            float f7 = 2.0F;
            GL11.glScalef(1.0F / f7, 1.0F / f7, 1.0F / f7);
            GL11.glTranslatef(0.0F, 24.0F * f6, 0.0F);
        } else if (entity.isSneaking()) {
            GL11.glTranslatef(0.0F, 0.2F, 0.0F);
        }

        this.modelRenderer3.render(f6);
        this.modelRenderer4.render(f6);
        this.modelRenderer.render(f6);
        this.modelRenderer2.render(f6);
        this.modelRenderer5.render(f6);
        GL11.glPopMatrix();
        this.bipedRightLeg.rotateAngleZ = 0.0F;
        this.bipedLeftLeg.rotateAngleZ = 0.0F;
        this.bipedLeftLeg.offsetX = 0.0F;
        this.bipedRightLeg.offsetX = 0.0F;
        this.bipedBody.rotateAngleZ = 0.0F;
        this.modelRenderer2.rotateAngleX = 0.0F;
        this.modelRenderer2.rotateAngleY = 0.0F;
        this.modelRenderer2.rotateAngleZ = 0.0F;
        this.modelRenderer2.offsetX = 0.0F;
        this.modelRenderer.rotateAngleX = 0.0F;
        this.modelRenderer.rotateAngleY = 0.0F;
        this.modelRenderer.rotateAngleZ = 0.0F;
        this.modelRenderer.offsetX = 0.0F;
        this.modelRenderer4.rotateAngleX = 0.0F;
        this.modelRenderer4.rotateAngleY = 0.0F;
        this.modelRenderer4.rotateAngleZ = 0.0F;
        this.modelRenderer4.offsetX = 0.0F;
        this.modelRenderer3.rotateAngleX = 0.0F;
        this.modelRenderer3.rotateAngleY = 0.0F;
        this.modelRenderer3.rotateAngleZ = 0.0F;
        this.modelRenderer3.offsetX = 0.0F;
    }
}
