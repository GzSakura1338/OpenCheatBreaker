package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWither;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWither extends RenderLiving {
    public static ResourceLocation invulnerableWitherTextures = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
    public static ResourceLocation witherTextures = new ResourceLocation("textures/entity/wither/wither.png");
    public int intValue = ((ModelWither)this.mainModel).constantInt();

    public RenderWither() {
        super(new ModelWither(), 1.0F);
    }

    public void doRender(EntityWither entityWither, double d, double d2, double d3, float f, float f2) {
        BossStatus.setBossStatus(entityWither, true);
        int constantInt = ((ModelWither)this.mainModel).constantInt();
        if (constantInt != this.intValue) {
            this.intValue = constantInt;
            this.mainModel = new ModelWither();
        }

        super.doRender(entityWither, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityWither entityWither) {
        int invulTime = entityWither.getInvulTime();
        return invulTime <= 0 || invulTime <= 80 && invulTime / 5 % 2 == 1 ? witherTextures : invulnerableWitherTextures;
    }

    public void preRenderCallback(EntityWither entityWither, float f) {
        int invulTime = entityWither.getInvulTime();
        if (invulTime > 0) {
            float f2 = 2.0F - (invulTime - f) / 220.0F * 0.5F;
            GL11.glScalef(f2, f2, f2);
        } else {
            GL11.glScalef(2.0F, 2.0F, 2.0F);
        }
    }

    public int shouldRenderPass(EntityWither entityWither, int n, float f) {
        if (entityWither.isArmored()) {
            GL11.glDepthMask(!entityWither.isInvisible());
            if (n == 1) {
                float f2 = entityWither.ticksExisted + f;
                this.bindTexture(invulnerableWitherTextures);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                float f3 = MathHelper.cos(f2 * 0.02F) * 3.0F;
                float f4 = f2 * 0.01F;
                GL11.glTranslatef(f3, f4, 0.0F);
                this.setRenderPassModel(this.mainModel);
                GL11.glMatrixMode(5888);
                GL11.glEnable(3042);
                float f5 = 0.5F;
                GL11.glColor4f(f5, f5, f5, 1.0F);
                GL11.glDisable(2896);
                GL11.glBlendFunc(1, 1);
                GL11.glTranslatef(0.0F, -0.01F, 0.0F);
                GL11.glScalef(1.1F, 1.1F, 1.1F);
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

    public int inheritRenderPass(EntityWither entityWither, int n, float f) {
        return -1;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityWither)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityWither)entityLivingBase, f);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntityWither)entityLivingBase, n, f);
    }

    @Override
    public int inheritRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.inheritRenderPass((EntityWither)entityLivingBase, n, f);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityWither)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityWither)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityWither)entity, d, d2, d3, f, f2);
    }
}
