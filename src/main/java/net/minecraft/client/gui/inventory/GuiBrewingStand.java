package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiBrewingStand extends GuiContainer {
    public static ResourceLocation brewingStandGuiTextures = new ResourceLocation("textures/gui/container/brewing_stand.png");
    public TileEntityBrewingStand tileBrewingStand;

    public GuiBrewingStand(InventoryPlayer inventoryPlayer, TileEntityBrewingStand tileEntityBrewingStand) {
        super(new ContainerBrewingStand(inventoryPlayer, tileEntityBrewingStand));
        this.tileBrewingStand = tileEntityBrewingStand;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String str = this.tileBrewingStand.isCustomInventoryName()
            ? this.tileBrewingStand.getInventoryName()
            : I18n.format(this.tileBrewingStand.getInventoryName());
        this.fontRendererObj.drawString(str, this.xSize / 2 - this.fontRendererObj.getStringWidth(str) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(brewingStandGuiTextures);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        drawTexturedRect(n, n2, 0.0F, 0.0F, this.xSize, this.ySize);
        int brewTime = this.tileBrewingStand.getBrewTime();
        if (brewTime > 0) {
            int n3 = (int)(28.0F * (1.0F - brewTime / 400.0F));
            if (n3 > 0) {
                drawTexturedRect(n + 97, n2 + 16, 176.0F, 0.0F, 9, n3);
            }

            int n4 = brewTime / 2 % 7;
            switch (n4) {
                case 0:
                    n3 = 29;
                    break;
                case 1:
                    n3 = 24;
                    break;
                case 2:
                    n3 = 20;
                    break;
                case 3:
                    n3 = 16;
                    break;
                case 4:
                    n3 = 11;
                    break;
                case 5:
                    n3 = 6;
                    break;
                case 6:
                    n3 = 0;
            }

            if (n3 > 0) {
                drawTexturedRect(n + 65, n2 + 14 + 29 - n3, 185.0F, 29 - n3, 12, n3);
            }
        }
    }
}
