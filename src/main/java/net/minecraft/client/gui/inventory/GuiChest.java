package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiChest extends GuiContainer {
    public static ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    public IInventory upperChestInventory;
    public IInventory lowerChestInventory;
    public int inventoryRows;

    public GuiChest(IInventory inventory, IInventory inventory2) {
        super(new ContainerChest(inventory, inventory2));
        this.upperChestInventory = inventory;
        this.lowerChestInventory = inventory2;
        this.allowUserInput = false;
        short s = 222;
        int n = s - 108;
        this.inventoryRows = inventory2.getSizeInventory() / 9;
        this.ySize = n + this.inventoryRows * 18;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String inventoryName = this.lowerChestInventory.getInventoryName();
        String inventoryName2 = this.upperChestInventory.getInventoryName();
        this.fontRendererObj.drawString(this.lowerChestInventory.isCustomInventoryName() ? inventoryName : I18n.format(inventoryName), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.upperChestInventory.isCustomInventoryName() ? inventoryName2 : I18n.format(inventoryName2), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        drawTexturedRect(n, n2, 0.0F, 0.0F, this.xSize, this.inventoryRows * 18 + 17);
        drawTexturedRect(n, n2 + this.inventoryRows * 18 + 17, 0.0F, 126.0F, this.xSize, 96);
    }
}
