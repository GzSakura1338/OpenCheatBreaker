package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderLightningBolt extends Render {
    public void doRender(EntityLightningBolt entityLightningBolt, double d, double d2, double d3, float f, float f2) {
        Tessellator instance = Tessellator.instance;
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        double[] doubles = new double[8];
        double[] doubles2 = new double[8];
        double d4 = 0.0;
        double d5 = 0.0;
        Random random = new Random(entityLightningBolt.boltVertex);

        for (int i = 7; i >= 0; i--) {
            doubles[i] = d4;
            doubles2[i] = d5;
            d4 += random.nextInt(11) - 5;
            d5 += random.nextInt(11) - 5;
        }

        for (int i = 0; i < 4; i++) {
            Random random2 = new Random(entityLightningBolt.boltVertex);

            for (int j = 0; j < 3; j++) {
                int n = 7;
                int n2 = 0;
                if (j > 0) {
                    n = 7 - j;
                }

                if (j > 0) {
                    n2 = n - 2;
                }

                double d6 = doubles[n] - d4;
                double d7 = doubles2[n] - d5;

                for (int k = n; k >= n2; k--) {
                    double d8 = d6;
                    double d9 = d7;
                    if (j == 0) {
                        d6 += random2.nextInt(11) - 5;
                        d7 += random2.nextInt(11) - 5;
                    } else {
                        d6 += random2.nextInt(31) - 15;
                        d7 += random2.nextInt(31) - 15;
                    }

                    instance.startDrawing(5);
                    float f3 = 0.5F;
                    instance.setColorRGBA_F(0.9F * f3, 0.9F * f3, 1.0F * f3, 0.3F);
                    double d10 = 0.1 + i * 0.2;
                    if (j == 0) {
                        d10 *= k * 0.1 + 1.0;
                    }

                    double d11 = 0.1 + i * 0.2;
                    if (j == 0) {
                        d11 *= (k - 1) * 0.1 + 1.0;
                    }

                    for (int m = 0; m < 5; m++) {
                        double d12 = d + 0.5 - d10;
                        double d13 = d3 + 0.5 - d10;
                        if (m == 1 || m == 2) {
                            d12 += d10 * 2.0;
                        }

                        if (m == 2 || m == 3) {
                            d13 += d10 * 2.0;
                        }

                        double d14 = d + 0.5 - d11;
                        double d15 = d3 + 0.5 - d11;
                        if (m == 1 || m == 2) {
                            d14 += d11 * 2.0;
                        }

                        if (m == 2 || m == 3) {
                            d15 += d11 * 2.0;
                        }

                        instance.addVertex(d14 + d6, d2 + k * 16, d15 + d7);
                        instance.addVertex(d12 + d8, d2 + (k + 1) * 16, d13 + d9);
                    }

                    instance.draw();
                }
            }
        }

        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }

    public ResourceLocation getEntityTexture(EntityLightningBolt entityLightningBolt) {
        return null;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityLightningBolt)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityLightningBolt)entity, d, d2, d3, f, f2);
    }
}
