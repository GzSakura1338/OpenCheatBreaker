package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererPiston extends TileEntitySpecialRenderer {
    public RenderBlocks renderBlocks;

    public void renderTileEntityAt(TileEntityPiston tileEntityPiston, double d, double d2, double d3, float f) {
        Block storedBlockID = tileEntityPiston.getStoredBlockID();
        if (storedBlockID.getMaterial() != Material.air && tileEntityPiston.getAmountExtended(f) < 1.0F) {
            Tessellator instance = Tessellator.instance;
            this.bindTexture(TextureMap.locationBlocksTexture);
            RenderHelper.disableStandardItemLighting();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glDisable(2884);
            if (Minecraft.isAmbientOcclusionEnabled()) {
                GL11.glShadeModel(7425);
            } else {
                GL11.glShadeModel(7424);
            }

            instance.startDrawingQuads();
            instance.setTranslation(
                (float)d - tileEntityPiston.xCoord + tileEntityPiston.computeFloat(f),
                (float)d2 - tileEntityPiston.yCoord + tileEntityPiston.computeFloat2(f),
                (float)d3 - tileEntityPiston.zCoord + tileEntityPiston.computeFloat3(f)
            );
            instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            if (storedBlockID == Blocks.piston_head && tileEntityPiston.getAmountExtended(f) < 0.5F) {
                this.renderBlocks.renderPistonExtensionAllFaces(storedBlockID, tileEntityPiston.xCoord, tileEntityPiston.yCoord, tileEntityPiston.zCoord, false);
            } else if (tileEntityPiston.shouldPistonHeadBeRendered() && !tileEntityPiston.isExtending()) {
                Blocks.piston_head.setIIcon(((BlockPistonBase)storedBlockID).getPistonExtensionTexture());
                this.renderBlocks.renderPistonExtensionAllFaces(Blocks.piston_head, tileEntityPiston.xCoord, tileEntityPiston.yCoord, tileEntityPiston.zCoord, tileEntityPiston.getAmountExtended(f) < 0.5F);
                Blocks.piston_head.run();
                instance.setTranslation((float)d - tileEntityPiston.xCoord, (float)d2 - tileEntityPiston.yCoord, (float)d3 - tileEntityPiston.zCoord);
                this.renderBlocks.renderPistonBaseAllFaces(storedBlockID, tileEntityPiston.xCoord, tileEntityPiston.yCoord, tileEntityPiston.zCoord);
            } else {
                this.renderBlocks.renderBlockAllFaces(storedBlockID, tileEntityPiston.xCoord, tileEntityPiston.yCoord, tileEntityPiston.zCoord);
            }

            instance.setTranslation(0.0, 0.0, 0.0);
            instance.draw();
            RenderHelper.enableStandardItemLighting();
        }
    }

    @Override
    public void onWorldChange(World world) {
        this.renderBlocks = new RenderBlocks(world);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f) {
        this.renderTileEntityAt((TileEntityPiston)tileEntity, d, d2, d3, f);
    }
}
