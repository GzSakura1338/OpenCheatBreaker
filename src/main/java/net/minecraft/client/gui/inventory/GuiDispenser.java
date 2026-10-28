package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiDispenser extends GuiContainer {
    public static ResourceLocation dispenserGuiTextures = new ResourceLocation("textures/gui/container/dispenser.png");
    public TileEntityDispenser tileDispenser;

    public GuiDispenser(InventoryPlayer inventoryPlayer, TileEntityDispenser tileEntityDispenser) {
        super(new ContainerDispenser(inventoryPlayer, tileEntityDispenser));
        this.tileDispenser = tileEntityDispenser;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String str = this.tileDispenser.isCustomInventoryName() ? this.tileDispenser.getInventoryName() : I18n.format(this.tileDispenser.getInventoryName());
        this.fontRendererObj.drawString(str, this.xSize / 2 - this.fontRendererObj.getStringWidth(str) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(dispenserGuiTextures);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        drawTexturedRect(n, n2, 0.0F, 0.0F, this.xSize, this.ySize);
    }
}
