package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelLeashKnot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderLeashKnot extends Render {
    public static ResourceLocation leashKnotTextures = new ResourceLocation("textures/entity/lead_knot.png");
    public ModelLeashKnot leashKnotModel = new ModelLeashKnot();

    public void doRender(EntityLeashKnot entityLeashKnot, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        GL11.glDisable(2884);
        GL11.glTranslatef((float)d, (float)d2, (float)d3);
        float f3 = 0.0625F;
        GL11.glEnable(32826);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glEnable(3008);
        this.bindEntityTexture(entityLeashKnot);
        this.leashKnotModel.render(entityLeashKnot, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, f3);
        GL11.glPopMatrix();
    }

    public ResourceLocation getEntityTexture(EntityLeashKnot entityLeashKnot) {
        return leashKnotTextures;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityLeashKnot)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityLeashKnot)entity, d, d2, d3, f, f2);
    }
}
