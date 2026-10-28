package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public abstract class RenderLiving extends RendererLivingEntity {
    public RenderLiving(ModelBase modelBase, float f) {
        super(modelBase, f);
    }

    public boolean canRenderName(EntityLiving targetEntity) {
        return super.canRenderName(targetEntity)
            && (targetEntity.getAlwaysRenderNameTagForRender() || targetEntity.hasCustomNameTag() && targetEntity == this.renderManager.entity);
    }

    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        super.doRender(entityLiving, d, d2, d3, f, f2);
        this.render(entityLiving, d, d2, d3, f, f2);
    }

    public double computeDouble(double d, double d2, double d3) {
        return d + (d2 - d) * d3;
    }

    public void render(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        if (!Config.isShaders() || !Shaders.isShadowPass) {
            Entity leashedToEntity = entityLiving.getLeashedToEntity();
            if (leashedToEntity != null) {
                d2 -= (1.6 - entityLiving.height) * 0.5;
                Tessellator instance = Tessellator.instance;
                double d4 = this.computeDouble(leashedToEntity.prevRotationYaw, leashedToEntity.rotationYaw, f2 * 0.5F) * (float) (Math.PI / 180.0);
                double d5 = this.computeDouble(leashedToEntity.prevRotationPitch, leashedToEntity.rotationPitch, f2 * 0.5F) * (float) (Math.PI / 180.0);
                double cos = Math.cos(d4);
                double sin = Math.sin(d4);
                double sin2 = Math.sin(d5);
                if (leashedToEntity instanceof EntityHanging) {
                    cos = 0.0;
                    sin = 0.0;
                    sin2 = -1.0;
                }

                double cos2 = Math.cos(d5);
                double d6 = this.computeDouble(leashedToEntity.prevPosX, leashedToEntity.posX, f2) - cos * 0.7 - sin * 0.5 * cos2;
                double d7 = this.computeDouble(leashedToEntity.prevPosY + leashedToEntity.getEyeHeight() * 0.7, leashedToEntity.posY + leashedToEntity.getEyeHeight() * 0.7, f2) - sin2 * 0.5 - 0.25;
                double d8 = this.computeDouble(leashedToEntity.prevPosZ, leashedToEntity.posZ, f2) - sin * 0.7 + cos * 0.5 * cos2;
                double d9 = this.computeDouble(entityLiving.prevRenderYawOffset, entityLiving.renderYawOffset, f2) * (float) (Math.PI / 180.0) + (Math.PI / 2);
                cos = Math.cos(d9) * entityLiving.width * 0.4;
                sin = Math.sin(d9) * entityLiving.width * 0.4;
                double d10 = this.computeDouble(entityLiving.prevPosX, entityLiving.posX, f2) + cos;
                double computeDouble = this.computeDouble(entityLiving.prevPosY, entityLiving.posY, f2);
                double d11 = this.computeDouble(entityLiving.prevPosZ, entityLiving.posZ, f2) + sin;
                d += cos;
                d3 += sin;
                double d12 = (float)(d6 - d10);
                double d13 = (float)(d7 - computeDouble);
                double d14 = (float)(d8 - d11);
                GL11.glDisable(3553);
                GL11.glDisable(2896);
                GL11.glDisable(2884);
                if (Config.isShaders()) {
                    Shaders.beginLeash();
                }

                boolean flag = true;
                double d15 = 0.025;
                instance.startDrawing(5);

                for (int i = 0; i <= 24; i++) {
                    if (i % 2 == 0) {
                        instance.setColorRGBA_F(0.5F, 0.4F, 0.3F, 1.0F);
                    } else {
                        instance.setColorRGBA_F(0.35F, 0.28F, 0.21000001F, 1.0F);
                    }

                    float f3 = i / 24.0F;
                    instance.addVertex(
                        d + d12 * f3 + 0.0, d2 + d13 * (f3 * f3 + f3) * 0.5 + ((24.0F - i) / 18.0F + 0.125F), d3 + d14 * f3
                    );
                    instance.addVertex(
                        d + d12 * f3 + 0.025, d2 + d13 * (f3 * f3 + f3) * 0.5 + ((24.0F - i) / 18.0F + 0.125F) + 0.025, d3 + d14 * f3
                    );
                }

                instance.draw();
                instance.startDrawing(5);

                for (int i = 0; i <= 24; i++) {
                    if (i % 2 == 0) {
                        instance.setColorRGBA_F(0.5F, 0.4F, 0.3F, 1.0F);
                    } else {
                        instance.setColorRGBA_F(0.35F, 0.28F, 0.21000001F, 1.0F);
                    }

                    float f3 = i / 24.0F;
                    instance.addVertex(
                        d + d12 * f3 + 0.0, d2 + d13 * (f3 * f3 + f3) * 0.5 + ((24.0F - i) / 18.0F + 0.125F) + 0.025, d3 + d14 * f3
                    );
                    instance.addVertex(
                        d + d12 * f3 + 0.025, d2 + d13 * (f3 * f3 + f3) * 0.5 + ((24.0F - i) / 18.0F + 0.125F), d3 + d14 * f3 + 0.025
                    );
                }

                instance.draw();
                if (Config.isShaders()) {
                    Shaders.endLeash();
                }

                GL11.glEnable(2896);
                GL11.glEnable(3553);
                GL11.glEnable(2884);
            }
        }
    }

    @Override
    public boolean canRenderName(EntityLivingBase targetEntity) {
        return this.canRenderName((EntityLiving)targetEntity);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityLiving)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityLiving)entity, d, d2, d3, f, f2);
    }
}
