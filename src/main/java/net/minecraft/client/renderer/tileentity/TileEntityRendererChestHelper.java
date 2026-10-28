package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

public class TileEntityRendererChestHelper {
    public static TileEntityRendererChestHelper instance = new TileEntityRendererChestHelper();
    public TileEntityChest tileEntityChest = new TileEntityChest(0);
    public TileEntityChest tileEntityChest2 = new TileEntityChest(1);
    public TileEntityEnderChest tileEntityEnderChest = new TileEntityEnderChest();

    public void renderChest(Block block, int n, float f) {
        if (block == Blocks.ender_chest) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileEntityEnderChest, 0.0, 0.0, 0.0, 0.0F);
        } else if (block == Blocks.trapped_chest) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileEntityChest2, 0.0, 0.0, 0.0, 0.0F);
        } else {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileEntityChest, 0.0, 0.0, 0.0, 0.0F);
        }
    }
}
