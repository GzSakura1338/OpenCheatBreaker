package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWitherSkull extends Render {
    public static ResourceLocation invulnerableWitherTextures = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
    public static ResourceLocation witherTextures = new ResourceLocation("textures/entity/wither/wither.png");
    public ModelSkeletonHead skeletonHeadModel = new ModelSkeletonHead();

    public float computeFloat(float f, float f2, float f3) {
        float f4 = f2 - f;

        while (f4 < -180.0F) {
            f4 += 360.0F;
        }

        while (f4 >= 180.0F) {
            f4 -= 360.0F;
        }

        return f + f3 * f4;
    }

    public void doRender(EntityWitherSkull entityWitherSkull, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        GL11.glDisable(2884);
        float computeFloat = this.computeFloat(entityWitherSkull.prevRotationYaw, entityWitherSkull.rotationYaw, f2);
        float f3 = entityWitherSkull.prevRotationPitch + (entityWitherSkull.rotationPitch - entityWitherSkull.prevRotationPitch) * f2;
        GL11.glTranslatef((float)d, (float)d2, (float)d3);
        float f4 = 0.0625F;
        GL11.glEnable(32826);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glEnable(3008);
        this.bindEntityTexture(entityWitherSkull);
        this.skeletonHeadModel.render(entityWitherSkull, 0.0F, 0.0F, 0.0F, computeFloat, f3, f4);
        GL11.glPopMatrix();
    }

    public ResourceLocation getEntityTexture(EntityWitherSkull entityWitherSkull) {
        return entityWitherSkull.isInvulnerable() ? invulnerableWitherTextures : witherTextures;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityWitherSkull)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityWitherSkull)entity, d, d2, d3, f, f2);
    }
}
