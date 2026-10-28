package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderCreeper extends RenderLiving {
    public static ResourceLocation armoredCreeperTextures = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    public static ResourceLocation creeperTextures = new ResourceLocation("textures/entity/creeper/creeper.png");
    public ModelBase creeperModel = new ModelCreeper(2.0F);

    public RenderCreeper() {
        super(new ModelCreeper(), 0.5F);
    }

    public void preRenderCallback(EntityCreeper entityCreeper, float f) {
        float creeperFlashIntensity = entityCreeper.getCreeperFlashIntensity(f);
        float f2 = 1.0F + MathHelper.sin(creeperFlashIntensity * 100.0F) * creeperFlashIntensity * 0.01F;
        if (creeperFlashIntensity < 0.0F) {
            creeperFlashIntensity = 0.0F;
        }

        if (creeperFlashIntensity > 1.0F) {
            creeperFlashIntensity = 1.0F;
        }

        creeperFlashIntensity *= creeperFlashIntensity;
        creeperFlashIntensity *= creeperFlashIntensity;
        float f3 = (1.0F + creeperFlashIntensity * 0.4F) * f2;
        float f4 = (1.0F + creeperFlashIntensity * 0.1F) / f2;
        GL11.glScalef(f3, f4, f3);
    }

    public int getColorMultiplier(EntityCreeper entityCreeper, float f, float f2) {
        float creeperFlashIntensity = entityCreeper.getCreeperFlashIntensity(f2);
        if ((int)(creeperFlashIntensity * 10.0F) % 2 == 0) {
            return 0;
        }

        int n = (int)(creeperFlashIntensity * 0.2F * 255.0F);
        if (n < 0) {
            n = 0;
        }

        if (n > 255) {
            n = 255;
        }

        short s = 255;
        short s2 = 255;
        short s3 = 255;
        return n << 24 | s << 16 | s2 << 8 | s3;
    }

    public int shouldRenderPass(EntityCreeper entityCreeper, int n, float f) {
        if (entityCreeper.getPowered()) {
            GL11.glDepthMask(!entityCreeper.isInvisible());
            if (n == 1) {
                float f2 = entityCreeper.ticksExisted + f;
                this.bindTexture(armoredCreeperTextures);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                float f3 = f2 * 0.01F;
                float f4 = f2 * 0.01F;
                GL11.glTranslatef(f3, f4, 0.0F);
                this.setRenderPassModel(this.creeperModel);
                GL11.glMatrixMode(5888);
                GL11.glEnable(3042);
                float f5 = 0.5F;
                GL11.glColor4f(f5, f5, f5, 1.0F);
                GL11.glDisable(2896);
                GL11.glBlendFunc(1, 1);
                return 1;
            }

            if (n == 2) {
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(5888);
                GL11.glEnable(2896);
                GL11.glDisable(3042);
            }
        }

        return -1;
    }

    public int inheritRenderPass(EntityCreeper entityCreeper, int n, float f) {
        return -1;
    }

    public ResourceLocation getEntityTexture(EntityCreeper entityCreeper) {
        return creeperTextures;
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityCreeper)entityLivingBase, f);
    }

    @Override
    public int getColorMultiplier(EntityLivingBase entityLivingBase, float f, float f2) {
        return this.getColorMultiplier((EntityCreeper)entityLivingBase, f, f2);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntityCreeper)entityLivingBase, n, f);
    }

    @Override
    public int inheritRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.inheritRenderPass((EntityCreeper)entityLivingBase, n, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityCreeper)entity);
    }
}
