package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWitch extends RenderLiving {
    public static ResourceLocation witchTextures = new ResourceLocation("textures/entity/witch.png");
    public ModelWitch witchModel = (ModelWitch)this.mainModel;

    public RenderWitch() {
        super(new ModelWitch(0.0F), 0.5F);
    }

    public void doRender(EntityWitch entityWitch, double d, double d2, double d3, float f, float f2) {
        ItemStack heldItem = entityWitch.getHeldItem();
        this.witchModel.flag = heldItem != null;
        super.doRender(entityWitch, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityWitch entityWitch) {
        return witchTextures;
    }

    public void renderEquippedItems(EntityWitch entityWitch, float f) {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        super.renderEquippedItems(entityWitch, f);
        ItemStack heldItem = entityWitch.getHeldItem();
        if (heldItem != null) {
            GL11.glPushMatrix();
            if (this.mainModel.isChild) {
                float f2 = 0.5F;
                GL11.glTranslatef(0.0F, 0.625F, 0.0F);
                GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
                GL11.glScalef(f2, f2, f2);
            }

            this.witchModel.villagerNose.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.53125F, 0.21875F);
            if (heldItem.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(heldItem.getItem()).getRenderType())) {
                float f2 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f2 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
            } else if (heldItem.getItem() == Items.bow) {
                float f2 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else if (heldItem.getItem().isFull3D()) {
                float f2 = 0.625F;
                if (heldItem.getItem().shouldRotateAroundWhenRendering()) {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                this.glTranslatef();
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else {
                float f2 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(f2, f2, f2);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
            this.renderManager.itemRenderer.renderItem(entityWitch, heldItem, 0);
            if (heldItem.getItem().requiresMultipleRenderPasses()) {
                this.renderManager.itemRenderer.renderItem(entityWitch, heldItem, 1);
            }

            GL11.glPopMatrix();
        }
    }

    public void glTranslatef() {
        GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
    }

    public void preRenderCallback(EntityWitch entityWitch, float f) {
        float f2 = 0.9375F;
        GL11.glScalef(f2, f2, f2);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityWitch)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityWitch)entityLivingBase, f);
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        this.renderEquippedItems((EntityWitch)entityLivingBase, f);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityWitch)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityWitch)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityWitch)entity, d, d2, d3, f, f2);
    }
}
