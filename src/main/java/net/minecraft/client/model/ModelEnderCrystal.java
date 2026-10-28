package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelEnderCrystal extends ModelBase {
    public ModelRenderer glass = new ModelRenderer(this, "glass");
    public ModelRenderer cube;
    public ModelRenderer base;

    public ModelEnderCrystal(float f, boolean flag) {
        this.glass.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
        this.cube = new ModelRenderer(this, "cube");
        this.cube.setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
        if (flag) {
            this.base = new ModelRenderer(this, "base");
            this.base.setTextureOffset(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12);
        }
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glTranslatef(0.0F, -0.5F, 0.0F);
        if (this.base != null) {
            this.base.render(f6);
        }

        GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.8F + f3, 0.0F);
        GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        this.glass.render(f6);
        float f7 = 0.875F;
        GL11.glScalef(f7, f7, f7);
        GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
        this.glass.render(f6);
        GL11.glScalef(f7, f7, f7);
        GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
        this.cube.render(f6);
        GL11.glPopMatrix();
    }
}
