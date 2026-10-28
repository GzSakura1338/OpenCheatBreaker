package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBlaze extends ModelBase {
    public ModelRenderer[] blazeSticks = new ModelRenderer[12];
    public ModelRenderer blazeHead;

    public ModelBlaze() {
        for (int i = 0; i < this.blazeSticks.length; i++) {
            this.blazeSticks[i] = new ModelRenderer(this, 0, 16);
            this.blazeSticks[i].addBox(0.0F, 0.0F, 0.0F, 2, 8, 2);
        }

        this.blazeHead = new ModelRenderer(this, 0, 0);
        this.blazeHead.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
    }

    public int constantInt() {
        return 8;
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        this.blazeHead.render(f6);

        for (int i = 0; i < this.blazeSticks.length; i++) {
            this.blazeSticks[i].render(f6);
        }
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        float f7 = f3 * (float) Math.PI * -0.1F;

        for (int i = 0; i < 4; i++) {
            this.blazeSticks[i].rotationPointY = -2.0F + MathHelper.cos((i * 2 + f3) * 0.25F);
            this.blazeSticks[i].rotationPointX = MathHelper.cos(f7) * 9.0F;
            this.blazeSticks[i].rotationPointZ = MathHelper.sin(f7) * 9.0F;
            f7++;
        }

        f7 = (float) (Math.PI / 4) + f3 * (float) Math.PI * 0.03F;

        for (int i = 4; i < 8; i++) {
            this.blazeSticks[i].rotationPointY = 2.0F + MathHelper.cos((i * 2 + f3) * 0.25F);
            this.blazeSticks[i].rotationPointX = MathHelper.cos(f7) * 7.0F;
            this.blazeSticks[i].rotationPointZ = MathHelper.sin(f7) * 7.0F;
            f7++;
        }

        f7 = 0.47123894F + f3 * (float) Math.PI * -0.05F;

        for (int i = 8; i < 12; i++) {
            this.blazeSticks[i].rotationPointY = 11.0F + MathHelper.cos((i * 1.5F + f3) * 0.5F);
            this.blazeSticks[i].rotationPointX = MathHelper.cos(f7) * 5.0F;
            this.blazeSticks[i].rotationPointZ = MathHelper.sin(f7) * 5.0F;
            f7++;
        }

        this.blazeHead.rotateAngleY = f4 / (180.0F / (float)Math.PI);
        this.blazeHead.rotateAngleX = f5 / (180.0F / (float)Math.PI);
    }
}
