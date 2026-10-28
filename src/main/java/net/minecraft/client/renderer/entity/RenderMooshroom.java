package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderMooshroom extends RenderLiving {
    public static ResourceLocation mooshroomTextures = new ResourceLocation("textures/entity/cow/mooshroom.png");

    public RenderMooshroom(ModelBase modelBase, float f) {
        super(modelBase, f);
    }

    public void doRender(EntityMooshroom entityMooshroom, double d, double d2, double d3, float f, float f2) {
        super.doRender(entityMooshroom, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityMooshroom entityMooshroom) {
        return mooshroomTextures;
    }

    public void renderEquippedItems(EntityMooshroom entityMooshroom, float f) {
        super.renderEquippedItems(entityMooshroom, f);
        if (!entityMooshroom.isChild()) {
            this.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glEnable(2884);
            GL11.glPushMatrix();
            GL11.glScalef(1.0F, -1.0F, 1.0F);
            GL11.glTranslatef(0.2F, 0.4F, 0.5F);
            GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
            this.renderBlocks.renderBlockAsItem(Blocks.red_mushroom, 0, 1.0F);
            GL11.glTranslatef(0.1F, 0.0F, -0.6F);
            GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
            this.renderBlocks.renderBlockAsItem(Blocks.red_mushroom, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            ((ModelQuadruped)this.mainModel).head.postRender(0.0625F);
            GL11.glScalef(1.0F, -1.0F, 1.0F);
            GL11.glTranslatef(0.0F, 0.75F, -0.2F);
            GL11.glRotatef(12.0F, 0.0F, 1.0F, 0.0F);
            this.renderBlocks.renderBlockAsItem(Blocks.red_mushroom, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(2884);
        }
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityMooshroom)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        this.renderEquippedItems((EntityMooshroom)entityLivingBase, f);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityMooshroom)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityMooshroom)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityMooshroom)entity, d, d2, d3, f, f2);
    }
}
