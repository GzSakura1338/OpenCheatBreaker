package net.minecraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiHopper extends GuiContainer {
    public static ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/hopper.png");
    public IInventory playerInventory;
    public IInventory hopperInventory;

    public GuiHopper(InventoryPlayer inventoryPlayer, IInventory inventory) {
        super(new ContainerHopper(inventoryPlayer, inventory));
        this.playerInventory = inventoryPlayer;
        this.hopperInventory = inventory;
        this.allowUserInput = false;
        this.ySize = 133;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj
            .drawString(
                this.hopperInventory.isCustomInventoryName() ? this.hopperInventory.getInventoryName() : I18n.format(this.hopperInventory.getInventoryName()),
                8,
                6,
                4210752
            );
        this.fontRendererObj
            .drawString(
                this.playerInventory.isCustomInventoryName() ? this.playerInventory.getInventoryName() : I18n.format(this.playerInventory.getInventoryName()),
                8,
                this.ySize - 96 + 2,
                4210752
            );
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        drawTexturedRect(n, n2, 0.0F, 0.0F, this.xSize, this.ySize);
    }
}
