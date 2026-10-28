package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityEnderChestRenderer extends TileEntitySpecialRenderer {
    public static ResourceLocation TEXTURE = new ResourceLocation("textures/entity/chest/ender.png");
    public ModelChest model = new ModelChest();

    public void renderTileEntityAt(TileEntityEnderChest tileEntityEnderChest, double d, double d2, double d3, float f) {
        int n = 0;
        if (tileEntityEnderChest.hasWorldObj()) {
            n = tileEntityEnderChest.getBlockMetadata();
        }

        this.bindTexture(TEXTURE);
        GL11.glPushMatrix();
        GL11.glEnable(32826);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)d, (float)d2 + 1.0F, (float)d3 + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        short s = 0;
        if (n == 2) {
            s = 180;
        }

        if (n == 3) {
            s = 0;
        }

        if (n == 4) {
            s = 90;
        }

        if (n == 5) {
            s = -90;
        }

        GL11.glRotatef(s, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        float f2 = tileEntityEnderChest.prevLidAngle + (tileEntityEnderChest.animationProgress - tileEntityEnderChest.prevLidAngle) * f;
        f2 = 1.0F - f2;
        f2 = 1.0F - f2 * f2 * f2;
        this.model.chestLid.rotateAngleX = -(f2 * (float) Math.PI / 2.0F);
        this.model.renderAll();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f) {
        this.renderTileEntityAt((TileEntityEnderChest)tileEntity, d, d2, d3, f);
    }
}
