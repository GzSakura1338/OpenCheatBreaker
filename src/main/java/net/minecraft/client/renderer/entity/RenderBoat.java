package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBoat extends Render {
    public static ResourceLocation boatTextures = new ResourceLocation("textures/entity/boat.png");
    public ModelBase modelBoat;

    public RenderBoat() {
        this.shadowSize = 0.5F;
        this.modelBoat = new ModelBoat();
    }

    public void doRender(EntityBoat entityBoat, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d2, (float)d3);
        GL11.glRotatef(180.0F - f, 0.0F, 1.0F, 0.0F);
        float f3 = entityBoat.getTimeSinceHit() - f2;
        float f4 = entityBoat.getDamageTaken() - f2;
        if (f4 < 0.0F) {
            f4 = 0.0F;
        }

        if (f3 > 0.0F) {
            GL11.glRotatef(MathHelper.sin(f3) * f3 * f4 / 10.0F * entityBoat.getForwardDirection(), 1.0F, 0.0F, 0.0F);
        }

        float f5 = 0.75F;
        GL11.glScalef(f5, f5, f5);
        GL11.glScalef(1.0F / f5, 1.0F / f5, 1.0F / f5);
        this.bindEntityTexture(entityBoat);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.modelBoat.render(entityBoat, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    public ResourceLocation getEntityTexture(EntityBoat entityBoat) {
        return boatTextures;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityBoat)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityBoat)entity, d, d2, d3, f, f2);
    }
}
