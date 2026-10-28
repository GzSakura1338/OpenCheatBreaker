package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiScreenHorseInventory extends GuiContainer {
    public static ResourceLocation horseGuiTextures = new ResourceLocation("textures/gui/container/horse.png");
    public IInventory armorInventory;
    public IInventory chestInventory;
    public EntityHorse entity;
    public float mouseX;
    public float mouseY;

    public GuiScreenHorseInventory(IInventory inventory, IInventory inventory2, EntityHorse entityHorse) {
        super(new ContainerHorseInventory(inventory, inventory2, entityHorse));
        this.armorInventory = inventory;
        this.chestInventory = inventory2;
        this.entity = entityHorse;
        this.allowUserInput = false;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj
            .drawString(
                this.chestInventory.isCustomInventoryName() ? this.chestInventory.getInventoryName() : I18n.format(this.chestInventory.getInventoryName()),
                8,
                6,
                4210752
            );
        this.fontRendererObj
            .drawString(
                this.armorInventory.isCustomInventoryName() ? this.armorInventory.getInventoryName() : I18n.format(this.armorInventory.getInventoryName()),
                8,
                this.ySize - 96 + 2,
                4210752
            );
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(horseGuiTextures);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        drawTexturedRect(n, n2, 0.0F, 0.0F, this.xSize, this.ySize);
        if (this.entity.isChested()) {
            drawTexturedRect(n + 79, n2 + 17, 0.0F, this.ySize, 90, 54);
        }

        if (this.entity.canWearArmor()) {
            drawTexturedRect(n + 7, n2 + 35, 0.0F, this.ySize + 54, 18, 18);
        }

        GuiInventory.drawEntityOnScreen(n + 51, n2 + 60, 17, n + 51 - this.mouseX, n2 + 75 - 50 - this.mouseY, this.entity);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
