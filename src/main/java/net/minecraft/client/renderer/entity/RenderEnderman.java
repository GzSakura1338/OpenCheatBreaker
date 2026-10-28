package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class RenderEnderman extends RenderLiving {
    public static ResourceLocation endermanEyesTexture = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
    public static ResourceLocation endermanTextures = new ResourceLocation("textures/entity/enderman/enderman.png");
    public Random rnd = new Random();
    public ModelEnderman endermanModel = (ModelEnderman)this.mainModel;

    public RenderEnderman() {
        super(new ModelEnderman(), 0.5F);
        this.setRenderPassModel(this.endermanModel);
    }

    public void doRender(EntityEnderman entityEnderman, double d, double d2, double d3, float f, float f2) {
        this.endermanModel.isCarrying = entityEnderman.getCarriedBlock().getMaterial() != Material.air;
        this.endermanModel.isAttacking = entityEnderman.isScreaming();
        if (entityEnderman.isScreaming()) {
            double d4 = 0.02;
            d += this.rnd.nextGaussian() * d4;
            d3 += this.rnd.nextGaussian() * d4;
        }

        super.doRender(entityEnderman, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityEnderman entityEnderman) {
        return endermanTextures;
    }

    public void renderEquippedItems(EntityEnderman entityEnderman, float f) {
        super.renderEquippedItems(entityEnderman, f);
        if (entityEnderman.getCarriedBlock().getMaterial() != Material.air) {
            GL11.glEnable(32826);
            GL11.glPushMatrix();
            float f2 = 0.5F;
            GL11.glTranslatef(0.0F, 0.6875F, -0.75F);
            f2 *= 1.0F;
            GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(-f2, -f2, f2);
            int brightnessForRender = entityEnderman.getBrightnessForRender(f);
            int n = brightnessForRender % 65536;
            int n2 = brightnessForRender / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindTexture(TextureMap.locationBlocksTexture);
            this.renderBlocks.renderBlockAsItem(entityEnderman.getCarriedBlock(), entityEnderman.getCarryingData(), 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(32826);
        }
    }

    public int shouldRenderPass(EntityEnderman entityEnderman, int n, float f) {
        if (n != 0) {
            return -1;
        }

        this.bindTexture(endermanEyesTexture);
        float f2 = 1.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glBlendFunc(1, 1);
        GL11.glDisable(2896);
        GL11.glDepthMask(!entityEnderman.isInvisible());
        char c = '\uf0f0';
        int n2 = c % 65536;
        int n3 = c / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n2 / 1.0F, n3 / 1.0F);
        GL11.glEnable(2896);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, f2);
        if (Config.isShaders()) {
            Shaders.beginSpiderEyes();
        }

        return 1;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityEnderman)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntityEnderman)entityLivingBase, n, f);
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        this.renderEquippedItems((EntityEnderman)entityLivingBase, f);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityEnderman)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityEnderman)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityEnderman)entity, d, d2, d3, f, f2);
    }
}
