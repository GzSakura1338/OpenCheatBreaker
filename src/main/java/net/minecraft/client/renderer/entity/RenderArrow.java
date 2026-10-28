package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderArrow extends Render {
    public static ResourceLocation arrowTextures = new ResourceLocation("textures/entity/arrow.png");

    public void doRender(EntityArrow entityArrow, double d, double d2, double d3, float f, float f2) {
        if (!this.checkEntityArrow(entityArrow)) {
            this.bindEntityTexture(entityArrow);
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d, (float)d2, (float)d3);
            GL11.glRotatef(entityArrow.prevRotationYaw + (entityArrow.rotationYaw - entityArrow.prevRotationYaw) * f2 - 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(entityArrow.prevRotationPitch + (entityArrow.rotationPitch - entityArrow.prevRotationPitch) * f2, 0.0F, 0.0F, 1.0F);
            Tessellator instance = Tessellator.instance;
            byte b = 0;
            float f3 = 0.0F;
            float f4 = 0.5F;
            float f5 = (0 + b * 10) / 32.0F;
            float f6 = (5 + b * 10) / 32.0F;
            float f7 = 0.0F;
            float f8 = 0.15625F;
            float f9 = (5 + b * 10) / 32.0F;
            float f10 = (10 + b * 10) / 32.0F;
            float f11 = 0.05625F;
            GL11.glEnable(32826);
            float f12 = entityArrow.arrowShake - f2;
            if (f12 > 0.0F) {
                float f13 = -MathHelper.sin(f12 * 3.0F) * f12;
                GL11.glRotatef(f13, 0.0F, 0.0F, 1.0F);
            }

            GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(f11, f11, f11);
            GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
            GL11.glNormal3f(f11, 0.0F, 0.0F);
            instance.startDrawingQuads();
            instance.addVertexWithUV(-7.0, -2.0, -2.0, f7, f9);
            instance.addVertexWithUV(-7.0, -2.0, 2.0, f8, f9);
            instance.addVertexWithUV(-7.0, 2.0, 2.0, f8, f10);
            instance.addVertexWithUV(-7.0, 2.0, -2.0, f7, f10);
            instance.draw();
            GL11.glNormal3f(-f11, 0.0F, 0.0F);
            instance.startDrawingQuads();
            instance.addVertexWithUV(-7.0, 2.0, -2.0, f7, f9);
            instance.addVertexWithUV(-7.0, 2.0, 2.0, f8, f9);
            instance.addVertexWithUV(-7.0, -2.0, 2.0, f8, f10);
            instance.addVertexWithUV(-7.0, -2.0, -2.0, f7, f10);
            instance.draw();

            for (int i = 0; i < 4; i++) {
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glNormal3f(0.0F, 0.0F, f11);
                instance.startDrawingQuads();
                instance.addVertexWithUV(-8.0, -2.0, 0.0, f3, f5);
                instance.addVertexWithUV(8.0, -2.0, 0.0, f4, f5);
                instance.addVertexWithUV(8.0, 2.0, 0.0, f4, f6);
                instance.addVertexWithUV(-8.0, 2.0, 0.0, f3, f6);
                instance.draw();
            }

            GL11.glDisable(32826);
            GL11.glPopMatrix();
        }
    }

    public ResourceLocation getEntityTexture(EntityArrow entityArrow) {
        return arrowTextures;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityArrow)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityArrow)entity, d, d2, d3, f, f2);
    }

    public boolean checkEntityArrow(EntityArrow entityArrow) {
        boolean isInGround = entityArrow.isInGround();
        boolean flag = entityArrow.motionX > 0.0 || entityArrow.motionY > 0.0 || entityArrow.motionZ > 0.0;
        return CheatBreaker.getInstance().getKeybindSettings().hideMovingArrows.getBooleanValue() && flag && !isInGround
            || CheatBreaker.getInstance().getKeybindSettings().hideGroundedArrows.getBooleanValue() && isInGround;
    }
}
