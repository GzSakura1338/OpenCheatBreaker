package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class GuiRepair extends GuiContainer implements ICrafting {
   public static ResourceLocation anvilResource = new ResourceLocation("textures/gui/container/anvil.png");
   public InventoryPlayer playerInventory;
   public GuiTextField nameField;
   public ContainerRepair anvil;

   public GuiRepair(InventoryPlayer inventoryIn, World worldIn) {
      super(new ContainerRepair(inventoryIn, worldIn, Minecraft.getMinecraft().thePlayer));
      this.playerInventory = inventoryIn;
      this.anvil = (ContainerRepair)this.inventorySlots;
   }

   @Override
   public void initGui() {
      super.initGui();
      Keyboard.enableRepeatEvents(true);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.nameField = new GuiTextField(0, this.fontRendererObj, n + 62, n2 + 24, 103, 12);
      this.nameField.setTextColor(-1);
      this.nameField.setDisabledTextColour(-1);
      this.nameField.setEnableBackgroundDrawing(false);
      this.nameField.setMaxStringLength(30);
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
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      this.fontRendererObj.drawString(I18n.format("container.repair"), 60.0F, 6.0F, 4210752);
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

      GlStateManager.enableLighting();
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
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
      this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", new PacketBuffer(Unpooled.buffer()).writeString(text)));
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
      this.nameField.drawTextBox();
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(anvilResource);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(n, n2, 0, 0, this.xSize, this.ySize);
      this.drawTexturedModalRect(n + 59, n2 + 20, 0, this.ySize + (this.anvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);
      if ((this.anvil.getSlot(0).getHasStack() || this.anvil.getSlot(1).getHasStack()) && !this.anvil.getSlot(2).getHasStack()) {
         this.drawTexturedModalRect(n + 99, n2 + 45, this.xSize, 0, 28, 21);
      }
   }

   @Override
   public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList) {
      this.sendSlotContents(containerToSend, 0, containerToSend.getSlot(0).getStack());
   }

   @Override
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      if (slotInd == 0) {
         this.nameField.setText(stack == null ? "" : stack.getDisplayName());
         this.nameField.setEnabled(stack != null);
         if (stack != null) {
            this.renameItem();
         }
      }
   }

   @Override
   public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue) {
   }

   @Override
   public void sendAllWindowProperties(Container container, IInventory inventory) {
   }
}
