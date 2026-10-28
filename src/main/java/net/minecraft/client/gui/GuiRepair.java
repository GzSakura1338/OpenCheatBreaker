package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.io.Charsets;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiRepair extends GuiContainer implements ICrafting {
    public static ResourceLocation anvilResource = new ResourceLocation("textures/gui/container/anvil.png");
    public InventoryPlayer playerInventory;
    public GuiTextField nameField;
    public ContainerRepair anvil;

    public GuiRepair(InventoryPlayer inventoryPlayer, World world, int n, int n2, int n3) {
        super(new ContainerRepair(inventoryPlayer, world, n, n2, n3, Minecraft.getMinecraft().thePlayer));
        this.playerInventory = inventoryPlayer;
        this.anvil = (ContainerRepair)this.inventorySlots;
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        this.nameField = new GuiTextField(this.fontRendererObj, n + 62, n2 + 24, 103, 12);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(40);
        this.inventorySlots.removeCraftingFromCrafters(this);
        this.inventorySlots.onCraftGuiOpened(this);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        this.inventorySlots.removeCraftingFromCrafters(this);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GL11.glDisable(2896);
        GL11.glDisable(3042);
        this.fontRendererObj.drawString(I18n.format("container.repair"), 60, 6, 4210752);
        if (this.anvil.maximumCost > 0) {
            int n = 8453920;
            boolean flag = true;
            String format = I18n.format("container.repair.cost", this.anvil.maximumCost);
            if (this.anvil.maximumCost >= 40 && !this.mc.thePlayer.capabilities.isCreativeMode) {
                format = I18n.format("container.repair.expensive");
                n = 16736352;
            } else if (!this.anvil.getSlot(2).getHasStack()) {
                flag = false;
            } else if (!this.anvil.getSlot(2).canTakeStack(this.playerInventory.player)) {
                n = 16736352;
            }

            if (flag) {
                int n2 = 0xFF000000 | (n & 16579836) >> 2 | n & 0xFF000000;
                int n3 = this.xSize - 8 - this.fontRendererObj.getStringWidth(format);
                byte b = 67;
                if (this.fontRendererObj.getUnicodeFlag()) {
                    drawRect(n3 - 3, b - 2, this.xSize - 7, b + 10, -16777216);
                    drawRect(n3 - 2, b - 1, this.xSize - 8, b + 9, -12895429);
                } else {
                    this.fontRendererObj.drawString(format, n3, b + 1, n2);
                    this.fontRendererObj.drawString(format, n3 + 1, b, n2);
                    this.fontRendererObj.drawString(format, n3 + 1, b + 1, n2);
                }

                this.fontRendererObj.drawString(format, n3, b, n);
            }
        }

        GL11.glEnable(2896);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        if (this.nameField.textboxKeyTyped(typedChar, keyCode)) {
            this.renameItem();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    public void renameItem() {
        String text = this.nameField.getText();
        Slot slot = this.anvil.getSlot(0);
        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && text.equals(slot.getStack().getDisplayName())) {
            text = "";
        }

        this.anvil.updateItemName(text);
        this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", text.getBytes(Charsets.UTF_8)));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(2896);
        GL11.glDisable(3042);
        this.nameField.drawTextBox();
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(anvilResource);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        drawTexturedRect(n, n2, 0.0F, 0.0F, this.xSize, this.ySize);
        drawTexturedRect(n + 59, n2 + 20, 0.0F, this.ySize + (this.anvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);
        if ((this.anvil.getSlot(0).getHasStack() || this.anvil.getSlot(1).getHasStack()) && !this.anvil.getSlot(2).getHasStack()) {
            drawTexturedRect(n + 99, n2 + 45, this.xSize, 0.0F, 28, 21);
        }
    }

    @Override
    public void updateCraftingInventory(Container container, List list) {
        this.sendSlotContents(container, 0, container.getSlot(0).getStack());
    }

    @Override
    public void sendSlotContents(Container container, int n, ItemStack itemStack) {
        if (n == 0) {
            this.nameField.setText(itemStack == null ? "" : itemStack.getDisplayName());
            this.nameField.setEnabled(itemStack != null);
            if (itemStack != null) {
                this.renameItem();
            }
        }
    }

    @Override
    public void sendProgressBarUpdate(Container container, int n, int n2) {
    }
}
