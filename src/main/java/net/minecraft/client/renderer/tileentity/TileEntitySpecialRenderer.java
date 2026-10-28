package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class TileEntitySpecialRenderer {
    public TileEntityRendererDispatcher dispatcher;

    public abstract void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f);

    public void bindTexture(ResourceLocation resourceLocation) {
        TextureManager renderEngine = this.dispatcher.renderEngine;
        if (renderEngine != null) {
            renderEngine.bindTexture(resourceLocation);
        }
    }

    public void setDispatcher(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
        this.dispatcher = tileEntityRendererDispatcher;
    }

    public void onWorldChange(World world) {
    }

    public FontRenderer getTextRenderer() {
        return this.dispatcher.getFontRenderer();
    }
}
