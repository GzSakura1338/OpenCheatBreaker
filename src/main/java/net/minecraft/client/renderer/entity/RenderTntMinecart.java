package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;

public class RenderTntMinecart extends RenderMinecart {
    public void render(EntityMinecartTNT entityMinecartTNT, float f, Block block, int n) {
        int fuseTicks = entityMinecartTNT.getFuseTicks();
        if (fuseTicks > -1 && fuseTicks - f + 1.0F < 10.0F) {
            float f2 = 1.0F - (fuseTicks - f + 1.0F) / 10.0F;
            if (f2 < 0.0F) {
                f2 = 0.0F;
            }

            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            f2 *= f2;
            f2 *= f2;
            float f3 = 1.0F + f2 * 0.3F;
            GL11.glScalef(f3, f3, f3);
        }

        super.render(entityMinecartTNT, f, block, n);
        if (fuseTicks > -1 && fuseTicks / 5 % 2 == 0) {
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 772);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, (1.0F - (fuseTicks - f + 1.0F) / 100.0F) * 0.8F);
            GL11.glPushMatrix();
            this.renderBlocks2.renderBlockAsItem(Blocks.tnt, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
        }
    }

    @Override
    public void render(EntityMinecart entityMinecart, float f, Block block, int n) {
        this.render((EntityMinecartTNT)entityMinecart, f, block, n);
    }
}
