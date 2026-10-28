package net.minecraft.client.model;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelGhast extends ModelBase {
    public ModelRenderer[] tentacles = new ModelRenderer[9];
    public ModelRenderer body;

    public ModelGhast() {
        byte b = -16;
        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
        this.body.rotationPointY += 24 + b;
        Random random = new Random(1660L);

        for (int i = 0; i < this.tentacles.length; i++) {
            this.tentacles[i] = new ModelRenderer(this, 0, 0);
            float f = ((i % 3 - i / 3 % 2 * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
            float f2 = (i / 3 / 2.0F * 2.0F - 1.0F) * 5.0F;
            int n = random.nextInt(7) + 8;
            this.tentacles[i].addBox(-1.0F, 0.0F, -1.0F, 2, n, 2);
            this.tentacles[i].rotationPointX = f;
            this.tentacles[i].rotationPointZ = f2;
            this.tentacles[i].rotationPointY = 31 + b;
        }
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        for (int i = 0; i < this.tentacles.length; i++) {
            this.tentacles[i].rotateAngleX = 0.2F * MathHelper.sin(f3 * 0.3F + i) + 0.4F;
        }
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.6F, 0.0F);
        this.body.render(f6);

        for (ModelRenderer modelRenderer : this.tentacles) {
            modelRenderer.render(f6);
        }

        GL11.glPopMatrix();
    }
}
