package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderFallingBlock extends Render {
    public RenderBlocks renderBlocks2 = new RenderBlocks();

    public RenderFallingBlock() {
        this.shadowSize = 0.5F;
    }

    public void doRender(EntityFallingBlock entityFallingBlock, double d, double d2, double d3, float f, float f2) {
        if (CheatBreaker.getInstance().getKeybindSettings().shouldRenderEntity(entityFallingBlock)) {
            World worldObj = entityFallingBlock.getWorldObj();
            Block block = entityFallingBlock.getBlock();
            int n = MathHelper.floor_double(entityFallingBlock.posX);
            int n2 = MathHelper.floor_double(entityFallingBlock.posY);
            int n3 = MathHelper.floor_double(entityFallingBlock.posZ);
            if (block != null && block != worldObj.getBlock(n, n2, n3)) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)d, (float)d2, (float)d3);
                this.bindEntityTexture(entityFallingBlock);
                GL11.glDisable(2896);
                if (block instanceof BlockAnvil) {
                    this.renderBlocks2.blockAccess = worldObj;
                    Tessellator instance = Tessellator.instance;
                    instance.startDrawingQuads();
                    instance.setTranslation(-n - 0.5F, -n2 - 0.5F, -n3 - 0.5F);
                    this.renderBlocks2.renderBlockAnvilMetadata((BlockAnvil)block, n, n2, n3, entityFallingBlock.metadata);
                    instance.setTranslation(0.0, 0.0, 0.0);
                    instance.draw();
                } else if (block instanceof BlockDragonEgg) {
                    this.renderBlocks2.blockAccess = worldObj;
                    Tessellator instance = Tessellator.instance;
                    instance.startDrawingQuads();
                    instance.setTranslation(-n - 0.5F, -n2 - 0.5F, -n3 - 0.5F);
                    this.renderBlocks2.renderBlockDragonEgg((BlockDragonEgg)block, n, n2, n3);
                    instance.setTranslation(0.0, 0.0, 0.0);
                    instance.draw();
                } else {
                    this.renderBlocks2.setRenderBoundsFromBlock(block);
                    this.renderBlocks2.renderBlockSandFalling(block, worldObj, n, n2, n3, entityFallingBlock.metadata);
                }

                GL11.glEnable(2896);
                GL11.glPopMatrix();
            }
        }
    }

    public ResourceLocation getEntityTexture(EntityFallingBlock entityFallingBlock) {
        return TextureMap.locationBlocksTexture;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityFallingBlock)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityFallingBlock)entity, d, d2, d3, f, f2);
    }
}
