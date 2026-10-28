package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiFurnace extends GuiContainer {
    public static ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
    public TileEntityFurnace tileFurnace;

    public GuiFurnace(InventoryPlayer inventoryPlayer, TileEntityFurnace tileEntityFurnace) {
        super(new ContainerFurnace(inventoryPlayer, tileEntityFurnace));
        this.tileFurnace = tileEntityFurnace;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String str = this.tileFurnace.isCustomInventoryName() ? this.tileFurnace.getInventoryName() : I18n.format(this.tileFurnace.getInventoryName());
        this.fontRendererObj.drawString(str, this.xSize / 2 - this.fontRendererObj.getStringWidth(str) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        drawTexturedRect(n, n2, 0.0F, 0.0F, this.xSize, this.ySize);
        if (this.tileFurnace.isBurning()) {
            int burnTimeRemainingScaled = this.tileFurnace.getBurnTimeRemainingScaled(13);
            drawTexturedRect(n + 56, n2 + 36 + 12 - burnTimeRemainingScaled, 176.0F, 12 - burnTimeRemainingScaled, 14, burnTimeRemainingScaled + 1);
            burnTimeRemainingScaled = this.tileFurnace.getCookProgressScaled(24);
            drawTexturedRect(n + 79, n2 + 34, 176.0F, 14.0F, burnTimeRemainingScaled + 1, 16);
        }
    }
}
