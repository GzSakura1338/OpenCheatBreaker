package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderTNTPrimed extends Render {
    public RenderBlocks blockRenderer = new RenderBlocks();

    public RenderTNTPrimed() {
        this.shadowSize = 0.5F;
    }

    public void doRender(EntityTNTPrimed entityTNTPrimed, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d2, (float)d3);
        if (entityTNTPrimed.fuse - f2 + 1.0F < 10.0F) {
            float f3 = 1.0F - (entityTNTPrimed.fuse - f2 + 1.0F) / 10.0F;
            if (f3 < 0.0F) {
                f3 = 0.0F;
            }

            if (f3 > 1.0F) {
                f3 = 1.0F;
            }

            f3 *= f3;
            f3 *= f3;
            float f4 = 1.0F + f3 * 0.3F;
            GL11.glScalef(f4, f4, f4);
        }

        float f3 = (1.0F - (entityTNTPrimed.fuse - f2 + 1.0F) / 100.0F) * 0.8F;
        this.bindEntityTexture(entityTNTPrimed);
        this.blockRenderer.renderBlockAsItem(Blocks.tnt, 0, entityTNTPrimed.getBrightness(f2));
        if (entityTNTPrimed.fuse / 5 % 2 == 0) {
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 772);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, f3);
            this.blockRenderer.renderBlockAsItem(Blocks.tnt, 0, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }

        GL11.glPopMatrix();
    }

    public ResourceLocation getEntityTexture(EntityTNTPrimed entityTNTPrimed) {
        return TextureMap.locationBlocksTexture;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityTNTPrimed)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityTNTPrimed)entity, d, d2, d3, f, f2);
    }
}
