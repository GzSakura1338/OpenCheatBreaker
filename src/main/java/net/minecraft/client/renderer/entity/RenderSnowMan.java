package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSnowMan extends RenderLiving {
    public static ResourceLocation snowManTextures = new ResourceLocation("textures/entity/snowman.png");
    public ModelSnowMan snowmanModel = (ModelSnowMan)this.mainModel;

    public RenderSnowMan() {
        super(new ModelSnowMan(), 0.5F);
        this.setRenderPassModel(this.snowmanModel);
    }

    public void renderEquippedItems(EntitySnowman entitySnowman, float f) {
        super.renderEquippedItems(entitySnowman, f);
        ItemStack itemStack = new ItemStack(Blocks.pumpkin, 1);
        if (itemStack.getItem() instanceof ItemBlock) {
            GL11.glPushMatrix();
            this.snowmanModel.head.postRender(0.0625F);
            if (RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType())) {
                float f2 = 0.625F;
                GL11.glTranslatef(0.0F, -0.34375F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
            }

            this.renderManager.itemRenderer.renderItem(entitySnowman, itemStack, 0);
            GL11.glPopMatrix();
        }
    }

    public ResourceLocation getEntityTexture(EntitySnowman entitySnowman) {
        return snowManTextures;
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        this.renderEquippedItems((EntitySnowman)entityLivingBase, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntitySnowman)entity);
    }
}
