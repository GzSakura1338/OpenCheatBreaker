package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntitySignRenderer extends TileEntitySpecialRenderer {
    public static ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sign.png");
    public ModelSign model = new ModelSign();

    public void renderTileEntityAt(TileEntitySign tileEntitySign, double d, double d2, double d3, float f) {
        Block blockType = tileEntitySign.getBlockType();
        GL11.glPushMatrix();
        float f2 = 0.6666667F;
        if (blockType == Blocks.standing_sign) {
            GL11.glTranslatef((float)d + 0.5F, (float)d2 + 0.75F * f2, (float)d3 + 0.5F);
            float f3 = tileEntitySign.getBlockMetadata() * 360 / 16.0F;
            GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
            this.model.signStick.showModel = true;
        } else {
            int blockMetadata = tileEntitySign.getBlockMetadata();
            float f3 = 0.0F;
            if (blockMetadata == 2) {
                f3 = 180.0F;
            }

            if (blockMetadata == 4) {
                f3 = 90.0F;
            }

            if (blockMetadata == 5) {
                f3 = -90.0F;
            }

            GL11.glTranslatef((float)d + 0.5F, (float)d2 + 0.75F * f2, (float)d3 + 0.5F);
            GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
            this.model.signStick.showModel = false;
        }

        this.bindTexture(TEXTURE);
        GL11.glPushMatrix();
        GL11.glScalef(f2, -f2, -f2);
        this.model.renderSign();
        GL11.glPopMatrix();
        FontRenderer textRenderer = this.getTextRenderer();
        float f3 = 0.016666668F * f2;
        GL11.glTranslatef(0.0F, 0.5F * f2, 0.07F * f2);
        GL11.glScalef(f3, -f3, f3);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
        GL11.glDepthMask(false);
        byte b = 0;

        for (int i = 0; i < tileEntitySign.signText.length; i++) {
            String str = tileEntitySign.signText[i];
            if (i == tileEntitySign.lineBeingEdited) {
                str = "> " + str + " <";
                textRenderer.drawString(str, -textRenderer.getStringWidth(str) / 2, i * 10 - tileEntitySign.signText.length * 5, b);
            } else {
                textRenderer.drawString(str, -textRenderer.getStringWidth(str) / 2, i * 10 - tileEntitySign.signText.length * 5, b);
            }
        }

        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f) {
        this.renderTileEntityAt((TileEntitySign)tileEntity, d, d2, d3, f);
    }
}
