package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityChestRenderer extends TileEntitySpecialRenderer {
    public ModelChest modelChest = new ModelChest();
    public static ResourceLocation trappedDoublePng = new ResourceLocation("textures/entity/chest/trapped_double.png");
    public static ResourceLocation christmasDoublePng = new ResourceLocation("textures/entity/chest/christmas_double.png");
    public static ResourceLocation normalDoublePng = new ResourceLocation("textures/entity/chest/normal_double.png");
    public ModelChest modelChest2 = new ModelLargeChest();
    public boolean isChristams;
    public static ResourceLocation trappedPng = new ResourceLocation("textures/entity/chest/trapped.png");
    public static ResourceLocation christmasPng = new ResourceLocation("textures/entity/chest/christmas.png");
    public static ResourceLocation normalPng = new ResourceLocation("textures/entity/chest/normal.png");

    public TileEntityChestRenderer() {
        Calendar instance = Calendar.getInstance();
        if (instance.get(2) + 1 == 12 && instance.get(5) >= 24 && instance.get(5) <= 26) {
            this.isChristams = true;
        }
    }

    public void renderTileEntityAt(TileEntityChest tileEntityChest, double d, double d2, double d3, float f) {
        int n;
        if (!tileEntityChest.hasWorldObj()) {
            n = 0;
        } else {
            Block blockType = tileEntityChest.getBlockType();
            n = tileEntityChest.getBlockMetadata();
            if (blockType instanceof BlockChest && n == 0) {
                ((BlockChest)blockType).initMetadata(tileEntityChest.getWorld(), tileEntityChest.xCoord, tileEntityChest.yCoord, tileEntityChest.zCoord);
                n = tileEntityChest.getBlockMetadata();
            }

            tileEntityChest.checkForAdjacentChests();
        }

        if (tileEntityChest.adjacentChestZNeg == null && tileEntityChest.adjacentChestXNeg == null) {
            ModelChest modelChest;
            if (tileEntityChest.adjacentChestXPos == null && tileEntityChest.adjacentChestZPos == null) {
                modelChest = this.modelChest;
                if (tileEntityChest.getChestType() == 1) {
                    this.bindTexture(trappedPng);
                } else if (this.isChristams) {
                    this.bindTexture(christmasPng);
                } else {
                    this.bindTexture(normalPng);
                }
            } else {
                modelChest = this.modelChest2;
                if (tileEntityChest.getChestType() == 1) {
                    this.bindTexture(trappedDoublePng);
                } else if (this.isChristams) {
                    this.bindTexture(christmasDoublePng);
                } else {
                    this.bindTexture(normalDoublePng);
                }
            }

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

            if (n == 2 && tileEntityChest.adjacentChestXPos != null) {
                GL11.glTranslatef(1.0F, 0.0F, 0.0F);
            }

            if (n == 5 && tileEntityChest.adjacentChestZPos != null) {
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);
            }

            GL11.glRotatef(s, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            float f2 = tileEntityChest.prevLidAngle + (tileEntityChest.lidAngle - tileEntityChest.prevLidAngle) * f;
            if (tileEntityChest.adjacentChestZNeg != null) {
                float f3 = tileEntityChest.adjacentChestZNeg.prevLidAngle + (tileEntityChest.adjacentChestZNeg.lidAngle - tileEntityChest.adjacentChestZNeg.prevLidAngle) * f;
                if (f3 > f2) {
                    f2 = f3;
                }
            }

            if (tileEntityChest.adjacentChestXNeg != null) {
                float f3 = tileEntityChest.adjacentChestXNeg.prevLidAngle + (tileEntityChest.adjacentChestXNeg.lidAngle - tileEntityChest.adjacentChestXNeg.prevLidAngle) * f;
                if (f3 > f2) {
                    f2 = f3;
                }
            }

            f2 = 1.0F - f2;
            f2 = 1.0F - f2 * f2 * f2;
            modelChest.chestLid.rotateAngleX = -(f2 * (float) Math.PI / 2.0F);
            modelChest.renderAll();
            GL11.glDisable(32826);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f) {
        this.renderTileEntityAt((TileEntityChest)tileEntity, d, d2, d3, f);
    }
}
