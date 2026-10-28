package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.IntEvent3;
import com.cheatbreaker.client.event.SimpleEvent31;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderFish extends Render {
    public static ResourceLocation PARTICLES = new ResourceLocation("textures/particle/particles.png");

    public void doRender(EntityFishHook entityFishHook, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d2, (float)d3);
        GL11.glEnable(32826);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        this.bindEntityTexture(entityFishHook);
        Tessellator instance = Tessellator.instance;
        byte b = 1;
        byte b2 = 2;
        float f3 = (b * 8 + 0) / 128.0F;
        float f4 = (b * 8 + 8) / 128.0F;
        float f5 = (b2 * 8 + 0) / 128.0F;
        float f6 = (b2 * 8 + 8) / 128.0F;
        float f7 = 1.0F;
        float f8 = 0.5F;
        float f9 = 0.5F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        instance.startDrawingQuads();
        instance.setNormal(0.0F, 1.0F, 0.0F);
        instance.addVertexWithUV(0.0F - f8, 0.0F - f9, 0.0, f3, f6);
        instance.addVertexWithUV(f7 - f8, 0.0F - f9, 0.0, f4, f6);
        instance.addVertexWithUV(f7 - f8, 1.0F - f9, 0.0, f4, f5);
        instance.addVertexWithUV(0.0F - f8, 1.0F - f9, 0.0, f3, f5);
        instance.draw();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
        if (entityFishHook.angler != null) {
            float swingProgress = entityFishHook.angler.getSwingProgress(f2);
            float sin = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
            Vec3 createVectorHelper = Vec3.createVectorHelper(-0.5, 0.03, 0.8);
            createVectorHelper.rotateAroundX(-(entityFishHook.angler.prevRotationPitch + (entityFishHook.angler.rotationPitch - entityFishHook.angler.prevRotationPitch) * f2) * (float) Math.PI / 180.0F);
            createVectorHelper.rotateAroundY(-(entityFishHook.angler.prevRotationYaw + (entityFishHook.angler.rotationYaw - entityFishHook.angler.prevRotationYaw) * f2) * (float) Math.PI / 180.0F);
            createVectorHelper.rotateAroundY(sin * 0.5F);
            createVectorHelper.rotateAroundX(-sin * 0.7F);
            double d4 = entityFishHook.angler.prevPosX + (entityFishHook.angler.posX - entityFishHook.angler.prevPosX) * f2 + createVectorHelper.xCoord;
            double d5 = entityFishHook.angler.prevPosY + (entityFishHook.angler.posY - entityFishHook.angler.prevPosY) * f2 + createVectorHelper.yCoord;
            double d6 = entityFishHook.angler.prevPosZ + (entityFishHook.angler.posZ - entityFishHook.angler.prevPosZ) * f2 + createVectorHelper.zCoord;
            double d7 = entityFishHook.angler == Minecraft.getMinecraft().thePlayer ? 0.0 : entityFishHook.angler.getEyeHeight();
            if (this.renderManager.options.thirdPersonView > 0 || entityFishHook.angler != Minecraft.getMinecraft().thePlayer) {
                float f10 = (entityFishHook.angler.prevRenderYawOffset + (entityFishHook.angler.renderYawOffset - entityFishHook.angler.prevRenderYawOffset) * f2) * (float) Math.PI / 180.0F;
                double sin2 = MathHelper.sin(f10);
                double cos = MathHelper.cos(f10);
                d4 = entityFishHook.angler.prevPosX + (entityFishHook.angler.posX - entityFishHook.angler.prevPosX) * f2 - cos * 0.35 - sin2 * 0.85;
                d5 = entityFishHook.angler.prevPosY + d7 + (entityFishHook.angler.posY - entityFishHook.angler.prevPosY) * f2 - 0.45;
                d6 = entityFishHook.angler.prevPosZ + (entityFishHook.angler.posZ - entityFishHook.angler.prevPosZ) * f2 - sin2 * 0.35 + cos * 0.85;
            }

            double d8 = entityFishHook.prevPosX + (entityFishHook.posX - entityFishHook.prevPosX) * f2;
            double d9 = entityFishHook.prevPosY + (entityFishHook.posY - entityFishHook.prevPosY) * f2 + 0.25;
            double d10 = entityFishHook.prevPosZ + (entityFishHook.posZ - entityFishHook.prevPosZ) * f2;
            double d11 = (float)(d4 - d8);
            double d12 = (float)(d5 - d9);
            double d13 = (float)(d6 - d10);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            instance.startDrawing(3);
            int n = -16777216;
            IntEvent3 intEvent3 = new IntEvent3(n);
            CheatBreaker.getInstance().getEventBus().handleEvent(intEvent3);
            n = intEvent3.getIntValue();
            instance.setColorOpaque_I(n);
            byte b3 = 16;

            for (int i = 0; i <= b3; i++) {
                float f10 = (float)i / b3;
                instance.addVertex(d + d11 * f10, d2 + d12 * (f10 * f10 + f10) * 0.5 + 0.25, d3 + d13 * f10);
            }

            instance.draw();
            CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent31());
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }
    }

    public ResourceLocation getEntityTexture(EntityFishHook entityFishHook) {
        return PARTICLES;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityFishHook)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityFishHook)entity, d, d2, d3, f, f2);
    }
}
