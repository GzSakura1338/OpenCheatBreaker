package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiChest extends GuiContainer {
   public static ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
   public IInventory upperChestInventory;
   public IInventory lowerChestInventory;
   public int inventoryRows;

   public GuiChest(IInventory upperInv, IInventory lowerInv) {
      super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().thePlayer));
      this.upperChestInventory = upperInv;
      this.lowerChestInventory = lowerInv;
      this.allowUserInput = false;
      short s = 222;
      int n = s - 108;
      this.inventoryRows = lowerInv.getSizeInventory() / 9;
      this.ySize = n + this.inventoryRows * 18;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String unformattedText = this.lowerChestInventory.getDisplayName().getUnformattedText();
      String unformattedText2 = this.upperChestInventory.getDisplayName().getUnformattedText();
      this.fontRendererObj.drawString(unformattedText, 8.0F, 6.0F, 4210752);
      this.fontRendererObj.drawString(unformattedText2, 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(n, n2, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
      this.drawTexturedModalRect(n, n2 + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
   }
}
