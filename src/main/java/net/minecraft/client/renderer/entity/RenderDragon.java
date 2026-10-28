package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class RenderDragon extends RenderLiving {
    public static ResourceLocation enderDragonExplodingTextures = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
    public static ResourceLocation enderDragonCrystalBeamTextures = new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
    public static ResourceLocation enderDragonEyesTextures = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    public static ResourceLocation enderDragonTextures = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    public ModelDragon modelDragon = (ModelDragon)this.mainModel;

    public RenderDragon() {
        super(new ModelDragon(0.0F), 0.5F);
        this.setRenderPassModel(this.mainModel);
    }

    public void rotateCorpse(EntityDragon entityDragon, float f, float f2, float f3) {
        float f4 = (float)entityDragon.getMovementOffsets(7, f3)[0];
        float f5 = (float)(entityDragon.getMovementOffsets(5, f3)[1] - entityDragon.getMovementOffsets(10, f3)[1]);
        GL11.glRotatef(-f4, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(f5 * 10.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.0F, 1.0F);
        if (entityDragon.deathTime > 0) {
            float f6 = (entityDragon.deathTime + f3 - 1.0F) / 20.0F * 1.6F;
            f6 = MathHelper.sqrt_float(f6);
            if (f6 > 1.0F) {
                f6 = 1.0F;
            }

            GL11.glRotatef(f6 * this.getDeathMaxRotation(entityDragon), 0.0F, 0.0F, 1.0F);
        }
    }

    public void renderModel(EntityDragon entityDragon, float f, float f2, float f3, float f4, float f5, float f6) {
        if (entityDragon.deathTicks > 0) {
            float f7 = entityDragon.deathTicks / 200.0F;
            GL11.glDepthFunc(515);
            GL11.glEnable(3008);
            GL11.glAlphaFunc(516, f7);
            this.bindTexture(enderDragonExplodingTextures);
            this.mainModel.render(entityDragon, f, f2, f3, f4, f5, f6);
            GL11.glAlphaFunc(516, 0.1F);
            GL11.glDepthFunc(514);
        }

        this.bindEntityTexture(entityDragon);
        this.mainModel.render(entityDragon, f, f2, f3, f4, f5, f6);
        if (entityDragon.hurtTime > 0) {
            GL11.glDepthFunc(514);
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);
            this.mainModel.render(entityDragon, f, f2, f3, f4, f5, f6);
            GL11.glEnable(3553);
            GL11.glDisable(3042);
            GL11.glDepthFunc(515);
        }
    }

    public void doRender(EntityDragon entityDragon, double d, double d2, double d3, float f, float f2) {
        BossStatus.setBossStatus(entityDragon, false);
        super.doRender(entityDragon, d, d2, d3, f, f2);
        if (entityDragon.healingEnderCrystal != null) {
            float f3 = entityDragon.healingEnderCrystal.innerRotation + f2;
            float f4 = MathHelper.sin(f3 * 0.2F) / 2.0F + 0.5F;
            f4 = (f4 * f4 + f4) * 0.2F;
            float f5 = (float)(entityDragon.healingEnderCrystal.posX - entityDragon.posX - (entityDragon.prevPosX - entityDragon.posX) * (1.0F - f2));
            float f6 = (float)(f4 + entityDragon.healingEnderCrystal.posY - 1.0 - entityDragon.posY - (entityDragon.prevPosY - entityDragon.posY) * (1.0F - f2));
            float f7 = (float)(entityDragon.healingEnderCrystal.posZ - entityDragon.posZ - (entityDragon.prevPosZ - entityDragon.posZ) * (1.0F - f2));
            float f8 = MathHelper.sqrt_float(f5 * f5 + f7 * f7);
            float f9 = MathHelper.sqrt_float(f5 * f5 + f6 * f6 + f7 * f7);
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d, (float)d2 + 2.0F, (float)d3);
            GL11.glRotatef((float)(-Math.atan2(f7, f5)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((float)(-Math.atan2(f8, f6)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
            Tessellator instance = Tessellator.instance;
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(2884);
            this.bindTexture(enderDragonCrystalBeamTextures);
            GL11.glShadeModel(7425);
            float f10 = 0.0F - (entityDragon.ticksExisted + f2) * 0.01F;
            float f11 = MathHelper.sqrt_float(f5 * f5 + f6 * f6 + f7 * f7) / 32.0F - (entityDragon.ticksExisted + f2) * 0.01F;
            instance.startDrawing(5);
            byte b = 8;

            for (int i = 0; i <= b; i++) {
                float f12 = MathHelper.sin(i % b * (float) Math.PI * 2.0F / b) * 0.75F;
                float f13 = MathHelper.cos(i % b * (float) Math.PI * 2.0F / b) * 0.75F;
                float f14 = i % b * 1.0F / b;
                instance.setColorOpaque_I(0);
                instance.addVertexWithUV(f12 * 0.2F, f13 * 0.2F, 0.0, f14, f11);
                instance.setColorOpaque_I(16777215);
                instance.addVertexWithUV(f12, f13, f9, f14, f10);
            }

            instance.draw();
            GL11.glEnable(2884);
            GL11.glShadeModel(7424);
            RenderHelper.enableStandardItemLighting();
            GL11.glPopMatrix();
        }
    }

    public ResourceLocation getEntityTexture(EntityDragon entityDragon) {
        return enderDragonTextures;
    }

    public void renderEquippedItems(EntityDragon entityDragon, float f) {
        super.renderEquippedItems(entityDragon, f);
        Tessellator instance = Tessellator.instance;
        if (entityDragon.deathTicks > 0) {
            RenderHelper.disableStandardItemLighting();
            float f2 = (entityDragon.deathTicks + f) / 200.0F;
            float f3 = 0.0F;
            if (f2 > 0.8F) {
                f3 = (f2 - 0.8F) / 0.2F;
            }

            Random random = new Random(432L);
            GL11.glDisable(3553);
            GL11.glShadeModel(7425);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 1);
            GL11.glDisable(3008);
            GL11.glEnable(2884);
            GL11.glDepthMask(false);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -1.0F, -2.0F);

            for (int i = 0; i < (f2 + f2 * f2) / 2.0F * 60.0F; i++) {
                GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F + f2 * 90.0F, 0.0F, 0.0F, 1.0F);
                instance.startDrawing(6);
                float f4 = random.nextFloat() * 20.0F + 5.0F + f3 * 10.0F;
                float f5 = random.nextFloat() * 2.0F + 1.0F + f3 * 2.0F;
                instance.setColorRGBA_I(16777215, (int)(255.0F * (1.0F - f3)));
                instance.addVertex(0.0, 0.0, 0.0);
                instance.setColorRGBA_I(16711935, 0);
                instance.addVertex(-0.866 * f5, f4, -0.5F * f5);
                instance.addVertex(0.866 * f5, f4, -0.5F * f5);
                instance.addVertex(0.0, f4, 1.0F * f5);
                instance.addVertex(-0.866 * f5, f4, -0.5F * f5);
                instance.draw();
            }

            GL11.glPopMatrix();
            GL11.glDepthMask(true);
            GL11.glDisable(2884);
            GL11.glDisable(3042);
            GL11.glShadeModel(7424);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            RenderHelper.enableStandardItemLighting();
        }
    }

    public int shouldRenderPass(EntityDragon entityDragon, int n, float f) {
        if (n == 1) {
            GL11.glDepthFunc(515);
        }

        if (n != 0) {
            return -1;
        }

        this.bindTexture(enderDragonEyesTextures);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(1, 1);
        GL11.glDisable(2896);
        GL11.glDepthFunc(514);
        char c = '\uf0f0';
        int n2 = c % 65536;
        int n3 = c / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n2 / 1.0F, n3 / 1.0F);
        GL11.glEnable(2896);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (Config.isShaders()) {
            Shaders.beginSpiderEyes();
        }

        return 1;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityDragon)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntityDragon)entityLivingBase, n, f);
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        this.renderEquippedItems((EntityDragon)entityLivingBase, f);
    }

    @Override
    public void rotateCorpse(EntityLivingBase entityLivingBase, float f, float f2, float f3) {
        this.rotateCorpse((EntityDragon)entityLivingBase, f, f2, f3);
    }

    @Override
    public void renderModel(EntityLivingBase entityLivingBase, float f, float f2, float f3, float f4, float f5, float f6) {
        this.renderModel((EntityDragon)entityLivingBase, f, f2, f3, f4, f5, f6);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityDragon)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityDragon)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityDragon)entity, d, d2, d3, f, f2);
    }
}
