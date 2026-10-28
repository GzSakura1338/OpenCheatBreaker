package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;

public class ModelIronGolem extends ModelBase {
    public ModelRenderer ironGolemHead;
    public ModelRenderer ironGolemBody;
    public ModelRenderer ironGolemRightArm;
    public ModelRenderer ironGolemLeftArm;
    public ModelRenderer ironGolemLeftLeg;
    public ModelRenderer ironGolemRightLeg;

    public ModelIronGolem() {
        this(0.0F);
    }

    public ModelIronGolem(float f) {
        this(f, -7.0F);
    }

    public ModelIronGolem(float f, float f2) {
        short s = 128;
        short s2 = 128;
        this.ironGolemHead = new ModelRenderer(this).setTextureSize(s, s2);
        this.ironGolemHead.setRotationPoint(0.0F, 0.0F + f2, -2.0F);
        this.ironGolemHead.setTextureOffset(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8, 10, 8, f);
        this.ironGolemHead.setTextureOffset(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2, 4, 2, f);
        this.ironGolemBody = new ModelRenderer(this).setTextureSize(s, s2);
        this.ironGolemBody.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
        this.ironGolemBody.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18, 12, 11, f);
        this.ironGolemBody.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6, f + 0.5F);
        this.ironGolemRightArm = new ModelRenderer(this).setTextureSize(s, s2);
        this.ironGolemRightArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.ironGolemRightArm.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4, 30, 6, f);
        this.ironGolemLeftArm = new ModelRenderer(this).setTextureSize(s, s2);
        this.ironGolemLeftArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.ironGolemLeftArm.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4, 30, 6, f);
        this.ironGolemLeftLeg = new ModelRenderer(this, 0, 22).setTextureSize(s, s2);
        this.ironGolemLeftLeg.setRotationPoint(-4.0F, 18.0F + f2, 0.0F);
        this.ironGolemLeftLeg.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
        this.ironGolemRightLeg = new ModelRenderer(this, 0, 22).setTextureSize(s, s2);
        this.ironGolemRightLeg.mirror = true;
        this.ironGolemRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0F, 18.0F + f2, 0.0F);
        this.ironGolemRightLeg.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        this.ironGolemHead.render(f6);
        this.ironGolemBody.render(f6);
        this.ironGolemLeftLeg.render(f6);
        this.ironGolemRightLeg.render(f6);
        this.ironGolemRightArm.render(f6);
        this.ironGolemLeftArm.render(f6);
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        this.ironGolemHead.rotateAngleY = f4 / (180.0F / (float)Math.PI);
        this.ironGolemHead.rotateAngleX = f5 / (180.0F / (float)Math.PI);
        this.ironGolemLeftLeg.rotateAngleX = -1.5F * this.abs(f, 13.0F) * f2;
        this.ironGolemRightLeg.rotateAngleX = 1.5F * this.abs(f, 13.0F) * f2;
        this.ironGolemLeftLeg.rotateAngleY = 0.0F;
        this.ironGolemRightLeg.rotateAngleY = 0.0F;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entityLivingBase, float f, float f2, float f3) {
        EntityIronGolem entityIronGolem = (EntityIronGolem)entityLivingBase;
        int attackTimer = entityIronGolem.getAttackTimer();
        if (attackTimer > 0) {
            this.ironGolemRightArm.rotateAngleX = -2.0F + 1.5F * this.abs(attackTimer - f3, 10.0F);
            this.ironGolemLeftArm.rotateAngleX = -2.0F + 1.5F * this.abs(attackTimer - f3, 10.0F);
        } else {
            int holdRoseTick = entityIronGolem.getHoldRoseTick();
            if (holdRoseTick > 0) {
                this.ironGolemRightArm.rotateAngleX = -0.8F + 0.025F * this.abs(holdRoseTick, 70.0F);
                this.ironGolemLeftArm.rotateAngleX = 0.0F;
            } else {
                this.ironGolemRightArm.rotateAngleX = (-0.2F + 1.5F * this.abs(f, 13.0F)) * f2;
                this.ironGolemLeftArm.rotateAngleX = (-0.2F - 1.5F * this.abs(f, 13.0F)) * f2;
            }
        }
    }

    public float abs(float f, float f2) {
        return (Math.abs(f % f2 - f2 * 0.5F) - f2 * 0.25F) / (f2 * 0.25F);
    }
}
